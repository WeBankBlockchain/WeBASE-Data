/*
 * Copyright 2014-2090 the original author or authors.
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
    "109000": {
        en: "system error",
        zh: '系统异常'
    },
    "109001": {
        en: "param exception",
        zh: '请求参数错误'
    },
    "109002": {
        en: "database exception",
        zh: '数据库异常'
    },
    "209001": {
        en: "chain id already exists",
        zh: '链编号已经存在'
    },
    "209002": {
        en: "chain name already exists",
        zh: '链名称已经存在'
    },
    "209003": {
        en: "save chain fail",
        zh: '链保存失败'
    },
    "209004": {
        en: "invalid chain id",
        zh: '无效链编号'
    },
    "209005": {
        en: "invalid chain type",
        zh: '无效链类型'
    },
    "209006": {
        en: "chain id not exists",
        zh: '链编号不存在'
    },
    "209101": {
        en: "wrong host or port",
        zh: 'ip或端口错误'
    },
    "209102": {
        en: "invalid front id",
        zh: '无效前置编号'
    },
    "209103": {
        en: "not found any front",
        zh: '找不到前置'
    },
    "209104": {
        en: "front already exists",
        zh: '前置已经存在'
    },
    "209105": {
        en: "save front fail",
        zh: '前置保存失败'
    },
    "209106": {
        en: "request front fail",
        zh: '前置请求失败'
    },
    "209107": {
        en: "request node exception",
        zh: '前置节点请求失败'
    },
    "209108": {
        en: "front's encrypt type not matches",
        zh: '前置类型不匹配'
    },
    "209109": {
        en: "invalid block number",
        zh: '无效块高'
    },
    "209110": {
        en: "invalid node id",
        zh: '无效节点编号'
    },
    "209201": {
        en: "invalid group id",
        zh: '无效群组编号'
    },
    "209301": {
        en: "user name already exists",
        zh: '用户名已存在'
    },
    "209302": {
        en: "user address already exists",
        zh: '用户地址已存在'
    },
    "209401": {
        en: "contract already exists",
        zh: '合约已存在'
    },
    "209402": {
        en: "invalid contract id",
        zh: '无效合约编号'
    },
    "209403": {
        en: "contract name cannot be repeated",
        zh: '合约名重复'
    },
    "209501": {
        en: "task is still running",
        zh: '任务正在执行'
    },
    "209502": {
        en: "block has been reset",
        zh: '区块已重置'
    },
    "209601": {
        en: "solc js file cannot be empty",
        zh: '编译器文件不能为空'
    },
    "209602": {
        en: "solc js file already exist",
        zh: '编译器文件已存在'
    },
    "209603": {
        en: "solc js file not exist",
        zh: '编译器文件不存在'
    },
    "209604": {
        en: "save solc js file error",
        zh: '编译器文件保存失败'
    },
    "209605": {
        en: "read solc js file error",
        zh: '编译器文件读取失败'
    },
}
export function chooseLang(code) {
    let lang = localStorage.getItem('lang')
    let message = errCode[code]['zh'];
    return message
}
