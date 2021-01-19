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
package com.webank.webase.data.collect.table;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.group.entity.TbGroup;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * service of table
 */
@Log4j2
@Service
public class TableService {

    @Autowired
    private TableMapper tableMapper;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    public static Map<String, Integer> CREATED_MAP = new ConcurrentHashMap<>();
    public static final Integer CREATED = 1;

    /**
     * create common table.
     */
    public void newCommonTable() {
        tableMapper.createTbChain();
        tableMapper.createTbFront();
        tableMapper.createTbGroup();
        tableMapper.createTbFrontGroupMap();
        tableMapper.createTbNode();
        tableMapper.createTbTxnDaily();
        tableMapper.createTbUser();
        tableMapper.createTbContract();
        tableMapper.createTbMethod();
        tableMapper.createTbSolc();
        tableMapper.createTbEventExportTask();
        tableMapper.createTbGas();
        tableMapper.createTbGasReconciliation();
    }

    /**
     * create sub table.
     */
    @Transactional
    public void newSubTable(int chainId, int groupId) {
        if (chainId == 0 || groupId == 0) {
            return;
        }
        // table created record in map, check if exist in map
        String chainIdGroupIndexKey = chainId + "_" + groupId;
        if (CREATED_MAP.get(chainIdGroupIndexKey) != null) {
            log.debug("table of indexKey: {} created.", chainIdGroupIndexKey);
            return;
        }
        tableMapper.createTbTaskPool(TableName.TASK.getTableName(chainId, groupId));
        tableMapper.createTbBlock(TableName.BLOCK.getTableName(chainId, groupId));
        tableMapper.createTbTransaction(TableName.TRANS.getTableName(chainId, groupId));
        tableMapper.createTbReceipt(TableName.RECEIPT.getTableName(chainId, groupId));
        tableMapper.createTbParser(TableName.PARSER.getTableName(chainId, groupId));
        log.info("create table of indexKey: {} ", chainIdGroupIndexKey);
        CREATED_MAP.put(chainIdGroupIndexKey, CREATED);
    }

    /**
     * newEvnetInfoTable.
     * 
     * @param chainId
     * @param groupId
     * @param eventTaskId
     */
    public void newEvnetInfoTable(int chainId, int groupId, int eventExportId) {
        if (chainId == 0 || groupId == 0 || eventExportId == 0) {
            return;
        }
        // table created record in map, check if exist in map
        String indexKey = chainId + "_" + groupId + "_" + eventExportId;
        if (CREATED_MAP.get(indexKey) != null) {
            log.debug("table of indexKey: {} created.", indexKey);
            return;
        }
        tableMapper.createTbEventInfo(
                TableName.EVENT.getEventInfoTableName(chainId, groupId, eventExportId));
        log.info("create table of indexKey: {} ", indexKey);
        CREATED_MAP.put(indexKey, CREATED);
    }

    /**
     * drop table.
     */
    public void dropTable(int chainId, int groupId) {
        String indexKey = chainId + "_" + groupId;
        for (TableName enumName : TableName.values()) {
            dropTableByName(enumName.getTableName(chainId, groupId));
        }
        CREATED_MAP.remove(indexKey);
    }

    /**
     * createPartition.
     */
    public void createPartition(String partitionName, int partitionValue, List<TbGroup> groupList) {
        // partition created record in map, check if exist in map
        String indexKey = partitionName;
        String partitionRecord =
                tableMapper.queryPartition(getDbName(), ConstantProperties.TB_GAS, partitionName);
        if (CREATED_MAP.get(indexKey) != null || partitionRecord != null) {
            log.info("partition: {} has been created.", indexKey);
            return;
        }
        createPartition(ConstantProperties.TB_GAS, partitionName, partitionValue);
        for (TbGroup tbGroup : groupList) {
            createPartition(TableName.TASK.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            createPartition(
                    TableName.BLOCK.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            createPartition(
                    TableName.TRANS.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            createPartition(
                    TableName.RECEIPT.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
            createPartition(
                    TableName.PARSER.getTableName(tbGroup.getChainId(), tbGroup.getGroupId()),
                    partitionName, partitionValue);
        }
        log.info("partition: {} create.", indexKey);
        CREATED_MAP.put(indexKey, CREATED);
    }

    /**
     * createPartition.
     */
    public void createPartition(String tableName, String partitionName, int partitionValue) {
        try {
            tableMapper.createPartition(tableName, partitionName, partitionValue);
        } catch (Exception e) {
            log.error("createPartition Exception", e);
        }
    }

    /**
     * get db name.
     */
    public String getDbName() {
        if (StringUtils.isBlank(dbUrl)) {
            log.error("fail getDbName. dbUrl is null");
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
        String subUrl = dbUrl.substring(0, dbUrl.indexOf("?"));
        String dbName = subUrl.substring(subUrl.lastIndexOf("/") + 1);
        return dbName;
    }

    /**
     * drop table by tableName.
     */
    public void dropTableByName(String tableName) {
        log.info("start drop table. tableName:{}", tableName);
        if (StringUtils.isBlank(tableName)) {
            return;
        }
        List<String> tableNameList = tableMapper.queryTables(getDbName(), tableName);
        if (CollectionUtils.isEmpty(tableNameList)) {
            log.warn("fail dropTableByName. not fount this table, tableName:{}", tableName);
            return;
        }
        int affectedRow = 1;
        while (affectedRow > 0) {
            affectedRow = tableMapper.deleteByTableName(tableName);
            log.debug("delete table:{} affectedRow:{}", tableName, affectedRow);
        }

        // drop table
        tableMapper.dropTable(getDbName(), tableName);
        log.info("end dropTableByName. tableName:{}", tableName);
    }

}
