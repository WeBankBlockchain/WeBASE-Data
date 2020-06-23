import request from '@/utils/request'
const baseURLChain = '/group'
export function groupList(chainId) {
    return request({
        url: `${baseURLChain}/list/${chainId}`,
        method: 'get',
    })
}

export function groupGeneral(chainId, groupId) {
    return request({
        url: `${baseURLChain}/general/${chainId}/${groupId}`,
        method: 'get',
    })
}

export function groupTransDaily(chainId, groupId) {
    return request({
        url: `${baseURLChain}/transDaily/${chainId}/${groupId}`,
        method: 'get',
    })
}

export function nodeList(chainId, groupId, pageNumber, pageSize) {
    return request({
        url: `${baseURLChain}/nodeList/${chainId}/${groupId}/${pageNumber}/${pageSize}`,
        method: 'get',
    })
}

export function blockList(chainId, groupId, pageNumber, pageSize, params) {
    return request({
        url: `${baseURLChain}/blockList/${chainId}/${groupId}/${pageNumber}/${pageSize}`,
        method: 'get',
        params: params
    })
}

export function transList(chainId, groupId, pageNumber, pageSize, params) {
    return request({
        url: `${baseURLChain}/transList/${chainId}/${groupId}/${pageNumber}/${pageSize}`,
        method: 'get',
        params: params
    })
}

export function userList(chainId, groupId, pageNumber, pageSize, params) {
    return request({
        url: `${baseURLChain}/userList/${chainId}/${groupId}/${pageNumber}/${pageSize}`,
        method: 'get',
        params: params
    })
}

export function contractList(chainId, groupId, pageNumber, pageSize, params) {
    return request({
        url: `${baseURLChain}/contractList/${chainId}/${groupId}/${pageNumber}/${pageSize}`,
        method: 'get',
        params: params
    })
}


