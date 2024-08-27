package com.webank.wsbcs.chdis.base.aspect;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.AspectProperties;
import com.webank.wsbcs.chdis.common.redis.RedisService;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Log4j2
public class RedisAspectBase {

    private final static String PROG_TAG = UUID.randomUUID().toString(); // 本进程的唯一标识
    private final static String LOCK_SUCCESS = "OK";
    private final static Long RELEASE_SUCCESS = 1L;
    private final static String RELEASE_LOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private RedisService redisService;
    @Autowired
    private AspectProperties aspectProperties;
    @Autowired
    @Qualifier(value = "lockExecutorService")
    private ExecutorService executorService;

    /**
     * 查询Redis锁
     * 
     * @param lockKey
     * @param millisecondsToExpire
     * @return
     */
    protected boolean checkLock(String lockKey, long millisecondsToExpire, String bizSeqNo) {
        return this.tryGetDistributedLock(lockKey, millisecondsToExpire, bizSeqNo);
    }

    /**
     * 获取Redis锁
     * 
     * @param lockKey
     * @param millisecondsToExpire
     */
    protected void waitLock(String lockKey, long millisecondsToExpire, String bizSeqNo) {
        boolean isGetLock = this.tryGetDistributedLock(lockKey, millisecondsToExpire, bizSeqNo);
        if (!isGetLock) {
            this.lockSelfUntilGetDistributedLock(lockKey, millisecondsToExpire, bizSeqNo);
        }
    }

    /**
     * 释放Redis锁
     * 
     * @param lockKey
     */
    protected void releaseLock(String lockKey, String bizSeqNo) {
        this.releaseDistributedLock(lockKey, bizSeqNo);
    }
    /** redis start **/

    // 尝试获取分布式锁
    private boolean tryGetDistributedLock(final String lockKey, long millisecondsToExpire,
            String bizSeqNo) {
        log.debug("[Redis]try get dis lockKey:{} bizSeqNo:{} process:{}", lockKey, bizSeqNo, PROG_TAG);
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            jedis = redisService.selectJedis();
            String result =
                    jedis.set(lockKey, PROG_TAG, new SetParams().nx().px(millisecondsToExpire));
            log.debug("[Redis]end get dis lockKey:{} bizSeqNo:{} useTime:{} result:{}", lockKey, bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), result);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("OnError:tryGetDistributedLock. bizSeqNo:{}", bizSeqNo, e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    // 释放分布式锁
    private boolean releaseDistributedLock(String lockKey, String bizSeqNo) {
        log.debug("[Redis]release dis lockKey:{} bizSeqNo:{} process:{}", lockKey, bizSeqNo, PROG_TAG);
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            jedis = redisService.selectJedis();
            Object result = jedis.eval(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey),
                    Collections.singletonList(PROG_TAG));
            if (RELEASE_SUCCESS.equals(result)) {
                log.debug("[Redis]release dis lock success. lockKey:{} bizSeqNo:{} useTime:{} result:{}", lockKey, bizSeqNo,
                        Duration.between(startTime, Instant.now()).toMillis(), result);
                return true;
            }
            // 锁失效
            log.debug("[Redis]dis lock disable. lockKey:{} bizSeqNo:{} useTime:{} result:{}", lockKey, bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), result);
            return false;
        } catch (Exception e) {
            log.error("OnError:releaseDistributedLock. bizSeqNo:{}", bizSeqNo, e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    // 自锁，直到获取分布式锁
    private void lockSelfUntilGetDistributedLock(final String lockKey, long millisecondsToExpire,
            String bizSeqNo) {
        Object selfLookObj = new Object();// 自锁对象
        tryUnLock(lockKey, millisecondsToExpire, selfLookObj, bizSeqNo);
        synchronized (selfLookObj) {
            try {
                log.info("[Redis]get dis lock failed, self lock Key:{} bizSeqNo:{} hashCode:{}", lockKey,
                        bizSeqNo, selfLookObj.hashCode());
                selfLookObj.wait(); // NOSONAR
            } catch (InterruptedException e) {
                log.error(
                        "[Redis]OnError:lockSelfUntilGetDistributedLock. wait failed lockKey:{} bizSeqNo:{} message:{}",
                        lockKey, bizSeqNo, e.getMessage());
            }
        }
    }

    // 解锁线程
    private void tryUnLock(String lockKey, long millisecondsToExpire, Object selfLookObj,
            String bizSeqNo) {
        executorService.submit(() -> {
            synchronized (selfLookObj) {// NOSONAR
                while (true) {
                    try {
                        Thread.sleep(aspectProperties.getRequestLockInterval());// NOSONAR
                    } catch (InterruptedException e) {
                        log.error("[Redis]OnError:sleep failed lockKey:{} bizSeqNo:{} message:{}", lockKey,
                                bizSeqNo, e.getMessage());
                    }
                    boolean isGetLock =
                            tryGetDistributedLock(lockKey, millisecondsToExpire, bizSeqNo);
                    if (isGetLock) {
                        log.info("[Redis]got dis lock, self unlock Key:{} bizSeqNo:{} hashCode:{}",
                                lockKey, bizSeqNo, selfLookObj.hashCode());
                        selfLookObj.notify();// NOSONAR
                        break;
                    }
                    log.info("[Redis]get dis lock failed, keep self lockKey:{} bizSeqNo:{} hashCode:{}",
                            lockKey, bizSeqNo, selfLookObj.hashCode());
                }
            }
        });
    }

    /** redis end **/

}
