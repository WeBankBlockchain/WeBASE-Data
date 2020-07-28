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
package com.webank.webase.data.collect.keywords;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.keywords.entity.KeywordInfo;
import com.webank.webase.data.collect.keywords.entity.TbKeyword;

import java.util.List;

import com.webank.webase.data.collect.keywords.entity.UpdateKeywordInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service of Keywords.
 */
@Log4j2
@Service
public class KeywordsService {

    @Autowired
    private KeywordsMapper keywordsMapper;

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
        TbKeyword tbChain = new TbKeyword();
        BeanUtils.copyProperties(keywordInfo, tbChain);
        // save keyword info
        int result = keywordsMapper.add(tbChain);
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

        keywordsMapper.update(tbKeyword);
        return getKeywordById(tbKeyword.getId());
    }

    /**
     * get keyword count
     */
    public int getKeywordCount() {
        Integer count = keywordsMapper.getCount();
        return count == null ? 0 : count;
    }

    /**
     * get keyword list
     */
    public List<TbKeyword> getKeywordList() {
        return keywordsMapper.getList();
    }

    /**
     * get keyword info
     */
    public TbKeyword getKeywordById(Integer keywordId) {
        return keywordsMapper.getKeywordById(keywordId);
    }

    /**
     * get keyword info
     */
    public TbKeyword getKeywordByKeyword(String keyword) {
        return keywordsMapper.getKeywordByKeyword(keyword);
    }

    /**
     * remove keyword
     */
    @Transactional
    public void removeKeyword(Integer keywordId) {

        TbKeyword tbKeyword = getKeywordById(keywordId);
        if (tbKeyword == null) {
            throw new BaseException(ConstantCode.INVALID_KEYWORD_ID);
        }
        keywordsMapper.remove(keywordId);
    }
    
    /**
     * check keyword id
     */
    private boolean checkKeywordId(Integer keywordId) {
        TbKeyword tbKeyword = getKeywordById(keywordId);
        if (tbKeyword == null) {
            return false;
        }
        return true;
    }
}
