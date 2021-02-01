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
package com.webank.webase.data.collect.receipt.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class of table tb_receipt
 */
@Data
@NoArgsConstructor
public class TbReceipt {

    private long id;
    private String transHash;
    private BigInteger blockNumber;
    private String receiptDetail;
    private LocalDateTime blockTimestamp;
    private Integer recordPatition;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

    public TbReceipt(String transHash, BigInteger blockNumber, String receiptDetail,
            LocalDateTime blockTimestamp, Integer recordPatition) {
        this.transHash = transHash;
        this.blockNumber = blockNumber;
        this.receiptDetail = receiptDetail;
        this.blockTimestamp = blockTimestamp;
        this.recordPatition = recordPatition;
    }
}
