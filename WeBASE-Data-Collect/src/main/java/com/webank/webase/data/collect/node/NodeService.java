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
package com.webank.webase.data.collect.node;

import com.fasterxml.jackson.core.type.TypeReference;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.frontinterface.entity.PeerInfo;
import com.webank.webase.data.collect.frontinterface.entity.PeerOfConsensusStatus;
import com.webank.webase.data.collect.frontinterface.entity.PeerOfSyncStatus;
import com.webank.webase.data.collect.frontinterface.entity.SyncStatus;
import com.webank.webase.data.collect.node.entity.NodeParam;
import com.webank.webase.data.collect.node.entity.TbNode;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * services for node data.
 */
@Log4j2
@Service
public class NodeService {

    @Autowired
    private NodeMapper nodeMapper;
    @Autowired
    private FrontInterfaceService frontInterface;

    private static final Long CHECK_NODE_WAIT_MIN_MILLIS = 5000L;

    /**
     * add new node data.
     */
    public void addNodeInfo(Integer chainId, Integer groupId, PeerInfo peerInfo)
            throws BaseException {
        String nodeIp = null;
        Integer nodeP2PPort = null;

        if (StringUtils.isNotBlank(peerInfo.getIPAndPort())) {
            String[] ipPort = peerInfo.getIPAndPort().split(":");
            nodeIp = ipPort[0];
            nodeP2PPort = Integer.valueOf(ipPort[1]);
        }

        // add row
        TbNode tbNode = new TbNode();
        tbNode.setNodeId(peerInfo.getNodeId());
        tbNode.setChainId(chainId);
        tbNode.setGroupId(groupId);
        tbNode.setNodeIp(nodeIp);
        tbNode.setP2pPort(nodeP2PPort);
        nodeMapper.add(tbNode);
    }

    /**
     * query count of node.
     */
    public Integer countOfNode(NodeParam queryParam) throws BaseException {
        try {
            Integer nodeCount = nodeMapper.getCount(queryParam);
            return nodeCount;
        } catch (RuntimeException ex) {
            log.error("fail countOfNode . queryParam:{}", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query node list by page.
     */
    public List<TbNode> queryNodeList(NodeParam queryParam) throws BaseException {
        // query node list
        List<TbNode> listOfNode = nodeMapper.getList(queryParam);
        return listOfNode;
    }

    /**
     * query node by groupId
     */
    public List<TbNode> queryByGroupId(int chainId, int groupId) {
        NodeParam nodeParam = new NodeParam();
        nodeParam.setChainId(chainId);
        nodeParam.setGroupId(groupId);
        return queryNodeList(nodeParam);
    }

    /**
     * query all node list
     */
    public List<TbNode> getAll() {
        return queryNodeList(new NodeParam());
    }

    /**
     * query node info.
     */
    public TbNode queryByNodeId(String nodeId) throws BaseException {
        try {
            TbNode nodeRow = nodeMapper.queryByNodeId(nodeId);
            return nodeRow;
        } catch (RuntimeException ex) {
            log.error("fail queryNode . nodeId:{}", nodeId, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }


    /**
     * update node info.
     */
    public void updateNode(TbNode tbNode) throws BaseException {
        Integer affectRow = 0;
        try {
            affectRow = nodeMapper.update(tbNode);
        } catch (RuntimeException ex) {
            log.error("updateNodeInfo exception", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }

        if (affectRow == 0) {
            log.warn("affect 0 rows of tb_node");
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
        log.debug("end updateNodeInfo");
    }

    /**
     * query node info.
     */
    public TbNode queryNodeInfo(NodeParam nodeParam) {
        TbNode tbNode = nodeMapper.queryNodeInfo(nodeParam);
        return tbNode;
    }

    /**
     * delete by node and group.
     */
    public void deleteByNodeAndGroupId(String nodeId, int groupId) throws BaseException {
        nodeMapper.deleteByNodeAndGroup(nodeId, groupId);
    }

    /**
     * delete by groupId.
     */
    public void deleteByGroupId(int chainId, int groupId) {
        if (chainId == 0 || groupId == 0) {
            return;
        }
        nodeMapper.deleteByGroupId(chainId, groupId);
    }

    /**
     * delete by chainId.
     */
    public void deleteByChainId(int chainId) {
        if (chainId == 0) {
            return;
        }
        nodeMapper.deleteByChainId(chainId);
    }

    /**
     * check node status
     */
    public void checkAndUpdateNodeStatus(int chainId, int groupId) {
        // get local node list
        List<TbNode> nodeList = queryByGroupId(chainId, groupId);

        // getPeerOfConsensusStatus
        List<PeerOfConsensusStatus> consensusList = getPeerOfConsensusStatus(chainId, groupId);
        if (Objects.isNull(consensusList)) {
            log.error("fail checkNodeStatus, consensusList is null");
            return;
        }

        // getObserverList
        List<String> observerList = frontInterface.getObserverList(chainId, groupId);

        for (TbNode tbNode : nodeList) {
            String nodeId = tbNode.getNodeId();
            BigInteger localBlockNumber = tbNode.getBlockNumber();
            BigInteger localPbftView = tbNode.getPbftView();
            LocalDateTime modifyTime = tbNode.getModifyTime();
            LocalDateTime createTime = tbNode.getCreateTime();

            Duration duration = Duration.between(modifyTime, LocalDateTime.now());
            Long subTime = duration.toMillis();
            if (subTime < CHECK_NODE_WAIT_MIN_MILLIS && createTime.isBefore(modifyTime)) {
                log.info("checkNodeStatus jump over. subTime:{}", subTime);
                return;
            }

            int nodeType = 0; // 0-consensus;1-observer
            if (observerList != null) {
                nodeType = observerList.stream()
                        .filter(observer -> observer.equals(tbNode.getNodeId())).map(c -> 1)
                        .findFirst().orElse(0);
            }

            BigInteger latestNumber = getBlockNumberOfNodeOnChain(chainId, groupId, nodeId);// blockNumber
            BigInteger latestView =
                    consensusList.stream().filter(cl -> nodeId.equals(cl.getNodeId()))
                            .map(c -> c.getView()).findFirst().orElse(BigInteger.ZERO);// pbftView

            if (nodeType == 0) { // 0-consensus;1-observer
                if (localBlockNumber.equals(latestNumber) && localPbftView.equals(latestView)) {
                    tbNode.setNodeActive(DataStatus.INVALID.getValue());
                } else {
                    tbNode.setBlockNumber(latestNumber);
                    tbNode.setPbftView(latestView);
                    tbNode.setNodeActive(DataStatus.NORMAL.getValue());
                }
            } else { // observer
                if (!latestNumber.equals(frontInterface.getLatestBlockNumber(chainId, groupId))) {
                    tbNode.setNodeActive(DataStatus.INVALID.getValue());
                } else {
                    tbNode.setBlockNumber(latestNumber);
                    tbNode.setPbftView(latestView);
                    tbNode.setNodeActive(DataStatus.NORMAL.getValue());
                }
            }
            // update node
            updateNode(tbNode);
        }

    }

    /**
     * get latest number of peer on chain.
     */
    private BigInteger getBlockNumberOfNodeOnChain(int chainId, int groupId, String nodeId) {
        SyncStatus syncStatus = frontInterface.getSyncStatus(chainId, groupId);
        if (nodeId.equals(syncStatus.getNodeId())) {
            return syncStatus.getBlockNumber();
        }
        List<PeerOfSyncStatus> peerList = syncStatus.getPeers();
        BigInteger latestNumber = peerList.stream().filter(peer -> nodeId.equals(peer.getNodeId()))
                .map(s -> s.getBlockNumber()).findFirst().orElse(BigInteger.ZERO);// blockNumber
        return latestNumber;
    }

    /**
     * get peer of consensusStatus
     */
    private List<PeerOfConsensusStatus> getPeerOfConsensusStatus(int chainId, int groupId) {
        String consensusStatusJson = frontInterface.getConsensusStatus(chainId, groupId);
        if (StringUtils.isBlank(consensusStatusJson)) {
            return null;
        }
        List jsonArr = JacksonUtils.stringToObj(consensusStatusJson, List.class);
        if (jsonArr == null) {
            return null;
        }
        List<PeerOfConsensusStatus> dataIsList = new ArrayList<>();
        for (int i = 0; i < jsonArr.size(); i++) {
            if (jsonArr.get(i) instanceof List) {
                List<PeerOfConsensusStatus> tempList =
                        JacksonUtils.stringToObj(JacksonUtils.objToString(jsonArr.get(i)),
                                new TypeReference<List<PeerOfConsensusStatus>>() {});
                if (tempList != null) {
                    dataIsList.addAll(tempList);
                }
            }
        }
        return dataIsList;
    }

    /**
     * add sealer and observer in NodeList return: List<String> nodeIdList
     */
    public List<PeerInfo> getSealerAndObserverList(int chainId, int groupId) {
        log.debug("start getSealerAndObserverList groupId:{}", groupId);
        List<String> sealerList = frontInterface.getSealerList(chainId, groupId);
        List<String> observerList = frontInterface.getObserverList(chainId, groupId);
        List<PeerInfo> resList = new ArrayList<>();
        sealerList.stream().forEach(nodeId -> resList.add(new PeerInfo(nodeId)));
        observerList.stream().forEach(nodeId -> resList.add(new PeerInfo(nodeId)));
        log.debug("end getSealerAndObserverList resList:{}", resList);
        return resList;
    }
}
