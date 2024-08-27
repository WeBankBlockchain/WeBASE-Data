import { Message } from 'element-ui'
import Vue from 'vue'
import i18n from '../i18n'
function numMulti(num1, num2) {
  let baseNum = 0
  try {
    baseNum += num1.toString().split('.')[1].length
  } catch (e) {}
  try {
    baseNum += num2.toString().split('.')[1].length
  } catch (e) {}
  return (Number(num1.toString().replace('.', '')) * Number(num2.toString().replace('.', ''))) / Math.pow(10, baseNum)
}

function copy(val, message, errorMessage = i18n.t('copy_fail')){
  const v = Array.isArray(val) ? JSON.stringify(val) : val;
  setTimeout(() => {
    Vue.prototype.$copyText(v)
    .then(() => {
      Message.success(message)
    })
    .catch(() => {
      Message.error(errorMessage)
    });
  },0)
}

// function copy(val, message) {
//   // 模拟 输入框
//   // debugger
//   const cInput = document.createElement('input')
//   cInput.value = val ? JSON.stringify(val) : val
//   document.body.appendChild(cInput)
//   cInput.select() // 选取文本框内容
//   // 执行浏览器复制命令
//   // 复制命令会将当前选中的内容复制到剪切板中（这里就是创建的input标签）
//   // Input要在正常的编辑状态下原生复制方法才会生效
//   document.execCommand('copy')
//   Message.success(message)
//   // 复制成功后再将构造的标签 移除
//   document.body.removeChild(cInput)
// }

const changeDate = function (date) {
  const newData = new Date(date)
  const Y = newData.getFullYear()
  const M = newData.getMonth() + 1 > 9 ? newData.getMonth() + 1 : '0' + (newData.getMonth() + 1)
  const D = newData.getDate() > 9 ? newData.getDate() : '0' + newData.getDate()
  return Y + '-' + M + '-' + D
}
function changWeek(data) {
  const lastDate = new Date().getTime()
  const firstDate = lastDate - 14 * 24 * 3600 * 1000
  const dateList = []
  dateList[0] = {}
  dateList[0].txs = 0
  dateList[0].day = firstDate
  for (let i = 1; i < 15; i++) {
    dateList[i] = {}
    dateList[i].day = dateList[i - 1].day + 24 * 3600 * 1000
    dateList[i].txs = 0
  }
  for (let i = 0; i < 15; i++) {
    dateList[i].day = changeDate(dateList[i].day)
  }
  dateList.forEach(function (value) {
    if (data && data.length) {
      for (let j = 0; j < data.length; j++) {
        if (value.day === data[j].timestamp) {
          value.txs = data[j].txs
        } else if (data[j].timestamp && value.day === changeDate(data[j].timestamp).substring(0, 10)) {
          value.txs = data[j].txs
        }
      }
    }
  })
  console.log(dateList)
  return dateList
}
function newChangWeek(data) {
  console.log('data',data);
  const lastDate = new Date().getTime()
  const firstDate = lastDate - 14 * 24 * 3600 * 1000
  const dateList = []
  dateList[0] = {}
  dateList[0].amount = 0
  dateList[0].day = firstDate
  for (let i = 1; i < 15; i++) {
    dateList[i] = {}
    dateList[i].day = dateList[i - 1].day + 24 * 3600 * 1000
    dateList[i].amount = 0
  }
  for (let i = 0; i < 15; i++) {
    dateList[i].day = changeDate(dateList[i].day)
  }
  console.log('1',dateList);
  dateList.forEach(function (value) {
    if (data && data.length) {
      for (let j = 0; j < data.length; j++) {
        if (value.day === data[j].date) {
          value.amount = data[j].amount
        } else if (data[j].date && value.day === changeDate(data[j].date).substring(0, 10)) {
          value.amount = data[j].amount
        }
      }
    }
  })
  console.log(dateList)
  return dateList
}

export { numMulti, copy, changWeek, changeDate,newChangWeek }
