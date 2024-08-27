package com.webank.wsas.front.utils.log;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * [{"code":"sendNewCard",res_code:0,"msg":"success","bizSeqNo":"123","result":"success",usedTime:
 * 9000}]
 *
 * @author marsli
 */

@Data
public class MonitorLog {

    private String code;// 业务码、方法名

    public void setRes_code(String res_code) {
        if ("200".equals(res_code)) {
            res_code = "0";
        }
        this.res_code = res_code;
    }

    private String res_code;// 错误号、返回码
    private String msg;// 错误信息（监控日志打印方法名，异常告警日志打印描述信息）
    private String bizSeqNo;// 请求流水号
    private Long usedTime;// 方法耗时(为监控日志时，打印耗时)
    private String result;// 返回结果

    /**
     * 增加字段必须在开头加上 ！！！逗号！！！
     *
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[{");
        sb.append("\"code\":\"").append(code).append('\"');
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
