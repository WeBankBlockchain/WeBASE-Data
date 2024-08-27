package com.webank.wsas.front.utils;

import java.time.Duration;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import com.webank.wsas.front.config.BasePageResponse;
import com.webank.wsas.front.config.BaseResponse;
import com.webank.wsas.front.config.ErrorCodes;
import com.webank.wsas.front.config.HeadRequestKey;
import com.webank.wsas.front.utils.log.LogUtils;
import com.webank.wsas.front.utils.log.MonitorLog;
import com.webank.wsas.front.utils.log.ViewLog;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GlobalLogUtils {

    public static void printLocalRespLog(ServerWebExchange exchange, BaseResponse resp,
            String bizSeqNo) {
        
        ServerHttpRequest request = exchange.getRequest();
        
        String sPath = request.getPath().toString();
        sPath = sPath.startsWith("/") ? sPath.substring(1) : sPath;
        String methodName = sPath.replace("/", "::");
        
        MonitorLog monitorLogEntity = new MonitorLog();
        monitorLogEntity.setBizSeqNo(bizSeqNo);
        monitorLogEntity.setCode(methodName);
        monitorLogEntity.setUsedTime(0L);
        monitorLogEntity.setRes_code(String.valueOf(HttpStatus.OK.value()));
        monitorLogEntity.setMsg(resp.getMessage());
        
        ViewLog viewLog = new ViewLog(monitorLogEntity);
        viewLog.setData(JacksonUtils.toJSONString(resp));
        if (ErrorCodes.isAlarmCode(resp.getCode())) {
            viewLog.setStatus(ViewLog.Status.OnError);
            log.error(viewLog.toString());
            
            monitorLogEntity.setRes_code(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            LogUtils.getMonitorLogger().error(monitorLogEntity.toString());
        } else {
            viewLog.setStatus(ViewLog.Status.OnSuccess);
            log.info(viewLog.toString());
            
            LogUtils.getMonitorLogger().info(monitorLogEntity.toString());
        }
    }
    
    public static BasePageResponse printGlobalRespLog(HttpHeaders respHeader, HttpStatus respStatus,
            String respBody) {

        String bizSeqNo = respHeader.getFirst(HeadRequestKey.BIZSEQNO);
        String methodName = respHeader.getFirst(HeadRequestKey.METHOD_NAME);
        String startTimeStr = respHeader.getFirst(HeadRequestKey.START_TIME);
        Instant startTime = null;
        if (!StringUtils.isEmpty(startTimeStr)) {
            startTime = Instant.ofEpochMilli(Long.parseLong(startTimeStr));
        }
        Long spendTime =
                startTime == null ? -1 : Duration.between(startTime, Instant.now()).toMillis();

        MonitorLog monitorLogEntity = new MonitorLog();
        monitorLogEntity.setBizSeqNo(bizSeqNo);
        monitorLogEntity.setCode(methodName);
        monitorLogEntity.setUsedTime(spendTime);
        monitorLogEntity.setResult(respStatus.getReasonPhrase());

        ViewLog viewLog = new ViewLog(monitorLogEntity);
        String data = null;
        BasePageResponse resp = null;
        if (!StringUtils.isEmpty(respBody)) {
            data = respBody.length() > 2000 ? null : respBody;
            if (JacksonUtils.isJson(respBody)) {
                resp = JacksonUtils.toJavaObject(respBody, BasePageResponse.class);
                monitorLogEntity.setMsg(resp.getMessage());
            }
        }
        if (resp == null && respStatus != HttpStatus.OK) {
            viewLog.setStatus(ViewLog.Status.OnError);
            viewLog.setData(data);
            log.error(viewLog.toString());
            
            monitorLogEntity.setRes_code(String.valueOf(respStatus.value()));
            LogUtils.getMonitorLogger().error(monitorLogEntity.toString());
        } else {
            viewLog.setStatus(ViewLog.Status.OnSuccess);
            viewLog.setData(data);
            log.info(viewLog.toString());
            
            monitorLogEntity.setRes_code(String.valueOf(HttpStatus.OK.value()));
            LogUtils.getMonitorLogger().info(monitorLogEntity.toString());
        }
        return resp;
    }
}
