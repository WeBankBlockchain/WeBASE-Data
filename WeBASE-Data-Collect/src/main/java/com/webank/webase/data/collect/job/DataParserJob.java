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
package com.webank.webase.data.collect.job;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.transaction.TransactionService;

import lombok.extern.slf4j.Slf4j;

/**
 * DataParserJob.
 *
 */
@Service
@Slf4j
@ConditionalOnProperty(value = {"constant.multiLiving"}, havingValue = "true", matchIfMissing = false)
public class DataParserJob implements DataflowJob<TbGroup> {

    @Autowired
    private GroupService groupService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public List<TbGroup> fetchData(ShardingContext shardingContext) {
        List<TbGroup> groupList = groupService.getGroupListByJob(null, DataStatus.NORMAL.getValue(),
                shardingContext.getShardingTotalCount(), shardingContext.getShardingItem());
        if (CollectionUtils.isEmpty(groupList)) {
            log.info("DataParserJob item:{} jump over: not found any group",
                    shardingContext.getShardingItem());
        }
        return groupList;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<TbGroup> groupList) {
        log.info("start parser. item:{} ", shardingContext.getShardingItem());
        Instant startTime = Instant.now();
        if (CollectionUtils.isEmpty(groupList)) {
            log.warn("parser jump over: not found any group");
            return;
        }
        // count down group, make sure all group's transMonitor finished
        CountDownLatch latch = new CountDownLatch(groupList.size());
        groupList.stream().forEach(group -> transactionService.parserProcess(latch,
                group.getChainId(), group.getGroupId()));
        try {
            latch.await();
        } catch (InterruptedException ex) {
            log.error("InterruptedException", ex);
            Thread.currentThread().interrupt();
        }
        log.info("end parser. item:{} useTime:{} ", shardingContext.getShardingItem(),
                Duration.between(startTime, Instant.now()).toMillis());
    }

}
