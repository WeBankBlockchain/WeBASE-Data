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
    <div>
        <v-content-head :headTitle="`链${chainId}`" :headSubTitle="`群组${groupId}(区块列表)`" :icon="true"></v-content-head>
        <div class="module-wrapper">
            <div class="search-part">
                <div class="search-part-left-bg">
                    <span>共</span>
                    <span>{{numberFormat(total, 0, ".", ",")}}</span>
                    <span>条</span>
                </div>
                <div class="search-part-right">
                    <el-input :placeholder="$t('inputText.blockInput')" v-model.trim="searchKey.value" @keyup.enter.native="search" class="input-with-select" clearable @clear="clearText">
                        <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
                    </el-input>
                </div>
            </div>
            <div class="search-table">
                <el-table :data="blockData" class="block-table-content" v-loading="loading" @row-click="clickTable" ref="refTable">
                    <el-table-column prop="blockNumber" label="块高" width="120" align="center">
                        <template slot-scope="scope">
                            <span >{{scope.row.blockNumber}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="transCount" label="交易数量" width="100" align="center">
                        <template slot-scope="scope">
                            <span class="">{{scope.row['transCount']}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="blockHash" label="区块Hash" :show-overflow-tooltip="true" align="center">
                        <template slot-scope="scope">
                            <span class="">
                                <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['blockHash'], $event)" title="复制"></i>
                                {{scope.row['blockHash']}}   
                            </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sealer" label="打包节点" :show-overflow-tooltip="true" align="center">
                        <template slot-scope="scope">
                            <span class="">
                                <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['sealer'], $event)" title="复制"></i>
                                {{scope.row['sealer']}}   
                            </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="blockTimestamp" label="出块时间" width="280" :show-overflow-tooltip="true" align="center">
                        <template slot-scope="scope">
                            <span class="">{{scope.row['blockTimestamp']}}</span>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total">
                </el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
import contentHead from "@/components/contentHead";
import { blockList } from "@/util/api";
import { numberFormat } from "@/util/util";
import clip from "@/util/clipboard";
export default {
    name: "blockInfo",
    components: {
        "v-content-head": contentHead
    },
    data: function() {
        return {
            blockData: [],
            currentPage: 1,
            pageSize: 10,
            total: 0,
            loading: false,
            numberFormat: numberFormat,
            searchKey: {
                key: "",
                value: ""
            },
            chainId: '',
            groupId: ''
        };
    },
    mounted: function() {
        if (this.$route.query.chainId || this.$route.query.groupId) {
            this.chainId = this.$route.query.chainId
            this.groupId = this.$route.query.groupId
        }
        if (this.$route.query.blockNumber) {
            this.searchKey.key = "blockNumber";
            this.searchKey.value = this.$route.query.blockNumber;
        }
        this.getBlockList();
    },
    methods: {
        search: function() {
            if (
                this.searchKey.key == "blockHash" &&
                this.searchKey.value.length != 66 &&
                this.searchKey.value
            ) {
                this.$message({
                    message: "搜索区块哈希不支持模糊查询",
                    type: "error",
                    duration: 2000
                });
            } else {
                this.currentPage = 1
                this.getBlockList();
            }
        },
        getBlockList: function() {
            this.loading = true;
            let reqData = {
                    chainId: this.chainId,
                    groupId: this.groupId,
                    pageNumber: this.currentPage,
                    pageSize: this.pageSize
                },
                reqQuery = {};
            
            if(this.searchKey.value){
                if(this.searchKey.value.length===66){
                    reqQuery.blockHash = this.searchKey.value;
                }else {
                    reqQuery.blockNumber = this.searchKey.value;
                }
            }
            blockList(reqData, reqQuery)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.blockData = res.data.data;
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
                        message: '系统异常',
                        type: "error",
                        duration: 2000
                    });
                    
                });
        },
        handleSizeChange: function(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.getBlockList();
        },
        handleCurrentChange: function(val) {
            this.currentPage = val;
            this.getBlockList();
        },
        
        clearText: function(){
            this.getBlockList()
        },
        handleCopy(text, event) {
            clip(text, event)
        },
        link: function(val) {
            this.$router.push({
                path: "/transactionInfo",
                query: {
                    chainId: this.chainId,
                    groupId: this.groupId,
                    blockNumber: val
                }
            });
        },
        clickTable: function(row, column, $event) {
            let nodeName = $event.target.nodeName;
            if (nodeName === "I") {
                return
            }
            this.link(row.blockNumber)
        },
    }
};
</script>
<style scoped>
.search-part {
    padding: 30px 0px;
    overflow: hidden;
    margin: 0;
}
.input-with-select {
    width: 610px;
}
.input-with-select>>>.el-input__inner {
    border-top-left-radius: 20px;
    border-bottom-left-radius: 20px;
    border: 1px solid #eaedf3;
    box-shadow: 0 3px 11px 0 rgba(159, 166, 189, 0.11);
}
.input-with-select>>>.el-input--suffix > .el-input__inner {
    box-shadow: none;
}
.input-with-select>>>.el-input-group__prepend {
    border-left-color: #fff;
}
.input-with-select>>>.el-input-group__append {
    border-top-right-radius: 20px;
    border-bottom-right-radius: 20px;
    box-shadow: 0 3px 11px 0 rgba(159, 166, 189, 0.11);
}
.input-with-select>>>.el-button {
    border: 1px solid #20d4d9;
    border-radius: inherit;
    background: #20d4d9;
    color: #fff;
}
.block-table-content {
    width: 100%;
    padding-bottom: 16px;
    font-size: 12px;
}
.block-table-content /deep/ .el-table__row {
    cursor: pointer;
}
</style>
