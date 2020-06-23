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
const blank = resolve => require(['@/components/blank'], resolve);
const main = resolve => require(['@/views/index/main'], resolve);
const chain = resolve => require(['@/views/chain'], resolve);
const front = resolve => require(['@/views/front'], resolve);
const contract = resolve => require(['@/views/chaincode/contract'], resolve);
const rivateKeyManagement = resolve => require(['@/views/rivateKeyManagement/rivateKeyManagement'], resolve);
Vue.use(Router);
const routes = [
    {
        path: '/',
        redirect: '/chain',
    },
    {
        path: '/',
        component: main,
        name: '链管理',
        nameKey: "Chain",
        leaf: true,
        menuShow: true,
        iconCls: 'wbs-icon-qukuailian sidebar-icon',
        children: [
            { path: '/chain', component: chain, name: '链管理', nameKey: "Chain", menuShow: true, meta: { requireAuth: true } }
        ]
    },
    {
        path: '/',
        component: main,
        name: '前置管理',
        nameKey: "Front",
        leaf: true,
        menuShow: true,
        iconCls: 'wbs-icon-group sidebar-icon',
        children: [
            { path: '/front', component: front, name: '前置管理', nameKey: "Front", menuShow: true, meta: { requireAuth: true } }
        ]
    },
    {
        path: '/',
        component: main,
        name: '合约管理',
        nameKey: "contractTitle",
        leaf: true,
        menuShow: true,
        iconCls: 'wbs-icon-heyueguanli sidebar-icon', 
        children: [
            { path: '/contract', component: contract, name: '合约管理', nameKey: "contractTitle", menuShow: true, meta: { requireAuth: true } },
            
        ]
    },
    {
        path: '/',
        component: main,
        name: '用户管理',
        nameKey: "PrivateKey",
        leaf: true,
        menuShow: true,
        iconCls: 'wbs-icon-lock sidebar-icon',
        children: [
            { path: '/privateKeyManagement', component: rivateKeyManagement, name: '用户管理', nameKey: "PrivateKey", menuShow: true, meta: { requireAuth: true } }
        ]
    }, 
    {
        path: '/',
        component: main,
        name: '空白页',
        nameKey: "blank",
        leaf: true,
        menuShow: false,
        iconCls: 'wbs-icon-lock sidebar-icon',
        children: [
            { path: '/blank', component: blank, name: '空白页', nameKey: "blank", menuShow: true, meta: { requireAuth: true } }
        ]
    }
    
]
const router = new Router({
    routes
});
const originalPush = Router.prototype.push;
Router.prototype.push = function push(location, onResolve, onReject) {
    if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
    return originalPush.call(this, location).catch(err => err)
}

export default router
