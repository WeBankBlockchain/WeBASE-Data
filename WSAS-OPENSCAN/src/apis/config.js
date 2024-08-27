import http from '../utils/http'

// 按天查询数据趋势图
const getEchartByDay = (params) => http.get('/data/trendChartByDay', params)

// 按小时查询数据趋势图
const getEchartByHour = (params) => http.get('/data/trendChartByHour', params)

const getUserListData = (params) => http.get(`/data/userList/${params.pageNumber}/${params.pageSize}`, params)
const getAdminListData = (params) => http.get(`/data/managerList/${params.pageNumber}/${params.pageSize}`, params)
// 查询用户交易流水（分页）
const getUserFlowData = (params) => http.get(`/data/userFlowList/${params.pageNumber}/${params.pageSize}`, params)

// 查询实时统计数据
const getGeneralData = (params) => http.get(`/data/general`, params)

// 查询历史统计数据
const getGeneralHistoryData = (params) => http.get(`/data/generalHistory`, params)

// 保存配置信息
const saveConfig = (data) => http.post(`/config/save`, data)

// 查询配置信息
const getConfigData = (params) => http.get(`/config/info`, params)

// 查询数据同步状态
const getIsLoading = (params) => http.get(`/data/isLoading`, params)

// 查询用户余额
const getUserBalance = (params) => http.get(`/data/userBalance`, params)
// 查询总发放量
const getTotalSupply = (params) => http.get(`/data/totalSupplyFromSync`, params)
// 查询发放和销毁流水
const getIssueAndDestroyFlowList = (params) => http.get(`/data/issueAndDestroyFlowList/${params.pageNumber}/${params.pageSize}`, params)

export default {
  getEchartByDay,
  getUserListData,
  getUserFlowData,
  getGeneralData,
  getGeneralHistoryData,
  saveConfig,
  getConfigData,
  getEchartByHour,
  getIsLoading,
  getUserBalance,
  getAdminListData,
  getTotalSupply,
  getIssueAndDestroyFlowList
}
