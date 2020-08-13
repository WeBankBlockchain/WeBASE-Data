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
import url from './url'
import { post, get, patch, put, deleted } from './http'
import { reviseParam } from './util'
import qs from 'qs'

export function chainGeneral() {
    return get({
        url: `${url.ORG_LIST}/chain/general`,
        method: 'get'
    })
}

export function searchAll(data) {
    return post({
        url: `${url.ORG_LIST}/search/normal`,
        method: 'post',
        data: data
    })
}
export function simpleSearch(pageNumber, pageSize,data) {
    return get({
        url: `${url.ORG_LIST}/search/keyword/${pageNumber}/${pageSize}`,
        method: 'get',
        params: data
    })
}
export function chainAll() {
    return get({
        url: `${url.ORG_LIST}/chain/all`,
        method: 'get',
    })
}
export function groupList(param) {
    return get({
        url: `${url.ORG_LIST}/group/list`,
        method: 'get',
        params: param
    })
}
//查询链节点
export function chainNodes(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/group/orgList/${params.str}`,
        method: 'get',
        params: params.querys,
    })
}
export function groupGeneral(chainId, groupId) {
    return get({
        url: `${url.ORG_LIST}/group/general/${chainId}/${groupId}`,
        method: 'get',
    })
}

export function groupTransDaily(chainId, groupId) {
    return get({
        url: `${url.ORG_LIST}/group/txnDaily/${chainId}/${groupId}`,
        method: 'get',
    })
}

export function groupNodeList(chainId, groupId, pageNumber, pageSize) {
    return get({
        url: `${url.ORG_LIST}/group/nodeList/${chainId}/${groupId}/${pageNumber}/${pageSize}`,
        method: 'get',
    })
}

export function blockList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/group/blockList/${params.str}`,
        method: 'get',
        params: params.querys
    })
}

export function transList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/group/transList/${params.str}`,
        method: 'get',
        params: params.querys
    })
}

export function userList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/group/userList/${params.str}`,
        method: 'get',
        params: params.querys
    })
}

export function contractList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/group/contractList/${params.str}`,
        method: 'get',
        params: params.querys
    })
}

export function keywordsAdd(data) {
    return post({
        url: `${url.ORG_LIST}/keywords/add`,
        method: 'post',
        data: data
    })
}

export function keywordsList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/keywords/list/${params.str}`,
        method: 'get',
        params: params.querys
    })
}

export function keywordsUpdate(data) {
    return post({
        url: `${url.ORG_LIST}/keywords/update`,
        method: 'post',
        data: data
    })
}

export function keywordsDelete(data) {
    return deleted({
        url: `${url.ORG_LIST}/keywords/${data}`,
        method: 'delete',
    })
}

export function auditAdd(data) {
    return post({
        url: `${url.ORG_LIST}/audit/add`,
        method: 'post',
        data: data
    })
}

export function auditList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/audit/list/${params.str}`,
        method: 'get',
        data: params.querys
    })
}

export function auditConfirm(data) {
    return post({
        url: `${url.ORG_LIST}/audit/confirm/${data}`,
        method: 'post'
    })
}

export function auditDelete(data) {
    return deleted({
        url: `${url.ORG_LIST}/audit/${data}`,
        method: 'delete',
    })
}

export function appAuditAdd(data) {
    return post({
        url: `${url.ORG_LIST}/appAudit/add`,
        method: 'post',
        data: data
    })
}

export function appAuditList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/appAudit/list/${params.str}`,
        method: 'get',
        data: params.querys
    })
}

export function appAuditConfirm(data) {
    return post({
        url: `${url.ORG_LIST}/appAudit/confirm/${data}`,
        method: 'post'
    })
}

export function appAuditDelete(data) {
    return deleted({
        url: `${url.ORG_LIST}/appAudit/${data}`,
        method: 'delete',
    })
}

export function txAuditAdd(data) {
    return post({
        url: `${url.ORG_LIST}/transAudit/add`,
        method: 'post',
        data: data
    })
}

export function txAuditList(data, list) {
    const params = reviseParam(data, list);
    return get({
        url: `${url.ORG_LIST}/transAudit/list/${params.str}`,
        method: 'get',
        data: params.querys
    })
}

export function txAuditConfirm(data) {
    return post({
        url: `${url.ORG_LIST}/transAudit/confirm/${data}`,
        method: 'post'
    })
}

export function txAuditDelete(data) {
    return deleted({
        url: `${url.ORG_LIST}/transAudit/${data}`,
        method: 'delete',
    })
}