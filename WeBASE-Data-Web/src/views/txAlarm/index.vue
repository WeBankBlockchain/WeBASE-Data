<template>
    <div>
        <v-content-head :headTitle="'交易告警'"></v-content-head>
        <div class="module-wrapper">
        </div>
        <div class="module-wrapper">
            <!-- <div style="padding: 10px 0 0 40px; font-weight: bold;font-size: 16px;">告警列表</div> -->
            <div class="search-table">
                <el-table :data="txAlarmList" tooltip-effect="dark" v-loading="loading">
                    <el-table-column type="expand" align="center">
                        <template slot-scope="scope">
                            <detail :txData="scope.row"></detail>
                        </template>
                    </el-table-column>
                    <el-table-column v-for="head in txAlarmHead" :label="head.name" :key="head.enName" :width='head.width' show-overflow-tooltip align="center">
                        <template slot-scope="scope">
                            <template v-if="head.enName!='operate'">
                                <span v-if="head.enName=='status'" :style="{'color': statusColor(scope.row[head.enName])}">{{statusText(scope.row[head.enName])}}</span>
                                <span v-else-if="head.enName=='txHash'">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row[head.enName], $event)" title="复制"></i>
                                    <span class="link" @click="link(scope.row,'transHash')">{{scope.row[head.enName]}}</span>
                                </span>
                                <span v-else-if="head.enName=='address'">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row[head.enName], $event)" title="复制"></i>
                                    <span  class="link" @click="link(scope.row,'userAddress')">{{scope.row[head.enName]}}</span>
                                </span>
                                <span v-else>
                                    <span v-if="scope.row[head.enName]">{{scope.row[head.enName]}}</span>
                                    <span v-else>-</span>
                                </span>
                            </template>
                            <template v-else>
                                <el-button type="text" size="small" @click="deleteKeyword(scope.row,'modify')">删除</el-button>
                                <el-button type="text" size="small" @click="handleBtn(scope.row)">{{btnText(scope.row['status'])}}</el-button>
                                <!-- <el-button type="text" size="small" @click="pauseBtn(scope.row)">暂停</el-button>
                                <el-button type="text" size="small" @click="shutDownBtn(scope.row)">关停</el-button>
                                <el-button type="text" size="small" @click="freezeBtn(scope.row)">冻结</el-button> -->
                            </template>
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
import detail from "./components/detail.vue";
import { txAuditList, txAuditConfirm, txAuditDelete } from "@/util/api";
import clip from "@/util/clipboard";
export default {
    name: 'keywordConfig',

    components: {
        "v-content-head": contentHead,
        detail
    },

    props: {
    },

    data() {
        return {
            loading: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            txAlarmHead: [
                {
                    enName: 'keyword',
                    name: '关键字',
                    width: ""
                },
                {
                    enName: 'chainName',
                    name: '链名称',
                    width: ""
                },
                {
                    enName: 'appName',
                    name: '应用名称',
                    width: ""
                },
                {
                    enName: 'comment',
                    name: '监管意见',
                    width: ""
                },
                {
                    enName: 'status',
                    name: '状态',
                    width: ""
                },
                {
                    enName: 'txHash',
                    name: '交易Hash',
                    width: ""
                },
                {
                    enName: 'address',
                    name: '用户',
                    width: ""
                },
                {
                    enName: 'modifyTime',
                    name: '修改时间',
                    width: ""
                },
                {
                    enName: "operate",
                    name: '操作',
                    width: 210
                }
            ],
            txAlarmList: [],

        }
    },

    computed: {
    },

    watch: {
    },

    created() {
    },

    mounted() {
        this.queryTxAuditList()
    },

    methods: {
        handleSizeChange: function (val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryTxAuditList();
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.queryTxAuditList();
        },
        queryTxAuditList() {
            this.loading = true;
            let reqData = {
                pageNumber: this.currentPage,
                pageSize: this.pageSize
            }
            txAuditList(reqData, {})
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.txAlarmList = res.data.data;
                        this.total = res.data.totalCount
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
                        message: '系统错误',
                        type: "error",
                        duration: 2000
                    });
                });

        },
        deleteKeyword(val) {
            this.$confirm('确认删除?', {
                type: 'warning'
            })
                .then(_ => {
                    this.queryTxAuditDelete(val)
                })
                .catch(_ => { });
        },
        queryTxAuditDelete(val) {
            txAuditDelete(val.id)
                .then(res => {
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '删除成功'
                        });
                        this.queryTxAuditList()
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
                        message: '系统错误',
                        type: "error",
                        duration: 2000
                    });
                });
        },
        handleBtn(val) {
            this.$confirm('确认提交处理?', {
                type: 'warning'
            })
                .then(_ => {
                    this.queryTxAuditConfirm(val)
                })
                .catch(_ => { });
        },
        pauseBtn(){},
        shutDownBtn(){},
        freezeBtn(){},
        queryTxAuditConfirm(val) {
            txAuditConfirm(val.id)
                .then(res => {
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '确认成功'
                        });
                        this.queryTxAuditList()
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
                        message: '系统错误',
                        type: "error",
                        duration: 2000
                    });
                });
        },
        link(val, type) {
            switch (type) {
                case 'userAddress':
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.address,
                            chainName: val.chainName,
                            appName: val.appName
                        }
                    })
                    break;
                case 'transHash':
                    this.$router.push({
                        path: "/transactionInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            transHash: val.txHash,
                            chainName: val.chainName,
                            appName: val.appName
                        }
                    })
                    break;
                default:
                    break;
            }
        },
        statusText(key) {
            switch (key) {
                case 1:
                    return '未处理'
                    break;

                case 2:
                    return '已处理'
                    break;
            }
        },
        statusColor(key) {
            switch (key) {
                case 1:
                    return '#F56C6C'
                    break;

                case 2:
                    return '#67C23A'
                    break;
            }
        },
        btnText(key) {
            switch (key) {
                case 1:
                    return '确认'
                    break;

                case 2:
                    return ''
                    break;
            }
        },
        handleCopy(text, event) {
            clip(text, event)
        },
    }
}
</script>

<style scoped>
</style>
