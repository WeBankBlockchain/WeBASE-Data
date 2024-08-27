package com.webank.wsbcs.chdis.base.util.logentity;

/**
 * log format: "0start或OnSuccess或者OnError|1methodName|2bizSeqNo|3openid|4code|5usedTime|6data" 7个位置
 * log format: {}|{}|{}|{}|{}|{}|{}
 * @author marsli
 * @date 2022/1/19
 */
public class MonitorLog {
    private String status;//OnSuccess,OnError,start
    private String methodName;//业务码、方法名
    private String bizSeqNo;// 请求流水号
    private String openid;//用户Id
    private String res_code;//错误号、返回码
    private Long usedTime;//方法耗时(为监控日志时，打印耗时)
    private String data;//返回结果

    public MonitorLog(String status, String methodName, String bizSeqNo, String openid, String res_code, Long usedTime, String data) {
        this.status = status;
        this.methodName = methodName;
        this.bizSeqNo = bizSeqNo;
        this.openid = openid;
        this.res_code = res_code;
        this.usedTime = usedTime;
        this.data = data;
    }
    
    public static String monitorStr(String status, String methodName, String bizSeqNo,  String data) {
        return new MonitorLog(status, methodName, bizSeqNo, null, null, null, data).toString();
    }

    public static String monitorStr(String status, String methodName, String bizSeqNo, String openid, String data) {
        return new MonitorLog(status, methodName, bizSeqNo, openid, null, null, data).toString();
    }
    
    public static String monitorStr(String status, String methodName, String bizSeqNo, String res_code, Long usedTime, String data) {
        return new MonitorLog(status, methodName, bizSeqNo, null, res_code, usedTime, data).toString();
    }

    public static String monitorStr(String status, String methodName, String bizSeqNo, String openid, String res_code, Long usedTime, String data) {
        return new MonitorLog(status, methodName, bizSeqNo, openid, res_code, usedTime, data).toString();
    }

    /**
     * 增加字段需要同步修改 awk -F"|" '{print $4}' | awk -F":" '{print $2}' 输出用户openid的脚本的变量下表
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        sb.append("status:").append(status).append('|');
        sb.append("methodName:").append(methodName).append('|');
        sb.append("bizSeqNo:").append(bizSeqNo).append('|');
        sb.append("openid:").append(openid).append('|');
        sb.append("res_code:").append(res_code).append('|');
        sb.append("usedTime:").append(usedTime).append('|');
        sb.append("data:").append(data);
        sb.append("]");
        return sb.toString();
    }
}
