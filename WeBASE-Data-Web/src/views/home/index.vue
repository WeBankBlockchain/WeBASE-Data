<template>
    <div>
        <content-head :headTitle="'搜索'"></content-head>
        <div style="margin: 15px 15px 0 15px;">
            <el-row :gutter="10">
                <el-col :xs="8" :sm="6" :md="4" :lg="4" :xl="4" v-for="item in detailsList" :key='item.label'>
                    <div class="overview-item" :class="item.bg">
                        <div class="overview-item-img">
                            <svg class="overview-item-img" aria-hidden="true" v-if='item.icon == "#wbs-icon-node1"'>
                                <use xlink:href="#wbs-icon-node1"></use>
                            </svg>
                            <svg class="overview-item-img" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-contract"'>
                                <use xlink:href="#wbs-icon-contract"></use>
                            </svg>
                            <svg class="overview-item-img" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-block"'>
                                <use xlink:href="#wbs-icon-block"></use>
                            </svg>
                            <svg class="overview-item-img" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-transation"'>
                                <use xlink:href="#wbs-icon-transation"></use>
                            </svg>
                            <svg class="overview-item-img" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-user-icon"'>
                                <use xlink:href="#wbs-icon-user-icon"></use>
                            </svg>
                            <svg class="overview-item-img" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-h-group"'>
                                <use xlink:href="#wbs-icon-h-group"></use>
                            </svg>
                        </div>
                        <div class="overview-item-content">
                            <div class="overview-item-number">{{item.value}}</div>
                            <div class="overview-item-title">{{item.label}}</div>
                        </div>
                    </div>
                </el-col>

            </el-row>
        </div>
        <div class="module-wrapper">
            <el-tabs type="border-card" @tab-click="handleClickTab" :value="tabName" v-loading="listLoading">
                <el-tab-pane label="关键字搜索">
                    <div class="search-table">
                        <el-input v-model="singleSearchValue" @keyup.enter.native="querySimpleSearch" placeholder="请输入用户名/用户地址/合约名/合约地址/块高/交易Hash/上链数据"></el-input>
                        <el-button @click="querySimpleSearch" type="primary">搜索</el-button>
                    </div>
                    <div class="search-result" v-if="list.length > 0">
                        <search-results :list="list" :singleSearchValue='singleSearchValue' :handleType="searchType" />

                        <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total">
                        </el-pagination>
                    </div>
                    <div v-else class="no-data" :style=" {'height': initHeight + 'px'}">
                        <div class="no-data-text">

                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="条件搜索">
                    <div class="search-table">
                        <el-select v-model="chainId" placeholder="请选择链" @change="changeChain">
                            <el-option v-for="item in chainList" :key="item.value" :label="item.label" :value="item.value">
                            </el-option>
                        </el-select>
                        <el-select v-model="groupId" placeholder="请选择应用" @change="changeGroup">
                            <el-option v-for="item in groupCollection" :key="item.value" :label="item.appName" :value="item.value">
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
import { searchAll, chainAll, groupList, simpleSearch, blockList, transList, chainGeneral } from "@/util/api"
import contentHead from "@/components/contentHead";
import SearchResults from "./components/SearchResults";
import { rexIsNumber } from "@/util/util";
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
            searchType: sessionStorage.getItem('searchType') ? sessionStorage.getItem('searchType') : '',
            searchValue: sessionStorage.getItem('searchValue') ? sessionStorage.getItem('searchValue') : '',
            singleSearchValue: sessionStorage.getItem('simpleKeyword') ? sessionStorage.getItem('simpleKeyword') : '',
            chainId: sessionStorage.getItem('chainId') ? sessionStorage.getItem('chainId') : '',
            groupId: sessionStorage.getItem('groupId') ? sessionStorage.getItem('groupId') : '',
            chainList: [],
            groupCollection: [],
            currentPage: 1,
            pageSize: 10,
            total: 0,
            tabName: sessionStorage.getItem('tab') ? sessionStorage.getItem('tab') : '0',
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
                    keyword: '新冠病毒'
                },
                {
                    chainName: '存证链',
                    appName: '产品质量码',
                    hash: '0x19843jsdf9834dff3',
                    user: '0x5743r4545fe3r',
                    status: '已处理',
                    statusType: '2',
                    modifyTime: '2019-03-14 11:14:29',
                    keyword: '新冠病毒'
                }
            ],
            rexIsNumber: rexIsNumber,
            loadingNumber: false,
            detailsList: [
                {
                    label: "链数量",
                    name: "chainCount",
                    value: 0,
                    icon: "#wbs-icon-node1",
                    bg: 'chain-bg'
                },
                {
                    label: "应用数量",
                    name: "groupCount",
                    value: 0,
                    icon: "#wbs-icon-h-group",
                    bg: 'group-bg'
                },
                {
                    label: "用户数量",
                    name: "userCount",
                    value: 0,
                    icon: "#wbs-icon-user-icon",
                    bg: 'node-bg'
                },
                {
                    label: "已部署的智能合约",
                    name: "contractCount",
                    value: 0,
                    icon: "#wbs-icon-contract",
                    bg: 'contract-bg'
                },
                {
                    label: "区块数量",
                    name: "blockCount",
                    value: 0,
                    icon: "#wbs-icon-transation",
                    bg: "block-bg"
                },
                {
                    label: "交易数量",
                    name: "txnCount",
                    value: 0,
                    icon: "#wbs-icon-block",
                    bg: 'transation-bg'
                }
            ]
        }
    },
    computed: {
    },
    mounted() {
        if (this.singleSearchValue && this.tabName == '0') {
            this.querySimpleSearch()
        }
        if (this.searchValue && this.tabName == '1') {
            this.sureSearch()
        }
        if (this.tabName == 0) {
            this.searchType = '5'
        } else if (this.tabName == 1) {
            this.searchType = sessionStorage.getItem('searchType') || '1'
        }
        this.queryChainAll()
        this.getNetworkDetails()
    },
    methods: {
        deleteKeyword() {

        },
        handleBtn() {

        },
        btnText(key) {
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
            sessionStorage.setItem('tab', tab.paneName)
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
                this.searchType = sessionStorage.getItem('searchType') || '1'
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
                            chain.value = chain.chainId.toString()
                        })
                        if (!sessionStorage.getItem('chainId')) {
                            if (this.chainList.length) {
                                this.chainId = this.chainList[0]['value']
                                this.queryGroupList(this.chainId);
                            }
                        } else {
                            this.chainId = sessionStorage.getItem('chainId')
                            this.queryGroupList(this.chainId);
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
            let param = {
                chainId: val
            }
            groupList(param)
                .then(res => {
                    if (res.data.code === 0) {
                        let arr = res.data.data
                        this.groupCollection = []
                        arr.forEach(group => {
                            this.groupCollection.push({
                                label: group.groupName,
                                value: group.groupId.toString(),
                                appName: group.appName,
                            })

                        })
                        let groupIdList = this.groupCollection.map(item => {
                            return item.value
                        })

                        var is = groupIdList.includes(sessionStorage.getItem('groupId'))
                        if (!is) {
                            if (this.groupCollection.length) {
                                this.groupId = this.groupCollection[0]['value']
                            }
                        } else {
                            this.groupId = sessionStorage.getItem('groupId')
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
            sessionStorage.setItem('simpleKeyword', this.singleSearchValue)
            this.listLoading = true;
            var data = {
                keyword: this.singleSearchValue
            }

            simpleSearch(this.currentPage, this.pageSize, data)
                .then(res => {
                    if (res.data.code === 0) {
                        let list = res.data.data;
                        let total = res.data.totalCount
                        this.queryAppList(list, total)
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
        queryAppList(list, total) {
            this.total = total
            let param = {

            }
            groupList(param)
                .then(res => {
                    if (res.data.code === 0) {
                        let appList = res.data.data
                        list.forEach(item => {
                            appList.forEach(it => {
                                if (item.chainId == it.chainId && item.groupId == it.groupId) {
                                    item.appName = it.appName
                                }
                            })
                        })
                        this.queryAppChain(list)
                    } else {
                        this.$message({
                            type: 'error',
                            message: this.$chooseLang(res.data.code),
                        })
                    }
                })
        },
        queryAppChain(list) {
            chainAll()
                .then(res => {
                    if (res.data.code === 0) {
                        let chainList = res.data.data;
                        list.forEach(item => {
                            chainList.forEach(it => {
                                if (item.chainId == it.chainId) {
                                    item.chainName = it.chainName
                                }
                            })
                        })
                        this.list = list
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
        changeChain(val) {
            sessionStorage.setItem('chainId', val)
            this.chainId = val
            this.queryGroupList(val)
        },
        changeGroup(val) {
            sessionStorage.setItem('groupId', val)
            this.groupId = val
            this.searchValue = ''
            // this.sureSearch()
        },
        changeSearchType(type) {
            sessionStorage.setItem('searchType', type)
            sessionStorage.setItem('searchValue', '')
            this.list = []
            this.searchType = type;
            this.searchValue = ''
        },
        sureSearch() {
            sessionStorage.setItem('chainId', this.chainId)
            sessionStorage.setItem('groupId', this.groupId)
            sessionStorage.setItem('searchType', this.searchType)
            sessionStorage.setItem('searchValue', this.searchValue)
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
                if (!rexIsNumber(this.searchValue)) {
                    reqQuery.blockHash = this.searchValue;
                } else {
                    reqQuery.blockNumber = this.searchValue
                }

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
        getNetworkDetails: function () {
            this.loadingNumber = true;
            chainGeneral()
                .then(res => {
                    this.loadingNumber = false;
                    if (res.data.code === 0) {
                        this.detailsList.forEach(function (value, index) {
                            for (let i in res.data.data) {
                                if (value.name === i) {
                                    value.value = res.data.data[i];
                                }
                            }
                        });
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.$message({
                        message: this.$t('text.systemError'),
                        type: "error",
                        duration: 2000
                    });

                });
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

.el-col {
    border-radius: 4px;
}
.overview-item {
    border-radius: 4px;
    height: 64px;
    display: flex;
    padding: 28px 16px;
}
.overview-item-img {
    display: inline-block;
    width: 64px;
    height: 64px;
    line-height: 64px;
}
.overview-item-content {
    font-size: 12px;
    display: inline-block;
    padding-left: 10px;
}
.overview-item-number {
    font-size: 24px;
    color: #fff;
}
.overview-item-title {
    width: 100%;
    color: #fff;
}
.node-bg {
    background: linear-gradient(to top right, #47befa, #37eef2);
}
.contract-bg {
    background: linear-gradient(to top right, #466dff, #30a7ff);
}
.block-bg {
    background: linear-gradient(to top right, #736aff, #b287ff);
}
.transation-bg {
    background: linear-gradient(to top right, #ff6e9a, #ffa895);
}
.chain-bg {
    background: linear-gradient(to top right, #e87b66, #f89e8c);
}
.group-bg {
    background: linear-gradient(to top right, #4acbba, #89e9d6);
}
@media screen and (max-width: 1400px) {
    .overview-item-img {
        width: 54px;
        height: 54px;
    }
}
@media screen and (max-width: 1200px) {
    .overview-item-img {
        width: 44px;
        height: 44px;
    }
}
@media screen and (max-width: 992px) {
}
@media screen and (max-width: 768px) {
}
</style>