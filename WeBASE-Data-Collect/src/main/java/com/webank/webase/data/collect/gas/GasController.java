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
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.MybatisExampleTools;
import com.webank.webase.data.collect.dao.entity.TbGas;
import com.webank.webase.data.collect.dao.entity.TbGasExample;
import com.webank.webase.data.collect.gas.entity.GasParam;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
        TbGasExample example = MybatisExampleTools.initSamplePageExample(TbGasExample.class,
                gasParam.getPageNumber(), gasParam.getPageSize(), tbGas);
        int count = gasService.getGasCount(example);
        pagesponse.setTotalCount(count);
        if (count > 0) {
            example.setOrderByClause(ConstantProperties.ORDER_BY_ID_DESC);
            List<TbGas> list = gasService.getGasList(example);
            pagesponse.setData(list);
        }

        log.info("end queryGasList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

}
