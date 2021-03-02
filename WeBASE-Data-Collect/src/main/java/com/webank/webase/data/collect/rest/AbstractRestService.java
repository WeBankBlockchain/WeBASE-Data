package com.webank.webase.data.collect.rest;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.JacksonUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public abstract class AbstractRestService implements RestService {

    @Autowired
    private RestTemplate restTemplate;
    
    private static String separator = "/";

    /**
     * rest template.
     * 
     * @param url
     * @param method
     * @param param
     * @param clazz
     * @return
     */
    public <T> T restTemplate(String url, HttpMethod method, Object param, Class<T> clazz) {
        if (null == restTemplate) {
            log.error("fail exec method [restTemplateExchange]. restTemplate is null");
            throw new BaseException(ConstantCode.SYSTEM_EXCEPTION);
        }
        HttpEntity<?> entity = buildHttpEntity(param);// build entity
        ResponseEntity<T> response = restTemplate.exchange(url, method, entity, clazz);
        return response.getBody();
    }

    /**
     * build httpEntity
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HttpEntity buildHttpEntity(Object param) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String paramStr = null;
        if (Objects.nonNull(param)) {
            paramStr = JacksonUtils.objToString(param);
        }
        HttpEntity requestEntity = new HttpEntity(paramStr, headers);
        return requestEntity;
    }
    
    /**
     * url format
     * 
     */
    public static String urlFormat(String str)  {
        if (str.endsWith(separator)) {
            return str;
        }
        return str + separator;
    }

    /**
     * front error format
     * 
     * @param error
     */
    public static void frontErrorFormat(String str) throws BaseException {
        JsonNode error = JacksonUtils.stringToJsonNode(str);
        log.error("requestFront fail. error:{}", error);
        if (ObjectUtils.isEmpty(error.get("errorMessage"))) {
            throw new BaseException(ConstantCode.REQUEST_NODE_EXCEPTION);
        }
        String errorMessage = error.get("errorMessage").asText();
        if (errorMessage.contains("code")) {
            if (errorMessage.contains("error")) {
                JsonNode errorInside = JacksonUtils.stringToJsonNode(errorMessage).get("error");
                throw new BaseException(errorInside.get("code").asInt(), errorInside.get("message").asText());
            } else {
                JsonNode errorInside = JacksonUtils.stringToJsonNode(errorMessage);
                throw new BaseException(errorInside.get("code").asInt(), errorInside.get("msg").asText());
            }
        }
        throw new BaseException(error.get("code").asInt(), errorMessage);
    }

    /**
     * common error format
     * 
     * @param error
     */
    public static void commonErrorFormat(String str) throws BaseException {
        JsonNode error = JacksonUtils.stringToJsonNode(str);
        log.error("fail exec method [commonErrorFormat]. error:{}", error);
        if (ObjectUtils.isEmpty(error.get("message"))) {
            throw new BaseException(ConstantCode.REMOTE_SERVER_REQUEST_FAIL);
        }
        String message = error.get("message").asText();
        if (message.contains("code")) {
            JsonNode errorInside = error.get("message").get("error");
            throw new BaseException(errorInside.get("code").asInt(), errorInside.get("message").asText());
        }
        throw new BaseException(error.get("code").asInt(), message);
    }

    /**
     * @param response
     * @param dataClass
     * @param <T>
     * @return
     */
    public static <T> T getResultData(BaseResponse response, Class<T> dataClass) {
        if (dataClass == null) {
            log.info("finish exec method [getResultData]. dataClass is null,return null");
            return null;
        }
        if (Objects.isNull(response)) {
            log.info("finish exec method [getResultData]. response is null,return null");
            return null;
        }
        if (ConstantCode.SUCCESS.getCode() != response.getCode()) {
            log.info("finish exec method [getResultData]. response code:{} message:{}", response.getCode(),
                    response.getMessage());
            throw new BaseException(response.getCode(), response.getMessage());
        }

        T t = JacksonUtils.objToJavaBean(response.getData(), dataClass);
        log.debug("success exec method [getResultData]. data:{}", JacksonUtils.objToString(response.getData()));
        return t;
    }
}
