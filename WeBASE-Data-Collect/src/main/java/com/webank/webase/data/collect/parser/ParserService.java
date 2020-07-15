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

import com.webank.webase.data.collect.base.enums.ParserUserType;
import com.webank.webase.data.collect.base.enums.PrecompiledAddress;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.enums.TransParserType;
import com.webank.webase.data.collect.base.enums.TransType;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
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
import com.webank.webase.data.collect.parser.entity.EsParser;
import com.webank.webase.data.collect.parser.entity.ResetInfo;
import com.webank.webase.data.collect.parser.entity.TbParser;
import com.webank.webase.data.collect.parser.entity.UnusualContractInfo;
import com.webank.webase.data.collect.parser.entity.UnusualUserInfo;
import com.webank.webase.data.collect.parser.entity.UserParserResult;
import com.webank.webase.data.collect.receipt.ReceiptService;
import com.webank.webase.data.collect.receipt.entity.TbReceipt;
import com.webank.webase.data.collect.table.TableService;
import com.webank.webase.data.collect.transaction.TransactionService;
import com.webank.webase.data.collect.user.UserService;
import com.webank.webase.data.collect.user.entity.TbUser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
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
    @Autowired
    private TableService tableService;
    @Autowired
    private EsCurdService esCurdService;

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
    @Async("asyncExecutor")
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
    @Async("asyncExecutor")
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
            TbReceipt tbReceipt = receiptService.getTbReceiptByHash(chainId, groupId, transHash);
            if (ObjectUtils.isEmpty(tbReceipt)) {
                return;
            }
            TransactionReceipt receipt = JacksonUtils.stringToObj(tbReceipt.getReceiptDetail(),
                    TransactionReceipt.class);
            ContractParserResult contractResult = parserContract(chainId, groupId, receipt);
            // update parser info
            String tableName = TableName.PARSER.getTableName(chainId, groupId);
            parserMapper.updateUnusualContract(tableName, contractResult);
            // update es
            TbParser tbParser = parserMapper.queryByTxHash(tableName, transHash);
            EsParser esParser = new EsParser();
            BeanUtils.copyProperties(tbParser, esParser);
            esParser.setChainId(chainId);
            esParser.setGroupId(groupId);
            esCurdService.update(tableService.getDbName(), transHash, esParser);
        } catch (Exception ex) {
            log.error("fail updateUnusualContract. transHash:{}", transHash, ex);
        }
    }

    /**
     * query parser user list.
     */
    public List<String> queryParserUserList(int chainId, int groupId) throws BaseException {
        List<String> parserUserList =
                parserMapper.parserUserList(TableName.PARSER.getTableName(chainId, groupId));
        return parserUserList;
    }

    /**
     * query parser interface list.
     */
    public List<String> queryParserInterfaceList(int chainId, int groupId, String userName)
            throws BaseException {

        List<String> parserInterfaceList = parserMapper
                .parserInterfaceList(TableName.PARSER.getTableName(chainId, groupId), userName);
        return parserInterfaceList;
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
    public List<UnusualUserInfo> queryUnusualUserList(int chainId, int groupId, String userName,
            Integer pageNumber, Integer pageSize) throws BaseException {
        log.debug("start queryUnusualUserList groupId:{} userName:{} pageNumber:{} pageSize:{}",
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
    public List<UnusualContractInfo> queryUnusualContractList(int chainId, int groupId,
            String contractAddress, Integer pageNumber, Integer pageSize) throws BaseException {
        log.debug("start queryUnusualContractList groupId:{} userName:{} pageNumber:{} pageSize:{}",
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
    public void parserTransaction(int chainId, int groupId, TbReceipt tbReceipt)
            throws IOException {
        TransactionReceipt receipt =
                JacksonUtils.stringToObj(tbReceipt.getReceiptDetail(), TransactionReceipt.class);
        // parser user
        UserParserResult userResult = parserUser(chainId, groupId, receipt.getFrom());
        // parser contract
        ContractParserResult contractResult = parserContract(chainId, groupId, receipt);

        TbParser tbParser = new TbParser();
        BeanUtils.copyProperties(userResult, tbParser);
        BeanUtils.copyProperties(contractResult, tbParser);
        tbParser.setBlockNumber(tbReceipt.getBlockNumber());
        tbParser.setBlockTimestamp(tbReceipt.getBlockTimestamp());
        dataAddAndUpdate(chainId, groupId, tbParser);
    }

    @Transactional
    public void dataAddAndUpdate(int chainId, int groupId, TbParser tbParser) throws IOException {
        String tableName = TableName.PARSER.getTableName(chainId, groupId);
        parserMapper.add(tableName, tbParser);
        transactionService.updateTransStatFlag(chainId, groupId, tbParser.getTransHash());
        tbParser = parserMapper.queryByTxHash(tableName, tbParser.getTransHash());
        EsParser esParser = new EsParser();
        BeanUtils.copyProperties(tbParser, esParser);
        esParser.setChainId(chainId);
        esParser.setGroupId(groupId);
        esCurdService.insert(tableService.getDbName(), String.valueOf(tbParser.getTransHash()),
                esParser);
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

    /**
     * parser contract.
     */
    private ContractParserResult parserContract(int chainId, int groupId,
            TransactionReceipt receipt) {
        ContractParserResult contractResult = new ContractParserResult();
        contractResult.setTransHash(receipt.getTransactionHash());
        contractResult.setTransType(TransType.DEPLOY.getValue());
        contractResult.setTransParserType(TransParserType.NORMAL.getValue());
        // deploy
        if (isDeploy(receipt.getTo())) {
            parserDeploy(chainId, groupId, contractResult, receipt, receipt.getContractAddress());
        } else { // function call
            parserFunction(chainId, groupId, contractResult, receipt, receipt.getTo());
        }
        return contractResult;
    }

    /**
     * parserDeploy.
     * 
     */
    private void parserDeploy(int chainId, int groupId, ContractParserResult contractResult,
            TransactionReceipt receipt, String contractAddress) {
        String contractBin, contractName;
        if (ConstantProperties.ADDRESS_DEPLOY.equals(contractAddress)) {
            contractBin = StringUtils.removeStart(receipt.getInput(), "0x");
            ContractParam param = new ContractParam();
            param.setChainId(chainId);
            param.setGroupId(groupId);
            param.setPartOfBytecodeBin(contractBin);
            TbContract tbContract = contractService.queryContract(param);
            if (Objects.nonNull(tbContract)) {
                contractName = tbContract.getContractName();
                parserInputOutputLogs(chainId, contractResult, receipt,
                        tbContract.getContractAbi());
            } else {
                contractName = getNameFromContractBin(chainId, groupId, contractBin);
                contractResult.setTransParserType(TransParserType.CONTRACT.getValue());
            }
        } else {
            contractBin = frontInterfacee.getCodeFromFront(chainId, groupId, contractAddress,
                    receipt.getBlockNumber());
            contractBin = removeBinFirstAndLast(contractBin);
            List<TbContract> tbContract =
                    contractService.queryContractByBin(chainId, groupId, contractBin);
            if (!CollectionUtils.isEmpty(tbContract)) {
                contractName = tbContract.get(0).getContractName();
                parserInputOutputLogs(chainId, contractResult, receipt,
                        tbContract.get(0).getContractAbi());
            } else {
                contractName = subContractBinForName(contractBin);
                contractResult.setTransParserType(TransParserType.CONTRACT.getValue());
            }
        }
        contractResult.setContractName(contractName);
        contractResult.setContractAddress(contractAddress);
        contractResult.setInterfaceName(contractName);
    }

    /**
     * parserFunction.
     * 
     */
    private void parserFunction(int chainId, int groupId, ContractParserResult contractResult,
            TransactionReceipt receipt, String contractAddress) {
        String contractBin, contractName, interfaceName;
        String methodId = receipt.getInput().substring(0, 10);
        // Precompiled contract
        if (PrecompiledAddress.isInclude(contractAddress)) {
            List<MethodInfo> methodInfoList =
                    methodService.getByMethodInfo(0, 0, methodId, contractAddress, null);
            if (!CollectionUtils.isEmpty(methodInfoList)) {
                contractName = methodInfoList.get(0).getContractName();
                parserInputOutputLogs(chainId, contractResult, receipt,
                        methodInfoList.get(0).getContractAbi());
                interfaceName = contractResult.getInterfaceName();
            } else {
                interfaceName = methodId;
                contractName = contractAddress;
            }
        } else { // normal contract
            contractBin = frontInterfacee.getCodeFromFront(chainId, groupId, contractAddress,
                    receipt.getBlockNumber());
            contractBin = removeBinFirstAndLast(contractBin);
            List<MethodInfo> methodInfoList =
                    methodService.getByMethodInfo(chainId, groupId, methodId, null, contractBin);
            if (!CollectionUtils.isEmpty(methodInfoList)) {
                contractName = methodInfoList.get(0).getContractName();
                parserInputOutputLogs(chainId, contractResult, receipt,
                        methodInfoList.get(0).getContractAbi());
                interfaceName = contractResult.getInterfaceName();
            } else {
                interfaceName = methodId;
                contractName = subContractBinForName(contractBin);
                contractResult.setTransParserType(TransParserType.CONTRACT.getValue());
                if (StringUtils.isNoneBlank(contractBin)) {
                    List<TbContract> contractRow =
                            contractService.queryContractByBin(chainId, groupId, contractBin);
                    if (!CollectionUtils.isEmpty(contractRow)) {
                        contractName = contractRow.get(0).getContractName();
                        contractResult.setTransParserType(TransParserType.FUNCTION.getValue());
                    }
                }
            }
        }
        contractResult.setContractName(contractName);
        contractResult.setContractAddress(contractAddress);
        contractResult.setInterfaceName(interfaceName);
        contractResult.setTransType(TransType.CALL.getValue());
    }

    /**
     * parserInputOutputLogs.
     * 
     */
    private void parserInputOutputLogs(int chainId, ContractParserResult contractResult,
            TransactionReceipt receipt, String abi) {
        TbChain tbChain = chainService.getChainById(chainId);
        if (ObjectUtils.isEmpty(tbChain)) {
            return;
        }
        TransactionDecoder transactionDecoder = new TransactionDecoder(abi, tbChain.getChainType());
        InputAndOutputResult inputResult = null;
        InputAndOutputResult outputResult = null;
        String logs = null;
        try {
            inputResult = transactionDecoder.decodeInputReturnObject(receipt.getInput());
            outputResult = transactionDecoder.decodeOutputReturnObject(receipt.getInput(),
                    receipt.getOutput());
            logs = transactionDecoder.decodeEventReturnJson(receipt.getLogs());
        } catch (Exception e) {
            log.error("parserInputOutputLogs error:", e);
        }
        if (inputResult != null) {
            contractResult.setInterfaceName(inputResult.getFunction());
            contractResult.setInput(JacksonUtils.objToString(inputResult.getResult()));
        }
        if (outputResult != null && !CollectionUtils.isEmpty(outputResult.getResult())) {
            contractResult.setOutput(JacksonUtils.objToString(outputResult.getResult()));
        }
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
        List<TbContract> tbContract =
                contractService.queryContractByBin(chainId, groupId, contractBin);
        if (!CollectionUtils.isEmpty(tbContract)) {
            return tbContract.get(0).getContractName();
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
