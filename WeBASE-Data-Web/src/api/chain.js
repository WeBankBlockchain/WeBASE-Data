import request from '@/utils/request'
const baseURLChain = '/chain'

export function chainAll() {
    return request({
        url: `${baseURLChain}/all`,
        method: 'get',
    })
}