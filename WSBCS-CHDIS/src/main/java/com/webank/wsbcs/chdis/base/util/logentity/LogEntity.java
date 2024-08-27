package com.webank.wsbcs.chdis.base.util.logentity;

import org.apache.commons.lang3.StringUtils;

/**
 * [{"code":"sendNewCard",res_code:0,"msg":"success","bizSeqNo":"123","result":"success",usedTime:
 * 9000}]
 * 
 * @author marsli
 */
public class LogEntity {
    private String code;// 业务码、方法名
    private String res_code;// 错误号、返回码
    private String msg;// 错误信息（监控日志打印方法名，异常告警日志打印描述信息）
    private String bizSeqNo;// 请求流水号
    private Long usedTime;// 方法耗时(为监控日志时，打印耗时)
    private String openid;// 错误信息（监控日志打印方法名，异常告警日志打印描述信息）
    private String result;// 返回结果


    public LogEntity(String code, String openid, String res_code, String msg, String bizSeqNo,
            Long usedTime) {
        this.code = code;
        this.res_code = res_code;
        this.msg = msg;
        this.bizSeqNo = bizSeqNo;
        this.usedTime = usedTime;
        this.openid = openid;
    }

    public static String monitorStr(String methodCode, String resCode, String msg,
            String bizSeqNo, Long costTime) {
        return new LogEntity(methodCode, null, resCode, msg, bizSeqNo, costTime).toString();
    }

    public static String monitorStr(String methodCode, String openid, String resCode, String msg,
            String bizSeqNo, Long costTime) {
        return new LogEntity(methodCode, openid, resCode, msg, bizSeqNo, costTime).toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRes_code() {
        return res_code;
    }

    public void setRes_code(String res_code) {
        this.res_code = res_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBizSeqNo() {
        return bizSeqNo;
    }

    public void setBizSeqNo(String bizSeqNo) {
        this.bizSeqNo = bizSeqNo;
    }

    public Long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Long usedTime) {
        this.usedTime = usedTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    /**
     * 增加字段必须在开头加上 ！！！逗号！！！
     * 
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[{");
        sb.append("\"code\":\"").append(code).append('\"');
        sb.append(",").append("\"openid\":\"").append(openid).append('\"');
        sb.append(",").append("\"res_code\":\"").append(res_code).append('\"');
        sb.append(",").append("\"msg\":\"").append(msg).append('\"');
        sb.append(",").append("\"bizSeqNo\":\"").append(bizSeqNo).append('\"');
        sb.append(",").append("\"usedTime\":\"").append(usedTime).append('\"');
        if (StringUtils.isNotBlank(result)) {
            sb.append(",").append("\"result\":\"").append(result).append('\"');
        }
        sb.append("}]");
        return sb.toString();
    }


}
