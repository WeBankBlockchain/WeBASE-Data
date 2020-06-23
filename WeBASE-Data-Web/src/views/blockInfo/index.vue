<template>
     <div class="app-container">
        <div class="search-part-right">
            <el-input placeholder="请输入交易哈希或块高" v-model="searchKey.value" class="input-with-select">
                <el-button slot="append" icon="el-icon-search"></el-button>
            </el-input>
        </div>
        <el-table :data="blockList" class="block-table-content" v-loading="loading" element-loading-text="Loading" ref="refTable">
            <el-table-column prop="blockNumber" label="块高" align="center" width="120" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>
                        {{scope.row['blockNumber']}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="transCount" label="交易" width="120" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>{{scope.row['transCount']}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="pkHash" label="区块哈希" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>{{scope.row['pkHash']}}</span>
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
import { getBlockList } from "@/api/chain1";
export default {
    name: 'BlockInfo',

    components: {
    },

    props: {
    },

    data() {
        return {
            searchKey: {
                key: '',
                value: ''
            },
            blockList: [],
            loading: true
        }
    },

    computed: {
    },

    watch: {
    },

    created() {
        this.queryBlockList()
    },

    mounted() {
    },

    methods: {
        queryBlockList (){
            this.loading = true;
            getBlockList()
                .then(response=>{
                    this.blockList = response.data.items
                    this.loading = false;
                })
        }
    }
}
</script>

<style scoped>
</style>
