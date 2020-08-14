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
package com.webank.webase.data.fetcher.table;

import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.enums.TableName;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    /**
     * create common table.
     */
    public void newCommonTable() {
        tableMapper.createTbKeyword();
        tableMapper.createTbTransAudit();
        tableMapper.createTbAppAudit();
    }

    /**
     * drop table.
     */
    public void dropTable(int chainId, int groupId) {
        for (TableName enumName : TableName.values()) {
            dropTableByName(enumName.getTableName(chainId, groupId));
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
    private void dropTableByName(String tableName) {
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
