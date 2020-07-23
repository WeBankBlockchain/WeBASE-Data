<template>
    <div>
        <el-table :data="list" element-loading-text="Loading" fit highlight-current-row>
            <el-table-column v-for="head in tableHead[type]" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                <template slot-scope="scope">
                    <template v-if="head.enName=='contractAddress'">
                        <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['contractAddress'], $event)" title="复制"></i>
                        <span class="link link-item" @click="link(scope.row, 'contractAddress')">{{ scope.row.contractAddress }}</span>
                    </template>
                    <template v-else-if="head.enName=='contractName'">
                        <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['contractName'], $event)" title="复制"></i>
                        <span class="link link-item" @click="link(scope.row, 'contractName')">{{ scope.row.contractName }}</span>
                    </template>
                    <template v-else-if="head.enName=='userName'">
                        <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['userName'], $event)" title="复制"></i>
                        <span class="link link-item" @click="link(scope.row, 'userName')">{{ scope.row.userName }}</span>
                    </template>
                    <template v-else-if="head.enName=='userAddress'">
                        <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['userAddress'], $event)" title="复制"></i>
                        <span class="link link-item" @click="link(scope.row, 'userAddress')">{{ scope.row.userAddress }}</span>
                    </template>
                    <template v-else-if="head.enName=='transHash'">
                        <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['transHash'], $event)" title="复制"></i>
                        <span class="link link-item" @click="link(scope.row, 'transHash')">{{ scope.row.transHash }}</span>
                    </template>
                    <template v-else-if="head.enName=='blockNumber'">
                        <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['blockNumber'], $event)" title="复制"></i>
                        <span class="link link-item" @click="link(scope.row, 'blockNumber')">{{ scope.row.blockNumber }}</span>
                    </template>
                    <template v-else-if="head.enName=='chainId'">
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
                    <el-button type="text" size="small" @click="handleBtn(scope.row)">{{btnText(scope.row['statusType'])}}</el-button>
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
export default {
    components: {
    },
    props: ['list', 'groupId', 'chainId', 'handleType'],

    data() {
        return {
            tableHead: head,
            interventionVisible: false,
            interventionForm: {
                content: ''
            }
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
        deleteKeyword() {
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
                    // var { href } = this.$router.resolve({
                    //     path: "/userInfo",
                    //     query: {
                    //         chainId: val.chainId || this.chainId,
                    //         groupId: val.groupId || this.groupId,
                    //         userParam: val.userName
                    //     }
                    // })
                    // window.open(href, '_blank')
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
                    // var { href } = this.$router.resolve({
                    //     path: "/userInfo",
                    //     query: {
                    //         chainId: val.chainId || this.chainId,
                    //         groupId: val.groupId || this.groupId,
                    //         userParam: val.userAddress
                    //     }
                    // })
                    // window.open(href, '_blank')
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
                    // var { href } = this.$router.resolve({
                    //     path: "/transactionInfo",
                    //     query: {
                    //         chainId: val.chainId || this.chainId,
                    //         groupId: val.groupId || this.groupId,
                    //         transHash: val.transHash
                    //     }
                    // })
                    // window.open(href, '_blank')
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
                    // var { href } = this.$router.resolve({
                    //     path: "/blockInfo",
                    //     query: {
                    //         chainId: val.chainId || this.chainId,
                    //         groupId: val.groupId || this.groupId,
                    //         blockNumber: val.blockNumber
                    //     }
                    // })
                    // window.open(href, '_blank')
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
                    // var { href } = this.$router.resolve({
                    //     path: "/contractInfo",
                    //     query: {
                    //         chainId: val.chainId || this.chainId,
                    //         groupId: val.groupId || this.groupId,
                    //         contractParam: val.contractAddress
                    //     }
                    // })
                    // window.open(href, '_blank')
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
                    // var { href } = this.$router.resolve({
                    //     path: "/contractInfo",
                    //     query: {
                    //         chainId: val.chainId || this.chainId,
                    //         groupId: val.groupId || this.groupId,
                    //         contractParam: val.contractName
                    //     }
                    // })
                    // window.open(href, '_blank')
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
                    // this.modelClose()
                    console.log(this.chainId,this.groupId)
                    this.$router.push({
                        path: "/alarm",
                        query: {
                            
                            content: this.interventionForm.content
                        }
                    })
                } else {
                    return false;
                }
            });
        },
    }
}
</script>

<style scoped>
.link-item {
    padding: 5px 10px 5px 10px;
}
</style>
