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
package com.webank.webase.data.collect.contract;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.webank.webase.data.collect.contract.entity.ContractParam;
import com.webank.webase.data.collect.contract.entity.TbContract;

/**
 * contract data interface.
 */
@Repository
public interface ContractMapper {
    
    int addList(List<TbContract> list);

    Integer add(TbContract tbContract);

    Integer update(TbContract tbContract);

    Integer countOfContract(ContractParam param);

    List<TbContract> listOfContract(ContractParam param);

    TbContract queryByContractId(@Param("contractId") Integer contractId);

    TbContract queryContract(ContractParam queryParam);

    List<TbContract> queryContractByBin(@Param("chainId") Integer chainId,
            @Param("groupId") Integer groupId, @Param("runtimeBin") String runtimeBin);

    Integer remove(@Param("contractId") Integer contractId);

    Integer removeByChainId(@Param("chainId") Integer chainId);

    Integer removeByGroupId(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId);
}
