package com.webank.webase.data.collect.rest;

public interface RestService {

    <T> T httpGet(String url, Class<T> clazz);

    <T> T httpPost(String url, Object param, Class<T> clazz);

    <T> T httpPut(String url, Object param, Class<T> clazz);

    <T> T httpDelete(String url, Object param, Class<T> clazz);
}
