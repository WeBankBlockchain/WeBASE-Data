package com.webank.wsas.front.filter.gateway;

import java.util.function.Predicate;
import org.springframework.stereotype.Component;
import com.webank.wsas.front.utils.JacksonUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class RequestBodyCachePredicate implements Predicate<Object> {
    @Override
    public boolean test(Object o) {
        log.info("get req body-->{}", JacksonUtils.toJSONString(o));
        return true;
    }
}
