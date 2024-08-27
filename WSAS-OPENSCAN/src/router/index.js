import Vue from 'vue'
import VueRouter from 'vue-router'
import i18n from '../i18n'
Vue.use(VueRouter)
const webName = 'Wecoscan'

const routes = [
  {
    path: '/noPermission',
    component: () => import(/* webpackChunkName: "about" */ '../views/noPermission/index.vue'),
    meta: {
      title: '暂无权限',
      requireAuth: false,
      permissionCheck: false
    }
  },
  {
    path: '/',
    redirect: '/layout',
    meta: {
      title: '',
      requireAuth: true,
      permissionCheck: false
    }
  },
  {
    path: '/ecc',
    component: () => import(/* webpackChunkName: "about" */ '../views/ecc/index.vue'),
    meta: {
      title: '',
      requireAuth: false,
      permissionCheck: false
    }
  },
  {
    path: '/layout',
    name: 'layout',
    redirect: '/chain',
    component: () => import(/* webpackChunkName: "about" */ '../views/layout/index.vue'),
    children: [
      // {
      //   name: 'indexPage',
      //   path: '/indexPage',
      //   component: () => import(/* webpackChunkName: "layout" */ '@/views/indexPage/index.vue'),
      //   meta: {
      //     title: `首页 | ${webName}`,
      //     bread: [{ text: '任务管理' }],
      //     requireAuth: false,
      //     permissionCheck: false,
      //     belong: 'taskManage',
      //     bgColor: '#f6f7fb'
      //   }
      // },

      {
        name: 'chain',
        path: '/chain',
        component: () => import(/* webpackChunkName: "layout" */ '@/views/chain/index.vue'),
        meta: {
          title: `${webName}`,
          requireAuth: false,
          permissionCheck: false,
          belong: 'taskManage',
          bgColor: '#F6F7FB'
        }
      },
      {
        name: 'user',
        path: '/user',
        component: () => import(/* webpackChunkName: "layout" */ '@/views/user/index.vue'),
        meta: {
          title: `用户 | ${webName}`,
          requireAuth: false,
          permissionCheck: false,
          belong: 'taskManage',
          bgColor: '#F6F7FB'
        }
      },

      {
        name: 'queryRecord',
        path: '/queryRecord',
        component: () => import(/* webpackChunkName: "layout" */ '@/views/queryRecord/index.vue'),
        meta: {
          title: `用户 | ${webName}`,
          requireAuth: false,
          permissionCheck: false,
          belong: 'taskManage',
          bgColor: '#F6F7FB'
        }
      },

      {
        name: 'block',
        path: '/block',
        component: () => import(/* webpackChunkName: "layout" */ '@/views/block/index.vue'),
        meta: {
          title: `${webName}`,
          requireAuth: false,
          permissionCheck: false,
          belong: 'taskManage',
          bgColor: '#F6F7FB'
        }
      },
      {
        name: 'transaction',
        path: '/transaction',
        component: () => import(/* webpackChunkName: "layout" */ '@/views/transaction/index.vue'),
        meta: {
          title: `${webName}`,
          requireAuth: false,
          permissionCheck: false,
          belong: 'taskManage',
          bgColor: '#F6F7FB'
        }
      },
      {
        name: 'notFound',
        path: '/notFound',
        component: () => import(/* webpackChunkName: "layout" */ '@/views/notFound/index.vue'),
        meta: {
          title: `${webName}`,
          requireAuth: false,
          permissionCheck: false,
          belong: 'taskManage',
          bgColor: '#F6F7FB'
        }
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router
