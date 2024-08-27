package com.webank.wsbcs.chdis.common.config;
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

import java.math.BigInteger;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.controller.BaseController;
import com.webank.wsbcs.chdis.base.entity.BaseResponse;
import com.webank.wsbcs.chdis.base.entity.chain.AbiInfo;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.common.method.MethodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * config controller
 */
@RestController
@RequestMapping("config")
@Api(value = "config")
public class ConfigController extends BaseController {

    @Autowired
    private ConfigService configService;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private MethodService methodService;

    @ApiOperation(value = "重置数据，重新解析")
    @GetMapping("/resetData")
    public BaseResponse resetData(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        configService.resetData(bizSeqNo);
        return BaseResponse.success(null, bizSeqNo);
    }

    @ApiOperation(value = "从某个区块开始重新解析")
    @GetMapping("/resetDataFromBlockNumber")
    public BaseResponse resetDataFromBlockNumber(
            @RequestParam(value = "blockNumber", required = true) BigInteger blockNumber,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        configService.resetDataFromBlockNumber(blockNumber, bizSeqNo);
        return BaseResponse.success(null, bizSeqNo);
    }

    /**
     * add method info.
     */
    @ApiOperation(value = "新增方法ABI")
    @PostMapping(value = "/addMethod")
    public BaseResponse addMethod(@RequestBody @Valid AbiInfo abiInfo, BindingResult result,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo) {
        checkBindResult(result, bizSeqNo);
        String chainId = cProperties.getDefaultChainId();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        methodService.saveMethodFromAbiV3(chainId,
                chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], abiInfo.getAbiInfo(),
                bizSeqNo);
        return baseResponse;
    }

    /**
     * init method info.
     */
    @ApiOperation(value = "初始化方法ABI")
    @PostMapping(value = "/initMethod")
    public BaseResponse initMethod(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        methodService.initMethod();
        return baseResponse;
    }
}
