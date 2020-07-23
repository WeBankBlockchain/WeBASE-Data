<template>
    <div>
        <el-table :data="nodeList" tooltip-effect="dark">
            <el-table-column v-for="head in nodeHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                <template slot-scope="scope">
                    <template>
                        <span>{{scope.row[head.enName]}}</span>
                    </template>
                </template>

            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import { chainNodes } from "@/util/api";
export default {
    name: 'NodeInfo',

    components: {

    },

    props: {
        chainId: {
            type: Number
        }
    },

    data() {
        return {
            nodeHead: [
                {
                    enName: 'nodeId',
                    name: '节点编号'
                },
                {
                    enName: 'orgName',
                    name: '机构名称'
                },
                {
                    enName: 'description',
                    name: '备注'
                }
            ],
            nodeList: [],
            currentPage: 1,
            pageSize: 100,
            total: 0,
        }
    },

    computed: {
    },

    watch: {
    },

    created() {
    },

    mounted() {
        this.queryChainNodes()
    },

    methods: {

        queryChainNodes() {
            let reqData = {
                chainId: this.chainId,
                pageNumber: this.currentPage,
                pageSize: this.pageSize
            }
            chainNodes(reqData, {})
                .then(res => {
                    if (res.data.code === 0) {
                        var list = res.data.data;
                        this.nodeList = list;
                    } else {
                        this.$message({
                            type: "error",
                            message: this.$chooseLang(res.data.code)
                        })
                    }
                })
                .catch(err => {
                    this.$message({
                        type: "error",
                        message: "系统错误"
                    })
                })
        }
    }
}
</script>

<style scoped>
</style>

