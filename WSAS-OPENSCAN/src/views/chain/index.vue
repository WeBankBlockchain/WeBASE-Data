<template>
  <div class="application-container">
    <div v-if="hasData">
      <div class="top">

        <!-- <div class="title">{{$t('chain.title')}}</div> -->
        <!-- <div class="message">{{ detail.desc }}</div> -->
        <div class="serch-container">
          <el-input style="width: 760px" v-model="input" @keydown.enter="serch" :placeholder="$t('chain.placeholder')"></el-input>
          <img @click="serch" src="../../assets/images/serch.png" alt="" />
        </div>
      </div>

      <div class="center">
        <div class="left">
          <div class="sum box">
            <span>{{$t('chain.trans_sum')}}</span>
            <p>{{ detail.txs | thousandSeparator }}</p>
          </div>
          <div class="height box">
            <span>{{$t('chain.block_height')}}</span>
            <p>{{ detail.blocks | thousandSeparator }}</p>
          </div>
        </div>
        <div class="right graph">
          <p>{{$t('chain.trading_volume')}}</p>
          <div id="echartContainer"></div>
        </div>
      </div>
      <div class="bottom">
        <div class="left box">
          <div class="title">{{$t('chain.recent_trans')}}</div>
          <div class="list-container">
            <div class="list" v-for="item in transList" :key="item.transHash">
              <img src="../../assets/images/trail.png" alt="" />
              <div class="detail">
                <div class="time">
                  <p class="ellips" @click="openTransPage(item.transHash)"><span> </span>{{ item.transHash }}</p>
                  <span>{{ item.blockTimestamp | timeTrans }}</span>
                </div>
                <div class="fromto">
                  <p class="from ellips"><span>from</span> {{ item.transFrom }}</p>
                  <p class="to ellips">
                    <!-- <span>to</span><i v-if="!isDecChain" @click="openContractPage(item.transTo, item.appId)">{{ item.transTo }}</i> -->
                    <!-- <i style="color: black" v-else>{{ item.transTo }}</i> -->
                    <span>to</span><i style="color: black">{{ item.transTo }}</i>
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="right box">
          <div class="title">{{$t('chain.recent_block')}}</div>
          <div class="list-container">
            <div class="list" v-for="item in blockList" :key="item.pkHash" @click="openBlockPage(item.blockNumber)">
              <img src="../../assets/images/block.png" alt="" />
              <div class="detail">
                <div class="time">
                  <p class="ellips"><span> </span>{{ item.blockNumber | thousandSeparator }}</p>
                  <span>{{ item.blockTimestamp | timeTrans }}</span>
                </div>
                <div class="fromto">
                  <p class="from ellips"><span class="rightlabel">{{$t('chain.block_node')}}</span> {{ item.sealer }}</p>
                  <p class="to ellips tags">
                    <el-tag size="mini">{{ item.transCount | thousandSeparator }} txns</el-tag>
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- <div v-if="isDecChain" class="check"><span @click="gocheck">文件/哈希核验中心 ></span></div>
      <div v-else>
        <div class="app">已开放dapp</div>
        <div class="footer">
          <div class="app-con" v-for="item in appList" :key="item.appId" @click="openAppDetail(item.appId)">
            <img v-if="item.icon" :src="item.icon" alt="" />
            <img v-else src="../../assets/images/default.png" alt="" />
            <p>{{ item.appName }}</p>
          </div>
          <div>
            <img src="../../assets/images/more.png" alt="" />
            <p>更多dapp陆续开放</p>
          </div>
        </div>
      </div> -->
    </div>
    <empty v-else @reload="init"></empty>
    <div class="update" v-if="attachment"><img src="../../assets/images/loading.png" />{{$t('history_data')}}</div>
  </div>
</template>
<script>
import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
import { chainManageServer, transManageServer, appManageServer } from 'Api'
import { SET_CHAIN_NAME } from 'Store/mutation-types.js'
import * as echarts from 'echarts'
import empty from '@/components/empty.vue'
import { changWeek } from 'Utils'
import { mapMutations } from 'vuex'
export default {
  name: 'groupManage',
  mixins: [tableHeightHandle],
  props: {},
  components: {
    empty
  },
  data() {
    return {
      dataList: [
        {
          name: '累计交易数',
          value: '12324'
        },
        {
          name: '累计区块数',
          value: '12324'
        },
        {
          name: '今日交易数',
          value: '12324'
        },
        {
          name: 'dapp数',
          value: '12324'
        }
      ],
      detail: {},
      transList: [],
      blockList: [],
      appList: [],
      myChart: null,
      timer: null,
      timer1: null,
      defaultOption: {
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        grid: {
          top: '10px',
          left: '0px',
          right: '0px',
          bottom: '0px',
          containLabel: true
        },
        series: [
          {
            data: [],
            type: 'line',
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: 'rgba(94, 146, 224, 0.2)'
                  },
                  {
                    offset: 1,
                    color: 'rgba(94, 146, 224, 0)'
                  }
                ],
                global: false // 缺省为 false
              }
            }
          }
        ]
      },
      input: '',
      chainId: '',
      // isDecChain: false,
      attachment: null,
      hasData: true
    }
  },
  created() {
    this.init()
  },
  mounted() {
    this.drawChart()
  },
  watch: {
    $route() {
      this.timer && clearInterval(this.timer)
      this.timer1 && clearInterval(this.timer1)
      this.init()
      this.drawChart()
    }
  },
  methods: {
    ...mapMutations([SET_CHAIN_NAME]),
    drawChart() {
      this.initChart()
      this.getLinesData()
      this.timer = setInterval(() => {
        this.getLinesData({ hideLoading: true })
      }, 1000 * 60 * 10)
    },
    init() {

      this.getChainGenaralInfo()
   
      this.getTransList()
      this.getBlockList()
   
      this.timer1 = setInterval(() => {
        this.getChainGenaralInfo({hideLoading: true})
   
        this.getTransList({  hideLoading: true })
        this.getBlockList({  hideLoading: true })
 
      }, 3000)
    },
    async serch() {
      const { input } = this
      if (!input) {
        this.$message.info(this.$t('voidSearch'))
        return
      }
      const res = await chainManageServer.serchByKey({ input })
      if (res && res.code === 0) {
        const { data } = res
        if (data) {
          if (data.number !== undefined) {
            this.openBlockPage(data.number)
          } else {
            this.openTransPage(data.hash)
          }
        } else {
          this.$router.push({ path: 'notFound' })
          // this.$message.info('关键字未匹配上内容')
        }
      }
    },
    initChart() {
      const chartDom = document.getElementById('echartContainer')
      this.myChart = echarts.init(chartDom)
    },
    // gocheck() {
    //   const { chainId } = this
    //   this.$router.push({ path: 'checkCenter', query: { chainId } })
    // },
    // openAppDetail(appId) {
    //   const { chainId } = this
    //   this.$router.push({ path: 'application', query: { appId, chainId } })
    // },
    openTransPage(transHash) {
   
      this.$router.push({ path: 'transaction', query: { transHash } })
    },
    // openContractPage(contractAddress, appId) {
    //   const { chainId } = this
    //   this.$router.push({ path: 'contract', query: { chainId, contractAddress, appId } })
    // },
    openBlockPage(blockNumber) {
      const { chainId } = this.$route.query
      this.$router.push({ path: 'block', query: { blockNumber } })
    },
    async getChainGenaralInfo(params) {
      const res = await chainManageServer.getChainGeneral(params)
      console.log(res, 'res')
      if (res && res.code === 0 && res.data) {
        this.hasData = true
        const { attachment } = res
        this.attachment = attachment
        this.detail = { ...res.data }
        // this.isDecChain = res.data.isDecChain
        // const { chainName } = res.data
        // this.SET_CHAIN_NAME(chainName)
      } else {
        this.hasData = false
      }
    },
    async getTransList(params) {
      const handleParams = { ...params, pageNumber: 1, pageSize: 5 }
      const res = await transManageServer.getTransList(handleParams)
      console.log(res, 'res')
      if (res && res.code === 0) {
        this.transList = res.data
      }
    },
    async getBlockList(params) {
      const handleParams = { ...params, pageNumber: 1, pageSize: 5 }
      const res = await transManageServer.getBlockList(handleParams)
      console.log(res, 'res')
      if (res && res.code === 0) {
        this.blockList = res.data
      }
    },
    async getAppList(params = {}) {
      const res = await appManageServer.getAppList(params)
      console.log(res, 'res')
      if (res && res.code === 0) {
        this.appList = res.data.filter((v) => v.appName)
      }
    },
    async getLinesData(params = {}) {
      const { chainId } = this.$route.query
    
      const res = await transManageServer.getTransDataHalfMonth({ chainId, ...params })

      if (res && res.code === 0) {
        const resData = changWeek(res.data)
        const XData = resData.map((item) => {
          return item.day.substr(5)
        })
        const YData = resData.map((item) => {
          return item.txs
        })
        this.defaultOption.tooltip = {
          trigger: 'axis',
          show: true
        }
        this.defaultOption.xAxis.data = [...XData]
        this.defaultOption.yAxis.type = 'value'
        this.defaultOption.series[0].data = [...YData]
        this.defaultOption.series[0].showSymbol = false
        this.myChart.setOption(this.defaultOption)
      }
    }
  },
  beforeDestroy() {
    this.timer && clearInterval(this.timer)
    this.timer1 && clearInterval(this.timer1)
  }
}
</script>
<style lang="less" scoped>
.application-container {
  div.top {
    width: 100%;
    min-width: 1200px;
    /* background-image: url('~Assets/images/banner_chain.png'); */
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;
    padding-top: 64px;
    box-sizing: border-box;
    p {
      text-align: center;
      font-size: 26px;
      font-weight: 500;
      font-family: PingFangSC-Medium;
      color: #262a32;
      margin-bottom: 24px;
    }
    div.title{
      text-align: left;
      font-weight: 700;
      font-size: 36px;
    }
    div.message {
      font-size: 14px;
      line-height: 24px;
      text-align: center;
      color: #525660;
      width: 800px;
      margin: 0 auto;
      margin-bottom: 60px;
    }
  }
  div.serch-container {
    display: flex;
    width: 800px;
    margin: 0 auto;
    margin-bottom: 60px;
    margin-top: 48px;
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
  .graph {
    box-shadow: 0px 0px 20px 0px #1e53a414;
  }
  div#echartContainer {
    height: 180px;
    margin-top: 20px;
  }
  div.center {
    width: 1200px;
    margin: 0 auto;
    padding-bottom: 50px;
    display: flex;
    .box {
      width: 285px;
      height: 125px;
      padding: 32px;
      box-sizing: border-box;
      background-color: white;
      border-radius: 8px;
      box-shadow: 0px 0px 20px 0px #1e53a414;
      span {
        font-size: 14px;
        color: #787b84;
      }
      p {
        margin-top: 16px;
        font-size: 24px;
        color: #262a32;
      }
    }
    .sum {
      margin-bottom: 20px;
    }
    .right {
      flex: 1;
      margin-left: 20px;
      border-radius: 8px;
      background-color: white;
      padding: 26px 24px;
      box-sizing: border-box;
    }
  }
  div.bottom {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    box-sizing: border-box;
    padding: 10px 0;
    div.box {
      background-color: white;
      border-radius: 8px;
      width: 590px;
      height: 454px;
      box-shadow: 0px 0px 20px 0px #1e53a414;
      div.title {
        height: 54px;
        font-size: 16px;
        line-height: 54px;
        font-weight: 600;
        color: #262a32;
        padding: 0 24px;
        box-sizing: border-box;
        border-bottom: 1px solid #eee;
      }
      div.list-container {
        .list {
          height: 80px;
          padding: 16px 24px;
          box-sizing: border-box;
          display: flex;
          cursor: pointer;
          img {
            width: 48px;
            height: 48px;
            margin-right: 12px;
          }
          .time {
            flex: 1;
            font-size: 14px;
            line-height: 22px;
            p {
              color: #215ed9;
              margin-bottom: 2px;
              width: 240px;
              span {
                color: #787b84;
                display: inline-block;
                margin-right: 4px;
              }
            }
          }
          .detail {
            flex: 1;
            display: flex;
            div.fromto {
              width: 210px;
              font-size: 14px;
              line-height: 22px;
              p.from {
                margin-bottom: 2px;
              }
              i {
                font-style: normal;
                color: #215ed9;
                cursor: pointer;
                padding-left: 5px;
              }
              p {
                color: #262a32;
                margin-right: 4px;
                span {
                  color: #787b84;
                  width: 42px;
                  text-align: right;
                  display: inline-block;
                }
                span.rightlabel {
                  width: auto;
                }
                .count {
                  display: block;
                }
              }
              p.tags {
                text-align: right;
              }
            }
          }
        }
      }
    }
  }
  div.check {
    width: 1200px;
    margin: 0 auto;
    text-align: center;
    margin-top: 60px;
    span {
      color: #215ed9;
      font-size: 16px;
      text-align: center;
      cursor: pointer;
    }
  }
  div.app {
    text-align: center;
    font-weight: 500;
    font-family: PingFangSC-Medium;
    font-size: 22px;
    margin-top: 80px;
    margin-bottom: 50px;
    color: #262a32;
  }
  div.footer {
    width: 1200px;
    margin: 0 auto;
    overflow: hidden;
    div {
      width: 387px;
      height: 166px;
      text-align: center;
      background-color: white;
      padding: 32px;
      box-sizing: border-box;
      border-radius: 8px;
      margin-bottom: 20px;
      float: left;
      margin-right: 19px;
      cursor: pointer;
      img {
        width: 60px;
        height: 60px;
        margin-bottom: 16px;
      }
      p {
        color: #262a32;
        font-weight: 600;
      }
      .tag {
        float: right;
      }
    }
    div:nth-child(3n) {
      margin-right: 0;
    }
  }
  div.update {
    position: fixed;
    top: 22px;
    left: 50%;
    transform: translateX(-50%);
    padding: 9px 12px;
    font-size: 14px;
    line-height: 22px;
    color: #262a32;
    border: 1px solid #60bcf5;
    background: #e6f7ff;
    color: #262a32;
    border-radius: 4px;
    img {
      display: inline-block;
      width: 16px;
      height: 16px;
      margin-right: 3px;
      position: relative;
      top: 4px;
      animation: loading-rotate 1s linear infinite;
    }
    @keyframes loading-rotate {
      from {
        transform: rotate(0);
      }
      to {
        transform: rotate(360deg);
      }
    }
  }
}
</style>
