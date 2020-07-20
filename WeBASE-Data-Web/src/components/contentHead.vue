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
            <a v-if="headHref" target="_blank" :href="headHref.href" class="font-color-fff font-12">{{headHref.content}}</a>
        </div>
        <div class="content-head-network">
            <el-dropdown trigger="click" @command="changeGroup" placement="bottom" v-if="chainId">
                <span class="cursor-pointer font-color-fff" @click="groupVisible = !groupVisible">
                    {{this.$t("head.group")}}: {{groupName}}<i :class="[groupVisible?'el-icon-arrow-up':'el-icon-arrow-down']"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                    <ul style="max-height: 220px;overflow-y:auto" class="text-center">
                        <el-dropdown-item v-for=" item in groupList" :key="item.group" :command="item">
                            <span>{{item.groupName}}</span>
                            <span style="font-size: 12px;">{{item.appName}}</span>
                        </el-dropdown-item>
                    </ul>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
import { groupList } from "@/util/api";
import router from "@/router";
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
        headTitle: function (val) {
            this.title = val;
        },
        $route: {
            handler(to,from) {
                if (this.$route.params.chainId) {
                    localStorage.removeItem('groupId')
                    this.chainId = this.$route.params.chainId
                    this.queryGroup()
                }
            },
            deep: true,
            immediate: true
        }
    },
    data: function () {
        return {
            title: this.headTitle,
            groupName: "-",
            accountName: "-",
            dialogShow: false,
            path: "",
            headIcon: this.icon || false,
            way: this.route || "",
            changePasswordDialogVisible: false,
            groupList: [],
            groupVisible: false,
            chainId: ''
        };
    },
    mounted: function () {
        console.log()
    },
    methods: {
        skip: function () {
            if (this.route) {
                this.$router.push(this.way);
            } else {
                this.$router.go(-1);
            }
        },
        queryGroup() {
            groupList(this.chainId)
                .then(res => {
                    if (res.data.code === 0) {
                        this.groupList = res.data.data
                        if (!localStorage.getItem('groupId')) {
                            this.groupName = res.data.data[0]['groupName']
                            this.groupId = res.data.data[0]['groupId']
                            this.$emit('changGroup', this.groupId);
                        }else {
                            this.groupName = localStorage.getItem('groupName')
                            this.groupId = localStorage.getItem('groupId')
                            this.$emit('changGroup', this.groupId);
                        }
                        
                        
                    }
                })

        },
        changeGroup: function (val) {
            this.groupName = val.groupName
            localStorage.setItem("groupName", val.groupName);
            localStorage.setItem("groupId", val.groupId);
            this.$emit('changGroup', val.groupId);
        },
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
