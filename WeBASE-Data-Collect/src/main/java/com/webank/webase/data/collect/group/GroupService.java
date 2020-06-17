/**
 * Copyright 2014-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.data.collect.group;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.chain.ChainService;
import com.webank.webase.data.collect.chain.entity.TbChain;
import com.webank.webase.data.collect.contract.ContractService;
import com.webank.webase.data.collect.contract.MethodService;
import com.webank.webase.data.collect.front.FrontService;
import com.webank.webase.data.collect.front.entity.FrontParam;
import com.webank.webase.data.collect.front.entity.TbFront;
import com.webank.webase.data.collect.front.entity.TotalTransCountInfo;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapCache;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapService;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.frontinterface.entity.PeerInfo;
import com.webank.webase.data.collect.group.entity.GroupGeneral;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.node.NodeService;
import com.webank.webase.data.collect.node.entity.TbNode;
import com.webank.webase.data.collect.table.TableService;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * services for group data.
 */
@Log4j2
@Service
public class GroupService {

    @Autowired
    private ChainService chainService;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private TableService tableService;
    @Autowired
    private FrontInterfaceService frontInterface;
    @Autowired
    private FrontService frontService;
    @Autowired
    private FrontGroupMapCache frontGroupMapCache;
    @Autowired
    private FrontGroupMapService frontGroupMapService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private MethodService methodService;
    @Autowired
    private ConstantProperties constants;

    /**
     * save group
     */
    @Transactional
    public void saveGroup(int chainId, int groupId, int nodeCount, String genesisBlockHash) {
        // save group
        String groupName = "group" + groupId;
        TbGroup tbGroup = new TbGroup(groupId, chainId, groupName, nodeCount, genesisBlockHash);
        groupMapper.save(tbGroup);
        // create table by group id
        tableService.newSubTable(chainId, groupId);
    }

    /**
     * query count of group.
     */
    public int countOfGroup(Integer chainId, Integer groupId, Integer groupStatus)
            throws BaseException {
        try {
            return groupMapper.getCount(chainId, groupId, groupStatus);
        } catch (RuntimeException ex) {
            log.error("fail countOfGroup", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query all group info.
     */
    public List<TbGroup> getGroupList(Integer chainId, Integer groupStatus) throws BaseException {
        try {
            List<TbGroup> groupList = groupMapper.getList(chainId, null, groupStatus);
            return groupList;
        } catch (RuntimeException ex) {
            log.error("fail getGroupList", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * update status.
     */
    public void updateGroupStatus(Integer chainId, Integer groupId, Integer groupStatus) {
        groupMapper.updateStatus(chainId, groupId, groupStatus);
    }

    /**
     * Check the validity of the groupId.
     */
    public void checkGroupId(Integer chainId, Integer groupId) throws BaseException {
        Integer groupCount = countOfGroup(chainId, groupId, null);
        if (groupCount == null || groupCount == 0) {
            throw new BaseException(ConstantCode.INVALID_GROUP_ID);
        }
    }

    /**
     * query group overview information.
     */
    public GroupGeneral queryGroupGeneral(Integer chainId, Integer groupId) throws BaseException {
        GroupGeneral generalInfo = groupMapper.getGeneral(chainId, groupId);
        if (generalInfo != null) {
            TotalTransCountInfo transCountInfo =
                    frontInterface.getTotalTransactionCount(chainId, groupId);
            generalInfo.setLatestBlock(transCountInfo.getBlockNumber());
            generalInfo.setTransactionCount(transCountInfo.getTxSum());
        }
        return generalInfo;
    }


    /**
     * reset groupList.
     */
    @Transactional
    public void resetGroupList() {
        Instant startTime = Instant.now();
        log.info("start resetGroupList.");

        List<TbChain> chainList = chainService.getChainList(null);
        if (chainList == null || chainList.size() == 0) {
            log.info("not fount any chain.");
            return;
        }

        for (TbChain tbChain : chainList) {
            Integer chainId = tbChain.getChainId();
            // all groupId from chain
            Set<Integer> allGroupSet = new HashSet<>();

            // get all front
            List<TbFront> frontList = frontService.getFrontList(new FrontParam());
            if (frontList == null || frontList.size() == 0) {
                log.info("not fount any front.");
                return;
            }
            // get group from chain
            for (TbFront front : frontList) {
                String frontIp = front.getFrontIp();
                int frontPort = front.getFrontPort();
                // query group list
                List<String> groupIdList;
                try {
                    groupIdList = frontInterface.getGroupListFromSpecificFront(frontIp, frontPort);
                } catch (Exception ex) {
                    log.error("fail getGroupListFromSpecificFront.");
                    continue;
                }
                for (String groupId : groupIdList) {
                    Integer gId = Integer.valueOf(groupId);
                    allGroupSet.add(gId);
                    // peer in group
                    List<String> groupPeerList =
                            frontInterface.getGroupPeersFromSpecificFront(frontIp, frontPort, gId);
                    String genesisBlockHash =
                            frontInterface.getBlockByNumberFromSpecificFront(frontIp, frontPort,
                                    gId, BigInteger.ZERO).getHash();
                    // add group
                    saveGroup(chainId, gId, groupPeerList.size(), genesisBlockHash);
                    // save map
                    frontGroupMapService.newFrontGroup(chainId, front.getFrontId(), gId);
                    // save new peers
                    savePeerList(chainId, frontIp, frontPort, gId, groupPeerList);
                    // remove invalid peers
                    removeInvalidPeer(chainId, gId, groupPeerList);
                    // refresh: add sealer and observer no matter validity
                    frontService.refreshSealerAndObserverInNodeList(frontIp, frontPort,
                            front.getChainId(), gId);
                }
            }

            // check group status
            checkGroupStatusAndRemoveInvalidGroup(chainId, allGroupSet);
            // remove invalid group
            frontGroupMapService.removeInvalidFrontGroupMap();
            // clear cache
            frontGroupMapCache.clearMapList(chainId);
        }
        log.info("end resetGroupList. useTime:{} ",
                Duration.between(startTime, Instant.now()).toMillis());
    }

    /**
     * remove by chainId.
     */
    public void removeByChainId(int chainId) {
        if (chainId == 0) {
            return;
        }
        // remove chainId.
        groupMapper.remove(chainId, null);
    }

    /**
     * check group status.
     */
    private void checkGroupStatusAndRemoveInvalidGroup(Integer chainId,
            Set<Integer> allGroupOnChain) {
        if (CollectionUtils.isEmpty(allGroupOnChain)) {
            return;
        }

        List<TbGroup> allLocalGroup = getGroupList(chainId, null);
        if (CollectionUtils.isEmpty(allLocalGroup)) {
            return;
        }

        for (TbGroup localGroup : allLocalGroup) {
            int localGroupId = localGroup.getGroupId();
            long count = allGroupOnChain.stream().filter(id -> id == localGroupId).count();
            try {
                if (count > 0) {
                    log.info("group is valid, localGroupId:{}", localGroupId);
                    // update NORMAL
                    updateGroupStatus(chainId, localGroupId, DataStatus.NORMAL.getValue());
                    continue;
                }

                if (!CommonTools.isDateTimeInValid(localGroup.getModifyTime(),
                        constants.getGroupInvalidGrayscaleValue())) {
                    log.warn("remove group, localGroup:{}", localGroup.getGroupId());
                    // remove group
                    removeByGroupId(chainId, localGroupId);
                    continue;
                }

                log.warn("group is invalid, localGroupId:{}", localGroupId);
                if (DataStatus.NORMAL.getValue() == localGroup.getGroupStatus()) {
                    // update invalid
                    updateGroupStatus(chainId, localGroupId, DataStatus.INVALID.getValue());
                    continue;
                }
            } catch (Exception ex) {
                log.info("fail check group. localGroup:{}", localGroup.getGroupId());
                continue;
            }

        }
    }

    /**
     * remove by groupId.
     */
    private void removeByGroupId(int chainId, int groupId) {
        if (chainId == 0 || groupId == 0) {
            return;
        }
        // remove groupId.
        groupMapper.remove(chainId, groupId);
        // remove mapping.
        frontGroupMapService.removeByGroupId(chainId, groupId);
        // remove node
        nodeService.deleteByGroupId(chainId, groupId);
        // remove contract
        contractService.deleteByGroupId(chainId, groupId);
        // remove method
        methodService.removeByChainIdAndGroupId(chainId, groupId);
        // drop table.
        tableService.dropTable(chainId, groupId);
    }

    /**
     * save new peers.
     */
    private void savePeerList(int chainId, String frontIp, Integer frontPort, int groupId,
            List<String> groupPeerList) {
        // get all local nodes
        List<TbNode> localNodeList = nodeService.queryByGroupId(chainId, groupId);
        // get peers on chain
        PeerInfo[] peerArr = frontInterface.getPeersFromSpecificFront(frontIp, frontPort, groupId);
        List<PeerInfo> peerList = Arrays.asList(peerArr);
        // save new nodes
        for (String nodeId : groupPeerList) {
            long count = localNodeList.stream()
                    .filter(ln -> groupId == ln.getGroupId() && nodeId.equals(ln.getNodeId()))
                    .count();
            if (count == 0) {
                PeerInfo newPeer = peerList.stream().filter(peer -> nodeId.equals(peer.getNodeId()))
                        .findFirst().orElseGet(() -> new PeerInfo(nodeId));
                nodeService.addNodeInfo(chainId, groupId, newPeer);
            }
        }
    }

    /**
     * remove invalid peer.
     */
    private void removeInvalidPeer(int chainId, int groupId, List<String> groupPeerList) {
        if (groupId == 0) {
            return;
        }
        // get local peers
        List<TbNode> localNodes = nodeService.queryByGroupId(chainId, groupId);
        if (CollectionUtils.isEmpty(localNodes)) {
            return;
        }
        // remove node that's not in groupPeerList and not in sealer/observer list
        localNodes.stream()
                .filter(node -> !groupPeerList.contains(node.getNodeId())
                        && !checkSealerAndObserverListContains(chainId, groupId, node.getNodeId()))
                .forEach(n -> nodeService.deleteByNodeAndGroupId(n.getNodeId(), groupId));
    }

    private boolean checkSealerAndObserverListContains(int chainId, int groupId, String nodeId) {
        log.debug("checkSealerAndObserverListNotContains nodeId:{},groupId:{}", nodeId, groupId);
        // get sealer and observer on chain
        List<PeerInfo> sealerAndObserverList =
                nodeService.getSealerAndObserverList(chainId, groupId);
        for (PeerInfo peerInfo : sealerAndObserverList) {
            if (nodeId.equals(peerInfo.getNodeId())) {
                return true;
            }
        }
        return false;
    }
}
