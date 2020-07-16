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
    <div class="bg-f7f7f7">
        <content-head :headTitle="`链${chainId}`" :headSubTitle="`群组${groupId}(交易列表)`" :icon="true"></content-head>
        <div class="module-wrapper">
            <div class="search-part">
                <div class="search-part-left-bg"> 
                    <span>共</span>
                    <span>{{numberFormat(total, 0, ".", ",")}}</span>
                    <span>条</span>
                </div>
                <div class="search-part-right">
                    <el-input :placeholder="$t('inputText.transactionSearch')" v-model.trim="searchKey.value" @keyup.enter.native="search" class="input-with-select" clearable @clear="clearText">
                        <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
                    </el-input>
                </div>
            </div>
            <div class="search-table">
                <el-table :data="transactionList" class="block-table-content"  :row-key="getRowKeys" :expand-row-keys="expands" v-loading="loading" ref="refTable">
                    <el-table-column type="expand" align="center">
                        <template slot-scope="scope">
                            <transaction-detail :txData="scope.row"></transaction-detail>
                        </template>
                    </el-table-column>
                    <el-table-column prop="transHash" label="交易Hash" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span>
                                <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['transHash'], $event)" title="复制"></i>
                                {{scope.row['transHash']}}
                            </span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="blockNumber" label="块高" width="160" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span>{{scope.row['blockNumber']}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="transDetail" label="交易详情" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['transDetail'], $event)" title="复制"></i>
                            <span>{{scope.row['transDetail']}}</span>
                        </template>
                    </el-table-column>
                    <!-- <el-table-column prop="auditFlag" label="统计" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span :style="{'color': (scope.row['auditFlag'] == 1 ? '#E6A23C': '#67C23A')}">{{scope.row['auditFlag'] | statusFilter}}</span>
                        </template>
                    </el-table-column> -->
                    <el-table-column prop="receiptDetail" label="交易回执详情" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['receiptDetail'], $event)" title="复制"></i>
                            <span>{{scope.row['receiptDetail']}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="blockTimestamp" label="出块时间" width="280" :show-overflow-tooltip="true" align="center">
                        <template slot-scope="scope">
                            <span>{{scope.row['blockTimestamp']}}</span>
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
import TransactionDetail from "@/components/TransactionDetail/index.vue";
import { transList } from "@/util/api";
import { numberFormat } from "@/util/util";
import clip from "@/util/clipboard";
export default {
    name: "transaction",
    components: {
        contentHead,
        TransactionDetail
    },
    filters: {
        statusFilter(status) {
            switch (status) {
                case 1:
                    return "未审计"
                    break;

                case 2:
                    return "已审计"
                    break;
            }
        }
    },
    data() {
        return {
            transactionList: [],
            expands: [],
            searchKey: {
                key: "",
                value: ""
            },
            currentPage: 1,
            pageSize: 10,
            total: 0,
            loading: false,
            numberFormat: numberFormat,
            chainId: '',
            groupId: '',
            getRowKeys(row) {
                return row.transHash;
            }
        };
    },
    mounted() {
        if (this.$route.query.chainId || this.$route.query.groupId) {
            this.chainId = this.$route.query.chainId
            this.groupId = this.$route.query.groupId
        }
        if(this.$route.query.transHash){
            this.searchKey.value = this.$route.query.transHash
        }
        if(this.$route.query.blockNumber){
            this.searchKey.value = this.$route.query.blockNumber
        }
        
        this.getTransaction();
    },
    methods: {
        search() {
            if (
                this.searchKey.key == "transactionHash" &&
                this.searchKey.value.length != 66 &&
                this.searchKey.value
            ) {
                this.$message({
                    message: '搜索交易哈希不支持模糊查询',
                    type: "error",
                    duration: 2000
                });
            } else {
                this.currentPage = 1
                this.getTransaction();
            }
        },
        getTransaction() {
            this.expands = [];
            this.loading = true;
            let reqData = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageNumber: this.currentPage,
                pageSize: this.pageSize
            },
                reqQuery = {};
            if (this.searchKey.value) {
                if (this.searchKey.value.length === 66) {
                    reqQuery.transHash = this.searchKey.value;
                } else {
                    reqQuery.blockNumber = this.searchKey.value;
                }
            }
            transList(reqData, reqQuery)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.transactionList = res.data.data;
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
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.getTransaction();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.getTransaction();
        },
        clickTable(row, column, $event) {
            let nodeName = $event.target.nodeName;
            if (nodeName === "I") {
                return
            }
            this.$refs.refTable.toggleRowExpansion(row);
        },
        clearText() {
            this.getTransaction();
        },
        handleCopy(text, event) {
            clip(text, event)
        },
    }
};
</script>
<style scoped>
.block-table-content {
    width: 100%;
    padding-bottom: 16px;
    font-size: 12px;
}
.block-table-content >>> .el-table__expanded-cell {
    padding: 12px 6px;
}
.block-table-content >>> .el-table__expand-icon > .el-icon {
    font-size: 14px;
}
.block-table-content >>> .el-table__row {
    /* cursor: pointer; */
}
.search-part {
    padding: 30px 0px;
    overflow: hidden;
    margin: 0;
}
.search-part::after {
    display: block;
    content: "";
    clear: both;
}
.input-with-select {
    width: 610px;
}
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
.input-with-select >>> .el-input__inner {
    border-top-left-radius: 20px;
    border-bottom-left-radius: 20px;
    border: 1px solid #eaedf3;
    box-shadow: 0 3px 11px 0 rgba(159, 166, 189, 0.11);
}
.input-with-select >>> .el-input--suffix > .el-input__inner {
    box-shadow: none;
}
.input-with-select >>> .el-input-group__prepend {
    border-left-color: #fff;
}
.input-with-select >>> .el-input-group__append {
    border-top-right-radius: 20px;
    border-bottom-right-radius: 20px;
    box-shadow: 0 3px 11px 0 rgba(159, 166, 189, 0.11);
}
</style>

