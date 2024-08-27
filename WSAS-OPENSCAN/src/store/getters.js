// 相当于store的计算属性
// Getter 也可以接受其他 getter 作为第二个参数：
const getters = {
  authorization (state) {
    return state.authorization
  },
  permission (state) {
    return state.permission
  },
  userinfo (state) {
    return state.userinfo
  },
  chainName (state) {
    return state.chainName
  },
  noticeData (state) {
    return state.noticeData
  },
  showNoticeDetail (state) {
    return state.showNoticeDetail
  },
  active(state){
    return state.active
  },
  lang(state){
    return state.lang
  }
}

export default getters
