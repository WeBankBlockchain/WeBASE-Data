<template>
    <div class="class=" app-container"">
        <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" fit highlight-current-row>
            <el-table-column label="区块链名称" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span class="link-type" @click="toOverview(scope.row)">{{ scope.row.groupId }}</span>
                </template>
            </el-table-column>
            <el-table-column label="区块链编号" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span class="link-type" @click="toOverview(scope.row)">{{ scope.row.groupId | groupName }}</span>
                </template>
            </el-table-column>
            <el-table-column label="区块链类型" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>{{ scope.row.nodeCount }}</span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="created_at" label="创建时间" width="200">
                <template slot-scope="scope">
                    <i class="el-icon-time" />
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import { chainAll } from '@/api/chain'
export default {
    name: 'Chain',

    components: {
    },

    props: {
    },

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
    }
}
</script>

<style scoped>
</style>
