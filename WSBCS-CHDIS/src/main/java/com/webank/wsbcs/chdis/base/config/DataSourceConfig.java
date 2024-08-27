package com.webank.wsbcs.chdis.base.config;

import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.alibaba.druid.pool.DruidDataSource;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.SecurityUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
@Configuration
public class DataSourceConfig {

    @Autowired
    private ConstantProperties constantProperties;

    /**
     * @Description: 数据源
     */
    @Bean(value = "dataSource") // 声明其为Bean实例
    @Primary // 在同样的DataSource中，首先使用被标注的DataSource
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(url);
        datasource.setUsername(username);
        // 大于32才启用行内加解密
        String decPassword = StringUtils.length(password) > 32 ? SecurityUtil.decrypt(password,
                constantProperties.getPrivateStr(), constantProperties.getPublicStr()) : password;
        log.debug("**** decPassword:{}", decPassword);
        datasource.setPassword(decPassword);
        datasource.setDriverClassName(driverClassName);
        datasource.setDefaultAutoCommit(defaultAutoCommit);
        // configuration
        datasource.setInitialSize(initialSize);
        datasource.setMaxActive(maxActive);
        datasource.setMinIdle(minIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setValidationQuery(validationQuery);
        //
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        datasource.setRemoveAbandoned(removeAbandoned);
        datasource.setLogAbandoned(logAbandoned);
        datasource.setRemoveAbandonedTimeout(removeAbandonedTimeout);

        datasource.setMaxWait(maxWait);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setConnectionProperties(connectionProperties);
        return datasource;
    }

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.defaultAutoCommit}")
    private boolean defaultAutoCommit;

    @Value("${spring.datasource.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.maxWait}")
    private long maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.removeAbandoned}")
    private boolean removeAbandoned;

    @Value("${spring.datasource.logAbandoned}")
    private boolean logAbandoned;

    @Value("${spring.datasource.removeAbandonedTimeout}")
    private int removeAbandonedTimeout;

    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.connectionProperties}")
    private String connectionProperties;

}
