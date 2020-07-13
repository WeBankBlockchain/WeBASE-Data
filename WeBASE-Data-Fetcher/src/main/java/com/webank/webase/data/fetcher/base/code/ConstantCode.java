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
package com.webank.webase.data.fetcher.base.code;

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

    /* group code */
    public static final RetCode INVALID_GROUP_ID = RetCode.mark(202101, "invalid group id");

    /* search code */
    public static final RetCode SEARCHTYPE_NOT_EXISTS =
            RetCode.mark(202201, "searchType not exists");
    public static final RetCode SEARCH_CONTENT_IS_EMPTY =
            RetCode.mark(202202, "search content can not be empty");
    public static final RetCode INDEX_NOT_EXISTS = RetCode.mark(202203, "search index not exists");
    public static final RetCode SEARCH_FAIL = RetCode.mark(202204, "search fail");

}
