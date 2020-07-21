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

import com.webank.webase.data.fetcher.base.controller.BaseController;
import com.webank.webase.data.fetcher.base.entity.BasePageResponse;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.search.entity.NormalSearchParam;
import java.time.Duration;
import java.time.Instant;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for processing group information.
 */
@Log4j2
@RestController
@RequestMapping("search")
public class SearchController extends BaseController {

    @Autowired
    private SearchService searchService;

    /**
     * normal search.
     */
    @PostMapping(value = "/normal")
    public BasePageResponse normalList(@RequestBody @Valid NormalSearchParam queryParam,
            BindingResult result) throws BaseException {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start normal search.");

        BasePageResponse pageResponse = searchService.normalList(queryParam);

        log.info("end normalList useTime:{}.",
                Duration.between(startTime, Instant.now()).toMillis());
        return pageResponse;
    }

    @GetMapping(value = "/keyword/{pageNumber}/{pageSize}")
    public BasePageResponse findByKey(@PathVariable Integer pageNumber,
            @PathVariable Integer pageSize,
            @RequestParam(value = "keyword", required = true) String keyword) {
        log.info("start keyword search. keyword:{}", keyword);
        return searchService.findByKey(pageNumber, pageSize, keyword);
    }
}
