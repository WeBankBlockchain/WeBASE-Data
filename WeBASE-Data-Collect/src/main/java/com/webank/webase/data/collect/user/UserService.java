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
import com.webank.webase.data.collect.base.enums.HasPk;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.Web3Tools;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.monitor.MonitorService;
import com.webank.webase.data.collect.user.entity.BindUserInputParam;
import com.webank.webase.data.collect.user.entity.TbUser;
import com.webank.webase.data.collect.user.entity.UpdateUserInputParam;
import com.webank.webase.data.collect.user.entity.UserParam;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private GroupService groupService;
    @Lazy
    @Autowired
    private MonitorService monitorService;

    /**
     * bind user info.
     */
    @Transactional
    public Integer bindUserInfo(BindUserInputParam user) throws BaseException {
        log.info("start bindUserInfo.");

        String publicKey = user.getPublicKey();
        if (StringUtils.isBlank(publicKey)) {
            log.info("fail bindUserInfo. publicKey cannot be empty");
            throw new BaseException(ConstantCode.PUBLICKEY_NULL);
        }

        if (publicKey.length() != ConstantProperties.PUBLICKEY_LENGTH
                && publicKey.length() != ConstantProperties.ADDRESS_LENGTH) {
            log.info("fail bindUserInfo. publicKey length error");
            throw new BaseException(ConstantCode.PUBLICKEY_LENGTH_ERROR);
        }

        // check group id
        groupService.checkGroupId(user.getChainId(), user.getGroupId());

        // check userName
        TbUser userRow = queryByName(user.getGroupId(), user.getUserName());
        if (Objects.nonNull(userRow)) {
            log.warn("fail bindUserInfo. userName is already exists");
            throw new BaseException(ConstantCode.USER_EXISTS);
        }
        String address = publicKey;
        if (publicKey.length() == ConstantProperties.PUBLICKEY_LENGTH) {
            address = Web3Tools.getAddressByPublicKey(publicKey);
        }

        // check address
        TbUser addressRow = queryUser(null, user.getGroupId(), null, address);
        if (Objects.nonNull(addressRow)) {
            log.warn("fail bindUserInfo. address is already exists");
            throw new BaseException(ConstantCode.USER_EXISTS);
        }

        // add row
        TbUser newUserRow = new TbUser(HasPk.NONE.getValue(), user.getUserType(),
                user.getUserName(), user.getGroupId(), address, publicKey, user.getDescription());
        Integer affectRow = userMapper.addUserRow(newUserRow);
        if (affectRow == 0) {
            log.warn("bindUserInfo affect 0 rows of tb_user");
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }

        Integer userId = newUserRow.getUserId();

        // update monitor unusual user's info
        monitorService.updateUnusualUser(user.getChainId(), user.getGroupId(), user.getUserName(),
                address);

        log.info("end bindUserInfo userId:{}", userId);
        return userId;
    }

    /**
     * query count of user.
     */
    public Integer countOfUser(UserParam userParam) throws BaseException {
        try {
            Integer count = userMapper.countOfUser(userParam);
            return count;
        } catch (RuntimeException ex) {
            log.error("fail countOfUser.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query count of user.
     */
    private Integer countOfUser(Integer userId) throws BaseException {
        UserParam userParam = new UserParam();
        userParam.setUserId(userId);
        return countOfUser(userParam);
    }

    /**
     * query user list by page.
     */
    public List<TbUser> qureyUserList(UserParam userParam) throws BaseException {
        // query user list
        List<TbUser> listOfUser = userMapper.listOfUser(userParam);
        return listOfUser;
    }

    /**
     * query user row.
     */
    public TbUser queryUser(Integer userId, Integer groupId, String userName, String address)
            throws BaseException {
        try {
            TbUser userRow = userMapper.queryUser(userId, groupId, userName, address);
            return userRow;
        } catch (RuntimeException ex) {
            log.error("fail queryUser userId:{} groupId:{} userName:{}  address:{}", userId,
                    groupId, userName, address, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query by groupId、userName.
     */
    public TbUser queryUser(Integer groupId, String userName) throws BaseException {
        return queryUser(null, groupId, userName, null);
    }

    /**
     * query by userName.
     */
    public TbUser queryByName(int groupId, String userName) throws BaseException {
        return queryUser(null, groupId, userName, null);
    }


    /**
     * query by userId.
     */
    public TbUser queryByUserId(Integer userId) throws BaseException {
        return queryUser(userId, null, null, null);
    }

    /**
     * query by group id and address.
     */
    public String getSignUserIdByAddress(int groupId, String address) throws BaseException {
        TbUser user = queryUser(null, groupId, null, address);
        return user.getSignUserId();
    }

    /**
     * update user info.
     */
    public void updateUser(UpdateUserInputParam user) throws BaseException {
        TbUser tbUser = queryByUserId(user.getUserId());
        tbUser.setDescription(user.getDescription());
        updateUser(tbUser);
    }

    /**
     * update user info.
     */
    public void updateUser(TbUser user) throws BaseException {
        Integer userId = Optional.ofNullable(user).map(u -> u.getUserId()).orElse(null);
        String description = Optional.ofNullable(user).map(u -> u.getDescription()).orElse(null);
        if (userId == null) {
            log.warn("fail updateUser. user id is null");
            throw new BaseException(ConstantCode.USER_ID_NULL);
        }
        TbUser tbUser = queryByUserId(userId);
        tbUser.setDescription(description);

        try {
            Integer affectRow = userMapper.updateUser(tbUser);
            if (affectRow == 0) {
                log.warn("affect 0 rows of tb_user");
                throw new BaseException(ConstantCode.DB_EXCEPTION);
            }
        } catch (RuntimeException ex) {
            log.error("fail updateUser  userId:{} description:{}", userId, description, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }


    /**
     * get user name by address.
     */
    public String queryUserNameByAddress(Integer groupId, String address) throws BaseException {
        String userName = userMapper.queryUserNameByAddress(groupId, address);
        return userName;
    }

    public void deleteByAddress(String address) throws BaseException {
        userMapper.deleteByAddress(address);
    }
}
