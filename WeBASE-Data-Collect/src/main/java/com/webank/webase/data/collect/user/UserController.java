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
package com.webank.webase.data.collect.user;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.user.entity.TbUser;
import com.webank.webase.data.collect.user.entity.UserInfo;
import com.webank.webase.data.collect.user.entity.UserParam;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Key pair manage
 */
@Log4j2
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * add user info.
     */
    @PostMapping(value = "/add")
    public BaseResponse addUserInfo(@RequestBody @Valid UserInfo user, BindingResult result)
            throws BaseException {
        log.info("start addUserInfo.");
        Instant startTime = Instant.now();
        checkBindResult(result);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TbUser userRow = userService.addUserInfo(user);
        baseResponse.setData(userRow);
        log.info("end addUserInfo useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * query user info list.
     */
    @GetMapping(value = "/list/{pageNumber}/{pageSize}")
    public BasePageResponse userList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "userParam", required = false) String userParam)
            throws BaseException {
        log.info("start userList.");
        Instant startTime = Instant.now();
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);

        UserParam param = new UserParam();
        param.setUserParam(userParam);
        Integer count = userService.countOfUser(param);
        if (count != null && count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            param.setStart(start);
            param.setPageSize(pageSize);
            List<TbUser> listOfUser = userService.qureyUserList(param);
            pagesponse.setData(listOfUser);
            pagesponse.setTotalCount(count);
        }
        log.info("end userList useTime:{}", Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }
    
    /**
     * delete by userId
     */
    @DeleteMapping("/{userId}")
    public BaseResponse removeUser(@PathVariable("userId") Integer userId) {
        Instant startTime = Instant.now();
        log.info("start removeUser. userId:{}", userId);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        // remove
        userService.deleteByUserId(userId);
        log.info("end removeUser useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }
}
