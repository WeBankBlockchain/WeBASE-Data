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
package com.webank.wsbcs.chdis.v3.web3api;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.exceptions.ClientException;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.Peers;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.web3.ResSystemConfig;
import com.webank.wsbcs.chdis.base.entity.web3.RspBlockInfo;
import com.webank.wsbcs.chdis.base.entity.web3.RspTransCountInfo;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.base.util.PrecompiledUtils;
import lombok.extern.log4j.Log4j2;

/**
 * Web3Api manage.
 */
@Log4j2
@Service
public class Web3ApiServiceV3 {

    @Autowired
    @Qualifier("bcosSDKV3")
    private Map<String, BcosSDK> bcosSDKMap;
    @Autowired
    @Qualifier("rpcClientV3")
    private Map<String, Client> rpcWeb3jMap;
    @Autowired
    @Qualifier("v3ClientMap")
    private Map<String, Map<String, Client>> clientMap;


    /**
     * key: nodeId, value: nodeStatusInfo cached
     */
    private static final int HASH_OF_TRANSACTION_LENGTH = 66;

    /**
     * getBlockNumber.
     */
    public BigInteger getBlockNumber(String groupId, String chainId) throws BaseException {

        BigInteger blockNumber;
        blockNumber = getWeb3j(groupId, chainId).getBlockNumber().getBlockNumber();
        return blockNumber;
    }
    
    public String getABI(String groupId, String chainId, String address) throws BaseException {
        String abi = getWeb3j(groupId, chainId).getABI(address).getABI();
        return abi;
    }
    
    public String getCode(String groupId, String chainId, String address) throws BaseException {
        String code = getWeb3j(groupId, chainId).getCode(address).getCode();
        return code;
    }

    /**
     * getTransactionReceipt.
     *
     * @param transHash transHash
     */
    public TransactionReceipt getTransactionReceipt(String groupId, String chainId,
            String transHash) throws BaseException {
        try {
            TransactionReceipt transactionReceipt = getWeb3j(groupId, chainId)
                    .getTransactionReceipt(transHash, true).getTransactionReceipt();
            return transactionReceipt;
        } catch (ClientException ex) {
            log.error("getBlockByHash failed :{}|{}", ex.getErrorCode(), ex.getErrorMessage());
            throw new BaseException(ConstantCode.TX_RECEIPT_HASH_NOT_ON_CHAIN);
        }
    }

    /**
     * getTransactionByHash.
     *
     * @param transHash transHash
     */
    public JsonTransactionResponse getTransactionByHash(String groupId, String chainId,
            String transHash, boolean withProof) throws BaseException {
        try {
            JsonTransactionResponse transaction = null;
            Optional<JsonTransactionResponse> opt =
                    getWeb3j(groupId, chainId).getTransaction(transHash, withProof).getTransaction();
            if (opt.isPresent()) {
                transaction = opt.get();
            } else {
                throw new BaseException(ConstantCode.TX_RECEIPT_HASH_NOT_ON_CHAIN);
            }
            return transaction;
        } catch (Exception ex) {
            log.error("getTransactionByHash failed :{}", ex.getMessage());
            throw new BaseException(ConstantCode.TX_RECEIPT_HASH_NOT_ON_CHAIN);
        }
    }


    private boolean blockNumberCheck(String groupId, String chainId, BigInteger blockNumber)
            throws BaseException {
        BigInteger currentNumber = getWeb3j(groupId, chainId).getBlockNumber().getBlockNumber();
        log.debug("**** currentNumber:{}", currentNumber);
        return (blockNumber.compareTo(currentNumber) > 0);

    }

    public ConsensusStatus.ConsensusStatusInfo getConsensusStatus(String groupId, String chainId) {
        return getWeb3j(groupId, chainId).getConsensusStatus().getConsensusStatus();
    }

    /**
     * getConsensusStatus by node name @mark： only support query from connected node
     * 
     * @param groupId
     * @param nodeName
     * @return
     */
    private ConsensusStatus.ConsensusStatusInfo getConsensusStatus(String groupId, String chainId,
            String nodeName) {
        Instant startTime = Instant.now();
        log.debug("getConsensusStatus groupId{},nodeName:{}", groupId, nodeName);
        ConsensusStatus.ConsensusStatusInfo consensusStatusInfo =
                getWeb3j(groupId, chainId).getConsensusStatus(nodeName).getConsensusStatus();
        log.debug("getConsensusStatus groupId{},nodeName:{},{}", groupId, nodeName,
                Duration.between(startTime, Instant.now()).toMillis());
        return consensusStatusInfo;
    }

    public SyncStatus.SyncStatusInfo getSyncStatus(String groupId, String chainId) {
        return getWeb3j(groupId, chainId).getSyncStatus().getSyncStatus();
    }

    /**
     * @mark： only support query from connected node
     * 
     * @param groupId
     * @param nodeName
     * @return
     */
    private SyncStatus.SyncStatusInfo getSyncStatus(String groupId, String chainId,
            String nodeName) {
        return getWeb3j(groupId, chainId).getSyncStatus(nodeName).getSyncStatus();
    }

    public List<String> getSealerList(String groupId, String chainId) {
        List<SealerList.Sealer> sealerList =
                getWeb3j(groupId, chainId).getSealerList().getSealerList();
        List<String> sealerStrList =
                sealerList.stream().map(SealerList.Sealer::getNodeID).collect(Collectors.toList());
        return sealerStrList;
    }

    public List<String> getObserverList(String groupId, String chainId) {
        return getWeb3j(groupId, chainId).getObserverList().getObserverList();
    }


    /**
     * search By Criteria
     */
    public Object searchByCriteria(String groupId, String chainId, String input, String bizSeqNo) {
        if (StringUtils.isBlank(input)) {
            log.warn("fail searchByCriteria. input is null");
            throw new BaseException(ConstantCode.PARAM_EXCEPTION, bizSeqNo);
        }
        if (StringUtils.isNumeric(input)) {
            return getBlockByNumber(groupId, chainId, new BigInteger(input));
        } else if (input.length() == HASH_OF_TRANSACTION_LENGTH) {
            JsonTransactionResponse txResponse =
                    getTransactionByHash(groupId, chainId, input, true);
            return txResponse;
            // BcosBlockHeader.BlockHeader blockHeader = getBlockNumber(groupId,
            // txResponse.getBlockNumber());
            // RspSearchTransaction rspSearchTransaction = new
            // RspSearchTransaction(blockHeader.getTimestamp(), txResponse);
            // return rspSearchTransaction;
        }
        return null;
    }

    /**
     * getBlockByNumber.
     *
     * @param blockHash blockNumber
     */
    public BcosBlock.Block getBlockByHash(String groupId, String chainId, String blockHash) {
        try {
            BcosBlock.Block block =
                    getWeb3j(groupId, chainId).getBlockByHash(blockHash, false, false).getBlock();
            return block;
        } catch (ClientException ex) {
            log.error("getBlockByHash failed :{}|{}", ex.getErrorCode(), ex.getErrorMessage());
            throw new BaseException(ConstantCode.BLOCK_HASH_ERROR);
        }
    }

    /**
     * getBlockByNumber.
     *
     * @param blockNumber blockNumber
     */
    public BcosBlock.Block getBlockByNumber(String groupId, String chainId,
            BigInteger blockNumber) {
        if (blockNumberCheck(groupId, chainId, blockNumber)) {
            return null;
        }
        try {
            BcosBlock.Block block = getWeb3j(groupId, chainId)
                    .getBlockByNumber(blockNumber, false, false).getBlock();
            return block;
        } catch (ClientException ex) {
            log.error("getBlockByNumber failed {}|{}|{}", blockNumber, ex.getErrorCode(), ex.getErrorMessage());
            throw new BaseException(ConstantCode.BLOCK_NUMBER_ERROR);
        }
    }

    public RspBlockInfo getRspBlockInfoByNumber(String groupId, String chainId,
            BigInteger blockNumber, Integer pageNumber, Integer pageSize) {
        if (blockNumberCheck(groupId, chainId, blockNumber)) {
            return null;
        }
        try {
            BcosBlock.Block block = getWeb3j(groupId, chainId)
                    .getBlockByNumber(blockNumber, false, false).getBlock();
            RspBlockInfo rspBlockInfo = new RspBlockInfo(block);
            if (rspBlockInfo.getTransCounts().intValue() > 5) {
                List<com.webank.wsbcs.chdis.base.entity.web3.RspBlockInfo.JsonTransactionResponse> transactions =
                        rspBlockInfo.getTransactions();
                Integer start = Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize)
                        .orElse(0);
                transactions = transactions.stream().skip(start).limit(pageSize)
                        .collect(Collectors.toList());
                rspBlockInfo.setTransactions(transactions);
            }
            return rspBlockInfo;
        } catch (ClientException ex) {
            log.error("getBlockByNumber failed :{}|{}", ex.getErrorCode(), ex.getErrorMessage());
            throw new BaseException(ConstantCode.BLOCK_NUMBER_ERROR);
        }
    }

    /**
     * getBlockByNumber.
     *
     * @param blockHash blockNumber
     */
    public BcosBlock.Block getBlockByHash(String groupId, String chainId, String blockHash,
            boolean onlyHeader, boolean onlyTxHash) {
        try {
            BcosBlock.Block block = getWeb3j(groupId, chainId)
                    .getBlockByHash(blockHash, onlyHeader, onlyTxHash).getBlock();
            return block;
        } catch (ClientException ex) {
            log.error("getBlockByHash failed :{}|{}", ex.getErrorCode(), ex.getErrorMessage());
            throw new BaseException(ConstantCode.BLOCK_HASH_ERROR);
        }
    }

    /**
     * getBlockByNumber.
     *
     * @param blockNumber blockNumber
     */
    public BcosBlock.Block getBlockByNumber(String groupId, String chainId, BigInteger blockNumber,
            boolean onlyHeader, boolean onlyTxHash) {
        if (blockNumberCheck(groupId, chainId, blockNumber)) {
            throw new BaseException(ConstantCode.BLOCK_NUMBER_ERROR);
        }
        try {
            BcosBlock.Block block = getWeb3j(groupId, chainId)
                    .getBlockByNumber(blockNumber, onlyHeader, onlyTxHash).getBlock();
            return block;
        } catch (ClientException ex) {
            log.error("getBlockByNumber failed :{}|{}", ex.getErrorCode(), ex.getErrorMessage());
            throw new BaseException(ConstantCode.BLOCK_NUMBER_ERROR);
        }
    }

    /**
     * get transaction hash by block number
     */
    public List<JsonTransactionResponse> getTransByBlockNumber(String groupId, String chainId,
            BigInteger blockNumber, String bizSeqNo) {// NOSONAR
        log.debug("start getTransByBlockNumber. groupId:{} blockNumber:{}", groupId, blockNumber);
        BcosBlock.Block blockInfo = getBlockByNumber(groupId, chainId, blockNumber);
        if (blockInfo == null) {
            return Collections.emptyList();
        }
        List<JsonTransactionResponse> transactionResponses = new ArrayList<>();
        List<BcosBlock.TransactionResult> transInBLock = blockInfo.getTransactions();
        for (BcosBlock.TransactionResult t : transInBLock) {
            JsonTransactionResponse tran = (JsonTransactionResponse) t;
            transactionResponses.add(tran);
        }
        log.debug("end getTransByBlockNumber. transInBLock:{}",
                JsonUtils.toJSONString(transInBLock));
        return transactionResponses;
    }

    /**
     * get transaction counts.
     */
    public RspTransCountInfo getTransCnt(String groupId, String chainId) {
        TotalTransactionCount.TransactionCountInfo transactionCount =
                getWeb3j(groupId, chainId).getTotalTransactionCount().getTotalTransactionCount();
        String txSumHex = transactionCount.getTransactionCount();
        String blockNumberHex = transactionCount.getBlockNumber();
        String failedTxSumHex = transactionCount.getFailedTransactionCount();
        RspTransCountInfo txCountResult = new RspTransCountInfo(Numeric.decodeQuantity(txSumHex),
                Numeric.decodeQuantity(blockNumberHex), Numeric.decodeQuantity(failedTxSumHex));
        log.info("getTransCnt :{}", JsonUtils.toJSONString(txCountResult));
        return txCountResult;
    }


    /**
     * get system config list by groupId directory through web3j instead of Precompiled instance
     *
     * @param groupId
     * @return
     * @throws IOException
     */
    public List<ResSystemConfig> getConfigList(String groupId, String chainId) {
        List<ResSystemConfig> list = new ArrayList<>();

        String txCountLimit =
                this.getSystemConfigByKey(groupId, chainId, PrecompiledUtils.TxCountLimit);
        ResSystemConfig systemConfigCount = new ResSystemConfig();
        systemConfigCount.setConfigKey(PrecompiledUtils.TxCountLimit);
        systemConfigCount.setConfigValue(txCountLimit);
        systemConfigCount.setGroupId(groupId);

        String txGasLimit =
                this.getSystemConfigByKey(groupId, chainId, PrecompiledUtils.TxGasLimit);
        ResSystemConfig systemConfigGas = new ResSystemConfig();
        systemConfigGas.setConfigKey(PrecompiledUtils.TxGasLimit);
        systemConfigGas.setConfigValue(txGasLimit);
        systemConfigGas.setGroupId(groupId);

        // String consensusTimeout = this.getSystemConfigByKey(groupId,
        // PrecompiledUtils.ConsensusTimeout);
        // ResSystemConfig systemConfigTimeout = new ResSystemConfig();
        // systemConfigTimeout.setConfigKey(PrecompiledUtils.ConsensusTimeout);
        // systemConfigTimeout.setConfigValue(consensusTimeout);
        // systemConfigTimeout.setGroupId(groupId);

        list.add(systemConfigCount);
        list.add(systemConfigGas);
        // list.add(systemConfigTimeout);
        return list;
    }

    /**
     * get getSystemConfigByKey of tx_count_limit/tx_gas_limit
     * 
     * @param groupId
     * @param key
     * @return value for config, ex: return 1000
     */
    public String getSystemConfigByKey(String groupId, String chainId, String key) {
        return getWeb3j(groupId, chainId).getSystemConfigByKey(key).getSystemConfig().getValue();
    }



    /**
     * get latest number of peer on chain.
     */
    private long getBlockNumberOfNodeOnChain(SyncStatus.SyncStatusInfo syncStatus, String nodeId) {
        if (Objects.isNull(syncStatus)) {
            log.warn("fail getBlockNumberOfNodeOnChain. SyncStatus is null");
            return 0L;
        }
        if (StringUtils.isBlank(nodeId)) {
            log.warn("fail getBlockNumberOfNodeOnChain. nodeId is null");
            return 0L;
        }
        if (nodeId.equals(syncStatus.getNodeId())) {
            return syncStatus.getBlockNumber();
        }
        List<SyncStatus.PeersInfo> peerList = syncStatus.getPeers();
        // blockNumber
        long latestNumber = peerList.stream().filter(peer -> nodeId.equals(peer.getNodeId()))
                .map(SyncStatus.PeersInfo::getBlockNumber).findFirst().orElse(0L);
        return latestNumber;
    }


    /**
     * get group list and refresh web3j map
     * 
     * @return
     */
    public List<String> getGroupList(String chainId) {
        log.debug("getGroupList. ");
        List<String> groupIdList = getWeb3j(chainId).getGroupList().getResult().getGroupList();
        log.debug("end getGroupList :{}", groupIdList);
        return groupIdList;
    }

    /**
     * node id list is groupPeers in 3.0
     * 
     * @param groupId
     * @return
     */
    public List<String> getNodeIdList(String groupId, String chainId) throws BaseException {
        return getWeb3j(groupId, chainId).getGroupPeers().getGroupPeers();
    }


    public List<String> getGroupPeers(String groupId, String chainId) {
        GroupPeers groupPeers = getWeb3j(groupId, chainId).getGroupPeers();
        return groupPeers.getGroupPeers();
    }

    // get all peers of chain
    public Peers.PeersInfo getPeers(String groupId, String chainId) {
        return getWeb3j(groupId, chainId).getPeers().getPeers();
    }


    public List<BcosGroupNodeInfo.GroupNodeInfo> getGroupNodeInfo(String groupId, String chainId) {
        List<BcosGroupNodeInfo.GroupNodeInfo> groupNodeInfos =
                getWeb3j(groupId, chainId).getGroupInfo().getResult().getNodeList();
        return groupNodeInfos;
    }

    public Map<String, String> getNodeIdNameMap(String groupId, String chainId) {
        Instant startTime = Instant.now();
        log.debug("refreshAndGetNodeNameMap groupId:{}", groupId);
        Map<String, String> nodeIdNameMap = new HashMap<>();
        List<BcosGroupNodeInfo.GroupNodeInfo> nodeInfoList =
                this.getGroupNodeInfo(groupId, chainId);
        for (BcosGroupNodeInfo.GroupNodeInfo node : nodeInfoList) {
            String nodeId = node.getIniConfig().getNodeID();
            nodeIdNameMap.put(nodeId, node.getName());
        }
        log.debug("end refreshAndGetNodeNameMap groupId:{},nodeIdNameMap:{}", groupId,
                nodeIdNameMap);
        log.info("refreshAndGetNodeNameMap groupId{},{}", groupId,
                Duration.between(startTime, Instant.now()).toMillis());
        return nodeIdNameMap;
    }

    public CryptoSuite getCryptoSuite(String groupId, String chainId) throws BaseException {
        return getWeb3j(groupId, chainId).getCryptoSuite();
    }

    public Integer getCryptoType(String groupId, String chainId) throws BaseException {
        return getWeb3j(groupId, chainId).getCryptoType();
    }


    /**
     * get first web3j in web3jMap
     *
     * @return
     */
    public Client getWeb3j(String chainId) {
        Client client = rpcWeb3jMap.get(chainId);
        if (client == null) {
            throw new BaseException(ConstantCode.WEB3J_CLIENT_IS_NULL);
        }
        return client;
    }

    /**
     * check client connected with group and get target group's web3j
     * 
     * @param groupId
     * @return
     */
    public Client getWeb3j(String groupId, String chainId) throws BaseException {

        Map<String, Client> groupClientMap = clientMap.get(chainId);
        if (groupClientMap == null || groupClientMap.isEmpty()) {
            log.warn("chainId:{},groupId:{} chainId not exist.", chainId, groupId);
            throw new BaseException(ConstantCode.CHAIN_ID_NOT_EXIST);
        }
        Client client = groupClientMap.get(groupId);
        if (client == null) {
            log.warn("chainId:{},groupId:{} client not in map", chainId, groupId);
            boolean checkGroupExist = rpcWeb3jMap.get(chainId).getGroupList().getResult()
                    .getGroupList().contains(groupId);
            log.warn("chainId:{},groupId:{} client not in map, checkGroupExist:{}", chainId,
                    groupId, checkGroupExist);
            if (!checkGroupExist) {
                throw new BaseException(ConstantCode.GROUPID_NOT_EXIST);
            }
            client = bcosSDKMap.get(chainId).getClient(groupId);
            groupClientMap.put(groupId, client);
            // 不需要put到chainId的map中
        }
        return client;
    }

    public BcosSDK getSdk(String chainId) {
        return bcosSDKMap.get(chainId);
    }
}
