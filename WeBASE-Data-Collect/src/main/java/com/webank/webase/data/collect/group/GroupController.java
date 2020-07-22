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
package com.webank.webase.data.collect.group;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.group.entity.GroupGeneral;
import com.webank.webase.data.collect.group.entity.AppInfo;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.scheduler.ResetGroupListTask;
import com.webank.webase.data.collect.txndaily.TxnDailyService;
import com.webank.webase.data.collect.txndaily.entity.TbTxnDaily;
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
    @Autowired
    private TxnDailyService txnDailyService;
    @Autowired
    private ResetGroupListTask resetGroupListTask;

    /**
     * update group app info
     */
    @PostMapping("/update")
    public BaseResponse updateGroupAppInfo(@RequestBody @Valid AppInfo appInfo,
            BindingResult result) throws BaseException {
        checkBindResult(result);
        Instant startTime = Instant.now();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        log.info("start updateGroupInfo.");
        groupService.updateGroupAppInfo(appInfo);
        log.info("end updateGroupInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * get group general.
     */
    @GetMapping("/general/{chainId}/{groupId}")
    public BaseResponse getGroupGeneral(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId) throws BaseException {
        Instant startTime = Instant.now();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        log.info("start getGroupGeneral. groupId:{}", groupId);
        GroupGeneral groupGeneral = groupService.queryGroupGeneral(chainId, groupId);

        baseResponse.setData(groupGeneral);
        log.info("end getGroupGeneral useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

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
            List<TbGroup> groupList =
                    groupService.getGroupList(chainId, DataStatus.NORMAL.getValue());
            pagesponse.setTotalCount(count);
            pagesponse.setData(groupList);
        }

        // reset group
        resetGroupListTask.asyncResetGroupList();

        log.info("end getGroupList useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
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

        // query txn daily
        List<TbTxnDaily> listTrans = txnDailyService.listSeventDayOfTrans(chainId, groupId);
        pagesponse.setData(listTrans);

        log.info("end getTransDaily useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }
}
