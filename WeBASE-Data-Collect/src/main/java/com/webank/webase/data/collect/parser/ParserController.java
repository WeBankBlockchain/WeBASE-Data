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
package com.webank.webase.data.collect.parser;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.parser.entity.TbParser;
import com.webank.webase.data.collect.parser.entity.UnusualContractInfo;
import com.webank.webase.data.collect.parser.entity.UnusualUserInfo;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
// @RestController
@RequestMapping("parser")
public class ParserController {

    @Autowired
    private ParserService parserService;

    /**
     * parser user list.
     */
    @GetMapping(value = "/userList/{chainId}/{groupId}")
    public BaseResponse parserUserList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId) throws BaseException {
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start parserUserList startTime:{} groupId:{} ", startTime.toEpochMilli(),
                groupId);

        List<TbParser> listOfUser = parserService.qureyParserUserList(chainId, groupId);
        response.setData(listOfUser);

        log.info("end parserUserList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return response;
    }

    /**
     * parser interface list.
     */
    @GetMapping(value = "/interfaceList/{chainId}/{groupId}")
    public BaseResponse parserInterfaceList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @RequestParam(value = "userName") String userName) throws BaseException {
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start parserInterfaceList startTime:{} groupId:{} ", startTime.toEpochMilli(),
                groupId);

        List<TbParser> listOfInterface =
                parserService.qureyParserInterfaceList(chainId, groupId, userName);
        response.setData(listOfInterface);

        log.info("end parserInterfaceList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return response;
    }

    /**
     * parser trans list.
     */
    @GetMapping(value = "/transList/{chainId}/{groupId}")
    public BaseResponse parserTransList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "interfaceName", required = false) String interfaceName)
            throws BaseException {
        Instant startTime = Instant.now();
        log.info(
                "start parserTransList startTime:{} groupId:{} userName:{} startDate:{}"
                        + " endDate:{} interfaceName:{}",
                startTime.toEpochMilli(), groupId, userName, startDate, endDate, interfaceName);

        BaseResponse response = parserService.qureyParserTransList(chainId, groupId, userName,
                startDate, endDate, interfaceName);

        log.info("end parserTransList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return response;
    }

    /**
     * unusual user list.
     */
    @GetMapping(value = "/unusualUserList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse unusualUserList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userName", required = false) String userName)
            throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start unusualUserList startTime:{} groupId:{} pageNumber:{} pageSize:{}"
                        + " userName:{}",
                startTime.toEpochMilli(), groupId, pageNumber, pageSize, userName);

        Integer count = parserService.countOfUnusualUser(chainId, groupId, userName);
        if (count != null && count > 0) {
            List<UnusualUserInfo> listOfUnusualUser = parserService.qureyUnusualUserList(chainId,
                    groupId, userName, pageNumber, pageSize);
            pagesponse.setData(listOfUnusualUser);
            pagesponse.setTotalCount(count);
        }

        log.info("end unusualUserList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * unusual contract list.
     */
    @GetMapping(value = "/unusualContractList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse unusualContractList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "contractAddress", required = false) String contractAddress)
            throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start unusualContractList startTime:{} groupId:{} pageNumber:{}"
                        + " pageSize:{} contractAddress:{}",
                startTime.toEpochMilli(), groupId, pageNumber, pageSize, contractAddress);

        Integer count = parserService.countOfUnusualContract(chainId, groupId, contractAddress);
        if (count != null && count > 0) {
            List<UnusualContractInfo> listOfUnusualContract =
                    parserService.qureyUnusualContractList(chainId, groupId, contractAddress,
                            pageNumber, pageSize);
            pagesponse.setData(listOfUnusualContract);
            pagesponse.setTotalCount(count);
        }

        log.info("end unusualContractList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }
}
