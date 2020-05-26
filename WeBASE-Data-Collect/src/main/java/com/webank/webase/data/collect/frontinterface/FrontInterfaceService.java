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
import com.webank.webase.data.collect.block.entity.BlockInfo;
import com.webank.webase.data.collect.front.entity.TotalTransCountInfo;
import com.webank.webase.data.collect.frontinterface.entity.PeerInfo;
import com.webank.webase.data.collect.frontinterface.entity.SyncStatus;
import com.webank.webase.data.collect.monitor.ChainTransInfo;
import com.webank.webase.data.collect.receipt.entity.TransReceipt;
import com.webank.webase.data.collect.transaction.entity.TransactionInfo;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.NodeVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class FrontInterfaceService {

    @Autowired
    private FrontRestTools frontRestTools;

    public BlockInfo getBlockByNumberFromSpecificFront(String frontIp, Integer frontPort,
            Integer groupId, BigInteger blockNumber) throws BaseException {
        String uri = String.format(FrontRestTools.URI_BLOCK_BY_NUMBER, blockNumber);
        return frontRestTools.getFromSpecificFront(groupId, frontIp, frontPort, uri,
                BlockInfo.class);
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

    public PeerInfo[] getPeers(Integer groupId) {
        return frontRestTools.getForEntity(groupId, FrontRestTools.URI_PEERS, PeerInfo[].class);
    }

    public String getContractCode(Integer groupId, String address, BigInteger blockNumber)
            throws BaseException {
        String uri = String.format(FrontRestTools.URI_CODE, address, blockNumber);
        String contractCode = frontRestTools.getForEntity(groupId, uri, String.class);
        return contractCode;
    }

    public TransReceipt getTransReceipt(Integer groupId, String transHash) throws BaseException {
        String uri = String.format(FrontRestTools.FRONT_TRANS_RECEIPT_BY_HASH_URI, transHash);
        TransReceipt transReceipt = frontRestTools.getForEntity(groupId, uri, TransReceipt.class);
        return transReceipt;
    }

    public TransactionInfo getTransaction(Integer groupId, String transHash) throws BaseException {
        if (StringUtils.isBlank(transHash)) {
            return null;
        }
        String uri = String.format(FrontRestTools.URI_TRANS_BY_HASH, transHash);
        TransactionInfo transInfo =
                frontRestTools.getForEntity(groupId, uri, TransactionInfo.class);
        return transInfo;
    }

    public BlockInfo getBlockByNumber(Integer groupId, BigInteger blockNumber)
            throws BaseException {
        String uri = String.format(FrontRestTools.URI_BLOCK_BY_NUMBER, blockNumber);
        BlockInfo blockInfo = null;
        try {
            blockInfo = frontRestTools.getForEntity(groupId, uri, BlockInfo.class);
        } catch (Exception ex) {
            log.info("fail getBlockByNumber,exception:{}", ex);
        }
        return blockInfo;
    }

    public BlockInfo getblockByHash(Integer groupId, String blockHash) throws BaseException {
        String uri = String.format(FrontRestTools.URI_BLOCK_BY_HASH, blockHash);
        BlockInfo blockInfo = frontRestTools.getForEntity(groupId, uri, BlockInfo.class);
        return blockInfo;
    }

    public ChainTransInfo getTransInfoByHash(Integer groupId, String hash) throws BaseException {
        TransactionInfo trans = getTransaction(groupId, hash);
        if (Objects.isNull(trans)) {
            return null;
        }
        ChainTransInfo chainTransInfo = new ChainTransInfo(trans.getFrom(), trans.getTo(),
                trans.getInput(), trans.getBlockNumber());
        return chainTransInfo;
    }

    public String getAddressByHash(Integer groupId, String transHash) throws BaseException {
        TransReceipt transReceipt = getTransReceipt(groupId, transHash);
        String contractAddress = transReceipt.getContractAddress();
        return contractAddress;
    }

    public String getCodeFromFront(Integer groupId, String contractAddress, BigInteger blockNumber)
            throws BaseException {
        String uri = String.format(FrontRestTools.URI_CODE, contractAddress, blockNumber);
        String code = frontRestTools.getForEntity(groupId, uri, String.class);
        return code;
    }

    public TotalTransCountInfo getTotalTransactionCount(Integer groupId) {
        TotalTransCountInfo totalCount = frontRestTools.getForEntity(groupId,
                FrontRestTools.URI_TRANS_TOTAL, TotalTransCountInfo.class);
        return totalCount;
    }

    public List<TransactionInfo> getTransByBlockNumber(Integer groupId, BigInteger blockNumber) {
        BlockInfo blockInfo = getBlockByNumber(groupId, blockNumber);
        if (blockInfo == null) {
            return null;
        }
        List<TransactionInfo> transInBLock = blockInfo.getTransactions();
        return transInBLock;
    }

    @SuppressWarnings("unchecked")
    public List<String> getGroupPeers(Integer groupId) {
        List<String> groupPeers =
                frontRestTools.getForEntity(groupId, FrontRestTools.URI_GROUP_PEERS, List.class);
        return groupPeers;
    }

    @SuppressWarnings("unchecked")
    public List<String> getObserverList(Integer groupId) {
        List<String> observers = frontRestTools.getForEntity(groupId,
                FrontRestTools.URI_GET_OBSERVER_LIST, List.class);
        return observers;
    }

    public String getConsensusStatus(Integer groupId) {
        String consensusStatus = frontRestTools.getForEntity(groupId,
                FrontRestTools.URI_CONSENSUS_STATUS, String.class);
        return consensusStatus;
    }

    public SyncStatus getSyncStatus(Integer groupId) {
        SyncStatus ststus = frontRestTools.getForEntity(groupId, FrontRestTools.URI_CSYNC_STATUS,
                SyncStatus.class);
        return ststus;
    }

    public BigInteger getLatestBlockNumber(Integer groupId) {
        BigInteger latestBlockNmber = frontRestTools.getForEntity(groupId,
                FrontRestTools.URI_BLOCK_NUMBER, BigInteger.class);
        return latestBlockNmber;
    }

    @SuppressWarnings("unchecked")
    public List<String> getSealerList(Integer groupId) {
        List<String> getSealerList = frontRestTools.getForEntity(groupId,
                FrontRestTools.URI_GET_SEALER_LIST, List.class);
        return getSealerList;
    }

    public String getSystemConfigByKey(Integer groupId, String key) {
        String uri = String.format(FrontRestTools.URI_SYSTEMCONFIG_BY_KEY, key);
        String config = frontRestTools.getForEntity(groupId, uri, String.class);
        return config;
    }
}
