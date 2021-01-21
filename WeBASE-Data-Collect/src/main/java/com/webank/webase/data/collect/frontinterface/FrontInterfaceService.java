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
package com.webank.webase.data.collect.frontinterface;

import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.front.entity.TotalTransCountInfo;
import com.webank.webase.data.collect.frontinterface.entity.PeerInfo;
import com.webank.webase.data.collect.frontinterface.entity.SyncStatus;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.TransactionResult;
import org.fisco.bcos.web3j.protocol.core.methods.response.NodeVersion;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class FrontInterfaceService {

    @Autowired
    private FrontRestTools frontRestTools;

    public Block getBlockByNumberFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId, BigInteger blockNumber) throws BaseException {
        String uri = String.format(FrontRestTools.URI_BLOCK_BY_NUMBER, blockNumber);
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort, uri, Block.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getGroupListFromSpecificFront(String frontIp, Integer frontPort) {
        Integer groupId = Integer.MAX_VALUE;
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort,
                FrontRestTools.URI_GROUP_PLIST, List.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getGroupPeersFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId) {
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort,
                FrontRestTools.URI_GROUP_PEERS, List.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getNodeIDListFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId) {
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort,
                FrontRestTools.URI_NODEID_LIST, List.class);
    }

    public PeerInfo[] getPeersFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId) {
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort,
                FrontRestTools.URI_PEERS, PeerInfo[].class);
    }

    public SyncStatus getSyncStatusFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId) {
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort,
                FrontRestTools.URI_CSYNC_STATUS, SyncStatus.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getObserverListFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId) {
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort,
                FrontRestTools.URI_GET_OBSERVER_LIST, List.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getSealerListFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId) {
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort,
                FrontRestTools.URI_GET_SEALER_LIST, List.class);
    }

    public Integer getEncryptTypeFromSpecificFront(String nodeIp, Integer frontPort) {
        Integer groupId = Integer.MAX_VALUE;
        int encryptType = frontRestTools.getFromSpecificFront(groupId, nodeIp, frontPort,
                FrontRestTools.URI_ENCRYPT_TYPE, Integer.class);
        return encryptType;
    }

    public String getClientVersion(String frontIp, Integer frontPort, Integer groupId) {
        NodeVersion.Version clientVersion = frontRestTools.getFromSpecificFront(groupId, frontIp,
                frontPort, FrontRestTools.URI_GET_CLIENT_VERSION, NodeVersion.Version.class);
        return clientVersion.getVersion();
    }

    public PeerInfo[] getPeers(Integer chainId, Integer groupId) {
        return frontRestTools.getForEntity(chainId, groupId, FrontRestTools.URI_PEERS,
                PeerInfo[].class);
    }

    public String getContractCode(Integer chainId, Integer groupId, String address,
            BigInteger blockNumber) throws BaseException {
        String uri = String.format(FrontRestTools.URI_CODE, address, blockNumber);
        String contractCode = frontRestTools.getForEntity(chainId, groupId, uri, String.class);
        return contractCode;
    }

    public TransactionReceipt getTransReceipt(Integer chainId, Integer groupId, String transHash)
            throws BaseException {
        String uri = String.format(FrontRestTools.FRONT_TRANS_RECEIPT_BY_HASH_URI, transHash);
        TransactionReceipt transReceipt =
                frontRestTools.getForEntity(chainId, groupId, uri, TransactionReceipt.class);
        return transReceipt;
    }

    public Transaction getTransaction(Integer chainId, Integer groupId, String transHash)
            throws BaseException {
        if (StringUtils.isBlank(transHash)) {
            return null;
        }
        String uri = String.format(FrontRestTools.URI_TRANS_BY_HASH, transHash);
        Transaction transInfo =
                frontRestTools.getForEntity(chainId, groupId, uri, Transaction.class);
        return transInfo;
    }

    public Block getBlockByNumber(Integer chainId, Integer groupId, BigInteger blockNumber)
            throws BaseException {
        String uri = String.format(FrontRestTools.URI_BLOCK_BY_NUMBER, blockNumber);
        Block block = null;
        try {
            block = frontRestTools.getForEntity(chainId, groupId, uri, Block.class);
        } catch (Exception ex) {
            log.info("fail getBlockByNumber,exception:{}", ex);
        }
        return block;
    }

    public Block getblockByHash(Integer chainId, Integer groupId, String blockHash)
            throws BaseException {
        String uri = String.format(FrontRestTools.URI_BLOCK_BY_HASH, blockHash);
        Block block = frontRestTools.getForEntity(chainId, groupId, uri, Block.class);
        return block;
    }

    public String getAddressByHash(Integer chainId, Integer groupId, String transHash)
            throws BaseException {
        TransactionReceipt transReceipt = getTransReceipt(chainId, groupId, transHash);
        String contractAddress = transReceipt.getContractAddress();
        return contractAddress;
    }

    public String getCodeFromFront(Integer chainId, Integer groupId, String contractAddress,
            BigInteger blockNumber) throws BaseException {
        String uri = String.format(FrontRestTools.URI_CODE, contractAddress, blockNumber);
        String code = frontRestTools.getForEntity(chainId, groupId, uri, String.class);
        return code;
    }

    public TotalTransCountInfo getTotalTransactionCount(Integer chainId, Integer groupId) {
        TotalTransCountInfo totalCount = frontRestTools.getForEntity(chainId, groupId,
                FrontRestTools.URI_TRANS_TOTAL, TotalTransCountInfo.class);
        return totalCount;
    }

    @SuppressWarnings("rawtypes")
    public List<TransactionResult> getTransByBlockNumber(Integer chainId, Integer groupId,
            BigInteger blockNumber) {
        Block block = getBlockByNumber(chainId, groupId, blockNumber);
        if (block == null) {
            return null;
        }
        List<TransactionResult> transList = block.getTransactions();
        return transList;
    }

    @SuppressWarnings("unchecked")
    public List<String> getGroupPeers(Integer chainId, Integer groupId) {
        List<String> groupPeers = frontRestTools.getForEntity(chainId, groupId,
                FrontRestTools.URI_GROUP_PEERS, List.class);
        return groupPeers;
    }

    @SuppressWarnings("unchecked")
    public List<String> getObserverList(Integer chainId, Integer groupId) {
        List<String> observers = frontRestTools.getForEntity(chainId, groupId,
                FrontRestTools.URI_GET_OBSERVER_LIST, List.class);
        return observers;
    }

    public String getConsensusStatus(Integer chainId, Integer groupId) {
        String consensusStatus = frontRestTools.getForEntity(chainId, groupId,
                FrontRestTools.URI_CONSENSUS_STATUS, String.class);
        return consensusStatus;
    }

    public SyncStatus getSyncStatus(Integer chainId, Integer groupId) {
        SyncStatus ststus = frontRestTools.getForEntity(chainId, groupId,
                FrontRestTools.URI_CSYNC_STATUS, SyncStatus.class);
        return ststus;
    }

    public BigInteger getLatestBlockNumber(Integer chainId, Integer groupId) {
        BigInteger latestBlockNmber = frontRestTools.getForEntity(chainId, groupId,
                FrontRestTools.URI_BLOCK_NUMBER, BigInteger.class);
        return latestBlockNmber;
    }

    @SuppressWarnings("unchecked")
    public List<String> getSealerList(Integer chainId, Integer groupId) {
        List<String> getSealerList = frontRestTools.getForEntity(chainId, groupId,
                FrontRestTools.URI_GET_SEALER_LIST, List.class);
        return getSealerList;
    }

    public String getSystemConfigByKey(Integer chainId, Integer groupId, String key) {
        String uri = String.format(FrontRestTools.URI_SYSTEMCONFIG_BY_KEY, key);
        String config = frontRestTools.getForEntity(chainId, groupId, uri, String.class);
        return config;
    }

    public BigInteger getGasRemain(Integer chainId, Integer groupId, String userAddress) {
        Map<String, Object> params = new HashMap<>();
        params.put("chainId", chainId);
        params.put("groupId", groupId);
        params.put("handleType", "queryRemainGas");
        params.put("userAccount", userAddress);
        BigInteger gasRemain = frontRestTools.postForEntity(chainId, groupId,
                FrontRestTools.URI_GAS_CHARGE_MANAGE, params, BigInteger.class);
        return gasRemain;
    }
}
