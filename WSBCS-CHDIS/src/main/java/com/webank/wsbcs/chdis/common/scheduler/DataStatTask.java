package com.webank.wsbcs.chdis.common.scheduler;

import java.time.Duration;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.webank.wsbcs.chdis.base.aspect.RedisAspectBase;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.BizSeqNoUtil;
import com.webank.wsbcs.chdis.common.config.ConfigService;
import com.webank.wsbcs.chdis.common.data.DataService;
import com.webank.wsbcs.chdis.common.redis.RedisService;
import lombok.extern.log4j.Log4j2;

/**
 * 统计数据量
 */
@Log4j2
@Component
public class DataStatTask extends RedisAspectBase {

    @Autowired
    private DataService dataService;
    @Autowired
    private RedisService redisService;

    private static final String KEY_DATASTATTASK = "dataStatTask";
    private static final String KEY_TRANSFERSTATTASK = "transferStatTask";

    @Scheduled(cron = "${constant.syncStatTaskCron}")
    public void dataStatTask() {
        Instant startTime = Instant.now();
        String bizSeqNo = BizSeqNoUtil.getBizSeqNo();
        // 是否在重置区块
        String resetBlockNumber =
                redisService.getString(ConfigService.KEY_RESETBLOCKNUMBER, bizSeqNo);
        if (StringUtils.isNotBlank(resetBlockNumber)) {
            log.info("pullBlockTaskV3. being resetBlockNumber. bizSeqNo:{}", bizSeqNo);
            return;
        }
        // 分布式锁
        boolean isGetLock =
                super.checkLock(KEY_DATASTATTASK, ConstantProperties.TASK_COMMON_TIMEOUT, bizSeqNo);
        if (!isGetLock) {
            log.info("dataStatTask. not get lock. bizSeqNo:{}", bizSeqNo);
            return;
        }
        log.info("start dataStatTask. bizSeqNo:{}", bizSeqNo);
        dataService.dataStat(bizSeqNo);

        // 释放锁
        super.releaseLock(KEY_DATASTATTASK, bizSeqNo);

        log.info("end dataStatTask. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
    }

    @Scheduled(cron = "${constant.syncStatTaskCron}")
    public void transferStatTask() {
        Instant startTime = Instant.now();
        String bizSeqNo = BizSeqNoUtil.getBizSeqNo();
        // 是否在重置区块
        String resetBlockNumber =
                redisService.getString(ConfigService.KEY_RESETBLOCKNUMBER, bizSeqNo);
        if (StringUtils.isNotBlank(resetBlockNumber)) {
            log.info("pullBlockTaskV3. being resetBlockNumber. bizSeqNo:{}", bizSeqNo);
            return;
        }
        // 分布式锁
        boolean isGetLock = super.checkLock(KEY_TRANSFERSTATTASK,
                ConstantProperties.TASK_COMMON_TIMEOUT, bizSeqNo);
        if (!isGetLock) {
            log.info("transferStatTask. not get lock. bizSeqNo:{}", bizSeqNo);
            return;
        }
        log.info("start transferStatTask. bizSeqNo:{}", bizSeqNo);
        dataService.transferStat(bizSeqNo);

        // 释放锁
        super.releaseLock(KEY_TRANSFERSTATTASK, bizSeqNo);

        log.info("end transferStatTask. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
    }
}
