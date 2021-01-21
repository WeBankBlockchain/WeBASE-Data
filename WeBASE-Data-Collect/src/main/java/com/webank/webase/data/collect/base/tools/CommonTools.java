/**
 * Copyright 2014-2020 the original author or authors.
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
package com.webank.webase.data.collect.base.tools;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.code.RetCode;
import com.webank.webase.data.collect.base.entity.BaseResponse;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.pagetools.entity.MapHandle;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.utils.Numeric;

/**
 * common method.
 */
@Log4j2
public class CommonTools {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_NO_SPACE = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_NO_SPACE = "yyyyMMdd";

    /**
     * convert hex to localDateTime.
     */
    public static LocalDateTime hex2LocalDateTime(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return null;
        }
        Long timeLong = Long.parseLong(hexStr, 16);
        return timestamp2LocalDateTime(timeLong);
    }

    /**
     * convert timestamp to localDateTime.
     */
    public static LocalDateTime timestamp2LocalDateTime(Long inputTime) {
        if (inputTime == null) {
            log.warn("timestamp2LocalDateTime fail. inputTime is null");
            return null;
        }
        Instant instant = Instant.ofEpochMilli(inputTime);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * convert String to localDateTime.
     */
    public static LocalDateTime string2LocalDateTime(String time, String format) {
        if (StringUtils.isBlank(time)) {
            log.info("string2LocalDateTime. time is null");
            return null;
        }
        if (StringUtils.isBlank(format)) {
            log.info("string2LocalDateTime. format is null");
            format = DEFAULT_DATE_TIME_FORMAT;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    /**
     * convert localDateTime to String.
     */
    public static String localDateTime2String(LocalDateTime dateTime, String format) {
        if (dateTime == null) {
            log.warn("localDateTime2String. dateTime is null");
            return null;
        }
        if (StringUtils.isBlank(format)) {
            log.info("localDateTime2String. format is null");
            format = DEFAULT_DATE_TIME_FORMAT;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        String localTimeStr = df.format(dateTime);
        return localTimeStr;
    }

    /**
     * get x509 cert's fingerprint Hash using: SHA-1
     * 
     * @param byteArray
     * @return
     */
    public static String getCertFingerPrint(byte[] byteArray) {
        byte[] hashResult;
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
            hashResult = sha.digest(byteArray);
            return Numeric.toHexStringNoPrefix(hashResult).toUpperCase();
        } catch (Exception e) {
            log.error("shaEncode getCertFingerPrint fail:", e);
            return null;
        }
    }

    /**
     * sort list and convert to String.
     */
    private static String list2SortString(List<String> values) {
        if (values == null) {
            throw new NullPointerException("values is null");
        }

        values.removeAll(Collections.singleton(null));// remove null
        Collections.sort(values);

        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * convert list to url param.
     */
    public static String convertUrlParam(List<String> nameList, List<Object> valueList) {
        if (nameList == null || valueList == null || nameList.size() != valueList.size()) {
            log.error("fail convertUrlParm. nameList or valuesList is error");
            return "";
        }
        StringBuilder urlParamB = new StringBuilder("");
        for (int i = 0; i < valueList.size(); i++) {
            Object value = valueList.get(i);
            if (value != null) {
                urlParamB.append("&").append(nameList.get(i)).append("=").append(value);
            }
        }

        if (urlParamB.length() == 0) {
            log.info("fail convertUrlParam. urlParamB length is 0");
            return "";
        }

        String urlParam = urlParamB.toString();
        return urlParam.substring(1);

    }

    /**
     * convert list to map.
     */
    public static Map<String, Object> buidMap(List<String> nameList, List<Object> valueList) {
        if (nameList == null || valueList == null || nameList.size() != valueList.size()) {
            log.error("fail buidMap. nameList or valuesList is error");
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < nameList.size(); i++) {
            map.put(nameList.get(i), valueList.get(i));
        }
        return map;
    }

    /**
     * check server host.
     */
    public static void checkServerHostConnect(String serverHost) {
        Boolean state;
        try {
            InetAddress address = InetAddress.getByName(serverHost);
            state = address.isReachable(500);
        } catch (Exception ex) {
            log.error("fail checkServerHostConnect", ex);
            throw new BaseException(ConstantCode.SERVER_CONNECT_FAIL);
        }

        if (!state) {
            log.info("host connect state:{}", state);
            throw new BaseException(ConstantCode.SERVER_CONNECT_FAIL);
        }
    }


    /**
     * check host an port.
     */
    public static void checkServerConnect(String serverHost, int serverPort) {
        // check host
        // checkServerHostConnect(serverHost);

        Socket socket = null;
        try {
            // check port
            socket = new Socket();
            socket.setReceiveBufferSize(8193);
            socket.setSoTimeout(500);
            SocketAddress address = new InetSocketAddress(serverHost, serverPort);
            socket.connect(address, 1000);
        } catch (Exception ex) {
            log.error("fail checkServerConnect", ex);
            throw new BaseException(ConstantCode.SERVER_CONNECT_FAIL);
        } finally {
            if (Objects.nonNull(socket)) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("fail close socket", e);
                }
            }
        }
    }


    /**
     * response exception.
     */
    public static void responseRetCodeException(HttpServletResponse response, RetCode retCode) {
        BaseResponse baseResponse = new BaseResponse(retCode);
        try {
            response.getWriter().write(JacksonUtils.objToString(baseResponse));
        } catch (IOException e) {
            log.error("fail responseRetCodeException", e);
        }
    }


    /**
     * check target time is valid.
     *
     * @param dateTime target time.
     * @param validLength y:year, M:month, d:day of month, h:hour, m:minute, n:forever valid;
     *        example1:1d;example2:n
     */
    public static boolean isDateTimeInValid(LocalDateTime dateTime, String validLength) {
        log.debug("start isDateTimeInValid. dateTime:{} validLength:{}", dateTime, validLength);
        if ("n".equals(validLength)) {
            return true;
        }
        if (Objects.isNull(dateTime) || StringUtils.isBlank(validLength)
                || validLength.length() < 2) {
            return false;
        }

        String lifeStr = validLength.substring(0, validLength.length() - 1);
        if (!StringUtils.isNumeric(lifeStr)) {
            log.warn("fail isDateTimeInValid");
            throw new RuntimeException("fail isDateTimeInValid. validLength is error");
        }
        int lifeValue = Integer.valueOf(lifeStr);
        String lifeUnit = validLength.substring(validLength.length() - 1);

        LocalDateTime now = LocalDateTime.now();
        switch (lifeUnit) {
            case "y":
                return dateTime.getYear() - now.getYear() < lifeValue;
            case "M":
                return dateTime.getMonthValue() - now.getMonthValue() < lifeValue;
            case "d":
                return dateTime.getDayOfMonth() - now.getDayOfMonth() < lifeValue;
            case "m":
                return dateTime.getMinute() - now.getMinute() < lifeValue;
            default:
                log.warn("fail isDateTimeInValid lifeUnit:{}", lifeUnit);
                return false;
        }
    }

    /**
     * response string.
     */
    public static void responseString(HttpServletResponse response, String str) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SYSTEM_EXCEPTION);
        if (StringUtils.isNotBlank(str)) {
            baseResponse.setMessage(str);
        }

        RetCode retCode;
        if (JacksonUtils.isJson(str)
                && (retCode = JacksonUtils.stringToObj(str, RetCode.class)) != null) {
            baseResponse = new BaseResponse(retCode);
        }

        try {
            response.getWriter().write(JacksonUtils.objToString(baseResponse));
        } catch (IOException e) {
            log.error("fail responseRetCodeException", e);
        }
    }

    /**
     * sort Mappings
     * 
     * @param mapping
     * @return List<MapHandle>
     */
    public static List<MapHandle> sortMap(Map<?, ?> mapping) {
        List<MapHandle> list = new ArrayList<>();
        Iterator it = mapping.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            MapHandle handle = new MapHandle(key, mapping.get(key));
            list.add(handle);
        }
        Collections.sort(list, new Comparator<MapHandle>() {
            @Override
            public int compare(MapHandle o1, MapHandle o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return list;
    }

    /**
     * parseHexStr2Int.
     * 
     * @param str str
     * @return
     */
    public static int parseHexStr2Int(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        return Integer.parseInt(str.substring(2), 16);
    }

    /**
     * @param content
     * @return
     */
    public static String upperCaseFirstChar(String content) {
        if (StringUtils.isBlank(content))
            return null;

        String firstChar = content.substring(0, 1);
        String otherChars = content.substring(1);
        return firstChar.toUpperCase() + otherChars;
    }

    public static int getYearMonth(Long recordTimestamp) {
        LocalDateTime recordTime = timestamp2LocalDateTime(recordTimestamp);
        return getYearMonth(recordTime);
    }

    public static int getYearMonth(LocalDateTime recordTime) {
        return recordTime.getYear() * 100 + recordTime.getMonthValue();
    }

    public static int getYearMonthNext(LocalDateTime recordTime) {
        int year = recordTime.getYear();
        int month = recordTime.getMonthValue();
        if (month == 12) {
            return (year + 1) * 100 + 1;
        }
        return year * 100 + month + 1;
    }
    
    public static int getYearMonthDay(Long recordTimestamp) {
        LocalDateTime recordTime = timestamp2LocalDateTime(recordTimestamp);
        return getYearMonthDay(recordTime);
    }

    public static int getYearMonthDay(LocalDateTime recordTime) {
        String dayString = localDateTime2String(recordTime, DATE_FORMAT_NO_SPACE);
        return Integer.valueOf(dayString);
    }

    public static int getYearMonthDayNext(LocalDateTime recordTime) {
        String dayString = localDateTime2String(recordTime.plusDays(1), DATE_FORMAT_NO_SPACE);
        return Integer.valueOf(dayString);
    }
}
