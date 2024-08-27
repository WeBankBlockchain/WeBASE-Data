package com.webank.wsbcs.chdis.base.util.logentity;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类  加入监控埋点和告警error；统一错误日志格式
 */
public class LogUtils {

    private static final Logger MONILOGGER = LoggerFactory.getLogger("appmonitor");

    public static Logger getMonitorLogger() {
        return MONILOGGER;
    }


    public static String trimLongStr(String src) {
        return StringUtils.isNotEmpty(src) ? src.substring(0, Math.min(512, src.length())) : "";
    }
}
