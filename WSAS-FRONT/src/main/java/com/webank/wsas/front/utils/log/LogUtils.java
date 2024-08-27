package com.webank.wsas.front.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 */
public class LogUtils {

    private static final Logger MONILOGGER = LoggerFactory.getLogger("appmonitor");

    public static Logger getMonitorLogger() {
        return MONILOGGER;
    }

}
