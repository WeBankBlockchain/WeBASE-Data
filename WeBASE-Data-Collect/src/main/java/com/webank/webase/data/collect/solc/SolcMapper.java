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
package com.webank.webase.data.collect.solc;

import com.webank.webase.data.collect.solc.entity.TbSolc;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * solc data interface.
 */
@Repository
public interface SolcMapper {

    int save(TbSolc tbSolc);

    List<TbSolc> getAll(@Param("encryptType") Integer encryptType);

    TbSolc findById(@Param("id") Integer id);

    TbSolc findBySolcName(@Param("fileName") String fileName);

    TbSolc findByMd5(@Param("md5") String md5);

    void delete(@Param("id") Integer id);
}
