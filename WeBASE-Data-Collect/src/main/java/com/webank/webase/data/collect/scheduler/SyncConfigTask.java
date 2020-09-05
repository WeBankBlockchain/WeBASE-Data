/**
 * Copyright 2014-2020 the original author or authors.
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
package com.webank.webase.data.collect.scheduler;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.config.ConfigService;

import lombok.extern.log4j.Log4j2;

/**
 * get block info from chain. including tb_block and tb_trans (block contains trans)
 */
@Log4j2
@Component
@ConditionalOnProperty(value = {"constant.syncConfig"}, havingValue = "true")
public class SyncConfigTask {
    
    @Autowired
    private ConfigService configService;
    @Autowired
    private ConstantProperties cProperties;

    @Scheduled(cron = "${constant.syncConfigCron}")
    public void taskStart() {
        // toggle
        if (!cProperties.isSyncConfig()) {
            return;
        }
        syncConfigStart();
    }

    /**
     * task start
     */
    public void syncConfigStart() {
        log.info("start syncConfig.");
        Instant startTime = Instant.now();
        configService.syncConfig();
        log.info("end syncConfig useTime:{} ",
                Duration.between(startTime, Instant.now()).toMillis());
    }
}
