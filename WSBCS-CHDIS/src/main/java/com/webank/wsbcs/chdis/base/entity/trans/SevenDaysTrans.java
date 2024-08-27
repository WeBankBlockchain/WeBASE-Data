/**
 * Copyright 2014-2021  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.wsbcs.chdis.base.entity.trans;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.webank.wsbcs.chdis.base.util.CommonUtils;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.common.dao.entity.TbTransDaily;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class of Trading within seven days.
 */
@Data
@NoArgsConstructor
public class SevenDaysTrans {

    @JsonFormat(pattern = JsonUtils.DEFAULT_DATE_FORMAT)
    private LocalDate transDay;
    private String chainId;
    private Long transCount;
    private Long blockNumber;

    public SevenDaysTrans(TbTransDaily tbTransDaily) {
        this.transCount = tbTransDaily.getTransCount().longValue();
        this.transDay = CommonUtils.convert2LocalDate(tbTransDaily.getTransDay());
        this.chainId = tbTransDaily.getChainId();
        this.blockNumber = Long.valueOf(tbTransDaily.getBlockNumber());
    }
}
