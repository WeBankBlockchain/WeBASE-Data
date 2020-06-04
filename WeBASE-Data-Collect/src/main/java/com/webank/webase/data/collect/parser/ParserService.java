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
import com.webank.webase.data.collect.base.tools.Web3Tools;
import com.webank.webase.data.collect.contract.ContractService;
import com.webank.webase.data.collect.contract.MethodService;
import com.webank.webase.data.collect.contract.entity.ContractParam;
import com.webank.webase.data.collect.contract.entity.MethodInfo;
import com.webank.webase.data.collect.contract.entity.TbContract;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.parser.entity.ContractParserResult;
import com.webank.webase.data.collect.parser.entity.PageTransInfo;
import com.webank.webase.data.collect.parser.entity.TbParser;
import com.webank.webase.data.collect.parser.entity.TransParser;
import com.webank.webase.data.collect.parser.entity.UnusualContractInfo;
import com.webank.webase.data.collect.parser.entity.UnusualUserInfo;
import com.webank.webase.data.collect.parser.entity.UserParserResult;
import com.webank.webase.data.collect.receipt.entity.TransReceipt;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import com.webank.webase.data.collect.transaction.entity.TransactionInfo;
import com.webank.webase.data.collect.user.UserService;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * ParserService.
 */
@Log4j2
@Service
public class ParserService {

    @Autowired
    private GroupService groupService;
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

    public void updateUnusualUser(String userName, String address) {
        log.info("start updateUnusualUser address:{}", address);
        List<TbGroup> groupList = groupService.getGroupList(null, null);
        groupList.stream()
                .forEach(g -> parserMapper.updateUnusualUser(
                        TableName.PARSER.getTableName(g.getChainId(), g.getGroupId()), userName,
                        address));
    }

    /**
     * update unusual contract.
     */
    public void updateUnusualContract(int chainId, int groupId, String contractName,
            String contractBin) throws BaseException {
        try {
            log.info("start updateUnusualContract groupId:{} contractName:{} contractBin:{}",
                    groupId, contractName, contractBin);
            String tableName = TableName.PARSER.getTableName(chainId, groupId);
            contractBin = removeBinFirstAndLast(contractBin);
            String subContractBin = subContractBinForName(contractBin);
            List<String> txHashList = parserMapper.queryUnusualTxHash(tableName, subContractBin);
            if (CollectionUtils.isEmpty(txHashList)) {
                return;
            }
            // TODO
            TbTransaction trans = new TbTransaction();
            if (trans == null) {
                return;
            }
            ContractParserResult contractResult = parserContract(chainId, groupId, null,null);

            // update parser into
            parserMapper.updateUnusualContract(tableName, contractName, subContractBin,
                    contractResult.getInterfaceName(), contractResult.getTransUnusualType());
        } catch (Exception ex) {
            log.error("fail updateUnusualContract", ex);
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
    public void parserTransaction(int chainId, int groupId, TransactionInfo transInfo,
            TransReceipt transReceipt) {
        try {
            // parser user
            UserParserResult userResult = parserUser(chainId, groupId, transInfo.getFrom());
            // parser contract
            ContractParserResult contractResult =
                    parserContract(chainId, groupId, transInfo, transReceipt);

            TbParser tbParser = new TbParser();
            BeanUtils.copyProperties(userResult, tbParser);
            BeanUtils.copyProperties(contractResult, tbParser);
            tbParser.setBlockNumber(transInfo.getBlockNumber());
            tbParser.setTransHash(transInfo.getHash());
            // tbParser.setBlockTimestamp(trans.getBlockTimestamp());
            addRow(chainId, groupId, tbParser);
        } catch (Exception ex) {
            log.error("transaction:{} analysis fail...", transInfo.getHash(), ex);
        }
    }

    public TbParser queryTbParser(int chainId, int groupId, TbParser tbParser) {
        return parserMapper.queryTbParser(TableName.PARSER.getTableName(chainId, groupId),
                tbParser);
    }

    public void addRow(int chainId, int groupId, TbParser tbParser) {
        parserMapper.add(TableName.PARSER.getTableName(chainId, groupId), tbParser);
    }

    public void updateRow(int chainId, int groupId, TbParser tbParser) {
        parserMapper.update(TableName.PARSER.getTableName(chainId, groupId), tbParser);
    }

    /**
     * parser contract.
     */
    private ContractParserResult parserContract(int chainId, int groupId, TransactionInfo transInfo,
            TransReceipt transReceipt) {
        String transInput = transInfo.getInput();
        String contractAddress, contractName, interfaceName = "", contractBin;
        int transType = TransType.DEPLOY.getValue();
        int transUnusualType = TransUnusualType.NORMAL.getValue();

        if (isDeploy(transInfo.getTo())) {
            contractAddress = transReceipt.getContractAddress();
            if (ConstantProperties.ADDRESS_DEPLOY.equals(contractAddress)) {
                contractBin = StringUtils.removeStart(transInput, "0x");
                ContractParam param = new ContractParam();
                param.setChainId(chainId);
                param.setGroupId(groupId);
                param.setPartOfBytecodeBin(contractBin);
                TbContract tbContract = contractService.queryContract(param);
                if (Objects.nonNull(tbContract)) {
                    contractName = tbContract.getContractName();
                } else {
                    contractName = getNameFromContractBin(chainId, groupId, contractBin);
                    transUnusualType = TransUnusualType.CONTRACT.getValue();
                }
            } else {
                contractBin = frontInterfacee.getCodeFromFront(chainId, groupId, contractAddress,
                        transInfo.getBlockNumber());
                contractBin = removeBinFirstAndLast(contractBin);
                TbContract contractRow =
                        contractService.queryContractByBin(chainId, groupId, contractBin);
                if (Objects.nonNull(contractRow)) {
                    contractName = contractRow.getContractName();
                } else {
                    contractName = getNameFromContractBin(chainId, groupId, contractBin);
                    transUnusualType = TransUnusualType.CONTRACT.getValue();
                }
            }
            interfaceName = contractName;
        } else { // function call
            transType = TransType.CALL.getValue();
            String methodId = transInput.substring(0, 10);
            contractAddress = transInfo.getTo();
            contractBin = frontInterfacee.getCodeFromFront(chainId, groupId, contractAddress,
                    transInfo.getBlockNumber());
            contractBin = removeBinFirstAndLast(contractBin);

            TbContract contractRow =
                    contractService.queryContractByBin(chainId, groupId, contractBin);
            if (Objects.nonNull(contractRow)) {
                contractName = contractRow.getContractName();
                interfaceName = getInterfaceName(methodId, contractRow.getContractAbi());
                if (StringUtils.isBlank(interfaceName)) {
                    interfaceName = transInput.substring(0, 10);
                    transUnusualType = TransUnusualType.FUNCTION.getValue();
                }
            } else {
                contractName = getNameFromContractBin(chainId, groupId, contractBin);
                MethodInfo methodInfo = methodService.getByMethodId(methodId, null, groupId);
                if (Objects.nonNull(methodInfo)) {
                    interfaceName = getInterfaceName(methodId, "[" + methodInfo.getAbiInfo() + "]");
                    log.info("parser methodId:{} interfaceName:{}", methodId, interfaceName);
                }
                if (StringUtils.isBlank(interfaceName)) {
                    interfaceName = transInput.substring(0, 10);
                    transUnusualType = TransUnusualType.CONTRACT.getValue();
                }
            }
        }
        ContractParserResult contractResult = new ContractParserResult();
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
        String userName = userService.queryByAddress(userAddress).getUserName();
        int userType = ParserUserType.NORMAL.getValue();
        if (StringUtils.isBlank(userName)) {
            userName = userAddress;
            userType = ParserUserType.ABNORMAL.getValue();
        }
        UserParserResult parserResult = new UserParserResult();
        parserResult.setUserName(userName);
        parserResult.setUserType(userType);
        return parserResult;
    }

    /**
     * get interface name.
     */
    private String getInterfaceName(String methodId, String contractAbi) {
        if (StringUtils.isAnyBlank(methodId, contractAbi)) {
            log.warn("fail getInterfaceName. methodId:{} contractAbi:{}", methodId, contractAbi);
            return null;
        }

        String interfaceName = null;
        try {
            List<AbiDefinition> abiList = Web3Tools.loadContractDefinition(contractAbi);
            for (AbiDefinition abiDefinition : abiList) {
                if ("function".equals(abiDefinition.getType())) {
                    // support guomi sm3
                    String buildMethodId = Web3Tools.buildMethodId(abiDefinition);
                    if (methodId.equals(buildMethodId)) {
                        interfaceName = abiDefinition.getName();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("fail getInterfaceName", ex);
        }
        return interfaceName;
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
            return false;
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
