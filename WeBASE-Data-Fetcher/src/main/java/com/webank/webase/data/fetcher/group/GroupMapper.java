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
package com.webank.webase.data.fetcher.group;

import com.webank.webase.data.fetcher.base.entity.BaseQueryParam;
import com.webank.webase.data.fetcher.group.entity.BlockInfoDto;
import com.webank.webase.data.fetcher.group.entity.BlockListParam;
import com.webank.webase.data.fetcher.group.entity.ContractInfoDto;
import com.webank.webase.data.fetcher.group.entity.GroupGeneral;
import com.webank.webase.data.fetcher.group.entity.GroupInfoDto;
import com.webank.webase.data.fetcher.group.entity.NodeInfoDto;
import com.webank.webase.data.fetcher.group.entity.OrgInfoDto;
import com.webank.webase.data.fetcher.group.entity.TransListParam;
import com.webank.webase.data.fetcher.group.entity.TransactionInfoDto;
import com.webank.webase.data.fetcher.group.entity.TxnDailyDto;
import com.webank.webase.data.fetcher.group.entity.UserInfoDto;
import java.math.BigInteger;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * mapper for group info.
 */
@Repository
public interface GroupMapper {

    /**
     * query group count.
     */
    Integer countOfGroup(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId,
            @Param("groupStatus") Integer groupStatus);

    /**
     * get all group.
     */
    List<GroupInfoDto> getGroupList(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId,
            @Param("groupStatus") Integer groupStatus);

    /**
     * query general info.
     */
    GroupGeneral getGeneral(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId);
    
    /**
     * query all block counts.
     */
    BigInteger getBlockCounts();
    
    /**
     * query all transaction counts.
     */
    BigInteger getTxnCounts();

    /**
     * listSeventDayOfTransDaily.
     */
    List<TxnDailyDto> listSeventDayOfTransDaily(@Param(value = "chainId") Integer chainId,
            @Param("groupId") int groupId);

    /**
     * Query the number of node according to some conditions.
     */
    Integer countOfNode(BaseQueryParam param);


    /**
     * Query node list according to some conditions.
     */
    List<NodeInfoDto> queryNodeList(BaseQueryParam param);
    
    /**
     * Query the number of org node according to some conditions.
     */
    Integer getOrgNodeCount(BaseQueryParam param);
    
    /**
     * Query org node list according to some conditions.
     */
    List<OrgInfoDto> getOrgNodeList(BaseQueryParam param);

    /**
     * query block count.
     */
    Integer countOfBlock(@Param("tableName") String tableName,
            @Param("param") BlockListParam param);

    /**
     * query list of block by page.
     */
    List<BlockInfoDto> queryBlockList(@Param("tableName") String tableName,
            @Param("param") BlockListParam param);

    /**
     * query trans count.
     */
    Integer countOfTrans(@Param("tableName") String tableName,
            @Param("param") TransListParam param);

    /**
     * query list of trans by page.
     */
    List<TransactionInfoDto> queryTransList(@Param("tableTrans") String tableTrans,
            @Param("tableReceipt") String tableReceipt,
            @Param("param") TransListParam param);

    /**
     * query user count.
     */
    Integer countOfUser(@Param("tableName") String tableName, @Param("param") BaseQueryParam param);

    /**
     * query list of user by page.
     */
    List<UserInfoDto> queryUserList(@Param("tableName") String tableName,
            @Param("param") BaseQueryParam param);

    /**
     * query contract count.
     */
    Integer countOfContract(@Param("tableName") String tableName,
            @Param("param") BaseQueryParam param);

    /**
     * query list of contract by page.
     */
    List<ContractInfoDto> queryContractList(@Param("tableName") String tableName,
            @Param("param") BaseQueryParam param);
}
