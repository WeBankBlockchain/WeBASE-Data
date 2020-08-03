<template>
    <div>
        <v-content-head :headTitle="'配置'"></v-content-head>
        <div class="module-wrapper">
            <div class="search-part" style="padding-top: 20px;">
                <div class="search-part-left">
                    <el-button type="primary" class="search-part-left-btn" @click="createKeyword">新增关键字</el-button>
                </div>
            </div>
            <div>
                <el-table :data="keywordList" tooltip-effect="dark" v-loading="loading">
                    <el-table-column v-for="head in keywordHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                        <template slot-scope="scope">
                            <template v-if="head.enName!='operate'">
                                <span>{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else>
                                <el-button type="text" size="small" @click="deleteKeyword(scope.row,'delete')">删除</el-button>
                                <el-button type="text" size="small" @click="updateKeyword(scope.row,'modify')">修改</el-button>
                            </template>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total">
                </el-pagination>
            </div>

        </div>
        <el-dialog :visible.sync="keywordVisible" :title="configTitle" width="433px" :append-to-body="true" :center="true" class="dialog-wrapper" v-if="keywordVisible">
            <keyword @success="success" @close="close" :configOptions="configOptions"></keyword>
        </el-dialog>
    </div>
</template>

<script>
import contentHead from "@/components/contentHead";
import keyword from "./components/keyword";
import { keywordsList } from "@/util/api";
export default {
    name: 'keywordConfig',

    components: {
        "v-content-head": contentHead,
        keyword
    },

    props: {
    },

    data() {
        return {
            keywordVisible: false,
            loading: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            keywordHead: [
                {
                    enName: 'keyword',
                    name: '关键字'
                },
                {
                    enName: 'createTime',
                    name: '创建时间'
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
            keywordList: [],
            configTitle: '',
            configOptions: {}
        }
    },

    computed: {
    },

    watch: {
    },

    created() {
    },

    mounted() {
        this.queryKeywordslist()
    },

    methods: {
        handleSizeChange: function(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryKeywordslist();
        },
        handleCurrentChange: function(val) {
            this.currentPage = val;
            this.queryKeywordslist();
        },
        queryKeywordslist() {
            this.loading = true
            let reqData = {
                pageNumber: this.currentPage,
                pageSize: this.pageSize
            }
            keywordsList(reqData, {})
                .then(res => {
                    this.loading = false
                    if (res.data.code === 0) {
                        this.keywordList = res.data.data;
                        this.total = res.data.totalCount
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.loading = false
                    this.$message({
                        type: "error",
                        message: '系统异常'
                    })
                })
        },
        createKeyword() {
            this.configTitle = '增加关键字'
            this.configOptions = {
                type: 'creat',
                data: {
                    keyword: '',
                }
            }
            this.keywordVisible = true

        },
        deleteKeyword(val, type) {
            this.configTitle = '删除关键字'
            this.configOptions = {
                type: type,
                data: val
            }
            this.keywordVisible = true
        },
        updateKeyword(val, type) {
            this.configTitle = '修改关键字'
            this.configOptions = {
                type: type,
                data: val
            }
            this.keywordVisible = true
        },
        success() {
            this.queryKeywordslist()
        },
        close(val) {
            this.keywordVisible = val
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
        }
    }
}
</script>

<style scoped>
</style>
