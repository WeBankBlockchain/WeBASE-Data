<template>
  <div class="application">
    <div class="top">
      <p>合约数据验证中心</p>
    </div>
    <div class="center">
      <div class="title">请输入合约ABI</div>
      <textarea></textarea>
      <div class="an">
        <el-button type="primary">解析</el-button>
      </div>
      <div class="title">请输入合约ABI</div>
      <div class="box">
        <p class="sub-title">函数名</p>
        <el-select style="width: 100%" v-model="value" placeholder="请选择函数">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"> </el-option>
        </el-select>
      </div>
      <div class="box">
        <p class="sub-title">参数</p>
        <div class="params" v-for="item in paramsList" :key="item">
          <el-select class="sel" v-model="value" placeholder="请选择参数类型">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"> </el-option>
          </el-select>
          <el-input class="input" v-model="input" placeholder="请输入内容"></el-input>
          <el-button class="btn" type="text">删除</el-button>
        </div>
        <div class="an">
          <el-button type="primary">call</el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
export default {
  name: 'groupManage',
  mixins: [tableHeightHandle],
  props: {},
  components: {},
  data () {
    return {
      tableData: [
        {
          hy_name: 'ssad',
          hy_add: 'sadsadsa'
        },
        {
          hy_name: 'ssad',
          hy_add: 'sadsadsa'
        },
        {
          hy_name: 'ssad',
          hy_add: 'sadsadsa'
        },
        {
          hy_name: 'ssad',
          hy_add: 'sadsadsa'
        }
      ],
      pageData: {
        page_offset: 0,
        page_size: 10
      },
      keys: '',
      options: [
        {
          value: '选项1',
          label: '黄金糕'
        },
        {
          value: '选项2',
          label: '双皮奶'
        }
      ],
      paramsList: [{}, {}]
    }
  },
  created () {},
  methods: {
    paginationHandle () {}
  }
}
</script>
<style lang="less" scoped>
.application {
  height: 100%;
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
    width: 720px;
    margin: 0 auto;
    padding-bottom: 50px;
    box-sizing: border-box;
    div.title {
      font-size: 16px;
      color: #262a32;
      margin-bottom: 36px;
      font-weight: 600;
      border-left: 3px solid #215ed9;
      padding-left: 8px;
    }
    textarea {
      width: 720px;
      height: 150px;
      padding: 10px;
    }
    div.an {
      margin-top: 24px;
      margin-bottom: 32px;
    }
    div.box {
      margin-bottom: 24px;
    }
    p.sub-title {
      font-size: 14px;
      margin-bottom: 12px;
    }
    div.params {
      color: #215ed9;
      font-size: 14px;
      display: flex;
      margin-bottom: 12px;
      .sel {
        width: 204px;
      }
      .btn{
        margin-left: 20px;
      }
    }
  }
}
</style>
