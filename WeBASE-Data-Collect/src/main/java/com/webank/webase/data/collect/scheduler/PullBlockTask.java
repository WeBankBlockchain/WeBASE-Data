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

import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.base.properties.BlockConstants;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.block.entity.BlockInfo;
import com.webank.webase.data.collect.block.taskpool.BlockTaskPoolService;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * get block info from chain. including tb_block and tb_trans (block contains trans)
 */
@Log4j2
@Component
public class PullBlockTask {

    @Autowired
    private BlockTaskPoolService blockTaskPoolService;
    @Autowired
    private FrontInterfaceService frontInterface;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private GroupService groupService;

    @Scheduled(fixedDelayString = "${constant.pullBlockTaskFixedDelay}", initialDelay = 1000)
    public void taskStart() {
        pullBlockStart();
    }

    /**
     * task start
     */
    public void pullBlockStart() {
        log.info("start pullBLock.");
        Instant startTime = Instant.now();
        List<TbGroup> groupList = groupService.getGroupList(DataStatus.NORMAL.getValue());
        if (CollectionUtils.isEmpty(groupList)) {
            log.warn("pullBlock jump over: not found any group");
            return;
        }

        CountDownLatch latch = new CountDownLatch(groupList.size());
        groupList.stream().forEach(group -> pullBlockByGroupId(latch, group.getGroupId()));

        try {
            latch.await();
        } catch (InterruptedException ex) {
            log.error("InterruptedException", ex);
            Thread.currentThread().interrupt();
        }

        log.info("end pullBLock useTime:{} ",
                Duration.between(startTime, Instant.now()).toMillis());
    }

    /**
     * get block from chain by groupId
     */
    @Async(value = "mgrAsyncExecutor")
    public void pullBlockByGroupId(CountDownLatch latch, int groupId) {
        log.info("start pullBlockByGroupId groupId:{}", groupId);
        try {
            boolean check = true;
            while (check) {
                // max block in chain
                long currentChainHeight = frontInterface.getLatestBlockNumber(groupId).longValue();
                long fromHeight = getHeight(blockTaskPoolService.getTaskPoolHeight(groupId));
                // control the batch unit number
                long end = fromHeight + cProperties.getCrawlBatchUnit() - 1;
                long toHeight = currentChainHeight < end ? currentChainHeight : end;
                log.info("Current depot status: {} of {}, and try to process block from {} to {}",
                        fromHeight - 1, currentChainHeight, fromHeight, toHeight);
                boolean certainty = toHeight + 1 < currentChainHeight
                        - BlockConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER;
                if (fromHeight <= toHeight) {
                    log.info("Try to sync block number {} to {} of {}", fromHeight, toHeight,
                            currentChainHeight);
                    blockTaskPoolService.prepareTask(groupId, fromHeight, toHeight, certainty);
                }

                log.info("Begin to fetch at most {} tasks", cProperties.getCrawlBatchUnit());
                List<BlockInfo> taskList =
                        blockTaskPoolService.fetchData(groupId, cProperties.getCrawlBatchUnit());
                for (BlockInfo b : taskList) {
                    blockTaskPoolService.handleSingleBlock(groupId, b, currentChainHeight);
                }
                if (!certainty) {
                    blockTaskPoolService.checkForks(groupId, currentChainHeight);
                    blockTaskPoolService.checkTaskCount(groupId, cProperties.getStartBlockNumber(),
                            currentChainHeight);
                }
                blockTaskPoolService.checkTimeOut(groupId);
                blockTaskPoolService.processErrors(groupId);
                if (fromHeight > toHeight) {
                    check = false;
                }
            }
        } catch (Exception ex) {
            log.error("fail pullBlockByGroupId. groupId:{} ", groupId, ex);
        } finally {
            latch.countDown();
        }
        log.info("end pullBlockByGroupId groupId:{}", groupId);
    }

    private long getHeight(long height) {
        return height > cProperties.getStartBlockNumber() ? height
                : cProperties.getStartBlockNumber();
    }
}
