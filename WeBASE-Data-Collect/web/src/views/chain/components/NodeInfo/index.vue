<template>
    <div>
        <el-table :data="nodeList" tooltip-effect="dark">
            <el-table-column v-for="head in nodeHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                <template slot-scope="scope">
                    <template v-if="head.enName!='operate'">
                        <span>{{scope.row[head.enName]}}</span>
                    </template>
                    <template v-else>
                        <el-button type="text" size="small" @click="modifyNode(scope.row,'modify')">修改</el-button>
                    </template>
                </template>

            </el-table-column>
        </el-table>
        <el-dialog :visible.sync="nodeDialogVisible" :title="nodeDialogTitle" width="433px" :append-to-body="true" :center="true" class="dialog-wrapper" v-if="nodeDialogVisible">
            <node-dialog :nodeDialogOptions="nodeDialogOptions" @success="success" @close="close"></node-dialog>
        </el-dialog>
    </div>
</template>

<script>
import { chainNodes } from "@/util/api";
import nodeDialog from "./components/nodeDialog";
export default {
    name: 'NodeInfo',

    components: {
        nodeDialog
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
                },
                
                {
                    enName: "operate",
                    name: '操作'
                }
            ],
            nodeList: [],
            nodeDialogVisible: false,
            nodeDialogTitle: '',
            nodeDialogOptions: {},
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
        modifyNode(val, type) {
            this.nodeDialogTitle = '修改节点'
            this.nodeDialogVisible = true;
            this.nodeDialogOptions = {
                data: val,
                type: type
            }
        },
        success() {
            this.queryChainNodes()
        },
        close(val) {
            this.nodeDialogVisible = val
        },
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

