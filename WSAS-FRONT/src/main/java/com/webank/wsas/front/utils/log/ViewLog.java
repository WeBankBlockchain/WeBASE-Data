package com.webank.wsas.front.utils.log;

import lombok.Data;

/**
 * log format: "0start或OnSuccess或者OnError|1methodName|2bizSeqNo|3openid|4code|5usedTime|6data" 7个位置
 * log format: {}|{}|{}|{}|{}|{}|{}
 *
 * @author marsli
 * @date 2022/1/19
 */

@Data
public class ViewLog {

    public static enum Status {
        OnSuccess, OnError, start
    }

    private Status status;
    private String methodName;//业务码、方法名
    private String bizSeqNo;// 请求流水号
    private String resCode;//错误号、返回码
    private Long usedTime;//方法耗时(为监控日志时，打印耗时)
    private String data;//返回结果

    public ViewLog() {
    }

    public ViewLog(MonitorLog monitorLog) {
        this.methodName = monitorLog.getCode();
        this.bizSeqNo = monitorLog.getBizSeqNo();
        this.resCode = monitorLog.getRes_code();
        this.usedTime = monitorLog.getUsedTime();
    }

    /**
     * 增加字段需要同步修改 awk -F"|" '{print $4}' | awk -F":" '{print $2}' 输出用户openid的脚本的变量下表
     *
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        sb.append("status:").append(status.name()).append('|');
        sb.append("res_code:").append(resCode).append('|');
        sb.append("data:").append(data).append('|');
        sb.append("usedTime:").append(usedTime).append('|');
        sb.append("methodName:").append(methodName).append('|');
        sb.append("bizSeqNo:").append(bizSeqNo).append('|');
        sb.append("]");
        return sb.toString();
    }
}
