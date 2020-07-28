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
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.validation.Valid;

import com.webank.webase.data.collect.keywords.entity.KeywordInfo;
import com.webank.webase.data.collect.keywords.entity.TbKeyword;
import com.webank.webase.data.collect.keywords.entity.UpdateKeywordInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * chain controller
 */
@Log4j2
@RestController
@RequestMapping("keywords")
public class KeywordsController extends BaseController {

    @Autowired
    private KeywordsService keywordsService;

    /**
     * add a new keyword
     */
    @PostMapping("/new")
    public BaseResponse newKeyword(@RequestBody @Valid KeywordInfo keywordInfo, BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start newKeyword.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbKeyword tbKeyword = keywordsService.newKeyword(keywordInfo);
        baseResponse.setData(tbKeyword);
        log.info("end newKeyword useTime:{}", Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * update a keyword
     */
    @PostMapping("/update")
    public BaseResponse updateKeyword(@RequestBody @Valid UpdateKeywordInfo updateKeywordInfo,
            BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start updateKeyword.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbKeyword tbKeyword = keywordsService.updateKeyword(updateKeywordInfo);
        baseResponse.setData(tbKeyword);
        log.info("end updateKeyword useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * query keyword list.
     */
    @GetMapping("/all")
    public BasePageResponse queryKeywordList() {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryKeywordList.");

        int count = keywordsService.getKeywordCount();
        pagesponse.setTotalCount(count);
        if (count > 0) {
            List<TbKeyword> list = keywordsService.getKeywordList();
            pagesponse.setData(list);
        }

        log.info("end queryKeywordList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * delete by keywordId
     */
    @DeleteMapping("/{keywordId}")
    public BaseResponse removeKeyword(@PathVariable("keywordId") Integer keywordId) {
        Instant startTime = Instant.now();
        log.info("start removeKeyword. keywordId:{}", keywordId);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);

        // remove
        keywordsService.removeKeyword(keywordId);

        log.info("end removeKeyword useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
}
