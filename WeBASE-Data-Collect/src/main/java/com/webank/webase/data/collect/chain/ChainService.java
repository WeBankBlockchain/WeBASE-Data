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
package com.webank.webase.data.collect.chain;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.EncryptType;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.chain.entity.ChainInfo;
import com.webank.webase.data.collect.chain.entity.ChainParam;
import com.webank.webase.data.collect.chain.entity.TbChain;
import com.webank.webase.data.collect.chain.entity.UpdateChainInfo;
import com.webank.webase.data.collect.contract.ContractService;
import com.webank.webase.data.collect.contract.MethodService;
import com.webank.webase.data.collect.front.FrontService;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapCache;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapService;
import com.webank.webase.data.collect.gas.GasReconciliationService;
import com.webank.webase.data.collect.gas.GasService;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.node.NodeService;
import com.webank.webase.data.collect.scheduler.ResetGroupListTask;
import com.webank.webase.data.collect.table.TableService;
import com.webank.webase.data.collect.txndaily.TxnDailyService;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * service of chain.
 */
@Log4j2
@Service
public class ChainService {

    @Autowired
    private ChainMapper chainMapper;
    @Autowired
    private GroupService groupService;
    @Autowired
    private FrontService frontService;
    @Autowired
    private TableService tableService;
    @Autowired
    private FrontGroupMapService frontGroupMapService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private MethodService methodService;
    @Autowired
    private TxnDailyService txnDailyService;
    @Autowired
    private FrontGroupMapCache frontGroupMapCache;
    @Autowired
    private GasService gasService;
    @Autowired
    private GasReconciliationService gasReconciliationService;
    @Autowired
    @Lazy
    private ResetGroupListTask resetGroupListTask;

    /**
     * add chain list
     */
    public void addChainList(List<TbChain> chainList) {
        if (CollectionUtils.isEmpty(chainList)) {
            log.info("contractList is empty.");
            return;
        }
        chainMapper.addList(chainList);
    }
    /**
     * add new chain
     */
    public TbChain newChain(ChainInfo chainInfo) {
        log.debug("start newChain chainInfo:{}", chainInfo);
        // check type
        if (!EncryptType.isInclude(chainInfo.getEncryptType())) {
            throw new BaseException(ConstantCode.INVALID_ENCRYPT_TYPE);
        }
        // check id
        if (checkChainId(chainInfo.getChainId())) {
            throw new BaseException(ConstantCode.CHAIN_ID_EXISTS);
        }
        // check name
        ChainParam param = new ChainParam();
        param.setChainName(chainInfo.getChainName());
        int nameCount = getChainCount(param);
        if (nameCount > 0) {
            throw new BaseException(ConstantCode.CHAIN_NAME_EXISTS);
        }
        // copy attribute
        TbChain tbChain = new TbChain();
        BeanUtils.copyProperties(chainInfo, tbChain);
        // save chain info
        int result = chainMapper.add(tbChain);
        if (result == 0) {
            log.warn("fail newChain after save.");
            throw new BaseException(ConstantCode.SAVE_CHAIN_FAIL);
        }
        return getChainById(chainInfo.getChainId());
    }
    
    /**
     * update chain info
     */
    public TbChain updateChain(UpdateChainInfo chainInfo) {
        log.debug("start updateChain chainInfo:{}", chainInfo);
        // check id
        if (!checkChainId(chainInfo.getChainId())) {
            throw new BaseException(ConstantCode.CHAIN_ID_NOT_EXISTS);
        }
        // copy attribute
        TbChain tbChain = new TbChain();
        BeanUtils.copyProperties(chainInfo, tbChain);
        // update chain info
        chainMapper.update(tbChain);
        return getChainById(chainInfo.getChainId());
    }

    /**
     * get chain count
     */
    public int getChainCount(ChainParam param) {
        Integer count = chainMapper.getCount(param);
        return count == null ? 0 : count;
    }

    /**
     * get chain list
     */
    public List<TbChain> getChainList(ChainParam param) {
        return chainMapper.getList(param);
    }

    /**
     * get chain info
     */
    public TbChain getChainById(Integer chainId) {
        return chainMapper.getChainById(chainId);
    }

    /**
     * remove chain
     */
    @Transactional
    public void removeChain(Integer chainId) {
        // check chainId
        ChainParam param = new ChainParam();
        param.setChainId(chainId);
        int count = getChainCount(param);
        if (count == 0) {
            throw new BaseException(ConstantCode.INVALID_CHAIN_ID);
        }

        List<TbGroup> groupList = groupService.getGroupList(chainId, null);
        // remove chain
        chainMapper.remove(chainId);
        // remove front
        frontService.removeByChainId(chainId);
        // remove group
        groupService.removeByChainId(chainId);
        // remove map
        frontGroupMapService.removeByChainId(chainId);
        // remove node
        nodeService.deleteByChainId(chainId);
        // remove contract
        contractService.deleteContractByChainId(chainId);
        // remove method
        methodService.removeByChainIdAndGroupId(chainId, null);
        //remove txnDaily
        txnDailyService.deleteByChainId(chainId);
        // clear cache
        frontGroupMapCache.clearMapList(chainId);
        // clear gas
        gasService.deleteByChainId(chainId);
        // clear gas user
        gasReconciliationService.deleteByChainId(chainId);
        // drop sub tables
        groupList.forEach(g -> tableService.dropTable(chainId, g.getGroupId()));
        // clear table cache
        TableService.CREATED_MAP.clear();
    }
    
    /**
     * check chain id
     */
    private boolean checkChainId(Integer chainId) {
        TbChain tbChainInfo = getChainById(chainId);
        if (tbChainInfo == null) {
            return false;
        }
        return true;
    }
}
