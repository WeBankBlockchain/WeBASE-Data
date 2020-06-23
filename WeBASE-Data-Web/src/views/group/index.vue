<template>
    <div class="app-container">
        <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" fit highlight-current-row>
            <el-table-column label="群组编号" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span class="link-type" @click="toOverview(scope.row)">{{ scope.row.groupId }}</span>
                </template>
            </el-table-column>
            <el-table-column label="群组名称" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span class="link-type" @click="toOverview(scope.row)">{{ scope.row.groupId | groupName }}</span>
                </template>
            </el-table-column>
            <el-table-column label="节点数量" width="" align="center" :show-overflow-tooltip="true">
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
import { getGroupList } from "@/api/chain1";
export default {
    filters: {
        statusFilter(status) {
            const statusMap = {
                published: 'success',
                draft: 'gray',
                deleted: 'danger'
            }
            return statusMap[status]
        },
        type(val) {
            if (val) {
                return "sm2/sm3"
            } else {
                return "secp256k1/sha3"
            }
        },
        groupName(val) {
            return `group${val}`
        }
    },
    data() {
        return {
            list: null,
            listLoading: true
        }
    },
    created() {
        
    },
    methods: {
        fetchData() {
            this.listLoading = true
            getGroupList().then(response => {
                this.list = response.data.items
                this.listLoading = false
            })
        },
        toOverview(row) {
            this.$router.push({
                path: '/overview',
                query: {
                    groupId: row.groupId,
                    groupName: row.groupName
                }
            })
        }
    }
}
</script>

<style scoped>
</style>
