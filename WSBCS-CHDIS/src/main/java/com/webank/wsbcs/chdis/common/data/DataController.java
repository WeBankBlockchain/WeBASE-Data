package com.webank.wsbcs.chdis.common.data;
/**
 * Copyright 2014-2023 the original author or authors.
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.controller.BaseController;
import com.webank.wsbcs.chdis.base.entity.BasePageResponse;
import com.webank.wsbcs.chdis.base.entity.BaseResponse;
import com.webank.wsbcs.chdis.base.entity.data.ReqTransFlow;
import com.webank.wsbcs.chdis.base.entity.data.RspTrendChartAll;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.common.dao.entity.TbSearchCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * data controller
 */
@RestController
@RequestMapping("data")
@Api(value = "data")
public class DataController extends BaseController {

    @Autowired
    DataService dataService;

    @ApiOperation(value = "查询统计数据")
    @GetMapping("/general")
    public BaseResponse general(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        TbSearchCache result = dataService.general(bizSeqNo);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "查询普通用户列表（分页）")
    @GetMapping("/userList/{pageNumber}/{pageSize}")
    public BasePageResponse userList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userAddress", required = false) String userAddress,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        if (StringUtils.isNotBlank(userAddress)) {
            userAddress = userAddress.toLowerCase();
        }
        return dataService.userList(pageNumber, pageSize, userAddress, bizSeqNo);
    }

    @ApiOperation(value = "查询管理员列表（分页）")
    @GetMapping("/managerList/{pageNumber}/{pageSize}")
    public BasePageResponse managerList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userAddress", required = false) String userAddress,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        if (StringUtils.isNotBlank(userAddress)) {
            userAddress = userAddress.toLowerCase();
        }
        return dataService.managerList(pageNumber, pageSize, userAddress, bizSeqNo);
    }

    @ApiOperation(value = "查询用户交易流水（分页）")
    @GetMapping("/userFlowList/{pageNumber}/{pageSize}")
    public BasePageResponse userFlowList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userAddress", required = true) String userAddress,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "fromAddress", required = false) String fromAddress,
            @RequestParam(value = "toAddress", required = false) String toAddress,
            @RequestParam(value = "dateStart", required = false) String dateStart,
            @RequestParam(value = "dateEnd", required = false) String dateEnd,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        return dataService.userFlowList(pageNumber, pageSize, userAddress.toLowerCase(), type,
                fromAddress, toAddress, dateStart, dateEnd, bizSeqNo);
    }

    @ApiOperation(value = "查询发放和销毁流水（分页）")
    @GetMapping("/issueAndDestroyFlowList/{pageNumber}/{pageSize}")
    public BasePageResponse issueAndDestroyFlowList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userAddress", required = true) String userAddress,
            @RequestParam(value = "dateStart", required = false) String dateStart,
            @RequestParam(value = "dateEnd", required = false) String dateEnd,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        return dataService.issueAndDestroyFlowList(pageNumber, pageSize, userAddress.toLowerCase(),
                dateStart, dateEnd, bizSeqNo);
    }

    @ApiOperation(value = "查询同步的总发放量")
    @GetMapping("/totalSupplyFromSync")
    public BaseResponse totalSupplyFromSync(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        BaseResponse result = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        result.setData(dataService.totalSupplyFromSync(bizSeqNo));
        return result;
    }

    @ApiOperation(value = "查询用户余额")
    @GetMapping("/userBalance")
    public BaseResponse userBalance(@RequestParam(value = "type", required = true) Integer type,
            @RequestParam(value = "userAddress", required = true) String userAddress,
            @RequestParam(value = "searchValue", required = false) String searchValue,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        BaseResponse result = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        result.setData(
                dataService.userBalance(type, userAddress.toLowerCase(), searchValue, bizSeqNo));
        return result;
    }

    @ApiOperation(value = "查询历史数据")
    @GetMapping("/generalHistory")
    public BaseResponse generalHistory(@RequestParam(value = "type", required = true) Integer type,
            @RequestParam(value = "searchValue", required = true) String searchValue,
            @RequestParam(value = "resetEnable", required = false) Boolean resetEnable,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        TbSearchCache result = dataService.generalHistory(type, resetEnable, searchValue, bizSeqNo);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "按小时查询数据趋势图")
    @GetMapping("/trendChartByHour")
    public BaseResponse trendChartByHour(@RequestParam(value = "date", required = true) String date,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        RspTrendChartAll result = dataService.trendChartByHour(date, bizSeqNo);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "按天查询数据趋势图")
    @GetMapping("/trendChartByDay")
    public BaseResponse trendChartByDay(
            @RequestParam(value = "dateStart", required = true) String dateStart,
            @RequestParam(value = "dateEnd", required = true) String dateEnd,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        RspTrendChartAll result = dataService.trendChartByDay(dateStart, dateEnd, bizSeqNo);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "查询数据同步状态")
    @GetMapping("/isLoading")
    public BaseResponse isLoading(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        boolean result = dataService.checkStatus(bizSeqNo);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "查询充值订单记录（分页）")
    @GetMapping("/rechargeOrderList/{pageNumber}/{pageSize}")
    public BasePageResponse rechargeOrderList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userAddress", required = false) String userAddress,
            @RequestParam(value = "depositStatus", required = false) Boolean depositStatus,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        if (StringUtils.isNotBlank(userAddress)) {
            userAddress = userAddress.toLowerCase();
        }
        return dataService.rechargeOrderList(pageNumber, pageSize, userAddress, depositStatus,
                bizSeqNo);
    }

    @ApiOperation(value = "查询提现订单记录（分页）")
    @GetMapping("/withdrawOrderList/{pageNumber}/{pageSize}")
    public BasePageResponse withdrawOrderList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userAddress", required = false) String userAddress,
            @RequestParam(value = "withdrawStatus", required = false) Boolean withdrawStatus,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        if (StringUtils.isNotBlank(userAddress)) {
            userAddress = userAddress.toLowerCase();
        }
        return dataService.withdrawOrderList(pageNumber, pageSize, userAddress, withdrawStatus,
                bizSeqNo);
    }

    @ApiOperation(value = "查询卡信息（分页）")
    @GetMapping("/cardList/{pageNumber}/{pageSize}")
    public BasePageResponse cardList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "withdrawStatus", required = false) Boolean withdrawStatus,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        return dataService.cardList(pageNumber, pageSize, withdrawStatus, bizSeqNo);
    }

    @ApiOperation(value = "查询交易流水（分页）")
    @GetMapping("/transFlowList/{pageNumber}/{pageSize}")
    public BasePageResponse transFlowList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userAddress", required = true) String userAddress,
            @RequestParam(value = "txHash", required = false) String txHash,
            @RequestParam(value = "fromAddress", required = false) String fromAddress,
            @RequestParam(value = "fromAddressNE", required = false) String fromAddressNE,
            @RequestParam(value = "toAddress", required = false) String toAddress,
            @RequestParam(value = "transAddress", required = false) String transAddress,
            @RequestParam(value = "timestamp", required = false) Long timestamp,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        return dataService.transFlowList(pageNumber, pageSize, userAddress.toLowerCase(), txHash,
                fromAddress == null ? null : fromAddress.toLowerCase(),
                fromAddressNE == null ? null : fromAddressNE.toLowerCase(),
                toAddress == null ? null : toAddress.toLowerCase(),
                transAddress == null ? null : transAddress.toLowerCase(), timestamp, startTime,
                endTime, bizSeqNo);
    }

    @ApiOperation(value = "根据交易哈希查询流水（批量）")
    @PostMapping("/transFlowByTxHashBatch")
    public BasePageResponse transFlowByTxHashBatch(@RequestBody ReqTransFlow req,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        return dataService.transFlowByTxHashBatch(req.getTxHashList(), bizSeqNo);
    }

    @ApiOperation(value = "查询交易统计数据")
    @GetMapping("/transTotal")
    public BaseResponse transTotal(
            @RequestParam(value = "userAddress", required = true) String userAddress,
            @RequestParam(value = "txHash", required = false) String txHash,
            @RequestParam(value = "fromAddress", required = false) String fromAddress,
            @RequestParam(value = "fromAddressNE", required = false) String fromAddressNE,
            @RequestParam(value = "toAddress", required = false) String toAddress,
            @RequestParam(value = "transAddress", required = false) String transAddress,
            @RequestParam(value = "timestamp", required = false) Long timestamp,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        return dataService.transTotal(userAddress.toLowerCase(), txHash,
                fromAddress == null ? null : fromAddress.toLowerCase(),
                fromAddressNE == null ? null : fromAddressNE.toLowerCase(),
                toAddress == null ? null : toAddress.toLowerCase(),
                transAddress == null ? null : transAddress.toLowerCase(), timestamp, startTime,
                endTime, bizSeqNo);
    }
}
