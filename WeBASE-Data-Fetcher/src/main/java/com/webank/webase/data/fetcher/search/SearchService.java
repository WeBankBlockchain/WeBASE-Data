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
import com.webank.webase.data.fetcher.base.enums.SearchType;
import com.webank.webase.data.fetcher.base.enums.TableName;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.group.GroupService;
import com.webank.webase.data.fetcher.search.entity.NormalSearchDto;
import com.webank.webase.data.fetcher.search.entity.NormalSearchParam;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
    public BasePageResponse normalList(NormalSearchParam queryParam) throws BaseException {
        // check param
        checkParam(queryParam);

        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        int count = countOfNormal(queryParam);
        if (count > 0) {
            Integer pageNumber = Optional.ofNullable(queryParam.getPageNumber())
                    .map(page -> (page - 1) * queryParam.getPageSize()).orElse(null);
            queryParam.setPageNumber(pageNumber);
            List<NormalSearchDto> searchList = queryNormalList(queryParam);
            pageResponse.setData(searchList);
            pageResponse.setTotalCount(count);
        }
        return pageResponse;
    }

    /**
     * query count of search.
     */
    private int countOfNormal(NormalSearchParam queryParam) throws BaseException {
        try {
            Integer count = searchMapper.countOfNormal(
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
    private List<NormalSearchDto> queryNormalList(NormalSearchParam queryParam)
            throws BaseException {
        try {
            List<NormalSearchDto> listOfSearch = searchMapper.queryNormalList(
                    TableName.PARSER.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    TableName.TRANS.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    TableName.RECEIPT.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return listOfSearch;
        } catch (RuntimeException ex) {
            log.error("fail querySearchList queryParam:{}", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * checkParam.
     */
    private void checkParam(NormalSearchParam queryParam) throws BaseException {
        // check groupId
        groupService.checkGroupId(queryParam.getChainId(), queryParam.getGroupId());
        int searchType = queryParam.getSearchType();
        if (!SearchType.isInclude(searchType)) {
            log.error("fail checkParam queryParam:{}", queryParam);
            throw new BaseException(ConstantCode.SEARCHTYPE_NOT_EXISTS);
        }
        if (searchType == SearchType.BLOCK.getValue() & queryParam.getBlockNumber() == null) {
            throw new BaseException(ConstantCode.SEARCH_CONTENT_IS_EMPTY);
        }
        if (searchType == SearchType.TRANS.getValue() & StringUtils.isBlank(queryParam.getTransHash())) {
            throw new BaseException(ConstantCode.SEARCH_CONTENT_IS_EMPTY);
        }
        if (searchType == SearchType.USER.getValue() & StringUtils.isBlank(queryParam.getUserParam())) {
            throw new BaseException(ConstantCode.SEARCH_CONTENT_IS_EMPTY);
        }
        if (searchType == SearchType.CONTRACT.getValue() & StringUtils.isBlank(queryParam.getContractParam())) {
            throw new BaseException(ConstantCode.SEARCH_CONTENT_IS_EMPTY);
        }
    }
}
