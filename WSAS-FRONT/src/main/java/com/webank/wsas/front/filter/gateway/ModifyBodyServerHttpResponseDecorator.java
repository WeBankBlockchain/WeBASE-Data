package com.webank.wsas.front.filter.gateway;

import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import com.webank.wsas.front.config.BasePageResponse;
import com.webank.wsas.front.utils.GlobalLogUtils;
import com.webank.wsas.front.utils.JacksonUtils;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
public class ModifyBodyServerHttpResponseDecorator extends ServerHttpResponseDecorator {

    public ModifyBodyServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (body instanceof Flux) {
            HttpHeaders respHeader = this.getHeaders();
            log.info("resp header --> {}", JacksonUtils.toJSONString(respHeader));
            Flux<DataBuffer> fluxBody = (Flux<DataBuffer>) body;
            Publisher publisherWithBodyModify =
                    fluxBody.buffer().map(new ModifyRespBodyFunc(getStatusCode(), respHeader));
            return super.writeWith(publisherWithBodyModify);
        }
        return super.writeWith(body);
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        log.info("writeAndFlushWith --> {}", body.toString());
        return super.writeAndFlushWith(body);
    }

    class ModifyRespBodyFunc implements Function<List<DataBuffer>, DataBuffer> {

        protected HttpStatus respStatus;
        protected HttpHeaders respHeader;

        public ModifyRespBodyFunc(HttpStatus status, HttpHeaders header) {
            respStatus = status;
            respHeader = header;
        }

        @Override
        public DataBuffer apply(List<DataBuffer> dataBuffers) {
            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
            DataBuffer join = dataBufferFactory.join(dataBuffers);
            byte[] content = new byte[join.readableByteCount()];
            join.read(content);
            DataBufferUtils.release(join); // 释放掉内存
            String bodyStr = new String(content, Charset.forName("UTF-8"));

            bodyStr = modifyBody(bodyStr);

            getDelegate().getHeaders().setContentLength(bodyStr.getBytes().length);
            return bufferFactory().wrap(bodyStr.getBytes());
        }

        // 修改响应体函数
        private String modifyBody(String bodyStr) {
            String tmp = bodyStr.replace(" ", "");
            if (StringUtils.isEmpty(tmp)) {
                bodyStr = tmp;
            }
            log.debug("get resp --> {}", bodyStr);

            // 打印日志
            BasePageResponse resp = GlobalLogUtils.printGlobalRespLog(respHeader, respStatus, bodyStr);// NOSONAR
            return resp == null ? bodyStr : JacksonUtils.toJSONString(resp);
        }
    }
}
