package com.webank.wsas.front.filter.gateway;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

public class ModifyBodyServerHttpRequestDecorator extends ServerHttpRequestDecorator {

    public ModifyBodyServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }

    HttpHeaders headers;
    Flux<DataBuffer> bodyFlux;

    public ModifyBodyServerHttpRequestDecorator(ServerHttpRequest delegate, HttpHeaders headers, Flux<DataBuffer> bodyFlux) {
        super(delegate);
        this.headers = headers;
        this.bodyFlux = bodyFlux;
    }

    @Override
    public HttpHeaders getHeaders() {
        long contentLength = headers.getContentLength();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(super.getHeaders());
        if (contentLength > 0) {
            httpHeaders.setContentLength(contentLength);
        } else {
            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
        }
        return httpHeaders;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        if (bodyFlux == null) {
            return super.getBody();
        }
        return bodyFlux;
    }
}