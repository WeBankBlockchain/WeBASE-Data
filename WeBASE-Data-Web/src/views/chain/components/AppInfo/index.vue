<template>
    <div>
        <el-table :data="appList" tooltip-effect="dark">
            <el-table-column v-for="head in appHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                <template slot-scope="scope">
                    <template v-if=" head.enName!='operate'">
                        <template>
                            <span class="link" @click="link(scope.row, 'contractName')" v-if="head.enName=='appName'">{{scope.row[head.enName]}}</span>
                            <span v-else>{{scope.row[head.enName]}}</span>
                        </template>
                    </template>
                    <template v-else>
                        <el-button type="text" size="small" @click="disposeKeyword(scope.row,'modify')">介入处理</el-button>
                    </template>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog :visible.sync="interventionVisible" title="处理意见" width="433px" :append-to-body="true" :center="true" class="dialog-wrapper" v-if="interventionVisible">
            <el-form :model="interventionForm" :rules="rules" ref="interventionForm" label-width="70px" class="demo-ruleForm">
                <el-form-item label="意见" prop="content" style="width: 380px;">
                    <el-input v-model="interventionForm.content" placeholder="请输入处理意见" type="textarea"></el-input>
                </el-form-item>
            </el-form>
            <div class="dialog-footer text-right">
                <el-button @click="modelClose">取消</el-button>
                <el-button type="primary" @click="submit('interventionForm')">添加到告警列表</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { groupList, appAuditAdd } from "@/util/api";
export default {
    name: 'AppInfo',

    components: {

    },

    props: {
        chainId: {
            type: Number
        },
        chainName: {
            type: String
        }

    },

    data() {
        return {
            appHead: [
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
                    name: "操作"
                }
            ],
            appList: [],
            interventionVisible: false,
            interventionForm: {
                content: ''
            },
            rowData: {}
        }
    },

    computed: {
        rules() {
            let data = {
                content: [
                    {
                        required: true,
                        message: '请输入处理意见',
                        trigger: "blur"
                    }
                ]
            }
            return data
        }
    },

    watch: {
    },

    created() {
    },

    mounted() {
        this.queryGroupList()
    },

    methods: {
        queryGroupList() {
            let param = {
                chainId: this.chainId
            }
            groupList(param).then(res => {
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
        disposeKeyword(val) {
            this.rowData = val
            this.interventionVisible = true
        },
        modelClose: function () {
            this.interventionVisible = false
        },
        submit: function (formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.queryAppAuditAdd()
                } else {
                    return false;
                }
            });
        },
        queryAppAuditAdd() {
            let reqData = {
                chainId: this.rowData.chainId,
                groupId: this.rowData.groupId,
                comment: this.interventionForm.content,
                chainName: this.chainName,
                appName: this.rowData.appName,
                appVersion: this.rowData.appVersion
            };
            appAuditAdd(reqData)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '介入成功'
                        });
                        this.modelClose();
                        this.$router.push({
                            path: "/appAlarm"
                        })
                    } else {
                        this.modelClose();
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.modelClose();
                    this.$message({
                        message: '系统错误',
                        type: "error",
                        duration: 2000
                    });
                });
        },
        link(val) {
            this.$router.push({
                path: `/overview/${val.chainId}/${val.appName}/${val.groupId}`,
                query: {
                    chainName: this.chainName
                }
            })
        }
    }
}
</script>

<style scoped>
</style>
