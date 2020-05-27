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
package com.webank.webase.data.collect.front;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.chain.ChainService;
import com.webank.webase.data.collect.chain.entity.TbChain;
import com.webank.webase.data.collect.front.entity.FrontInfo;
import com.webank.webase.data.collect.front.entity.FrontParam;
import com.webank.webase.data.collect.front.entity.TbFront;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapCache;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapService;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.frontinterface.entity.SyncStatus;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.scheduler.ResetGroupListTask;
import java.math.BigInteger;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service of web3.
 */
@Log4j2
@Service
public class FrontService {

    @Autowired
    private ChainService chainService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private FrontMapper frontMapper;
    @Autowired
    private FrontGroupMapService frontGroupMapService;
    @Autowired
    private FrontInterfaceService frontInterface;
    @Autowired
    private FrontGroupMapCache frontGroupMapCache;
    @Autowired
    private ResetGroupListTask resetGroupListTask;

    /**
     * add new front
     */
    @Transactional
    public TbFront newFront(FrontInfo frontInfo) {
        log.debug("start newFront frontInfo:{}", frontInfo);
        // check chainId
        Integer chainId = frontInfo.getChainId();
        TbChain tbChain = chainService.getChainById(chainId);
        if (tbChain == null) {
            throw new BaseException(ConstantCode.INVALID_CHAIN_ID);
        }
        String frontIp = frontInfo.getFrontIp();
        Integer frontPort = frontInfo.getFrontPort();
        // check front ip and port
        CommonTools.checkServerConnect(frontIp, frontPort);
        // query group list
        List<String> groupIdList = null;
        try {
            groupIdList = frontInterface.getGroupListFromSpecificFront(frontIp, frontPort);
        } catch (Exception e) {
            log.error("fail newFront, frontIp:{},frontPort:{}", frontIp, frontPort);
            throw new BaseException(ConstantCode.REQUEST_FRONT_FAIL);
        }
        // check front's encrypt type
        int encryptType = frontInterface.getEncryptTypeFromSpecificFront(frontIp, frontPort);
        if (encryptType != tbChain.getChainType()) {
            log.error(
                    "fail newFront, frontIp:{},frontPort:{},front's encryptType:{},"
                            + "local encryptType not match:{}",
                    frontIp, frontPort, encryptType, tbChain.getChainType());
            throw new BaseException(ConstantCode.ENCRYPT_TYPE_NOT_MATCH);
        }
        // check front not exist
        SyncStatus syncStatus = frontInterface.getSyncStatusFromSpecificFront(frontIp, frontPort,
                Integer.valueOf(groupIdList.get(0)));
        FrontParam param = new FrontParam();
        param.setChainId(chainId);
        param.setNodeId(syncStatus.getNodeId());
        int count = getFrontCount(param);
        if (count > 0) {
            throw new BaseException(ConstantCode.FRONT_EXISTS);
        }
        // copy attribute
        TbFront tbFront = new TbFront();
        BeanUtils.copyProperties(frontInfo, tbFront);
        tbFront.setNodeId(syncStatus.getNodeId());
        // save front info
        frontMapper.add(tbFront);
        if (tbFront.getFrontId() == null || tbFront.getFrontId() == 0) {
            log.warn("fail newFront, after save, tbFront:{}", tbFront);
            throw new BaseException(ConstantCode.SAVE_FRONT_FAIL);
        }
        for (String groupId : groupIdList) {
            Integer gId = Integer.valueOf(groupId);
            // peer in group
            List<String> groupPeerList =
                    frontInterface.getGroupPeersFromSpecificFront(frontIp, frontPort, gId);
            String genesisBlockHash = frontInterface
                    .getBlockByNumberFromSpecificFront(frontIp, frontPort, gId, BigInteger.ZERO)
                    .getHash();
            // add group
            groupService.saveGroup(chainId, gId, groupPeerList.size(), genesisBlockHash);
            // save front group map
            frontGroupMapService.newFrontGroup(chainId, tbFront.getFrontId(), gId);
        }
        // clear cache
        frontGroupMapCache.clearMapList(chainId);
        return tbFront;
    }

    /**
     * get front count
     */
    public int getFrontCount(FrontParam param) {
        Integer count = frontMapper.getCount(param);
        return count == null ? 0 : count;
    }

    /**
     * get front list
     */
    public List<TbFront> getFrontList(FrontParam param) {
        return frontMapper.getList(param);
    }

    /**
     * query front by frontId.
     */
    public TbFront getById(int frontId) {
        if (frontId == 0) {
            return null;
        }
        return frontMapper.getById(frontId);
    }
    
    /**
     * query front by nodeId.
     */
    public TbFront getByChainIdAndNodeId(Integer chainId, String nodeId) {
        if (chainId == null || nodeId == null) {
            return null;
        }
        return frontMapper.getByChainIdAndNodeId(chainId, nodeId);
    }

    /**
     * remove front by frontId
     */
    public void removeByFrontId(int frontId) {
        // check frontId
        TbFront tbFront = getById(frontId);
        if (tbFront == null) {
            throw new BaseException(ConstantCode.INVALID_FRONT_ID);
        }

        // remove front
        frontMapper.removeById(frontId);
        // remove map
        frontGroupMapService.removeByFrontId(frontId);
        // reset group list
        resetGroupListTask.asyncResetGroupList();
        // clear cache
        frontGroupMapCache.clearMapList(tbFront.getChainId());
    }
    
    /**
     * remove front by chainId
     */
    public void removeByChainId(int chainId) {
        if (chainId == 0) {
            return;
        }

        // remove front
        frontMapper.removeByChainId(chainId);
    }
}
