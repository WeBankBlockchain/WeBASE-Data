<template>
    <div>
        <content-head :headTitle="'首页'"></content-head>
        <div class="module-wrapper">
            <el-tabs type="border-card" @tab-click="handleClickTab">
                <el-tab-pane label="简单">
                    <div class="search-table">
                        <el-input v-model="singleSearchValue" @keyup.enter.native="querySimpleSearch" placeholder="请输入用户名/用户地址/合约名/合约地址/块高/交易Hash/上链数据"></el-input>
                        <el-button @click="querySimpleSearch" type="primary">搜索</el-button>
                    </div>
                    <div class="search-result" v-if="list.length > 0">
                        <search-results :list="list" />

                        <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total">
                        </el-pagination>
                    </div>
                    <div v-else class="no-data" :style=" {'height': initHeight + 'px'}">
                        <div class="no-data-text">
                            <!-- <span>暂无数据</span> -->
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="高级">
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
                        <el-input v-model.trim="searchValue" @keyup.enter.native="sureSearch"></el-input>
                        <el-button @click="sureSearch" type="primary">搜索</el-button>
                    </div>

                    <div class="search-result" v-if="list.length > 0">
                        <search-results :list="list" :chainId="chainId" :groupId="groupId"/>

                        <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total">
                        </el-pagination>
                    </div>
                    <div v-else class="no-data" :style=" {'height': initHeight + 'px'}">
                        <div class="no-data-text">
                            <!-- <span>暂无数据</span> -->
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>

    </div>
</template>

<script>
import { searchAll, chainAll, groupList, simpleSearch } from "@/util/api"
import contentHead from "@/components/contentHead";
import SearchResults from "./components/SearchResults";
export default {
    name: 'home',
    components: {
        contentHead,
        SearchResults
    },
    data() {
        return {
            list: [],
            listLoading: false,
            labelName: '',
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
            singleSearchValue: '',
            chainId: '',
            groupId: '',
            chainList: [],
            groupCollection: [],
            currentPage: 1,
            pageSize: 10,
            total: 0,
            tabName: '0',
            initHeight: window.innerHeight - 200
        }
    },
    mounted() {
        this.queryChainAll()
    },
    methods: {
        handleClickTab(tab) {
            this.tabName = tab.paneName
            this.searchValue = ""
            this.singleSearchValue = ""
            this.currentPage = 1
            this.pageSize = 10
            this.total = 0
            this.list = [];
        },
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
                        if (this.chainList.length) {
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
                .catch(err => {
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
                        if (this.groupCollection.length) {
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
            this.listLoading = true;
            var data = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageSize: this.pageSize,
                pageNumber: this.currentPage,
            }
            if (this.tabName == '0') {

            } else if (this.tabName == '1') {
                data.searchType = this.searchType
                switch (this.searchType) {
                    case "1":
                        data.blockParam = this.searchValue
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
            }


            searchAll(data)
                .then(res => {

                    if (res.data.code === 0) {
                        this.list = res.data.data;
                        this.total = res.data.totalCount
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
        querySimpleSearch() {
            this.listLoading = true;
            var data = {
                keyword: this.singleSearchValue
            }
            simpleSearch(this.currentPage,this.pageSize,data)
                .then(res => {
                    if (res.data.code === 0) {
                        this.list = res.data.data;
                        this.total = res.data.totalCount
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
        handleSizeChange: function (val) {
            this.pageSize = val;
            this.currentPage = 1;
            if(this.tabName == 0){
                this.querySimpleSearch()
            }else {
                this.querySearchAll();
            }
            
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            if(this.tabName == 0){
                this.querySimpleSearch()
            }else {
                this.querySearchAll();
            }
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
    /* margin-top: 100px; */
}
.search-result {
    padding: 30px 29px 0 30px;
}
.no-data {
    min-height: 500px;
}
.no-data-text {
    height: 100%;
    width: 100%;
    margin: auto;
}
</style>