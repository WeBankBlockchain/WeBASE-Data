package com.webank.wsbcs.chdis.base.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * build httpEntity.
 */
public class HttpEntityUtils {

    /**
     * @param host
     * @return
     */
    public static HttpEntity buildHttpEntityByHost(String host) {
        HttpHeaders headers = buildHttpHeaderByHost(host);
        return buildHttpEntity(headers);
    }


    /**
     * param or HttpHeaders
     *
     * @param param
     * @return
     */
    public static HttpEntity buildHttpEntity(Object param) {
        if (param instanceof HttpHeaders) {
            HttpHeaders headers = (HttpHeaders) param;
            return buildHttpEntity(headers, null);
        }
        return buildHttpEntity(null, param);
    }


    /**
     * build httpEntity
     */
    public static HttpEntity buildHttpEntity(HttpHeaders httpHeaders, Object param) {
        if (Objects.isNull(httpHeaders)) {
            httpHeaders = instantiateHttpHeaders();
        }
        if (Objects.isNull(httpHeaders.getContentType())) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        String paramStr = null;
        if (Objects.nonNull(param)) {
            paramStr = JsonUtils.toJSONString(param);
        }
        HttpEntity requestEntity = new HttpEntity(paramStr, httpHeaders);
        return requestEntity;
    }


    /**
     * @param host
     * @return
     */
    public static HttpHeaders buildHttpHeaderByHost(String host) {
        HttpHeaders headers = instantiateHttpHeaders();
        if (StringUtils.isNotBlank(host)) {
            headers.set(HttpHeaders.HOST, host);
        }
        return headers;
    }

    /**
     * @return
     */
    public static HttpHeaders instantiateHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");//支持自定义请求头
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * 流水号header
     * @param bizSeqNo
     * @return
     */
    public static HttpHeaders buildHttpHeaderBiz(String bizSeqNo) {
        HttpHeaders headers = instantiateHttpHeaders();
        headers.set("bizSeqNo", bizSeqNo);
        return headers;
    }

    /**
     * get uri.
     */
    public static String getUri(HttpServletRequest httpRequest) {
        String uri = httpRequest.getRequestURI().replace("//", "/");
        String contextPath = httpRequest.getContextPath();
        return StringUtils.removeStart(uri, contextPath);
    }


    /**
     * convert map to query params
     * ex: uri:permission,
     *     params: (groupId, 1) (address, 0x01)
     *
     * result: permission?groupId=1&address=0x01
     */
    public static String getQueryUri(String uriHead, Map<String, String> map) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(entry.getKey(), entry.getValue());
        }

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .queryParams(params).build();

        return uriHead + uriComponents.toString();
    }
}
