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

export function searchAll(data) {
    return post({
        url: `${url.ORG_LIST}/search/normal`,
        method: 'post',
        data: data
    })
}
export function chainAll() {
    return get({
        url: `${url.ORG_LIST}/chain/all`,
        method: 'get',
    })
}
export function groupList(chainId) {
    return get({
        url: `${url.ORG_LIST}/group/list/${chainId}`,
        method: 'get',
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


