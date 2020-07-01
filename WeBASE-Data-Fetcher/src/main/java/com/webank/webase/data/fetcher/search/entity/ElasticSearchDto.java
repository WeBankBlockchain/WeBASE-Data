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
package com.webank.webase.data.fetcher.search.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "mysql02")
public class ElasticSearchDto {
    @Id
    private long id;
    private BigInteger blockNumber = BigInteger.ZERO;
    private String transHash;
    private String userName;
    private String userAddress;
    private Integer userType = 0;
    private String contractName;
    private String contractAddress;
    private String interfaceName;
    private Integer transType = 0;
    private Integer transParserType = 0;
    private String input;
    private String output;
    private String logs;
    private String transDetail;
    private String receiptDetail;
    private LocalDateTime blockTimestamp;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}
