<template>
  <div class="application">
    <div v-if="hasData">
      <div class="bread-con">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: 'admin' }">管理员列表</el-breadcrumb-item>
          <el-breadcrumb-item><a>查询流水</a></el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="top">
        <p>管理员地址:</p>
        <div>{{ userAddress }}</div>
        <img @click="copyData(userAddress, '用户地址复制成功')" src="../../assets/images/copy.png" />
        <div class="dotBalance">
          <span>元豆余额:</span><span>{{userBalance}}</span>
          <div class="btn" @click="refresh(true)"><img src="../../assets/images/refresh.svg" alt="">刷新</div>
        </div>
      </div>
      <div class=" center">
        <div class="title">
          流水列表
          <div class="serch-container">
            <el-select v-model="selectOption" placeholder="请选择">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"> </el-option>
            </el-select>
            <el-date-picker v-model="queryTime" type="daterange" :picker-options="pickerOptions" placeholder="选择日期" v-if="selectOption == 1" value-format="yyyy-MM-dd" start-placeholder="开始日期" end-placeholder="结束日期"> </el-date-picker>
            <el-select class="flowType" v-model="flowType" placeholder="请选择类型" v-if="selectOption == 2" clearable>
              <el-option v-for="item in flowTypeOptions" :key="item.value" :label="item.label" :value="item.value"> </el-option>
            </el-select>
            <el-input class="fromAddress" v-model="fromAddress" placeholder="请输入from地址" v-if="selectOption == 3"></el-input>
            <el-input class="fromAddress" v-model="toAddress" placeholder="请输入to地址" v-if="selectOption == 4"></el-input>
            <div class="queryBtn" @click="queryUserFLow">查询</div>
            <!-- <div class="queryBtn realTime">实时查询</div> -->
          </div>
        </div>
        <div class="tableContent">
          <el-table size="small" v-loading="loadingFlag" :data="transData" :border="false" class="table-wrap" resizable>
            <el-table-column width="" label="交易时间" prop="blockTimestamp" show-overflow-tooltip>
              <template v-slot="scope">
                <span>
                  {{scope.row.blockTimestamp| timeTrans}}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="块高" prop="blockNumber" show-overflow-tooltip width="80"> </el-table-column>
            <el-table-column label="类型" prop="toAddress" show-overflow-tooltip width="60">
              <template v-slot="scope">
                <span>
                  {{(scope.row.toAddress===userAddress?'转入':'转出')}}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="转移地址" prop="fromAddress" show-overflow-tooltip>
              <template v-slot="scope">
                <div class="hash">
                  <img @click.stop="copyData(scope.row.fromAddress, 'fromAddress')" src="../../assets/images/copy.png" alt="" /> {{ scope.row.fromAddress }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="接收地址" prop="toAddress" show-overflow-tooltip>
              <template v-slot="scope">
                <div class="hash">
                  <img @click.stop="copyData(scope.row.toAddress, 'toAddress复制成功')" src="../../assets/images/copy.png" alt="" /> {{ scope.row.toAddress }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="方法名" prop="funcName" show-overflow-tooltip width="100" />
            <el-table-column label="操作者" prop="operator" show-overflow-tooltip />
            <el-table-column label="交易哈希" prop="txHash" show-overflow-tooltip>
              <template v-slot="scope">
                <div class="hash">
                  <img @click.stop="copyData(scope.row.txHash, 'txHash复制成功')" src="../../assets/images/copy.png" alt="" /> {{ scope.row.txHash }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="交易数额" prop="amount" show-overflow-tooltip width="100"> </el-table-column>
            <el-table-column label="余额" prop="balance" show-overflow-tooltip width="100"> </el-table-column>
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
import empty from '@/components/empty.vue'
import { copy, changeDate } from 'Utils'
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
      changeDate,
      transData: [],
      pageData: {
        pageNumber: 1,
        pageSize: 10
      },
      input: '',
      appId: '',
      chainId: '',
      userAddress: this.$route.query.userAddress,
      appName: '',
      contractName: '',
      chainName: '',
      totalCount: 0,
      loadingFlag: false,
      hasData: true,
      selectOption: 1,
      queryTime: '',
      fromAddress: '',
      toAddress: '',
      userBalance: '',
      options: [
        {
          value: 1,
          label: '按时间'
        },
        {
          value: 2,
          label: '按类型'
        },
        {
          value: 3,
          label: '按from地址'
        },
        {
          value: 4,
          label: '按to地址'
        }
      ],
      flowTypeOptions: [
        {
          value: 0,
          label: '转入'
        },
        {
          value: 1,
          label: '转出'
        }
      ],
      flowType: '',
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      }
    }
  },
  created() {
    // this.init()
    console.log(this.$route)
    this.getUserFLow()
    this.getDotBalance()
  },
  methods: {
    refresh() {
      this.getUserFLow()
      this.getDotBalance(true)
    },
    async getDotBalance(refresh) {
      const paramsHandle = { type: 0, userAddress: this.userAddress }
      const res = await config.getUserBalance({ ...paramsHandle })
      if (res && res.code === 0) {
        this.userBalance = res.data
        if (refresh) {
          this.$message.success('刷新成功')
        }
      }
    },
    queryUserFLow() {
      this.pageData = {
        pageNumber: 1,
        pageSize: 10
      }
      this.getUserFLow()
    },
    async getUserFLow() {
      const { pageNumber, pageSize } = this.pageData
      const paramsHandle = {
        pageSize,
        pageNumber,
        userAddress: this.userAddress,
        dateStart: this.selectOption === 1 ? (this.queryTime ? this.queryTime[0] : '') : '',
        dateEnd: this.selectOption === 1 ? (this.queryTime ? this.queryTime[1] : '') : '',
        type: this.selectOption === 2 ? this.flowType : '',
        fromAddress: this.selectOption === 3 ? this.fromAddress : '',
        toAddress: this.selectOption === 4 ? this.toAddress : ''
      }
      const res = await config.getUserFlowData({ ...paramsHandle })
      if (res && res.code === 0) {
        this.transData = res.data
        this.totalCount = res.totalCount
      }
    },
    init() {
      const { userAddress, appId, chainId } = this.$route.query
      this.userAddress = userAddress
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
      this.getUserFLow()
    },
    openBlock(blockNumber) {
      const { chainId } = this
      this.$router.push({ path: 'block', query: { blockNumber, chainId } })
    },
    copyData(str, msg) {
      copy(str, msg)
    },
    async getContractDetail() {
      const { userAddress, chainId, pageData } = this
      const handledParams = { chainId, userAddress, ...pageData }
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
      const { userAddress, appId } = this
      const res = await appManageServer.getContractLine({ userAddress, appId })
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
      const { userAddress, chainId, pageData } = this
      const params = { chainId, userAddress, ...pageData }
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
    /deep/ .el-breadcrumb__inner.is-link {
      color: var(--grey3, #787b84);
      /* text 14 R */
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 400;
      line-height: 22px;
    }
    /deep/ .el-breadcrumb__inner {
      color: var(--grey1, #262a32);
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 500;
      line-height: 22px;
    }
  }
  div.top {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    padding: 24px 0;
    p {
      color: var(--grey1, #262a32);
      /* header 18 */
      font-family: PingFang SC;
      font-size: 18px;
      font-style: normal;
      font-weight: 600;
      line-height: 26px; /* 144.444% */
    }
    div {
      font-size: 14px;
      line-height: 26px;
      color: #525660;
      font-weight: 400;
      color: #262a32;
      margin-left: 5px;
    }
    img {
      width: 16px;
      height: 16px;
      margin-top: 5px;
      margin-left: 6px;
      cursor: pointer;
    }
    .dotBalance {
      margin-left: auto;
      div {
        display: inline-block;
        cursor: pointer;
        color: #215ed9;
      }
      img {
        vertical-align: text-bottom;
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
      color: var(--grey1, #262a32);
      font-family: PingFang SC;
      font-size: 16px;
      font-style: normal;
      font-weight: 500;
      line-height: 32px;
      div.serch-container {
        display: flex;
        float: right;
        padding-bottom: 16px;
        /deep/ .el-select {
          width: 125px;
          // display: flex;
          line-height: 24px;
          height: 32px;
          padding: 4px 15px;
          align-items: center;
          gap: 8px;
          border-radius: 4px 0px 0px 4px;
          border-top: 1px solid var(--grey5, #e0e4ed);
          border-bottom: 1px solid var(--grey5, #e0e4ed);
          border-left: 1px solid var(--grey5, #e0e4ed);
          background: var(--grey-bg2, #f6f7fb);
          .el-select > .el-input {
            border: none;
          }
          .el-input--suffix .el-input__inner {
            height: auto;
            border: none;
            background: var(--grey-bg2, #f6f7fb);
            line-height: normal;
            padding: 0;
          }
          .el-input__suffix {
            right: -5px;
            top: -3px;
            .el-input__icon {
              line-height: normal;
            }
          }
        }
        /deep/ .el-date-editor {
          // width: 142px;
          // vertical-align: bottom;
          height: 32px !important;
          line-height: 24px !important;
          border-top-left-radius: 0;
          border-bottom-left-radius: 0;
          border-top-right-radius: 4px !important;
          border-bottom-right-radius: 4px !important;
          border-radius: 0px 4px 4px 0px;
          border: 1px solid var(--grey5, #e0e4ed);
          width: 250px;
          .el-input__prefix {
            top: -3px;
          }
          .el-input__inner {
            border-radius: 0px 4px 4px 0px;
            border: 1px solid var(--grey5, #e0e4ed);
            height: 32px;
            line-height: 32px;
          }
          .el-range__icon {
            line-height: 26px;
          }
          .el-range-separator,
          .el-input__icon {
            line-height: 26px;
          }
        }
        .flowType {
          width: 250px;
          border-radius: 0px 4px 4px 0px;
          border-right: 1px solid var(--grey5, #e0e4ed);
          background-color: white;
          /deep/ .el-input__inner {
            background: white !important;
          }
        }
        .retry {
          margin-left: 24px;
        }
        .fromAddress {
          vertical-align: bottom;
          width: 250px;
          height: 32px;
          padding: 0;
          align-items: center;
          gap: 10px;
          border-radius: 0px 4px 4px 0px;
          border: 1px solid var(--grey5, #e0e4ed);
          /deep/ .el-input__inner {
            border: none;
            line-height: 24px;
            height: auto;
          }
        }
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
        .queryBtn {
          // width: 88px;
          margin-left: 10px;
          border-radius: 4px;
          background: var(--main-active, #215ed9);
          padding: 5px 16px;
          justify-content: center;
          align-items: center;
          gap: 10px;
          border-radius: 4px;
          background: var(--main-active, #215ed9);
          padding: 5px 16px;
          justify-content: center;
          align-items: center;
          gap: 10px;
          color: #fff;
          /* text 14 R */
          font-family: PingFang SC;
          font-size: 14px;
          font-style: normal;
          font-weight: 400;
          line-height: 22px;
          cursor: pointer;
        }
        .realTime {
          margin-left: 24px;
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
        vertical-align: text-bottom;
      }
    }
    div.blockNumber {
      cursor: pointer;
    }
    .table-wrap {
      ::v-deep .el-table__header-wrapper {
        // height: 32px;
        color: var(--grey1, #262a32);
        /* menu 14 M */
        font-family: PingFang SC;
        font-size: 14px;
        font-style: normal;
        font-weight: 500;
        // line-height: 22px; /* 157.143% */
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
        height: 42px;
      }
    }
  }
}
</style>
