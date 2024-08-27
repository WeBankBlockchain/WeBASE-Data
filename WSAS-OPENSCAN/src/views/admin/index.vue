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
        <div class="title">
          管理员列表 <img v-if="false" src="../../assets/images/find.png" alt="" />
          <span class="TotalSupply">发放量:</span> {{TotalSupply}}
          <div class="serch-container">
            <el-input style="width: 500px" v-model="userAddress" placeholder="搜索管理员地址"></el-input>
            <img @click="serch" src="../../assets/images/serch.png" alt="" />
          </div>
        </div>
        <div class="tableContent">
          <el-table size="small" v-loading="loadingFlag" :data="tableData" :border="false" class="table-wrap">
            <el-table-column label="管理员地址" prop="userAddress" show-overflow-tooltip>
              <template v-slot="scope">
                <div class="addr">{{ scope.row.userAddress }} <img @click.stop="copyData(scope.row.userAddress, '管理员地址复制成功')" src="../../assets/images/copy.png" alt="" /></div>
              </template>
            </el-table-column>
            <el-table-column label="操作" prop="contractType" show-overflow-tooltip>
              <template v-slot="scope">
                <span class="operate" @click="queryRecord(scope.row)">查询流水 </span>
                <span class="operate" @click="queryFlow(scope.row)">发放和销毁流水</span>
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
import { appManageServer, config } from 'Api'
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
  data() {
    return {
      tableData: [
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        },
        {
          userAddress: '0xab92b01394962bd41033e0be6a70f1394396956b'
        }
      ],
      pageData: {
        pageNumber: 1,
        pageSize: 10
      },
      detail: {},
      chainId: '',
      totalCount: 0,
      appId: '',
      loadingFlag: false,
      hasData: true,
      userAddress: '',
      TotalSupply: ''
    }
  },
  created() {
    // this.init()
    this.getUserList()
    this.getTotalSupplyData()
  },
  methods: {
    async getUserList() {
      const { pageNumber, pageSize } = this.pageData
      const paramsHandle = { pageSize, pageNumber, userAddress: this.userAddress }
      const res = await config.getAdminListData({ ...paramsHandle })
      if (res && res.code === 0) {
        this.tableData = res.data.map((item) => {
          return { userAddress: item }
        })
        this.totalCount = res.totalCount
      }
    },
    async getTotalSupplyData() {
      const res = await config.getTotalSupply()
      if (res && res.code === 0) {
        this.TotalSupply = res.data
      }
    },
    init() {
      const { appId, chainId } = this.$route.query
      this.chainId = chainId
      this.appId = appId
      appId && this.getAppInfo({ appId })
      appId && this.getContractList()
    },
    copyData(str, msg) {
      copy(str, msg)
    },
    // 分页切换
    paginationHandle(pageData) {
      this.pageData = {
        pageNumber: pageData.page_offset,
        pageSize: pageData.page_size
      }
      this.getUserList()
    },
    openContractPage(contractAddress) {
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
    async getContractList() {
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
    async getAppInfo(params) {
      const res = await appManageServer.getAppInfo(params)
      console.log(res)
      if (res && res.code === 0 && res.data) {
        this.detail = { ...res.data }
        this.hasData = true
      } else {
        this.hasData = false
      }
    },
    queryRecord(row) {
      this.$router.push({ path: 'queryTransFlow', query: { userAddress: row.userAddress } })
    },
    queryFlow(row) {
      this.$router.push({ path: 'queryFlow', query: { userAddress: row.userAddress } })
    },
    async serch() {
      // const { userAddress } = this
      // if (!userAddress) {
      //   this.$message.info('请输入查询内容')
      //   return
      // }
      this.pageData = {
        pageNumber: 1,
        pageSize: 10
      }
      this.getUserList()
      // const res = await appManageServer.serchByKey({ input, chainId })
      // if (res && res.code === 0) {
      //   const { data } = res
      //   if (data) {
      //     if (data.number !== undefined) {
      //       this.openBlockPage(data.number)
      //     } else {
      //       this.openTransPage(data.hash)
      //     }
      //   } else {
      //     this.$router.push({ path: 'notFound', query: { chainId } })
      //   }
      // }
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
    padding: 11px 0;
    .img-con {
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
    div.title {
      padding-bottom: 30px;
      color: var(--grey1, #262a32);
      /* menu 16 M */
      font-family: PingFang SC;
      font-size: 16px;
      font-style: normal;
      font-weight: 500;
      line-height: 24px; /* 150% */
      .TotalSupply {
        margin-left: 20px;
      }
      div.serch-container {
        display: flex;
        float: right;
        img {
          float: right;
          width: 32px;
          height: 32px;
          cursor: pointer;
        }
        ::v-deep .el-input__inner {
          height: 32px;
          line-height: 32px;
          border-top-right-radius: 0;
          border-bottom-right-radius: 0;
        }
      }
    }
    div.addr {
      // color: #215ed9;
      font-size: 14px;
      cursor: pointer;
      color: var(--grey1, #262a32);
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 400;
      line-height: 22px;
      img {
        width: 14px;
        height: auto;
        transform: translateY(3px);
        cursor: pointer;
      }
    }
    .operate {
      cursor: pointer;
      color: #215ed9;
      color: #215ed9;
      text-overflow: ellipsis;
      /* text 14 R */
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 400;
      line-height: 22px; /* 157.143% */
    }
  }
  .table-wrap {
    ::v-deep .el-table__header-wrapper {
      height: 32px;
      color: var(--grey1, #262a32);
      /* menu 14 M */
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 500;
      line-height: 22px; /* 157.143% */
    }
    ::v-deep .el-table__row {
      height: 50px;
    }
    ::v-deep th.el-table__cell {
      background-color: #f5f7fa;
      padding-top: 4px;
      padding-bottom: 3px;
      color: var(--grey1, #262a32);
      /* menu 14 M */
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 500;
      line-height: 22px; /* 157.143% */
    }
  }
}
</style>
