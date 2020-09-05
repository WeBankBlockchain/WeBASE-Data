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
package com.webank.webase.data.collect.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.chain.ChainService;
import com.webank.webase.data.collect.config.entity.ConfigVersionInfo;
import com.webank.webase.data.collect.config.entity.TbConfigVersion;
import com.webank.webase.data.collect.config.entity.RspUserInfo;
import com.webank.webase.data.collect.contract.ContractService;
import com.webank.webase.data.collect.contract.entity.TbContract;
import com.webank.webase.data.collect.front.FrontService;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.user.UserService;
import com.webank.webase.data.collect.user.entity.TbUser;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service of chain.
 */
@Log4j2
@Service
public class ConfigService {

    @Autowired
    private ConfigVersionMapper configVersionMapper;
    @Autowired
    private WebServerRestTools webServerRestTools;
    @Autowired
    private ChainService chainService;
    @Autowired
    private FrontService frontService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;
    @Autowired
    private ContractService contractService;

    private static final int PAGESIZE = 20;

    public void syncConfig() {
        chainService.addChainList(webServerRestTools.getChainList());
        frontService.addFrontList(webServerRestTools.getFrontList());
        List<TbGroup> groupList = webServerRestTools.getGroupList();
        groupService.addGroupList(groupList);
        addContractList();
        addUserList(groupList);
    }

    public void syncConfigWithVersion() {
        ConfigVersionInfo configVersionInfo = webServerRestTools.getConfigVersion();
        if (configVersionInfo == null) {
            log.warn("syncConfig version info is null.");
            return;
        }
        TbConfigVersion tbConfigVersion = getTbConfigVersion();
        if (tbConfigVersion == null) {
            newInfo(configVersionInfo);
        } else {
            updateInfo(configVersionInfo, tbConfigVersion);
        }
    }

    /**
     * new info
     */
    @Transactional
    public void newInfo(ConfigVersionInfo configVersionInfo) {
        chainService.addChainList(webServerRestTools.getChainList());
        frontService.addFrontList(webServerRestTools.getFrontList());
        List<TbGroup> groupList = webServerRestTools.getGroupList();
        groupService.addGroupList(groupList);
        addContractList();
        addUserList(groupList);
        newVersionInfo(configVersionInfo);
    }

    /**
     * new info
     */
    @Transactional
    public void updateInfo(ConfigVersionInfo configVersionInfo, TbConfigVersion tbConfigVersion) {
        boolean ifChange = false;
        if (!configVersionInfo.getChainInfoVersion()
                .equals(tbConfigVersion.getChainInfoVersion())) {
            chainService.addChainList(webServerRestTools.getChainList());
            ifChange = true;
        }
        if (!configVersionInfo.getFrontInfoVersion()
                .equals(tbConfigVersion.getFrontInfoVersion())) {
            frontService.addFrontList(webServerRestTools.getFrontList());
            ifChange = true;
        }
        if (!configVersionInfo.getGroupInfoVersion()
                .equals(tbConfigVersion.getGroupInfoVersion())) {
            groupService.addGroupList(webServerRestTools.getGroupList());
            ifChange = true;
        }
        if (!configVersionInfo.getContractInfoVersion()
                .equals(tbConfigVersion.getContractInfoVersion())) {
            addContractList();
            ifChange = true;
        }
        if (!configVersionInfo.getUserInfoVersion().equals(tbConfigVersion.getUserInfoVersion())) {
            List<TbGroup> groupList = webServerRestTools.getGroupList();
            addUserList(groupList);
            ifChange = true;
        }
        if (ifChange) {
            BeanUtils.copyProperties(configVersionInfo, tbConfigVersion);
            updateVersionInfo(tbConfigVersion);
        }
    }

    /**
     * add contract list.
     */
    private void addContractList() {
        Integer pageNumber = 1;
        Integer pageSize = PAGESIZE;
        BasePageResponse response = webServerRestTools.getContractList(pageNumber, pageSize);
        Integer count = response.getTotalCount();
        while ((pageNumber - 1) * pageSize < count) {
            List<TbContract> list = JacksonUtils.objToJavaBean(response.getData(),
                    new TypeReference<List<TbContract>>() {});
            contractService.addContractList(list);
            pageNumber = pageNumber + 1;
            response = webServerRestTools.getContractList(pageNumber, pageSize);
        }
    }

    /**
     * add user list.
     */
    private void addUserList(List<TbGroup> groupList) {
        for (TbGroup tbGroup : groupList) {
            Integer chainId = tbGroup.getChainId();
            Integer groupId = tbGroup.getGroupId();
            Integer pageNumber = 1;
            Integer pageSize = PAGESIZE;
            BasePageResponse response =
                    webServerRestTools.getUserList(groupId, pageNumber, pageSize);
            Integer count = response.getTotalCount();
            while ((pageNumber - 1) * pageSize < count) {
                List<RspUserInfo> list = JacksonUtils.objToJavaBean(response.getData(),
                        new TypeReference<List<RspUserInfo>>() {});
                list.forEach(u -> {u.setChainId(chainId);});
                userService.addUserList(list);
                pageNumber = pageNumber + 1;
                response = webServerRestTools.getUserList(groupId, pageNumber, pageSize);
            }
        }
    }

    /**
     * add new version info.
     */
    private void newVersionInfo(ConfigVersionInfo configVersionInfo) {
        TbConfigVersion tbConfigVersion = new TbConfigVersion();
        BeanUtils.copyProperties(configVersionInfo, tbConfigVersion);
        configVersionMapper.add(tbConfigVersion);
    }

    /**
     * update version info.
     */
    private void updateVersionInfo(TbConfigVersion tbConfigVersion) {
        configVersionMapper.update(tbConfigVersion);
    }

    /**
     * get info.
     */
    private TbConfigVersion getTbConfigVersion() {
        return configVersionMapper.getTbConfigVersion();
    }

}
