package com.webank.wsbcs.chdis.base.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.webank.wsbcs.chdis.base.util.BizSeqNoUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CustomHeaderFilter implements Filter {

    private static final String BIZSEQNO = "bizSeqNo";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        CustomRequestWrapper requestWrapper = new CustomRequestWrapper(req);
        String bizSeqNo = req.getHeader(BIZSEQNO);
        if (StringUtils.isBlank(bizSeqNo)) {
            bizSeqNo = BizSeqNoUtil.getBizSeqNo();
            log.debug("bizSeqNo is null. auto create:{}", bizSeqNo);
            requestWrapper.addHeader(BIZSEQNO, bizSeqNo);
        }
        chain.doFilter(requestWrapper, response);
    }
}
