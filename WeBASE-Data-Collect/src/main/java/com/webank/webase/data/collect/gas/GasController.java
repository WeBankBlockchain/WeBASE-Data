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
package com.webank.webase.data.collect.gas;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.enums.GasRecordType;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.MybatisExampleTools;
import com.webank.webase.data.collect.dao.entity.TbGas;
import com.webank.webase.data.collect.dao.entity.TbGasExample;
import com.webank.webase.data.collect.dao.entity.TbGasReconciliation;
import com.webank.webase.data.collect.dao.entity.TbGasReconciliationExample;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.gas.entity.GasParam;
import com.webank.webase.data.collect.gas.entity.GasReconciliationParam;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * event controller
 */
@Log4j2
@RestController
@RequestMapping("gas")
public class GasController extends BaseController {

    @Autowired
    private GasService gasService;
    @Autowired
    private GasReconciliationService gasReconciliationService;
    @Autowired
    private FrontInterfaceService frontInterfaceService;
    @Autowired
    private ConstantProperties cProperties;

    /**
     * query gas list.
     */
    @PostMapping("/list")
    public BasePageResponse queryGasList(@RequestBody @Valid GasParam gasParam,
            BindingResult result) {
        checkBindResult(result);
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryGasList.");

        // param
        TbGas tbGas = new TbGas();
        BeanUtils.copyProperties(gasParam, tbGas);
        if (gasParam.getRecordType() != null) {
            if (!GasRecordType.isInclude(gasParam.getRecordType())) {
                log.error("gas record type:{} not exists.", gasParam.getRecordType());
                throw new BaseException(ConstantCode.GAS_RECORD_TYPE_NOT_EXISTS);
            }
            tbGas.setRecordType(gasParam.getRecordType().byteValue());
        }
        TbGasExample example = MybatisExampleTools.initSamplePageExample(TbGasExample.class,
                gasParam.getPageNumber(), gasParam.getPageSize(), tbGas);
        int count = gasService.getGasCount(example);
        pagesponse.setTotalCount(count);
        if (count > 0) {
            example.setOrderByClause(ConstantProperties.ORDER_BY_BLOCK_NUMBER_ASC);
            List<TbGas> list = gasService.getGasList(example);
            pagesponse.setData(list);
        }

        log.info("end queryGasList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }


    /**
     * query gas reconciliation list.
     */
    @PostMapping("/reconciliationlist")
    public BasePageResponse queryGasReconciliationList(
            @RequestBody @Valid GasReconciliationParam param, BindingResult result) {
        checkBindResult(result);
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryGasReconciliationList.");
        
        if (!cProperties.isIfGasReconciliation()) {
            log.error("gas reconciliation's config is false.");
            throw new BaseException(ConstantCode.GAS_RECONCILIATION_FALSE);
        }

        // param
        TbGasReconciliation tbGasReconciliation = new TbGasReconciliation();
        BeanUtils.copyProperties(param, tbGasReconciliation);
        TbGasReconciliationExample example =
                MybatisExampleTools.initSamplePageExample(TbGasReconciliationExample.class,
                        param.getPageNumber(), param.getPageSize(), tbGasReconciliation);
        int count = gasReconciliationService.getCount(example);
        pagesponse.setTotalCount(count);
        if (count > 0) {
            List<TbGasReconciliation> list = gasReconciliationService.selectByExample(example);
            pagesponse.setData(list);
        }

        log.info("end queryGasReconciliationList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * query gas remain.
     */
    @GetMapping("/queryRemain/{chainId}/{groupId}/{userAddress}")
    public BaseResponse queryRemain(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("userAddress") String userAddress) {
        BaseResponse pagesponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryRemain.");
        BigInteger gasRemain = frontInterfaceService.getGasRemain(chainId, groupId, userAddress);
        pagesponse.setData(gasRemain);
        log.info("end queryRemain useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

}
