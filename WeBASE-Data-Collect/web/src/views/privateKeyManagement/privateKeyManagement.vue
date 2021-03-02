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
    <div class="rivate-key-management-wrapper">
        <v-contentHead :headTitle="'用户管理'" @changeChain="changeChain" @changeGroup="changeGroup"></v-contentHead>
        <div class="module-wrapper">
            <div class="search-part">
                <div class="search-part-left">
                    <el-button type="primary" class="search-part-left-btn" @click="$store.dispatch('switch_creat_user_dialog')">新增用户 </el-button>
                </div>
                <div class="search-part-right">
                    <el-input :clearable="true" placeholder="请输入用户名或公钥地址" v-model="userName" class="input-with-select" style="width: 370px;">
                        <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
                    </el-input>
                </div>
            </div>
            <div class="search-table">
                <el-table :data="rivateKeyList" tooltip-effect="dark" v-loading="loading">
                    <el-table-column v-for="head in rivateKeyHead" :label="head.name" :key="head.enName" show-overflow-tooltip :width="tdWidth[head.enName] || ''" align="center">
                        <template slot-scope="scope">
                            <template v-if="head.enName!='operate'">
                                <span v-if="head.enName ==='address'">
                                    <i class="wbs-icon-copy font-12 copy-public-key" v-show="scope.row[head.enName]" @click="copyPubilcKey(scope.row[head.enName])" title="复制"></i>
                                    {{scope.row[head.enName]}}
                                </span>
                                <span v-else-if="head.enName ==='userId'">
                                    <el-tooltip content="公钥" placement="top" effect="dark">
                                        <i class="wbs-icon-key-b font-12" style="color: #4F9DFF"></i>
                                    </el-tooltip>
                                    {{scope.row[head.enName]}}
                                </span>
                                <span v-else>{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else>
                                <el-button type="text" size="small" @click="deleteUser(scope.row)">删除</el-button>
                            </template>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
                </el-pagination>
            </div>
        </div>
        <el-dialog :visible.sync="$store.state.creatUserVisible" :title="`新建用户(链：${chainId}群组：${groupId})`" width="640px" :append-to-body="true" class="dialog-wrapper" v-if='$store.state.creatUserVisible' center>
            <v-creatUser @creatUserClose="creatUserClose" @bindUserClose="bindUserClose" ref="creatUser"></v-creatUser>
        </el-dialog>
    </div>
</template>


<script>
import contentHead from "@/components/contentHead";
import creatUser from "./components/creatUser.vue";
import { getUserList, deleteUser } from "@/util/api";
import errcode from "@/util/errcode";
export default {
    name: "RivateKeyManagement",
    components: {
        "v-contentHead": contentHead,
        "v-creatUser": creatUser,
    },
    data() {
        return {
            userName: "",
            loading: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            rivateKeyList: [],
            tdWidth: {
                publicKey: 450
            },
            chainId: localStorage.getItem("chainId"),
            groupId: localStorage.getItem("groupId")
        };
    },
    computed: {
        rivateKeyHead() {
            let data = [
                {
                    enName: "userName",
                    name: "用户名称"
                },
                {
                    enName: "userId",
                    name: "用户ID"
                },
                {
                    enName: "description",
                    name: "用户描述"
                },
                {
                    enName: "address",
                    name: "用户公钥地址信息"
                },
                {
                    enName: "createTime",
                    name: "创建时间"
                },
                {
                    enName: "operate",
                    name: "用户名称"
                }
            ];
            return data
        }
    },
    mounted() {
        this.getUserInfoData();
    },
    methods: {
        changeChain() {
            this.chainId = localStorage.getItem("chainId")
            this.getUserInfoData()
        },
        changeGroup(){
            this.groupId = localStorage.getItem("groupId")
            this.getUserInfoData()
        },
        getUserInfoData() {
            this.loading = true;
            let reqData = {
                pageNumber: this.currentPage,
                pageSize: this.pageSize
            },
                reqQuery = {
                    userParam: this.userName.replace(/^\s+|\s+$/g, ""),
                    chainId: localStorage.getItem('chainId'),
                    groupId: localStorage.getItem("groupId")
                };
            getUserList(reqData, reqQuery)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.rivateKeyList = res.data.data || [];
                        this.total = res.data.totalCount;
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.loading = false;
                    this.$message({
                        message: "系统错误",
                        type: "error",
                        duration: 2000
                    });
                });
        },
        search() {
            this.currentPage = 1;
            this.getUserInfoData();
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.getUserInfoData();
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.getUserInfoData();
        },
        creatUserClose() {
            this.getUserInfoData();
        },
        bindUserClose() {
            this.getUserInfoData();
        },
        deleteUser(params) {
            this.$confirm('是否确认删除?', '', {
                confirmButtonText: "确认",
                cancelButtonText: "取消",
            })
                .then(() => {
                    this.queryDeleteUser(params);
                })
                .catch(() => {
                    this.$message({
                        type: "info",
                        message: "取消",
                    });
                });
        },
        queryDeleteUser(params) {
            deleteUser(params.userId)
                .then(res => {
                    if (res.data.code == 0) {
                        this.getUserInfoData();
                        this.$message({
                            type: "success",
                            message: "删除成功"
                        });
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.$message({
                        message: "系统异常",
                        type: "error",
                        duration: 2000
                    });
                });
        },
        copyPubilcKey(val) {
            if (!val) {
                this.$message({
                    type: "fail",
                    showClose: true,
                    message: "key为空，不可复制",
                    duration: 2000
                });
            } else {
                this.$copyText(val).then(e => {
                    this.$message({
                        type: "success",
                        showClose: true,
                        message: "复制成功",
                        duration: 2000
                    });
                });
            }
        }
    }
};
</script>
<style scoped>
@import "./privateKeyManagement.css";
</style>
