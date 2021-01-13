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
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.parser.ParserService;
import com.webank.webase.data.collect.user.entity.TbUser;
import com.webank.webase.data.collect.user.entity.UserInfo;
import com.webank.webase.data.collect.user.entity.UserParam;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * services for user data.
 */
@Log4j2
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Lazy
    @Autowired
    private ParserService parserService;
    @Autowired
    private GroupService groupService;

    /**
     * add user info.
     */
    @Transactional
    public TbUser addUserInfo(UserInfo user) throws BaseException {
        Integer chainId = user.getChainId();
        Integer groupId = user.getGroupId();
        // check groupId
        groupService.checkGroupId(chainId, groupId);
        // check userName
        if (checkUserName(chainId, groupId, user.getUserName())) {
            log.warn("fail addUserInfo. userName is already exists");
            throw new BaseException(ConstantCode.USER_NAME_EXISTS);
        }
        // check address
        if (checkAddress(chainId, groupId, user.getAddress())) {
            log.warn("fail addUserInfo. address is already exists");
            throw new BaseException(ConstantCode.USER_ADDRESS_EXISTS);
        }
        // add row
        TbUser newUser = new TbUser(chainId, groupId, user.getUserName(), user.getAddress(),
                user.getDescription());
        Integer affectRow = userMapper.addUserRow(newUser);
        if (affectRow == 0) {
            log.warn("addUserInfo affect 0 rows of tb_user");
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }

        // update unusual user's info
        parserService.updateUnusualUser(chainId, groupId, user.getUserName(), user.getAddress());

        return queryByUserId(newUser.getUserId());
    }

    /**
     * query count of user.
     */
    public Integer countOfUser(UserParam userParam) {
        return userMapper.countOfUser(userParam);
    }

    /**
     * query user list by page.
     */
    public List<TbUser> queryUserList(UserParam userParam) {
        List<TbUser> listOfUser = userMapper.listOfUser(userParam);
        return listOfUser;
    }

    /**
     * query by userId.
     */
    public TbUser queryByUserId(Integer userId) {
        return queryUser(null, null, userId, null, null);
    }

    /**
     * query by userName.
     */
    public TbUser queryByName(Integer chainId, Integer groupId, String userName) {
        return queryUser(chainId, groupId, null, userName, null);
    }

    /**
     * get by address.
     */
    public TbUser queryByAddress(Integer chainId, Integer groupId, String address) {
        return queryUser(chainId, groupId, null, null, address);
    }

    /**
     * delete by userId
     */
    public void deleteByUserId(Integer userId) {
        userMapper.deleteByUserId(userId);
    }

    /**
     * check userName exists.
     */
    private boolean checkUserName(Integer chainId, Integer groupId, String userName) {
        TbUser tbUser = queryByName(chainId, groupId, userName);
        if (Objects.isNull(tbUser)) {
            return false;
        }
        return true;
    }

    /**
     * check address exists.
     */
    private boolean checkAddress(Integer chainId, Integer groupId, String address) {
        TbUser tbUser = queryByAddress(chainId, groupId, address);
        if (Objects.isNull(tbUser)) {
            return false;
        }
        return true;
    }

    /**
     * get by address.
     */
    private TbUser queryUser(Integer chainId, Integer groupId, Integer userId, String userName,
            String address) {
        UserParam userParam = new UserParam();
        userParam.setChainId(chainId);
        userParam.setGroupId(groupId);
        userParam.setUserId(userId);
        userParam.setUserName(userName);
        userParam.setAddress(address);
        return userMapper.queryUser(userParam);
    }
}
