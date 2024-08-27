package com.webank.wsas.front.config;

import java.util.Arrays;
import java.util.List;

public class ErrorCodes {

    public static final ErrorCode internalError = new ErrorCode("system error", 43671001);

    public static final ErrorCode paramsError = new ErrorCode("parameter error", 43672001);

    public static final ErrorCode limitError = new ErrorCode("rate limit exceeded", 43672002);

    public final static List<ErrorCode> CODE_ALARM_LIST = Arrays.asList(internalError);

    public static boolean isAlarmCode(int code) {
        long count = CODE_ALARM_LIST.stream().filter(c -> c.eCode == code).count();
        return count > 0;
    }

    public static class ErrorCode {
        public String errorMsg;
        public int eCode;

        public ErrorCode(String errorMsg, int eCode) {
            this.errorMsg = errorMsg;
            this.eCode = eCode;
        }

        public ErrorCode addMsg(String extMsg) {
            return new ErrorCode(this.errorMsg + (" : " + extMsg), this.eCode);
        }
    }
}
