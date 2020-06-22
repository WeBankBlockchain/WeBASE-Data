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

package com.webank.webase.data.collect.frontinterface;

import com.fasterxml.jackson.databind.JsonNode;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.frontgroupmap.FrontGroupMapCache;
import com.webank.webase.data.collect.frontgroupmap.entity.FrontGroup;
import com.webank.webase.data.collect.frontinterface.entity.FailInfo;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * about http request for WeBASE-Front.
 */
@Log4j2
@Service
public class FrontRestTools {

    public static final String FRONT_URL = "http://%1s:%2d/WeBASE-Front/%3s";
    public static final String URI_GROUP_PLIST = "web3/groupList";
    public static final String URI_BLOCK_BY_NUMBER = "web3/blockByNumber/%1d";
    public static final String URI_BLOCK_BY_HASH = "web3/blockByHash/%1s";
    public static final String URI_TRANS_BY_HASH = "web3/transaction/%1s";
    public static final String FRONT_TRANS_RECEIPT_BY_HASH_URI = "web3/transactionReceipt/%1s";
    public static final String URI_TRANS_TOTAL = "web3/transaction-total";
    public static final String URI_GROUP_PEERS = "web3/groupPeers";
    public static final String URI_NODEID_LIST = "web3/nodeIdList";
    public static final String URI_PEERS = "web3/peers";
    public static final String URI_CONSENSUS_STATUS = "web3/consensusStatus";
    public static final String URI_CSYNC_STATUS = "web3/syncStatus";
    public static final String URI_SYSTEMCONFIG_BY_KEY = "web3/systemConfigByKey/%1s";
    public static final String URI_CODE = "web3/code/%1s/%2s";
    public static final String URI_BLOCK_NUMBER = "web3/blockNumber";
    public static final String URI_GET_SEALER_LIST = "web3/sealerList";
    public static final String URI_GET_OBSERVER_LIST = "web3/observerList";
    public static final String URI_GET_CLIENT_VERSION = "web3/clientVersion";
    public static final String URI_ENCRYPT_TYPE = "encrypt";

    // 不需要在url的前面添加groupId的
    private static final List<String> URI_NOT_PREPEND_GROUP_ID = Arrays.asList(URI_ENCRYPT_TYPE);
    private static Map<String, FailInfo> failRequestMap = new HashMap<>();

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ConstantProperties cproperties;
    @Autowired
    private FrontGroupMapCache frontGroupMapCache;

    /**
     * get from specific front.
     */
    public <T> T getFromSpecificFront(int groupId, String frontIp, Integer frontPort, String uri,
            Class<T> clazz) {
        return requestSpecificFront(groupId, frontIp, frontPort, HttpMethod.GET, uri, null, clazz);
    }

    /**
     * post to specific front.
     */
    public <T> T postToSpecificFront(Integer groupId, String frontIp, Integer frontPort, String uri,
            Object param, Class<T> clazz) {
        return requestSpecificFront(groupId, frontIp, frontPort, HttpMethod.POST, uri, param,
                clazz);
    }

    /**
     * delete to specific front.
     */
    public <T> T deleteToSpecificFront(Integer groupId, String frontIp, Integer frontPort,
            String uri, Object param, Class<T> clazz) {
        return requestSpecificFront(groupId, frontIp, frontPort, HttpMethod.DELETE, uri, param,
                clazz);
    }

    /**
     * get from front for entity.
     */
    public <T> T getForEntity(Integer chainId, Integer groupId, String uri, Class<T> clazz) {
        return restTemplateExchange(chainId, groupId, uri, HttpMethod.GET, null, clazz);
    }

    /**
     * post from front for entity.
     */
    public <T> T postForEntity(Integer chainId, Integer groupId, String uri, Object params,
            Class<T> clazz) {
        return restTemplateExchange(chainId, groupId, uri, HttpMethod.POST, params, clazz);
    }

    /**
     * delete from front for entity.
     */
    public <T> T deleteForEntity(Integer chainId, Integer groupId, String uri, Object params,
            Class<T> clazz) {
        return restTemplateExchange(chainId, groupId, uri, HttpMethod.DELETE, params, clazz);
    }

    /**
     * request from specific front.
     */
    private <T> T requestSpecificFront(int groupId, String frontIp, Integer frontPort,
            HttpMethod method, String uri, Object param, Class<T> clazz) {
        uri = uriAddGroupId(groupId, uri);
        String url = String.format(FRONT_URL, frontIp, frontPort, uri);
        try {
            HttpEntity<?> entity = buildHttpEntity(param);// build entity
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, clazz);
            return response.getBody();
        } catch (ResourceAccessException e) {
            log.error("requestSpecificFront. ResourceAccessException:", e);
            throw new BaseException(ConstantCode.REQUEST_FRONT_FAIL);
        } catch (HttpStatusCodeException e) {
            errorFormat(e.getResponseBodyAsString());
        }
        throw new BaseException(ConstantCode.REQUEST_FRONT_FAIL);
    }

    /**
     * request exchange.
     */
    @SuppressWarnings("rawtypes")
    private <T> T restTemplateExchange(int chainId, int groupId, String uri, HttpMethod method,
            Object param, Class<T> clazz) {
        List<FrontGroup> frontList = frontGroupMapCache.getMapListByChainId(chainId, groupId);
        if (frontList == null || frontList.size() == 0) {
            log.error("fail restTemplateExchange. frontList is empty");
            throw new BaseException(ConstantCode.FRONT_LIST_NOT_FOUNT);
        }
        ArrayList<FrontGroup> list = new ArrayList<>(frontList);
        while (list != null && list.size() > 0) {
            String url = buildFrontUrl(list, uri, method);// build url
            try {
                HttpEntity entity = buildHttpEntity(param);// build entity
                ResponseEntity<T> response = restTemplate.exchange(url, method, entity, clazz);
                return response.getBody();
            } catch (ResourceAccessException ex) {
                log.warn("fail restTemplateExchange", ex);
                setFailCount(url, method.toString());
                if (isServiceSleep(url, method.toString())) {
                    throw ex;
                }
                log.info("continue next front");
                continue;
            } catch (HttpStatusCodeException ex) {
                errorFormat(ex.getResponseBodyAsString());
            }
        }
        throw new BaseException(ConstantCode.REQUEST_FRONT_FAIL);
    }

    /**
     * build url of front service.
     */
    private String buildFrontUrl(ArrayList<FrontGroup> list, String uri, HttpMethod httpMethod) {
        Collections.shuffle(list);// random one
        log.debug("====================map list:{}", JacksonUtils.objToString(list));
        Iterator<FrontGroup> iterator = list.iterator();
        while (iterator.hasNext()) {
            FrontGroup frontGroup = iterator.next();
            log.debug("============frontGroup:{}", JacksonUtils.objToString(frontGroup));

            uri = uriAddGroupId(frontGroup.getGroupId(), uri);// append groupId to uri
            String url = String
                    .format(FRONT_URL, frontGroup.getFrontIp(), frontGroup.getFrontPort(), uri)
                    .replaceAll(" ", "");
            iterator.remove();

            if (isServiceSleep(url, httpMethod.toString())) {
                log.warn("front url[{}] is sleep,jump over", url);
                continue;
            }
            return url;
        }
        log.info("end buildFrontUrl. url is null");
        return null;
    }

    /**
     * append groupId to uri.
     */
    private static String uriAddGroupId(Integer groupId, String uri) {
        if (groupId == null || StringUtils.isBlank(uri)) {
            return null;
        }

        final String tempUri = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;
        long countNotAppend =
                URI_NOT_PREPEND_GROUP_ID.stream().filter(u -> u.contains(tempUri)).count();
        if (countNotAppend > 0) {
            return uri;
        }
        return groupId + "/" + uri;
    }


    /**
     * build httpEntity.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static HttpEntity buildHttpEntity(Object param) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String paramStr = null;
        if (Objects.nonNull(param)) {
            paramStr = JacksonUtils.objToString(param);
        }
        HttpEntity requestEntity = new HttpEntity(paramStr, headers);
        return requestEntity;
    }

    /**
     * check url status.
     */
    private boolean isServiceSleep(String url, String methType) {
        // get failInfo
        String key = buildKey(url, methType);
        FailInfo failInfo = failRequestMap.get(key);

        // cehck server status
        if (failInfo == null) {
            return false;
        }
        int failCount = failInfo.getFailCount();
        Long subTime = Duration.between(failInfo.getLatestTime(), Instant.now()).toMillis();
        if (failCount > cproperties.getMaxRequestFail()
                && subTime < cproperties.getSleepWhenHttpMaxFail()) {
            return true;
        } else if (subTime > cproperties.getSleepWhenHttpMaxFail()) {
            // service is sleep
            deleteKeyOfMap(failRequestMap, key);
        }
        return false;

    }

    /**
     * set request fail times.
     */
    private void setFailCount(String url, String methodType) {
        // get failInfo
        String key = buildKey(url, methodType);
        FailInfo failInfo = failRequestMap.get(key);
        if (failInfo == null) {
            failInfo = new FailInfo();
            failInfo.setFailUrl(url);
        }

        // reset failInfo
        failInfo.setLatestTime(Instant.now());
        failInfo.setFailCount(failInfo.getFailCount() + 1);
        failRequestMap.put(key, failInfo);
        log.info("the latest failInfo:{}", JacksonUtils.objToString(failRequestMap));
    }


    /**
     * build key description: frontIp$frontPort example: 2651654951545$8081.
     */
    private String buildKey(String url, String methodType) {
        return url.hashCode() + "$" + methodType;
    }


    /**
     * delete key of map.
     */
    private static void deleteKeyOfMap(Map<String, FailInfo> map, String rkey) {
        log.debug("start deleteKeyOfMap. rkey:{} map:{}", rkey, JacksonUtils.objToString(map));
        Iterator<String> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (rkey.equals(key)) {
                iter.remove();
            }
        }
    }

    /**
     * front error format
     * 
     * @param error
     */
    private static void errorFormat(String str) {
        JsonNode error = JacksonUtils.stringToJsonNode(str);
        log.error("requestFront fail. error:{}", error);
        if (ObjectUtils.isEmpty(error.get("errorMessage"))) {
            throw new BaseException(ConstantCode.REQUEST_NODE_EXCEPTION);
        }
        String errorMessage = error.get("errorMessage").asText();
        if (errorMessage.contains("code")) {
            JsonNode errorInside =
                    JacksonUtils.stringToJsonNode(error.get("errorMessage").asText()).get("error");
            throw new BaseException(errorInside.get("code").asInt(),
                    errorInside.get("message").asText());
        }
        throw new BaseException(error.get("code").asInt(), errorMessage);
    }
}
