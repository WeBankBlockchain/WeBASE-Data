<template>
    <div style="padding-left: 30px;padding-bottom: 2px; marin-bottom:12px;">
        <div :id="id" style=" height: 190px; margin: 0 auto;"></div>
    </div>
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'

export default {
    mixins: [resize],
    props: {
        className: {
            type: String,
            default: 'chart'
        },
        id: {
            type: String,
            default: 'chart'
        },
        width: {
            type: String,
            default: '200px'
        },
        height: {
            type: String,
            default: '200px'
        },
        size: {
            type: Object
        }
    },
    data() {
        return {
            chart: null,
            chartData: ["2020-05-26", "2020-05-27", "2020-05-28", "2020-05-29", "2020-05-30", "2020-05-31", "2020-06-01", "2020-06-02"],
            chartTransactionDataArr: [0, 0, 0, 0, 0, 0, 0],
            chartSize: this.size,
        }
    },
    mounted() {
        this.initChart()
    },
    beforeDestroy() {
        if (!this.chart) {
            return
        }
        this.chart.dispose()
        this.chart = null
    },
    methods: {
        initChart() {
            let that = this
            this.chart = echarts.init(document.getElementById(this.id));
            let dayNum = this.chartData.length;
            let option = {
                legend: {
                    height: this.chartSize.height,
                    width: this.chartSize.width
                },
                tooltip: {
                    show: true,
                    trigger: "axis",
                    formatter: function (data) {
                        return (
                            '<span style="font-size:10px">' +
                            data[0].name +
                            '</span><br><table ><tr><td style="padding:0">' +
                            '<span style="font-size:10px;color:white">' + '交易量' + '：' +
                            data[0].value + '笔' +
                            "</a></span><br></td></tr></table>"
                        );
                    }
                },
                grid: {
                    left: 43,
                    right: 33,
                    top: 7,
                    bottom: 40
                },
                series: [
                    {
                        type: "line",
                        symbolSize: 1,
                        itemStyle: {
                            normal: {
                                color: "#0db1c1",
                                lineStyle: {
                                    color: "#0db1c1"
                                }
                            }
                        },
                        areaStyle: {
                            color: 'rgba(13, 177, 193,0.3)'
                        },
                        data: this.chartTransactionDataArr
                    }
                ],
                xAxis: {
                    data: this.chartData,
                    axisLine: {
                        lineStyle: {
                            color: "#e9e9e9",
                            width: 2
                        }
                    },
                    axisLabel: {
                        interval: 1,
                        textStyle: {
                            color: "rgba(0,14,31,0.62)"
                        }
                    }
                },
                yAxis: {
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: "#333"
                        }
                    },
                    splitLine: { show: true, lineStyle: { type: "dashed", color: "#e9e9e9" } },
                    axisTick: { show: false },
                    axisLabel: {
                        formatter: function (value, index) {
                            if (value > 1000 && value < 1000000) {
                                return value / 1000 + "K";
                            } else if (value > 1000000 || value === 1000000) {
                                return value / 1000000 + "M";
                            } else {
                                return value + "";
                            }
                        },
                        textStyle: {
                            color: "rgba(0,14,31,0.62)"
                        }
                    }
                }
            };
            this.chart.setOption(option, true);
        }
    }
}
</script>
