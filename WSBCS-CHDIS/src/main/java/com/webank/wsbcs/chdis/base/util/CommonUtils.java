/**
 * Copyright 2014-2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.wsbcs.chdis.base.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.Hex;
import com.fasterxml.jackson.core.type.TypeReference;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.BaseResponse;
import com.webank.wsbcs.chdis.base.entity.data.AppInfo;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import lombok.extern.log4j.Log4j2;

/**
 * common method.
 */
@Log4j2
public class CommonUtils {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_NO_SPACE = "yyyyMMddHHmmss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT_STR = "yyyyMMdd";
    public static final String DEFAULT_HOUR_FORMAT = "HH";

    public static void main(String[] args) {
        Date statDate = new Date(1706025581316L);
        int checkHour = getHour(statDate);
        String checkDate = getDateString(statDate);
        System.out.println("checkDate: " + checkDate);
        System.out.println("checkHour: " + checkHour);

        long days = getDaysBetweenDates("2024-01-01", "2024-01-10", "test");
        System.out.println("days: " + days);

        String nextDateStr = getNextDateStr("2023-12-31");
        System.out.println("nextDateStr: " + nextDateStr);

        System.out.println("currentTimeMillis: " + System.currentTimeMillis());

    }

    public static int getDaysBetweenDates(String dateStart, String dateEnd, String bizSeqNo) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            Date startDate = dateFormat.parse(dateStart);
            Date endDate = dateFormat.parse(dateEnd);
            long diffInMillies = endDate.getTime() - startDate.getTime();
            return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
        } catch (ParseException e) {
            throw new BaseException(ConstantCode.INVALID_DATE, bizSeqNo);
        }
    }

    public static String getTimestempToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STR);
        return simpleDateFormat.format(date);
    }

    public static long getTimestempFromString(String dateStr, String bizSeqNo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            throw new BaseException(ConstantCode.INVALID_DATE, bizSeqNo);
        }
    }

    public static String getNextDateStr(String inputDate) {
        LocalDate date = LocalDate.parse(inputDate).plusDays(1);
        return date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public static int compareToday(String inputTime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String dateString = simpleDateFormat.format(new Date(Long.valueOf(inputTime)));
        LocalDate date = LocalDate.parse(dateString);
        // 获取今天的日期
        LocalDate today = LocalDate.now();

        // 比较给定日期和今天日期的大小
        if (date.isBefore(today)) {
            return -1;
        } else if (date.isAfter(today)) {
            return 1;
        } else {
            return 0;
        }
    }

    public static boolean isBeforeToday(Long inputTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String inputDateStr = simpleDateFormat.format(new Date(inputTime));
        String todayStr = simpleDateFormat.format(new Date());
        try {
            return simpleDateFormat.parse(inputDateStr)
                    .compareTo(simpleDateFormat.parse(todayStr)) < 0;
        } catch (ParseException e) {
            log.warn("isToday parse error. {}", e.getMessage());
        }
        return false;
    }

    public static boolean isToday(Long inputTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String inputDateStr = simpleDateFormat.format(new Date(inputTime));
        String todayStr = simpleDateFormat.format(new Date());
        try {
            return simpleDateFormat.parse(inputDateStr)
                    .compareTo(simpleDateFormat.parse(todayStr)) == 0;
        } catch (ParseException e) {
            log.warn("isToday parse error. {}", e.getMessage());
        }
        return false;
    }

    public static boolean isSameDate(long timestamp1, long timestamp2) {
        // 将时间戳转换为LocalDateTime
        LocalDateTime dateTime1 = LocalDateTime
                .ofInstant(java.time.Instant.ofEpochMilli(timestamp1), ZoneId.systemDefault());
        LocalDateTime dateTime2 = LocalDateTime
                .ofInstant(java.time.Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault());
        // 比较年月日是否相同
        boolean isSameDate = dateTime1.toLocalDate().equals(dateTime2.toLocalDate());
        return isSameDate;
    }

    public static int getHour(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_HOUR_FORMAT);
        int hour = Integer.parseInt(simpleDateFormat.format(date));
        return hour;
    }

    public static String getDateString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return simpleDateFormat.format(date);
    }

    public static String getTodayString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return simpleDateFormat.format(new Date());
    }

    public static String getYesterdayString() {
        Date yesterday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return simpleDateFormat.format(yesterday);
    }

    public static String getYesterdayString(Long inputTime) {
        Date yesterday = new Date(inputTime - 1000 * 60 * 60 * 24);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return simpleDateFormat.format(yesterday);
    }

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
     * conver object to java bean.
     */
    public static <T> T object2JavaBean(Object obj, Class<T> clazz) {
        if (obj == null || clazz == null) {
            log.warn("object2JavaBean. obj or clazz null");
            return null;
        }
        String jsonStr = JsonUtils.toJSONString(obj);

        return JsonUtils.toJavaObject(jsonStr, clazz);
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
                return now.getYear() - dateTime.getYear() < lifeValue;
            case "M":
                return now.getMonthValue() - dateTime.getMonthValue() < lifeValue;
            case "d":
                return now.getDayOfMonth() - dateTime.getDayOfMonth() < lifeValue;
            case "m":
                return now.getMinute() - dateTime.getMinute() < lifeValue;
            default:
                log.warn("fail isDateTimeInValid lifeUnit:{}", lifeUnit);
                return false;
        }
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
     * read File.
     * 
     * @param filePath filePath
     * @return
     */
    public static List<String> readFileToList(String filePath) {
        log.info("readFile dir:{}", filePath);
        File dirFile = new File(CleanPathUtil.cleanString(filePath));
        if (!dirFile.exists()) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        try (InputStream inputStream = new FileInputStream(dirFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (Exception e) {
            log.error("readFileToList dir:{}", filePath, e);
        }
        return result;
    }

    /**
     * delete Files.
     *
     * @param path path
     * @return
     */
    public static boolean deleteFiles(String path) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteFiles(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        return true;
    }

    /**
     * delete single File.
     *
     * @param filePath filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            flag = true;
        }
        return flag;
    }

    /**
     * 文件转Base64
     *
     * @param filePath 文件路径
     * @return
     */
    public static String fileToBase64(String filePath) {
        if (filePath == null) {
            return null;
        }
        FileInputStream inputFile = null;
        try {// NOSONAR
            File file = new File(filePath);
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);// NOSONAR
            return Base64.getEncoder().encodeToString(buffer);
        } catch (IOException e) {
            log.error("base64ToFile IOException:[{}]", e.toString());
        } finally {
            close(inputFile);
        }
        return null;
    }

    /**
     * 文件压缩并Base64加密
     *
     * @param srcFiles
     * @return
     */
    public static String fileToZipBase64(List<File> srcFiles) {
        long start = System.currentTimeMillis();
        String toZipBase64 = "";
        ZipOutputStream zos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {// NOSONAR
            zos = new ZipOutputStream(baos);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[1024];
                log.info("fileToZipBase64 fileName: [{}] size: [{}] ", srcFile.getName(),
                        srcFile.length());
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = null;
                try {// NOSONAR
                    in = new FileInputStream(srcFile);
                    while ((len = in.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                    zos.closeEntry();
                } finally {
                    close(in);
                }
            }
            long end = System.currentTimeMillis();
            log.info("fileToZipBase64 cost time：[{}] ms", (end - start));
        } catch (IOException e) {
            log.error("fileToZipBase64 IOException:[{}]", e.toString());
        } finally {
            close(zos);
        }

        byte[] refereeFileBase64Bytes = Base64.getEncoder().encode(baos.toByteArray());
        try {
            toZipBase64 = new String(refereeFileBase64Bytes, "UTF-8");
        } catch (IOException e) {
            log.error("fileToZipBase64 IOException:[{}]", e.toString());
        }
        return toZipBase64;
    }

    /**
     * close Closeable.
     *
     * @param closeable object
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("closeable IOException:[{}]", e.toString());
            }
        }
    }


    /**
     * zip Base64 解密 解压缩.
     *
     * @param base64 base64加密字符
     * @param path 解压文件夹路径
     */
    public static void zipBase64ToFile(String base64, String path) {
        ByteArrayInputStream bais = null;
        ZipInputStream zis = null;
        try {// NOSONAR
            File file = new File(path);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }

            byte[] byteBase64 = Base64.getDecoder().decode(base64);
            bais = new ByteArrayInputStream(byteBase64);
            zis = new ZipInputStream(bais);
            ZipEntry entry = zis.getNextEntry();
            File fout = null;
            while (entry != null) {
                if (entry.isDirectory()) {
                    File subdirectory = new File(
                            CleanPathUtil.cleanString(path + File.separator + entry.getName()));
                    if (!subdirectory.exists() && !subdirectory.isDirectory()) {
                        subdirectory.mkdirs();
                    }
                } else {
                    log.info("zipBase64ToFile fileName:[{}]", entry.getName());
                    String outPath = (path + entry.getName()).replaceAll("\\*", "/");
                    fout = new File(CleanPathUtil.cleanString(outPath));
                    BufferedOutputStream bos = null;
                    try {// NOSONAR
                        bos = new BufferedOutputStream(new FileOutputStream(fout));
                        int offo = -1;
                        byte[] buffer = new byte[1024];
                        while ((offo = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, offo);
                        }
                    } catch (IOException e) {
                        log.error("base64ToFile IOException:[{}]", e.toString());
                    } finally {
                        close(bos);
                    }
                }
                // next
                entry = zis.getNextEntry();
            }
        } catch (IOException e) {
            log.error("base64ToFile IOException:[{}]", e.toString());
        } finally {
            close(zis);
            close(bais);
        }
    }



    /**
     * stringToSignatureData. 19/12/24 support guomi： add byte[] pub in signatureData byte array: [v
     * + r + s + pub] 19/12/24 support guomi： add byte[] pub in signatureData 2021/08/05 webase-sign
     * <=1.4.3, v=27 >=1.5.0, v=0 or 1 or 2 if using web3sdk, v default 27, if using java-sdk, v
     * default 0, and add 27 in RLP encode
     * 
     * @param signatureData signatureData
     * @return
     */
    public static SignatureResult stringToSignatureData(String signatureData, int encryptType) {
        byte[] byteArr = Numeric.hexStringToByteArray(signatureData);
        // 从1开始，因为此处webase-sign返回的byteArr第0位是v
        byte signV = byteArr[0];
        log.debug("stringToSignatureData v:{}", (int) signV);
        byte[] signR = new byte[32];
        System.arraycopy(byteArr, 1, signR, 0, signR.length);
        byte[] signS = new byte[32];
        System.arraycopy(byteArr, 1 + signR.length, signS, 0, signS.length);
        if (encryptType == CryptoType.SM_TYPE) {
            if (byteArr.length != 129) {
                throw new BaseException(ConstantCode.INVALID_SIGN_USER_ID_OF_ENCRYPT_TYPE.getCode(),
                        "stringToSignatureData byteArray length not equal [SM2] SignatureResult length of 129");
            }
            byte[] pub = new byte[64];
            System.arraycopy(byteArr, 1 + signR.length + signS.length, pub, 0, pub.length);
            return new SM2SignatureResult(pub, signR, signS);
        } else {
            if (byteArr.length != 65) {
                throw new BaseException(ConstantCode.INVALID_SIGN_USER_ID_OF_ENCRYPT_TYPE.getCode(),
                        "stringToSignatureData byteArray length not equal [ECDSA] SignatureResult length of 65");
            }
            return new ECDSASignatureResult(signV, signR, signS);
        }
    }


    public static org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult stringToSignatureDataV3(
            String signatureData, int encryptType) throws BaseException {
        // 兼容sign的返回值，必须使用Numeric
        byte[] byteArr = org.fisco.bcos.sdk.v3.utils.Numeric.hexStringToByteArray(signatureData);
        // 从1开始，因为此处webase-sign返回的byteArr第0位是v
        byte signV = byteArr[0];
        byte[] signR = new byte[32];
        System.arraycopy(byteArr, 1, signR, 0, signR.length);
        byte[] signS = new byte[32];
        System.arraycopy(byteArr, 1 + signR.length, signS, 0, signS.length);
        if (encryptType == CryptoType.SM_TYPE) {
            if (byteArr.length != 129) {
                throw new BaseException(ConstantCode.INVALID_SIGN_USER_ID_OF_ENCRYPT_TYPE.getCode(),
                        "stringToSignatureData byteArray length not equal [SM2] SignatureResult length of 129");
            }
            byte[] pub = new byte[64];
            System.arraycopy(byteArr, 1 + signR.length + signS.length, pub, 0, pub.length);
            return new org.fisco.bcos.sdk.v3.crypto.signature.SM2SignatureResult(pub, signR, signS);
        } else {
            if (byteArr.length != 65) {
                throw new BaseException(ConstantCode.INVALID_SIGN_USER_ID_OF_ENCRYPT_TYPE.getCode(),
                        "stringToSignatureData byteArray length not equal [ECDSA] SignatureResult length of 65");
            }
            return new org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignatureResult(signV, signR,
                    signS);
        }
    }

    private static final Integer HTTP_SUCCESS_RESPONSE_CODE = 0;


    public static <T> T getResultData(String responseJson, Class<T> dataClass) {
        BaseResponse response = JsonUtils.toJavaObject(responseJson, BaseResponse.class);
        return getResultData(response, dataClass);
    }

    /**
     * @param response
     * @param dataClass
     * @param <T>
     * @return
     */
    public static <T> T getResultData(BaseResponse response, Class<T> dataClass) {
        if (dataClass == null) {
            log.info("finish exec method [getResultData]. dataClass is null,return null");
            return null;
        }
        if (Objects.isNull(response)) {
            log.info("finish exec method [getResultData]. response is null,return null");
            return null;
        }
        if (HTTP_SUCCESS_RESPONSE_CODE != response.getCode()) {
            log.info("finish exec method [getResultData]. response code:{} message:{}",
                    response.getCode(), response.getMessage());
            throw new BaseException(response.getCode(), response.getMessage());
        }

        T t = JsonUtils.toJavaObject(response.getData(), dataClass);
        log.info("success exec method [getResultData]. data:{}",
                JsonUtils.objToString(response.getData()));
        return t;
    }


    public static <T> T getResultData(BaseResponse response, TypeReference<T> tTypeReference) {
        if (tTypeReference == null) {
            log.info("finish exec method [getResultData]. tTypeReference is null,return null");
            return null;
        }
        if (Objects.isNull(response)) {
            log.info("finish exec method [getResultData]. response is null,return null");
            return null;
        }
        if (HTTP_SUCCESS_RESPONSE_CODE != response.getCode()) {
            log.info("finish exec method [getResultData]. response code:{} message:{}",
                    response.getCode(), response.getMessage());
            throw new BaseException(response.getCode(), response.getMessage());
        }

        T t = JsonUtils.stringToObj(JsonUtils.objToString(response.getData()), tTypeReference);
        log.info("success exec method [getResultData]. data:{}",
                JsonUtils.objToString(response.getData()));
        return t;
    }


    /**
     * @return
     */
    public static String getCurrentProcessId() {
        log.debug("start method getCurrentProcessId");
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        String processId = name.substring(0, name.indexOf("@"));
        log.debug("success exec  method getCurrentProcessId, result:{}", processId);
        return processId;
    }


    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\\t|\\r|\\n");// NOSONAR
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static byte[] base64Decode(String encodeContent) {
        // decode and save contract to file
        byte[] contractSourceByteArr = null;
        try {
            contractSourceByteArr = Base64.getDecoder().decode(replaceBlank(encodeContent));
        } catch (Exception ex) {
            log.warn("decode by base64 of jdk8 fail,content:{}", encodeContent, ex);
            contractSourceByteArr = Base64.getDecoder().decode(replaceBlank(encodeContent));
        }
        log.warn("decode contractSource success");
        return contractSourceByteArr;
    }


    /**
     * convert hex number string to decimal number string
     * 
     * @param receipt
     */
    public static void processReceiptHexNumber(TransactionReceipt receipt) {
        if (receipt == null) {
            return;
        }
        String gasUsed = Optional.ofNullable(receipt.getGasUsed()).orElse("0");
        String blockNumber = Optional.ofNullable(receipt.getBlockNumber()).orElse("0");
        receipt.setGasUsed(Numeric.decodeQuantity(gasUsed).toString(10));
        receipt.setBlockNumber(Numeric.decodeQuantity(blockNumber).toString(10));
    }

    /**
     * convert hex number string to decimal number string
     * 
     * @param receipt
     */
    public static void processReceiptHexNumber(
            org.fisco.bcos.sdk.v3.model.TransactionReceipt receipt) {
        if (receipt == null) {
            return;
        }
        String gasUsed = Optional.ofNullable(receipt.getGasUsed()).orElse("0x0");
        if (gasUsed.startsWith("0x")) {
            receipt.setGasUsed(
                    org.fisco.bcos.sdk.v3.utils.Numeric.decodeQuantity(gasUsed).toString(10));
        }
    }


    /**
     * convert hex number string to decimal number string
     * 
     * @param trans
     */
    public static void processTransHexNumber(JsonTransactionResponse trans) {
        if (trans == null) {
            return;
        }
        String gas = Optional.ofNullable(trans.getGas()).orElse("0");
        String gasPrice = Optional.ofNullable(trans.getGasPrice()).orElse("0");
        String groupIdStr = Optional.ofNullable(trans.getGroupId()).orElse("0");
        trans.setGas(Numeric.decodeQuantity(gas).toString(10));
        trans.setGasPrice(Numeric.decodeQuantity(gasPrice).toString(10));
        trans.setGroupId(Numeric.decodeQuantity(groupIdStr).toString(10));
    }



    public static void decodeReceipt(TransactionReceipt receipt, CryptoSuite cryptoSuite) {
        // decode receipt
        TransactionDecoderService txDecoder = new TransactionDecoderService(cryptoSuite);
        String receiptMsg = txDecoder.decodeReceiptStatus(receipt).getReceiptMessages();
        receipt.setMessage(receiptMsg);
    }


    /**
     * convert hex number string to decimal number string
     * 
     * @param block
     */
    public static void processBlockHexNumber(BcosBlock.Block block) {
        if (block == null) {
            return;
        }
        String gasLimit = Optional.ofNullable(block.getGasLimit()).orElse("0");
        String gasUsed = Optional.ofNullable(block.getGasUsed()).orElse("0");
        String timestamp = Optional.ofNullable(block.getTimestamp()).orElse("0");
        block.setGasLimit(Numeric.decodeQuantity(gasLimit).toString(10));
        block.setGasUsed(Numeric.decodeQuantity(gasUsed).toString(10));
        block.setTimestamp(Numeric.decodeQuantity(timestamp).toString(10));
    }

    public static Date convert2Date(LocalDate date) {
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date convert2Date(long inputTime) {
        return Date
                .from(Instant.ofEpochMilli(inputTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate convert2LocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static long convert2Timestamp(Date date) {
        return date.getTime();
    }

    public static Long convert2Timestamp(LocalDate date) {
        return date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate convert2LocalDate(long inputTime) {
        return Instant.ofEpochMilli(inputTime).atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public static Date convert2Date(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * md5Encrypt.
     *
     * @param dataStr
     * @return
     */
    public static String md5Encrypt(String dataStr) {
        if (StringUtils.isBlank(dataStr)) {
            return dataStr;
        }
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < s.length; i++) {
                result.append(Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6));
            }
            return result.toString().toUpperCase();
        } catch (Exception e) {
            log.error("fail md5Encrypt", e);
        }
        return "";
    }

    // judge string all numbers
    public static boolean isDigit(String strNum) {
        if (StringUtils.isBlank(strNum)) {
            return false;
        } else {
            for (int i = 0; i < strNum.length(); i++) {
                if (!(strNum.charAt(i) >= 48) || !(strNum.charAt(i) <= 57)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 判断IP格式和范围
     * 
     * @描述:用正则表达式判断是否为IP
     */
    public static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        String rexp =
                "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";// NOSONAR
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }

    public static AppInfo getAppInfoV2(String extraDataStr) {
        AppInfo appInfo = new AppInfo();
        try {
            if (StringUtils.isNotBlank(extraDataStr) || !"0x".equals(extraDataStr)
                    || !"0x7c".equals(extraDataStr)) { // 0x7c 是只有管道符的情况。0x是空的情况
                // 从hex转为utf8
                String extraData = new String(Hex.decode(Numeric.cleanHexPrefix(extraDataStr)),
                        StandardCharsets.UTF_8);
                log.debug("getAppInfoV2 extraData:{}", extraData);
                if (extraData.contains("|")) {
                    String[] appInfoArr = extraData.split("\\|");
                    if (appInfoArr.length != 2) {
                        return appInfo;
                    }
                    String appId = appInfoArr[0];
                    String appName = appInfoArr[1];
                    if (CommonUtils.isDigit(appId)) {
                        appInfo.setAppId(appId);
                        appInfo.setAppName(appName);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("get appInfo in extra data in statistic error {}|{}", extraDataStr,
                    ex.getMessage());
        }
        return appInfo;
    }

    public static AppInfo getAppInfoV3(String extraDataStr) {
        AppInfo appInfo = new AppInfo();
        try {
            if (StringUtils.isNotBlank(extraDataStr)) {
                log.debug("getAppInfoV3 extraData:{}", extraDataStr);
                if (extraDataStr.contains("|")) {
                    String[] appInfoArr = extraDataStr.split("\\|");
                    if (appInfoArr.length != 2) {
                        return appInfo;
                    }
                    appInfo.setAppId(appInfoArr[0]);
                    appInfo.setAppName(appInfoArr[1]);
                }
            }
        } catch (Exception ex) {
            log.warn("get appInfo in extra data in statistic error {}|{}", extraDataStr,
                    ex.getMessage());
        }
        return appInfo;
    }

}
