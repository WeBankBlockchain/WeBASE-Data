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
package com.webank.webase.data.fetcher.scheduler;

import com.webank.webase.data.fetcher.audit.service.TransAuditService;
import com.webank.webase.data.fetcher.keyword.KeywordService;
import com.webank.webase.data.fetcher.keyword.entity.TbKeyword;
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
public class KeywordAuditTask {

    @Autowired
    private KeywordService keywordService;
    @Autowired
    private TransAuditService transAuditService;

    @Scheduled(cron = "${constant.keywordAuditCron}")
    public void taskStart() {
        auditStart();
    }

    /**
     * auditStart.
     */
    public void auditStart() {
        log.info("start auditTask.");
        Instant startTime = Instant.now();
        List<TbKeyword> list = keywordService.getKeywordList(null);
        if (CollectionUtils.isEmpty(list)) {
            log.info("auditTask jump over: not found any keyword");
            return;
        }
        // count down , make sure all element finished
        CountDownLatch latch = new CountDownLatch(list.size());
        list.stream()
                .forEach(element -> transAuditService.auditProcess(latch, element.getKeyword()));
        try {
            latch.await();
        } catch (InterruptedException ex) {
            log.error("InterruptedException", ex);
            Thread.currentThread().interrupt();
        }
        log.info("end auditTask useTime:{} ",
                Duration.between(startTime, Instant.now()).toMillis());
    }

}
