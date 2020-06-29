<template>
    <div>
        <content-head :headTitle="'区块链'"></content-head>
        <div class="module-wrapper">
            <div class="search-table">
                
                <el-table :data="chainList" class="search-table-content" v-loading="loading">
                    <el-table-column v-for="head in chainHead" :label="head.name" :key="head.enName" :prop="head.enName" :width="head.width" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <template v-if="head.enName=='chainName'">
                                <span @click="link(scope.row.chainId)" class="link">{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else-if="head.enName=='chainId'">
                                <span @click="link(scope.row.chainId)" class="link">{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else-if="head.enName=='chainType'">
                                <span >{{scope.row[head.enName] | type}}</span>
                            </template>
                            <template v-else>
                                <span>{{scope.row[head.enName]}}</span>
                            </template>
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
import { chainAll } from "@/util/api"
export default {
    name: 'Chain',

    components: {
        contentHead,
    },
    filters: {
        type(val) {
            if (val) {
                return "sm2/sm3"
            } else {
                return "secp256k1/sha3"
            }
        },
    },
    props: {
    },

    data() {
        return {
            loading: false,
            currentPage: 1,
            pageSize: 10,
            total: 0,
            chainList: []
        }
    },

    computed: {
        chainHead() {
            let data = [
                {
                    enName: "chainName",
                    name: "区块链名称",
                    width: ''
                },
                {
                    enName: "chainId",
                    name: "区块链编号",
                    width: ''
                },
                {
                    enName: "chainType",
                    name: "区块链类型",
                    width: ''
                },
                {
                    enName: "createTime",
                    name: "创建时间",
                    width: ''
                }
            ];
            return data
        }
    },

    watch: {

    },
    created() {
    },
    mounted() {
        this.queryChainAll()
    },
    methods: {
        queryChainAll() {
            
            chainAll()
                .then(res => {
                    if (res.data.code === 0) {
                        this.chainList = res.data.data;
                    } else {
                        this.$message({
                            type: "error",
                            message: this.$chooseLang(res.data.code)
                        })
                    }
                })
                .catch(error => {
                    this.loading = false;
                    this.$message({
                        type: "error",
                        message: '系统异常'
                    })
                })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.queryChainAll();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.queryChainAll();
        },
        link(val) {
            this.$router.push({
                path: '/group',
                query: {
                    chainId: val
                }
            })
        }

    }
}
</script>

<style scoped>
.search-part-left-btn {
    border-radius: 20px;
}
</style>
