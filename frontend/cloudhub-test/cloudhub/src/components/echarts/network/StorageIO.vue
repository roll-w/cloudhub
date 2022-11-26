<template>
  <!-- 图表的大小(width与height)在调用该组件时定义 -->
  <div ref="myRef" data-bs-toggle="tooltip" data-bs-placement="top"
       title="">
  </div>
</template>

<script>
import {ref, onMounted, getCurrentInstance} from 'vue'

export default {
  name: "ReceiveAndSend",
  setup() {
    const {proxy} = getCurrentInstance() // 获取全局配置项
    const myRef = ref(null) // 获取dom实例

    onMounted(() => {
      renderChart() // 生命周期挂载函数渲染图表
    })

    const renderChart = () => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = proxy.$echarts.init(myRef.value)

      // 变量
      let base = +new Date(0, 0, 0);
      let oneDay = 24 * 3600 * 1000;
      let date = [];
      let receiveRate = [Math.random() * 150];
      let now = new Date(base);

      // 方法等
      function addData(shift) {
        now = [now.getFullYear(), now.getMonth() + 1, now.getDate()].join('/');
        date.push(now);
        receiveRate.push((Math.random() - 0.4) * 10 + receiveRate[receiveRate.length - 1]);
        if (shift) {
          date.shift();
          receiveRate.shift();
        }
        now = new Date(+new Date(now) + oneDay);
      }

      for (let i = 1; i < 10; i++) {
        addData();
      }

      // 指定图表的配置项和数据
      myChart.setOption({
        backgroundColor: '#fff',
        tooltip: {
          trigger: 'axis',
          formatter: function (params) {
            let res = ''
            res += '<div>' + params[0].seriesName + ':' + params[0].data + '</div><br>'
            res += '<div>' + params[1].seriesName + ':' + params[1].data + '</div>'
            return res;
          },

        },
        title: {
          text: `{a|存储器IO速率}`,
          textStyle: {
            rich: {
              a: {
                fontSize: 16,
                fontWeight: 600,
              },
            },
          },
          top: '2%',
          left: '2%',
        },
        legend: {
          // data: ['发送速率(bytes/s)', '接收速率(bytes/s)'],
          textStyle: {
            align: 'right',
          },
          top: '2%',
          right: '2%',
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          show: false,
          axisLabel: {
            //坐标轴刻度标签的相关设置。
            interval: 0, //设置为 1，表示『隔一个标签显示一个标签』
            //	margin:15,
            textStyle: {
              color: '#1B253A',
              fontStyle: 'normal',
              fontFamily: '微软雅黑',
              fontSize: 12,
            },
            axisTick: {
              show: false,
            },
          },
          axisLine: {
            lineStyle: {
              color: '#E5E9ED',
              // opacity:0.2
            },
          },
          splitLine: {
            show: false,
          },
        },
        yAxis: {
          boundaryGap: [0, '50%'],
          type: 'value',
          axisLabel1: {
            textStyle: {
              color: '#a8aab0',
              fontStyle: 'normal',
              fontFamily: '微软雅黑',
              fontSize: 12,
            },
          },
          axisLine: {
            show: false,
          },
          axisTick: {
            show: false,
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: '#E5E9ED',
              // 	opacity:0.1
            },
          },
          axisLabel2: {
            show: true,
            margin: 20,
            formatter: '{value}',
          },
        },
        series: [
          {
            name: '内存写(bytes/s)',
            type: 'line',
            smooth: true,
            stack: 'Total',
            itemStyle: { // 折线颜色(浅绿)
              normal: {
                color: '#ABEBC6',
                lineStyle: {
                  color: '#1EE882',
                  width: 1,
                },
                areaStyle: {
                  // color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [
                  //   {
                  //     offset: 0,
                  //     color: 'rgba(30,232,130,0)',
                  //   },
                  //   {
                  //     offset: 1,
                  //     color: 'rgba(30,232,130,0.5)',
                  //   },
                  // ]),

                },
              },
            },
            emphasis: {
              focus: 'series'
            },
            data: receiveRate
          },
          {
            name: '磁盘写(bytes/s)', // 浅黄
            type: 'line',
            smooth: true,
            stack: 'Total',
            itemStyle: {
              normal: {
                color: '#F9E79F', // 区域颜色
                lineStyle: {
                  color: 'rgba(253,222,9,1)',
                  width: 1,
                },
                areaStyle: {
                  // color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [
                  //   {
                  //     offset: 0,
                  //     color: 'rgba(253,222,9,0)',
                  //   },
                  //   {
                  //     offset: 1,
                  //     color: 'rgba(253,222,9,0.5)',
                  //   },
                  // ]),
                },
              },
            },
            emphasis: {
              focus: 'series'
            },
            data: receiveRate
          },
          {
            name: '磁盘读(bytes/s)', // 浅黄
            type: 'line',
            smooth: true,
            stack: 'Total',
            itemStyle: {
              normal: {
                color: '#AED6F1 ', // 区域颜色
                lineStyle: {
                  color: '#3498DB',
                  width: 1,
                },
                areaStyle: {
                  // color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [
                  //   {
                  //     offset: 0,
                  //     color: 'rgba(253,222,9,0)',
                  //   },
                  //   {
                  //     offset: 1,
                  //     color: 'rgba(253,222,9,0.5)',
                  //   },
                  // ]),
                },
              },
            },
            emphasis: {
              focus: 'series'
            },
            data: receiveRate
          }
        ]
      })
      setInterval(function () {
        addData(true);
        myChart.setOption({
          xAxis: {
            // receiveRate: date
          },
          series: [{
            name: '',
            data: receiveRate
          }]
        });
      }, 1000);
    }

    return {
      myRef,
    }
  }
}

</script>

<style scoped>

</style>
