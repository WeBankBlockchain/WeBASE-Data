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
package com.webank.webase.data.collect.gas;

import com.webank.webase.data.collect.base.enums.GasReconciliationType;
import com.webank.webase.data.collect.dao.entity.TbGas;
import com.webank.webase.data.collect.dao.entity.TbGasReconciliation;
import com.webank.webase.data.collect.dao.entity.TbGasReconciliationExample;
import com.webank.webase.data.collect.dao.mapper.TbGasReconciliationMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * service of gas.
 */
@Log4j2
@Service
public class GasReconciliationService {

    @Autowired
    private TbGasReconciliationMapper tbGasReconciliationMapper;
    @Autowired
    private GasService gasService;

    /**
     * save gas reconciliation info
     */
    public void saveGasReconciliation(TbGasReconciliation tbGasReconciliation) {
        tbGasReconciliationMapper.insertSelective(tbGasReconciliation);
    }

    /**
     * get gas reconciliation by user
     */
    public TbGasReconciliation selectReconciliationByUser(int chainId, int groupId,
            String userAddress) {
        TbGasReconciliation TbGasReconciliation =
                tbGasReconciliationMapper.selectByUser(chainId, groupId, userAddress);
        return TbGasReconciliation;
    }

    /**
     * get gas reconciliation count
     */
    public int getCount(TbGasReconciliationExample example) {
        Integer count = (int) tbGasReconciliationMapper.countByExample(example);
        return count == null ? 0 : count;
    }

    /**
     * get reconciliation list by TbGasReconciliationExample
     */
    public List<TbGasReconciliation> selectByExample(TbGasReconciliationExample example) {
        return tbGasReconciliationMapper.selectByExample(example);
    }

    /**
     * get reconciliation list by status except abnormal
     */
    public List<TbGasReconciliation> selectByStatusExceptAbnormal() {
        return tbGasReconciliationMapper.selectByStatusExceptAbnormal();
    }

    /**
     * delete by chainId
     */
    public void deleteByChainId(int chainId) {
        tbGasReconciliationMapper.deleteByChainId(chainId);
    }

    @Async("asyncExecutor")
    public void reconciliationProcess(CountDownLatch latch,
            TbGasReconciliation tbGasReconciliation) {
        int id = tbGasReconciliation.getId();
        int chainId = tbGasReconciliation.getChainId();
        int groupId = tbGasReconciliation.getGroupId();
        String userAddress = tbGasReconciliation.getUserAddress();
        log.debug("start reconciliationProcess. id:{} userAddress:{} ", id, userAddress);
        try {
            List<TbGas> gasList = gasService.getGasListForReconciliation(chainId, groupId,
                    userAddress, tbGasReconciliation.getBlockNumber());
            if (CollectionUtils.isEmpty(gasList) || gasList.size() == 1) {
                return;
            }
            for (int i = 0; i < gasList.size() - 1; i++) {
                boolean ifEqual =
                        gasList.get(i).getGasRemain().add(gasList.get(i + 1).getGasValue())
                                .compareTo(gasList.get(i + 1).getGasRemain()) == 0 ? true : false;
                if (!ifEqual) {
                    tbGasReconciliation.setBlockNumber(gasList.get(i + 1).getBlockNumber())
                            .setTransHash(gasList.get(i + 1).getTransHash())
                            .setReconciliationStatus(
                                    (byte) GasReconciliationType.abnormal.getType())
                            .setModifyTime(LocalDateTime.now());
                    tbGasReconciliationMapper.updateByPrimaryKeySelective(tbGasReconciliation);
                    return;
                }
            }
            tbGasReconciliation.setBlockNumber(gasList.get(gasList.size() - 1).getBlockNumber())
                    .setReconciliationStatus((byte) GasReconciliationType.normal.getType())
                    .setModifyTime(LocalDateTime.now());
            tbGasReconciliationMapper.updateByPrimaryKeySelective(tbGasReconciliation);
        } catch (Exception ex) {
            log.error("fail reconciliationProcess. id:{} ", id, ex);
        } finally {
            if (Objects.nonNull(latch)) {
                latch.countDown();
            }
        }
        log.debug("end reconciliationProcess. id:{} userAddress:{}", id, userAddress);
    }
}
