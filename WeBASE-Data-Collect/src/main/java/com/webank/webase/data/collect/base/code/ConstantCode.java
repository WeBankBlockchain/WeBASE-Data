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
 * WeBASE-Data-Collect:09 <br/>
 * C: error code <br/>
 */
public class ConstantCode {

    /* return success */
    public static final RetCode SUCCESS = RetCode.mark(0, "success");

    /* common exception */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(109000, "system exception");
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(109001, "param exception");
    public static final RetCode DB_EXCEPTION = RetCode.mark(109002, "database exception");

    /* chain code */
    public static final RetCode CHAIN_ID_EXISTS = RetCode.mark(209001, "chain id already exists");
    public static final RetCode CHAIN_NAME_EXISTS =
            RetCode.mark(209002, "chain name already exists");
    public static final RetCode SAVE_CHAIN_FAIL = RetCode.mark(209003, "save chain fail");
    public static final RetCode INVALID_CHAIN_ID = RetCode.mark(209004, "invalid chain id");
    public static final RetCode INVALID_ENCRYPT_TYPE = RetCode.mark(209005, "invalid encrypt type");
    public static final RetCode CHAIN_ID_NOT_EXISTS = RetCode.mark(209006, "chain id not exists");

    /* front code */
    public static final RetCode SERVER_CONNECT_FAIL = RetCode.mark(209101, "wrong host or port");
    public static final RetCode INVALID_FRONT_ID = RetCode.mark(209102, "invalid front id");
    public static final RetCode FRONT_LIST_NOT_FOUNT = RetCode.mark(209103, "not found any front");
    public static final RetCode FRONT_EXISTS = RetCode.mark(209104, "front already exists");
    public static final RetCode SAVE_FRONT_FAIL = RetCode.mark(209105, "save front fail");
    public static final RetCode REQUEST_FRONT_FAIL = RetCode.mark(209106, "request front fail");
    public static final RetCode REQUEST_NODE_EXCEPTION =
            RetCode.mark(209107, "request node exception");
    public static final RetCode ENCRYPT_TYPE_NOT_MATCH =
            RetCode.mark(209108, "front's encrypt type not matches");
    public static final RetCode INVALID_BLOCK_NUMBER = RetCode.mark(209109, "invalid block number");
    public static final RetCode INVALID_NODE_ID = RetCode.mark(209110, "invalid node id");

    /* group code */
    public static final RetCode INVALID_GROUP_ID = RetCode.mark(209201, "invalid group id");

    /* user code */
    public static final RetCode USER_NAME_EXISTS = RetCode.mark(209301, "user name already exists");
    public static final RetCode USER_ADDRESS_EXISTS =
            RetCode.mark(209302, "user address already exists");

    /* contract code */
    public static final RetCode CONTRACT_EXISTS = RetCode.mark(209401, "contract already exists");
    public static final RetCode INVALID_CONTRACT_ID = RetCode.mark(209402, "invalid contract id");
    public static final RetCode CONTRACT_NAME_REPEAT =
            RetCode.mark(209403, "contract name cannot be repeated");
    public static final RetCode CONTRACT_NOT_EXISTS =
            RetCode.mark(209404, "contract name or address not exists");

    /* task code */
    public static final RetCode TASK_RUNNING = RetCode.mark(209501, "task is still running");
    public static final RetCode BLOCK_BEEN_RESET = RetCode.mark(209502, "block has been reset");

    /* upload solc js file */
    public static final RetCode SOLC_FILE_EMPTY =
            RetCode.mark(209601, "solc js file cannot be empty");
    public static final RetCode SOLC_EXISTS = RetCode.mark(209602, "solc js file already exist");
    public static final RetCode SOLC_NOT_EXISTS = RetCode.mark(209603, "solc js file not exist");
    public static final RetCode SAVE_SOLC_FILE_ERROR =
            RetCode.mark(209604, "save solc js file error");
    public static final RetCode READ_SOLC_FILE_ERROR =
            RetCode.mark(209605, "read solc js file error");
    
    /* rest code */
    public static final RetCode REMOTE_SERVER_REQUEST_FAIL = RetCode.mark(209701, "remote server request fail");
    
    /* event code */
    public static final RetCode EVENT_NOT_EXISTS = RetCode.mark(209851, "event not exists");
    public static final RetCode EVENT_EXISTS = RetCode.mark(209852, "event exists");
    public static final RetCode SAVE_EVENT_FAIL = RetCode.mark(209853, "save event info fail");
    public static final RetCode INVALID_TASK_STATUS = RetCode.mark(209854, "invalid task status");
    public static final RetCode EVENT_NAME_FORMAT_ERROR = RetCode.mark(209855, "event name format error");
    
    /* gas code */
    public static final RetCode GAS_RECORD_TYPE_NOT_EXISTS = RetCode.mark(209901, "gas record type not exists");
    public static final RetCode GAS_CONTRACT_NOT_INIT = RetCode.mark(209902, "gas charge manage precompiled contract not init");
    public static final RetCode GAS_RECONCILIATION_FALSE = RetCode.mark(209903, "gas reconciliation's config is false");
    
}
