/**
 * Copyright 2014-2020  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webase.data.collect.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.properties.ExecutorProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

/**
 * config about bean.
 */
@Log4j2
@Configuration
public class BeanConfig {

    @Autowired
    private ConstantProperties constantProperties;
    @Autowired
    private ExecutorProperties executorProperties;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    /**
     * resttemplate.
     */
    @Bean
    public RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory factory = getHttpFactory();
        factory.setReadTimeout(constantProperties.getHttpTimeOut());// ms
        factory.setConnectTimeout(constantProperties.getHttpTimeOut());// ms
        return new RestTemplate(factory);
    }

    /**
     * factory.
     */
    @Bean()
    @Scope("prototype")
    public SimpleClientHttpRequestFactory getHttpFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        return factory;
    }

    /**
     * pull block and trans from chain
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor mgrAsyncExecutor() {
        log.info("start mgrAsyncExecutor init..");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueSize());
        executor.setThreadNamePrefix(executorProperties.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // init executor
        executor.initialize();
        return executor;
    }
    
    /**
     * config time format.
     */
    @Bean(name = "mapperObject")
    public ObjectMapper getObjectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        ObjectMapper om = new ObjectMapper();
        om.registerModule(javaTimeModule);
        return om;
    }
}
