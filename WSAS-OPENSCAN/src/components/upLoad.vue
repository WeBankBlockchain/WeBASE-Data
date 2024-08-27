<template>
  <el-upload
    class="upload-area"
    :multiple="false"
    :limit="limitNumber"
    :action="uploadUrl"
    :headers="headers"
    :on-remove="onRemove"
    :on-success="handleSuccess"
    :on-error="handleError"
    :before-upload="beforeUpload"
    :on-exceed="handleExceed"
    ref="upload"
    :file-list="fileList"
  >
    <el-button size="small" type="primary">点击上传</el-button>
    <div v-if="tips" slot="tip" class="el-upload__tip">{{ tips }}</div>
  </el-upload>
</template>

<script>
import { uploadUrl } from 'Utils/config.js'
import { mapGetters } from 'vuex'
export default {
  name: 'weUpload',
  model: {
    prop: 'value'
  },
  props: {
    value: {
      type: String,
      default: ''
    },
    tips: {
      type: String,
      default: ''
    },
    beforeUpload: {
      type: Function,
      default: () => {}
    }
  },
  data () {
    return {
      uploadUrl,
      fileList: [],
      limitNumber: 1
    }
  },
  created () {
    this.init()
  },
  methods: {
    init () {
      // 回显初始化
      if (this.value) {
        const [name, url] = this.value.split(',')
        this.fileList = [{ name, url }]
      }
    },
    handleSuccess (res) {
      const { data, code } = res
      if (code === '0') {
        const { file_url, file_name } = data
        this.$emit('input', file_name + ',' + file_url)
      } else {
        this.fileList = [...this.fileList]
        this.$message.error(res.msg || '上传资源失败')
      }
    },
    onRemove () {
      this.$emit('input', '')
    },
    handleError (error) {
      this.$message.error(error.msg || '上传资源失败')
    },
    handleExceed (files, fileList) {
      this.$message.error(`当前限制选择${this.limitNumber}个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    }
  },
  computed: {
    ...mapGetters(['authorization']),
    headers () {
      return {
        Authorization: this.authorization
      }
    }
  },
  watch: {
    value (newValue) {
      if (newValue) {
        const [name, url] = newValue.split(',')
        this.fileList = [{ name, url }]
      }
    }
  }
}
</script>

<style scoped lang="less">
::v-deep .el-upload-list {
  width: 400px;
}
::v-deep .el-upload-list__item.is-success .el-upload-list__item-status-label {
  right: -55px;
  display: none;
}
::v-deep .el-upload-list__item .el-icon-close {
  display: inline-block;
}
::v-deep .el-icon-close:before {
  content: '删除';
  color: #777;
  position: absolute;
  right: -40px;
}
::v-deep .el-icon-close-tip {
  display: none;
  visibility: hidden;
}
</style>
