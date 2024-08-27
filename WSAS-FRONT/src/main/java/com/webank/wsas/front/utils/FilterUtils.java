package com.webank.wsas.front.utils;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import com.webank.wsas.front.config.BaseResponse;
import com.webank.wsas.front.config.ErrorCodes;
import io.netty.buffer.ByteBufAllocator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FilterUtils {

    public static Mono<Void> returnError(ErrorCodes.ErrorCode code, String bizSeqNo,
            ServerWebExchange exchange) {
        BaseResponse data = new BaseResponse(code.eCode, code.errorMsg, bizSeqNo);
        GlobalLogUtils.printLocalRespLog(exchange, data, bizSeqNo);
        return writeToResp(data, exchange.getResponse());
    }

    public static Mono<Void> writeToResp(Object data, ServerHttpResponse response) {
        DataBuffer buffer =
                response.bufferFactory().wrap(JacksonUtils.toJSONString(data).getBytes());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Flux.just(buffer));
    }

    public static DataBuffer buildBufferFromString(String src) {
        byte[] bSrc = src.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory factory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = factory.allocateBuffer(bSrc.length);
        buffer.write(bSrc);
        return buffer;
    }
}
