/**
 * Copyright 2014-2023 the original author or authors.
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
package com.webank.wsbcs.chdis.base.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract;
import lombok.Data;

/**
 * constants.
 */
@Data
@Component
@ConfigurationProperties(prefix = ConstantProperties.CONSTANT_PREFIX)
public class ConstantProperties {
    // constant
    public static final String CONSTANT_PREFIX = "constant";
    public static final String PREFIX_RESULT_CODE = "0x";
    public static final int HTTP_SUCCESS_RESPONSE_CODE = 0;
    public static final String SPLIT_UNDERLINE = "_";

    public static final Boolean IS_WASM = false;
    public static final Long TASK_COMMON_TIMEOUT = 600000L;

    private String defaultChainId = "chain0_group0";
    private String v3ConfigPath = "conf/cert-v3/";
    private String bac20Address;
    private String rechargeProxyAddress;
    private String weworldManageAddress;
    private String beanSystemRecycleAddress;
    private String zombieAddress;
    private String redPacketProxyAddress;
    private long cacheInterval = 600000;
    
    // APP CONFIG
    private String privateStr;
    private String publicStr;
    
    /**
     * block into
     */
    private Boolean ifPullData = true;
    private Boolean isBlockPullFromZero = false;
    private Long pullBlockInitCnts = 200L;
    private Long pullBlockSleepTime = 10L;
    private Long pullBlockTimeout = 600000L;
    private Long blockRetainMax = 5000L;
    private Long transRetainMax = 10000L;
    private Integer fetchTransCount = 5000;
    // delete
    private Boolean isDeleteInfo = true;
    private Long deleteInfoTimeout = 30000L;
    private Long transReceiptTimeout = 7000L;
    
    private long userFlowMaxCnt = 10000L;
    private long orgFlowMaxCnt = 100000L;
    
    private RechargeWithdrawContract rechargeWithdrawContract;

}