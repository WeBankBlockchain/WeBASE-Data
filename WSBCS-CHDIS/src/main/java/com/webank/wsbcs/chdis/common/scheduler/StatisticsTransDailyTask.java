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

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.webank.wsbcs.chdis.base.aspect.RedisAspectBase;
import com.webank.wsbcs.chdis.base.entity.data.BlockListParam;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.BizSeqNoUtil;
import com.webank.wsbcs.chdis.base.util.CommonUtils;
import com.webank.wsbcs.chdis.common.config.ConfigService;
import com.webank.wsbcs.chdis.common.dao.entity.TbBlock;
import com.webank.wsbcs.chdis.common.dao.entity.TbTransDaily;
import com.webank.wsbcs.chdis.common.redis.RedisService;
import com.webank.wsbcs.chdis.common.transdaily.TransDailyService;
import com.webank.wsbcs.chdis.v3.data.BlockServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * Timed tasks for counting daily transaction data. using in web's charts 2.0和3.0分别执行
 */
@Log4j2
@Component
public class StatisticsTransDailyTask extends RedisAspectBase {

    @Autowired
    private BlockServiceV3 blockServiceV3;
    @Autowired
    private TransDailyService transDailyService;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private RedisService redisService;

    private static final String KEY_UPDATETRANSDAILYDATA = "updateTransDailyData";

    @Scheduled(cron = "${constant.statisticsTransDailyCron}")
    public void taskStart() {
        String bizSeqNo = BizSeqNoUtil.getBizSeqNo();
        // 是否在重置区块
        String resetBlockNumber =
                redisService.getString(ConfigService.KEY_RESETBLOCKNUMBER, bizSeqNo);
        if (StringUtils.isNotBlank(resetBlockNumber)) {
            log.info("pullBlockTaskV3. being resetBlockNumber. bizSeqNo:{}", bizSeqNo);
            return;
        }
        // 分布式锁
        boolean isGetLock = super.checkLock(KEY_UPDATETRANSDAILYDATA,
                ConstantProperties.TASK_COMMON_TIMEOUT, bizSeqNo);
        if (!isGetLock) {
            log.info("updateTransDailyData. not get lock. bizSeqNo:{}", bizSeqNo);
            return;
        }
        updateTransDailyData(bizSeqNo);

        // 释放锁
        super.releaseLock(KEY_UPDATETRANSDAILYDATA, bizSeqNo);
    }

    /**
     * counting daily transaction data.
     */
    public synchronized void updateTransDailyData(String bizSeqNo) {
        Instant startTime = Instant.now();
        log.info("start updateTransDailyData startTime:{} bizSeqNo:{}", startTime.toEpochMilli(),
                bizSeqNo);
        try {
            String chainId = cProperties.getDefaultChainId();
            TbTransDaily statisticalInfo = transDailyService.selectLatestStatisticalTrans();
            LocalDate latestSaveDay = statisticalInfo == null ? null
                    : CommonUtils.convert2LocalDate(statisticalInfo.getTransDay());
            // 通过块高确保多个线程，多个实例都只会保存连续块高的统计数据。（锁）
            long latestSaveBlockNumber =
                    statisticalInfo == null ? 0L : statisticalInfo.getBlockNumber();
            int netTransCount = statisticalInfo == null ? 0 : statisticalInfo.getTransCount();
            BigInteger maxBlockNumber = BigInteger.valueOf(latestSaveBlockNumber);

            // query block list
            BlockListParam queryParam = new BlockListParam(maxBlockNumber);
            queryParam.setStart(0);
            queryParam.setPageSize(cProperties.getFetchTransCount());

            List<TbBlock> blockList =
                    blockServiceV3.queryBlockList(chainId, null, queryParam, bizSeqNo);

            // Traversing block list
            if (blockList == null || blockList.size() == 0) {
                log.info(
                        "updateTransDailyData jump over .This chain [{}] did not find new block. bizSeqNo:{}",
                        chainId, bizSeqNo);
                return;
            }
            log.info("updateTransDailyData blockList size:{}|{}|{}", blockList.size(), chainId,
                    bizSeqNo);

            for (int i = 0; i < blockList.size(); i++) {
                TbBlock tbBlock = blockList.get(i);
                LocalDate blockDate = tbBlock.getBlockTimestamp() == null ? null
                        : CommonUtils.convert2LocalDate(tbBlock.getBlockTimestamp());
                if (blockDate == null) {
                    log.info("updateTransDailyData jump over. blockDate is null. bizSeqNo:{}",
                            bizSeqNo);
                    continue;
                }

                if (blockDate.equals(latestSaveDay)) {
                    // 统计list中所有同一天的block
                    netTransCount = netTransCount + tbBlock.getTransCount().intValue();
                    maxBlockNumber = BigInteger.valueOf(tbBlock.getBlockNumber());
                } else { // 新增一条跨天区块的新记录，交易量为该区块的交易数
                    // select出来的数据跨了一天，保存旧的，add一条新的
                    if (netTransCount > 0 && latestSaveDay != null) {
                        transDailyService.saveTbTransDailyInfo(chainId, null, latestSaveDay,
                                netTransCount, maxBlockNumber.longValue());
                    }
                    // 新一天的统计，新增一条
                    transDailyService.saveTbTransDailyInfo(chainId, null, blockDate,
                            tbBlock.getTransCount().intValue(), tbBlock.getBlockNumber());
                    // 新一天的日交易量重新开始计数
                    latestSaveDay = blockDate;
                    netTransCount = tbBlock.getTransCount().intValue();
                    maxBlockNumber = BigInteger.valueOf(tbBlock.getBlockNumber());
                }

                // 当前select的区块列表遍历结束了，保存
                if (i == (blockList.size() - 1)) {
                    transDailyService.saveTbTransDailyInfo(chainId, null, latestSaveDay,
                            netTransCount, maxBlockNumber.longValue());
                }
            }
        } catch (Exception ex) {
            log.error("fail updateTransDailyData bizSeqNo:{}", bizSeqNo, ex);
        }
        log.info("end updateTransDailyData useTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
    }
}
