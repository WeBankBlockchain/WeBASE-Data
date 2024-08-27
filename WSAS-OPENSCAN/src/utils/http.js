'use strict'

import axios from 'axios'
import store from 'Store'
import fileDownload from 'js-file-download'
import { Message } from 'element-ui'
import i18n from '../i18n'
let messageInstance = null

const http = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL,
  timeout: 30000
})
http.interceptors.request.use(
  (config) => {
    const authorization = store.state.authorization
    authorization && (config.headers.Authorization = authorization)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 请求网络失败 回调先走拦截器 => 再走 post.catch => 再走页面的 try-catch
// 请求返回code不对 进拦截器response后=> 直接走 post.catch => 再走页面的 try-catch
// 错误提示 统一处理 不需要 页面进行额外处理 包括token失效,断网

http.interceptors.response.use(
  (response) => {
    const { hideLoading } = response.config
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    const data = response.data
    const { code, message } = data
    if (code && code !== '0') {
      if (!messageInstance) {
        if (message && !hideLoading) {
          messageInstance = Message({
            type: 'error',
            message: message,
            duration: 5000,
            onClose() {
              messageInstance = null
            }
          })
        }
      }
    }
    return Promise.resolve(data)
  },
  // 网络请求失败会走这里
  (error) => {
    console.log('error:', error)
    const { hideLoading } = error.config
    // 兼容后台处理
    const data = error.response.data
    if (data) {
      const { message } = data
      console.log(messageInstance, 'messageInstance')
      if (!messageInstance && !hideLoading) {
        messageInstance = Message({
          type: 'error',
          message: message || i18n.t('networkError'),
          duration: 5000,
          onClose() {
            messageInstance = null
          }
        })
      }
      return Promise.resolve(data)
    } else {
      if (!messageInstance && !hideLoading) {
        messageInstance = Message({
          type: 'error',
          message: i18n.t('networkError'),
          duration: 5000,
          onClose() {
            messageInstance = null
          }
        })
      }
      return Promise.reject(error)
    }
  }
)
export default {
  post(url, params) {
    return new Promise((resolve) => {
      http
        .post(url, params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          resolve(err)
        })
    })
  },
  get(url, params = {}) {
    const { hideLoading = false, ...rest } = params
    return new Promise((resolve, reject) => {
      http
        .get(url, {
          params: rest,
          hideLoading
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          resolve(err)
        })
    })
  },
  delete(url, params) {
    return new Promise((resolve, reject) => {
      http
        .delete(url, {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          resolve(err)
        })
    })
  },
  put(url, params) {
    return new Promise((resolve, reject) => {
      http
        .put(url, params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          resolve(err)
        })
    })
  },
  patch(url, params) {
    return new Promise((resolve, reject) => {
      http
        .patch(url, params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          resolve(err)
        })
    })
  },
  getStream(url, params) {
    return new Promise((resolve, reject) => {
      http
        .get(
          url,
          {
            params: params
          },
          {
            responseType: 'blob'
          }
        )
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          resolve(err)
        })
    })
  },
  download(url, params, fileName = 'temp.xlsx') {
    console.log(params, 'params')
    return new Promise((resolve, reject) => {
      http
        .get(
          url,
          {
            params: params
          },
          {
            responseType: 'blob'
          }
        )
        .then((res) => {
          fileDownload(res, fileName)
          resolve({ code: '0' })
        })
        .catch((err) => {
          resolve(err)
        })
    })
  },
  uploadAjax(url, params) {
    return new Promise((resolve, reject) => {
      http
        .post(url, params, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          resolve(err)
        })
    })
  }
}
