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
package com.webank.webase.data.collect.job.config;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobRootConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.webank.webase.data.collect.job.DataParserJob;
import com.webank.webase.data.collect.job.DataPullJob;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DataflowJobConfig.
 *
 */
@Configuration
@ConditionalOnProperty(value = {"constant.multiLiving"}, havingValue = "true")
public class DataflowJobConfig {

    @Resource
    private ZookeeperRegistryCenter regCenter;
    
    @Value("${constant.dataPullCron}")
    private String dataPullCron;
    
    @Value("${constant.dataParserCron}")
    private String dataParserCron;
    
    @Value("${job.dataflow.shardingTotalCount}")
    private int shardingTotalCount;

    /**
     * parserScheduler.
     * 
     * @param dataflowJob instance
     * @return
     */
    @Bean(initMethod = "init")
    public JobScheduler parserScheduler(final DataParserJob dataflowJob) {
        return new SpringJobScheduler(dataflowJob, regCenter, parserDataflowConfig());
    }

    /**
     * pullScheduler.
     * 
     * @param dataflowJob instance
     * @return
     */
    @Bean(initMethod = "init")
    public JobScheduler pullScheduler(final DataPullJob dataflowJob) {
        return new SpringJobScheduler(dataflowJob, regCenter, pullDataflowConfig());
    }

    /**
     * parserDataflowConfig.
     * 
     * @return
     */
    private LiteJobConfiguration parserDataflowConfig() {
        JobCoreConfiguration dataflowCoreConfig = JobCoreConfiguration
                .newBuilder(DataParserJob.class.getName(), dataPullCron, shardingTotalCount)
                .shardingItemParameters(null).build();
        DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(
                dataflowCoreConfig, DataParserJob.class.getCanonicalName(), false);
        JobRootConfiguration dataflowJobRootConfig =
                LiteJobConfiguration.newBuilder(dataflowJobConfig).overwrite(true).build();

        return (LiteJobConfiguration) dataflowJobRootConfig;
    }

    /**
     * pullDataflowConfig.
     * 
     * @return
     */
    private LiteJobConfiguration pullDataflowConfig() {
        JobCoreConfiguration dataflowCoreConfig = JobCoreConfiguration
                .newBuilder(DataPullJob.class.getName(), dataParserCron, shardingTotalCount)
                .shardingItemParameters(null).build();
        DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(
                dataflowCoreConfig, DataPullJob.class.getCanonicalName(), false);
        JobRootConfiguration dataflowJobRootConfig =
                LiteJobConfiguration.newBuilder(dataflowJobConfig).overwrite(true).build();

        return (LiteJobConfiguration) dataflowJobRootConfig;
    }
}
