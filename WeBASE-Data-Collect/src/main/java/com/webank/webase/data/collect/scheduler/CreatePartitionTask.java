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
package com.webank.webase.data.collect.scheduler;

import com.webank.webase.data.collect.base.enums.PartitionType;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.table.TableService;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * create table partition
 * 
 */
@Log4j2
@Component
public class CreatePartitionTask {

    @Autowired
    private GroupService groupService;
    @Autowired
    private TableService tableService;
    @Autowired
    private ConstantProperties constantProperties;

    @Scheduled(cron = "${constant.createPartitionCron}")
    public void taskStart() {
        createPartitionStart();
    }

    /**
     * createPartitionStart.
     */
    @Transactional
    public void createPartitionStart() {
        log.info("start createPartition.");
        Instant startTime = Instant.now();
        // get partition info
        LocalDateTime localDateTime = LocalDateTime.now();
        String partitionName = ConstantProperties.PREFIX_PARTITION;
        int partitionValue = 0;
        if (constantProperties.getPartitionType() == PartitionType.month.getType()) {
            partitionName = partitionName + CommonTools.getYearMonth(localDateTime) * 100;
            partitionValue = CommonTools.getYearMonthNext(localDateTime) * 100;
        } else {
            partitionName = partitionName + CommonTools.getYearMonthDay(localDateTime);
            partitionValue = CommonTools.getYearMonthDayNext(localDateTime);

        }
        // create table partition
        tableService.createPartition(ConstantProperties.TB_GAS, partitionName, partitionValue);
        List<TbGroup> groupList = groupService.getGroupList(null, null);
        for (TbGroup tbGroup : groupList) {
            tableService.createPartition(
                    TableName.TASK.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            tableService.createPartition(
                    TableName.BLOCK.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            tableService.createPartition(
                    TableName.TRANS.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            tableService.createPartition(
                    TableName.RECEIPT.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            tableService.createPartition(
                    TableName.PARSER.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
        }
        log.info("end createPartition useTime:{} ",
                Duration.between(startTime, Instant.now()).toMillis());
    }
}
