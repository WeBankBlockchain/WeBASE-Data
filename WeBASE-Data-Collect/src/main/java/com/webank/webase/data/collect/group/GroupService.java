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

import com.alibaba.fastjson.JSON;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.contract.ContractService;
import com.webank.webase.data.collect.front.FrontService;
import com.webank.webase.data.collect.front.entity.FrontParam;
import com.webank.webase.data.collect.front.entity.TbFront;
import com.webank.webase.data.collect.front.entity.TotalTransCountInfo;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapService;
import com.webank.webase.data.collect.frontgroupmap.entity.FrontGroupMapCache;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.group.entity.GroupGeneral;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.method.MethodService;
import com.webank.webase.data.collect.table.TableService;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
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
    private ContractService contractService;
    @Autowired
    private MethodService methodService;
    @Autowired
    private ConstantProperties constants;

    /**
     * save group
     */
    public void saveGroup(int groupId, int nodeCount, String genesisBlockHash) {
        if (groupId == 0) {
            return;
        }
        // save group id
        String groupName = "group" + groupId;
        TbGroup tbGroup = new TbGroup(groupId, groupName, nodeCount, genesisBlockHash);
        groupMapper.save(tbGroup);

        // create table by group id
        tableService.newTableByGroupId(groupId);
    }

    /**
     * query count of group.
     */
    public int countOfGroup(Integer groupId, Integer groupStatus) throws BaseException {
        try {
            return groupMapper.getCount(groupId, groupStatus);
        } catch (RuntimeException ex) {
            log.error("fail countOfGroup", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query all group info.
     */
    public List<TbGroup> getGroupList(Integer groupStatus) throws BaseException {
        try {
            List<TbGroup> groupList = groupMapper.getList(groupStatus);
            return groupList;
        } catch (RuntimeException ex) {
            log.error("fail getGroupList", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * update status.
     */
    public void updateGroupStatus(int groupId, int groupStatus) {
        groupMapper.updateStatus(groupId, groupStatus);
    }

    /**
     * Check the validity of the groupId.
     */
    public void checkGroupId(Integer groupId) throws BaseException {
        if (groupId == null) {
            log.error("fail checkGroupId groupId is null");
            throw new BaseException(ConstantCode.GROUP_ID_NULL);
        }
        Integer groupCount = countOfGroup(groupId, null);
        if (groupCount == null || groupCount == 0) {
            throw new BaseException(ConstantCode.INVALID_GROUP_ID);
        }
    }

    /**
     * query group overview information.
     */
    public GroupGeneral queryGroupGeneral(int groupId) throws BaseException {
        GroupGeneral generalInfo = groupMapper.getGeneral(groupId);
        if (generalInfo != null) {
            TotalTransCountInfo transCountInfo = frontInterface.getTotalTransactionCount(groupId);
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

        // all groupId from chain
        Set<Integer> allGroupSet = new HashSet<>();

        // get all front
        List<TbFront> frontList = frontService.getFrontList(new FrontParam());
        if (frontList == null || frontList.size() == 0) {
            log.info("not fount any front, start remove all group");
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
                log.error("fail getGroupListFromSpecificFront.", ex);
                continue;
            }
            for (String groupId : groupIdList) {
                Integer gId = Integer.valueOf(groupId);
                allGroupSet.add(gId);
                // peer in group
                List<String> groupPeerList =
                        frontInterface.getNodeIDListFromSpecificFront(frontIp, frontPort, gId);
                String genesisBlockHash = frontInterface
                        .getBlockByNumberFromSpecificFront(frontIp, frontPort, gId, BigInteger.ZERO)
                        .getHash();
                // add group
                saveGroup(gId, groupPeerList.size(), genesisBlockHash);
                frontGroupMapService.newFrontGroup(front.getFrontId(), gId);
            }
        }

        // check group status
        checkGroupStatusAndRemoveInvalidGroup(allGroupSet);
        // remove invalid group
        frontGroupMapService.removeInvalidFrontGroupMap();
        // clear cache
        frontGroupMapCache.clearMapList();

        log.info("end resetGroupList. useTime:{} ",
                Duration.between(startTime, Instant.now()).toMillis());
    }

    /**
     * check group status.
     */
    private void checkGroupStatusAndRemoveInvalidGroup(Set<Integer> allGroupOnChain) {
        if (CollectionUtils.isEmpty(allGroupOnChain)) {
            return;
        }

        List<TbGroup> allLocalGroup = getGroupList(null);
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
                    updateGroupStatus(localGroupId, DataStatus.NORMAL.getValue());
                    continue;
                }

                if (!CommonTools.isDateTimeInValid(localGroup.getModifyTime(),
                        constants.getGroupInvalidGrayscaleValue())) {
                    log.warn("remove group, localGroup:{}", JSON.toJSONString(localGroup));
                    // remove group
                    removeByGroupId(localGroupId);
                    continue;
                }

                log.warn("group is invalid, localGroupId:{}", localGroupId);
                if (DataStatus.NORMAL.getValue() == localGroup.getGroupStatus()) {
                    // update invalid
                    updateGroupStatus(localGroupId, DataStatus.INVALID.getValue());
                    continue;
                }
            } catch (Exception ex) {
                log.info("fail check group. localGroup:{}", JSON.toJSONString(localGroup));
                continue;
            }

        }
    }


    /**
     * remove by groupId.
     */
    private void removeByGroupId(int groupId) {
        if (groupId == 0) {
            return;
        }
        // remove groupId.
        groupMapper.remove(groupId);
        // remove mapping.
        frontGroupMapService.removeByGroupId(groupId);
        // remove contract
        contractService.deleteByGroupId(groupId);
        // remove method
        methodService.deleteByGroupId(groupId);
        // drop table.
        tableService.dropTableByGroupId(groupId);
    }
}
