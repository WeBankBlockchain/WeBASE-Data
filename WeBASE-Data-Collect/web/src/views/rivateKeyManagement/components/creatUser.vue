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
    <div class="key-dialog">
        <div class="divide-line"></div>
        <el-form :model="userForm" :rules="rules" ref="userForm" label-width="142px" class="demo-ruleForm">
            <el-form-item label="用户名称" prop="name" style="width: 546px;" maxlength="12">
                <el-input v-model.trim="userForm.name" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="公钥地址" prop="address" style="width: 546px;">
                <el-input v-model.trim="userForm.address" placeholder="请输入公钥地址"></el-input>
            </el-form-item>
            <el-form-item label="用户描述" prop="explain"style="width: 546px">
                <el-input type="textarea" v-model.trim="userForm.explain" maxlength="120" placeholder="请输入用户描述"></el-input>
            </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button @click="modelClose">取 消</el-button>
            <el-button type="primary" @click="submit('userForm')" :loading="loading">确 定</el-button>
        </div>
    </div>
</template>

<script>
import { getAddUser, bindUser, userAdd } from "@/util/api";
import errcode from "@/util/errcode";

export default {
    name: "AddUser",
    data () {
        return {
            loading: false,
            userForm: {
                name: "",
                explain: "",
                address: ""
            },
            timeGranularity: "RIV"
        };
    },
    computed: {
        rules() {
            let data = {
                name: [
                    {
                        required: true,
                        message: "请输入用户名称",
                        trigger: "blur"
                    },
                    {
                        min: 1,
                        max: 12,
                        message: "长度在 1 到 12 个字符",
                        trigger: "blur"
                    }
                ],
                address: [
                    {
                        required: true,
                        message: "请输入公钥地址",
                        trigger: "blur"
                    }
                ],
                explain: [
                    {
                        required: true,
                        message: "请输入描述 ",
                        trigger: "blur"
                    }
                ]
            };
            return data
        }
    },
    methods: {
        modelClose () {
            this.userForm = Object.assign({
                name: "",
                address: "",
                explain: ""
            });
            this.$store.state.creatUserVisible = false;
        },
        submit (formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.$confirm('确认提交？', {
                        center: true
                    })
                        .then(() => {
                            this.loading = true;

                            this.queryAddUser();

                        })
                        .catch(() => {
                            this.modelClose();
                        });
                } else {
                    return false;
                }
            });
        },
        queryAddUser () {
            let reqData = {
                chainId: localStorage.getItem('chainId'),
                groupId: localStorage.getItem('groupId'),
                userName: this.userForm.name,
                address: this.userForm.address,
                description: this.userForm.explain || ""
            };
            userAdd(reqData)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.$emit("success");
                        this.$message({
                            type: "success",
                            message: this.$t("privateKey.addUserSuccess")
                        });
                        this.$emit("creatUserClose");
                        this.modelClose();
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
                    this.$message({
                        message: this.$t('text.systemError'),
                        type: "error",
                        duration: 2000
                    });
                    this.modelClose();
                });
        }
    }
};
</script>
<style scoped>
.key-dialog {
    margin-top: 10px;
}
.dialog-footer {
    text-align: right;
    margin-right: -5px;
    padding-bottom: 20px;
}
.radio-key {
    cursor: context-menu;
    font-size: 14px;
}
.base-span-key {
    margin-left: 8px;
    color: #00122c;
}
.pub-key {
    margin-left: 30px;
}
.riv-key {
    margin-left: 50px;
}

.divide-line {
    border: 1px solid #e7ebf0;
    margin-left: 30px;
    width: 514px;
    margin-top: 15px;
    margin-bottom: 25px;
}
</style>
