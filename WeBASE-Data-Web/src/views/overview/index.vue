<template>
    <div class="app-container">
        <content-head :headTitle="`链${chainId}`" :headSubTitle="`群组${groupId}`" :icon="true"></content-head>
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
    </div>
</template>

<script>
import { groupGeneral, groupTransDaily, groupNodeList, blockList } from "@/util/api";
import Chart from "@/components/Charts/BaseLine"
import contentHead from "@/components/contentHead";
import { changWeek, numberFormat, unique } from "@/util/util";
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
        }
    },

    computed: {
        detailsList() {
            let data = [
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
            ]
            return data
        },
        nodeHead() {
            let data = [
                {
                    enName: "nodeId",
                    name: "节点ID",
                    width: ""
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
        if (this.$route.query.chainId || this.$route.query.groupId) {
            this.chainId = this.$route.query.chainId
            this.groupId = this.$route.query.groupId
        }
        this.querygroupGeneral()
        this.queryNodeList()
        this.$nextTick(function () {
            this.chartStatistics.chartSize.width = this.$refs.chart.offsetWidth;
            this.chartStatistics.chartSize.height = this.$refs.chart.offsetHeight;
            this.queryGroupTransDaily();
        });
    },

    methods: {
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
    }
}
</script>

<style scoped>
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
    /* cursor: pointer; */
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
</style>
