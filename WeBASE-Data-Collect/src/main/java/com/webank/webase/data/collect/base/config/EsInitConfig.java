/*
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

package com.webank.webase.data.collect.base.config;

import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.parser.EsCurdService;
import com.webank.webase.data.collect.table.TableService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * es init.
 *
 */
@Log4j2
@Configuration
public class EsInitConfig implements InitializingBean {
    @Autowired
    private TableService tableService;
    @Autowired
    private EsCurdService esCurdService;
    @Autowired
    private ConstantProperties constantProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (constantProperties.isIfEsEnable()) {
            String indexName = tableService.getDbName();
            if (!esCurdService.existIndex(indexName)) {
                log.info("create es index: {}", indexName);
                esCurdService.createIndex(indexName);
            }
        }
    }
}