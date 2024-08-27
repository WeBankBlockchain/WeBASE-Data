import { getStore } from 'Utils/sessionstore'
import { defaultLang } from 'Utils/config.js'
const state = {
  authorization: getStore('authorization') || '',
  permission: getStore('permission') || [],
  userinfo: getStore('userinfo') || '',
  chainName: getStore('chainName') || 'transData',
  noticeData: {},
  showNoticeDetail: false,
  active: getStore('active') || '0',
  lang: getStore('lang') || defaultLang
}
export default state
