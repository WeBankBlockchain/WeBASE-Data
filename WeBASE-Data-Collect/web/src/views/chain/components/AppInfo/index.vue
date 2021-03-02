<template>
    <div>
        <el-table :data="appList" tooltip-effect="dark">
            <el-table-column v-for="head in appHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                <template slot-scope="scope">
                    <template v-if="head.enName!='operate'">
                        <span>{{scope.row[head.enName]}}</span>
                    </template>
                    <template v-else>
                        <el-button type="text" size="small" @click="modifyApp(scope.row,'modify')">修改</el-button>
                    </template>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog :visible.sync="appDialogVisible" :title="appDialogTitle" width="433px" :append-to-body="true" :center="true" class="dialog-wrapper" v-if="appDialogVisible">
            <app-dialog :appDialogOptions="appDialogOptions" @success="success" @close="close"></app-dialog>
        </el-dialog>
    </div>
</template>

<script>
import { getGroups } from "@/util/api";
import appDialog from "./components/appDialog";
export default {
    name: 'AppInfo',

    components: {
        appDialog
    },

    props: {
        chainId: {
            type: Number
        }   
    },

    data() {
        return {
            appHead: [
                {
                    enName: 'groupId',
                    name: '应用编号'
                },
                {
                    enName: 'appName',
                    name: '应用名称'
                },
                {
                    enName: 'appVersion',
                    name: '应用版本'
                },
                {
                    enName: 'appSummary',
                    name: '应用概要'
                },
                {
                    enName: 'description',
                    name: '描述'
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
            appList: [],
            appDialogVisible: false,
            appDialogTitle: '',
            appDialogOptions: {}
        }
    },

    computed: {
    },

    watch: {
    },

    created() {
    },

    mounted() {
        this.queryGroupList()
    },

    methods: {
        modifyApp(val, type) {
            this.appDialogTitle = '修改应用'
            this.appDialogVisible = true;
            console.log(val)
            this.appDialogOptions = {
                data: val,
                type: type
            }
        },
        success(){
            this.queryGroupList()
        },
        close(val){
            this.appDialogVisible = val
        },
        queryGroupList() {
            getGroups(this.chainId).then(res => {
                if (res.data.code === 0) {
                    var list = res.data.data;
                    this.appList = list;
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
    }
}
</script>

<style scoped>
</style>
