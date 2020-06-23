import request from '@/utils/request'
const baseURLChain = '/search'
export function searchAll(data) {
    return request({
        url: `${baseURLChain}/normal`,
        method: 'post',
        data: data
    })
}