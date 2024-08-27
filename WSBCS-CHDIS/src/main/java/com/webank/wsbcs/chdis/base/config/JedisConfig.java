package com.webank.wsbcs.chdis.base.config;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.SecurityUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author v_sflkchen
 * @date 2024/04/23
 */
@Data
@Log4j2
@Configuration
@ConfigurationProperties(prefix = "jedis")
public class JedisConfig {

    @Autowired
    private ConstantProperties constantProperties;

    private Integer maxIdle = 32;
    private Integer maxTotal = 32;
    private Integer minIdle = 16;
    private Integer maxWaitMillis = 1000;
    private Boolean testOnBorrow = false;

    private String ip;
    private Integer port;
    private String authPwd;
    private Integer connectionTimeout = 1000;
    private Integer socketTimeout = 1000;
    // select redis db index
    private Integer select = -1;

    /**
     * redis init
     */
    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig setJedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(testOnBorrow);
        return config;
    }

    @Bean(name = "multiJedisShardedPool")
    public JedisPool setJedisShardedPool() throws IOException {
        // 大于32才启用行内加解密
        String password = StringUtils.length(authPwd) > 32 ? SecurityUtil.decrypt(authPwd,
                constantProperties.getPrivateStr(), constantProperties.getPublicStr()) : authPwd;
        int database;
        if (this.select == -1) {
            database = Protocol.DEFAULT_DATABASE;
        } else {
            database = this.select;
        }
        try {
            JedisPool jedisPool = new JedisPool(setJedisPoolConfig(), ip, port, connectionTimeout,
                    socketTimeout, password, database, null);
            checkJedisPool(jedisPool);
            log.info("####multiJedisShardedPool init succeed");
            return jedisPool;
        } catch (Exception ex) {
            log.error("setJedisShardedPool failed:", ex);
            return null;
        }
    }

    public void checkJedisPool(JedisPool jedisPool) throws BaseException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String status = jedis.get("check");
            log.info("checkJedisPool select db:{} status:{}", this.select, status);
        } catch (Exception e) {
            log.error("OnError:checkJedisPool.", e);
            throw new BaseException(ConstantCode.REDIS_INIT_ERROR);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
