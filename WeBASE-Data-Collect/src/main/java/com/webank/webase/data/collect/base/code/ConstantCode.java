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
    
    /* common exception */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(102000, "system exception");
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(102001, "param exception");
    public static final RetCode DB_EXCEPTION = RetCode.mark(102002, "database exception");
    
    /* chain code*/
    public static final RetCode CHAIN_ID_EXISTS = RetCode.mark(202001, "chain id already exists");
    public static final RetCode CHAIN_NAME_EXISTS = RetCode.mark(202002, "chain name already exists");
    public static final RetCode SAVE_CHAIN_FAIL = RetCode.mark(202003, "save chain fail");
    public static final RetCode INVALID_CHAIN_ID = RetCode.mark(202004, "invalid chain id");
    
    /* front code*/
    public static final RetCode SERVER_CONNECT_FAIL = RetCode.mark(202101, "wrong host or port");
    public static final RetCode INVALID_FRONT_ID = RetCode.mark(202102, "invalid front id");
    public static final RetCode FRONT_LIST_NOT_FOUNT = RetCode.mark(202103, "not found any front");
    public static final RetCode FRONT_EXISTS = RetCode.mark(202104, "front already exists");
    public static final RetCode SAVE_FRONT_FAIL = RetCode.mark(202105, "save front fail");
    public static final RetCode REQUEST_FRONT_FAIL = RetCode.mark(202106, "request front fail");
    public static final RetCode REQUEST_NODE_EXCEPTION = RetCode.mark(202107, "request node exception");
    public static final RetCode ENCRYPT_TYPE_NOT_MATCH = RetCode.mark(202108, "front's encrypt type not matches");
    public static final RetCode INVALID_BLOCK_NUMBER = RetCode.mark(202109, "invalid block number");
    
    /* group code*/
    public static final RetCode INVALID_GROUP_ID = RetCode.mark(202201, "invalid group id");
    
    /* user code*/
    public static final RetCode USER_NAME_EXISTS = RetCode.mark(202301, "user name already exists");
    public static final RetCode USER_ADDRESS_EXISTS = RetCode.mark(202302, "user address already exists");
    
    /* contract code*/
    public static final RetCode CONTRACT_EXISTS = RetCode.mark(202401, "contract already exists");
    public static final RetCode INVALID_CONTRACT_ID = RetCode.mark(202402, "invalid contract id");
    public static final RetCode INVALID_PARAM_INFO = RetCode.mark(202403, "invalid param info");
    public static final RetCode CONTRACT_NAME_REPEAT = RetCode.mark(202404, "contract name cannot be repeated");
    
    /* task code*/
    public static final RetCode TASK_RUNNING = RetCode.mark(202501, "task is still running");
    public static final RetCode BLOCK_BEEN_RESET = RetCode.mark(202502, "block has been reset");
    
    /* upload solc js file*/
    public static final RetCode SOLC_FILE_EMPTY = RetCode.mark(202601, "solc js file cannot be empty");
    public static final RetCode SOLC_EXISTS = RetCode.mark(202602, "solc js file already exist");
    public static final RetCode SOLC_NOT_EXISTS = RetCode.mark(202603, "solc js file not exist");
    public static final RetCode SAVE_SOLC_FILE_ERROR = RetCode.mark(202604, "save solc js file error");
    public static final RetCode READ_SOLC_FILE_ERROR = RetCode.mark(202605, "read solc js file error");
}
