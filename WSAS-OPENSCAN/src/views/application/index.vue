<template>
  <div class="application">
    <div v-if="hasData">
      <div class="top">
      <div class="img-con" v-if="detail.icon">
        <img :src="detail.icon" alt="" />
      </div>
      <div class="content">
        <p>{{ detail.appName }}</p>
        <div>{{ detail.desc }}</div>
      </div>
    </div>
    <div class="center">
      <p class="title">合约列表 <img v-if="false" src="../../assets/images/find.png" alt="" /></p>
      <div class="tableContent">
        <el-table size="small" v-loading="loadingFlag" :data="tableData" :border="false" class="table-wrap">

          <el-table-column label="合约地址" prop="hy_add" show-overflow-tooltip>
            <template v-slot="scope">
              <div class="addr" @click="openContractPage(scope.row.contractAddress)">{{ scope.row.contractAddress }} <img  @click.stop="copyData(scope.row.contractAddress, '合约地址复制成功')"  src="../../assets/images/copy.png" alt="" /></div>
            </template>
          </el-table-column>
          <el-table-column label="合约类型" prop="contractType" show-overflow-tooltip>
            <template v-slot="scope">
              <div>{{ scope.row.contractType }}</div>
            </template>
          </el-table-column>
        </el-table>
        <we-pagination :total="totalCount" :page_offset="pageData.pageNumber" :page_size="pageData.pageSize" @paginationChange="paginationHandle"></we-pagination>
      </div>
    </div>
    </div>
    <empty v-else @reload="init"></empty>
  </div>
</template>
<script>
import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
import wePagination from '@/components/wePagination.vue'
import { appManageServer } from 'Api'
import { copy } from 'Utils'
import empty from '@/components/empty.vue'
export default {
  name: 'groupManage',
  mixins: [tableHeightHandle],
  props: {},
  components: {
    wePagination,
    empty
  },
  data () {
    return {
      tableData: [],
      pageData: {
        pageNumber: 1,
        pageSize: 10
      },
      detail: {},
      chainId: '',
      totalCount: 0,
      appId: '',
      loadingFlag: false,
      hasData: true
    }
  },
  created () {
    this.init()
  },
  methods: {
    init() {
      const { appId, chainId } = this.$route.query
      this.chainId = chainId
      this.appId = appId
      appId && this.getAppInfo({ appId })
      appId && this.getContractList()
    },
    copyData (str, msg) {
      copy(str, msg)
    },
    // 分页切换
    paginationHandle (pageData) {
      this.pageData = {
        pageNumber: pageData.page_offset,
        pageSize: pageData.page_size
      }
      this.getContractList()
    },
    openContractPage (contractAddress) {
      const { appId, chainId } = this
      this.$router.push({
        path: 'contract',
        query: {
          contractAddress,
          appId,
          chainId
        }
      })
    },
    async getContractList () {
      const { appId } = this
      const { pageNumber, pageSize } = this.pageData
      const paramsHandle = { pageSize, pageNumber, appId }
      this.loadingFlag = true
      const res = await appManageServer.getContractListByAppId({ ...paramsHandle })
      this.loadingFlag = false
      if (res && res.code === 0) {
        this.tableData = res.data
        this.totalCount = res.totalCount
      }
    },
    async getAppInfo (params) {
      const res = await appManageServer.getAppInfo(params)
      console.log(res)
      if (res && res.code === 0 && res.data) {
        this.detail = { ...res.data }
        this.hasData = true
      } else {
        this.hasData = false
      }
    }
  }
}
</script>
<style lang="less" scoped>
.application {
  height: 100%;
  div.top {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    padding: 48px 0;
    .img-con{
      width: 72px;
      height: auto;
      margin-right: 24px;
    }
    img {
      width: 72px;
      height: auto;
    }
    div.content {
      flex: 1;
      p {
        text-align: center;
        font-size: 22px;
        font-weight: 500;
        font-family: PingFangSC-Medium;
        text-align: left;
        margin-bottom: 10px;
      }
      div {
        font-size: 14px;
        line-height: 22px;
        color: #525660;
      }
    }
  }
  div.center {
    width: 1200px;
    margin: 0 auto;
    padding-bottom: 50px;
    background-color: white;
    padding: 24px;
    box-sizing: border-box;
    box-shadow: 0px 0px 20px 0px #1e53a414;
    p.title {
      font-size: 16px;
      color: #262a32;
      padding-bottom: 24px;
      font-weight: 500;
      font-family: PingFangSC-Medium;
      img {
        float: right;
        width: 20px;
        height: auto;
        cursor: pointer;
      }
    }
    div.addr {
      color: #215ed9;
      font-size: 14px;
      cursor: pointer;
      img {
        width: 14px;
        height: auto;
        transform: translateY(3px);
        cursor: pointer;
      }
    }
  }
}
</style>
