<template>
    <div class="app-container">
        <div class="search">
            <el-select v-model="chainId" placeholder="请选择链" @change="changeChain">
                <el-option v-for="item in chainList" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-select v-model="groupId" placeholder="请选择群组" @change="changeChain">
                <el-option v-for="item in groupCollection" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-select v-model="searchType" placeholder="请选择" slot="prepend" @change="changeSearchType">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <el-input v-model.trim="searchValue"></el-input>
            <el-button @click="sureSearch">搜索</el-button>
        </div>
        <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" fit highlight-current-row v-if="list">
            <el-table-column label="合约名称" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>{{ scope.row.contractName }}</span>
                </template>
            </el-table-column>
            <el-table-column label="用户" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>{{ scope.row.address }}</span>
                </template>
            </el-table-column>
            <el-table-column label="交易哈希" width="" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <span>{{ scope.row.txHash }}</span>
                </template>
            </el-table-column>
            <el-table-column label="快高" width="" align="center">
                <template slot-scope="scope">
                    {{ scope.row.blockNumber }}
                </template>
            </el-table-column>
            <el-table-column label="Input" width="" align="center">
                <template slot-scope="scope">
                    {{ scope.row.input }}
                </template>
            </el-table-column>
            <el-table-column label="Output" width="" align="center">
                <template slot-scope="scope">
                    {{ scope.row.output }}
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
import { searchAll } from '@/api/search'
import { chainAll } from '@/api/chain'
import { groupList } from '@/api/group'
export default {
    filters: {
        statusFilter(status) {
            const statusMap = {
                published: 'success',
                draft: 'gray',
                deleted: 'danger'
            }
            return statusMap[status]
        }
    },
    data() {
        return {
            list: null,
            listLoading: true,
            options: [{
                value: '1',
                label: '区块'
            }, {
                value: '2',
                label: '交易'
            }, {
                value: '3',
                label: '用户'
            }, {
                value: '4',
                label: '合约'
            }],
            searchType: '',
            searchValue: '',
            chainId: '',
            groupId: '',
            chainList: [],
            groupCollection: []
        }
    },
    mounted() {
        this.queryChainAll()
    },
    methods: {
        queryChainAll() {
            chainAll()
                .then(res => {
                    const { code, data, message } = res;
                    if (code === 0) {
                        this.chainList = data;
                        this.chainList.forEach(chain => {
                            chain.label = chain.chainName
                            chain.value = chain.chainId
                        })
                    } else {
                        this.$message({
                            type: 'error',
                            message: 'xxx'
                        })
                    }
                })
        },
        queryGroupList(val) {

            groupList(val)
                .then(res => {
                    const { code, data, message } = res;
                    if (code === 0) {
                        this.groupCollection = data
                        this.groupCollection.forEach(group => {
                            group.label = group.groupName;
                            group.value = group.id;
                        })
                    } else {
                        this.$message({
                            type: 'error',
                            message: message
                        })
                    }
                })
        },
        querySearchAll() {
            this.listLoading = true;
            var data = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageSize: 10,
                pageNumber: 1,
                searchType: this.searchType
            }
            switch (this.searchType) {
                case "1":
                    data.blockNumber = this.searchValue
                    break;

                case "2":
                    data.transHash = this.searchValue
                    break;
                case "3":
                    data.userParam = this.searchValue
                    break;

                case "4":
                    data.contractParam = this.searchValue
                    break;
            }
            searchAll(data)
                .then(res => {
                    const { code, data, message } = res;
                    console.log(res)
                    if (code === 0) {
                        this.list = data;
                    } else {
                        this.$message({
                            type: 'error',
                            message: message
                        })
                    }
                    this.listLoading = false
                })
                .catch(err => {
                    this.$message({
                        type: 'error',
                        message: err
                    })
                    this.listLoading = false
                })
        },
        changeChain(val) {
            this.chainId = val
            this.queryGroupList(val)
        },
        changeGroup(val) {
            this.groupId = val
        },
        changeSearchType(type) {
            this.searchType = type;
        },
        sureSearch() {
            // if (!this.chainId || !this.groupId || this.searchType || this.searchValue) return
            this.querySearchAll()
        }
    }
}
</script>


<style lang="scss" scoped>
.app-container {
    .search {
        display: flex;
    }
}
.search-input-with-type >>> .el-input__inner {
    width: 140px;
}
</style>