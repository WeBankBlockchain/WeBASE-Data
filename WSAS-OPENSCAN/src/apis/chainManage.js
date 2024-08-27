import http from '../utils/http'

// 微众链预览
const getGeneralAll = (params) => http.get('/chain/generalAll', params)
// 单链预览
const getChainGeneral = (params) => http.get('/chain/general', params)
// 查询链列表
const getChainList = (params) => http.get('/chain/list ', params)
// 查询区块详情
const getBlockDetail = (params) => http.get(`/chain/getBlockByNumber/${params.blockNumber}`, params)
// 获取top app list
const getTopList = (params) => http.get('/chain/topAppList', params)

const serchByKey = (params) => http.get(`/chain/search/${params.input}`, params)

const hashCheck = (params) => http.get(`/openChain/hashCheck/${params.hash}`, params)
const fileCheck = (params) => http.uploadAjax('/openChain/fileCheck', params)

const getTest = (params) => http.get(`/data/userFlowList/1/1?userAddress=0x`, params)

export default { getGeneralAll, getChainGeneral, getBlockDetail, getChainList, getTopList, serchByKey, hashCheck, fileCheck, getTest }
