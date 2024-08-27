import http from '../utils/http'

// 获取dapp列表
const getAppList = (params) => http.get('/app/appList', params)
// 获取dapp详情
const getAppInfo = (params) => http.get(`/app/appInfo/${params.appId}`, params)
// 获取dapp合约列表
const getContractListByAppId = (params) => http.get(`/app/contractList/${params.appId}/${params.pageNumber}/${params.pageSize}`, params)
// 获取dapp合约交易列表
const getContractTransListByAppId = (params) => http.get(`/app/contractTransList/${params.pageNumber}/${params.pageSize}`, params)

const getContractLine = (params) => http.get('/app/contractLine', params)

export default { getAppList, getAppInfo, getContractListByAppId, getContractTransListByAppId, getContractLine }
