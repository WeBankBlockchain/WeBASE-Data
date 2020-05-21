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
package com.webank.webase.data.collect.transaction;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.enums.SqlSortType;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.receipt.entity.TransReceipt;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import com.webank.webase.data.collect.transaction.entity.TransListParam;
import com.webank.webase.data.collect.transaction.entity.TransactionInfo;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = "transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * query trans list.
     */
    @GetMapping(value = "/list/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryTransList(@PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "transHash", required = false) String transHash,
            @RequestParam(value = "blockNumber", required = false) BigInteger blockNumber) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryTransList.");
        TransListParam queryParam = new TransListParam(transHash, blockNumber);
        Integer count = transactionService.queryCountOfTran(groupId, queryParam);
        if (count != null && count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            queryParam.setFlagSortedByBlock(SqlSortType.DESC.getValue());
            List<TbTransaction> transList = transactionService.queryTransList(groupId, queryParam);
            pageResponse.setData(transList);
            // on chain tx count
            pageResponse.setTotalCount(count);
        }

        log.info("end queryBlockList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pageResponse;
    }

    /**
     * get transaction by hash.
     */
    @GetMapping("/transInfo/{groupId}/{transHash}")
    public BaseResponse getTransaction(@PathVariable("groupId") Integer groupId,
            @PathVariable("transHash") String transHash) throws BaseException {
        Instant startTime = Instant.now();
        log.info("start getTransaction. transHash:{}", transHash);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TransactionInfo transInfo = transactionService.getTransaction(groupId, transHash);
        baseResponse.setData(transInfo);
        log.info("end getTransaction useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
    
    /**
     * get transaction receipt.
     */
    @GetMapping("/receipt/{groupId}/{transHash}")
    public BaseResponse getTransReceipt(@PathVariable("groupId") Integer groupId,
            @PathVariable("transHash") String transHash) throws BaseException {
        Instant startTime = Instant.now();
        log.info("start getTransReceipt transHash:{}", transHash);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TransReceipt transReceipt = transactionService.getTransReceipt(groupId, transHash);
        baseResponse.setData(transReceipt);
        log.info("end getTransReceipt useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
}
