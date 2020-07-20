/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
<template>
    <div>
        <el-form :model="nodeForm" :rules="rules" ref="nodeForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="链编号" prop="chainId" style="width: 300px;">
                <el-input v-model="nodeForm.chainId" placeholder="请输入链编号" :disabled="nodeForm.disabled"></el-input>
            </el-form-item>
            <el-form-item label="节点编号" prop="nodeId" style="width: 300px;">
                <el-input v-model="nodeForm.nodeId" placeholder="请输入节点编号" :disabled="nodeForm.disabled"></el-input>
            </el-form-item>
            <el-form-item label="机构名" prop="orgName" style="width: 300px;">
                <el-input v-model="nodeForm.orgName" placeholder="请输入机构名"></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="description" style="width: 300px;">
                <el-input v-model="nodeForm.description" type="textarea" placeholder="请输入描述"></el-input>
            </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button @click="modelClose">取消</el-button>
            <el-button type="primary" @click="submit('nodeForm')" :loading="loading">确定</el-button>
        </div>
    </div>
</template>

<script>
import { modifyChainNodes } from "@/util/api";
export default {
    name: "nodeDialog",
    props: {
        nodeDialogOptions: {
            type: Object
        }
    },
    watch: {
        "nodeDialogOptions.type": {
            handler(val) {
                this.type = val;
                switch (val) {
                    case "modify":
                        this.nodeForm = {
                            chainId: this.nodeDialogOptions.data["chainId"],
                            nodeId: this.nodeDialogOptions.data["nodeId"],
                            orgName: this.nodeDialogOptions.data["orgName"],
                            description: this.nodeDialogOptions.data["description"],
                            disabled: true,
                            mDisabled: false,
                            dShow: true,
                            mShow: false
                        };
                        break;
                }
            },
            deep: true,
            immediate: true
        }
    },
    data() {
        return {
            type: this.nodeDialogOptions.type,
            loading: false,
            nodeForm: {},
            roleList: [],
        };
    },
    computed: {
        rules() {
            let data = {
                orgName: [
                    {
                        required: true,
                        message: '请输入机构',
                        trigger: "blur"
                    }
                ],
                description: [
                    {
                        required: true,
                        message: '请输入描述',
                        trigger: "blur"
                    }
                ]
            }
            return data
        }
    },
    mounted() {

    },
    methods: {
        modelClose: function () {
            this.$emit("close", false);
        },
        submit: function (formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.loading = true;
                    this.getAllAccountInfo();
                } else {
                    return false;
                }
            });
        },
        getAllAccountInfo: function () {
            let type = this.type;
            console.log(this.type)
            switch (type) {
                case "modify":
                    this.getModifyInfo();
                    break;

            }
        },
        getModifyInfo: function () {
            let reqData = {
                chainId: this.nodeDialogOptions.data.chainId,
                nodeId: this.nodeForm.nodeId,
                orgName: this.nodeForm.orgName,
                description: this.nodeForm.description
            };

            modifyChainNodes(reqData, {})
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '修改成功'
                        });
                        this.modelClose();
                        this.$emit("success");
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
    }
};
</script>

<style scoped>
.dialog-footer {
    text-align: right;
    margin-right: -5px;
    padding-bottom: 20px;
    padding-top: 12px;
}
.isNone {
    display: none;
}
.isShow {
    display: block;
}
.demo-ruleForm >>> .el-form-item__error {
    padding-top: 0;
}
</style>
