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
package com.webank.webase.data.collect.contract.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class of table tb_contract.
 */
@Data
@NoArgsConstructor
public class TbContract {
    private Integer contractId;
    private Integer chainId;
    private Integer groupId;
    private String contractPath;
    private String contractName;
    private String contractSource;
    private String contractAbi;
    @JsonProperty(value = "contractBin")
    private String runtimeBin;
    private String bytecodeBin;
    private Integer contractType;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

    public TbContract(Integer contractId, String contractName, Integer chainId, Integer groupId) {
        super();
        this.contractId = contractId;
        this.contractName = contractName;
        this.chainId = chainId;
        this.groupId = groupId;
    }
}
