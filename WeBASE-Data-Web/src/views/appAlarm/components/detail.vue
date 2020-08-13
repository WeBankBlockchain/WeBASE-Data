<template>
    <div>
        <el-tabs type="border-card" @tab-click="handleClick">
            <el-tab-pane label="交易信息">
                <el-row v-for="item in txInfoList" :key="item">
                    <el-col :xs='24' :sm="24" :md="6" :lg="4" :xl="2">
                        <span class="receipt-field">{{item}}：</span>
                    </el-col>
                    <el-col :xs='24' :sm="24" :md="18" :lg="20" :xl="22">
                        <template v-if="item==='input'">
                            <div class="detail-input-content">

                                <div v-if="inputData && inputData.length" class="input-data">
                                    <div class="input-label">
                                        <span class="label">function</span>
                                        <span>{{interfaceName}}</span>
                                    </div>
                                    <div class="input-label">
                                        <span class="label">data</span>
                                        <el-table :data="inputData" style="display:inline-block;width:400px">
                                            <el-table-column prop="name" label="name" align="left"></el-table-column>
                                            <el-table-column prop="type" label="type" align="left"></el-table-column>
                                            <el-table-column prop="data" label="data" align="left" :show-overflow-tooltip="true">
                                                <template slot-scope="scope">
                                                    <i class="wbs-icon-copy font-12 copy-public-key" @click="handleCopy(scope.row['data'], $event)" title="复制"></i>
                                                    <span>{{scope.row.data}}</span>
                                                </template>
                                            </el-table-column>
                                        </el-table>
                                    </div>
                                </div>
                                <span v-else class="input-data">{{txInfoMap[item]}}</span>
                            </div>
                        </template>
                        <template v-else-if="item==='from'">
                            <span>{{txInfoMap[item]}}</span>
                            =><span class="link" @click="link(txInfoMap[item])">{{userName}}</span>
                        </template>
                        <template v-else>
                            <span>{{txInfoMap[item]}}</span>
                        </template>
                    </el-col>
                </el-row>
            </el-tab-pane>
            <el-tab-pane label="交易回执">
                <el-row v-for="item in txReceiptInfoList" :key="item">
                    <el-col :xs='24' :sm="24" :md="6" :lg="4" :xl="2">
                        <span class="receipt-field">{{item}}：</span>
                    </el-col>
                    <el-col :xs='24' :sm="24" :md="18" :lg="20" :xl="22">
                        <template v-if="item==='logs'">
                            <div class="detail-input-content" v-if="eventLog&&eventLog.length >0" style="padding: 0 0 0 10px">

                                <div v-for="(item) in eventLog">
                                    <div class="item">
                                        <span class="label">function </span>
                                        <span>{{item.eventName}}</span>
                                    </div>
                                    <div class="item">
                                        <span class="label">Data </span>
                                        <div class="detail-input-content">
                                            <el-table class="input-data" :data="item.eventLgData" style="display:inline-block;width:100%;">
                                                <el-table-column prop="name" width="150" label="name" align="left"></el-table-column>
                                                <el-table-column prop="data" label="data" align="left" :show-overflow-tooltip="true">
                                                    <template slot-scope="scope">
                                                        <i class="wbs-icon-copy font-12 copy-public-key" @click="handleCopy(scope.row.data, $event)" title="复制"></i>
                                                        <span>{{scope.row.data}}</span>
                                                    </template>
                                                </el-table-column>
                                            </el-table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <span v-else class="input-data">{{txReceiptInfoMap['logs']}}</span>
                        </template>
                        <template v-else-if="item==='from'">
                            <span>{{txReceiptInfoMap[item]}}</span>
                            =><span class="link" @click="link(txReceiptInfoMap[item])">{{userName}}</span>
                        </template>
                        <template v-else>
                            <span>{{txReceiptInfoMap[item]}}</span>
                        </template>
                    </el-col>
                </el-row>
            </el-tab-pane>
        </el-tabs>
    </div>

</template>
<script>
import { transList, searchAll, userList } from "@/util/api";
import clip from "@/util/clipboard";
export default {
    name: 'detail',
    props: ['txData'],
    data() {
        return {
            txDetail: {},
            txInfoMap: {},
            txReceiptInfoMap: {},
            txInfoList: [
                "blockHash",
                "blockNumber",
                "gas",
                "from",
                "to",
                "hash",
                "input"
            ],
            txReceiptInfoList: [
                "output",
                "blockHash",
                "gasUsed",
                "blockNumber",
                "contractAddress",
                "from",
                "transactionIndex",
                "to",
                "logsBloom",
                "transactionHash",
                "status",
                "logs"
            ],
            chainId: '',
            groupId: '',
            interfaceName: '',
            inputData: [],
            logsName: '',
            eventLog: [],
            logsMap: [],
            userName: ''
        }
    },
    mounted() {
        this.getTransaction()
        this.querySearchAll()
        this.queryUser()
    },
    methods: {
        handleClick() {

        },
        getTransaction() {
            let reqData = {
                chainId: this.txData.chainId,
                groupId: this.txData.groupId,
                pageNumber: 1,
                pageSize: 10
            },
                reqQuery = {
                    transHash: this.txData.txHash
                };
            transList(reqData, reqQuery)
                .then(res => {
                    if (res.data.code === 0) {
                        let arr = res.data.data;
                        if (arr && arr.length) {
                            this.txDetail = arr[0]
                            this.txInfoMap = JSON.parse(arr[0].transDetail)
                            this.txReceiptInfoMap = JSON.parse(arr[0].receiptDetail)
                        };

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
        link(val) {
            this.$router.push({
                path: "/userInfo",
                query: {
                    chainId: this.txData.chainId,
                    groupId: this.txData.groupId,
                    userParam: val
                }
            });
        },
        querySearchAll() {
            var data = {
                chainId:  this.txData.chainId,
                groupId:  this.txData.groupId,
                pageSize: 10,
                pageNumber: 1,
                searchType: 2,
                transHash: this.txData.txHash
            }
            
            searchAll(data)
                .then(res => {

                    if (res.data.code === 0) {
                        this.interfaceName = res.data.data[0]['interfaceName']
                        this.inputData = JSON.parse(res.data.data[0]['input'])
                        this.logsMap = JSON.parse(res.data.data[0]['logs'])
                        this.eventLog = []
                        for (let key in this.logsMap) {
                            this.eventLog.push({
                                eventName: key,
                                eventLgData: this.logsMap[key][0]
                            })
                        }
                    } else {
                        this.$message({
                            type: 'error',
                            message: this.$chooseLang(res.data.code),
                        })
                    }
                })
                .catch(err => {
                    this.$message({
                        type: "error",
                        message: '系统异常'
                    })
                })
        },
        queryUser() {
            let reqData = {
                chainId: this.txData.chainId,
                groupId: this.txData.groupId,
                pageSize: 1,
                pageNumber: 10,

            }, reqQuery = {
                userParam: this.txReceiptInfoMap.from
            };
            
            userList(reqData, reqQuery).then(res => {
                if (res.data.code == 0) {
                    let list = res.data.data
                    if(list.length) this.userName = list[0]['userName']
                    
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
    }
}
</script>
<style scoped>
.item-detail-key {
    width: 150px;
    display: inline-block;
}
.item-datail-val-table {
    display: inline-block;
    width: 900px;
}
.label {
    display: inline-block;
    width: 65px;
    font-weight: bold;
    vertical-align: top;
    font-size: 12px;
}
.item {
    padding: 3px 0;
}
.input {
    display: inline-block;
    font-size: 0;
    vertical-align: top;
}
.input span {
    font-size: 12px;
}
.input-data {
    display: inline-block;
    width: 100%;
    padding: 10px;
    max-height: 200px;
    overflow: auto;
    word-break: break-all;
    word-wrap: break-word;
    box-sizing: border-box;
}
.input-data-from {
    display: inline-block;
    width: calc(100% - 200px);
    height: auto;
    word-break: break-all;
    word-wrap: break-word;
    box-sizing: border-box;
}
.input-label {
    padding-bottom: 15px;
}
.input-label .label {
    width: 80px;
    font-size: 12px;
}
.detail-input-content {
    display: inline-block;
    width: calc(100% - 200px);
    height: auto;
    border: 1px solid #eaedf3;
    border-radius: 4px;
    font-size: 12px;
}
.receipt-field {
    font-weight: bold;
}
.base-p {
    overflow: hidden;
    word-break: break-all;
    word-wrap: break-word;
}
</style>
