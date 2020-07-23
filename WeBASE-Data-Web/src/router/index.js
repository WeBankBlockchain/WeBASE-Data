/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import Vue from 'vue'
import Router from 'vue-router'
import { getCookie } from '@/util/util'

const main = resolve => require(['@/views/index/main'], resolve);
const home = resolve => require(['@/views/home'], resolve);
const chain = resolve => require(['@/views/chain'], resolve);
const group = resolve => require(['@/views/group'], resolve);
const overview = resolve => require(['@/views/overview'], resolve);
const blockInfo = resolve => require(['@/views/blockInfo'], resolve);
const transactionInfo = resolve => require(['@/views/transactionInfo'], resolve);
const contractInfo = resolve => require(['@/views/contractInfo'], resolve);
const userInfo = resolve => require(['@/views/userInfo'], resolve);
const keywordConfig = resolve => require(['@/views/keywordConfig/index.vue'], resolve);
const alarm = resolve => require(['@/views/alarm/index.vue'], resolve);
Vue.use(Router);
const routes = [
    {
        path: '/',
        redirect: '/login',
    },
    {
        path: '/login',
        name: 'login',
        component: resolve => require(['@/views/login/index.vue'], resolve),
    },
    {
        path: '/main',
        name: 'main',
        redirect: '/home',
        leaf: true,
        nameKey: "home",    
        menuShow: true,
        iconCls: 'wbs-icon-gailan sidebar-icon',
        component: main,
        children: [
            {
                path: '/home', component: home, name: '搜索',nameKey: "home", menuShow: true, meta: { requireAuth: true }
            }
        ]
    },
    {
        path: '/',
        component: main,
        name: '区块链',
        nameKey: "chain",
        leaf: true,
        menuShow: true,
        iconCls: 'wbs-icon-group sidebar-icon',
        children: [
            { path: '/chain', component: chain, name: '区块链', nameKey: "nodeTitle", menuShow: true, meta: { requireAuth: true } },
        ]
    }, 
    {
        path: '/',
        component: main,
        name: '群组',
        nameKey: "group",
        leaf: true,
        menuShow: false,
        iconCls: 'wbs-icon-group sidebar-icon',
        children: [
            { path: '/group', component: group, name: '群组', nameKey: "group", menuShow: true, meta: { requireAuth: true } },
        ]
    },
    {
        path: '/',
        component: main,
        name: '概览',
        nameKey: "overview",
        leaf: true,
        menuShow: false,
        iconCls: 'wbs-icon-group sidebar-icon',
        children: [
            { path: '/overview/:chainId/:chainName/:groupId', component: overview, name: '概览', nameKey: "overview", menuShow: true, meta: { requireAuth: true } },
        ]
    },
    {
        path: '/',
        component: main,
        name: '区块浏览',
        nameKey: "blockBrowsing",
        menuShow: false,
        iconCls: 'wbs-icon-overview sidebar-icon',
        children: [
            { path: '/blockInfo', component: blockInfo, name: '区块信息', nameKey: "blockTitle", menuShow: true, meta: { requireAuth: true } },
            { path: '/transactionInfo', component: transactionInfo, name: '交易信息', nameKey: "transactionInfo", menuShow: true, meta: { requireAuth: true } },
            { path: '/contractInfo', component: contractInfo, name: '合约信息', nameKey: "contractInfo", menuShow: true, meta: { requireAuth: true } },
            { path: '/userInfo', component: userInfo, name: '用户信息', nameKey: "userInfo", menuShow: true, meta: { requireAuth: true } },
            
        ]
    },
    {
        path: '/',
        component: main,
        name: '配置',
        nameKey: "keywordConfig",
        leaf: true,
        menuShow: true,
        iconCls: 'wbs-icon-key sidebar-icon',
        children: [
            { path: '/keywordConfig', component: keywordConfig, name: '配置', nameKey: "keywordConfig", menuShow: true, meta: { requireAuth: true } }
        ]
    }, 
    {
        path: '/',
        component: main,
        name: '告警列表',
        nameKey: "alarm",
        leaf: true,
        menuShow: true,
        iconCls: 'wbs-icon-wenjian sidebar-icon',
        children: [
            { path: '/alarm', component: alarm, name: '告警列表', nameKey: "alarm", menuShow: true, meta: { requireAuth: true } }
        ]
    }, 
]
const router = new Router({
    routes
});

const originalPush = Router.prototype.push;
Router.prototype.push = function push(location, onResolve, onReject) {
    if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
    return originalPush.call(this, location).catch(err => err)
}
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}
export default router
