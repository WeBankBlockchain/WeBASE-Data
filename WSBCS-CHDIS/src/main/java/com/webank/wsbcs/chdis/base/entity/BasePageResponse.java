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
package com.webank.wsbcs.chdis.base.entity;

import com.webank.wsbcs.chdis.base.code.RetCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity class of page response info.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BasePageResponse extends BaseResponse {

    private int totalCount;

    public BasePageResponse() {}

    public BasePageResponse(RetCode retcode, String bizSeqNo) {
        super(retcode, bizSeqNo);
    }
    public BasePageResponse(RetCode retcode, int totalCount, String bizSeqNo) {
        super(retcode, bizSeqNo);
        this.totalCount = totalCount;
    }

    public BasePageResponse(RetCode retcode, Object data, int totalCount, String bizSeqNo) {
        super(retcode, data, bizSeqNo);
        this.totalCount = totalCount;
    }
}
