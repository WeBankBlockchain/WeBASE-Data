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
package com.webank.webase.data.fetcher.group;

import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.controller.BaseController;
import com.webank.webase.data.fetcher.base.entity.BasePageResponse;
import com.webank.webase.data.fetcher.base.entity.BaseQueryParam;
import com.webank.webase.data.fetcher.base.entity.BaseResponse;
import com.webank.webase.data.fetcher.base.enums.DataStatus;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.group.entity.BlockInfoDto;
import com.webank.webase.data.fetcher.group.entity.BlockListParam;
import com.webank.webase.data.fetcher.group.entity.ContractInfoDto;
import com.webank.webase.data.fetcher.group.entity.GroupGeneral;
import com.webank.webase.data.fetcher.group.entity.GroupInfoDto;
import com.webank.webase.data.fetcher.group.entity.NodeInfoDto;
import com.webank.webase.data.fetcher.group.entity.TransListParam;
import com.webank.webase.data.fetcher.group.entity.TransactionInfoDto;
import com.webank.webase.data.fetcher.group.entity.TxnDailyDto;
import com.webank.webase.data.fetcher.group.entity.UserInfoDto;
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

/**
 * Controller for processing group information.
 */
@Log4j2
@RestController
@RequestMapping("group")
public class GroupController extends BaseController {

    @Autowired
    private GroupService groupService;

    /**
     * query all group.
     */
    @GetMapping("/list/{chainId}")
    public BasePageResponse getGroupList(@PathVariable("chainId") Integer chainId)
            throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start getGroupList.");

        // get group list
        int count = groupService.countOfGroup(chainId, null, DataStatus.NORMAL.getValue());
        if (count > 0) {
            List<GroupInfoDto> groupList =
                    groupService.getGroupList(chainId, null, DataStatus.NORMAL.getValue());
            pagesponse.setTotalCount(count);
            pagesponse.setData(groupList);
        }

        log.info("end getGroupList useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * get group general.
     */
    @GetMapping("/general/{chainId}/{groupId}")
    public BaseResponse getGroupGeneral(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId) throws BaseException {
        Instant startTime = Instant.now();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        log.info("start getGroupGeneral.");
        GroupGeneral groupGeneral = groupService.queryGroupGeneral(chainId, groupId);

        baseResponse.setData(groupGeneral);
        log.info("end getGroupGeneral useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * get trans daily.
     */
    @GetMapping("/txnDaily/{chainId}/{groupId}")
    public BaseResponse getTransDaily(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId) {
        BaseResponse pagesponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start getTransDaily.");

        // check groupId
        groupService.checkGroupId(chainId, groupId);

        // query txn daily
        List<TxnDailyDto> listTrans = groupService.listSeventDayOfTrans(chainId, groupId);
        pagesponse.setData(listTrans);

        log.info("end getTransDaily useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * query node info list.
     */
    @GetMapping(value = "/nodeList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryNodeList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize) throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryNodeList.");

        // check groupId
        groupService.checkGroupId(chainId, groupId);

        // param
        BaseQueryParam queryParam = new BaseQueryParam();
        queryParam.setChainId(chainId);
        queryParam.setGroupId(groupId);
        int count = groupService.countOfNode(queryParam);
        if (count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            List<NodeInfoDto> listOfnode = groupService.queryNodeList(queryParam);
            pagesponse.setData(listOfnode);
            pagesponse.setTotalCount(count);
        }

        log.info("end queryNodeList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * query block list.
     */
    @GetMapping(value = "/blockList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryBlockList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "blockHash", required = false) String blockHash,
            @RequestParam(value = "blockNumber", required = false) BigInteger blockNumber)
            throws BaseException {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryBlockList.");

        // check groupId
        groupService.checkGroupId(chainId, groupId);

        BlockListParam queryParam = new BlockListParam();
        queryParam.setChainId(chainId);
        queryParam.setGroupId(groupId);
        queryParam.setBlockHash(blockHash);
        queryParam.setBlockNumber(blockNumber);
        int count = groupService.countOfBlock(queryParam);
        if (count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            List<BlockInfoDto> blockList = groupService.queryBlockList(queryParam);
            pageResponse.setData(blockList);
            pageResponse.setTotalCount(count);
        }
        log.info("end queryBlockList useTime:{}.",
                Duration.between(startTime, Instant.now()).toMillis());
        return pageResponse;
    }

    /**
     * query trans list.
     */
    @GetMapping(value = "/transList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryTransList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "transHash", required = false) String transHash,
            @RequestParam(value = "blockNumber", required = false) BigInteger blockNumber) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryTransList.");

        // check groupId
        groupService.checkGroupId(chainId, groupId);

        TransListParam queryParam = new TransListParam();
        queryParam.setChainId(chainId);
        queryParam.setGroupId(groupId);
        queryParam.setTransHash(transHash);
        queryParam.setBlockNumber(blockNumber);
        int count = groupService.countOfTrans(queryParam);
        if (count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            List<TransactionInfoDto> transList = groupService.queryTransList(queryParam);
            pageResponse.setData(transList);
            pageResponse.setTotalCount(count);
        }

        log.info("end queryTransList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pageResponse;
    }

    /**
     * query user info list.
     */
    @GetMapping(value = "/userList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryUserList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userParam", required = false) String userParam)
            throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryUserList.");

        // check groupId
        groupService.checkGroupId(chainId, groupId);

        BaseQueryParam param = new BaseQueryParam();
        param.setChainId(chainId);
        param.setGroupId(groupId);
        param.setComVariable(userParam);
        Integer count = groupService.countOfUser(param);
        if (count != null && count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            param.setStart(start);
            param.setPageSize(pageSize);
            List<UserInfoDto> listOfUser = groupService.queryUserList(param);
            pagesponse.setData(listOfUser);
            pagesponse.setTotalCount(count);
        }
        log.info("end queryUserList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }

    /**
     * query contract info list.
     */
    @GetMapping(value = "/contractList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryContractList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "contractParam", required = false) String contractParam)
            throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryContractList.");

        // param
        BaseQueryParam queryParam = new BaseQueryParam();
        queryParam.setChainId(chainId);
        queryParam.setGroupId(groupId);
        queryParam.setComVariable(contractParam);

        int count = groupService.countOfContract(queryParam);
        if (count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            // query list
            List<ContractInfoDto> listOfContract = groupService.queryContractList(queryParam);
            pagesponse.setData(listOfContract);
            pagesponse.setTotalCount(count);
        }

        log.info("end queryContractList. useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }
}
