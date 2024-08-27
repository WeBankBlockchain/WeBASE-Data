/**
 * Copyright 2014-2021 the original author or authors.
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
package com.webank.wsbcs.chdis.common.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.webank.wsbcs.chdis.base.aspect.RedisAspectBase;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.BizSeqNoUtil;
import com.webank.wsbcs.chdis.common.config.ConfigService;
import com.webank.wsbcs.chdis.common.redis.RedisService;
import com.webank.wsbcs.chdis.v3.data.BlockServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * get block info and trans info from chain. including tb_block and tb_trans (block contains trans)
 * 分别支持v2 v3的链
 */
@Log4j2
@Component
public class PullBlockTask extends RedisAspectBase {

    @Autowired
    private BlockServiceV3 blockServiceV3;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private RedisService redisService;

    private static final String KEY_PULLBLOCKTASKV3 = "pullBlockTaskV3";

    @Scheduled(cron = "${constant.pullBlockTaskCron}")
    public void pullBlockTaskV3() {
        String bizSeqNo = BizSeqNoUtil.getBizSeqNo();
        // 是否在重置区块
        String resetBlockNumber =
                redisService.getString(ConfigService.KEY_RESETBLOCKNUMBER, bizSeqNo);
        if (StringUtils.isNotBlank(resetBlockNumber)) {
            log.info("pullBlockTaskV3. being resetBlockNumber. bizSeqNo:{}", bizSeqNo);
            return;
        }
        // 分布式锁
        boolean isGetLock = super.checkLock(KEY_PULLBLOCKTASKV3,
                cProperties.getPullBlockTimeout() + 5000L, bizSeqNo);
        if (!isGetLock) {
            log.info("pullBlockTaskV3. not get lock. bizSeqNo:{}", bizSeqNo);
            return;
        }
        pullBlockStartV3(bizSeqNo);
    }

    public void pullBlockStartV3(String bizSeqNo) {
        Instant startTime = Instant.now();
        log.info("start pullBlockStartV3 {}", bizSeqNo);

        CountDownLatch latch = new CountDownLatch(1);
        String chainId = cProperties.getDefaultChainId();
        blockServiceV3.pullBlockByChainIdV3(latch, chainId,
                chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], bizSeqNo);

        try {
            latch.await(cProperties.getPullBlockTimeout(), TimeUnit.MILLISECONDS);// NOSONAR
        } catch (InterruptedException ex) {
            log.error("pullBlockStartV3 InterruptedException bizSeqNo:{}", bizSeqNo, ex);
            Thread.currentThread().interrupt();
        }

        // 释放锁
        super.releaseLock(KEY_PULLBLOCKTASKV3, bizSeqNo);

        log.info("end pullBlockStartV3 useTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
    }
}
