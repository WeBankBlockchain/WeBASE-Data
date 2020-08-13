<template>
    <div>
        <content-head :headTitle="'区块链'"></content-head>
        <div class="module-wrapper">
            <div class="search-part" style="padding-top: 20px;">

            </div>
            <div class="search-table">
                <el-table :data="chainData" class="search-table-content" ref="refTable" v-loading='loading'>
                    <el-table-column type="expand" align="center">
                        <template slot-scope="scope">
                            <chain-detail :scope="scope"></chain-detail>
                        </template>
                    </el-table-column>
                    <el-table-column v-for="head in chainHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                        <template slot-scope="scope">
                            <span v-if="head.enName == 'chainType' ">{{scope.row[head.enName] | Type}}</span>
                            <span v-else>{{scope.row[head.enName]}}</span>
                        </template>
                    </el-table-column>
                </el-table>

                <!-- <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
                </el-pagination> -->
            </div>
        </div>
    </div>
</template>
<script>
import contentHead from "@/components/contentHead";
import chainDetail from "./chainDetail";
import { chainAll } from "@/util/api"
export default {
    name: 'chain',
    components: { contentHead, chainDetail },
    data() {
        return {
            chainHead: [
                {
                    enName: 'chainName',
                    name: '区块链名称'
                },
                {
                    enName: 'chainId',
                    name: '区块链编号'
                },
                {
                    enName: 'chainType',
                    name: '区块链类型'
                },
                {
                    enName: 'createTime',
                    name: '创建时间'
                },
                {
                    enName: 'description',
                    name: '备注'
                }
            ],
            chainData: [],
            loading: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
        }
    },
    mounted() {
        this.getChainList()
    },
    methods: {
        getChainList: function () {
            this.loading = true
            chainAll().then(res => {
                this.loading = false
                if (res.data.code == 0) {
                    this.chainData = res.data.data;
                    this.total = res.data.totalCount;
                } else {
                    this.$message({
                        type: "error",
                        message: this.$chooseLang(res.data.code)
                    })
                }
            }).catch(err => {
                this.loading = false
                this.$message({
                    type: "error",
                    message: "系统错误"
                })
            })
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.getChainList();
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.getChainList();
        },
        handleTab(val, $event) {
            this.currentTab = $event.name
        },
    },
    filters: {
        Type: function (val) {
            if (val) {
                return "fisco-bcos(secp256k1/sha3)"
            } else {
                return "fisco-bcos(sm2/sm3)"
            }
        }
    }
}
</script>