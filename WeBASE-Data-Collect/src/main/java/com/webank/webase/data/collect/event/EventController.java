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
package com.webank.webase.data.collect.event;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.MybatisExampleTools;
import com.webank.webase.data.collect.dao.entity.TbEventExportTask;
import com.webank.webase.data.collect.dao.entity.TbEventExportTaskExample;
import com.webank.webase.data.collect.dao.entity.TbEventInfo;
import com.webank.webase.data.collect.dao.entity.TbEventInfoExample;
import com.webank.webase.data.collect.event.entity.EventExportInfo;
import com.webank.webase.data.collect.event.entity.EventExportUpdateParam;
import com.webank.webase.data.collect.event.entity.EventInfoParam;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
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
 * event controller
 */
@Log4j2
@RestController
@RequestMapping("event")
public class EventController extends BaseController {

    @Autowired
    private EventService eventService;

    /**
     * add a new exportInfo
     */
    @PostMapping("/export/add")
    public BaseResponse newExportInfo(@RequestBody @Valid EventExportInfo exportInfo,
            BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start newExportInfo.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbEventExportTask tbEventExportTask = eventService.newExportInfo(exportInfo);
        baseResponse.setData(tbEventExportTask);
        log.info("end newExportInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * query exportInfo list.
     */
    @GetMapping("/export/list/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryExportInfoList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "contractAddress", required = false) String contractAddress) {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryExportInfoList.");

        // param
        TbEventExportTaskExample example = MybatisExampleTools.initSamplePageExample(
                TbEventExportTaskExample.class, pageNumber, pageSize,
                new TbEventExportTask(chainId, groupId, contractAddress, null));
        int count = eventService.getExportInfoCount(example);
        pagesponse.setTotalCount(count);
        if (count > 0) {
            List<TbEventExportTask> list = eventService.getExportInfoList(example);
            pagesponse.setData(list);
        }

        log.info("end queryExportInfoList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * confirm a exportInfo
     */
    @PostMapping("/export/updateStatus")
    public BaseResponse updateExportStatus(@RequestBody @Valid EventExportUpdateParam param,
            BindingResult result) {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start updateExportStatus.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbEventExportTask tbEventExportTask = eventService.updateExportStatus(param.getId(), param.getStatus());
        baseResponse.setData(tbEventExportTask);
        log.info("end updateExportStatus useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * delete by exportInfoId
     */
    @GetMapping("/export/deleteById/{id}")
    public BaseResponse removeExportInfo(@PathVariable("id") Integer id) {
        Instant startTime = Instant.now();
        log.info("start removeExportInfo. id:{}", id);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);

        // remove
        eventService.removeExportInfo(id);

        log.info("end removeExportInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * query event info list.
     */
    @PostMapping("/info/list")
    public BasePageResponse queryEventInfoList(@RequestBody @Valid EventInfoParam eventInfoParam,
            BindingResult result) {
        checkBindResult(result);
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryEventInfoList.");

        TbEventExportTask tbEventExportTask = eventService.getExportInfoExample(
                eventInfoParam.getChainId(), eventInfoParam.getGroupId(),
                eventInfoParam.getContractAddress(), eventInfoParam.getEventName());
        if (tbEventExportTask == null) {
            throw new BaseException(ConstantCode.EVENT_NOT_EXISTS);
        }

        // param
        String tableName = TableName.EVENT.getEventInfoTableName(tbEventExportTask.getChainId(),
                tbEventExportTask.getGroupId(), tbEventExportTask.getId());
        TbEventInfoExample example = MybatisExampleTools.initSamplePageExample(
                TbEventInfoExample.class, eventInfoParam.getPageNumber(),
                eventInfoParam.getPageSize(), new TbEventInfo());
        int count = eventService.getEventInfoCount(tableName, example);
        pagesponse.setTotalCount(count);
        if (count > 0) {
            example.setOrderByClause(ConstantProperties.ORDER_BY_ID_DESC);
            List<TbEventInfo> list = eventService.getExportInfoList(tableName, example);
            pagesponse.setData(list);
        }

        log.info("end queryEventInfoList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

}
