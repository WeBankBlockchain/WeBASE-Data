<template>
  <div class="index-container">
    <div class="header">
      <div class="text">Weco监控浏览器</div>
    </div>

    <div class="area">
      <div class="title">
        <span class="text">总量</span><span style="margin-right:8px">截止</span><span  style="margin-right:8px">{{nowDate | formatDate}}</span><span>每{{updateMinute}}分钟更新</span>
      </div>

      <div class="area_content">
        <div class="content_item">
          <div class="text">元豆总量：京东E卡总面值</div>
          <div class="number">{{ydProportion}}:1</div>
        </div>
        <div class="info_part">

          <div class="info_part_item">
            <div class="text">元豆总量（单位：元豆）</div>
            <div class="number">{{generalData[0]?.totalSupply | thousandSeparator}}</div>
          </div>
          <div class="info_part_item">
            <div class="text">京东E卡总面值（单位：元）</div>
            <div class="number">{{jdCardTotal | thousandSeparator}}</div>
          </div>
        </div>

      </div>

    </div>

    <div class="area">
      <div class="title">
        <span class="text">流通数据</span><span style="margin-right:8px">截止</span><span  style="margin-right:8px">{{nowDate | formatDate}}</span><span>每{{updateMinute}}分钟更新</span>
      </div>

      <div class="area_content">
        <div class="content_item">
          <div class="text">流通元豆总量：京东E卡总面值</div>
          <div class="number">{{ydCirculateJdCardProportion}}:1</div>
          
        </div>

        <div class="info_part">
          <div class="info_part_item">
            <div class="text"><span><strong>元豆数据</strong></span><span>（单位：元豆）</span></div>
            <div class="graph_box">
              <div class="graph"  ref="graphLeft"></div>
              <div class="graph_legend">
                <div class="legend_item"  style="position:relative">
                   <div class="color_block" :style="{background: graphLeftData['color'][0] }"></div>
                   <div class="text">
                    <div class="name">
                      <!-- <span>{{graphLeftData['name'][0]}}</span><span class="smallText">（包含<el-tooltip :visible-arrow="false" class="item" effect="dark"  placement="bottom-start">
                        <div slot="content">待平账元豆总量{{graphLeftData['value'][2] | thousandSeparator}}<br/>微众世界发行元豆将统一于6.30做平账处理</div>
                        <span class="highlight">待平账元豆总量</span>
                      </el-tooltip>）</span> -->
  
                      <span>{{graphLeftData['name'][0]}}</span><span class="smallText">（包含<newtooltip :text="showText"  class="item" >
                        
                        <span class="highlight">待平账元豆总量</span>
                      </newtooltip>）</span>
  
                      
                     
                      
                    </div>
                    
                     <div class="desc">{{graphLeftData['value'][0] | thousandSeparator}}</div>
                   </div>
                  
                </div>
                <div class="legend_item">
                   <div class="color_block" :style="{background: graphLeftData['color'][1] }"></div>
                   <div class="text">
                    <div class="name">
                      <span>{{graphLeftData['name'][1]}}</span>
                      <el-tooltip :visible-arrow="false" class="item" effect="dark"  placement="bottom-start">
                        <div slot="content">元豆回收与再分享逻辑：元豆回收包含平<br/>台/机构活动收入、员工捐赠等，元豆再分<br/>享后续将根据一定规则进行定期分享</div>
                        <img class="explain_icon" src="../../assets/images/explain.png" alt="">
                      </el-tooltip>
                    </div>
                     <div class="desc">{{graphLeftData['value'][1] | thousandSeparator}}</div>
                   </div>
                  
                </div>
                <!-- <div class="legend_item">
                  <div class="color_block" :style="{background: graphLeftData['color'][2] }"></div>
                  <div class="text">
                   <div class="name">
                     <span>{{graphLeftData['name'][2]}}</span>
                     <el-tooltip :visible-arrow="false" class="item" effect="dark"  placement="bottom-start">
                       <div slot="content">微众世界发行元豆将统一于<br/>6.30做平账处理</div>
                       <img class="explain_icon" src="../../assets/images/explain.png" alt="">
                     </el-tooltip>
                   </div>
                    <div class="desc">{{graphLeftData['value'][2] | thousandSeparator}}</div>
                  </div>
               </div> -->
  
              </div>
            </div>
   
          </div>
          <div class="info_part_item">
            <div class="text"><span><strong>京东E卡数据</strong></span><span>（单位：元）</span></div>
            <div class="graph_box">
              <div class="graph"  ref="graphRight"></div>
              <div class="graph_legend">
                <div class="legend_item">
                   <div class="color_block" :style="{background: graphRightData['color'][0] }"></div>
                   <div class="text">
                    
                     <div class="name">
                      <span>{{graphRightData['name'][0]}}</span>
                      <el-tooltip :visible-arrow="false" class="item" effect="dark" placement="bottom-end">
                        <div slot="content">在途未入库：京东E卡实体卡<br/>供应商已发货配送中，待由元<br/>豆管理系统保存</div>
                        <img @click="clickExplain()" class="explain_icon" src="../../assets/images/explain.png" alt="">
                      </el-tooltip>
                    </div>
                     <div class="desc">{{graphRightData['value'][0] | thousandSeparator}}</div>
                   </div>
                  
                </div>
                <div class="legend_item">
                   <div class="color_block" :style="{background: graphRightData['color'][1] }"></div>
                   <div class="text">
                     <div class="name">
                       <span>{{graphRightData['name'][1]}}</span>
                     </div>
                     <div class="desc">{{graphRightData['value'][1] | thousandSeparator}}</div>
                   </div>
                  
                </div>
                <!-- <div class="legend_item">
                  <div class="color_block" :style="{background: graphRightData['color'][2] }"></div>
                  <div class="text">
                   
                    <div class="name">
                      <span>{{graphRightData['name'][2]}}</span>
  
                      <el-tooltip :visible-arrow="false" class="item" effect="dark" placement="bottom-end">
                      <div slot="content">微众世界发行的元豆待平账入<br/>库对应数量的京东E卡</div>
                      <img @click="clickExplain()" class="explain_icon" src="../../assets/images/explain.png" alt="">
                    </el-tooltip>
                    </div>
                    <div class="desc">{{graphRightData['value'][2] | thousandSeparator}}</div>
                  </div>
                 
               </div> -->
              </div>
            </div>
          </div>

        </div>
      </div>

    </div>
    <div class="update" v-if="attachment"><img src="../../assets/images/loading.png" />历史数据同步中...</div>


  </div>
</template>
<script>
  import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
  import newtooltip from '../../components/newTooltip.vue'
  import { chainManageServer, transManageServer, config } from 'Api'
  import { changWeek } from 'Utils'
  import * as echarts from 'echarts'
  import ConfigPage from './ConfigPage.vue'
  import dayjs from 'dayjs'
  export default {
    name: 'groupManage',
    mixins: [tableHeightHandle],
    props: {},
    components: { ConfigPage, newtooltip},
    data() {
      return {
        updateMinute:1,
        showTooltip: false,
        attachment: false,
        myChart: null,
        generalData: [
          {
            totalSupply : 0,//元豆总量

            totalCirculate : 0,//流通量
            totalUsed : 0,//官方回收量  //分享量
            totalZombie : 0,//僵尸账号元豆总量 

            totalSupplyMerge : 0,//元豆总额 = 充值总额 - 已出库的总和 
            totalRecharge : 0,//充值总额（在库 + 在途）

            totalWithdraw : 0,//提现总额（在库 + 已出库） 
            totalCardInTransit : 0,//卡总额RMB（未入库） //在途未入库
            totalCardInLibrary : 0,//卡总额RMB（在库） //在库

            totalSupplyWeworld: 0,//待平账元豆总量 -微众世界
          }
        ],
        
        defaultData: null,
        timer: null,
        getDataTimer: null,
        nowDate: new Date().getTime()
      }
    },
    filters: {
      formatDate(timestamp) {
        return dayjs(Number(timestamp)).format('YYYY/MM/DD HH:mm:ss')
      },
    },
    watch: {
      generalData: {
        handler(newV, oldV) {
          console.log('new generalData', newV);
        },
        deep: true
      }
    },
    computed: {
      // 元豆总量和京东卡比例
      ydProportion(){
        const {
          totalSupply
        } = this.generalData[0]
        
        if(this.jdCardTotal == 0) return 0
        const proportionValue = totalSupply / this.jdCardTotal

        return proportionValue.toFixed(3)*1000/1000
      },
      // 京东卡总面值
      jdCardTotal(){
        const {
          totalCardInTransit,
          totalCardInLibrary,
          totalSupply,
        } = this.generalData[0]
        // return totalCardInTransit + totalCardInLibrary

        return  totalSupply / 100
      },
      // 元豆流通量和京东卡 比例
      ydCirculateJdCardProportion(){
        const {
          totalCirculate = 0,
        } = this.generalData[0]
        if(this.jdCardTotal == 0) return 0
        const proportionValue = totalCirculate / this.jdCardTotal
        return proportionValue.toFixed(3)*1000/1000
      },
      totalSupplyWeworldMoney(){
        const {
          totalSupplyWeworld = 0
        } = this.generalData[0]
        return  totalSupplyWeworld / 100
      },
      graphLeftData(){
        // echarts 甜甜圈 左边数据
        const {
          totalCirculate = 0,
          totalUsed = 0,
          totalSupplyWeworld = 0,

        } = this.generalData[0]
        return  {
          // refs: this.$refs.graphLeft,
          title : '元豆数据',
          name: ['流通量','待分享量','待平账元豆总量'],
          color: ['#5384FF','#60D2F2','#CFD0D3'],
          value: [totalCirculate,totalUsed,totalSupplyWeworld]
        }
      },
      graphRightData(){
        // echarts 甜甜圈 右边边数据
        const {
          totalCardInTransit = 0,
          totalCardInLibrary = 0,
          

        } = this.generalData[0]
        return  {
          // refs: this.$refs.graphRight,
          title : '京东E卡数据',
          name: ['在途未入库','在库','待平账入库京东E卡'],
          color: ['#5384FF','#60D2F2','#CFD0D3'],
          value: [totalCardInTransit,totalCardInLibrary, this.totalSupplyWeworldMoney]
        }
      },
      showText(){
        return String(this.numberWithCommas(this.graphLeftData['value'][2]))
        // return `待平账元豆总量${this.numberWithCommas(this.graphLeftData['value'][2])}<br/>微众世界发行元豆将统一于6.30做平账处理`
      }
    },
    mounted() {

      this.queryIsLoading()
      this.timer = setInterval(() => {
        this.queryIsLoading()
      },5000)



      this.queryGeneral()
      this.getDataTimer = setInterval(() => {
        this.queryGeneral()
      }, this.updateMinute * 60 * 1000)

    },
    methods: {
      setOption(type) {
        const vm = this
        if(type != 'left' && type != 'right'){
          type = 'left'
        }
        const {
          totalCirculate = 0,//流通量
          totalUsed = 0,//分享量

          totalCardInTransit = 0,//在途未入库
          totalCardInLibrary = 0,//在库
          totalSupplyWeworld = 0,
        } = this.generalData[0]
        const data = {
          'left': {
            refs: this.$refs.graphLeft,
            data: ['流通量','待分享量'],
            name: '元豆数据',
            color: ['#5384FF','#60D2F2',],
            seriesData: [
              { value: totalCirculate, name: '流通量'},
              { value: totalUsed, name: '待分享量'},
            ]
          },
          'right': {
            refs: this.$refs.graphRight,
            data: ['在途未入库','在库','待平账入库京东E卡'],
            name: '京东E卡数据',
            color: ['#5384FF','#60D2F2','#CFD0D3'],
            seriesData: [
              { value: totalCardInTransit, name: '在途未入库'},
              { value: totalCardInLibrary, name: '在库'},
              { value: this.totalSupplyWeworldMoney, name: '待平账入库京东E卡'},
            ]
          },
        }
        console.log(data[type]['seriesData']);

        this.myChart = echarts.init(data[type].refs)

    
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)',
            formatter: function(params) {
              if(params.name == '待平账入库京东E卡'){
                return `待平账入库京东E卡  <b>${vm.numberWithCommas(params.value)}</b><br/><small style="fontSize:12px">微众世界发行的元豆待平账入库对应数量的京东E卡</small>`
              } else  {

                return `${params.seriesName} <br/>${params.name}: ${vm.numberWithCommas(params.value)} (${params.percent}%)`;
              }
            }
          },
          // grid:{
          //   left: '25%',
          //   top: '0%',
            
          //   right: '20%',
          //   bottom: '10%',
          //   containLabel: true
          // },
          // legend: {

          //   // selectedMode: false,
         
          //   itemWidth: 5,
          //   itemHeight: 10,
          //   // itemGap: 58,//图例项间隔
          //   itemGap: 10,//图例项间隔
           
          //   orient: 'vertical',
          //   top: 24,
          //   right: '30%',
          //   data: data[type]['data'],
          //   formatter: function(name){
          //     const beforeA = `<div style="color:#0F1222;fontWeight:400;fontSize:14px;lineHeight:22px">`
          //     const beforeB = `<div style="color:#0F1222;fontWeight:700;fontSize:16px;lineHeight:22px">`
          //     const after = '</div>'

          //     const names = {
          //       '流通量':totalCirculate,
          //       '待分享量': totalUsed,
          //       '在途未入库': totalCardInTransit,
          //       '在库' : totalCardInLibrary
          //     }
          //     const content = vm.numberWithCommas(names[name])
          //     return  name  + '\n'  + content 
          //     // return beforeA + name + after + '\n' + beforeB + content + after
          
          //   },
          //   textSyle: {
          //     fontSize: 14,
          //     fontSize: 12,
          //     color: '#FFFFFF'
          //   },
            
          //   tooltip: {
          //     show: true,
          //     trigger: 'item',
          //     formatter: function(params) {
          //       const { name } = params
          //       console.log('图例鼠标滑入事件',name);
          //       const text = '<div style="fontSize:20px">123456</div><div>789</div>'
          //       if(name == '流通量'){
          //         return ''
          //       } else if(name == '待分享量'){
          //         return '元豆回收与再分享逻辑：元豆回收包含平<br/>台/机构活动收入、员工捐赠等，元豆再分<br/>享后续将根据一定规则进行定期分享'
          //       } else if(name == '在途未入库'){
          //         return ''

          //       } else if (name == '在库'){
          //         return '在途未入库：京东E卡实体卡<br/>供应商已发货配送中，待由元<br/>豆管理系统保存'

          //       } else {
          //         return name

          //       }
          //     }
          //   }
          // },
          legend: null,
          color: data[type]['color'],
          series: [
            {
              name: data[type]['name'],
              type: 'pie',
              radius: ['35px', '60px'],
              center: ['50%', '50%'],
              hoverAnimation:false,//鼠标放大
              avoidLabelOverlap: false,
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: false,//关闭中心内容
                  fontSize: '30',
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: data[type]['seriesData']
            }
          ]
        }
        option && this.myChart.setOption(option)
      },


      async queryIsLoading() {
        const res = await config.getIsLoading()
        if (res && res.code === 0) {
          this.attachment = res.data
        }
      },

      // 查询实时统计数据
      async queryGeneral(param) {
        const res = await config.getGeneralData()
        if (res && res.code === 0) {
          this.nowDate = new Date().getTime()
          this.generalData = [res.data]

          this.setOption('left')
          this.setOption('right')
          if (param) {
            this.$message.success('刷新成功')
          }
        }
      },

      // numberWithCommas(x) {
      //   if(!x) return 0
      //   x = x.toString();
      //   var pattern = /(-?\d+)(\d{3})/;
      //   while (pattern.test(x))
      //     x = x.replace(pattern, "$1,$2");
      //   return x;
      // },
      numberWithCommas (num){
        if (!num || !Number.isFinite(+num)) return num;

        // 处理负数情况
        const isNegative = num < 0;
        num = Math.abs(num);

        const decimals = Number.isInteger(num) ? '' : String(num).split('.')[1];
        const arr = String(parseInt(num)).split('').reverse();
        const formattedNum = `${arr
          .map((item, index) => {
            return index !== 0 && index % 3 === 0 ? item + ',' : item;
          })
          .reverse()
          .join('')}${decimals && '.' + decimals}`;

        // 添加负号
        return isNegative ? '-' + formattedNum : formattedNum;
      }

    },
    beforeDestroy() {
      this.timer && clearInterval(this.timer)
      this.getDataTimer && clearInterval(this.getDataTimer)
    }
  }
</script>
<style lang="less" scoped>
  /* .echar {
    width: 800px;
    height: 500px;
    background-color: #f4f4f4;
  } */

  .index-container {
    width: 100%;
    height: 100%;
    /* background: var(--grey-bg2, #f6f7fb); */
    color: #0F1222;

    div.header {
      box-sizing: border-box;
      padding: 64px 0;
      /* div.foo{
        height: 64px;
      } */
      div.text {
        /* margin-bottom: 64px; */
        /* margin: 64px 0; */
        font-weight: 700;
        font-size: 36px;
        color: #0F1222;
      }
    }

    div.area {
      width: 100%;
      box-sizing: border-box;
      padding: 20px 28px 0px;
      background-color: #FFFFFF;
      border-radius: 4px;
      box-shadow: 0px 4px 16px 0px rgba(35, 101, 143, 0.04);
      margin-bottom: 32px;
      word-wrap: break-word;

      div.title {
        box-sizing: border-box;
        padding-bottom: 16px;
        border-bottom: 1px solid #F7F7F7;

        span {
          font-weight: 400;
          font-size: 12px;
          line-height: 20px;
          color: rgba(15, 18, 34, 0.65);
        }

        span.text {
          font-weight: 600;
          font-size: 16px;
          line-height: 24px;
          color: #0F1222;
          margin-right: 16px;
        }
      }

      div.area_content {
        width: 100%;
        display: flex;
        padding: 32px 0;


        div.content_item{
          /* flex-grow: 1; */
          width: calc( 100% / 3);
          margin: 24px 0;
          padding-right: 10px;
          box-sizing: border-box;
          div.text {
            font-weight: 400;
            font-size: 14px;
            line-height: 22px;
            color: #0F1222;
          }

          div.number {
            margin-top: 8px;
            font-weight: 700;
            font-size: 36px;
            line-height: 46px;
            color: #000000;

          }

        }

        div.info_part {
          /* flex: 1; */
          width: 66.666%;
          background-color: #F5F7FE;
          display: flex;
          border-radius: 4px;


          div.info_part_item{
            width: 50%;
            margin: 24px 0;
            padding: 0 10px 0 24px;
            box-sizing: border-box;
            div.text {
              font-weight: 400;
              font-size: 14px;
              line-height: 22px;
              color: #0F1222;
            }
  
            div.number {
              margin-top: 8px;
              font-weight: 700;
              font-size: 36px;
              line-height: 46px;
              color: #000000;
  
            }
            div.pendingText{
              font-weight: 400;
              font-size: 12px;
              line-height: 20px;
              color: rgba(15, 18, 34, 0.65);
              margin-top: 20px;
  
            }
            div.graph_box{
              /* margin-top: 26px; */
              width: 100%;
              min-height: 120px;
              display: flex;
              justify-content: space-between;
  
              div.graph{
                /* flex: 1; */
                /* min-height: 120px; */
                min-width: 160px;
                min-height: 120px;
                
                
              }
              div.graph_legend{
                flex: 1;
                
                margin-top: 26px;
                /* margin-right: 40px; */
                div.legend_item{
                  display: flex;
                  margin-bottom: 24px;
                  div.color_block{
                    width: 5px;
                    height: 14px;
                    margin-right: 4px;
                  }
                  div.text{
                    div.name{
                      font-weight: 400;
                      font-size: 14px;
                      line-height: 14px;
                      color: #0F1222;
                      display: flex;
                      justify-content: left;
                      align-items: center;
                      .explain_icon{
                        margin-left: 4px;
                        width: 16px;
                        height: 16px;
                        cursor: pointer;
                      }
                      span.smallText{
                        font-size: 12px;
                        font-weight: 400;
                      }
                      span.highlight{
                        color: #0256FF;
                        cursor: pointer;
                      }
                    }
                    div.desc{
                      margin-top: 4px;
                      font-weight: 700;
                      font-size: 16px;
                      line-height: 22px;
                      color: #0F1222;
  
                    }
  
                  }
                  
                }
              }
  
            }
            div.graph{
              margin-top: 26px;
             
              min-height: 120px;
            }

          }
          


        }
      }
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
</style>