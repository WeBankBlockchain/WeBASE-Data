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
package com.webank.webase.data.collect.group;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.webank.webase.data.collect.group.entity.AppInfo;
import com.webank.webase.data.collect.group.entity.GroupGeneral;
import com.webank.webase.data.collect.group.entity.TbGroup;

/**
 * mapper for table tb_group.
 */
@Repository
public interface GroupMapper {
    
    int addList(List<TbGroup> list);

    /**
     * add group info
     */
    int save(TbGroup tbGroup);

    /**
     * update group application info
     */
    int updateAppInfo(AppInfo appInfo);

    /**
     * remove by id.
     */
    int remove(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId);

    /**
     * update status.
     */
    int updateStatus(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId,
            @Param("groupStatus") Integer groupStatus);

    /**
     * query group count.
     */
    int getCount(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId,
            @Param("groupStatus") Integer groupStatus);
    
    /**
     * query group by name.
     */
    List<TbGroup> getListByName(@Param("chainId") Integer chainId, @Param("name") String name);

    /**
     * get all group.
     */
    List<TbGroup> getList(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId,
            @Param("groupStatus") Integer groupStatus);

    /**
     * get all group by job.
     */
    List<TbGroup> getListByJob(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId,
            @Param("groupStatus") Integer groupStatus,
            @Param("shardingTotalCount") Integer shardingTotalCount,
            @Param("shardingItem") Integer shardingItem);

    /**
     * query general info.
     */
    GroupGeneral getGeneral(@Param("chainId") Integer chainId, @Param("groupId") Integer groupId);
}
