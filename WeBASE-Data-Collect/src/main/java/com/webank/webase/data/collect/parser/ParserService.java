/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.data.collect.parser;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.enums.ParserUserType;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.enums.TransType;
import com.webank.webase.data.collect.base.enums.TransUnusualType;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.base.tools.TransactionDecoder;
import com.webank.webase.data.collect.block.taskpool.BlockTaskPoolService;
import com.webank.webase.data.collect.chain.ChainService;
import com.webank.webase.data.collect.chain.entity.TbChain;
import com.webank.webase.data.collect.contract.ContractService;
import com.webank.webase.data.collect.contract.MethodService;
import com.webank.webase.data.collect.contract.entity.ContractParam;
import com.webank.webase.data.collect.contract.entity.MethodInfo;
import com.webank.webase.data.collect.contract.entity.TbContract;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.parser.entity.ContractParserResult;
import com.webank.webase.data.collect.parser.entity.PageTransInfo;
import com.webank.webase.data.collect.parser.entity.ResetInfo;
import com.webank.webase.data.collect.parser.entity.TbParser;
import com.webank.webase.data.collect.parser.entity.TransParser;
import com.webank.webase.data.collect.parser.entity.UnusualContractInfo;
import com.webank.webase.data.collect.parser.entity.UnusualUserInfo;
import com.webank.webase.data.collect.parser.entity.UserParserResult;
import com.webank.webase.data.collect.receipt.ReceiptService;
import com.webank.webase.data.collect.receipt.entity.TbReceipt;
import com.webank.webase.data.collect.transaction.TransactionService;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import com.webank.webase.data.collect.user.UserService;
import com.webank.webase.data.collect.user.entity.TbUser;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * ParserService.
 */
@Log4j2
@Service
public class ParserService {

    @Autowired
    private ChainService chainService;
    @Autowired
    private ParserMapper parserMapper;
    @Lazy
    @Autowired
    private ContractService contractService;
    @Lazy
    @Autowired
    private UserService userService;
    @Autowired
    private FrontInterfaceService frontInterfacee;
    @Autowired
    private MethodService methodService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private BlockTaskPoolService blockTaskPoolService;

    public void reset(ResetInfo resetInfo) {
        blockTaskPoolService.resetDataByBlockNumber(resetInfo.getChainId(), resetInfo.getGroupId(),
                resetInfo.getBlockNumber().longValue());
    }

    public void resetBetween(int chainId, int groupId, long startNumber, long endNumber) {
        for (long i = startNumber; i <= endNumber; i++) {
            blockTaskPoolService.resetDataByBlockNumber(chainId, groupId, i);
        }
    }

    public void updateUnusualUser(int chainId, int groupId, String userName, String address) {
        log.info("start updateUnusualUser address:{}", address);
        parserMapper.updateUnusualUser(TableName.PARSER.getTableName(chainId, groupId), userName,
                address);
    }

    /**
     * parser unusual contract.
     */
    public void parserUnusualContract(int chainId, int groupId, String contractBin) {
        log.debug("start parserUnusualContract chainId:{} groupId:{} contractBin:{}", chainId,
                groupId, contractBin);
        String tableName = TableName.PARSER.getTableName(chainId, groupId);
        contractBin = removeBinFirstAndLast(contractBin);
        String subContractBin = subContractBinForName(contractBin);
        List<String> txHashList = parserMapper.queryUnusualTxHashByBin(tableName, subContractBin);
        if (CollectionUtils.isEmpty(txHashList)) {
            return;
        }
        for (String transHash : txHashList) {
            updateUnusualContract(chainId, groupId, transHash);
        }
    }

    /**
     * parser unusual methodId.
     */
    public void parserUnusualMethodId(int chainId, int groupId, String methodId) {
        log.debug("start updateUnusualMethodId chainId:{} groupId:{} methodId:{}", chainId, groupId,
                methodId);
        String tableName = TableName.PARSER.getTableName(chainId, groupId);
        List<String> txHashList = parserMapper.queryUnusualTxHashMethodId(tableName, methodId);
        if (CollectionUtils.isEmpty(txHashList)) {
            return;
        }
        for (String transHash : txHashList) {
            updateUnusualContract(chainId, groupId, transHash);
        }
    }

    /**
     * parser unusual contract.
     */
    public void updateUnusualContract(int chainId, int groupId, String transHash) {
        try {
            TbTransaction tbTransaction =
                    transactionService.getTbTransByHash(chainId, groupId, transHash);
            TbReceipt tbReceipt = receiptService.getTbReceiptByHash(chainId, groupId, transHash);
            if (ObjectUtils.isEmpty(tbTransaction) || ObjectUtils.isEmpty(tbReceipt)) {
                return;
            }
            ContractParserResult contractResult =
                    parserContract(chainId, groupId, tbTransaction, tbReceipt);
            // update parser info
            parserMapper.updateUnusualContract(TableName.PARSER.getTableName(chainId, groupId),
                    contractResult);
        } catch (Exception ex) {
            log.error("fail updateUnusualContract. transHash:{}", transHash, ex);
        }
    }

    /**
     * query parser user list.
     */
    public List<TbParser> qureyParserUserList(int chainId, int groupId) throws BaseException {
        List<TbParser> parserUserList =
                parserMapper.parserUserList(TableName.PARSER.getTableName(chainId, groupId));
        return parserUserList;
    }

    /**
     * query parser interface list.
     */
    public List<TbParser> qureyParserInterfaceList(int chainId, int groupId, String userName)
            throws BaseException {

        List<TbParser> parserInterfaceList = parserMapper
                .parserInterfaceList(TableName.PARSER.getTableName(chainId, groupId), userName);
        return parserInterfaceList;
    }

    /**
     * query parser trans list.
     */
    public BaseResponse qureyParserTransList(int chainId, int groupId, String userName,
            String startDate, String endDate, String interfaceName) throws BaseException {
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);

        // param
        String tableName = TableName.PARSER.getTableName(chainId, groupId);
        List<String> nameList = Arrays.asList("tableName", "groupId", "userName", "startDate",
                "endDate", "interfaceName");
        List<Object> valueList =
                Arrays.asList(tableName, groupId, userName, startDate, endDate, interfaceName);
        Map<String, Object> param = CommonTools.buidMap(nameList, valueList);

        Integer count = parserMapper.countOfParserTrans(param);
        List<PageTransInfo> transInfoList = parserMapper.qureyTransCountList(param);

        TransParser transParser =
                new TransParser(chainId, groupId, userName, interfaceName, count, transInfoList);
        response.setData(transParser);
        return response;
    }

    /**
     * query count of unusual user.
     */
    public Integer countOfUnusualUser(int chainId, int groupId, String userName) {
        return parserMapper.countOfUnusualUser(TableName.PARSER.getTableName(chainId, groupId),
                userName);
    }

    /**
     * query unusual user list.
     */
    public List<UnusualUserInfo> qureyUnusualUserList(int chainId, int groupId, String userName,
            Integer pageNumber, Integer pageSize) throws BaseException {
        log.debug("start qureyUnusualUserList groupId:{} userName:{} pageNumber:{} pageSize:{}",
                groupId, userName, pageNumber, pageSize);

        Integer start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
        String tableName = TableName.PARSER.getTableName(chainId, groupId);
        List<String> nameList =
                Arrays.asList("tableName", "groupId", "userName", "start", "pageSize");
        List<Object> valueList = Arrays.asList(tableName, groupId, userName, start, pageSize);
        Map<String, Object> param = CommonTools.buidMap(nameList, valueList);

        List<UnusualUserInfo> listOfUnusualUser = parserMapper.listOfUnusualUser(param);
        return listOfUnusualUser;
    }

    /**
     * query count of unusual contract.
     */
    public Integer countOfUnusualContract(int chainId, int groupId, String contractAddress) {
        return parserMapper.countOfUnusualContract(TableName.PARSER.getTableName(chainId, groupId),
                contractAddress);
    }

    /**
     * query unusual contract list.
     */
    public List<UnusualContractInfo> qureyUnusualContractList(int chainId, int groupId,
            String contractAddress, Integer pageNumber, Integer pageSize) throws BaseException {
        log.debug("start qureyUnusualContractList groupId:{} userName:{} pageNumber:{} pageSize:{}",
                groupId, contractAddress, pageNumber, pageSize);

        String tableName = TableName.PARSER.getTableName(chainId, groupId);
        Integer start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);

        List<String> nameList =
                Arrays.asList("tableName", "groupId", "contractAddress", "start", "pageSize");
        List<Object> valueList =
                Arrays.asList(tableName, groupId, contractAddress, start, pageSize);
        Map<String, Object> param = CommonTools.buidMap(nameList, valueList);

        List<UnusualContractInfo> listOfUnusualContract = parserMapper.listOfUnusualContract(param);
        return listOfUnusualContract;
    }

    /**
     * parserTransaction.
     */
    public void parserTransaction(int chainId, int groupId, TbTransaction tbTransaction,
            TbReceipt tbReceipt) {
        // parser user
        UserParserResult userResult = parserUser(chainId, groupId, tbTransaction.getTransFrom());
        // parser contract
        ContractParserResult contractResult =
                parserContract(chainId, groupId, tbTransaction, tbReceipt);

        TbParser tbParser = new TbParser();
        BeanUtils.copyProperties(userResult, tbParser);
        BeanUtils.copyProperties(contractResult, tbParser);
        tbParser.setBlockNumber(tbTransaction.getBlockNumber());
        tbParser.setBlockTimestamp(tbTransaction.getBlockTimestamp());
        dataAddAndUpdate(chainId, groupId, tbParser);
    }

    @Transactional
    public void dataAddAndUpdate(int chainId, int groupId, TbParser tbParser) {
        parserMapper.add(TableName.PARSER.getTableName(chainId, groupId), tbParser);
        transactionService.updateTransStatFlag(chainId, groupId, tbParser.getTransHash());
    }

    /**
     * parser contract.
     */
    private ContractParserResult parserContract(int chainId, int groupId,
            TbTransaction tbTransaction, TbReceipt tbReceipt) {
        ContractParserResult contractResult = new ContractParserResult();
        contractResult.setTransHash(tbTransaction.getTransHash());
        contractResult.setInput(tbReceipt.getInput());
        contractResult.setOutput(tbReceipt.getOutput());
        contractResult.setLogs(tbReceipt.getLogs());

        String transInput = tbReceipt.getInput();
        String contractAddress, contractName, interfaceName = "", contractBin;
        int transType = TransType.DEPLOY.getValue();
        int transUnusualType = TransUnusualType.NORMAL.getValue();

        if (isDeploy(tbTransaction.getTransTo())) {
            contractAddress = tbReceipt.getContractAddress();
            if (ConstantProperties.ADDRESS_DEPLOY.equals(contractAddress)) {
                contractBin = StringUtils.removeStart(transInput, "0x");
                ContractParam param = new ContractParam();
                param.setChainId(chainId);
                param.setGroupId(groupId);
                param.setPartOfBytecodeBin(contractBin);
                TbContract tbContract = contractService.queryContract(param);
                if (Objects.nonNull(tbContract)) {
                    contractName = tbContract.getContractName();
                    parserInputOutputLogs(chainId, contractResult, tbContract.getContractAbi());
                } else {
                    contractName = getNameFromContractBin(chainId, groupId, contractBin);
                    transUnusualType = TransUnusualType.CONTRACT.getValue();
                }
            } else {
                contractBin = frontInterfacee.getCodeFromFront(chainId, groupId, contractAddress,
                        tbTransaction.getBlockNumber());
                contractBin = removeBinFirstAndLast(contractBin);
                TbContract tbContract =
                        contractService.queryContractByBin(chainId, groupId, contractBin);
                if (Objects.nonNull(tbContract)) {
                    contractName = tbContract.getContractName();
                    parserInputOutputLogs(chainId, contractResult, tbContract.getContractAbi());
                } else {
                    contractName = subContractBinForName(contractBin);
                    transUnusualType = TransUnusualType.CONTRACT.getValue();
                }
            }
            interfaceName = contractName;
        } else { // function call
            transType = TransType.CALL.getValue();
            String methodId = transInput.substring(0, 10);
            contractAddress = tbTransaction.getTransTo();

            MethodInfo methodInfo = methodService.getByMethodId(methodId, chainId, groupId);
            if (Objects.nonNull(methodInfo)) {
                contractName = methodInfo.getContractName();
                interfaceName = methodInfo.getMethodName();
                parserInputOutputLogs(chainId, contractResult, methodInfo.getContractAbi());
            } else {
                interfaceName = methodId;
                contractBin = frontInterfacee.getCodeFromFront(chainId, groupId, contractAddress,
                        tbTransaction.getBlockNumber());
                contractBin = removeBinFirstAndLast(contractBin);
                contractName = subContractBinForName(contractBin);
                transUnusualType = TransUnusualType.CONTRACT.getValue();
                if (StringUtils.isNoneBlank(contractBin)) {
                    TbContract contractRow =
                            contractService.queryContractByBin(chainId, groupId, contractBin);
                    if (Objects.nonNull(contractRow)) {
                        contractName = contractRow.getContractName();
                        transUnusualType = TransUnusualType.FUNCTION.getValue();
                    }
                }
            }
        }
        // set result
        contractResult.setContractName(contractName);
        contractResult.setContractAddress(contractAddress);
        contractResult.setInterfaceName(interfaceName);
        contractResult.setTransType(transType);
        contractResult.setTransUnusualType(transUnusualType);
        return contractResult;
    }

    /**
     * parser user.
     */
    private UserParserResult parserUser(int chainId, int groupId, String userAddress) {
        String userName = userAddress;
        int userType = ParserUserType.ABNORMAL.getValue();
        TbUser tbUser = userService.queryByAddress(chainId, groupId, userAddress);
        if (Objects.nonNull(tbUser)) {
            userName = tbUser.getUserName();
            userType = ParserUserType.NORMAL.getValue();
        }
        UserParserResult parserResult = new UserParserResult();
        parserResult.setUserName(userName);
        parserResult.setUserAddress(userAddress);
        parserResult.setUserType(userType);
        return parserResult;
    }

    private void parserInputOutputLogs(int chainId, ContractParserResult contractResult,
            String abi) {
        TbChain tbChain = chainService.getChainById(chainId);
        if (ObjectUtils.isEmpty(tbChain)) {
            return;
        }
        TransactionDecoder transactionDecoder = new TransactionDecoder(abi, tbChain.getChainType());
        String input = contractResult.getInput();
        String output = contractResult.getOutput();
        String logs = contractResult.getLogs();
        try {
            input = transactionDecoder.decodeInputReturnJson(input);
            output = transactionDecoder.decodeOutputReturnJson(input, output);
            logs = transactionDecoder.decodeEventReturnJson(logs);
        } catch (Exception e) {
            log.error("parserInputOutputLogs error:", e);
        }
        contractResult.setInput(input);
        contractResult.setOutput(output);
        contractResult.setLogs(logs);
    }

    /**
     * remove "0x" and last 68 character.
     */
    private String removeBinFirstAndLast(String contractBin) {
        if (StringUtils.isBlank(contractBin)) {
            return null;
        }
        if (contractBin.startsWith("0x")) {
            contractBin = StringUtils.removeStart(contractBin, "0x");
        }
        if (contractBin.length() > 68) {
            contractBin = contractBin.substring(0, contractBin.length() - 68);
        }
        return contractBin;
    }

    /**
     * check the address is deploy.
     */
    private boolean isDeploy(String address) {
        if (StringUtils.isBlank(address)) {
            return true;
        }
        return ConstantProperties.ADDRESS_DEPLOY.equals(address);
    }

    /**
     * get contractName from contractBin.
     */
    private String getNameFromContractBin(int chainId, int groupId, String contractBin) {
        TbContract tbContract = contractService.queryContractByBin(chainId, groupId, contractBin);
        if (Objects.nonNull(tbContract)) {
            return tbContract.getContractName();
        }
        return subContractBinForName(contractBin);
    }

    /**
     * substring contractBin for contractName.
     */
    private String subContractBinForName(String contractBin) {
        String contractName = ConstantProperties.CONTRACT_NAME_ZERO;
        if (StringUtils.isNotBlank(contractBin) && contractBin.length() > 10) {
            contractName = contractBin.substring(contractBin.length() - 10);
        }
        return contractName;
    }
}
