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
package com.webank.webase.data.fetcher.audit.controller;

import com.webank.webase.data.fetcher.audit.entity.AuditQueryParam;
import com.webank.webase.data.fetcher.audit.entity.TbTransAudit;
import com.webank.webase.data.fetcher.audit.entity.TransAuditInfo;
import com.webank.webase.data.fetcher.audit.service.TransAuditService;
import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.controller.BaseController;
import com.webank.webase.data.fetcher.base.entity.BasePageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * audit controller
 */
@Log4j2
@RestController
@RequestMapping("transAudit")
public class TransAuditController extends BaseController {

    @Autowired
    private TransAuditService transAuditService;

    /**
     * add a new auditInfo
     */
    @PostMapping("/add")
    public BaseResponse newAuditInfo(@RequestBody @Valid TransAuditInfo auditInfo,
            BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start newAuditInfo.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbTransAudit tbTransAudit = transAuditService.newAuditInfo(auditInfo);
        baseResponse.setData(tbTransAudit);
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
        TbTransAudit tbTransAudit = transAuditService.confirmAuditInfo(id);
        baseResponse.setData(tbTransAudit);
        log.info("end confirmAuditInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * query auditInfo list.
     */
    @GetMapping("/list/{pageNumber}/{pageSize}")
    public BasePageResponse queryAuditInfoList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "chainId", required = false) Integer chainId,
            @RequestParam(value = "groupId", required = false) Integer groupId) {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryAuditInfoList.");

        // param
        AuditQueryParam queryParam = new AuditQueryParam();
        queryParam.setChainId(chainId);
        queryParam.setGroupId(groupId);
        queryParam.setStatus(status);
        int count = transAuditService.getAuditInfoCount(queryParam);
        pagesponse.setTotalCount(count);
        if (count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            List<TbTransAudit> list = transAuditService.getAuditInfoList(queryParam);
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
        transAuditService.removeAuditInfo(id);

        log.info("end removeAuditInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

}
