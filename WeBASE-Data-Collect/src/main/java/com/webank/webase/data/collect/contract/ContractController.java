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
package com.webank.webase.data.collect.contract;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.enums.SqlSortType;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.contract.entity.Contract;
import com.webank.webase.data.collect.contract.entity.ContractParam;
import com.webank.webase.data.collect.contract.entity.NewMethodInput;
import com.webank.webase.data.collect.contract.entity.QueryContractParam;
import com.webank.webase.data.collect.contract.entity.TbContract;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("contract")
public class ContractController extends BaseController {

    @Autowired
    private ContractService contractService;
    @Autowired
    private MethodService methodService;

    /** 
     * add new contract info.
     */
    @PostMapping(value = "/save")
    public BaseResponse saveContract(@RequestBody @Valid Contract contract, BindingResult result)
            throws BaseException {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start saveContract.");

        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbContract tbContract = contractService.saveContract(contract);
        baseResponse.setData(tbContract);

        log.info("end saveContract useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }


    /**
     * delete contract by id.
     */
    @DeleteMapping(value = "/{contractId}")
    public BaseResponse deleteContract(@PathVariable("contractId") Integer contractId)
            throws BaseException, Exception {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start deleteContract.contractId:{}", contractId);

        contractService.deleteContract(contractId);

        log.info("end deleteContract useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * qurey contract info list.
     */
    @PostMapping(value = "/list")
    public BasePageResponse queryContractList(@RequestBody QueryContractParam inputParam)
            throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start contractList.");

        // param
        ContractParam queryParam = new ContractParam();
        BeanUtils.copyProperties(inputParam, queryParam);

        int count = contractService.countOfContract(queryParam);
        if (count > 0) {
            Integer start = Optional.ofNullable(inputParam.getPageNumber())
                    .map(page -> (page - 1) * inputParam.getPageSize()).orElse(0);
            queryParam.setStart(start);
            queryParam.setFlagSortedByTime(SqlSortType.DESC.getValue());
            // query list
            List<TbContract> listOfContract = contractService.qureyContractList(queryParam);
            pagesponse.setData(listOfContract);
            pagesponse.setTotalCount(count);
        }

        log.info("end contractList. useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }
    
    /**
     * add method info.
     */
    @PostMapping(value = "/addMethod")
    public BaseResponse addMethod(@RequestBody @Valid NewMethodInput newMethodInput,
            BindingResult result) throws BaseException {
        checkBindResult(result);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start addMethod.");
        methodService.saveMethod(newMethodInput);
        log.info("end addMethod. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
}
