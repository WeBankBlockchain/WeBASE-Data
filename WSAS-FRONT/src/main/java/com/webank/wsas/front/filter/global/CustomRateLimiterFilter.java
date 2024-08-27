package com.webank.wsas.front.filter.global;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.google.common.util.concurrent.RateLimiter;
import com.webank.wsas.front.config.ErrorCodes;
import com.webank.wsas.front.config.HeadRequestKey;
import com.webank.wsas.front.utils.FilterUtils;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class CustomRateLimiterFilter implements GlobalFilter, Ordered {

    @Autowired
    private Map<String, RateLimiter> limiterMap;

    private final static String LIMIT_GET = "get";
    private final static String LIMIT_POST = "post";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String bizSeqNo = exchange.getRequest().getHeaders().getFirst(HeadRequestKey.BIZSEQNO);
        // 如果没有获取到令牌，则拒绝请求或进行其他处理
        // 这里可以返回错误响应或者进行其他逻辑处理
        if (exchange.getRequest().getMethod() == HttpMethod.GET
                && !limiterMap.get(LIMIT_GET).tryAcquire()) {
            return FilterUtils.returnError(ErrorCodes.limitError, bizSeqNo, exchange);
        }
        if (exchange.getRequest().getMethod() == HttpMethod.POST
                && !limiterMap.get(LIMIT_POST).tryAcquire()) {
            return FilterUtils.returnError(ErrorCodes.limitError, bizSeqNo, exchange);
        }
        // 获取到令牌，继续处理请求
        log.info("rateLimiter success. bizSeqNo:{}", bizSeqNo);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置过滤器的执行顺序，值越小，执行顺序越高
        return -1;
    }
}
