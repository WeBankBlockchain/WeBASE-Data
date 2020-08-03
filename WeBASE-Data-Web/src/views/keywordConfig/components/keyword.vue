<template>
    <div>
        <el-form :model="keywordForm" :rules="rules" ref="keywordForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="关键字" prop="keyword" style="width: 300px;">
                <el-input v-model="keywordForm.keyword" placeholder="请输入关键字" :disabled="keywordForm['disabled']" clearable></el-input>
                <!-- <el-tooltip class="item" effect="dark" content="多个关键字以英文逗号隔开" placement="top-start">
                    <i class="el-icon-info"></i>
                </el-tooltip> -->

            </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button @click="modelClose">取消</el-button>
            <el-button type="primary" @click="submit('keywordForm')" :loading="loading">确定</el-button>
        </div>
    </div>
</template>

<script>
import { keywordsAdd, keywordsUpdate, keywordsDelete } from "@/util/api";
export default {
    name: 'Keyword',

    components: {
    },

    props: {
        configOptions: {
            type: Object
        }
    },

    data() {
        return {
            type: this.configOptions.type,
            keywordForm: {},
            loading: false
        }
    },

    computed: {
        rules() {
            let data = {
                keyword: [
                    {
                        required: true,
                        message: '请输入关键字',
                        trigger: "blur"
                    }
                ]
            }
            return data
        }
    },

    watch: {
        "configOptions.type": {
            handler(val) {
                this.type = val;
                switch (val) {
                    case "creat":
                        this.keywordForm = {
                            keyword: "",
                            id: this.configOptions.data.id,
                            disabled: false,
                            mDisabled: false,
                            dShow: true,
                            mShow: true,
                        };
                        break;
                    case "delete":
                        this.keywordForm = {
                            keyword: this.configOptions.data["keyword"],
                            id: this.configOptions.data.id,
                            disabled: true,
                            mDisabled: true,
                            dShow: false,
                            emailshow: false
                        };
                        break;
                    case "modify":
                        this.keywordForm = {
                            keyword: this.configOptions.data["keyword"],
                            id: this.configOptions.data.id,
                            disabled: false,
                            mDisabled: false,
                            dShow: true,
                            mShow: false,
                        };
                        break;
                }
            },
            deep: true,
            immediate: true
        }
    },

    created() {
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
                    this.getAllKeywordInfo()
                } else {
                    return false;
                }
            });
        },
        getAllKeywordInfo: function () {
            let type = this.type;
            switch (type) {
                case "creat":
                    this.getCreatKeywordInfo();
                    break;
                case "modify":
                    this.getModifyKeywordInfo();
                    break;
                case "delete":
                    this.getDeleteKeywordInfo();
                    break;
            }
        },
        getCreatKeywordInfo() {
            let reqData = {
                keyword: this.keywordForm.keyword
            };
            keywordsAdd(reqData)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '添加成功'
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
        getModifyKeywordInfo() {
            let reqData = {
                keyword: this.keywordForm.keyword,
                id: this.keywordForm.id
            };
            
            keywordsUpdate(reqData)
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
        getDeleteKeywordInfo() {
            
            keywordsDelete(this.keywordForm.id)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.$message({
                            type: "success",
                            message: '删除成功'
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
}
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
