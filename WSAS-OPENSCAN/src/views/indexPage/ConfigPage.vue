<template>
  <div class="config">
    <el-form ref="configForm" :rules="rules" :model="configForm" label-width="130px">
      <el-form-item label="元豆合约地址:" prop="bac20Address">
        <el-input v-model="configForm.bac20Address" placeholder="请输入元豆合约地址"></el-input>
      </el-form-item>
      <el-form-item label="僵尸账户地址:" prop="zombieAddress">
        <el-input type="textarea" spellcheck="false" v-model="configForm.zombieAddress" placeholder="请输入僵尸账户地址"></el-input>
      </el-form-item>
      <el-form-item label="管理员账号地址:" prop="manageAddress">
        <el-input type="textarea" spellcheck="false" v-model="configForm.manageAddress" placeholder="请输入管理员账号地址"></el-input>
      </el-form-item>
    </el-form>
    <div class="btn">
      <div class="cancel" @click="$emit('close')">取消</div>
      <div class="sure" @click="saveConfig('configForm')">保存配置</div>
    </div>
  </div>
</template>

<script>
import { config } from 'Api'

export default {
  name: 'ConfigPage',
  props: ['configData'],
  data() {
    return {
      configForm: {
        bac20Address: '',
        zombieAddress: '',
        manageAddress: ''
      },
      rules: {
        bac20Address: [
          { required: true, message: '合约地址为空', trigger: 'blur' },
          { pattern: /^0x[a-zA-Z0-9]{40}$/, message: '合约地址的长度为42位，以“0x”开头。', trigger: 'blur' }
        ],
        zombieAddress: [
          { required: true, message: '僵尸账户地址为空', trigger: 'blur' },
          { pattern: /^0x[a-zA-Z0-9]{40}(;0x[a-zA-Z0-9]{40})*$/, message: '僵尸账户地址的长度为42位，以“0x”开头。多个僵尸账户地址以“;”分隔', trigger: 'blur' }
        ],
        manageAddress: [
          { required: true, message: '管理员账号地址为空', trigger: 'blur' },
          { pattern: /^0x[a-zA-Z0-9]{40}(;0x[a-zA-Z0-9]{40})*$/, message: '僵尸账户地址的长度为42位，以“0x”开头。多个管理员账号地址以“;”分隔', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.configForm = JSON.parse(JSON.stringify(this.configData))
  },
  methods: {
    saveConfig(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.sureSave()
          // this.$emit('success', this.configForm)
        }
      })
    },
    async sureSave() {
      const res = await config.saveConfig(this.configForm)
      if (res && res.code === 0) {
        this.$message.success('保存成功')
        this.$emit('success')
      }
    }
  }
}
</script>
<style scoped lang="less">
.config {
  width: 100%;
  text-align: right;
  padding-right: 0;
  padding-bottom: 0;
  .btn {
    width: 100%;
    margin-top: 24px;
    text-align: center;
    margin: 0 auto;
    .cancel {
      width: 88px;
      border-radius: 4px;
      border: 1px solid var(--grey5, #e0e4ed);
      background: #fff;
      padding: 5px 16px;
      color: var(--grey1, #262a32);
      /* text 14 R */
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 400;
      line-height: 22px;
      display: inline-block;
      cursor: pointer;
    }
    .sure {
      width: 88px;
      padding: 5px 16px;
      border-radius: 4px;
      background: var(--main-active, #215ed9);
      color: #fff;
      /* text 14 R */
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 400;
      line-height: 22px;
      display: inline-block;
      margin-left: 12px;
      cursor: pointer;
    }
  }
  /deep/ .el-form-item__label {
    line-height: 32px;
  }
  /deep/ .el-form-item__content {
    line-height: 32px;
    .el-input__inner {
      height: 32px;
      line-height: 32px;
    }
  }
  /deep/ .el-textarea__inner {
    height: 90px;
    font-family: Arial;
  }
}
</style>
