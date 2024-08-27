import http from '../utils/http'

// 获取通告
const getBoardInfo = (params) => http.get('/board/boardInfo', params)

const setBoardReadByID = (params) => http.put(`/board/read/${params.boardId}`, params)

const setBoardPriority = (params) => http.put(`/board/priority/${params.boardId}/${params.priority}`, params)

// 快捷登录

export default { getBoardInfo, setBoardReadByID, setBoardPriority }
