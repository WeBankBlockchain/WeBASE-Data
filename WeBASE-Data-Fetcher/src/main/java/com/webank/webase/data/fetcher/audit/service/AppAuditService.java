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
package com.webank.webase.data.fetcher.audit.service;

import com.webank.webase.data.fetcher.audit.entity.AppAuditInfo;
import com.webank.webase.data.fetcher.audit.entity.TbAppAudit;
import com.webank.webase.data.fetcher.audit.mapper.AppAuditMapper;
import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.entity.BaseQueryParam;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * service of Keywords.
 */
@Log4j2
@Service
public class AppAuditService {

    @Autowired
    private AppAuditMapper appAuditMapper;

    /**
     * add a new auditInfo
     */
    public TbAppAudit newAuditInfo(AppAuditInfo appAuditInfo) {
        log.debug("start newAuditInfo auditInfo:{}", appAuditInfo);

        Integer chainId = appAuditInfo.getChainId();
        Integer groupId = appAuditInfo.getGroupId();
        TbAppAudit tbAppAudit = getAuditInfoByGroupId(chainId, groupId);
        if (tbAppAudit != null) {
            throw new BaseException(ConstantCode.AUDIT_EXISTS);
        }
        // copy attribute
        TbAppAudit tbAuditInfo1 = new TbAppAudit();
        BeanUtils.copyProperties(appAuditInfo, tbAuditInfo1);
        // save audit info
        int result = add(tbAuditInfo1);
        if (result == 0) {
            log.warn("fail auditInfo after save.");
            throw new BaseException(ConstantCode.SAVE_AUDIT_FAIL);
        }
        return getAuditInfoByGroupId(chainId, groupId);
    }

    public int add(TbAppAudit tbAppAudit) {
        return appAuditMapper.add(tbAppAudit);
    }

    /**
     * confirm auditInfo
     */
    public TbAppAudit confirmAuditInfo(Integer id) {
        if (!checkAuditInfoId(id)) {
            throw new BaseException(ConstantCode.AUDIT_ID_NOT_EXISTS);
        }
        appAuditMapper.confirm(id);
        return getAuditInfoById(id);
    }

    /**
     * get auditInfo count
     */
    public int getAuditInfoCount(BaseQueryParam queryParam) {
        Integer count = appAuditMapper.getCount(queryParam);
        return count == null ? 0 : count;
    }

    /**
     * get auditInfo list
     */
    public List<TbAppAudit> getAuditInfoList(BaseQueryParam queryParam) {
        return appAuditMapper.getList(queryParam);
    }

    /**
     * get audit info
     */
    public TbAppAudit getAuditInfoById(Integer id) {
        return appAuditMapper.getAuditInfoById(id);
    }

    /**
     * get audit info
     */
    public TbAppAudit getAuditInfoByGroupId(Integer chainId, Integer groupId) {
        return appAuditMapper.getAuditInfoByGroupId(chainId, groupId);
    }

    /**
     * remove auditInfo
     */
    @Transactional
    public void removeAuditInfo(Integer id) {
        TbAppAudit tbAppAudit = getAuditInfoById(id);
        if (tbAppAudit == null) {
            throw new BaseException(ConstantCode.AUDIT_ID_NOT_EXISTS);
        }
        appAuditMapper.remove(id);
    }

    /**
     * check auditInfo id
     */
    private boolean checkAuditInfoId(Integer id) {
        TbAppAudit tbAppAudit = getAuditInfoById(id);
        if (tbAppAudit == null) {
            return false;
        }
        return true;
    }

}
