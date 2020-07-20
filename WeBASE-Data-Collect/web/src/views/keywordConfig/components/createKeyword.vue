<template>
    <div>
        <el-form :model="keywordForm" :rules="rules" ref="keywordForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="关键字" prop="keyword" style="width: 300px;">
                <el-input v-model="keywordForm.keyword" placeholder="请输入关键字"></el-input>
                <!-- <el-tooltip class="item" effect="dark" content="多个关键字以英文逗号隔开" placement="top-start">
                    <i class="el-icon-info"></i>
                </el-tooltip> -->

            </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button @click="modelClose">取消</el-button>
            <el-button type="primary" @click="submit('keywordForm')">确定</el-button>
        </div>
    </div>
</template>

<script>
export default {
    name: 'createKeyword',

    components: {
    },

    props: {
    },

    data() {
        return {
            keywordForm: {
                keyword: ''
            }
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

                } else {
                    return false;
                }
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
