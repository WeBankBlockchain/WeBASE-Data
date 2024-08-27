package com.webank.wsbcs.chdis.common.redis;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * request redis and save
 * 
 * @author marsli
 * @date 2021/12/21
 */
@Log4j2
@Service
public class RedisService {
    @Autowired(required = false)
    private JedisPool jedisPool;

    /**
     * get后必须手动调用jedis.close() @2022-01-17 通过初始化JedisPool选择db
     * 
     * @return
     */
    public Jedis selectJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    /**
     * setStringIfNotExist
     * 
     * @param key
     * @param value
     * @return
     */
    public int setStringIfNotExist(String key, String value, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]setStringIfNotExist key:{},value:{}", key, value);
            jedis = this.selectJedis();
            // 1-success, 0-already exist
            Long successOrNot = jedis.setnx(key, value);
            log.info("[Redis]setStringIfNotExist key:{},value:{},successOrNot:{},usedTime:{}", key,
                    value, successOrNot, Duration.between(startTime, Instant.now()).toMillis());
            return successOrNot.intValue();
        } catch (Exception e) {
            log.error("[Redis]OnError:setStringIfNotExist bizSeqNo:{},usedTime:{},error.", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1;
    }
    
    /**
     * setString
     * 
     * @param key
     * @param value
     * @return
     */
    public String setString(String key, String value, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]setString key:{},value:{}", key, value);
            jedis = this.selectJedis();
            // 1-success, 0-already exist
            String result = jedis.set(key, value);
            log.info("[Redis]setString key:{},value:{},result:{},usedTime:{}", key,
                    value, result, Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:setString bizSeqNo:{},usedTime:{},error.", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public String getString(String key, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            jedis = this.selectJedis();
            String value = jedis.get(key);
            log.info("[Redis]getString {},value:{},bizSeqNo:{},usedTime:{}", key, value, bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
            return value;
        } catch (Exception e) {
            log.error("[Redis]OnError:getString  key:{},bizSeqNo:{},usedTime:{}.", key, bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long delString(String... key) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            jedis = this.selectJedis();
            Long value = jedis.del(key);
            log.info("[Redis]del {},status:{} :{}", key, value,
                    Duration.between(startTime, Instant.now()).toMillis());
            return value;
        } catch (Exception e) {
            log.error("[Redis]OnError:delString. key:{} {}", key,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 计数
     * 
     * @param key
     * @param bizSeqNo
     * @return
     */
    public void setIncr(String key, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]setIncr start. key:{} bizSeqNo:{}", key, bizSeqNo);
            jedis = this.selectJedis();
            Long successOrNot = jedis.incr(key);
            log.info("[Redis]setIncr end. key:{},bizSeqNo:{} successOrNot:{} usedTime:{}", key,
                    bizSeqNo, successOrNot, Duration.between(startTime, Instant.now()).toMillis());
        } catch (Exception e) {
            log.error("[Redis]OnError:setIncr bizSeqNo:{} usedTime:{} error.", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 累加
     * 
     * @param key
     * @param bizSeqNo
     * @return
     */
    public void setIncrby(String key, long increment, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]setIncrby start. key:{} increment:{} bizSeqNo:{}", key, increment,
                    bizSeqNo);
            jedis = this.selectJedis();
            Long successOrNot = jedis.incrBy(key, increment);
            log.info("[Redis]setIncrby end. key:{},bizSeqNo:{} successOrNot:{} usedTime:{}", key,
                    bizSeqNo, successOrNot, Duration.between(startTime, Instant.now()).toMillis());
        } catch (Exception e) {
            log.error("[Redis]OnError:setIncrby bizSeqNo:{} usedTime:{} error.", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置key超时时间
     * 
     * @param key
     * @param seconds 超时时间（秒）
     * @param bizSeqNo
     * @return
     */
    public void setExpire(String key, long seconds, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]setExpire start. key:{} bizSeqNo:{}", key, bizSeqNo);
            jedis = this.selectJedis();
            Long successOrNot = jedis.expire(key, seconds);
            log.info("[Redis]setExpire end. key:{} bizSeqNo:{} successOrNot:{} usedTime:{}", key,
                    bizSeqNo, successOrNot, Duration.between(startTime, Instant.now()).toMillis());
        } catch (Exception e) {
            log.error("[Redis]OnError:setExpire bizSeqNo:{} usedTime:{} error.", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 向Map添加成员，返回OK
     * 
     * @param key
     * @param hash
     * @return Long
     */
    public String hmset(String key, Map<String, String> hash, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.hmset(key, hash);
            log.info("[Redis]hmset {},bizSeqNo:{},result:{},usedTime:{}", key, bizSeqNo, result,
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:hmset bizSeqNo:{},usedTime:{}.",
                    Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo, e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获得map集合
     * 
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key, String bizSeqNo) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            jedis = this.selectJedis();
            Map<String, String> result = jedis.hgetAll(key);
            log.info("[Redis]hgetAll {},bizSeqNo:{},usedTime:{} result:{}", key, bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis(), result);
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:hgetAll bizSeqNo:{},usedTime:{}.",
                    Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo, e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获得map集合中指定多个field对应的值
     * 
     * @param key
     * @param fields
     * @return
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        try {
            log.info("[Redis]hmget {}", key);
            jedis = this.selectJedis();
            return jedis.hmget(key, fields);
        } catch (Exception e) {
            log.error("[Redis]OnError:hmget.", e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 向zSet添加成员，返回添加成功的数量
     * 
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Long zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            log.info("[Redis]zadd {}", key);
            jedis = this.selectJedis();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            log.error("[Redis]OnError:zadd.", e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取所有zset中的元素
     * 
     * @param key
     * @return
     */
    public Set<String> zrange(String key) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]zgetAll {}", key);
            jedis = this.selectJedis();
            Set<String> result = jedis.zrange(key, 0, -1);
            log.info("[Redis]zgetAll key:{},result:{},usedTime:{}", key, result,
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:zgetAll usedTime:{}.",
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取zSet的成员数
     * 
     * @param key
     * @return
     */
    public Long zcard(String key) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]zcard {}", key);
            jedis = this.selectJedis();
            Long result = jedis.zcard(key);
            log.info("[Redis]zcard key:{},result:{},usedTime:{}", key, result,
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:zcard key:{},usedTime:{}.", key,
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * score查询 member元素
     * 
     * @param key
     * @return
     */
    public Double zscore(String key, String member) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]zscore {}", key);
            jedis = this.selectJedis();
            Double result = jedis.zscore(key, member);
            log.info("[Redis]zscore key:{},result:{},usedTime:{}", key, result,
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:zscore usedTime:{}.",
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 向Set添加一个或多个成员，返回添加成功的数量
     * 
     * @param key
     * @param members
     * @return Long
     */
    public Long sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            log.info("[Redis]sadd {}", key);
            jedis = this.selectJedis();
            return jedis.sadd(key, members);
        } catch (Exception e) {
            log.error("[Redis]OnError:sadd.", e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取Set的成员数
     * 
     * @param key
     * @return
     */
    public Long scard(String key) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]scard {}", key);
            jedis = this.selectJedis();
            Long result = jedis.scard(key);
            log.info("[Redis]scard key:{},result:{},usedTime:{}", key, result,
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:scard usedTime:{}.",
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 返回Set中的所有成员
     * 
     * @param key
     * @return Set<String>
     */
    public Set<String> smembers(String key) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]smembers {}", key);
            jedis = this.selectJedis();
            Set<String> result = jedis.smembers(key);
            log.info("[Redis]smembers key:{},result:{},usedTime:{}", key, result,
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:smembers usedTime:{}.",
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 判断 member元素是否是Set key的成员，在集合中返回True
     * 
     * @param key
     * @param member
     * @return Boolean
     */
    public Boolean sIsMember(String key, String member) {
        Instant startTime = Instant.now();
        Jedis jedis = null;
        try {
            log.info("[Redis]sIsMember {}", key);
            jedis = this.selectJedis();
            Boolean result = jedis.sismember(key, member);
            log.info("[Redis]sIsMember key:{},result:{},usedTime:{}", key, result,
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.error("[Redis]OnError:sIsMember usedTime:{}.",
                    Duration.between(startTime, Instant.now()).toMillis(), e);
            throw new BaseException(ConstantCode.REDIS_REQUIRE_EXCEPTION);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
