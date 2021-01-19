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

import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.dao.entity.TbGasReconciliation;
import com.webank.webase.data.collect.gas.GasReconciliationService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Log4j2
@Component
public class GasReconciliationTask {

    @Autowired
    private GasReconciliationService gasReconciliationService;
    @Autowired
    private ConstantProperties cProperties;

    @Scheduled(cron = "${constant.gasReconciliationCron}")
    public void taskStart() {
        // toggle
        if (!cProperties.isIfSaveGas()) {
            return;
        }
        gasReconciliationStart();
    }

    /**
     * gasReconciliationStart.
     */
    public void gasReconciliationStart() {
        log.info("start gasReconciliation.");
        Instant startTime = Instant.now();
        List<TbGasReconciliation> list = gasReconciliationService.selectByStatusExceptAbnormal();
        if (CollectionUtils.isEmpty(list)) {
            log.debug("gasReconciliation jump over: not found any record");
            return;
        }
        // count down , make sure all element finished
        CountDownLatch latch = new CountDownLatch(list.size());
        list.stream()
                .forEach(element -> gasReconciliationService.reconciliationProcess(latch, element));
        try {
            latch.await();
        } catch (InterruptedException ex) {
            log.error("InterruptedException", ex);
            Thread.currentThread().interrupt();
        }
        log.info("end gasReconciliation useTime:{} ",
                Duration.between(startTime, Instant.now()).toMillis());
    }

}
