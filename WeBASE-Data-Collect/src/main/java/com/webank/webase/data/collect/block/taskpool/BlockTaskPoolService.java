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
package com.webank.webase.data.collect.block.taskpool;

import com.google.common.collect.Lists;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.properties.BlockConstants;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.block.BlockService;
import com.webank.webase.data.collect.block.entity.BlockInfo;
import com.webank.webase.data.collect.block.entity.TbBlockTaskPool;
import com.webank.webase.data.collect.block.enums.BlockCertaintyEnum;
import com.webank.webase.data.collect.block.enums.TxInfoStatusEnum;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

/**
 * BlockTaskPoolService
 *
 */
@Service
@Slf4j
public class BlockTaskPoolService {

    @Autowired
    private BlockTaskPoolMapper taskPoolMapper;
    @Autowired
    private FrontInterfaceService frontInterface;
    @Autowired
    private BlockService blockService;
    @Autowired
    private RollBackService rollBackService;
    @Autowired
    private ConstantProperties cProperties;

    public long getTaskPoolHeight(int groupId) {
        long height = 0;
        BigInteger localMaxBlockNumber =
                taskPoolMapper.getLatestBlockNumber(TableName.TASK.getTableName(groupId));
        if (Objects.nonNull(localMaxBlockNumber)) {
            height = localMaxBlockNumber.longValue() + 1;
        }
        return height;
    }

    @Transactional
    public void prepareTask(int groupId, long begin, long end, boolean certainty) {
        log.info("Begin to prepare sync blocks from {} to {}", begin, end);
        List<TbBlockTaskPool> list = Lists.newArrayList();
        for (long i = begin; i <= end; i++) {
            TbBlockTaskPool pool = new TbBlockTaskPool().setBlockNumber(i)
                    .setSyncStatus(TxInfoStatusEnum.INIT.getStatus());
            if (certainty) {
                pool.setCertainty(BlockCertaintyEnum.FIXED.getCertainty());
            } else {
                if (i <= end - BlockConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER) {
                    pool.setCertainty(BlockCertaintyEnum.FIXED.getCertainty());
                } else {
                    pool.setCertainty(BlockCertaintyEnum.UNCERTAIN.getCertainty());
                }
            }
            list.add(pool);
        }
        taskPoolMapper.saveAll(TableName.TASK.getTableName(groupId), list);
        log.info("Sync blocks from {} to {} are prepared.", begin, end);
    }

    public List<BlockInfo> fetchData(int groupId, int count) {
        List<TbBlockTaskPool> tasks = taskPoolMapper.findBySyncStatusOrderByBlockHeightLimit(
                TableName.TASK.getTableName(groupId), TxInfoStatusEnum.INIT.getStatus(), count);
        if (CollectionUtils.isEmpty(tasks)) {
            return new ArrayList<>();
        } else {
            return getTasks(groupId, tasks);
        }
    }

    public List<BlockInfo> getTasks(int groupId, List<TbBlockTaskPool> tasks) {
        List<BlockInfo> result = new ArrayList<>();
        List<TbBlockTaskPool> pools = new ArrayList<>();
        for (TbBlockTaskPool task : tasks) {
            task.setSyncStatus(TxInfoStatusEnum.DOING.getStatus());
            BigInteger bigBlockHeight = new BigInteger(Long.toString(task.getBlockNumber()));
            BlockInfo block;
            try {
                block = frontInterface.getBlockByNumber(groupId, bigBlockHeight);
                result.add(block);
                pools.add(task);
            } catch (Exception e) {
                log.error("Block {},  exception occur in job processing: {}", task.getBlockNumber(),
                        e.getMessage());
                taskPoolMapper.setSyncStatusByBlockHeight(TableName.TASK.getTableName(groupId),
                        TxInfoStatusEnum.ERROR.getStatus(), task.getBlockNumber());
            }
        }
        taskPoolMapper.saveAll(TableName.TASK.getTableName(groupId), pools);
        log.info("Successful fetch {} Blocks.", result.size());
        return result;
    }

    @Async("mgrAsyncExecutor")
    public void handleSingleBlock(int groupId, BlockInfo b, long total) {
        process(groupId, b, total);
    }

    public void processDataSequence(int groupId, List<BlockInfo> data, long total) {
        for (BlockInfo b : data) {
            process(groupId, b, total);
        }
    }

    public void process(int groupId, BlockInfo b, long total) {
        try {
            blockService.saveBlockInfo(b, groupId);
            taskPoolMapper.setSyncStatusByBlockHeight(TableName.TASK.getTableName(groupId),
                    TxInfoStatusEnum.DONE.getStatus(), b.getNumber().longValue());
            log.info("Block {} of {} sync block succeed.", b.getNumber().longValue(), total);
        } catch (Exception e) {
            log.error("block {}, exception occur in job processing: {}", b.getNumber().longValue(),
                    e.getMessage());
            taskPoolMapper.setSyncStatusByBlockHeight(TableName.TASK.getTableName(groupId),
                    TxInfoStatusEnum.ERROR.getStatus(), b.getNumber().longValue());
        }
    }

    public void checkForks(int groupId, long currentBlockHeight) throws IOException {
        log.info("current block height is {}, and begin to check forks", currentBlockHeight);
        List<TbBlockTaskPool> uncertainBlocks = taskPoolMapper.findByCertainty(
                TableName.TASK.getTableName(groupId), BlockCertaintyEnum.UNCERTAIN.getCertainty());
        for (TbBlockTaskPool pool : uncertainBlocks) {
            if (pool.getBlockNumber() <= currentBlockHeight
                    - BlockConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER) {
                if (pool.getSyncStatus() == TxInfoStatusEnum.DOING.getStatus()) {
                    log.error("block {} is doing!", pool.getBlockNumber());
                    continue;
                }
                if (pool.getSyncStatus() == TxInfoStatusEnum.INIT.getStatus()) {
                    log.error("block {} is not sync!", pool.getBlockNumber());
                    taskPoolMapper.setCertaintyByBlockHeight(TableName.TASK.getTableName(groupId),
                            BlockCertaintyEnum.FIXED.getCertainty(), pool.getBlockNumber());
                    continue;
                }
                BlockInfo block = frontInterface.getBlockByNumber(groupId,
                        BigInteger.valueOf(pool.getBlockNumber()));
                String newHash = block.getHash();
                if (!StringUtils.equals(newHash, blockService
                        .getBlockByBlockNumber(groupId, pool.getBlockNumber()).getBlockHash())) {
                    log.info("Block {} is forked!!! ready to resync", pool.getBlockNumber());
                    rollBackService.rollback(groupId, pool.getBlockNumber());
                    taskPoolMapper.setSyncStatusAndCertaintyByBlockHeight(
                            TableName.TASK.getTableName(groupId), TxInfoStatusEnum.INIT.getStatus(),
                            BlockCertaintyEnum.FIXED.getCertainty(), pool.getBlockNumber());
                } else {
                    log.info("Block {} is not forked!", pool.getBlockNumber());
                    taskPoolMapper.setCertaintyByBlockHeight(TableName.TASK.getTableName(groupId),
                            BlockCertaintyEnum.FIXED.getCertainty(), pool.getBlockNumber());
                }
            }
        }
    }

    public void checkTaskCount(int groupId, long startBlockNumber, long currentMaxTaskPoolNumber) {
        log.info("Check task count from {} to {}", startBlockNumber, currentMaxTaskPoolNumber);
        if (isComplete(groupId, startBlockNumber, currentMaxTaskPoolNumber)) {
            return;
        }
        List<TbBlockTaskPool> supplements = new ArrayList<>();
        long t = startBlockNumber;
        for (long i = startBlockNumber; i <= currentMaxTaskPoolNumber
                - cProperties.getCrawlBatchUnit(); i += cProperties.getCrawlBatchUnit()) {
            long j = i + cProperties.getCrawlBatchUnit() - 1;
            Optional<List<TbBlockTaskPool>> optional = findMissingPoolRecords(groupId, i, j);
            if (optional.isPresent()) {
                supplements.addAll(optional.get());
            }
            t = j + 1;
        }
        Optional<List<TbBlockTaskPool>> optional =
                findMissingPoolRecords(groupId, t, currentMaxTaskPoolNumber);
        if (optional.isPresent()) {
            supplements.addAll(optional.get());
        }
        log.info("Find {} missing pool numbers", supplements.size());
        taskPoolMapper.saveAll(TableName.TASK.getTableName(groupId), supplements);
    }

    public void checkTimeOut(int groupId) {
        DateTime offsetDate =
                DateUtil.offsetSecond(DateUtil.date(), 0 - BlockConstants.DEPOT_TIME_OUT);
        log.info("Begin to check timeout transactions which is ealier than {}", offsetDate);
        List<TbBlockTaskPool> list = taskPoolMapper.findBySyncStatusAndDepotUpdatetimeLessThan(
                TableName.TASK.getTableName(groupId), TxInfoStatusEnum.DOING.getStatus(),
                offsetDate);
        if (!CollectionUtils.isEmpty(list)) {
            log.info("Detect {} timeout transactions.", list.size());
        }
        list.forEach(p -> {
            log.error(
                    "Block {} sync block timeout!!, the depot_time is {}, and the threshold time is {}",
                    p.getBlockNumber(), p.getModifyTime(), offsetDate);
            taskPoolMapper.setSyncStatusByBlockHeight(TableName.TASK.getTableName(groupId),
                    TxInfoStatusEnum.TIMEOUT.getStatus(), p.getBlockNumber());
        });
    }

    public void processErrors(int groupId) {
        log.info("Begin to check error records");
        List<TbBlockTaskPool> unnormalRecords =
                taskPoolMapper.findUnNormalRecords(TableName.TASK.getTableName(groupId));
        if (CollectionUtils.isEmpty(unnormalRecords)) {
            return;
        } else {
            log.info("sync block detect {} error transactions.", unnormalRecords.size());
            unnormalRecords.parallelStream().map(b -> b.getBlockNumber()).forEach(e -> {
                log.error("Block {} sync error, and begin to rollback.", e);
                rollBackService.rollback(groupId, e);
                taskPoolMapper.setSyncStatusByBlockHeight(TableName.TASK.getTableName(groupId),
                        TxInfoStatusEnum.INIT.getStatus(), e);
            });
        }
    }

    private Optional<List<TbBlockTaskPool>> findMissingPoolRecords(int groupId, long startIndex,
            long endIndex) {
        if (isComplete(groupId, startIndex, endIndex)) {
            return Optional.empty();
        }
        List<TbBlockTaskPool> list = taskPoolMapper
                .findByBlockHeightRange(TableName.TASK.getTableName(groupId), startIndex, endIndex);
        List<Long> ids = list.stream().map(p -> p.getBlockNumber()).collect(Collectors.toList());
        List<TbBlockTaskPool> supplements = new ArrayList<>();
        for (long tmpIndex = startIndex; tmpIndex <= endIndex; tmpIndex++) {
            if (ids.indexOf(tmpIndex) >= 0) {
                continue;
            }
            log.info("Successfully detect block {} is missing. Try to sync block again.", tmpIndex);
            TbBlockTaskPool pool = new TbBlockTaskPool().setBlockNumber(tmpIndex)
                    .setSyncStatus(TxInfoStatusEnum.ERROR.getStatus())
                    .setCertainty(BlockCertaintyEnum.UNCERTAIN.getCertainty());
            supplements.add(pool);
        }
        return Optional.of(supplements);
    }

    private boolean isComplete(int groupId, long startBlockNumber, long currentMaxTaskPoolNumber) {
        long deserveCount = currentMaxTaskPoolNumber - startBlockNumber + 1;
        long actualCount = taskPoolMapper.countByBlockHeightRange(
                TableName.TASK.getTableName(groupId), startBlockNumber, currentMaxTaskPoolNumber);
        log.info(
                "Check task count from block {} to {}, deserve count is {}, and actual count is {}",
                startBlockNumber, currentMaxTaskPoolNumber, deserveCount, actualCount);
        if (deserveCount == actualCount) {
            return true;
        } else {
            return false;
        }
    }
}
