/**
 * Copyright 2014-2023 the original author or authors.
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
package com.webank.wsbcs.chdis.common.config;

import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.common.data.DataService;

/**
 * ConfigService.
 */
@Service
public class ConfigService {

    @Autowired
    private DataService dataService;

    public static final int ADDRESS_LENGTH = 42;
    public static final String ADDRESS_SPLIT = ";";
    public static final String ABI_CONTRACT_TYPE =
            "[{\"inputs\":[],\"name\":\"_contractType\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]";
    public static final String FUNCTION_CONTRACT_TYPE = "_contractType";
    public static final String BAC20_CONTRACT_TYPE = "BAC20";
    
    public static final String KEY_PULLFROMBLOCK = "PullFromBlock";
    public static final String KEY_ISRESETBLOCKNUMBER = "IsResetBlockNumber";
    public static final String KEY_RESETBLOCKNUMBER = "BeingResetBlockNumber";

    public void resetData(String bizSeqNo) {
        dataService.truncateTable(bizSeqNo);
    }

    public void resetDataFromBlockNumber(BigInteger blockNumber, String bizSeqNo) {
        dataService.resetDataFromBlockNumber(blockNumber.longValue(), bizSeqNo);
    }
}
