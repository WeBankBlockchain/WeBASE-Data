/**
 * Copyright 2014-2021 the original author or authors.
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
package com.webank.wsbcs.chdis.common.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.webank.wsbcs.chdis.base.aspect.RedisAspectBase;
import com.webank.wsbcs.chdis.base.entity.data.BlockListParam;
import com.webank.wsbcs.chdis.base.entity.data.TransListParam;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.BizSeqNoUtil;
import com.webank.wsbcs.chdis.common.config.ConfigService;
import com.webank.wsbcs.chdis.common.redis.RedisService;
import com.webank.wsbcs.chdis.v3.data.BlockServiceV3;
import com.webank.wsbcs.chdis.v3.data.TransDataServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * delete block/trans/monitorTrans data task related: yml-constant-transRetainMax 兼容2.0与3.0
 */
@Log4j2
@Component
@ConditionalOnProperty(name = "constant.isDeleteInfo", havingValue = "true")
public class DeleteInfoTask extends RedisAspectBase {

    @Autowired
    private BlockServiceV3 blockServiceV3;
    @Autowired
    private TransDataServiceV3 transDataServiceV3;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private RedisService redisService;

    private static final String KEY_DELETEINFOTASK = "deleteInfoTask";

    @Scheduled(cron = "${constant.deleteInfoCron}")
    public void deleteInfoTask() {
        String bizSeqNo = BizSeqNoUtil.getBizSeqNo();
        // 是否在重置区块
        String resetBlockNumber =
                redisService.getString(ConfigService.KEY_RESETBLOCKNUMBER, bizSeqNo);
        if (StringUtils.isNotBlank(resetBlockNumber)) {
            log.info("pullBlockTaskV3. being resetBlockNumber. bizSeqNo:{}", bizSeqNo);
            return;
        }
        // 分布式锁
        boolean isGetLock = super.checkLock(KEY_DELETEINFOTASK,
                ConstantProperties.TASK_COMMON_TIMEOUT, bizSeqNo);
        if (!isGetLock) {
            log.info("deleteInfoTask. not get lock. bizSeqNo:{}", bizSeqNo);
            return;
        }

        deleteInfoStart();

        // 释放锁
        super.releaseLock(KEY_DELETEINFOTASK, bizSeqNo);
    }

    /**
     * start.
     */
    public void deleteInfoStart() {
        Instant startTime = Instant.now();
        log.info("start deleteInfoStart.");

        CountDownLatch latch = new CountDownLatch(1);
        String chainId = cProperties.getDefaultChainId();
        deleteByGroupId(latch, chainId, chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1]);

        try {
            latch.await(cProperties.getDeleteInfoTimeout(), TimeUnit.MILLISECONDS);// NOSONAR
        } catch (InterruptedException ex) {
            log.error("deleteInfo InterruptedException", ex);
            Thread.currentThread().interrupt();
        }

        log.info("end deleteInfoStart useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
    }

    /**
     * delete by groupId.
     */
    @Async(value = "asyncExecutor")
    private void deleteByGroupId(CountDownLatch latch, String chainId, String groupId) {
        // delete block
        deleteBlock(chainId, groupId);
        // delete transData
        deleteTransData(chainId, groupId);
        latch.countDown();
    }


    /**
     * delete block.
     */
    private void deleteBlock(String chainId, String groupId) {
        Instant startTime = Instant.now();
        log.info("start deleteBlock. groupId:{}|{}", groupId, chainId);
        try {
            BlockListParam queryParam = new BlockListParam();
            Integer count =
                    blockServiceV3.queryCountOfBlock(chainId, groupId, queryParam, "deleteBlock");
            int removeCount = 0;
            if (count > cProperties.getBlockRetainMax().intValue()) {
                int subBlockNum = count - cProperties.getBlockRetainMax().intValue();
                removeCount = blockServiceV3.removeNew(chainId, groupId, subBlockNum);
            }
            log.info("end deleteBlock. groupId:{}|{} subNum:{}, usedTime:{}", groupId, chainId,
                    removeCount, Duration.between(startTime, Instant.now()).toMillis());
        } catch (Exception ex) {
            log.error("fail deleteBlock. groupId:{}|{}", groupId, chainId, ex);
        }
    }

    /**
     * delete transHash.
     */
    private void deleteTransData(String chainId, String groupId) {
        Instant startTime = Instant.now();
        log.info("start deleteTransData. groupId:{}|{}", groupId, chainId);
        try {
            TransListParam queryParam = new TransListParam();
            Integer count = transDataServiceV3.queryCountOfTran(chainId, groupId, queryParam,
                    "deleteTransData");
            int subNum = 0;
            if (count > cProperties.getTransRetainMax().intValue()) {
                subNum = count - cProperties.getTransRetainMax().intValue();
                transDataServiceV3.removeNew(chainId, groupId, subNum);
            }
            log.info("end deleteTransData. groupId:{}|{} subNum:{}, usedTime:{}", groupId, chainId,
                    subNum, Duration.between(startTime, Instant.now()).toMillis());
        } catch (Exception ex) {
            log.error("fail deleteTransData. groupId:{}|{}", groupId, chainId, ex);
        }
    }
}
