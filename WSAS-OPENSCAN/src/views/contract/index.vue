<template>
  <div class="application">
    <div v-if="hasData">
      <div class="bread-con">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: 'chain', query: { chainId } }">{{ chainName }}</el-breadcrumb-item>
          <el-breadcrumb-item v-if="appName" :to="{ path: 'application', query: { chainId, appId } }">{{ appName }}</el-breadcrumb-item>
          <el-breadcrumb-item><a>合约详情</a></el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="top">
        <p>合约地址：</p>
        <div>{{ contractAddress }}</div>
        <img @click="copyData(contractAddress, '合约地址复制成功')" src="../../assets/images/copy.png" />
      </div>
      <div class="center">
        <div class="title">
          交易列表
          <div class="serch-container">
            <el-input style="width: 500px" v-model="input" placeholder="搜索交易哈希/块高"></el-input>
            <img @click="serch" src="../../assets/images/serch.png" alt="" />
          </div>
        </div>
        <div class="tableContent">
          <el-table size="small" v-loading="loadingFlag" :data="transData" :border="false" class="table-wrap">
            <el-table-column width="520px" label="交易哈希" prop="transHash" show-overflow-tooltip>
              <template v-slot="scope">
                <div class="hash addr" @click="openTransDetail(scope.row.transHash)">
                  {{ scope.row.transHash }} <img @click.stop="copyData(scope.row.transHash, '交易哈希复制成功')" src="../../assets/images/copy.png" alt="" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="交易时间" prop="blockTimestamp" show-overflow-tooltip>
              <template v-slot="scope">
                {{ scope.row.blockTimestamp | timeTrans }}
              </template>
            </el-table-column>
            <el-table-column label="from" prop="transFrom" show-overflow-tooltip />
            <el-table-column label="to" prop="transTo" show-overflow-tooltip>
              <!-- <template v-slot="scope">
              <div class="hash addr ellips" @click="openTransDetail(scope.row.transTo)">
                {{ scope.row.transTo }}
              </div>
            </template> -->
            </el-table-column>
            <el-table-column label="区块高度" prop="blockNumber" show-overflow-tooltip>
              <template v-slot="scope">
                <div class="blockNumber addr" @click="openBlock(scope.row.blockNumber)">
                  {{ scope.row.blockNumber }}
                </div>
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
import empty from '@/components/empty.vue'
import { copy } from 'Utils'
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
      transData: [],
      pageData: {
        pageNumber: 1,
        pageSize: 10
      },
      input: '',
      appId: '',
      chainId: '',
      contractAddress: '',
      appName: '',
      contractName: '',
      chainName: '',
      totalCount: 0,
      loadingFlag: false,
      hasData: true
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      const { contractAddress, appId, chainId } = this.$route.query
      this.contractAddress = contractAddress
      this.appId = appId
      this.chainId = chainId
      this.getContractDetail()
      this.getContractLine()
    },
    paginationHandle(pageData) {
      this.pageData = {
        pageNumber: pageData.page_offset,
        pageSize: pageData.page_size
      }
      this.getContractDetail()
    },
    openBlock(blockNumber) {
      const { chainId } = this
      this.$router.push({ path: 'block', query: { blockNumber, chainId } })
    },
    copyData(str, msg) {
      copy(str, msg)
    },
    async getContractDetail() {
      const { contractAddress, chainId, pageData } = this
      const handledParams = { chainId, contractAddress, ...pageData }
      this.loadingFlag = true
      const res = await appManageServer.getContractTransListByAppId(handledParams)
      this.loadingFlag = false
      if (res && res.code === 0) {
        this.hasData = true
        this.transData = res.data
        this.totalCount = res.totalCount
      } else {
        this.hasData = false
      }
    },
    async getContractLine() {
      const { contractAddress, appId } = this
      const res = await appManageServer.getContractLine({ contractAddress, appId })
      console.log(res)
      if (res && res.code === 0) {
        this.contractName = res.data.contractName
        this.appName = res.data.appName
        this.chainName = res.data.chainName
      }
    },
    openTransDetail(transHash) {
      const { chainId } = this
      this.$router.push({
        path: 'transaction',
        query: {
          transHash,
          chainId
        }
      })
    },
    openTransPage(transHash) {
      const { chainId } = this
      this.$router.push({ path: 'transaction', query: { chainId, transHash } })
    },
    openBlockPage(blockNumber) {
      const { chainId } = this
      this.$router.push({ path: 'block', query: { blockNumber, chainId } })
    },
    async serch() {
      const { input } = this
      if (!input.length) {
        this.getContractDetail()
        return
      }
      this.pageData.pageNumber = 1
      const { contractAddress, chainId, pageData } = this
      const params = { chainId, contractAddress, ...pageData }
      if (/^\d+$/.test(input)) {
        params.blockNumber = input
      } else {
        params.transHash = input
      }
      this.loadingFlag = true
      const res = await appManageServer.getContractTransListByAppId(params)
      this.loadingFlag = false
      if (res && res.code === 0) {
        this.transData = res.data
        this.totalCount = res.totalCount
      }
    }
  }
}
</script>
<style lang="less" scoped>
.application {
  height: 100%;
  div.bread-con {
    width: 1200px;
    margin: 0 auto;
    padding-top: 24px;
  }
  div.top {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    padding: 24px 0;
    p {
      text-align: center;
      font-size: 18px;
      font-weight: 500;
      font-family: PingFangSC-Medium;
      text-align: left;
      line-height: 26px;
    }
    div {
      font-size: 14px;
      line-height: 26px;
      color: #525660;
      font-weight: 400;
      color: #262a32;
    }
    img {
      width: 16px;
      height: 16px;
      margin-top: 4px;
      margin-left: 6px;
      cursor: pointer;
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
      font-size: 16px;
      color: #262a32;
      padding-bottom: 36px;
      line-height: 32px;
      font-weight: 600;
      div.serch-container {
        display: flex;
        float: right;
        img {
          float: right;
          width: 40px;
          height: 40px;
          cursor: pointer;
        }
        ::v-deep .el-input__inner {
          height: 40px;
          line-height: 40px;
          border-top-right-radius: 0;
          border-bottom-right-radius: 0;
        }
      }
    }
    div.addr {
      color: #215ed9;
      cursor: pointer;
      img {
        width: 12px;
        height: auto;
        transform: translateY(4px);
        cursor: pointer;
      }
    }
    div.hash {
      img {
        width: 16px;
        height: auto;
        margin-left: 6px;
      }
    }
    div.blockNumber {
      cursor: pointer;
    }
  }
}
</style>
