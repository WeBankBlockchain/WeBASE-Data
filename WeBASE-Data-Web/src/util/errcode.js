/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
let errCode = {
    "110000": {
        en: "system error",
        zh: '系统异常'
    },
    "110001": {
        en: "param exception",
        zh: '请求参数错误'
    },
    "110002": {
        en: "database exception",
        zh: '数据库异常'
    },
    "210101": {
        en: "invalid group id",
        zh: '无效群组编号'
    },
    "210201": {
        en: "searchType not exists",
        zh: '搜索类型不存在'
    },
    "210202": {
        en: "search content can not be empty",
        zh: '搜索内容不能为空'
    },
    "210203": {
        en: "search index not exists",
        zh: '索引不存在'
    },
    "210204": {
        en: "search fail",
        zh: '搜索失败'
    },
    "210205": {
        en: "elasticsearch's config is false",
        zh: '未开启elasticsearch配置'
    },
    "210301": {
        en: "keyword id not exists",
        zh: '关键字不存在'
    },
    "210302": {
        en: "keyword exists",
        zh: '关键字已存在'
    },
    "210303": {
        en: "save keyword fail",
        zh: '关键字保存失败'
    },
    "210401": {
        en: "audit id not exists",
        zh: '告警信息不存在'
    },
    "210402": {
        en: "audit inffo exists",
        zh: '告警信息已存在'
    },
    "210403": {
        en: "save audit info fail",
        zh: '告警信息保存失败'
    },
    "210405": {
        en: "keyword can not be empty",
        zh: '关键字不能为空'
    },
}
export function chooseLang(code) {
    let lang = localStorage.getItem('lang')
    let message = errCode[code]['zh'];
    return message
}
