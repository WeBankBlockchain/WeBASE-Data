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
        <content-head :headTitle="`链${chainId}`" :headSubTitle="`群组${groupId}(用户列表)`" :icon="true"></content-head>
        <div class="module-wrapper">
            <div class="search-part">
                <div class="search-part-left-bg">
                    <span>共</span>
                    <span>{{numberFormat(total, 0, ".", ",")}}</span>
                    <span>条</span>
                </div>
                <div class="search-part-right">
                    <el-input placeholder="请输入用户名或地址" v-model="userParam" @keyup.enter.native="search" clearable @clear="clearText" class="input-with-select">
                        <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
                    </el-input>
                </div>
            </div>
            <div class="search-table">
                <el-table :data="contractList" tooltip-effect="dark" v-loading="loading">
                    <el-table-column prop="userName" label="用户名" show-overflow-tooltip width="" align="center">
                        <template slot-scope="scope">
                            <i class="wbs-icon-copy font-12 copy-public-key" @click="handleCopy(scope.row.userName, $event)" title="复制"></i>
                            <span>{{scope.row.userName}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userAddress" label="用户地址" show-overflow-tooltip align="center">
                        <template slot-scope="scope">
                            <i class="wbs-icon-copy font-12 copy-public-key" @click="handleCopy(scope.row.userAddress, $event)" title="复制"></i>
                            <span>{{scope.row.userAddress}}</span>
                        </template>
                    </el-table-column>
                    </el-table-column>
                </el-table>
                <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
                </el-pagination>
            </div>
        </div>

    </div>
</template>
<script>
import contentHead from "@/components/contentHead";
import { userList } from "@/util/api"
import clip from "@/util/clipboard";
import { numberFormat } from "@/util/util";
export default {
    name: "oldContract",
    components: { contentHead },
    data() {
        return {
            contractList: [],
            loading: false,
            currentPage: 1,
            pageSize: 10,
            data: null,
            total: 0,
            disabled: false,
            chainId: '',
            groupId: '',
            userParam: '',
            numberFormat: numberFormat,
        }
    },
    mounted() {
        if (this.$route.query.chainId || this.$route.query.groupId) {
            this.chainId = this.$route.query.chainId
            this.groupId = this.$route.query.groupId
        }
        if(this.$route.query.userParam ){
            this.userParam = this.$route.query.userParam
        }
        this.queryUser()
    },
    methods: {
        clearText(){
            this.queryUser()
        },
        queryUser() {
            let reqData = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageNumber: this.currentPage,
                pageSize: this.pageSize,

            }, reqQuery = {
                userParam: this.replaceStartEndSpace(this.userParam)
            };
            
            userList(reqData, reqQuery).then(res => {
                if (res.data.code == 0) {
                    this.contractList = res.data.data || [];
                    this.total = res.data.totalCount || 0;
                } else {
                    this.$message({
                        message: this.$chooseLang(res.data.code),
                        type: "error",
                        duration: 2000
                    });
                }
            }).catch(err => {
                this.$message({
                    message: '系统错误',
                    type: "error",
                    duration: 2000
                });
            })
        },
        handleCopy(text, event) {
            clip(text, event)
        },
        search() {

            this.currentPage = 1
            this.queryUser();
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryUser();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryUser();
        },
    }
}
</script>
<style scoped>
.input-with-select >>> .el-input__inner {
    border-top-left-radius: 20px;
    border-bottom-left-radius: 20px;
    border: 1px solid #eaedf3;
    box-shadow: 0 3px 11px 0 rgba(159, 166, 189, 0.11);
}
.input-with-select >>> .el-input-group__append {
    border-top-right-radius: 20px;
    border-bottom-right-radius: 20px;
    box-shadow: 0 3px 11px 0 rgba(159, 166, 189, 0.11);
}
.input-with-select >>> .el-button {
    border: 1px solid #20d4d9;
    border-radius: inherit;
    background: #20d4d9;
    color: #fff;
}
.grayColor {
    color: #666 !important;
}
</style>


