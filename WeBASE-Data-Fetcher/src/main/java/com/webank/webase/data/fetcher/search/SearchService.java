/**
 * Copyright 2014-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.data.fetcher.search;

import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.entity.BasePageResponse;
import com.webank.webase.data.fetcher.base.enums.TableName;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.group.GroupService;
import com.webank.webase.data.fetcher.search.entity.NormalSearchDto;
import com.webank.webase.data.fetcher.search.entity.NormalSearchParam;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * services for group data.
 */
@Log4j2
@Service
public class SearchService {

    @Autowired
    private GroupService groupService;
    @Autowired
    private SearchMapper searchMapper;

    /**
     * query count of search.
     */
    public BasePageResponse searchList(NormalSearchParam queryParam) throws BaseException {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        // check groupId
        groupService.checkGroupId(queryParam.getChainId(), queryParam.getGroupId());
        // check param
//        checkParam(queryParam);

        int count = countOfSearch(queryParam);
        if (count > 0) {
            Integer pageSize = queryParam.getPageSize();
            Integer start = Optional.ofNullable(queryParam.getPageNumber()).map(page -> (page - 1) * pageSize).orElse(null);
//            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            List<NormalSearchDto> searchList = querySearchList(queryParam);
            pageResponse.setData(searchList);
            pageResponse.setTotalCount(count);
        }
        return pageResponse;
    }

    /**
     * query count of search.
     */
    private int countOfSearch(NormalSearchParam queryParam) throws BaseException {
        try {
            Integer count = searchMapper.countOfSearch(
                    TableName.PARSER.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail countOfSearch queryParam:{} ", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query search info list.
     */
    private List<NormalSearchDto> querySearchList(NormalSearchParam queryParam)
            throws BaseException {
        try {
            List<NormalSearchDto> listOfSearch = searchMapper.querySearchList(
                    TableName.PARSER.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return listOfSearch;
        } catch (RuntimeException ex) {
            log.error("fail querySearchList queryParam:{}", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }
}
