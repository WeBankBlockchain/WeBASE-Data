<template>
    <div>
        <content-head :headTitle="'首页'"></content-head>
        <div class="module-wrapper">
            <div class="search-table">
                <el-select v-model="chainId" placeholder="请选择链" @change="changeChain">
                    <el-option v-for="item in chainList" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
                <el-select v-model="groupId" placeholder="请选择群组" @change="changeGroup">
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
            
            <div class="search-result">
                <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" fit highlight-current-row>
                    <el-table-column prop="contractAddress" label="合约地址" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span>{{ scope.row.contractAddress }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="contractName" label="合约名称" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span>{{ scope.row.contractName }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userName" label="用户" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span>{{ scope.row.userName }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userAddress" label="用户地址" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span>{{ scope.row.userAddress }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="transHash" label="交易哈希" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            <span>{{ scope.row.transHash }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="blockNumber" label="快高" width="" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            {{ scope.row.blockNumber }}
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
                    <el-table-column prop="blockTimestamp" align="center" label="出块时间" width="200">
                        <template slot-scope="scope">
                            <i class="el-icon-time" />
                            <span>{{ scope.row.blockTimestamp }}</span>
                        </template>
                    </el-table-column>
                </el-table>
                
                <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total">
                </el-pagination>
            </div>

        </div>

    </div>
</template>

<script>
import { searchAll, chainAll, groupList } from "@/util/api"
import contentHead from "@/components/contentHead";
export default {
    components: {
        contentHead
    },
    data() {
        return {
            list: [],
            listLoading: false,
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
            searchType: '1',
            searchValue: '',
            chainId: '',
            groupId: '',
            chainList: [],
            groupCollection: [],
            currentPage: 1,
            pageSize: 10,
            total: 0,
        }
    },
    mounted() {
        this.queryChainAll()
    },
    methods: {
        queryChainAll() {
            this.listLoading = true;
            chainAll()
                .then(res => {
                    this.listLoading = false
                    if (res.data.code === 0) {
                        this.chainList = res.data.data;
                        this.chainList.forEach(chain => {
                            chain.label = chain.chainName
                            chain.value = chain.chainId
                        })
                        if(this.chainList.length) {
                            this.chainId = this.chainList[0]['value']
                            this.queryGroupList(this.chainId)
                        }
                        
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err=>{
                    this.listLoading = false
                    this.$message({
                        type: "error",
                        message: '系统异常'
                    })
                })
        },
        queryGroupList(val) {
            groupList(val)
                .then(res => {
                    if (res.data.code === 0) {
                        this.groupCollection = res.data.data
                        this.groupCollection.forEach(group => {
                            group.label = group.groupName;
                            group.value = group.groupId;
                        })
                        if(this.groupCollection.length) {
                            this.groupId = this.groupCollection[0]['value']
                        }
                    } else {
                        this.$message({
                            type: 'error',
                            message: this.$chooseLang(res.data.code),
                        })
                    }
                })
        },
        querySearchAll() {
            if(!this.searchValue) return;
            this.listLoading = true;
            var data = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageSize: this.pageSize,
                pageNumber: this.currentPage,
                searchType: this.searchType
            }
            switch (this.searchType) {
                case "1":
                    if(!!+this.searchValue) {
                        data.blockNumber = +this.searchValue
                    }else {
                        this.$message({
                            type: 'warning',
                            message: '请输入数字'
                        })
                        this.listLoading = false;
                        return
                    }
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
                    
                    if (res.data.code === 0) {
                        this.list = res.data.data;
                    } else {
                        this.$message({
                            type: 'error',
                            message: this.$chooseLang(res.data.code),
                        })
                    }
                    this.listLoading = false
                })
                .catch(err => {
                    this.$message({
                        type: "error",
                        message: '系统异常'
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
            this.querySearchAll()
        },
        handleSizeChange: function(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.querySearchAll();
        },
        handleCurrentChange: function(val) {
            this.currentPage = val;
            this.querySearchAll();
        },
    }
}
</script>


<style scoped>
.home-wrapper {
    margin: 15px 15px 0 15px;
}
.search-table {
    padding-top: 10px;
    display: flex;
}
.search-result {
    padding-top: 30px;
}
</style>