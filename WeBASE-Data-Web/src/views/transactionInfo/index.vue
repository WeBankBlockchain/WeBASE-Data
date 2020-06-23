<template>
    <div class="app-container">
        <div class="search-part-right">
            <el-input placeholder="请输入交易哈希或块高" v-model="searchKey.value" class="input-with-select">
                <el-button slot="append" icon="el-icon-search"></el-button>
            </el-input>
        </div>
        <el-table :data="transactionList" v-loading="loading" element-loading-text="Loading" class="block-table-content" ref="refTable">
            <el-table-column prop="transHash" label="交易哈希" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>
                        {{scope.row['transHash']}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="blockNumber" label="块高" width="260" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>{{scope.row['blockNumber']}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="blockTimestamp" label="创建时间" width="280" :show-overflow-tooltip="true" align="center">
                <template slot-scope="scope">
                    <i class="el-icon-time" />
                    <span>{{scope.row['blockTimestamp']}}</span>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import { getTransList } from "@/api/chain1";
export default {
    name: 'transactionInfo',

    components: {
    },

    props: {
    },

    data() {
        return {
            transactionList: [],
            loading: true,
            searchKey: {
                key: '',
                value: ''
            }
        }
    },

    computed: {
    },

    watch: {
    },

    created() {
        this.queryTxInfo()
    },

    mounted() {
    },

    methods: {
        queryTxInfo() {
            this.loading = true;
            getTransList()
                .then(response => {
                    this.transactionList = response.data.items
                    this.loading = false;
                })
        }
    }
}
</script>

<style scoped>
.search-part-right {
    padding: 10px 0;
}
</style>
