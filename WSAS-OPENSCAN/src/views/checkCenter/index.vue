<template>
  <div class="application">
    <div class="top">
      <p>核验中心</p>
    </div>
    <div class="center">
      <ul>
        <li @click="handleChange('文件校验')" :class="active === '文件校验' ? 'active' : ''">文件校验</li>
        <li @click="handleChange('哈希校验')" :class="active === '哈希校验' ? 'active' : ''">哈希校验</li>
      </ul>
      <div class="box" v-if="active === '文件校验'">
        <el-upload class="upload-demo" :inline="true" action="" ref="upload" :http-request="uploadFile" :show-file-list="false">
          <div class="chose" size="small" type="primary"><img class="up" slot="trigger" src="../../assets/images/file.png" alt="" /> {{ fileName }}</div>
        </el-upload>
        <el-button class="btn" type="primary" @click="fileCheck">开始校验</el-button>
      </div>
      <div class="box" v-if="active === '哈希校验'">
        <el-form :model="form" ref="form" :rules="rules">
          <el-form-item prop="hash" class="hash">
            <el-input class="input" v-model="form.hash" placeholder="请输入哈希值"></el-input>
          </el-form-item>
          <el-button class="btn" @click="hashCheck" type="primary">开始校验</el-button>
        </el-form>
      </div>
    </div>
    <el-dialog width="514px" title="" :visible.sync="dialogVisible">
      <div class="result-container">
        <img v-if="result" src="../../assets/images/success.png" alt="" />
        <img v-else src="../../assets/images/error.png" alt="" />
        <p>{{ result ? '核验通过' : '核验未通过' }}</p>
        <el-button type="primary" @click="closeMoadal">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
import { chainManageServer } from 'Api'
export default {
  name: 'groupManage',
  mixins: [tableHeightHandle],
  props: {},
  components: {},
  data() {
    return {
      hash: '',
      activeName: 'first',
      chainId: '',
      fileName: '请选择文件',
      file: null,
      active: '文件校验',
      dialogVisible: false,
      result: true,
      reg: /^0x[a-z0-9]{64}$/,
      form: {
        hash: ''
      },
      rules: {
        hash: [
          {
            validator: (rule, value, callback) => {
              if (!/^0x[a-z0-9]{64}$/.test(value)) {
                callback(new Error('请正确输入哈希值'))
              } else {
                callback()
              }
            },
            required: true,
            trigger: 'blur'
          }
        ]
      }
    }
  },
  created() {
    const { chainId } = this.$route.query
    this.chainId = chainId
  },
  methods: {
    // 截取指定长度的字符
    truncateText(text, length) {
      const textArr = text.split('.')
      let truncatedText = ''
      let currentLength = 0
      for (let i = 0; i < textArr[0].length; i++) {
        const char = textArr[0][i]
        // 判断字符是否为中文
        if (/[\u4e00-\u9fa5]/.test(char)) {
          // 如果是中文，长度加2
          currentLength += 2
        } else {
          // 如果是英文或其他字符，长度加1
          currentLength += 1
        }
        // 判断当前长度是否超过目标长度
        if (currentLength <= length) {
          truncatedText += char
        } else {
          break
        }
      }
      if (currentLength <= 20) {
        return truncatedText + '.' + textArr[1]
      } else {
        return truncatedText + '...' + '.' + textArr[1]
      }
    },
    closeMoadal() {
      this.dialogVisible = false
    },
    handleChange(activeString) {
      this.active = activeString
    },
    uploadFile({ file }) {
      console.log(file)
      const { name = '' } = file
      this.file = file
      this.fileName = this.truncateText(name, 20)
      this.$message.success('文件上传成功')
    },
    async hashCheck() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.getHashCheck()
        }
      })
    },
    async getHashCheck() {
      const { chainId } = this
      const { hash } = this.form
      const res = await chainManageServer.hashCheck({ hash, chainId })
      if (res && res.code === 0) {
        const { data } = res
        this.result = data
        this.dialogVisible = true
      }
    },
    async fileCheck() {
      const { file, chainId } = this
      if (!file) {
        this.$message.error('请上传文件')
        return
      }
      const res = await chainManageServer.fileCheck({ file, chainId })
      if (res && res.code === 0) {
        const { data } = res
        this.result = data
        this.dialogVisible = true
      }
    }
  }
}
</script>
<style lang="less" scoped>
.application {
  height: 100%;
  .result-container {
    text-align: center;
    img {
      width: 32px;
      height: auto;
      margin-bottom: 10px;
    }
    p {
      color: #262a32;
      margin-bottom: 24px;
      font-weight: 600;
    }
    .el-button--primary {
      background-color: #215ed9;
    }
  }
  div.top {
    width: 720px;
    margin: 0 auto;
    display: flex;
    padding: 48px 0;
    p {
      width: 720px;
      margin: 0 auto;
      text-align: center;
      font-size: 20px;
      font-weight: 600;
      line-height: 26px;
    }
  }
  div.center {
    width: 454px;
    height: 300px;
    margin: 0 auto;
    background-color: white;
    box-sizing: border-box;
    .input {
      width: 296px;
      margin-right: 4px;
    }
    ul {
      display: flex;
      height: 56px;
      width: 100%;
      border-bottom: 1px solid #eee;
      padding: 0 20px;
      box-sizing: border-box;
      li {
        color: #525660;
        width: 50%;
        line-height: 56px;
        text-align: center;
        font-weight: 600;
        cursor: pointer;
      }
      li.active {
        color: #215ed9;
        border-bottom: 1px solid #215ed9;
        cursor: default;
      }
    }
    .upload-demo {
      width: 296px;
      margin-right: 4px;
    }
    div.box {
      padding: 27px;
      padding-top: 80px;
      box-sizing: border-box;
      display: flex;
    }
    div.chose {
      width: 296px;
      height: 38px;
      border: 1px solid #e0e4ed;
      display: inline-block;
      text-align: left;
      color: #787b84;
      font-size: 14px;
      line-height: 38px;
      transform: translateY(2px);
      padding-left: 6px;
      img {
        width: 22px;
        height: auto;
        cursor: pointer;
        vertical-align: middle;
        margin-right: 4px;
        transform: translateY(-2px);
      }
    }
    div.btn {
      display: inline-block;
      margin-left: 10px;
    }
    .el-button--primary {
      background-color: #215ed9;
    }
    .el-form-item{
      display: inline-block;
    }
    ::v-deep .el-form-item__error{
      margin-top: 6px;
    }
  }
}
</style>
