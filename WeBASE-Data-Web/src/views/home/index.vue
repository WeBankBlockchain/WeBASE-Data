<template>
    <div>
        <content-head :headTitle="'搜索'"></content-head>
        <div class="module-wrapper">
            <el-tabs type="border-card" @tab-click="handleClickTab" v-loading="listLoading">
                <el-tab-pane label="关键字搜索">
                    <div class="search-table">
                        <el-input v-model="singleSearchValue" @keyup.enter.native="querySimpleSearch" placeholder="请输入用户名/用户地址/合约名/合约地址/块高/交易Hash/上链数据"></el-input>
                        <el-button @click="querySimpleSearch" type="primary">搜索</el-button>
                    </div>
                    <div class="search-result" v-if="list.length > 0">
                        <search-results :list="list" :handleType="searchType" />

                        <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total">
                        </el-pagination>
                    </div>
                    <!-- <div v-else class="no-data" :style=" {'height': initHeight + 'px'}">
                        <div class="no-data-text">
                            
                        </div>
                    </div> -->
                    <div v-if="tabName == '0'">
                        <div style="padding: 10px 0 0 40px; font-weight: bold;font-size: 16px;">告警列表</div>
                        <div class="search-table">
                            <el-table :data="alarmList" tooltip-effect="dark">
                                <el-table-column v-for="head in alarmHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                                    <template slot-scope="scope">
                                        <template v-if="head.enName!='operate'">
                                            <span>{{scope.row[head.enName]}}</span>
                                        </template>
                                        <template v-else>
                                            <el-button type="text" size="small" @click="deleteKeyword(scope.row,'modify')">删除</el-button>
                                            <el-button type="text" size="small" @click="handleBtn(scope.row)">{{btnText(scope.row['statusType'])}}</el-button>
                                        </template>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="条件搜索">
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
                        <search-results :list="list" :chainId="chainId" :groupId="groupId" :handleType="searchType" />

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
import { searchAll, chainAll, groupList, simpleSearch, blockList, transList } from "@/util/api"
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
            searchType: '5',
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
            initHeight: window.innerHeight - 800,
            alarmHead: [
                {
                    enName: 'keyword',
                    name: '关键字'
                },
                {
                    enName: 'chainName',
                    name: '链名称'
                },
                {
                    enName: 'appName',
                    name: '应用名称'
                },
                {
                    enName: 'hash',
                    name: '交易Hash'
                },
                {
                    enName: 'user',
                    name: '用户'
                },
                {
                    enName: 'status',
                    name: '状态'
                },
                {
                    enName: 'modifyTime',
                    name: '修改时间'
                },
                {
                    enName: "operate",
                    name: '操作'
                }
            ],
            alarmList: [
                {
                    chainName: '存证链',
                    appName: '碳排放',
                    hash: '0x19843jsdf9834dff3',
                    user: '0x5743r4545fe3r',
                    status: '未处理',
                    statusType: '1',
                    modifyTime: '2019-03-15 11:14:29',
                    keyword: '金三胖'
                },
                {
                    chainName: '存证链',
                    appName: '产品质量码',
                    hash: '0x19843jsdf9834dff3',
                    user: '0x5743r4545fe3r',
                    status: '已处理',
                    statusType: '2',
                    modifyTime: '2019-03-14 11:14:29',
                    keyword: '金三胖'
                }
            ],
        }
    },
    mounted() {
        this.queryChainAll()
    },
    methods: {
        deleteKeyword(){
            
        },
        handleBtn(){

        },
        btnText(key){
            switch (key) {
                case '1':
                    return '确认'
                    break;
            
                case '2':
                    return ''
                    break;
            }
        },
        handleClickTab(tab) {
            this.tabName = tab.paneName
            this.searchValue = ""
            this.singleSearchValue = ""
            this.currentPage = 1
            this.pageSize = 10
            this.total = 0
            this.list = [];
            if (this.tabName == 0) {
                this.searchType = '5'
            } else if (this.tabName == 1) {
                this.searchType = '1'
            }

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
            simpleSearch(this.currentPage, this.pageSize, data)
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
            this.sureSearch()
        },
        changeSearchType(type) {
            this.list = []
            this.searchType = type;
        },
        sureSearch() {

            // this.querySearchAll()
            switch (this.searchType) {
                case "1":
                    this.getBlockList()
                    break;
                case "2":
                    this.getTransaction()
                    break;
                case "3":
                    this.querySearchAll()
                    break;

                case "4":
                    this.querySearchAll()
                    break;
            }
        },
        getBlockList: function () {
            this.listLoading = true;
            let reqData = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageNumber: this.currentPage,
                pageSize: this.pageSize
            },
                reqQuery = {};
            if (this.searchValue) {
                reqQuery.blockNumber = this.searchValue;

            }
            blockList(reqData, reqQuery)
                .then(res => {
                    this.listLoading = false;
                    if (res.data.code === 0) {
                        this.list = res.data.data;
                        this.total = res.data.totalCount;
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.listLoading = false;
                    this.$message({
                        message: '系统异常',
                        type: "error",
                        duration: 2000
                    });

                });
        },
        getTransaction() {
            this.expands = [];
            this.listLoading = true;
            let reqData = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageNumber: this.currentPage,
                pageSize: this.pageSize
            },
                reqQuery = {};
            if (this.searchValue) {
                reqQuery.transHash = this.searchValue
            }
            transList(reqData, reqQuery)
                .then(res => {
                    this.listLoading = false;
                    if (res.data.code === 0) {
                        this.list = res.data.data;
                        this.total = res.data.totalCount;
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.listLoading = false;
                    this.$message({
                        message: '系统异常',
                        type: "error",
                        duration: 2000
                    });

                });
        },

        handleSizeChange: function (val) {
            this.pageSize = val;
            this.currentPage = 1;
            if (this.tabName == 0) {
                this.querySimpleSearch()
            } else {
                this.querySearchAll();
            }

        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            if (this.tabName == 0) {
                this.querySimpleSearch()
            } else {
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