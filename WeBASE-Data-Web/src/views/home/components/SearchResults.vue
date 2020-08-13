<template>
    <div>
        <el-table :data="list" element-loading-text="Loading" fit highlight-current-row @expand-change="exChange">
            <el-table-column type="expand" v-if="handleType =='5'">
                <template slot-scope="scope">
                    <li v-for="(val, key) in (scope.row)">
                        <template v-if=" key !='createTime'&&key !='modifyTime'&&key !='rawData'&&key !='groupId'&&key !='chainId'&&key !='dynamicTableName'&&key !='id' ">
                            <template v-if="key=='transHash'">
                                <span class="item-detail-key">txHash：</span>
                                <span class="item-detail-val">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(val, $event)" title="复制"></i>
                                    <span class="link link-item" @click="link(scope.row, 'transHash')">
                                        {{val}}
                                    </span>
                                </span>
                            </template>
                            <template v-else-if="key=='blockNumber'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(val, $event)" title="复制"></i>
                                    <span class="link link-item" @click="link(scope.row, 'blockNumber')">
                                        {{val}}
                                    </span>
                                </span>
                            </template>
                            <template v-else-if="key=='userAddress'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(val, $event)" title="复制"></i>
                                    <span class="link link-item" @click="link(scope.row, 'userAddress')">
                                        {{val}}
                                    </span>
                                </span>
                            </template>
                            <template v-else-if="key=='contractAddress'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(val, $event)" title="复制"></i>
                                    <span class="link link-item" @click="link(scope.row, 'contractAddress')">
                                        {{val}}
                                    </span>
                                </span>
                            </template>
                            <template v-else-if="key=='contractName'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(val, $event)" title="复制"></i>
                                    <span class="link link-item" @click="link(scope.row, 'contractName')">
                                        {{val}}
                                    </span>
                                </span>
                            </template>
                            <template v-else-if="key=='userName'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(val, $event)" title="复制"></i>
                                    <span class="link link-item" @click="link(scope.row, 'userName')">
                                        {{val}}
                                    </span>
                                </span>
                            </template>
                            <template v-else-if="key=='userAddress'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">
                                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(val, $event)" title="复制"></i>
                                    <span class="link link-item" @click="link(scope.row, 'userAddress')">
                                        {{val}}
                                    </span>
                                </span>
                            </template>
                            <template v-else-if="key=='input'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-datail-val-table">
                                    <el-table :data="JSON.parse(val)" tooltip-effect="dark" :border="true">
                                        <el-table-column prop="name" label="name" show-overflow-tooltip width="200" align="center">
                                            <template slot-scope="scope">
                                                <span>{{scope.row.name}}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column prop="type" label="type" show-overflow-tooltip width="" align="center">
                                            <template slot-scope="scope">
                                                <span>{{scope.row.type}}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column prop="data" label="data" show-overflow-tooltip width="" align="center">
                                            <template slot-scope="scope">
                                                <span>{{scope.row.data}}</span>
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                </span>
                            </template>
                            <template v-else-if="key=='output'">
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-datail-val-table">
                                    <el-table :data="JSON.parse(val)" tooltip-effect="dark" :border="true">
                                        <el-table-column prop="name" label="name" show-overflow-tooltip width="200" align="center">
                                            <template slot-scope="scope">
                                                <span>{{scope.row.name}}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column prop="type" label="type" show-overflow-tooltip width="" align="center">
                                            <template slot-scope="scope">
                                                <span>{{scope.row.type}}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column prop="data" label="data" show-overflow-tooltip width="" align="center">
                                            <template slot-scope="scope">
                                                <span>{{scope.row.data}}</span>
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                </span>
                            </template>
                            <template v-else>
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">{{val}}</span>
                            </template>
                        </template>
                    </li>
                </template>
            </el-table-column>
            <el-table-column type="expand" align="center" v-if="type==1">
                <template slot-scope="scope">
                    <block-detail :blockData="JSON.parse(scope.row.blockDetail)"></block-detail>
                </template>
            </el-table-column>
            <el-table-column type="expand" align="center" v-if="type==2">
                <template slot-scope="scope">
                    <transaction-detail :txData="txData"></transaction-detail>
                </template>
            </el-table-column>
            <el-table-column v-for="head in tableHead[type]" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                <template slot-scope="scope">
                    <template v-if="head.enName=='chainId'">
                        <span>{{ scope.row.chainId }}</span>
                    </template>
                    <template v-else-if="head.enName=='groupId'">
                        <span>{{ scope.row.groupId }}</span>
                    </template>
                    <template v-else-if="head.enName=='transCount'">
                        <span>{{ scope.row.transCount }}</span>
                    </template>
                    <template v-else-if="head.enName=='chainName'">
                        <span>{{ scope.row.chainName }}</span>
                    </template>
                    <template v-else-if="head.enName=='appName'">
                        <span>{{ scope.row.appName }}</span>
                    </template>
                    <template v-else>
                        <i v-if="scope.row[head.enName]" class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row[head.enName], $event)" title="复制"></i>
                        {{ scope.row[head.enName] }}
                    </template>

                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" v-if="type=='5'">
                <template slot-scope="scope">
                    <el-button type="text" size="small" @click="interveneKeyword(scope.row,'modify')">介入处理</el-button>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" v-if="type=='2'">
                <template slot-scope="scope">
                    <el-button type="text" size="small" @click="interveneKeyword(scope.row,'modify')">介入处理</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog :visible.sync="interventionVisible" title="处理意见" width="433px" :append-to-body="true" :center="true" class="dialog-wrapper" v-if="interventionVisible&&type==1">
            <intervene :rowData="rowData" :singleSearchValue="singleSearchValue" type="1" @modelClose="modelClose"></intervene>
        </el-dialog>
        <el-dialog :visible.sync="interventionVisible" title="处理意见" width="433px" :append-to-body="true" :center="true" class="dialog-wrapper" v-if="interventionVisible&&type==2">
            <intervene :rowData="rowData" type="2" @modelClose="modelClose"></intervene>
        </el-dialog>
    </div>
</template>

<script>
import clip from "@/util/clipboard";
import head from "./head.json";
import Intervene from "@/components/Intervene";
import TransactionDetail from "@/components/TransactionDetail/index.vue";
import BlockDetail from "@/components/BlockDetail";
export default {
    components: {
        Intervene,
        TransactionDetail,
        BlockDetail
    },
    props: ['list', 'groupId', 'chainId', 'handleType', 'singleSearchValue'],

    data() {
        return {
            tableHead: head,
            interventionVisible: false,
            rowData: {},
            txData: {}
        }
    },

    computed: {
        type() {
            return this.handleType
        }
    },

    watch: {

    },

    created() {
    },

    mounted() {

    },

    methods: {
        interveneKeyword(val) {
            if (this.type == 1) {
                this.rowData = val
                this.interventionVisible = true
            } else if (this.type == 2) {
                this.rowData.userAddress = JSON.parse(val.receiptDetail).from;
                this.rowData.transHash = val.transHash;
                this.rowData.chainId = this.chainId
                this.rowData.groupId = this.groupId
                this.rowData.chainName = this.chainName
                this.rowData.appName = this.appName
                this.interventionVisible = true
            }

        },
        modelClose() {
            this.interventionVisible = false
        },
        handleBtn() {

        },
        btnText(key) {
            switch (key) {
                case '1':
                    return '确认'
                    break;

                case '2':
                    return ''
                    break;
            }
        },
        link(val, type) {
            switch (type) {
                case 'userName':
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.userName,
                            chainName: val.chainName,
                            appName: val.appName
                        }
                    })
                    break;
                case 'userAddress':
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.userAddress,
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
                            transHash: val.transHash,
                            chainName: val.chainName,
                            appName: val.appName
                        }
                    })

                    break;
                case "blockNumber":
                    this.$router.push({
                        path: "/blockInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            blockNumber: val.blockNumber,
                            chainName: val.chainName,
                            appName: val.appName
                        }
                    })

                    break;
                case "contractAddress":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractAddress,
                            chainName: val.chainName,
                            appName: val.appName
                        }
                    })

                    break;
                case "contractName":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractName,
                            chainName: val.chainName,
                            appName: val.appName
                        }
                    })

                    break;
                default:
                    break;
            }
        },
        handleCopy(text, event) {
            clip(text, event)
        },
        exChange(row) {
            this.txData = Object.assign(row, {
                chainId: this.chainId,
                groupId: this.groupId
            })
        }
    }
}
</script>

<style scoped>
.link-item {
    padding: 5px 10px 5px 10px;
}
.link-item {
    padding: 5px 10px 5px 10px;
}
.item-detail-key {
    width: 150px;
    display: inline-block;
}
.item-detail-val {
}
.item-datail-val-table {
    display: inline-block;
    width: 900px;
}
</style>
