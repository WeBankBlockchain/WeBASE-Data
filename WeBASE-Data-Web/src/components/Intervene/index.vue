<template>
    <div>
        <el-form :model="interventionForm" :rules="rules" ref="interventionForm" label-width="70px" class="demo-ruleForm">
            <el-form-item label="意见" prop="content" style="width: 380px;">
                <el-input v-model.trim="interventionForm.content" placeholder="请输入处理意见" type="textarea"></el-input>
            </el-form-item>
        </el-form>
        <div class="dialog-footer text-right">
            <el-button @click="modelClose">取消</el-button>
            <el-button type="primary" @click="submit('interventionForm')">添加到告警列表</el-button>
        </div>
    </div>
</template>

<script>
import { txAuditAdd } from "@/util/api"
export default {
    name: 'Intervene',

    components: {
    },

    props: ['rowData', 'singleSearchValue', 'type'],

    data() {
        return {
            interventionForm: {
                content: ''
            },
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
    },

    methods: {
        submit(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.queryTxAuditAdd()
                } else {
                    return false;
                }
            });
        },
        queryTxAuditAdd() {
            let reqData = {
                chainId: this.rowData.chainId,
                groupId: this.rowData.groupId,
                keyword: this.singleSearchValue,
                comment: this.interventionForm.content,
                txHash: this.rowData.transHash,
                address: this.rowData.userAddress,
                chainName: this.rowData.chainName,
                appName: this.rowData.appName,
                type: this.type
            };
            txAuditAdd(reqData)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '介入成功'
                        });
                        this.modelClose();
                        this.$router.push({
                            path: "/txAlarm"
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
        modelClose: function () {
            this.$emit('modelClose')
        },
    }
}
</script>

<style scoped>
</style>
