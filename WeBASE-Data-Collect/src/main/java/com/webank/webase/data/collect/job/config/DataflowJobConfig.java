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

import javax.annotation.Resource;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.webank.webase.data.collect.job.DataParserJob;
import com.webank.webase.data.collect.job.DataPullJob;

import lombok.Data;

/**
 * DataflowJobConfig.
 *
 */
@Data
@Configuration
@ConditionalOnProperty(value = {"constant.multiLiving"}, havingValue = "true", matchIfMissing = false)
public class DataflowJobConfig implements InitializingBean {

    @Resource
    private ZookeeperRegistryCenter regCenter;
    @Autowired
    private DataPullJob dataPullJob;
    @Autowired
    private DataParserJob dataParserJob;
    
    @Value("${constant.dataPullCron}")
    private String dataPullCron;
    
    @Value("${constant.dataParserCron}")
    private String dataParserCron;
    
    @Value("${job.dataflow.shardingTotalCount}")
    private int shardingTotalCount;

    @Override
	public void afterPropertiesSet() throws Exception {
		ScheduleJobBootstrap deploySchedule = new ScheduleJobBootstrap(regCenter, dataPullJob,
				pullDataflowConfig());
		deploySchedule.schedule();
		ScheduleJobBootstrap transSchedule = new ScheduleJobBootstrap(regCenter, dataParserJob,
				parserDataflowConfig());
		transSchedule.schedule();
	}
    
    /**
     * pullDataflowConfig.
     * 
     * @return
     */
    private JobConfiguration pullDataflowConfig() {
    	JobConfiguration jobConfiguration = JobConfiguration
				.newBuilder(DataPullJob.class.getName(), shardingTotalCount).cron(dataParserCron).build();
		return jobConfiguration;
    }

    /**
     * parserDataflowConfig.
     * 
     * @return
     */
    private JobConfiguration parserDataflowConfig() {
        JobConfiguration jobConfiguration = JobConfiguration
				.newBuilder(DataParserJob.class.getName(), shardingTotalCount).cron(dataParserCron).build();
		return jobConfiguration;
    }
    
}
