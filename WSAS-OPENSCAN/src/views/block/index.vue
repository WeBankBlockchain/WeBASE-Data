<template>
  <div class="application">
    <div v-if="hasData">
      <div class="bread-con">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: 'chain', query: { chainId } }">{{ $t(`${chainName}`) }}</el-breadcrumb-item>
          <el-breadcrumb-item><a>{{$t('block.block_detail')}}</a></el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="top">
        <p>{{$t('block.block_height')}}：</p>
        <div class="text">{{ detail.number }}</div>
        <div class="pag">
          <span @click="prev" :class="blockNumber == 0 ? 'disabled' : 'normal'"> &lt; {{$t('block.pre_block')}} </span>
          <span @click="next" :class="blockNumber >= maxBlockNumber ? 'disabled' : 'normal'"> {{$t('block.next_block')}} &gt; </span>
        </div>
      </div>
      <div class="center blockarea" v-loading="loading">
        <div class="item">
          <label>{{$t('block.block_hash')}}：</label>
          <p>{{ detail.hash }} <img @click="copyData(detail.hash, $t('copy_succ'))" src="../../assets/images/copy.png" /></p>
        </div>
        <div class="item">
          <label>{{$t('block.parent_block_hash')}}：</label>
         
          <p>
            <span class="link" @click="prev" v-if="detail.parentHash !== '0x0000000000000000000000000000000000000000000000000000000000000000'">{{ detail.parentHash }}</span>
            <span class="gray" v-else>{{ detail.parentHash }}</span>
            <img @click="copyData(detail.parentHash, $t('copy_succ'))" src="../../assets/images/copy.png" />
          </p>
        </div>
        <div class="item">
          <label>{{$t('block.transaction_root_hash')}}：</label>
          <p>{{ detail.transactionsRoot }}</p>
        </div>
        <div class="item">
          <label>{{$t('block.transaction_receipt_root_hash')}}：</label>
          <p>
            {{ detail.receiptsRoot }}
          </p>
        </div>
        <div class="item">
          <label>stateRoot：</label>
          <p>{{ detail.stateRoot }}</p>
        </div>
        <div class="item">
          <label>{{$t('timestamp')}} ：</label>
          <p>{{ detail.timestamp | timeTrans }}</p>
        </div>
        <div class="item">
          <label>{{$t('block.signature_list')}} ：</label>
          <div class="tableContent">
            <el-table size="small" :data="signatureList" :border="false" class="table-wrap">
              <el-table-column label="index" prop="index" show-overflow-tooltip />
              <el-table-column :label="$t('block.signature')" prop="signature" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
      </div>
      <div class="bottom blockarea" v-loading="loading">
        <p class="title">{{$t('block.trans_list')}}</p>
        <div class="tableContent">
          <el-table size="small" :data="transactions" :border="false" class="table-wrap">
            <el-table-column width="560px" :label="$t('block.trans_hash')" prop="hash" show-overflow-tooltip>
              <template v-slot="scope">
                <div class="hash" @click="openTransDetail(scope.row.hash)">
                  {{ scope.row.hash }} <img @click.stop="copyData(scope.row.hash, $t('copy_succ'))" src="../../assets/images/copy.png" alt="" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="from" prop="from" show-overflow-tooltip />
            <el-table-column label="to" prop="to" show-overflow-tooltip />
          </el-table>
          <we-pagination :total="transCounts" :page_offset="pageData.pageNumber" :page_size="pageData.pageSize" @paginationChange="paginationHandle"></we-pagination>
        </div>
      </div>
    </div>
    <empty v-else @reload="init"></empty>
  </div>
</template>
<script>
import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
import wePagination from '@/components/wePagination.vue'
import empty from '@/components/empty.vue'
import { chainManageServer } from 'Api'
import { copy } from 'Utils'
import { mapGetters } from 'vuex'
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
      pageData: {
        pageNumber: 1,
        pageSize: 10
      },
      keys: '',
      signatureList: [],
      transactions: [],
      transCounts: 0,
      detail: {},
      blockNumber: 0,
      maxBlockNumber: 0,
      chainId: '',
      loading: false,
      show: false,
      hasData: true
    }
  },
  created() {
    this.init()
  },
  computed: {
    ...mapGetters(['userinfo', 'chainName'])
  },
  methods: {
    copyData(str, msg) {
      copy(str, msg)
    },
    init() {
      const { blockNumber, chainId } = this.$route.query
      this.blockNumber = blockNumber
      this.chainId = chainId
      this.getBlockDetail()
      this.getMaxBlockNumber()
    },
    prev() {
      if (this.blockNumber === '0') {
        return
      }
      this.blockNumber--
      const { chainId, maxBlockNumber } = this
      this.$router.push({
        path: 'block',
        query: {
          blockNumber: this.blockNumber,
          maxBlockNumber,
          chainId
        }
      })
    },
    next() {
      if (Number(this.blockNumber) === Number(this.maxBlockNumber)) {
        return
      }
      this.blockNumber++
      const { maxBlockNumber, chainId } = this
      this.$router.push({
        path: 'block',
        query: {
          blockNumber: this.blockNumber,
          maxBlockNumber,
          chainId
        }
      })
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
    async getMaxBlockNumber() {
      const { chainId } = this
      const res = await chainManageServer.getChainGeneral({ chainId })
      if (res && res.code === 0) {
        this.maxBlockNumber = res.data.blocks
      }
    },
    // 分页切换
    paginationHandle(pageData) {
      console.log(pageData)
      this.pageData = {
        pageNumber: pageData.page_offset,
        pageSize: pageData.page_size
      }
      this.getBlockDetail()
    },
    async getBlockDetail(paramsNew = {}) {
      const { pageNumber, pageSize } = this.pageData
      const { blockNumber, chainId } = this
      let params = { pageNumber, pageSize, blockNumber, chainId }
      params = { ...params, ...paramsNew }
      this.loading = true
      const res = await chainManageServer.getBlockDetail(params)
      this.loading = false
      if (res && res.code === 0 && res.data) {
        this.hasData = true
        const { signatureList, transactions, transCounts, ...rest } = res.data
        this.detail = { ...rest }
        this.signatureList = [...signatureList]
        this.transactions = [...transactions]
        this.transCounts = transCounts
      } else {
        this.hasData = false
      }
    }
  },
  watch: {
    $route() {
      this.init()
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
      width: 14px;
      height: 14px;
      margin-top: 7px;
      margin-left: 10px;
      cursor: pointer;
    }
    div.text {
      flex: 1;
    }
    div.pag {
      span {
        cursor: pointer;
        color: #215ed9;
        margin-left: 10px;
      }
      span.disabled {
        color: #ccc;
        cursor: default;
      }
    }
  }
  .blockarea {
    width: 1200px;
    margin: 0 auto;
    background-color: white;
    padding: 32px 24px;
    box-sizing: border-box;
    box-shadow: 0px 0px 20px 0px #1e53a414;
  }
  div.center {
    div.item {
      display: flex;
      padding-bottom: 12px;
      label {
        width: 190px;
        text-align: right;
        margin-right: 12px;
      }
      p {
        width: 1044px;
        white-space: wrap;
        word-wrap: break-word;
        line-height: 22px;
        span {
          color: #1e53a4;
          cursor: pointer;
        }
        img {
          width: 16px;
          height: auto;
          vertical-align: middle;
          margin-left: 10px;
          cursor: pointer;
          transform: translateY(-2px);
        }
      }
      .tableContent {
        width: 1044px;
      }
      .gray {
        color: black;
        cursor: auto;
      }
    }
  }
  div.bottom {
    margin-top: 40px;
    .title {
      color: #262a32;
      font-size: 16px;
      padding-bottom: 24px;
      font-weight: 500;
    }
  }
  div.hash {
    cursor: pointer;
    color: #215ed9;
    font-size: 14px;
    img {
      width: 16px;
      height: auto;
      transform: translateY(3px);
      cursor: pointer;
    }
  }
}
</style>
