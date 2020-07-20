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
    <div class="login-bg" :style="{backgroundImage: 'url(' + bgLogin +')'}">
        <!-- <div class="login-lang">
            <lang-select class="right-menu-item hover-effect"></lang-select>
        </div> -->
        <div class="login">
            <div style="    height: 70px;
    line-height: 70px;
    font-size: 30px;
    font-family: fantasy;">
                <!-- <svg class="icon" aria-hidden="true">
                    <use xlink:href="#wbs-icon-WeBASE"></use>
                </svg> -->
                <!-- <p class="logo-content">xxxxxxx</p> -->
                数据运维平台
            </div>
            <div class="msg-wrapper">
                <div class="msg-error" v-show="msgError || timeout">
                    <i class="el-icon-remove"></i>
                    <span v-if="msgError">{{msgErrorContent || '登陆失败'}}</span>
                    <span v-else-if="timeout">请求超时</span>
                </div>
            </div>
            <div class="login-content">
                <template>
                    <el-form ref="loginForm" :model="loginForm" :rules="rules" class="login-form">
                        <el-form-item label="账号" prop="user">
                            <el-input v-model="loginForm.user" placeholder="请输入账号">
                            </el-input>
                        </el-form-item>
                        <el-form-item label="密码" prop="password">
                            <el-input v-model="loginForm.password" placeholder="请输入密码" show-password>
                            </el-input>
                        </el-form-item>
                        <!-- <el-form-item label="验证码" prop="vercode">
                            <div style="width: 100%;" class="ver-code">
                                <el-input style="width: 240px;" v-model="loginForm.vercode" placeholder="请输入验证码" @keyup.enter.native="submit('loginForm')">
                                </el-input>
                                <span class="codeUrlImg">
                                    <img style="width: 100%;height: 100%" :src="codeUrl" alt="" @click="changeCode()">
                                </span>
                            </div>
                        </el-form-item> -->
                    </el-form>
                </template>
            </div>
            <div>
                <el-button @click="submit('loginForm')" type="primary" class="login-submit" :loading="logining">登 录</el-button>
            </div>
        </div>
    </div>
</template>
<script>
import { login, getPictureCheckCode } from "@/util/api";
import url from "@/util/url"
import bg from "@/../static/image/banner-1.jpg";
import logo from "@/../static/image/logo-2 copy@1.5x.jpg";
import { delCookie } from '@/util/util'
const sha256 = require("js-sha256").sha256;
import langSelect from "@/components/LangSelect"
export default {
    name: "login",
    components: {
        "lang-select": langSelect
    },
    data() {
        return {
            bgLogin: bg,
            logoPng: logo,
            logining: false,
            msgError: false,
            msgErrorContent: "",
            timeout: false,
            codeUrl: url.codeUrl,
            loginForm: {
                user: "admin",
                password: "password",
                vercode: "",
            },
            authToken: null,
        };
    },
    computed: {
        rules() {
            var obj = {
                user: [{ required: true, message: '请输入账', trigger: "blur" }],
                password: [
                    { required: true, message: '请输入密码', trigger: "blur" }
                ],
                vercode: [
                    { required: true, message: '请输入验证码', trigger: "blur" }
                ]
            }
            return obj
        }
    },
    mounted() {
        // this.changeCode();
    },
    methods: {
        submit(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.logining = true;
                    // this.userLogin();
                    this.$router.push("/chain")
                } else {
                    return false;
                }
            });
        },
        changeCode() {
            this.codeUrl = "";
            this.authToken = ""
            getPictureCheckCode().then(res => {
                if (res.data.code === 0) {
                    this.codeUrl = `data:image/png;base64,${res.data.data.base64Image}`
                    this.authToken = res.data.data.token
                } else {
                    this.codeUrl = "";
                    this.authToken = ""
                    this.$message({
                        message: this.$chooseLang(res.data.code),
                        type: "error",
                        duration: 2000
                    });
                }
            }).catch(err => {
                this.codeUrl = "";
                this.authToken = ""
                this.$message({
                    message: '系统错误',
                    type: "error",
                    duration: 2000
                });
            })
        },
        userLogin(callback) {
            delCookie('JSESSIONID')
            delCookie('NODE_MGR_ACCOUNT_C')
            let reqData = {
                account: this.loginForm.user,
                accountPwd: sha256(this.loginForm.password)
            };
            let checkCode = this.loginForm.vercode
            login(reqData, checkCode, this.authToken)
                .then(res => {
                    if (res.data.code === 0) {
                        localStorage.setItem("groupName", "");
                        localStorage.setItem("groupId", "");
                        localStorage.setItem("folderList", "")
                        localStorage.setItem("user", res.data.data.account);
                        localStorage.setItem("root", res.data.data.roleName);
                        localStorage.setItem("token", res.data.data.token);
                        sessionStorage.setItem("accountStatus", res.data.data.accountStatus);
                        sessionStorage.setItem("reload", 1);
                        this.$router.push("/myWork")
                    } else {
                        this.changeCode()
                        this.msgErrorContent = this.$chooseLang(res.data.code)
                        this.msgError = true;
                        this.loginForm.password = "";
                        this.logining = false;
                    }
                })
                .catch(err => {
                    this.changeCode()
                    this.timeout = true;
                    this.loginForm.password = "";
                    this.logining = false;
                });
        },

    }
};
</script>
<style scoped>
.login-label.is-required:not(.is-no-asterisk) > .el-form-item__label:before {
    content: "" !important;
}
.login-bg {
    position: relative;
    width: 100%;
    height: 100%;
    background-repeat: no-repeat;
    background-size: 100% 100%;
}
.login {
    position: absolute;
    width: 430px;
    top: 52%;
    left: 50%;
    margin-top: -280px;
    margin-left: -201px;
    padding: 0 50px 40px 50px;
    background-color: #fff;
    border-radius: 16px;
    text-align: center;
    box-sizing: border-box;
}
.logo-content {
    position: absolute;
    width: 100%;
    top: 90px;
    left: 0;
    text-align: center;
    font-size: 12px;
    color: #979faa;
    letter-spacing: 0.02px;
}
.msg-wrapper {
    min-height: 20px;
    height: auto;
}
.msg-error {
    color: #e4393c;
}
.codeUrlImg {
    display: inline-block;
    height: 38px;
    width: 84px;
    line-height: 38px;
    border: 1px solid #dcdfe6;
    border-radius: 2px;
    vertical-align: middle;
    cursor: pointer;
    text-align: center;
}
.logo {
    width: 120px;
    padding-top: 80px;
}
.login-title {
    padding: 16px 0 0px 0;
    font-size: 20px;
    color: #2e2e2e;
    letter-spacing: 0.04px;
}
.login-label {
    position: relative;
    padding: 0 60px;
}
.login-label input {
    height: 54px;
    padding-left: 60px;
}
.login-submit {
    width: 100%;
    height: 45px;
    margin-top: 10px;
    font-size: 14px;
}
.icon {
    width: 142px;
    height: 142px;
    fill: currentColor;
    overflow: hidden;
}
.ver-code>>>.el-form-item.is-required:not(.is-no-asterisk)
    > .el-form-item__label:before {
    content: "";
}
.login-form>>>.el-form-item__label {
    display: block;
    line-height: 32px;
    float: none;
    text-align: left;
}
.login-lang {
    position: absolute;
    right: 10px;
    top: 20px;
    min-width: 160px;
}
.login-encrypt {
    position: absolute;
    right: 10px;
    top: 60px;
    min-width: 160px;
}
.login-encrypt .el-radio-button__inner {
    display: inline-block;
    width: 80px;
}
</style>
