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
<template>
    <div class="content-head-wrapper">
        <div class="content-head-title">
            <span class="content-head-icon" v-if="icon" @click="skip">
                <i class="wbs-icon-back"></i>
            </span>
            <span :class="{ 'font-color-9da2ab': headSubTitle}">{{title}}</span>
            <span v-show="headSubTitle" class="font-color-9da2ab">/</span>
            <span>{{headSubTitle}}</span>
            <el-tooltip effect="dark" placement="bottom-start" v-if="headTooltip" offset='0'>
                <div slot="content">{{headTooltip}}</div>
                <i class="el-icon-info contract-icon font-15"></i>
            </el-tooltip>
        </div>
        <div class="content-head-network">
            <el-dropdown trigger="click" @command="changeChain" placement="bottom" v-if="showChain">
                <span class="cursor-pointer font-color-fff" @click="chainVisible = !chainVisible">
                    区块链: <span>{{chainName}}</span><i :class="[chainVisible?'el-icon-arrow-up':'el-icon-arrow-down']"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                    <ul style="max-height: 220px;overflow-y:auto" class="text-center">
                        <el-dropdown-item v-for=" item in chainList" :key="item.chainId" :command="item">
                            {{item.chainName}}
                        </el-dropdown-item>
                    </ul>
                </el-dropdown-menu>
            </el-dropdown>
            <el-dropdown trigger="click" @command="changeGroup" placement="bottom" v-if="showGroup">
                <span class="cursor-pointer font-color-fff" @click="groupVisible = !groupVisible">
                    群组: {{appName}}<i :class="[groupVisible?'el-icon-arrow-up':'el-icon-arrow-down']"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                    <ul style="max-height: 220px;overflow-y:auto" class="text-center">
                        <el-dropdown-item v-for=" item in groupList" :key="item.groupId" :command="item">
                            {{item.appName}}
                        </el-dropdown-item>
                    </ul>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
import router from "@/router";
import { getChains, getGroups } from "@/util/api";
import { delCookie } from '@/util/util'
import Bus from "@/bus"
export default {
    name: "conetnt-head",
    props: {
        headTitle: {
            type: String
        },
        icon: {
            type: Boolean
        },
        route: {
            type: String
        },
        headSubTitle: {
            type: String
        },
        headTooltip: {
            type: String
        },
        headHref: {
            type: Object
        },
        updateGroup: {
            type: Number
        },
        updateGroupType: {
            type: String
        }
    },
    components: {

    },
    watch: {
        headTitle(val) {
            this.title = val;
        },
        $route: {
            handler(to, from) {

                if (this.$route.path == '/contract' || this.$route.path == '/privateKeyManagement') {
                    this.showChain = true;
                    this.showGroup = true
                }
                if (this.$route.path == '/front') {
                    this.showChain = true;
                }

            },
            deep: true,
            immediate: true
        }
    },
    data() {
        return {
            title: this.headTitle,
            appName: "-",
            accountName: "-",
            dialogShow: false,
            path: "",
            headIcon: this.icon || false,
            way: this.route || "",
            changePasswordDialogVisible: false,
            groupList: [],
            groupVisible: false,
            chainVisible: false,
            chainName: "-",
            chainList: [],
            showChain: false,
            showGroup: false
        };
    },
    beforeDestroy() {

    },
    mounted() {
        if (localStorage.getItem("chainName")) {
            this.chainName = localStorage.getItem("chainName");
        }
        if (localStorage.getItem("appName")) {
            this.appName = localStorage.getItem("appName")
        }
        this.queryChainList(this.queryGroupList)
    },
    methods: {
        queryChainList(callback) {
            getChains()
                .then(res => {
                    if (res.data.code === 0) {
                        var list = res.data.data;
                        var chainNameList = list.map(item=>{
                            return item.chainName
                        })
                        this.chainList = list;
                        if (!list.length) return;
                        if (!localStorage.getItem("chainName") || !localStorage.getItem("chainId") || !localStorage.getItem("chainType") || !localStorage.getItem("encryptType") || !chainNameList.includes(localStorage.getItem("chainName"))) {
                            this.chainName = list[0]['chainName']
                            localStorage.setItem("chainName", list[0]['chainName'])
                            localStorage.setItem("chainId", list[0]['chainId'])
                            localStorage.setItem("chainType", list[0]['chainType'])
                            localStorage.setItem("encryptType", list[0]['encryptType'])
                        }
                        callback()
                    } else {
                        this.chainList = [];
                        localStorage.setItem("chainName", "")
                        localStorage.setItem("chainId", "")
                        this.$message({
                            type: "error",
                            message: this.$chooseLang(res.data.code)
                        })
                    }
                })
                .catch(err => {
                    this.chainList = [];
                    localStorage.setItem("chainName", "")
                    localStorage.setItem("chainId", "")
                    this.$message({
                        message: "系统错误",
                        type: "error",
                        duration: 2000
                    });
                })
        },
        changeChain(val) {
            this.chainName = val.chainName
            localStorage.setItem("chainName", val.chainName);
            localStorage.setItem("chainId", val.chainId);
            localStorage.setItem("chainType", val.chainType);
            localStorage.setItem("encryptType", val.encryptType);
            this.queryGroupList()
            this.$emit('changeChain', val.encryptType);
        },
        queryGroupList() {
            getGroups(localStorage.getItem('chainId')).then(res => {
                if (res.data.code === 0) {
                    var list = res.data.data;
                    this.groupList = list;
                    if (!list.length) return;
                    let arr = [], appNameList = []
                    list.forEach(item => {
                        arr.push(item.groupId)
                        appNameList.push(item.appName)
                    });
                    
                    let is = arr.includes(+localStorage.getItem("groupId"))
                    let isAppName = appNameList.includes(localStorage.getItem("appName"))
                    if (!is || !isAppName) {
                        this.appName = list[0]['appName']
                        localStorage.setItem("appName", list[0]['appName'])
                        localStorage.setItem("groupId", list[0]['groupId'])
                    }
                    // if (!localStorage.getItem('groupId')&&!is) {
                    //     this.appName = list[0]['appName']
                    //     localStorage.setItem("appName", list[0]['appName'])
                    //     localStorage.setItem("groupId", list[0]['groupId'])
                    // }
                } else {
                    this.$message({
                        type: "error",
                        message: this.$chooseLang(res.data.code)
                    })
                }
            }).catch(err => {
                this.$message({
                    type: "error",
                    message: "系统错误"
                })
            })
        },
        changeGroup(val) {
            this.appName = val.appName
            localStorage.setItem("appName", val.appName);
            localStorage.setItem("groupId", val.groupId);
            this.$emit('changeGroup', val.groupId);
        },
        skip() {
            if (this.route) {
                this.$router.push(this.way);
            } else {
                this.$router.go(-1);
            }
        },
        groupStatusColor(key) {
            switch (key) {
                case 1:
                    return 'rgb(88, 203, 125)'
                    break;

                case 2:
                    return '#E6A23C'
                    break;
                case 3:
                    return '#F56C6C'
                    break;
                case 4:
                    return '#F56C6C'
                    break;
            }
        }
    }
};
</script>
<style scoped>
.content-head-wrapper {
    width: calc(100%);
    background-color: #181f2e;
    text-align: left;
    line-height: 54px;
    position: relative;
}
.content-head-wrapper::after {
    display: block;
    content: "";
    clear: both;
}
.content-head-icon {
    color: #fff;
    font-weight: bold;
    cursor: pointer;
}
.content-head-title {
    margin-left: 40px;
    display: inline-block;
    font-size: 16px;
    color: #fff;
    font-weight: bold;
}
.content-head-network {
    float: right;
    padding-right: 10px;
    position: relative;
}
.browse-user {
    text-align: center;
    text-decoration: none;
    font-size: 12px;
    cursor: pointer;
    color: #cfd7db;
}
.sign-out-wrapper {
    line-height: 32px;
    text-align: center;
}
.sign-out {
    cursor: pointer;
    color: #ed5454;
}
.change-password {
    color: #0db1c1;
    cursor: pointer;
}
.network-name {
    font-size: 12px;
    color: #9da2ab;
    padding: 3px 0px;
    /* border-right: 2px solid #e7ebf0; */
    margin-right: 16px;
}
.select-network {
    color: #2d5f9e;
    cursor: default;
}
.content-head-network a:nth-child(1) {
    text-decoration: none;
    outline: none;
    color: #cfd7db;
    padding-right: 15px;
    border-right: 1px solid #657d95;
    margin-right: 15px;
}
.contant-head-name {
    position: relative;
    cursor: pointer;
}
.contant-head-name ul {
    position: absolute;
    width: 150%;
    left: -10px;
    top: 35px;
    background-color: #fff;
    color: #666;
    text-align: center;
    z-index: 9999999;
    box-shadow: 1px 4px 4px;
}
.contant-head-name ul li {
    width: 100%;
    padding: 0 10px;
    height: 32px;
    line-height: 32px;
    cursor: pointer;
}
.group-item {
    line-height: 32px;
    text-align: center;
    max-height: 200px;
    overflow-y: auto;
    position: relative;
}
.group-item-list {
    cursor: pointer;
}
.group-item-list:hover {
    color: #0db1c1;
}
.right-menu-item {
    padding: 0 20px;
}
.hover-effect {
    cursor: pointer;
    /* transition: background 0.3s; */
}
.content-head-lang {
    position: absolute;
    /* background-color: #fff; */
    right: 350px;
    top: 0px;
}
</style>
