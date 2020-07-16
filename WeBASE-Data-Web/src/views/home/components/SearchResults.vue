<template>
    <el-table :data="list" element-loading-text="Loading" fit highlight-current-row>
        <el-table-column prop="contractAddress" label="合约地址" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                <span class="link" @click="link(scope.row, 'contractAddress')">{{ scope.row.contractAddress }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="contractName" label="合约名称" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                <span class="link" @click="link(scope.row, 'contractName')">{{ scope.row.contractName }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                <span class="link" @click="link(scope.row, 'userName')">{{ scope.row.userName }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="userAddress" label="用户地址" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                <span class="link" @click="link(scope.row, 'userAddress')">{{ scope.row.userAddress }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="transHash" label="交易哈希" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                <span class="link" @click="link(scope.row, 'transHash')">{{ scope.row.transHash }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="blockNumber" label="块高" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                <span class="link" @click="link(scope.row, 'blockNumber')">{{ scope.row.blockNumber }}</span>

            </template>
        </el-table-column>
        <el-table-column prop="input" label="Input" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                {{ scope.row.input }}
            </template>
        </el-table-column>
        <el-table-column prop="output" label="Output" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                {{ scope.row.output }}
            </template>
        </el-table-column>
        <el-table-column prop="logs" label="Event" width="" align="center" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                {{ scope.row.logs }}
            </template>
        </el-table-column>
        <el-table-column prop="blockTimestamp" align="center" label="出块时间" width="200">
            <template slot-scope="scope">
                <i class="el-icon-time" />
                <span>{{ scope.row.blockTimestamp }}</span>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
export default {
    components: {
    },
    props: ['list', 'groupId', 'chainId'],

    data() {
        return {
        }
    },

    computed: {
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
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.userName
                        }
                    });
                    break;
                case 'userAddress':
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            userParam: val.userAddress
                        }
                    });
                    break;
                case 'transHash':
                    this.$router.push({
                        path: "/transactionInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            transHash: val.transHash
                        }
                    });
                    break;
                case "blockNumber":
                    this.$router.push({
                        path: "/blockInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            blockNumber: val.blockNumber
                        }
                    });
                    break;
                case "contractAddress":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractAddress
                        }
                    });
                    break;
                case "contractName":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: val.chainId || this.chainId,
                            groupId: val.groupId || this.groupId,
                            contractParam: val.contractName
                        }
                    });
                    break;
                default:
                    break;
            }
        },
    }
}
</script>

<style scoped>
</style>
