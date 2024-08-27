package com.webank.wsas.front.filter.global;

import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.webank.wsas.front.config.ErrorCodes;
import com.webank.wsas.front.config.HeadRequestKey;
import com.webank.wsas.front.filter.gateway.ModifyBodyServerHttpRequestDecorator;
import com.webank.wsas.front.filter.gateway.ModifyBodyServerHttpResponseDecorator;
import com.webank.wsas.front.utils.FilterUtils;
import com.webank.wsas.front.utils.JacksonUtils;
import com.webank.wsas.front.utils.UuidUtils;
import com.webank.wsas.front.utils.log.ViewLog;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class GlobalModifyBodyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        ViewLog startLog = new ViewLog();
        startLog.setStatus(ViewLog.Status.start);

        ServerHttpRequest.Builder requestModifyBuild = request.mutate();
        String bizSeqNo = null;
        String orgBizSeqNo = headers.getFirst(HeadRequestKey.BIZSEQNO);
        if (orgBizSeqNo == null || StringUtils.isEmpty(orgBizSeqNo.trim())) {
            bizSeqNo = UuidUtils.getBizSeqNo();
            requestModifyBuild.header(HeadRequestKey.BIZSEQNO, bizSeqNo);
        } else {
            bizSeqNo = orgBizSeqNo;
        }
        startLog.setBizSeqNo(bizSeqNo);
        ServerHttpRequestDecorator requestModifyDecorator =
                new ModifyBodyServerHttpRequestDecorator(requestModifyBuild.build(), headers, null);
        ServerWebExchange.Builder newExchangeBuilder =
                exchange.mutate().request(requestModifyDecorator);

        if (request.getMethod() == HttpMethod.POST) {
            String postBody = exchange.getAttribute(HeadRequestKey.postBody);
            log.info("GlobalModifyBodyFilter bizSeqNo:{} postBody:{}", bizSeqNo, postBody);
            if (StringUtils.isNotBlank(postBody) && !JacksonUtils.isJson(postBody)) {
                return FilterUtils.returnError(ErrorCodes.paramsError, bizSeqNo, exchange);
            }
        }
        String sPath = request.getPath().toString();
        sPath = sPath.startsWith("/") ? sPath.substring(1) : sPath;
        String methodName = sPath.replace("/", "::");

        startLog.setMethodName(methodName);
        response.getHeaders().set(HeadRequestKey.METHOD_NAME, methodName);
        response.getHeaders().set(HeadRequestKey.BIZSEQNO, bizSeqNo);
        response.getHeaders().set(HeadRequestKey.START_TIME, startTime.toEpochMilli() + "");

        ModifyBodyServerHttpResponseDecorator responseDecorator =
                new ModifyBodyServerHttpResponseDecorator(response);
        ServerWebExchange newExchangeWithResp =
                newExchangeBuilder.response(responseDecorator).build();

        log.info(startLog.toString());
        return chain.filter(newExchangeWithResp);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
