/**
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.wsbcs.chdis.base.aspect;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.code.RetCode;
import com.webank.wsbcs.chdis.base.entity.BasePageResponse;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.base.util.logentity.LogEntity;
import com.webank.wsbcs.chdis.base.util.logentity.LogUtils;
import com.webank.wsbcs.chdis.base.util.logentity.LoggerManager;
import com.webank.wsbcs.chdis.base.util.logentity.MonitorLog;

@Aspect
@Component
public class LogAspect {

    private final String POINT_CUT =
            "execution(public * com.webank.wsbcs.chdis.*..*Controller*.*(..))";

    @Pointcut(POINT_CUT)
    public void logPointCut() {
        // 空函数，用于正则匹配切面
    }

    @Around("logPointCut()")
    public Object methodAround(ProceedingJoinPoint point) throws Throwable {
        Instant startTime = Instant.now();
        Class targetClass = point.getTarget().getClass();// NOSONAR
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        String methodName = methodSignature.getName();
        Object[] args = point.getArgs();
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String bizSeqNo = request.getHeader("bizSeqNo");

        Logger logger = LoggerManager.getLogger(targetClass);
        // logger args of param in controller
        // if args contains BindingResult(recursive of request entity and itself), stack over flow
        logger.info("startTime:{} methodName:{} args:{}", startTime, methodName,
                JsonUtils.toJSONString(this.excludeBindingResult(args)));
        Object result = null;
        try {
            result = point.proceed();
        } catch (BaseException exception) {
            // 不成功且不可忽略的错误码
            RetCode baseResponse = exception.getRetCode();
            if (!baseResponse.getCode().equals(ConstantCode.SUCCESS.getCode())
                    && ConstantCode.isAlarmCode(baseResponse.getCode())) {
                LogUtils.getMonitorLogger()
                        .error(LogEntity.monitorStr(methodName,
                                String.valueOf(baseResponse.getCode()), baseResponse.getMessage(),
                                bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
                logger.error("{}",
                        MonitorLog.monitorStr("OnError", methodName, bizSeqNo,
                                String.valueOf(baseResponse.getCode()),
                                Duration.between(startTime, Instant.now()).toMillis(),
                                JsonUtils.toJSONString(baseResponse)));
            }
            throw exception;
        } // 额外的报错，由ExceptionHandler获取，报错system error

        // 反序列化为baseResponse
        BasePageResponse baseResponse =
                Optional.ofNullable(result)
                        .map(r -> JsonUtils.stringToObj(JsonUtils.objToString(r),
                                BasePageResponse.class))
                        .orElseThrow(() -> new BaseException(ConstantCode.SYSTEM_EXCEPTION));

        // 直接打印

        logger.info("{}",
                MonitorLog.monitorStr("OnSuccess", methodName, bizSeqNo,
                    String.valueOf(baseResponse.getCode()),
                    Duration.between(startTime, Instant.now()).toMillis(),
                    JsonUtils.toJSONString(baseResponse))
        );
        // monitor日志
        LogUtils.getMonitorLogger()
                .info(LogEntity.monitorStr(methodName, String.valueOf(baseResponse.getCode()),
                        baseResponse.getMessage(), baseResponse.getBizSeqNo(),
                        Duration.between(startTime, Instant.now()).toMillis()));

        return result;
    }

    private List<Object> excludeBindingResult(Object[] params) {
        List<Object> retainParams = new ArrayList<>();
        for (int index = 0; index < params.length; index++) {
            if (!(params[index] instanceof BindingResult || params[index] instanceof ServletRequest
                    || params[index] instanceof ServletResponse
                    || params[index] instanceof MultipartFile)) {
                retainParams.add(params[index]);
            }
        }
        return retainParams;
    }


}
