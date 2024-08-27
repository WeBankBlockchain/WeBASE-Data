<template>
  <div id="ecc_page">
    <!-- <div class="ecc_title"></div> -->
    <div class="time">{{time | formatDateB}}</div>
    <div class="ecc_view">
      <div class="ecc_part_total">
        <div class="content_title">
          <img class="content_title_icon" src="~Assets/images/ecc/bg-total-title-icon.png" alt="">
          <div class="content_title_text">总量</div>
          <div class="content_title_time">截止 {{nowDate | formatDate}} 每{{updateMinute}}分钟更新</div>
        </div>
        <div class="content_data top_content">
          <!-- 左边数据呈现数据比例，右边数据数值 -->
          <div class="content_data_left">
            <div class="title">元豆总量 ：京东E卡总面值</div>
            <div class="text">100 : 1</div>
          </div>
          <div class="content_data_right">
            <div class="item">
              <div class="title">元豆总量（单位：元豆）</div>
              <div class="text">{{generalData.totalSupply | thousandSeparator}}</div>
            </div>
            <div style="width: 2px;height: 50px;background-color: rgb(16,63,112);"></div>
            <div class="item">
              <div class="title">京东E卡总面值（单位：元）</div>
              <div class="text">{{jdCardTotal | thousandSeparator}}</div>
            </div>
          </div>

        </div>
      </div>
      <div class="ecc_part_data">
        <div class="content_title">
          <img class="content_title_icon" src="~Assets/images/ecc/bg-total-title-icon.png" alt="">
          <div class="content_title_text">流通数据</div>
          <div class="content_title_time">截止 {{nowDate | formatDate}} 每{{updateMinute}}分钟更新</div>
        </div>
        <div class="content_data bottom_content">
          <div class="content_data_left">
            <div class="title">流通元豆总量 ：京东E卡总面值</div>
            <div class="text">{{ydCirculateJdCardProportion}} : 1</div>
          </div>
          <div class="data_view">
            <!-- 饼图 -->

            <div class="item itemA">
              <div class="top">
                <div class="title">
                  <span class="text">元豆流通数据</span><span class="tip">（单位：元豆）</span>
                </div>
                <div class="chartBox">
                  <div class="chart" ref="myChartA"></div>
                  <div class="chartText">
                    <div class="ul">
                      <div class="li_style" style="background-color: #5384FF;"></div>
                      <div class="li">
                        <div class="li_title">流通量</div>
                        <div class="li_text">{{generalData.totalCirculate | thousandSeparator}}</div>
                      </div>
                    </div>
                    <div class="ul" style="margin-top: 16px">
                      <div class="li_style" style="background-color: #55DFFF;"></div>
                      <div class="li">
                        <div class="li_title">待分享量</div>
                        <div class="li_text">{{generalData.totalUsed | thousandSeparator}}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="bottom">
                <div class="title">
                  <span class="text">京东E卡流通数据</span><span class="tip">（单位：元）</span>
                </div>
                <div class="chartBox">
                  <div class="chart" ref="myChartB"></div>
                  <div class="chartText">
                    <div class="ul">
                      <div class="li_style" style="background-color: #5384FF;"></div>
                      <div class="li">
                        <div class="li_title">在途未入库</div>
                        <div class="li_text">{{generalData.totalCardInTransit | thousandSeparator}}</div>
                      </div>
                    </div>
                    <div class="ul" style="margin-top: 16px">
                      <div class="li_style" style="background-color: #55DFFF;"></div>
                      <div class="li">
                        <div class="li_title">在库</div>
                        <div class="li_text">{{generalData.totalCardInLibrary | thousandSeparator}}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 折线图 -->


            <div class="item itemB">

              <div class="top">
                <div class="title">
                  <span class="text">元豆总量趋势图</span><span class="tip">{{dayText}}</span>
                </div>
                <div class="lineChartBox" ref="myChartC"></div>
              </div>
              <div class="bottom">
                <div class="title">
                  <span class="text">元豆交易趋势图</span><span class="tip">{{dayText}}</span>
                </div>
                <div class="lineChartBox" ref="myChartD"></div>

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
  import { chainManageServer, transManageServer, config } from 'Api'
  import { changWeek, newChangWeek } from 'Utils'
  import * as echarts from 'echarts'
  import dayjs from 'dayjs'
  export default {
    name: 'ecc-index',
    data() {
      return {
        time: new Date().getTime(),
        // 更新频率
        updateMinute: 1,
        // 更新时间
        nowDate: new Date().getTime(),
        attachment: false,

        timer: null,
        getDataTimer: null,

        generalData: {
          totalSupply: 0,//元豆总量

          totalCirculate: 0,//流通量
          totalUsed: 0,//官方回收量  //分享量
          totalZombie: 0,//僵尸账号元豆总量 

          totalSupplyMerge: 0,//元豆总额 = 充值总额 - 已出库的总和 
          totalRecharge: 0,//充值总额（在库 + 在途）

          totalWithdraw: 0,//提现总额（在库 + 已出库） 
          totalCardInTransit: 0,//卡总额RMB（未入库） //在途未入库
          totalCardInLibrary: 0,//卡总额RMB（在库） //在库

          totalSupplyWeworld: 0,//待平账元豆总量 -微众世界

          jdCardTotal: 0,//京东卡总面值

        },

        lineChartUpdataDays: 15,
        dayText: '',

        myChartA: null,
        myChartB: null,
        myChartC: null,
        myChartD: null,
        defaultOption: {
          tooltip: {
            show: true,
            trigger: 'axis'
          },
          grid: {
            top: '36px',
            left: '3%',
            right: '4%',
            bottom: '10%',
            borderWidth: 0, // 网格线的宽度
            containLabel: true,
            show: true,
            borderColor: '#ccc',
            backgroundColor: 'transparent', // 设置网格背景为透明，使得虚线网格线能够显示出来
            z: 1 // 确保网格线在折线图上方显示
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            splitLine: {
              show: true,
              lineStyle: {
                type: 'dashed',
                color: 'rgba(85, 223, 255, 0.15)'
              }
            },
            data: []
          },
          yAxis: {
            type: 'value',
            name: '元豆数',
            splitLine: { // 设置虚线网格线
              show: true,
              lineStyle: {
                type: 'dashed', // 设置为虚线
                color: 'rgba(85, 223, 255, 0.15)',

              }
            }
          },
          series: [
            {
              data: [],
              type: 'line',
              itemStyle: {
                normal: {

                  lineStyle: {
                    color: '#55DFFF' //折线颜色
                  }
                }
              },
              areaStyle: {
                color: '#55DFFF',//阴影颜色
                opacity: 0.3,
              }
            }
          ]
        },

      }
    },
    filters: {
      formatDate(timestamp) {
        return dayjs(Number(timestamp)).format('YYYY/MM/DD HH:mm:ss')
      },
      formatDateB(timestamp) {
        return dayjs(Number(timestamp)).format('YYYY-MM-DD HH:mm')
      },
    },
    computed: {
      jdCardTotal() {
        const {
          totalSupply = 0
        } = this.generalData
        return totalSupply / 100
      },
      // 元豆流通量和京东卡 比例
      ydCirculateJdCardProportion() {
        const {
          totalCirculate = 0

        } = this.generalData
        if (this.jdCardTotal == 0) return 0
        const proportionValue = totalCirculate / this.jdCardTotal
        console.log(totalCirculate, this.jdCardTotal, proportionValue);
        return proportionValue.toFixed(3) * 1000 / 1000
      }
    },
    mounted() {
      this.setDayText()
      this.setTime()
      // 禁止缩放
      // this.blockZoom()

      this.queryIsLoading()
      this.timer = setInterval(() => {
        this.queryIsLoading()
      }, 5000)

      this.initChart()

      this.drawChart()

    },
    methods: {

      initChart() {
        this.myChartA = echarts.init(this.$refs.myChartA)
        this.myChartB = echarts.init(this.$refs.myChartB)
        this.myChartC = echarts.init(this.$refs.myChartC)
        this.myChartD = echarts.init(this.$refs.myChartD)
      },
      drawChart() {
        this.getLinesData()
        this.timer = setInterval(() => {
          this.getLinesData({ hideLoading: true })
        }, this.updateMinute * 60 * 1000)

        this.queryGeneral()
        this.getDataTimer = setInterval(() => {
          this.queryGeneral()
        }, this.updateMinute * 60 * 1000)

      },
      setTime() {
        // 1分钟更新一次time
        setInterval(() => {
          this.setDayText()
          this.time = new Date().getTime()
        }, 1000 * 60)
      },
      // 查询实时统计数据
      async queryGeneral(param) {
        const res = await config.getGeneralData()
        if (res && res.code === 0) {
          this.nowDate = new Date().getTime()
          this.generalData = res.data

          // 元豆总量
          // this.generalData.totalSupply
          // 京东E卡总面值
          // this.generalData.jdCardTotal = this.generalData.totalSupply / 100
          // 流通量
          // this.generalData.totalCirculate
          // 待分享量
          // this.generalData.totalUsed 
          // 在途未入库
          // this.generalData.totalCardInTransit 
          // 在库
          // this.getLinesData.totalCardInLibrary

          this.setOption('A')
          this.setOption('B')
          if (param) {
            this.$message.success('刷新成功')
          }
        }
      },


      async getLinesData() {
        const startDate = dayjs(this.nowDate).subtract(this.lineChartUpdataDays, 'day').format('YYYY-MM-DD')
        const endDate = dayjs(this.nowDate).format('YYYY-MM-DD')
        const param = {
          dateStart: startDate,
          dateEnd: endDate
        }
        const res = await config.getEchartByDay(param)

        if (res && res.code === 0) {
          // issueList
          // transferList

          console.log('接口数据', res.data);
          this.setLineChart(res.data.issueList, 'C')
          this.setLineChart(res.data.transferList, 'D')
        }
      },
      setLineChart(dataList, type = 'C') {
        const vm = this
        const config = {
          'C': {
            dom: vm.$refs.myChartC,
            name: 'myChartC',
            title: '元豆数'
          },
          'D': {
            dom: vm.$refs.myChartD,
            name: 'myChartD',
            title: '元豆数'
          }
        }
        const resData = newChangWeek(dataList)
        // const options = this.defaultOption
        const options = this.getOption()
        const XData = resData.map((item) => {
          return item.day.substr(5)
        })
        const YData = resData.map((item) => {
          return item.amount
        })
        options.tooltip = {
          trigger: 'axis',
          show: true
        }
        options.xAxis.data = [...XData]
        options.yAxis.type = 'value'
        options.series[0].data = [...YData]
        options.series[0].showSymbol = false
        console.log('折线图配置', options);

        vm[config[type]['name']].setOption(options)

      },

      getOption() {
        return {
          tooltip: {
            show: true,
            trigger: 'axis'
          },
          grid: {
            top: '36px',
            left: '60px',
            // left: '120px',
            right: '18px',
            bottom: '18px',
            borderWidth: 0, // 网格线的宽度
            containLabel: false,// 包含刻度标签
            show: true,
            borderColor: '#ccc',
            backgroundColor: 'transparent', // 设置网格背景为透明，使得虚线网格线能够显示出来
            z: 1 // 确保网格线在折线图上方显示
          },

          xAxis: {
            type: 'category',
            boundaryGap: false,
            axisLabel:{
              interval: 1, //刻度间隔
            },
            splitLine: {
              show: true,
              lineStyle: {
                type: 'dashed',
                color: 'rgba(85, 223, 255, 0.15)'
              }
            },
            data: []
          },
          yAxis: {
            type: 'value',
            name: '元豆数',
            splitLine: { // 设置虚线网格线
              show: true,
              lineStyle: {
                type: 'dashed', // 设置为虚线
                color: 'rgba(85, 223, 255, 0.15)',

              }
            },
            axisLabel: {
              formatter: function (value) {
                if(value >= 1000000000000){
                  return value / 1000000000000 + '兆';
                } else if(value >= 100000000){
                  return value / 100000000 + '亿';
                } else if (value >= 10000) {
                    return value / 10000 + '万';
                } else {
                    return value;
                }
              }
            }
          },
          series: [
            {
              data: [],
              type: 'line',
              itemStyle: {
                normal: {

                  lineStyle: {
                    color: '#55DFFF' //折线颜色
                  }
                }
              },
              areaStyle: {
                color: '#55DFFF',//阴影颜色
                opacity: 0.3,
              }
            }
          ]
        }
      },


      async setOption(type = 'A') {
        const vm = this
        const config = {
          A: {
            name: 'myChartA',
            dom: vm.$refs.myChartA,
            title: '元豆流通数据（单位：元豆）',
            color: ['#5384FF', '#55DFFF'],
            data: [
              { value: this.generalData.totalCirculate, name: '流通量' },
              { value: this.generalData.totalUsed, name: '待分享量' }
            ]
          },
          B: {
            name: 'myChartB',
            dom: vm.$refs.myChartB,
            title: '京东E卡流通数据（单位：元）',
            color: ['#5384FF', '#55DFFF'],
            data: [
              { value: this.generalData.totalCardInTransit, name: '在途未入库' },
              { value: this.getLinesData.totalCardInLibrary, name: '在库' }
            ]
          }
        }
        const data = config[type]
        // vm[data.name] = echarts.init(data.dom)
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)',
          },
          legend: null,
          color: data.color,
          series: [
            {
              name: data.title,
              type: 'pie',
              radius: ['35%', '60%'],
              center: ['50%', '50%'],
              hoverAnimation: false,//鼠标放大
              avoidLabelOverlap: false,
              padAngle: 0,//扇形间隔
              itemStyle: {
                borderRadius: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: 12,
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: data.data
            }
          ]
        };

        vm[data.name].setOption(option)
      },

      async queryIsLoading() {
        const res = await config.getIsLoading()
        if (res && res.code === 0) {
          this.attachment = res.data
        }
      },


      setDayText() {
        // 根据this.lineChartUpdataDays的日期周期，获取当前15天前的起始，yyyy/mm/dd -mm/dd格式
        const startDate = dayjs(new Date().getTime()).subtract(this.lineChartUpdataDays - 1, 'day').format('YYYY/MM/DD')
        const endDate = dayjs(new Date().getTime()).format('MM/DD')
        // return `${startDate}-${endDate} 近${this.lineChartUpdataDays}日`
        console.log(`${startDate}-${endDate} 近${this.lineChartUpdataDays}日`);
        this.dayText = `${startDate}-${endDate} 近${this.lineChartUpdataDays}日`
      },


      blockZoom() {
        document.addEventListener('mousewheel', function (e) {
          e = e || window.event;
          if ((e.wheelDelta && event.ctrlKey) || e.detail) {
            event.preventDefault();
          }
        }, {
          capture: false,
          passive: false
        });

        document.addEventListener('keydown', function (event) {
          if ((event.ctrlKey === true || event.metaKey === true)
            && (event.keyCode === 61 || event.keyCode === 107
              || event.keyCode === 173 || event.keyCode === 109
              || event.keyCode === 187 || event.keyCode === 189)) {
            event.preventDefault();
          }
        }, false);
      },

    },
    beforeDestroy() {
      this.timer && clearInterval(this.timer)
      this.getDataTimer && clearInterval(this.getDataTimer)
    }


  }
</script>
<style scoped>
  @font-face {
    font-family: "Regular";
    src: url('../../assets/font/RTWSYueGoTrial-Regular.ttf') format('truetype');
    font-weight: 400;
  }

  @font-face {
    font-family: "Regular";
    src: url('../../assets/font/RTWSYueGoTrial-Regular.ttf') format('truetype');
    font-weight: 500;
  }

  @font-face {
    font-family: "Regular";
    src: url('../../assets/font/RTWSYueGoTrial-Regular.ttf') format('truetype');
    font-weight: 600;
  }

  @font-face {
    font-family: "Regular";
    src: url('../../assets/font/RTWSYueGoTrial-Regular.ttf') format('truetype');
    font-weight: 700;
  }

  #ecc_page {
    font-family: 'Regular', sans-serif, 'PingFang SC', -apple-system, 'Helvetica Neue', STHeiti;
  }
</style>
<style lang="less" scoped>
  /* @design-width: 1920px; */
  /* @design-height: 1080px; */

  #ecc_page {
    width: 100%;
    height: 100%;
    // min-height: calc((1080 / 1920) * 100vw);
    min-height: calc((980 / 1920) * 100vw);
    background-color: RGB(2, 22, 60);
    background-size: 100% 100%;
    background-repeat: no-repeat;
    /* background-image: url('~Assets/images/ecc/bg-grid.png'); */
    background-image: url('~Assets/images/ecc/bg.jpg');
    box-sizing: border-box;
    padding: 30px 20px 20px;
    position: relative;

  }

  .time {
    position: absolute;
    font-size: 16px;
    color: #55DFFF;
    /* 80*46 */
    left: calc(80 / 1920 * 100vw);
    top: calc(50 / 1080 * 100vh);
  }

  .ecc_title {
    width: calc(100% * (740 / 1920));
    height: calc(100% * (206.9 / 1080));
    /* background-image: url('~Assets/images/ecc/title.png'); */
    background-size: 100% auto;
    background-repeat: no-repeat;
    position: absolute;
    z-index: 3;
    top: -11.5px;
    left: calc((100% - (100% * (740 / 1920))) / 2);
  }

  .ecc_view {
    width: 100%;
    height: 100%;
    /* background-image: url('~Assets/images/ecc/bg.png'); */
    background-size: 100% 100%;
    background-repeat: no-repeat;
    box-sizing: border-box;
    /* padding: 98px 115px 40px; */
    /* padding: 90px 0 40px; */
    padding: calc(118 / 1080 * 100vh) 0 calc(40 / 1080 * 100vh);
  }

  .ecc_part_total {
    width: calc(1650 / 1920 * 100vw);
    margin: 0 auto;
    height: 200px;
    margin-bottom: 20px;
    /* background-image: url('~Assets/images/ecc/bg-total.png'); */
    background-image: url('~Assets/images/ecc/bg-total.jpg');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    color: #fff;
    font-size: 24px;
    /* box-sizing: border-box; */
    /* padding: 0 60px; */
  }

  .content_title {
    width: 100%;
    height: 48px;
    display: flex;
    align-items: center;
    /* padding: 0 60px; */
    background-image: url('~Assets/images/ecc/bg-total-title.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;

    .content_title_icon {
      width: 24px;
      height: 24px;
      margin-left: 20px;
      margin-right: 16px;
    }

    .content_title_text {
      font-size: 24px;
      margin-right: 16px;
    }

    .content_title_time {
      font-size: 14px;
    }
  }

  .content_data {
    width: 100%;
    /* height: 150px; */
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    padding: 20px 40px;

    .content_data_left {
      width: calc( 289 / 1920 * 100vw);
      /* width: 300px; */
      /* min-width: 160px; */

      .title {
        font-size: 14px;

      }

      .text {
        font-size: 32px;
        color: #55DFFF;
        margin-top: 8px;
      }

    }

    /* 数值区域 */
    .content_data_right {
      flex: 1;
      height: 100%;

      /* background-image: url('~Assets/images/ecc/bg-total-content.png'); */
      background-image: url('~Assets/images/ecc/bg-total-content.jpg');
      background-size: 100% 100%;
      background-repeat: no-repeat;
      display: flex;
      justify-content: space-around;
      align-items: center;

      /* margin: 0 0 0 -70px; */

      .item {
        flex: 1;
        text-align: center;

        .title {
          font-size: 14px;

        }

        .text {
          font-size: 32px;
          color: #55DFFF;
          margin-top: 8px;
        }
      }
    }

    /* 可视化区域 */
    .data_view {
      flex: 1;
      height: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .item {
        height: 100%;
        background-size: 100% 100%;
        background-repeat: no-repeat;
        box-sizing: border-box;
        /* padding: 30px 40px; */
        padding: 0px 40px;

        .top {
          width: 100%;
          height: 50%;
          box-sizing: border-box;
          padding: 20px 0px;

          .title {
            width: 100%;

            .text {
              font-size: 22px;
              line-height: 27px;
              margin-right: 8px;
            }

            .tip {
              font-size: 12px;

            }
          }

          .chartBox {
            width: 100%;
            height: calc(100% - 28px);
            display: flex;
            justify-content: center;
            align-items: center;

            .chart {
              width: calc(100% - 130px);
              // width: 100%;
              height: 100%;
            }

            .chartText {
              width: 130px;
              height: 100%;
              display: flex;
              flex-direction: column;
              justify-content: center;
              align-items: center;

              .ul {
                width: 100%;
                display: flex;
                justify-content: start;
                align-items: baseline;

                .li_style {
                  width: 8px;
                  height: 8px;
                  background-color: #55DFFF;
                  margin-right: 10px;
                  border-radius: 50%;
                }

                .li {
                  flex: 1;
                  min-width: 110px;

                  .li_title {
                    font-size: 12px;
                  }

                  .li_text {
                    font-size: 16px;
                    color: #55DFFF;
                    word-break: break-all;
                  }
                }
              }

            }
          }
        }

        .bottom {
          width: 100%;
          height: 50%;
          box-sizing: border-box;
          padding: 20px 0px;

          .title {
            width: 100%;

            .text {
              font-size: 22px;
              line-height: 27px;
              margin-right: 8px;
            }

            .tip {
              font-size: 12px;

            }
          }

          .chartBox {
            width: 100%;
            height: calc(100% - 28px);
            display: flex;
            justify-content: center;
            align-items: center;

            .chart {
              width: calc(100% - 130px);
              // width: 100%;
              height: 100%;
            }

            .chartText {
              width: 130px;
              height: 100%;
              display: flex;
              flex-direction: column;
              justify-content: center;
              align-items: center;

              .ul {
                width: 100%;
                display: flex;
                justify-content: start;
                align-items: baseline;

                .li_style {
                  width: 8px;
                  height: 8px;
                  background-color: #55DFFF;
                  margin-right: 10px;
                  border-radius: 50%;
                }

                .li {
                  flex: 1;
                  min-width: 110px;

                  .li_title {
                    font-size: 12px;
                  }

                  .li_text {
                    font-size: 16px;
                    color: #55DFFF;
                    word-break: break-all;
                  }
                }
              }

            }
          }

        }

      }

      .itemA {
        width: calc((420 / 1920) * 100vw);
        background-image: url('~Assets/images/ecc/bg-data-contentA.jpg');
        margin-right: 20px;

        .top {
          border-bottom: 2px solid rgb(16, 63, 112);

        }
      }

      .itemB {
        width: calc((800 / 1920) * 100vw);
        background-image: url('~Assets/images/ecc/bg-data-contentB.jpg');
        /* flex: 1; */

        .top {
          border-bottom: 2px solid rgb(16, 63, 112);

        }
      }
    }
  }

  .ecc_part_data {
    /* width: 100%; */
    width: calc(1650 / 1920 * 100vw);
    height: calc(100% - 220px);
    margin: 0 auto;
    background-image: url('~Assets/images/ecc/bg-data.jpg');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    color: #fff;
    font-size: 24px;
    /* box-sizing: border-box; */
    /* padding: 0 60px */
  }

  .top_content {
    height: 150px;
  }

  .bottom_content {
    height: calc(100% - 48px)
  }


  /* chartBox */
  .lineChartBox {
    width: 100%;
    height: calc(100% - 28px);
    /* height: 150px; */
    /* display: flex;
    justify-content: center;
    align-items: center; */


  }


  div.update {
    position: fixed;
    top: 80px;
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
    z-index: 4;

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