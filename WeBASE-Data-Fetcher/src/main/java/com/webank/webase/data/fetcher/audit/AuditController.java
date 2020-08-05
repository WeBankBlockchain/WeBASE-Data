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
package com.webank.webase.data.fetcher.audit;

import com.webank.webase.data.fetcher.audit.entity.AuditInfo;
import com.webank.webase.data.fetcher.audit.entity.TbAuditInfo;
import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.controller.BaseController;
import com.webank.webase.data.fetcher.base.entity.BasePageResponse;
import com.webank.webase.data.fetcher.base.entity.BaseQueryParam;
import com.webank.webase.data.fetcher.base.entity.BaseResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
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
 * audit controller
 */
@Log4j2
@RestController
@RequestMapping("audit")
public class AuditController extends BaseController {

    @Autowired
    private AuditService auditService;

    /**
     * add a new auditInfo
     */
    @PostMapping("/add")
    public BaseResponse newAuditInfo(@RequestBody @Valid AuditInfo auditInfo,
            BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start newAuditInfo.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbAuditInfo tbAuditInfo = auditService.newAuditInfo(auditInfo);
        baseResponse.setData(tbAuditInfo);
        log.info("end newAuditInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * confirm a auditInfo
     */
    @PostMapping("/confirm/{id}")
    public BaseResponse confirmAuditInfo(@PathVariable("id") Integer id) {
        Instant startTime = Instant.now();
        log.info("start confirmAuditInfo.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbAuditInfo tbAuditInfo = auditService.confirmAuditInfo(id);
        baseResponse.setData(tbAuditInfo);
        log.info("end confirmAuditInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * query auditInfo list.
     */
    @GetMapping("/list/{pageNumber}/{pageSize}")
    public BasePageResponse queryAuditInfoList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize) {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryAuditInfoList.");

        // param
        int count = auditService.getAuditInfoCount();
        pagesponse.setTotalCount(count);
        if (count > 0) {
            BaseQueryParam queryParam = new BaseQueryParam();
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            List<TbAuditInfo> list = auditService.getAuditInfoList(queryParam);
            pagesponse.setData(list);
        }

        log.info("end queryAuditInfoList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * delete by auditInfoId
     */
    @DeleteMapping("/{id}")
    public BaseResponse removeAuditInfo(@PathVariable("id") Integer id) {
        Instant startTime = Instant.now();
        log.info("start removeAuditInfo. id:{}", id);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);

        // remove
        auditService.removeAuditInfo(id);

        log.info("end removeAuditInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

}
