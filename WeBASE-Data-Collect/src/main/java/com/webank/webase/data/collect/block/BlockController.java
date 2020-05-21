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
package com.webank.webase.data.collect.block;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.enums.SqlSortType;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.block.entity.BlockListParam;
import com.webank.webase.data.collect.block.entity.TbBlock;
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
@RequestMapping(value = "block")
public class BlockController {

    @Autowired
    private BlockService blockService;

    /**
     * query block list.
     */
    @GetMapping(value = "/list/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryBlockList(@PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "blockHash", required = false) String blockHash,
            @RequestParam(value = "blockNumber", required = false) BigInteger blockNumber)
            throws BaseException, Exception {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryBlockList.}");
        int count = blockService.queryCountOfBlock(groupId, blockHash, blockNumber);
        if (count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            BlockListParam queryParam = new BlockListParam(start, pageSize, blockHash, blockNumber,
                    SqlSortType.DESC.getValue());
            List<TbBlock> blockList = blockService.queryBlockList(groupId, queryParam);
            pageResponse.setData(blockList);
            pageResponse.setTotalCount(count);
        }
        log.info("end queryBlockList useTime:{}.",
                Duration.between(startTime, Instant.now()).toMillis());
        return pageResponse;
    }


    /**
     * get block by number.
     */
    @GetMapping("/blockByNumber/{groupId}/{blockNumber}")
    public BaseResponse getBlockByNumber(@PathVariable("groupId") Integer groupId,
            @PathVariable("blockNumber") BigInteger blockNumber) throws BaseException {
        Instant startTime = Instant.now();
        log.info("start getBlockByNumber. blockNumber:{}", blockNumber);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Object blockInfo = blockService.getBlockFromFrontByNumber(groupId, blockNumber);
        baseResponse.setData(blockInfo);
        log.info("end getBlockByNumber useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
}
