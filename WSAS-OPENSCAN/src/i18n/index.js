import Vue from 'vue';
import VueI18n from 'vue-i18n'
import zh from './lang/zh'
import en from './lang/en'

import ElementUI from 'element-ui'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'

import { getStore } from 'Utils/sessionstore'
import { defaultLang } from 'Utils/config.js'
Vue.use(VueI18n);

const messages = {
  zh: {
    ...zh,
    ...zhLocale
  },
  en: {
    ...en,
    ...enLocale
  },
}

let lang = getStore('lang') || defaultLang

// 通过选项创建 VueI18n 实例
const i18n = new VueI18n({
  locale: lang, 
  fallbackLocale: lang,
  messages, 
})

Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value)
})

export default i18n