import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'Assets/style/index.less'
import dayjs from 'dayjs'
import i18n from './i18n/index.js'
import VueClipboard from 'vue-clipboard2'
VueClipboard.config.autoSetContainer = true
Vue.use(VueClipboard)

// Vue.use(ElementUI)
Vue.config.productionTip = false

Vue.filter('thousandSeparator', (num) => {
  if (!num || !Number.isFinite(+num)) return num;

  // 处理负数情况
  const isNegative = num < 0;
  num = Math.abs(num);

  const decimals = Number.isInteger(num) ? '' : String(num).split('.')[1];
  const arr = String(parseInt(num)).split('').reverse();
  const formattedNum = `${arr
    .map((item, index) => {
      return index !== 0 && index % 3 === 0 ? item + ',' : item;
    })
    .reverse()
    .join('')}${decimals && '.' + decimals}`;

  // 添加负号
  return isNegative ? '-' + formattedNum : formattedNum;
});

// Vue.filter('timeTrans', (timestamp) => {
//   return dayjs(timestamp).format('MM-DD')
// })

Vue.filter('timeTrans', (timestamp) => {
  return dayjs(Number(timestamp)).format('YYYY-MM-DD HH:mm:ss')
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? to.meta.title : ''
  const authorization = store.state.authorization
  const permission = store.state.permission
  document.querySelector('.page-container') && document.querySelector('.page-container').scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'nearest' })
  // token 失效
  if (!authorization && to.meta.requireAuth) {
    next('/login')
  } else if (!permission.includes(to.name) && to.meta.permissionCheck) {
    // 没有权限
    next('/noPermission')
  } else {
    next()
  }
})

new Vue({
  i18n,
  router,
  store,
  render: (h) => h(App)
}).$mount('#app')
