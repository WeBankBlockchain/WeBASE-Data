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
 * WeBASE-Data-Fetcher:10 <br/>
 * C: error code <br/>
 */
public class ConstantCode {

    /* return success */
    public static final RetCode SUCCESS = RetCode.mark(0, "success");

    /* common exception */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(110000, "system exception");
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(110001, "param exception");
    public static final RetCode DB_EXCEPTION = RetCode.mark(110002, "database exception");

    /* group code */
    public static final RetCode INVALID_GROUP_ID = RetCode.mark(210101, "invalid group id");

    /* search code */
    public static final RetCode SEARCHTYPE_NOT_EXISTS =
            RetCode.mark(210201, "searchType not exists");
    public static final RetCode SEARCH_CONTENT_IS_EMPTY =
            RetCode.mark(210202, "search content can not be empty");
    public static final RetCode INDEX_NOT_EXISTS = RetCode.mark(210203, "search index not exists");
    public static final RetCode SEARCH_FAIL = RetCode.mark(210204, "search fail");

    /* keyword code */
    public static final RetCode KEYWORD_ID_NOT_EXISTS = RetCode.mark(210301, "keyword id not exists");
    public static final RetCode KEYWORD_EXISTS = RetCode.mark(210302, "keyword exists");
    public static final RetCode SAVE_KEYWORD_FAIL = RetCode.mark(210303, "save keyword fail");
    
    /* audit code */
    public static final RetCode AUDIT_ID_NOT_EXISTS = RetCode.mark(210401, "audit id not exists");
    public static final RetCode AUDIT_EXISTS = RetCode.mark(210402, "audit info exists");
    public static final RetCode SAVE_AUDIT_FAIL = RetCode.mark(210403, "save audit info fail");
    public static final RetCode INVALID_AUDIT_TYPE = RetCode.mark(210404, "invalid audit type");
    public static final RetCode KEYWORD_ID_EMPTY = RetCode.mark(210405, "keyword can not be empty");
}
