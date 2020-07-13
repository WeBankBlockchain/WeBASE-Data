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
package com.webank.webase.data.fetcher.search;

import com.webank.webase.data.fetcher.search.entity.SearchDto;
import com.webank.webase.data.fetcher.search.entity.SearchListParam;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * mapper for search info.
 */
@Repository
public interface SearchMapper {

    /**
     * query search count.
     */
    Integer countOfNormal(@Param("tableParser") String tableParser,
            @Param("tableBlock") String tableBlock, @Param("param") SearchListParam param);

    /**
     * query list of search by page.
     */
    List<SearchDto> queryNormalList(@Param("tableParser") String tableParser,
            @Param("tableBlock") String tableBlock, @Param("tableTrans") String tableTrans,
            @Param("tableReceipt") String tableReceipt, @Param("param") SearchListParam param);

}
