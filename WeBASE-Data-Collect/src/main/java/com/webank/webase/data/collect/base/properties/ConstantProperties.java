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
package com.webank.webase.data.collect.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * constants.
 */
@Data
@Component
@ConfigurationProperties(prefix = ConstantProperties.CONSTANT_PREFIX)
public class ConstantProperties {
    // constant
    public static final String CONSTANT_PREFIX = "constant";
    public static final String CONTRACT_NAME_ZERO = "0x00000000";
    public static final String ADDRESS_DEPLOY = "0x0000000000000000000000000000000000000000";
    
    public static final String ORDER_BY_ID_DESC = "id DESC";

    // group info
    private String groupInvalidGrayscaleValue = "1M"; // y:year, M:month, d:day of month, h:hour,
                                                      // m:minute, n:forever valid

    // data pull
    private long startBlockNumber = 0;
    private int crawlBatchUnit = 100;

    // front http request
    private Integer httpTimeOut = 5000;
    private Integer maxRequestFail = 3;
    private Long sleepWhenHttpMaxFail = 60000L; // default 1min

    // data handle
    private boolean ifPullData = true; // default true
    private boolean ifEsEnable = false; // default false
    private boolean multiLiving = false; // default false
    private Long dataParserTaskFixedDelay = 60000L; // default 1min
    
    // config server
    private boolean syncConfig = false; // default false
    private String configServerIpPort = "127.0.0.1:8001";

}
