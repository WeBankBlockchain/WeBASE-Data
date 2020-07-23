<template>
    <div class="app-container">
        <content-head :headTitle="`${chainName}`" :icon="true" @changGroup="changGroup"></content-head>
        <!-- <div class="module-wrapper app-description">
            <div class="description-left">
                <div style="height: 160px;width: 160px;">
                    <img style="height: 100%;width: 100%;" src="../../../static/image/btb.jpeg" alt="图片">
                </div>
                <div class="left-content">
                    <p class="myReleasedApply">服务</p>
                    <p>
                        <span class="">
                            {{appTitleMap.appName}}
                        </span>
                        <span v-if="appTitleMap.appVersion">v{{appTitleMap.appVersion}}</span>
                    </p>
                </div>
            </div>
            <div class="description-right">
                <p class="myReleasedApply">服务简介</p>
                <span class="myReleasedApply-content">
                    {{appTitleMap.appSynopsis}}
                </span>
            </div>
        </div> -->
        <!-- <div class="module-wrapper box-content" >
            <div class="box-content-title">
                <p style="font-weight: bold; font-size: 18px;">
                    服务描述
                </p>
            </div>
            <div id="appDesc" style="margin-right: 22px; word-break: break-all;">
                <p>{{appTitleMap.description}}</p>
            </div>
            <div>
                <p></p>
            </div>
        </div> -->
        <div style="margin: 5px;">
            <div style="margin:10px 10px 6px 10px;">
                <el-row>
                    <el-col :xs='24' :sm="24" :md="11" :lg="10" :xl="8">
                        <div class="overview-item" style="font-size:0" v-for="item in detailsList" :key='item.label' @click="goDetailRouter(item)" :class="item.bg">
                            <div class="overview-item-img">
                                <svg class="overview-item-svg" aria-hidden="true" v-if='item.icon == "#wbs-icon-node1"'>
                                    <use xlink:href="#wbs-icon-node1"></use>
                                </svg>
                                <svg class="overview-item-svg" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-contract"'>
                                    <use xlink:href="#wbs-icon-contract"></use>
                                </svg>
                                <svg class="overview-item-svg" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-block"'>
                                    <use xlink:href="#wbs-icon-block"></use>
                                </svg>
                                <svg class="overview-item-svg" aria-hidden="true" v-else-if='item.icon == "#wbs-icon-transation"'>
                                    <use xlink:href="#wbs-icon-transation"></use>
                                </svg>
                            </div>
                            <div class="overview-item-content">
                                <div class="overview-item-number">{{item.value}}</div>
                                <div class="overview-item-title">{{item.label}}</div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :xs='24' :sm="24" :md="13" :lg="14" :xl="16">
                        <div style="margin: 8px 0px 0 0px;" class="module-box-shadow bg-fff">
                            <div class="part2-title">
                                <span class="part2-title-left">关键监控指标</span>
                                <span class="part2-title-right">最近有交易的7天交易量（笔）</span>
                            </div>
                            <div class="chart" ref="chart">
                                <chart ref="linechart" :id="'homeId'" v-if="chartStatistics.show" :data="chartStatistics.date" :transactionDataArr="chartStatistics.dataArr" :size="chartStatistics.chartSize" v-loading="loadingCharts"></chart>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>

        <div class="module-wrapper-small">
            <el-table :data="nodeList" class="search-table-content">
                <el-table-column v-for="head in nodeHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="" :width='head.width'>
                    <template slot-scope="scope">
                        <template>
                            <span v-if="head.enName ==='nodeActive'">
                                <span :style="{'color': textColor(scope.row[head.enName])}" class=" font-6">{{nodesStatus(scope.row[head.enName])}}</span>
                            </span>
                            <span v-else-if="head.enName === 'nodeId'">
                                <i class="wbs-icon-copy font-12" @click="copyNodeIdKey(scope.row[head.enName])" title="复制"></i>
                                {{scope.row[head.enName]}}
                            </span>
                            <span v-else>{{scope.row[head.enName]}}</span>
                        </template>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div style="min-width: 540px;margin: 8px 8px 0px 9px;">
            <el-row :gutter="16">
                <el-col :xs='24' :sm="24" :md="12" :lg="12" :xl="12">
                    <div class="overview-wrapper">
                        <p>
                            <span class="overview-title">区块</span>
                            <span class="overview-more cursor-pointer" @click="goRouter('blocks')">更多>>></span>
                        </p>
                        <div class="overview-item-base" v-loading="loadingBlock">
                            <div class="block-item font-color-2e384d" v-for="item in blockData" :key='item.blockNumber'>
                                <div class="block-amount" style="padding-bottom: 7px;">
                                    <span>
                                        <span class="link" @click="goRouter('blocks', item.blockNumber)">块高 {{item.blockNumber}}</span>
                                    </span>
                                    <span class="color-8798AD">{{item.blockTimestamp}}</span>
                                </div>
                                <div>
                                    <div class="block-miner">
                                        <span>出块者</span>
                                        <p :title="`${item.sealer}`">{{item.sealer}}</p>
                                    </div>
                                    <div class="text-right">
                                        <span class="block-trans" @click="goRouter('blocks',item.blockNumber)">
                                            <span>{{item.transCount}}</span>
                                            <span>txns</span>
                                        </span>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs='24' :sm="24" :md="12" :lg="12" :xl="12">
                    <div class="overview-wrapper">
                        <p>
                            <span class="overview-title">交易</span>
                            <span class="overview-more cursor-pointer" @click="goRouter('transactions')">更多>>></span>
                        </p>
                        <div class="overview-item-base" v-loading="loadingTransaction">
                            <div class="block-item font-color-2e384d" v-for="item in transactionList" :key='item.transHash'>
                                <div class="block-amount">
                                    <p class="trans-hash" :title="`${item.transHash}`">
                                        <i class="wbs-icon-copy font-12" @click="copyNodeIdKey(item.transHash)" title='复制'></i>

                                        <span class="link" @click="goRouter('transactions', item.transHash)">{{item.transHash}} </span>
                                    </p>
                                    <p class="trans-address color-8798AD">
                                        <span>
                                            <i class="wbs-icon-copy font-12" @click="copyNodeIdKey(JSON.parse(item.receiptDetail)['from'])" title='复制'></i>
                                            {{splitAddress(JSON.parse(item.receiptDetail)['from'])}}
                                        </span>
                                        <img :src="sRight" :alt="$t('text.arrow')">
                                        <span>
                                            <i class="wbs-icon-copy font-12" @click="copyNodeIdKey(JSON.parse(item.receiptDetail)['to'])" title='复制'></i>
                                            {{splitAddress(JSON.parse(item.receiptDetail)['to'])}}
                                        </span>
                                    </p>

                                </div>
                                <p class="color-8798AD text-right">{{item.blockTimestamp}}</p>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
import { groupGeneral, groupTransDaily, groupNodeList, blockList, transList, groupList } from "@/util/api";
import Chart from "@/components/Charts/BaseLine"
import contentHead from "@/components/contentHead";
import { changWeek, numberFormat, unique } from "@/util/util";
import sRight from "@/../static/image/s-right.png";
export default {
    name: 'overview',

    components: {
        Chart,
        contentHead
    },

    props: {
    },

    data() {
        return {
            sRight: sRight,
            nodeList: [],
            chainId: '',
            groupId: '',
            chartStatistics: {
                show: false,
                date: [],
                dataArr: [],
                chartSize: {
                    width: 0,
                    height: 0
                }
            },
            loadingNumber: false,
            loadingCharts: false,
            loadingNodes: false,
            loadingBlock: false,
            loadingTransaction: false,
            detailsList: [
                {
                    label: "用户数量",
                    name: "userCount",
                    value: 0,
                    icon: "#wbs-icon-node1",
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
                    name: "blockNumber",
                    value: 0,
                    icon: "#wbs-icon-block",
                    bg: "block-bg"
                },
                {
                    label: "交易数量",
                    name: "txnCount",
                    value: 0,
                    icon: "#wbs-icon-transation",
                    bg: 'transation-bg'
                }
            ],
            chainName: '',
            blockData: [],
            transactionList: [],
            appTitleMap: {}
        }
    },

    computed: {
        nodeHead() {
            let data = [
                {
                    enName: "nodeId",
                    name: "节点ID",
                    width: ""
                },
                {
                    enName: "orgName",
                    name: "机构名称",
                    width: 180
                },
                {
                    enName: "blockNumber",
                    name: "块高",
                    width: 180
                },
                
                {
                    enName: "pbftView",
                    name: "PbftView",
                    width: 180
                },
                {
                    enName: "nodeActive",
                    name: "状态",
                    width: 150
                }
            ]
            return data
        }
    },

    watch: {

    },

    created() {
    },

    mounted() {

    },

    methods: {
        changGroup(val) {
            if (this.$route.params.chainId) {
                this.chainName = this.$route.params.chainName
                this.chainId = this.$route.params.chainId
                this.groupId = val
            }
            this.queryGroup()
            this.querygroupGeneral()
            this.queryNodeList()
            this.getBlockList()
            this.getTransaction()
            this.$nextTick(function () {
                this.chartStatistics.chartSize.width = this.$refs.chart.offsetWidth;
                this.chartStatistics.chartSize.height = this.$refs.chart.offsetHeight;
                this.queryGroupTransDaily();
            });
        },
        queryGroup() {
            let param = {
                chainId: this.chainId
            }
            groupList(param)
                .then(res => {
                    if (res.data.code === 0) {
                        let arr = res.data.data
                        var list = arr.filter(item=>{
                            return item.groupId == this.groupId
                        })
                        
                        if(list.length) this.appTitleMap = list[0];
                        
                    }
                })

        },
        querygroupGeneral() {
            groupGeneral(this.chainId, this.groupId)
                .then(res => {
                    if (res.data.code === 0) {
                        this.detailsList.forEach((value, index) => {
                            for (let i in res.data.data) {
                                if (value.name === i) {
                                    this.$set(value, 'value', res.data.data[i])
                                }
                            }
                        });
                    }
                })
        },
        queryNodeList() {
            groupNodeList(this.chainId, this.groupId, 1, 1000).then(res => {
                if (res.data.code === 0) {
                    this.nodeList = res.data.data
                }
            })
        },
        queryGroupTransDaily() {

            groupTransDaily(this.chainId, this.groupId)
                .then(res => {
                    if (res.data.code === 0) {
                        let resData = changWeek(res.data.data);
                        this.chartStatistics.date = []
                        this.chartStatistics.dataArr = []
                        for (let i = 0; i < resData.length; i++) {
                            this.chartStatistics.date.push(resData[i].day);
                            this.chartStatistics.dataArr.push(
                                resData[i].transCount
                            );
                        }
                        this.$set(this.chartStatistics, "show", true);
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
        },
        getBlockList: function () {
            this.loadingBlock = true;
            let reqData = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageNumber: 1,
                pageSize: 6
            },
                reqQuery = {};
            blockList(reqData, reqQuery)
                .then(res => {
                    this.loadingBlock = false;
                    if (res.data.code === 0) {
                        this.blockData = res.data.data;
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
                        message: '系统错误',
                        type: "error",
                        duration: 2000
                    });

                });
        },
        getTransaction: function () {
            this.loadingTransaction = true;
            let reqData = {
                chainId: this.chainId,
                groupId: this.groupId,
                pageNumber: 1,
                pageSize: 6
            },
                reqQuery = {};
            transList(reqData, reqQuery)
                .then(res => {
                    this.loadingTransaction = false;
                    if (res.data.code === 0) {
                        this.transactionList = res.data.data;
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
                        message: '系统错误',
                        type: "error",
                        duration: 2000
                    });

                });
        },
        goDetailRouter(item) {
            let name = item.name;
            switch (name) {
                case "blockNumber":
                    this.$router.push({
                        path: "/blockInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId
                        }
                    });
                    break;
                case "txnCount":
                    this.$router.push({
                        path: "/transactionInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId
                        }
                    });
                    break;
                case "userCount":
                    this.$router.push({
                        path: "/userInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId
                        }
                    });
                    break;
                case "contractCount":
                    this.$router.push({
                        path: "/contractInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId
                        }
                    });
                    break;
            }
        },
        textColor: function (val) {
            let colorString = "";
            switch (val) {
                case 1:
                    colorString = "#58cb7d";
                    break;
                case 2:
                    colorString = "#ed5454";
                    break;
            }
            return colorString;
        },
        nodesStatus: function (val) {
            let transString = "";
            switch (val) {
                case 1:
                    transString = "运行";
                    break;
                case 2:
                    transString = "异常";
                    break;
            }
            return transString;
        },
        goRouter: function (val, num) {
            switch (val) {
                case "blocks":
                    this.$router.push({
                        path: "/blockInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            blockNumber: num
                        }
                    });
                    break;

                case "transactions":
                    this.$router.push({
                        path: "/transactionInfo",
                        query: {
                            chainId: this.chainId,
                            groupId: this.groupId,
                            transHash: num
                        }
                    });
                    break;
            }
        },
        copyNodeIdKey: function (val) {
            if (!val) {
                this.$message({
                    type: "fail",
                    showClose: true,
                    message: this.$t("text.copyErrorMsg"),
                    duration: 2000
                });
            } else {
                this.$copyText(val).then(e => {
                    this.$message({
                        type: "success",
                        showClose: true,
                        message: this.$t("text.copySuccessMsg"),
                        duration: 2000
                    });
                });
            }
        },
        splitAddress(val) {
            if (!val) return;
            var startStr = '', endStr = '', str = '';
            startStr = val.substring(0, 8);
            endStr = val.substring(val.length - 4);
            str = `${startStr}...${endStr}`;
            return str;
        }
    }
}
</script>

<style scoped>
.left-content {
    margin-left: 10px;
    min-width: 122px;
}
.left-content-title {
    font-weight: bold;
    font-size: 18px;
    margin-left: 12px;
    text-overflow: ellipsis;
    width: 230px;
    white-space: nowrap;
}
.myReleasedApply {
    font-weight: bold;
    font-size: 18px;
}
.myReleasedApply-content {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
}
.box-content {
    padding: 20px;
}
.box-content-title {
    padding: 0px 0 10px 0;
}
#appDesc p {
        word-break: break-all;
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
.over-view-wrapper {
    background: #f7f7f7;
}
.amount-wrapper {
    margin: 30px 30px 0 31px;
}
.font-12 {
    font-size: 12px;
    color: #9da2ab;
}
.part1-content {
    display: flex;
    background: #f7f7f7;
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: space-between;
}
.split-line {
    margin-left: 22px;
    margin-top: 10px;
    margin-bottom: 5px;
    margin-right: 20px;
    opacity: 0.25;
}
.overview-number {
    margin-top: 20px;
    margin-left: 20px;
    padding: 20px;
}
.part1-content-amount {
    overflow: auto;
    min-width: 112px;
}
.part2-title {
    padding: 22px 31px 26px 32px;
}
.part2-title::after {
    display: block;
    content: "";
    clear: both;
}
.part2-title-left {
    float: left;
    font-size: 16px;
    color: #000e1f;
    font-weight: bold;
}
.part2-title-right {
    float: right;
    font-size: 12px;
    color: #727476;
    padding: 2px 12px;
    border-radius: 20px;
    background: #f6f6f6;
}
.part3-title {
    padding: 40px 60px 40px 40px;
}
.part3-title::after {
    display: block;
    content: "";
    clear: both;
}
.more-content {
    font-size: 14px;
    color: #0db1c1;
    cursor: pointer;
}
.part3-table-content {
    width: 100%;
    padding: 0 39px 48px 40px;
    font-size: 12px;
}
.part3-table-content >>> th,
.part3-table-content >>> td {
    padding: 8px 0;
}
.part1-details-bottom {
    display: flex;
    flex-flow: row nowrap;
    justify-content: space-between;
    align-items: center;
}
.part1-details-arrow-right {
    position: relative;
    top: 4px;
}
.search-table-content {
    width: 100%;
}
.search-table-content >>> th {
    background: #fafafa;
    color: #2e384d;
}
.search-table-content >>> th,
.search-table-content >>> td {
    font-size: 14px;
}
.overview-wrapper {
    background: #fff;
    font-size: 15px;
    box-shadow: 0 4px 12px 0 #dfe2e9;
    border-radius: 2px;
}
.overview-wrapper > p {
    padding: 26px 18px 0px 22px;
    border-bottom: 1px solid #f2f2f2;
    display: flex;
    justify-content: space-between;
}
.overview-title {
    font-size: 15px;
    color: #2e384d;
    padding-bottom: 22px;
    border-bottom: 2px solid #2e384d;
}
.overview-more {
    font-size: 14px;
    color: #2fcdd1;
}
.block-item {
    display: flex;
    flex-flow: row;
    justify-content: space-between;
    padding-bottom: 10px;
}
.block-amount {
    display: flex;
    flex-flow: column;
}
.overview-item-base {
    margin: 26px 18px 30px 22px;
}
.block-miner {
    display: flex;
    flex-flow: row wrap;
}
.block-miner > p {
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-left: 10px;
}
.trans-hash {
    max-width: 300px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.node-ip {
    color: #0db1c1;
}
.block-trans {
    display: inline-block;
    padding: 0 2px;
    background-color: #f6f7f8;
    color: #0db1c1;
    cursor: pointer;
}
.trans-address span {
    display: inline-block;
    max-width: 150px;
}
.trans-address img {
    vertical-align: middle;
}
.overview-item {
    display: inline-block;
    width: calc(49% - 15px);
    max-width: 300px;
    height: 120px;
    padding: 28px 16px;
    margin: 8px 15px 16px 0;
    background-color: #fff;
    box-shadow: 0 4px 12px 0 #dfe2e9;
    border-radius: 2px;
    box-sizing: border-box;
    cursor: pointer;
}
.overview-item-img {
    display: inline-block;
    width: 50px;
}
.overview-item-content {
    font-size: 12px;
    display: inline-block;
    padding-left: 10px;
    width: calc(100% - 60px);
}
.overview-item-number {
    font-size: 24px;
    color: #fff;
}
.overview-item-title {
    width: 100%;
    color: #fff;
}
.overview-item-svg {
    width: 50px;
    height: 50px;
}
@media screen and (max-width: 1142px) {
    .overview-item {
        display: inline-block;
        width: calc(49% - 15px);
        max-width: 300px;
        height: 120px;
        padding: 28px 12px;
        margin: 8px 15px 16px 0;
        background-color: #fff;
        box-shadow: 0 4px 12px 0 #dfe2e9;
        border-radius: 2px;
        box-sizing: border-box;
    }
    .overview-item-img {
        display: inline-block;
        width: 40px;
    }
    .overview-item-svg {
        width: 40px;
        height: 40px;
    }
    .overview-item-content {
        font-size: 12px;
        display: inline-block;
        padding-left: 5px;
        width: calc(100% - 45px);
    }
}
@media screen and (max-width: 1042px) {
    .overview-item {
        display: inline-block;
        width: calc(49% - 15px);
        max-width: 300px;
        height: 120px;
        padding: 28px 6px;
        margin: 8px 15px 16px 0;
        background-color: #fff;
        box-shadow: 0 4px 12px 0 #dfe2e9;
        border-radius: 2px;
        box-sizing: border-box;
    }
    .overview-item-img {
        display: inline-block;
        width: 35px;
    }
    .overview-item-svg {
        width: 35px;
        height: 35px;
    }
    .overview-item-content {
        font-size: 12px;
        display: inline-block;
        padding-left: 5px;
        width: calc(100% - 40px);
    }
}
@media screen and (max-width: 991px) {
    .overview-item {
        display: inline-block;
        width: calc(49% - 8px);
        max-width: 385px;
        height: 120px;
        padding: 28px 16px;
        margin: 8px 15px 16px 0;
        background-color: #fff;
        box-shadow: 0 4px 12px 0 #dfe2e9;
        border-radius: 2px;
        box-sizing: border-box;
    }
    .overview-item-img {
        display: inline-block;
        width: 50px;
    }
    .overview-item-svg {
        width: 50px;
        height: 50px;
    }
    .overview-item-content {
        font-size: 12px;
        display: inline-block;
        padding-left: 10px;
        width: calc(100% - 60px);
    }
    /* .el-col:nth-child(2){
        margin: 8px 16px 16px 0;
    } */
    .overview-item:nth-child(2) {
        margin: 8px 15px 16px 0;
    }
}
.app-description {
    min-width: 1200px;
    display: flex;
    justify-content: flex-start;
    margin-top: 20px;
    padding: 10px;
}
.description-left {
    display: flex;
    justify-content: normal;
    width: 50%;
}
.description-right {
    width: 50%;
}
</style>
