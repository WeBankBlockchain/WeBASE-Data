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
package com.webank.webase.data.fetcher.keyword;

import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.entity.BaseQueryParam;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.keyword.entity.KeywordInfo;
import com.webank.webase.data.fetcher.keyword.entity.TbKeyword;
import com.webank.webase.data.fetcher.keyword.entity.UpdateKeywordInfo;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service of Keyword.
 */
@Log4j2
@Service
public class KeywordService {

    @Autowired
    private KeywordMapper keywordMapper;

    /**
     * add a new keyword
     */
    public TbKeyword newKeyword(KeywordInfo keywordInfo) {
        log.debug("start newKeyword keywordInfo:{}", keywordInfo);

        TbKeyword tbKeyword = getKeywordByKeyword(keywordInfo.getKeyword());
        if (tbKeyword != null) {
            throw new BaseException(ConstantCode.KEYWORD_EXISTS);
        }
        // copy attribute
        TbKeyword tbKeyword1 = new TbKeyword();
        BeanUtils.copyProperties(keywordInfo, tbKeyword1);
        // save keyword info
        int result = keywordMapper.add(tbKeyword1);
        if (result == 0) {
            log.warn("fail keyword after save.");
            throw new BaseException(ConstantCode.SAVE_KEYWORD_FAIL);
        }
        return getKeywordByKeyword(keywordInfo.getKeyword());
    }

    /**
     * update keyword
     */
    public TbKeyword updateKeyword(UpdateKeywordInfo updateKeywordInfo) {
        log.debug("start updateKeyword updateKeywordInfo:{}", updateKeywordInfo);

        if (!checkKeywordId(updateKeywordInfo.getId())) {
            throw new BaseException(ConstantCode.KEYWORD_ID_NOT_EXISTS);
        }

        TbKeyword tbKeyword = new TbKeyword();
        BeanUtils.copyProperties(updateKeywordInfo, tbKeyword);

        keywordMapper.update(tbKeyword);
        return getKeywordById(tbKeyword.getId());
    }

    /**
     * get keyword count
     */
    public int getKeywordCount() {
        Integer count = keywordMapper.getCount();
        return count == null ? 0 : count;
    }

    /**
     * get keyword list
     */
    public List<TbKeyword> getKeywordList(BaseQueryParam queryParam) {
        return keywordMapper.getList(queryParam);
    }

    /**
     * get keyword info
     */
    public TbKeyword getKeywordById(Integer id) {
        return keywordMapper.getKeywordById(id);
    }

    /**
     * get keyword info
     */
    public TbKeyword getKeywordByKeyword(String keyword) {
        return keywordMapper.getKeywordByKeyword(keyword);
    }

    /**
     * remove keyword
     */
    @Transactional
    public void removeKeyword(Integer id) {
        TbKeyword tbKeyword = getKeywordById(id);
        if (tbKeyword == null) {
            throw new BaseException(ConstantCode.KEYWORD_ID_NOT_EXISTS);
        }
        keywordMapper.remove(id);
    }

    /**
     * check keyword id
     */
    private boolean checkKeywordId(Integer id) {
        TbKeyword tbKeyword = getKeywordById(id);
        if (tbKeyword == null) {
            return false;
        }
        return true;
    }
}
