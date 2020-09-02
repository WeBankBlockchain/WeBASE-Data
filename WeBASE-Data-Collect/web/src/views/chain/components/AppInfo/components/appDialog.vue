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
        <el-form :model="appForm" :rules="rules" ref="appForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="应用名" prop="appName" style="width: 300px;">
                <el-input v-model="appForm.appName" placeholder="请输入应用名"></el-input>
            </el-form-item>
            <el-form-item label="应用版本" prop="appVersion" style="width: 300px;">
                <el-input v-model="appForm.appVersion" placeholder="请输入应用版本"></el-input>
            </el-form-item>
            <el-form-item label="应用概要" prop="appSummary" style="width: 300px;">
                <el-input v-model="appForm.appSummary" type="textarea" placeholder="请输入应用概要"></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="description" style="width: 300px;">
                <el-input v-model="appForm.description" type="textarea" placeholder="请输入应用描述"></el-input>
            </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button @click="modelClose">取消</el-button>
            <el-button type="primary" @click="submit('appForm')" :loading="loading">确定</el-button>
        </div>
    </div>
</template>

<script>
import { modifyGroups } from "@/util/api";
export default {
    name: "appDialog",
    props: {
        appDialogOptions: {
            type: Object
        }
    },
    watch: {
        "appDialogOptions.type": {
            handler(val) {
                this.type = val;
                switch (val) {
                    case "modify":
                        this.appForm = {
                            appName: this.appDialogOptions.data["appName"],
                            description: this.appDialogOptions.data["description"],
                            appVersion: this.appDialogOptions.data["appVersion"],
                            appSummary: this.appDialogOptions.data["appSummary"],
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
            type: this.appDialogOptions.type,
            loading: false,
            appForm: {},
            roleList: [],
        };
    },
    computed: {
        rules() {
            var validateVersion = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请输入版本号'));
                }else if (!/^\d+\.\d+\.\d+$/.test(value)) {
                    callback(new Error('请正确输入版本号例如0.0.1'));
                }else {
                    callback()
                }
            }
            let data = {
                appName: [
                    {
                        required: true,
                        message: '请输入应用名',
                        trigger: "blur"
                    }
                ],
                appVersion: [
                    { validator: validateVersion, trigger: 'blur',required: true, }
                ],
                appSummary: [
                    {
                        required: true,
                        message: '请输入应用概要',
                        trigger: "blur"
                    }
                ],
                description: [
                    {
                        required: true,
                        message: '请输入应用描述',
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
console.log(1111)
                if (valid) {
                    
                    this.loading = true;
                    this.getAllInfo();
                } else {
                    return false;
                }
            });
        },
        getAllInfo: function () {
            let type = this.type;
            console.log(type)
            switch (type) {

                case "modify":
                    this.getModifyAppInfo();
                    break;

            }
        },
        getModifyAppInfo: function () {
            let reqData = {
                chainId: this.appDialogOptions.data.chainId,
                groupId: this.appDialogOptions.data.groupId,
                appName: this.appForm.appName,
                appSummary: this.appForm.appSummary,
                appVersion: this.appForm.appVersion,
                description: this.appForm.description,
            };

            modifyGroups(reqData, {})
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
