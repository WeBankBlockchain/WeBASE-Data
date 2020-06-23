import request from '@/utils/request'

export function getChain1List(params) {
    return request({
        url: '/chain1/list',
        method: 'get',
        params
    })
}
export function getGroupList(params) {
    return request({
        url: '/group/list',
        method: 'get',
        params
    })
}
export function getGroupOverview(params) {
    return request({
        url: '/group/overview',
        method: 'get',
        params
    })
} 
export function getNodeList(params) {
    return request({
        url: '/group/nodeList',
        method: 'get',
        params
    })
}

export function getTransList(params) {
    return request({
        url: '/transaction/transList',
        method: 'get',
        params
    })
}
export function getBlockList(params) {
    return request({
        url: '/block/blockList',
        method: 'get',
        params
    })
}