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

import com.webank.webase.data.fetcher.audit.entity.AuditQueryParam;
import com.webank.webase.data.fetcher.audit.entity.TbTransAudit;
import com.webank.webase.data.fetcher.audit.entity.TransAuditInfo;
import com.webank.webase.data.fetcher.audit.mapper.TransAuditMapper;
import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.enums.AuditType;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.chain.ChainService;
import com.webank.webase.data.fetcher.group.GroupService;
import com.webank.webase.data.fetcher.group.entity.GroupInfoDto;
import com.webank.webase.data.fetcher.search.EsCurdService;
import com.webank.webase.data.fetcher.search.SearchService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
public class TransAuditService {

    @Autowired
    private TransAuditMapper transAuditMapper;
    @Autowired
    private SearchService searchService;
    @Autowired
    private ChainService chainService;
    @Autowired
    private GroupService groupService;

    private static final String COMMENT = "automatic scanning";

    /**
     * add a new transAuditInfo
     */
    public TbTransAudit newAuditInfo(TransAuditInfo transAuditInfo) {
        log.debug("start newAuditInfo transAuditInfo:{}", transAuditInfo);
        // check param
        if (!AuditType.isInclude(transAuditInfo.getType())) {
            log.error("invalid audit type:{}", transAuditInfo.getType());
            throw new BaseException(ConstantCode.INVALID_AUDIT_TYPE);
        }
        if (transAuditInfo.getType() == AuditType.KEYWORD.getValue()
                && StringUtils.isBlank(transAuditInfo.getKeyword())) {
            log.error("keyword can not be empty when audit type is:{}", transAuditInfo.getType());
            throw new BaseException(ConstantCode.KEYWORD_ID_EMPTY);
        }
        Integer chainId = transAuditInfo.getChainId();
        Integer groupId = transAuditInfo.getGroupId();
        TbTransAudit tbrecord = getAuditInfoByTxHash(chainId, groupId, transAuditInfo.getTxHash());
        if (tbrecord != null) {
            throw new BaseException(ConstantCode.AUDIT_EXISTS);
        }
        // copy attribute
        TbTransAudit tbAuditInfo = new TbTransAudit();
        BeanUtils.copyProperties(transAuditInfo, tbAuditInfo);
        if (StringUtils.isBlank(tbAuditInfo.getChainName())) {
            tbAuditInfo.setChainName(chainService.getNameById(transAuditInfo.getChainId()));
        }
        if (StringUtils.isBlank(tbAuditInfo.getAppName())) {
            List<GroupInfoDto> groupList = groupService.getGroupList(transAuditInfo.getChainId(),
                    transAuditInfo.getGroupId(), null);
            if (!CollectionUtils.isEmpty(groupList)) {
                tbAuditInfo.setAppName(groupList.get(0).getAppName());
            }
        }
        // save audit info
        int result = add(tbAuditInfo);
        if (result == 0) {
            log.warn("fail transAuditInfo after save.");
            throw new BaseException(ConstantCode.SAVE_AUDIT_FAIL);
        }
        return getAuditInfoByTxHash(chainId, groupId, transAuditInfo.getTxHash());
    }

    @Async("asyncExecutor")
    public void auditProcess(CountDownLatch latch, String keyword) {
        try {
            SearchResponse searchResponse =
                    searchService.findByKey(1, EsCurdService.MAX_RESULT_WINDOW, keyword);
            log.info("auditProcess. keyword:{} count:{}", keyword,
                    searchResponse.getHits().getTotalHits().value);
            searchResponse.getHits().iterator().forEachRemaining(hit -> {
                TbTransAudit tbTransAudit = new TbTransAudit();
                Map<String, Object> source = hit.getSourceAsMap();
                Integer chainId = Integer.valueOf(source.get("chainId").toString());
                Integer groupId = Integer.valueOf(source.get("groupId").toString());
                String transHash = source.get("transHash").toString();
                TbTransAudit tbRecord = getAuditInfoByTxHash(chainId, groupId, transHash);
                if (tbRecord != null) {
                    return;
                }
                log.info("auditProcess. transHash:{}", transHash);
                tbTransAudit.setChainId(chainId);
                tbTransAudit.setGroupId(groupId);
                tbTransAudit.setKeyword(keyword);
                tbTransAudit.setTxHash(source.get("transHash").toString());
                tbTransAudit.setAddress(source.get("userAddress").toString());
                tbTransAudit.setComment(COMMENT);
                tbTransAudit.setChainName(chainService.getNameById(chainId));
                tbTransAudit.setType(AuditType.KEYWORD.getValue());
                List<GroupInfoDto> groupList = groupService.getGroupList(chainId, groupId, null);
                if (!CollectionUtils.isEmpty(groupList)) {
                    tbTransAudit.setAppName(groupList.get(0).getAppName());
                }
                add(tbTransAudit);
            });
        } catch (Exception ex) {
            log.error("fail auditProcess. keyword:{} ", keyword, ex);
        } finally {
            if (Objects.nonNull(latch)) {
                latch.countDown();
            }
        }
    }

    public int add(TbTransAudit tbTransAudit) {
        return transAuditMapper.add(tbTransAudit);
    }

    /**
     * confirm transAuditInfo
     */
    public TbTransAudit confirmAuditInfo(Integer id) {
        if (!checkAuditInfoId(id)) {
            throw new BaseException(ConstantCode.AUDIT_ID_NOT_EXISTS);
        }
        transAuditMapper.confirm(id);
        return getAuditInfoById(id);
    }

    /**
     * get transAuditInfo count
     */
    public int getAuditInfoCount(AuditQueryParam queryParam) {
        Integer count = transAuditMapper.getCount(queryParam);
        return count == null ? 0 : count;
    }

    /**
     * get transAuditInfo list
     */
    public List<TbTransAudit> getAuditInfoList(AuditQueryParam queryParam) {
        return transAuditMapper.getList(queryParam);
    }

    /**
     * get audit info
     */
    public TbTransAudit getAuditInfoById(Integer id) {
        return transAuditMapper.getAuditInfoById(id);
    }

    /**
     * get audit info
     */
    public TbTransAudit getAuditInfoByTxHash(int chainId, int groupId, String txHash) {
        return transAuditMapper.getAuditInfoByTxHash(chainId, groupId, txHash);
    }

    /**
     * remove transAuditInfo
     */
    @Transactional
    public void removeAuditInfo(Integer id) {
        TbTransAudit tbTransAudit = getAuditInfoById(id);
        if (tbTransAudit == null) {
            throw new BaseException(ConstantCode.AUDIT_ID_NOT_EXISTS);
        }
        transAuditMapper.remove(id);
    }

    /**
     * check transAuditInfo id
     */
    private boolean checkAuditInfoId(Integer id) {
        TbTransAudit tbTransAudit = getAuditInfoById(id);
        if (tbTransAudit == null) {
            return false;
        }
        return true;
    }

}
