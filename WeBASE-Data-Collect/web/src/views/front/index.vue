<template>
    <div>
        <v-content-head :headTitle="'前置管理'" @changeChain='changeChain'></v-content-head>
        <div class="module-wrapper">
            <div class="search-part" style="padding-top: 20px;">
                <div class="search-part-left">
                    <el-button type="primary" class="search-part-left-btn" @click="createFront">新增节点前置</el-button>
                </div>
                <!-- <div class="search-part-right">
                    群组切换：
                    <el-select v-model="groupId" placeholder="请选择" @change='search'>
                            <el-option
                            v-for="item in groupList"
                            :key="item.groupId"
                            :label="item.groupName"
                            :value="item.groupId">
                            </el-option>
                        </el-select>
                </div> -->
            </div>
            <div class="search-table">
                <el-table :data="frontData" class="search-table-content" v-loading="loading">
                    <el-table-column prop="frontId" label="前置编号" width="180">
                    </el-table-column>
                    <el-table-column prop="nodeId" label="节点编号" show-overflow-tooltip>
                        <template slot-scope='scope'>
                            <i class="el-icon-document-copy" @click="copyNodeIdKey(scope.row.nodeId)" title="复制"></i>
                            <span>{{scope.row.nodeId}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="frontIp" label="前置IP" width="180">
                        <template slot-scope="scope">
                            <span>{{scope.row.frontIp}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="frontPort" label="前置端口">
                    </el-table-column>
                    <el-table-column prop="agency" label="所属机构">
                    </el-table-column>
                    <!-- <el-table-column
                        prop="description"
                        label="备注"
                        width="180">
                    </el-table-column> -->
                    <el-table-column prop="createTime" label="创建时间" width="180">
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" width="100">
                        <template slot-scope="scope">
                            <el-button @click="handleClick(scope.row)" type="text" size="small">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
        <set-front v-if='setFrontShow' :show='setFrontShow' @close='setFrontClose'></set-front>
    </div>
</template>

<script>
import contentHead from "@/components/contentHead";
import setFront from "./dialog/setFront"
import { getFronts, getGroups, delFront } from "@/util/api"

export default {
    name: "front",
    components: {
        "v-content-head": contentHead,
        "set-front": setFront
    },
    data() {
        return {
            frontData: [],
            loading: false,
            setFrontShow: false,
            groupList: [],
            groupId: ""
        }
    },
    mounted() {
        if (localStorage.getItem('chainId')) {
            this.getFrontList();
        }
    },
    methods: {
        changeChain() {
            this.getFrontList();
        },
        search() {
            this.getFrontList()
        },
        createFront() {
            this.setFrontShow = true;
        },
        setFrontClose() {
            this.setFrontShow = false;
            if (localStorage.getItem('chainId')) {
                this.getFrontList()
            }
        },
        getFrontList() {
            let data = {
                chainId: localStorage.getItem('chainId')
            }
            getFronts(data).then(res => {
                if (res.data.code === 0) {
                    this.frontData = res.data.data
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
        // getGroupList() {
        //     getGroups(localStorage.getItem('chainId')).then(res => {
        //         if (res.data.code === 0) {
        //             this.groupList = res.data.data;
        //             if (res.data.data.length) {
        //                 this.groupId = res.data.data[0].groupId
        //             }
        //         } else {
        //             this.$message({
        //                 type: "error",
        //                 message: this.$chooseLang(res.data.code)
        //             })
        //         }
        //     }).catch(err => {
        //         this.$message({
        //             type: "error",
        //             message: "系统错误"
        //         })
        //     })
        // },
        handleClick(val) {
            this.$confirm('此操作将在数据库删除该前置,删除后可以再次添加 是否继续?', '提示', {
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
        deleteData(val) {
            delFront(val.frontId).then(res => {
                if (res.data.code === 0) {
                    this.$message({
                        type: 'success',
                        message: '删除成功'
                    });
                    this.getFrontList()
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
        copyNodeIdKey(val) {
            if (!val) {
                this.$message({
                    type: "fail",
                    showClose: true,
                    message: '复制失败',
                    duration: 2000
                });
            } else {
                this.$copyText(val).then(e => {
                    this.$message({
                        type: "success",
                        showClose: true,
                        message: '复制成功',
                        duration: 2000
                    });
                });
            }
        },
    }
}
</script>

<style scoped>
.search-part-left {
    float: left;
}
.search-part-right {
    float: right;
}
</style>