import http from '../utils/http'

// 查询15天交易数据
const getTransDataHalfMonth = (params) => http.get('/chain/trans15Days', params)
// 查询交易回执详情
const getTransactionReceipt = (params) => http.get(`/chain/transactionReceipt/${params.transHash}`, params)
// 查询交易列表
const getTransList = (params) => http.get(`/chain/transList/${params.pageNumber}/${params.pageSize}`, params)
// 查询区块列表
const getBlockList = (params) => http.get(`/chain/blockList/${params.pageNumber}/${params.pageSize}`, params)
// 查询交易详情
const getTransInfo = (params) => http.get(`/chain/transInfo/${params.transHash}`, params)
// 查询交易详情
const inputMethodAbi = (params) => http.get(`/chain/methodAbi/${params.methodId}`, params)
const inputMethodAbiList = (params) => http.get('/chain/methodAbiList/', params)
// abi
const getDeloyAbi = (params) => http.get('/chain/contractAbi', params)
// bin
const getDeloyContractBin = (params) => http.get('/chain/contractBin', params)
// getError Prase
const getDeloyErrorParse = (params) => http.get('/chain/revertMessageParser', params)

export default {
  getTransDataHalfMonth,
  getTransactionReceipt,
  getTransList,
  getBlockList,
  getTransInfo,
  inputMethodAbi,
  getDeloyAbi,
  getDeloyContractBin,
  getDeloyErrorParse,
  inputMethodAbiList
}
