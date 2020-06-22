/*
 * Copyright 2014-2019 the original author or authors.
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
import { post, get, patch, put, deleted } from './http'
import { reviseParam } from '@/util/util'
import qs from 'qs'

var HANDLE = null;
if (process.env.NODE_ENV === 'development') {
    HANDLE = '/handle'
} else {
    HANDLE = '/WeBASE-Data-Collect';
}
//get front list
export function getFronts(data) {
    return get({
        url: `${HANDLE}/front/list`,
        method: 'get',
        params: data
    })
}

//add front
export function addFront(data) {
    return post({
        url: `${HANDLE}/front/new`,
        method: 'post',
        data: data
    })
}

//delete front
export function delFront(data) {
    return deleted({
        url: `${HANDLE}/front/${data}`,
        method: 'delete',
    })
}

//mointorInfo
export function getMointorInfo(data, list) {
    return get({
        url: `${HANDLE}/front/mointorInfo/${data}`,
        method: 'get',
        params: list
    })
}

//前置节点服务器监控信息
export function getRatio(data, list) {
    return get({
        url: `${HANDLE}/front/ratio/${data}`,
        method: 'get',
        params: list
    })
}

//前置节点服务器配置信息
export function getFrontConfig(data) {
    return get({
        url: `${HANDLE}/front/config/${data}`,
        method: 'get',
    })
}

// add new group
export function addGroup(data) {
    return post({
        url: `${HANDLE}/group/generate`,
        method: 'post',
        data: data
    })
}

// start group
export function startGroup(data) {
    return get({
        url: `${HANDLE}/group/start`,
        method: 'get',
        params: data
    })
}

//batchStart gtoup
export function batchStartGroup(data) {
    return post({
        url: `${HANDLE}/group/batchStart`,
        method: 'post',
        data: data
    })
}

// update group
export function updateGroup() {
    return get({
        url: `${HANDLE}/group/update`,
        method: 'get',
    })
}

// get group detail
export function getGroupDetail(data) {
    return get({
        url: `${HANDLE}/group/${data}`,
        method: 'get'
    })
}

//get group list
export function getGroups(data) {
    return get({
        url: `${HANDLE}/group/list/${data}`,
        method: 'get',
    })
}

//get node list
export function getNodes(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${HANDLE}/node/nodeList/${params.str}`,
        method: 'get',
        params: params.querys,
    })
}

//get node detail
export function getNodeDetail(data) {
    return get({
        url: `${HANDLE}/node/nodeInfo/${data}`,
        method: 'get'
    })
}

//add chain
export function addChain(data) {
    return post({
        url: `${HANDLE}/chain/new`,
        method: 'post',
        data: data
    })
}

//get chains
export function getChains() {
    return get({
        url: `${HANDLE}/chain/all`,
        method: 'get',
    })
}

//delete chains
export function deleteChain(data) {
    return deleted({
        url: `${HANDLE}/chain/${data}`,
        method: 'delete'
    })
}

export function saveChaincode(data) {
    return post({
        url: `${HANDLE}/contract/save`,
        method: 'post',
        data: data
    })
}

export function getContractList(data) {
    return get({
        url: `${HANDLE}/contract/list`,
        method: 'post',
        data: data
    })
}

export function deleteCode(data, list) {
    const params = reviseParam(data, list);
    return deleted({
        url: `${HANDLE}/contract/${params.str}`,
        method: 'delete'
    })
}

/**Public key or private key user list */
export function getUserList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${HANDLE}/user/list/${params.str}`,
        method: 'get',
        params: params.querys
    })
}
export function userAdd(data) {
    return post({
        url: `${HANDLE}/user/add`,
        method: 'post',
        data: data
    })
}
export function deleteUser(data) {
    return deleted({
        url: `${HANDLE}/user/${data}`,
        method: 'delete'
    })
}
/**Send transaction */
export function sendTransation(data) {
    return post({
        url: `${HANDLE}/contract/transaction`,
        method: 'post',
        data: data
    })
}

export function getFunctionAbi(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${HANDLE}/method/findById/${params.str}`,
        method: 'get'
    })
}

export function addFunctionAbi(data) {
    return post({
        url: `${HANDLE}/contract/addMethod`,
        method: 'post',
        data: data
    })
}

/**Deployment contract */
export function getDeployStatus(data) {
    return post({
        url: `${HANDLE}/contract/deploy`,
        method: 'post',
        data: data
    })
}
//solc list
export function solcList(data) {
    return get({
        url: `${HANDLE}/solc/list`,
        method: 'get',
        params: data,
    })
}

///solc/download?solcName={solcName}

export function solcDownload(data) {
    return post({
        url: `${HANDLE}/solc/download`,
        method: 'post',
        params: data,
        // headers: {
        //     'Content-Type': "application/octet-stream;charset=utf-8"
        // },
        responseType: 'blob/json'
    })
}