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
package com.webank.webase.data.collect.base.code;

/**
 * A-BB-CCC A:error level. <br/>
 * 1:system exception <br/>
 * 2:business exception <br/>
 * B:project number <br/>
 * WeBASE-Node-Manager:02 <br/>
 * C: error code <br/>
 */
public class ConstantCode {

    /* return success */
    public static final RetCode SUCCESS = RetCode.mark(0, "success");

    /* system exception */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(102000, "system exception");

    /**
     * Business exception.
     */
    public static final RetCode CHAIN_ID_EXISTS = RetCode.mark(202001, "chain id already exists");

    public static final RetCode CHAIN_NAME_EXISTS =
            RetCode.mark(202002, "chain name already exists");

    public static final RetCode SAVE_CHAIN_FAIL = RetCode.mark(202003, "save chain fail");

    public static final RetCode INVALID_CHAIN_ID = RetCode.mark(202004, "invalid chain id");

    public static final RetCode INVALID_FRONT_ID = RetCode.mark(202005, "invalid front id");

    public static final RetCode DB_EXCEPTION = RetCode.mark(202006, "database exception");

    public static final RetCode FRONT_LIST_NOT_FOUNT = RetCode.mark(202007, "not found any front");

    public static final RetCode FRONT_EXISTS = RetCode.mark(202008, "front already exists");

    public static final RetCode INVALID_GROUP_ID = RetCode.mark(202009, "invalid group id");

    public static final RetCode SAVE_FRONT_FAIL = RetCode.mark(202010, "save front fail");

    public static final RetCode REQUEST_FRONT_FAIL = RetCode.mark(202011, "request front fail");

    public static final RetCode USER_NAME_EXISTS = RetCode.mark(202012, "user name already exists");

    public static final RetCode USER_ADDRESS_EXISTS =
            RetCode.mark(202013, "user address already exists");

    public static final RetCode CONTRACT_EXISTS = RetCode.mark(202014, "contract already exists");

    public static final RetCode INVALID_CONTRACT_ID = RetCode.mark(202015, "invalid contract id");

    public static final RetCode INVALID_PARAM_INFO = RetCode.mark(202016, "invalid param info");

    public static final RetCode CONTRACT_NAME_REPEAT =
            RetCode.mark(202017, "contract name cannot be repeated");

    public static final RetCode SERVER_CONNECT_FAIL = RetCode.mark(202018, "wrong host or port");

    public static final RetCode REQUEST_NODE_EXCEPTION =
            RetCode.mark(202019, "request node exception");

    public static final RetCode ENCRYPT_TYPE_NOT_MATCH =
            RetCode.mark(202020, "Front's encrypt type not matches");

    /* param exception */
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(302000, "param exception");

}
