<template>
    <div>
        <v-content-head :headTitle="'区块链管理'"></v-content-head>
        <div class="module-wrapper">
            <div class="search-part" style="padding-top: 20px;">
                <div class="search-part-left">
                    <el-button type="primary" class="search-part-left-btn" @click="createChain">新增区块链</el-button>
                </div>
            </div>
            <div class="search-table">
                <el-table :data="chainData" class="search-table-content" ref="refTable">
                    <el-table-column type="expand" align="center">
                        <template slot-scope="scope">
                            <chain-detail :scope="scope"></chain-detail>
                        </template>
                    </el-table-column>
                    <el-table-column prop="chainName" show-overflow-tooltip label="区块链名称">
                    </el-table-column>
                    <el-table-column prop="chainId" label="区块链编号">
                    </el-table-column>
                    <el-table-column prop="encryptType" label="区块链类型">
                        <template slot-scope="scope">
                            <span>{{scope.row.encryptType | Type}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间">
                    </el-table-column>
                    <el-table-column prop="description" show-overflow-tooltip label="备注">
                    </el-table-column>
                    <el-table-column label="操作" width="100">
                        <template slot-scope="scope">
                            <el-button @click="modifyChain(scope.row, 'modify')" type="text" size="small">修改</el-button>
                            <el-button @click="handleClick(scope.row)" type="text" size="small">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <!-- <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
                </el-pagination> -->
            </div>
        </div>
        <add-chain v-if='addChainShow' :show='addChainShow' @close='addChainClose' :chainDialogTitle="chainDialogTitle" :chainDialogOptions="chainDialogOptions"></add-chain>
    </div>
</template>

<script>
import contentHead from "@/components/contentHead";
import { getChains, deleteChain } from "@/util/api"
import addChain from "./dialog/addChain"
import chainDetail from "./chainDetail.vue";
import Bus from "@/bus"
export default {
    name: "chain",
    components: {
        "v-content-head": contentHead,
        "add-chain": addChain,
        chainDetail
    },
    data() {
        return {
            chainData: [],
            addChainShow: false,
            loading: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            chainDialogOptions: {}
        }
    },
    mounted: function () {
        this.getChainList()
    },
    methods: {
        createChain: function () {
            this.chainDialogOptions = {
                type: 'creat',
                data: {
                    chainName: "",
                    chainId: "",
                    type: 0,
                }
            }
            this.addChainShow = true;
            this.chainDialogTitle = '新增区块链'
        },
        getChainList: function () {
            getChains().then(res => {
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
                this.$message({
                    type: "error",
                    message: "系统错误"
                })
            })
        },
        addChainClose: function () {
            Bus.$emit("delete")
            this.addChainShow = false;
            this.getChainList()
        },
        modifyChain(val, type) {
            this.chainDialogOptions = {
                type: type,
                data: val
            }
            this.addChainShow = true;
            this.chainDialogTitle = '修改区块链'
        },
        handleClick: function (val) {
            this.$confirm('此操作将删除该链, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteData(val)
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        deleteData: function (val) {
            deleteChain(val.chainId).then(res => {
                if (res.data.code === 0) {
                    this.$message({
                        type: 'success',
                        message: '删除成功'
                    });
                    Bus.$emit("delete")
                    this.getChainList()
                } else {
                    this.$message({
                        type: "error",
                        message: this.$errCode.errCode[res.data.code].zh
                    })
                }
            }).catch(err => {
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
        handleTab(tab, $event) {
            this.currentTab = tab.name
        }
    },
    filters: {
        Type: function (val) {
            if (val) {
                return "fisco-bcos(sm2/sm3)"
            } else {
                return "fisco-bcos(secp256k1/sha3)"
            }
        }
    }
}
</script>

<style>
</style>