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
package com.webank.webase.data.fetcher.audit;

import com.webank.webase.data.fetcher.audit.entity.AuditInfo;
import com.webank.webase.data.fetcher.audit.entity.TbAuditInfo;
import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.entity.BaseQueryParam;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.chain.ChainService;
import com.webank.webase.data.fetcher.group.GroupService;
import com.webank.webase.data.fetcher.group.entity.GroupInfoDto;
import com.webank.webase.data.fetcher.search.SearchService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


/**
 * service of Keywords.
 */
@Log4j2
@Service
public class AuditService {

    @Autowired
    private AuditMapper auditMapper;
    @Autowired
    private SearchService searchService;
    @Autowired
    private ChainService chainService;
    @Autowired
    private GroupService groupService;

    private static final String COMMENT = "automatic scanning";

    /**
     * add a new auditInfo
     */
    public TbAuditInfo newAuditInfo(AuditInfo auditInfo) {
        log.debug("start newAuditInfo auditInfo:{}", auditInfo);

        TbAuditInfo tbAuditInfo = getAuditInfoByTxHash(auditInfo.getTxHash());
        if (tbAuditInfo != null) {
            throw new BaseException(ConstantCode.AUDIT_EXISTS);
        }
        // copy attribute
        TbAuditInfo tbAuditInfo1 = new TbAuditInfo();
        BeanUtils.copyProperties(auditInfo, tbAuditInfo1);
        // save audit info
        int result = add(tbAuditInfo1);
        if (result == 0) {
            log.warn("fail auditInfo after save.");
            throw new BaseException(ConstantCode.SAVE_AUDIT_FAIL);
        }
        return getAuditInfoByTxHash(auditInfo.getTxHash());
    }

    @Async("asyncExecutor")
    public void auditProcess(CountDownLatch latch, String keyword) {
        try {
            SearchResponse searchResponse = searchService.findByKey(null, null, keyword);
            searchResponse.getHits().iterator().forEachRemaining(hit -> {
                TbAuditInfo tbAuditInfo = new TbAuditInfo();
                Map<String, Object> source = hit.getSourceAsMap();
                Integer chainId = Integer.valueOf(source.get("chainId").toString());
                Integer groupId = Integer.valueOf(source.get("groupId").toString());
                tbAuditInfo.setChainId(chainId);
                tbAuditInfo.setGroupId(groupId);
                tbAuditInfo.setKeyword(keyword);
                tbAuditInfo.setTxHash(source.get("transHash").toString());
                tbAuditInfo.setAddress(source.get("userAddress").toString());
                tbAuditInfo.setComment(COMMENT);
                tbAuditInfo.setChainName(chainService.getNameById(chainId));
                List<GroupInfoDto> groupList = groupService.getGroupList(chainId, groupId, null);
                if (!CollectionUtils.isEmpty(groupList)) {
                    tbAuditInfo.setAppName(groupList.get(0).getAppName());
                }
                add(tbAuditInfo);
            });
        } catch (Exception ex) {
            log.error("fail auditProcess. keyword:{} ", keyword, ex);
        } finally {
            if (Objects.nonNull(latch)) {
                latch.countDown();
            }
        }
    }

    public int add(TbAuditInfo tbAuditInfo) {
        return auditMapper.add(tbAuditInfo);
    }

    /**
     * confirm auditInfo
     */
    public TbAuditInfo confirmAuditInfo(Integer id) {
        if (!checkAuditInfoId(id)) {
            throw new BaseException(ConstantCode.AUDIT_ID_NOT_EXISTS);
        }
        auditMapper.confirm(id);
        return getAuditInfoById(id);
    }

    /**
     * get auditInfo count
     */
    public int getAuditInfoCount() {
        Integer count = auditMapper.getCount();
        return count == null ? 0 : count;
    }

    /**
     * get auditInfo list
     */
    public List<TbAuditInfo> getAuditInfoList(BaseQueryParam queryParam) {
        return auditMapper.getList(queryParam);
    }

    /**
     * get audit info
     */
    public TbAuditInfo getAuditInfoById(Integer id) {
        return auditMapper.getAuditInfoById(id);
    }

    /**
     * get audit info
     */
    public TbAuditInfo getAuditInfoByTxHash(String txHash) {
        return auditMapper.getAuditInfoByTxHash(txHash);
    }

    /**
     * remove auditInfo
     */
    @Transactional
    public void removeAuditInfo(Integer id) {
        TbAuditInfo tbAuditInfo = getAuditInfoById(id);
        if (tbAuditInfo == null) {
            throw new BaseException(ConstantCode.AUDIT_ID_NOT_EXISTS);
        }
        auditMapper.remove(id);
    }

    /**
     * check auditInfo id
     */
    private boolean checkAuditInfoId(Integer id) {
        TbAuditInfo tbAuditInfo = getAuditInfoById(id);
        if (tbAuditInfo == null) {
            return false;
        }
        return true;
    }

}
