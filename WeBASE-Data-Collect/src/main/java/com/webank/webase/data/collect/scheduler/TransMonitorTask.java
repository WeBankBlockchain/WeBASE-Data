/**
 * Copyright 2014-2020  the original author or authors.
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
package com.webank.webase.data.collect.scheduler;

import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.monitor.MonitorService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import lombok.extern.log4j.Log4j2;

/**
 * using in async monitor
 */
@Log4j2
@Component
public class TransMonitorTask {

    @Autowired
    private MonitorService monitorService;
    @Autowired
    private GroupService groupService;

//    @Scheduled(fixedRateString = "${constant.transMonitorTaskFixedRate}", initialDelay = 1000)
    public void taskStart() {
        monitorStart();
    }

    /**
     * start monitor.
     */
    public void monitorStart() {
        Instant startTime = Instant.now();
        log.info("=== start monitor. startTime:{}", startTime.toEpochMilli());
        //get group list
        List<TbGroup> groupList = groupService.getGroupList(DataStatus.NORMAL.getValue());
        if (CollectionUtils.isEmpty(groupList)) {
            log.info("monitor jump over .not found any group");
            return;
        }

        CountDownLatch latch = new CountDownLatch(groupList.size());
        groupList.stream()
            .forEach(group -> monitorService.transMonitorByGroupId(latch, group.getGroupId()));

        try {
             latch.await();
        } catch (InterruptedException ex) {
            log.error("InterruptedException", ex);
            Thread.currentThread().interrupt();
        }

        log.info("=== end monitor. useTime:{} ",
            Duration.between(startTime, Instant.now()).toMillis());
    }
}
