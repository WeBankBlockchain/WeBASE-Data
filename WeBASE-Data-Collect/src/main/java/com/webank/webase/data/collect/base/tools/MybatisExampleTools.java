package com.webank.webase.data.collect.base.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollectionUtil;

@Slf4j
public class MybatisExampleTools {
    private static String EQUAL_METHOD_NAME_FORMAT = "and%1sEqualTo";
    private static String IN_METHOD_NAME_FORMAT = "and%1sIn";
    private static String UUID_FIELD_NAME = "serialVersionUID";

    private static final String COMMON_TABLE_PRIMARY_KEY_NAME = "id";

    /**
     * @param tClass
     * @param equalParamObj
     * @param <T>
     * @return
     */
    public static <T> T initSampleExample(Class<T> tClass, Object equalParamObj) {
        return initSampleExample(tClass, equalParamObj, null);
    }


    /**
     * @param tClass
     * @param equalParamObj
     * @param inParamObjMap
     * @param <T>
     * @return
     */
    public static <T> T initSampleExample(Class<T> tClass, Object equalParamObj,
            Map<String, Object> inParamObjMap) {
        T t;
        try {
            t = tClass.newInstance();
            // createCriteria
            if (Objects.nonNull(equalParamObj) || CollectionUtil.isNotEmpty(inParamObjMap)) {
                Method createCriteriaMethod = tClass.getDeclaredMethod("createCriteria");
                createCriteriaMethod.setAccessible(true);
                Object criteriaObj = createCriteriaMethod.invoke(t);

                Class<?> criteriaClass = Class.forName(tClass.getName() + "$Criteria");
                Method[] innerMethods = criteriaClass.getDeclaredMethods();
                if (Objects.nonNull(equalParamObj)) {
                    MybatisExampleTools.invokeMethodByEqualParam(criteriaObj, innerMethods,
                            equalParamObj);
                }
                if (CollectionUtil.isNotEmpty(inParamObjMap)) {
                    MybatisExampleTools.invokeMethodByInParam(criteriaObj, innerMethods,
                            inParamObjMap);
                }
            }
        } catch (Exception ex) {
            log.error("error exec method [initSampleExample].", ex);
            throw new RuntimeException("init db param unsuccessful");
        }
        return t;
    }


    /**
     * @param tClass
     * @param pageNumber
     * @param pageSize
     * @param equalParamObj
     * @param <T>
     * @return
     */
    public static <T> T initSamplePageExample(Class<T> tClass, int pageNumber, int pageSize,
            Object equalParamObj) {
        return MybatisExampleTools.initSamplePageExample(tClass, pageNumber, pageSize,
                equalParamObj, null);
    }


    /**
     * @param tClass
     * @param pageNumber
     * @param pageSize
     * @param equalParamObj
     * @param inParamObjMap
     * @param <T>
     * @return
     */
    public static <T> T initSamplePageExample(Class<T> tClass, int pageNumber, int pageSize,
            Object equalParamObj, Map<String, Object> inParamObjMap) {
        T t;
        try {
            t = tClass.newInstance();
            // set start
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
            Method setStartMethod = tClass.getDeclaredMethod("setStart", long.class);
            setStartMethod.setAccessible(true);
            setStartMethod.invoke(t, start);

            // set Count
            Method setCountMethod = tClass.getDeclaredMethod("setCount", long.class);
            setCountMethod.setAccessible(true);
            setCountMethod.invoke(t, pageSize);

            // set order
            Method setOrderByClauseMethod =
                    tClass.getDeclaredMethod("setOrderByClause", String.class);
            setOrderByClauseMethod.setAccessible(true);
            setOrderByClauseMethod.invoke(t, COMMON_TABLE_PRIMARY_KEY_NAME);

            // createCriteria
            if (Objects.nonNull(equalParamObj) || CollectionUtil.isNotEmpty(inParamObjMap)) {

                Method createCriteriaMethod = tClass.getDeclaredMethod("createCriteria");
                createCriteriaMethod.setAccessible(true);
                Object criteriaObj = createCriteriaMethod.invoke(t);

                Class<?> criteriaClass = Class.forName(tClass.getName() + "$Criteria");
                Method[] innerMethods = criteriaClass.getDeclaredMethods();

                if (Objects.nonNull(equalParamObj)) {
                    MybatisExampleTools.invokeMethodByEqualParam(criteriaObj, innerMethods,
                            equalParamObj);
                }
                if (CollectionUtil.isNotEmpty(inParamObjMap)) {
                    MybatisExampleTools.invokeMethodByInParam(criteriaObj, innerMethods,
                            inParamObjMap);
                }
            }

        } catch (Exception ex) {
            log.error("error exec method [initSampleExample].", ex);
            throw new RuntimeException("init db param unsuccessful");
        }
        return t;
    }


    /**
     * @param obj
     * @param methods
     * @param equalParamObj
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void invokeMethodByEqualParam(Object obj, Method[] methods, Object equalParamObj)
            throws IllegalAccessException, InvocationTargetException {
        if (Objects.isNull(equalParamObj))
            return;

        Class clazz = equalParamObj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (UUID_FIELD_NAME.equals(field.getName()))
                continue;

            // get param value
            field.setAccessible(true);
            Object paramValue = field.get(equalParamObj);

            // case method
            Method method = null;
            if (Objects.nonNull(paramValue)) {
                String nameWithUpperFirstChar = CommonTools.upperCaseFirstChar(field.getName());
                String equalMethodName =
                        String.format(EQUAL_METHOD_NAME_FORMAT, nameWithUpperFirstChar);
                method = Arrays.stream(methods).filter(m -> m.getName().equals(equalMethodName))
                        .findFirst().orElse(null);
            }

            if (Objects.nonNull(method)) {
                method.setAccessible(true);
                method.invoke(obj, paramValue);
            }
        }
    }


    private static void invokeMethodByInParam(Object obj, Method[] methods,
            Map<String, Object> inParamObjMap)
            throws IllegalAccessException, InvocationTargetException {
        if (CollectionUtil.isEmpty(inParamObjMap))
            return;
        for (String key : inParamObjMap.keySet()) {
            Object paramValue = inParamObjMap.get(key);

            // case method
            Method method = null;
            if (Objects.nonNull(paramValue)) {
                String nameWithUpperFirstChar = CommonTools.upperCaseFirstChar(key);
                String inMethodName = String.format(IN_METHOD_NAME_FORMAT, nameWithUpperFirstChar);
                method = Arrays.stream(methods).filter(m -> m.getName().equals(inMethodName))
                        .findFirst().orElse(null);
            }

            if (Objects.nonNull(method)) {
                method.setAccessible(true);
                method.invoke(obj, paramValue);
            }
        }
    }
}
