package com.webank.webase.data.collect.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.chain.entity.TbChain;
import com.webank.webase.data.collect.config.entity.ConfigVersionInfo;
import com.webank.webase.data.collect.front.entity.TbFront;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.rest.AbstractRestService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class WebServerRestTools extends AbstractRestService {

    public static final String WEB_SERVER_URL = "http://%1s/UPB-WebServer/";
    public static final String URI_CONFIG_VERSION = "fisco-data-proxy/config/version";
    public static final String URI_CHAIN_ALL = "fisco-data-proxy/chain/all";
    public static final String URI_FRONT_LIST = "fisco-data-proxy/front/list";
    public static final String URI_GROUP_LIST = "fisco-data-proxy/group/list";
    public static final String URI_USER_LIST = "fisco-data-proxy/user/list/%1s/%2s/%3s";
    public static final String URI_CONTRACT_LIST = "fisco-data-proxy/contract/list/%1s/%2s";

    @Autowired
    private ConstantProperties constantProperties;

    public ConfigVersionInfo getConfigVersion() {
        String url = String.format(WEB_SERVER_URL, constantProperties.getConfigServerIpPort())
                + URI_CONFIG_VERSION;
        BaseResponse response = httpGet(url, BaseResponse.class);
        return JacksonUtils.objToJavaBean(response.getData(), ConfigVersionInfo.class);
    }

    public List<TbChain> getChainList() {
        String url = String.format(WEB_SERVER_URL, constantProperties.getConfigServerIpPort())
                + URI_CHAIN_ALL;
        BasePageResponse response = httpGet(url, BasePageResponse.class);
        return JacksonUtils.objToJavaBean(response.getData(),
                new TypeReference<List<TbChain>>() {});
    }

    public List<TbFront> getFrontList() {
        String url = String.format(WEB_SERVER_URL, constantProperties.getConfigServerIpPort())
                + URI_FRONT_LIST;
        BasePageResponse response = httpGet(url, BasePageResponse.class);
        return JacksonUtils.objToJavaBean(response.getData(),
                new TypeReference<List<TbFront>>() {});
    }

    public List<TbGroup> getGroupList() {
        String url = String.format(WEB_SERVER_URL, constantProperties.getConfigServerIpPort())
                + URI_GROUP_LIST;
        BasePageResponse response = httpGet(url, BasePageResponse.class);
        return JacksonUtils.objToJavaBean(response.getData(),
                new TypeReference<List<TbGroup>>() {});
    }

    public BasePageResponse getUserList(Integer groupId, Integer pageNumber, Integer pageSize) {
        String url = String.format(WEB_SERVER_URL, constantProperties.getConfigServerIpPort())
                + String.format(URI_USER_LIST, groupId, pageNumber, pageSize);
        BasePageResponse response = httpGet(url, BasePageResponse.class);
        return response;
    }

    public BasePageResponse getContractList(Integer pageNumber, Integer pageSize) {
        String url = String.format(WEB_SERVER_URL, constantProperties.getConfigServerIpPort())
                + String.format(URI_CONTRACT_LIST, pageNumber, pageSize);
        BasePageResponse response = httpGet(url, BasePageResponse.class);
        return response;
    }

    @Override
    public <T> T httpGet(String url, Class<T> clazz) {
        return restTemplateExchange(url, HttpMethod.GET, null, clazz);
    }

    @Override
    public <T> T httpPost(String url, Object param, Class<T> clazz) {
        return restTemplateExchange(url, HttpMethod.POST, param, clazz);
    }

    @Override
    public <T> T httpPut(String url, Object param, Class<T> clazz) {
        return restTemplateExchange(url, HttpMethod.PUT, param, clazz);
    }

    @Override
    public <T> T httpDelete(String url, Object param, Class<T> clazz) {
        return restTemplateExchange(url, HttpMethod.DELETE, param, clazz);
    }

    protected <T> T restTemplateExchange(String url, HttpMethod method, Object param,
            Class<T> clazz) {
        try {
            return restTemplate(url, method, param, clazz);
        } catch (ResourceAccessException e) {
            log.error("fail requestremote server. ResourceAccessException.", e);
        } catch (HttpStatusCodeException e) {
            AbstractRestService.commonErrorFormat(e.getResponseBodyAsString());
        }
        throw new BaseException(ConstantCode.REMOTE_SERVER_REQUEST_FAIL);
    }

}
