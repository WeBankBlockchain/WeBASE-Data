<template>
    <el-table :data="list" element-loading-text="Loading" fit highlight-current-row>
        <el-table-column v-for="head in tableHead[type]" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
            <template slot-scope="scope">
                <template v-if="head.enName=='contractAddress'">
                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['contractAddress'], $event)" title="复制"></i>
                    <span class="link" @click="link(scope.row, 'contractAddress')">{{ scope.row.contractAddress }}</span>
                </template>
                <template v-else-if="head.enName=='contractName'">
                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['contractName'], $event)" title="复制"></i>
                    <span class="link" @click="link(scope.row, 'contractName')">{{ scope.row.contractName }}</span>
                </template>
                <template v-else-if="head.enName=='userName'">
                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['userName'], $event)" title="复制"></i>
                    <span class="link" @click="link(scope.row, 'userName')">{{ scope.row.userName }}</span>
                </template>
                <template v-else-if="head.enName=='userAddress'">
                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['userAddress'], $event)" title="复制"></i>
                    <span class="link" @click="link(scope.row, 'userAddress')">{{ scope.row.userAddress }}</span>
                </template>
                <template v-else-if="head.enName=='transHash'">
                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['transHash'], $event)" title="复制"></i>
                    <span class="link" @click="link(scope.row, 'transHash')">{{ scope.row.transHash }}</span>
                </template>
                <template v-else-if="head.enName=='blockNumber'">
                    <i class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row['blockNumber'], $event)" title="复制"></i>
                    <span class="link" @click="link(scope.row, 'blockNumber')">{{ scope.row.blockNumber }}</span>
                </template>
                <template v-else>
                    <i  v-if="scope.row[head.enName]" class="wbs-icon-copy font-12 copy-key" @click="handleCopy(scope.row[head.enName], $event)" title="复制"></i>
                    {{ scope.row[head.enName] }}
                </template>
                
            </template>
        </el-table-column>

    </el-table>
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
        link(val, type) {
            switch (type) {
                case 'userName':
                    var { href } = this.$router.resolve({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.userName
                        }
                    })
                    window.open(href, '_blank')
                    break;
                case 'userAddress':
                    var { href } = this.$router.resolve({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.userAddress
                        }
                    })
                    window.open(href, '_blank')
                    break;
                case 'transHash':
                    var { href } = this.$router.resolve({
                        path: "/transactionInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            transHash: val.transHash
                        }
                    })
                    window.open(href, '_blank')
                    break;
                case "blockNumber":
                    var { href } = this.$router.resolve({
                        path: "/blockInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            blockNumber: val.blockNumber
                        }
                    })
                    window.open(href, '_blank')
                    break;
                case "contractAddress":
                    var { href } = this.$router.resolve({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractAddress
                        }
                    })
                    window.open(href, '_blank')
                    break;
                case "contractName":
                    var { href } = this.$router.resolve({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractName
                        }
                    })
                    window.open(href, '_blank')
                    break;
                default:
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
