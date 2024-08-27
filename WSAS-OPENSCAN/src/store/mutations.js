import {
  SET_AUTHORIZATION,
  SET_PERMISSION,
  SET_USERINFO,
  SET_CHAIN_NAME,
  SET_NOTICE_DATA,
  SET_SHOW_NOTICE_DETAIL,
  SET_INDEX_ACTIVE,
  SET_I18N_LANG
} from './mutation-types'
import { setStore } from 'Utils/sessionstore'
import i18n from '../i18n'
// 必须是同步代码
const mutation = {
  [SET_AUTHORIZATION] (state, authorization) {
    state.authorization = authorization
    setStore('authorization', authorization)
  },
  [SET_PERMISSION] (state, permission) {
    state.permission = permission
    setStore('permission', permission)
  },
  [SET_USERINFO] (state, userinfo) {
    state.userinfo = userinfo
    setStore('userinfo', userinfo)
  },
  [SET_CHAIN_NAME] (state, chainName) {
    state.chainName = chainName
    setStore('chainName', chainName)
  },
  [SET_NOTICE_DATA] (state, noticeData) {
    state.noticeData = noticeData
    setStore('noticeData', noticeData)
  },
  [SET_SHOW_NOTICE_DETAIL] (state, showNoticeDetail) {
    state.showNoticeDetail = showNoticeDetail
    setStore('showNoticeDetail', showNoticeDetail)
  },
  [SET_INDEX_ACTIVE](state,active){
    state.active = active
    setStore('active',active)
  },
  [SET_I18N_LANG](state,lang){
    // 如果有传入，则使用传入参数 否则取当前语言
    let langValue = lang ? lang : (state.lang === 'zh' ? 'en' : 'zh')
    i18n.locale = langValue
    state.lang = langValue
    setStore('lang',langValue)
  }
}
export default mutation
