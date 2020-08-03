<template>
    <div>
        <el-table :data="list" element-loading-text="Loading" fit highlight-current-row>
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
                            <template v-else>
                                <span class="item-detail-key">{{key}}：</span>
                                <span class="item-detail-val">{{val}}</span>
                            </template>
                        </template>
                    </li>
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
                    <el-button type="text" size="small" @click="deleteKeyword(scope.row,'modify')">介入处理</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog :visible.sync="interventionVisible" title="处理意见" width="433px" :append-to-body="true" :center="true" class="dialog-wrapper" v-if="interventionVisible">
            <el-form :model="interventionForm" :rules="rules" ref="interventionForm" label-width="70px" class="demo-ruleForm">
                <el-form-item label="意见" prop="content" style="width: 380px;">
                    <el-input v-model="interventionForm.content" placeholder="请输入处理意见" type="textarea"></el-input>
                </el-form-item>
            </el-form>
            <div class="dialog-footer text-right">
                <el-button @click="modelClose">取消</el-button>
                <el-button type="primary" @click="submit('interventionForm')">添加到告警列表</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import clip from "@/util/clipboard";
import head from "./head.json";
import { auditAdd } from "@/util/api"
export default {
    components: {
    },
    props: ['list', 'groupId', 'chainId', 'handleType', 'singleSearchValue'],

    data() {
        return {
            tableHead: head,
            interventionVisible: false,
            interventionForm: {
                content: ''
            },
            rowData: {}
        }
    },

    computed: {
        type() {
            return this.handleType
        },
        rules() {
            let data = {
                content: [
                    {
                        required: true,
                        message: '请输入处理意见',
                        trigger: "blur"
                    }
                ]
            }
            return data
        }
    },

    watch: {

    },

    created() {
    },

    mounted() {

    },

    methods: {
        deleteKeyword(val) {
            this.rowData = val
            this.interventionVisible = true
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
                            from: ''
                        }
                    })
                    break;
                case 'userAddress':
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.userAddress
                        }
                    })

                    break;
                case 'transHash':
                    this.$router.push({
                        path: "/transactionInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            transHash: val.transHash
                        }
                    })

                    break;
                case "blockNumber":
                    this.$router.push({
                        path: "/blockInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            blockNumber: val.blockNumber
                        }
                    })

                    break;
                case "contractAddress":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractAddress
                        }
                    })

                    break;
                case "contractName":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractName
                        }
                    })

                    break;
                default:
                    break;
            }
        },
        rowDetailLink(val, type) {
            console.log(val, type, this.chainId, this.groupId)
            return
            switch (type) {
                case 'userName':
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            userParam: val,
                            from: ''
                        }
                    })
                    break;
                case 'userAddress':
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            userParam: val
                        }
                    })

                    break;
                case 'transHash':
                    this.$router.push({
                        path: "/transactionInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            transHash: val
                        }
                    })

                    break;
                case "blockNumber":
                    this.$router.push({
                        path: "/blockInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            blockNumber: val
                        }
                    })

                    break;
                case "contractAddress":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            contractParam: val
                        }
                    })

                    break;
                case "contractName":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            contractParam: val
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
        modelClose: function () {
            this.interventionVisible = false
        },
        submit: function (formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.queryAuditAdd()
                } else {
                    return false;
                }
            });
        },
        queryAuditAdd() {
            let reqData = {
                chainId: this.rowData.chainId,
                groupId: this.rowData.groupId,
                keyword: this.singleSearchValue,
                comment: this.interventionForm.content,
                txHash: this.rowData.transHash,
                address: this.rowData.userAddress,
                chainName: this.rowData.chainName,
                appName: this.rowData.appName
            };
            auditAdd(reqData)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '介入成功'
                        });
                        this.modelClose();
                        this.$router.push({
                            path: "/alarm"
                        })
                    } else {
                        this.modelClose();
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.modelClose();
                    this.$message({
                        message: '系统错误',
                        type: "error",
                        duration: 2000
                    });
                });
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
</style>
