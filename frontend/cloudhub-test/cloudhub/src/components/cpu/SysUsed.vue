<template>
  <!-- 图表的大小(width与height)在调用该组件时定义 -->
  <div style="width: 200px;height: 200px" ref="myRef" data-bs-toggle="tooltip" data-bs-placement="top"
       title="">
  </div>
</template>

<script>
import {ref, onMounted, getCurrentInstance} from 'vue'

export default {
  name: "SysUsed",
  setup() {
    const {proxy} = getCurrentInstance() // 获取全局配置项
    const myRef = ref(null) // 获取dom实例

    onMounted(() => {
      renderChart() // 生命周期挂载函数渲染图表
    })

    const renderChart = () => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = proxy.$echarts.init(myRef.value)

      // 系统使用率,单位: %
      let ratio = 3.16

      // 指定图表的配置项和数据
      myChart.setOption({
        title: {
          text: (ratio).toFixed(2) + '%',
          x: 'center',
          y: 'center',
          textStyle: {
            fontWeight: 'normal',
            color: '#3a99ef',
            fontSize: '20'
          },
          subtext: '系统使用率',  // 饼状图名称
          subtextStyle: {
            fontWeight: 'normal',
            color: '#000',
            fontSize: '12'
          },
        },
        tooltip: {
          formatter: function (params) {
            return params.name + '：' + params.percent + ' %'
          }
        },

        series: [{
          name: 'circle',
          type: 'pie',
          clockWise: true,
          radius: ['50%', '66%'],
          itemStyle: {
            normal: {
              label: {
                show: false
              },
              labelLine: {
                show: false
              }
            }
          },
          hoverAnimation: false,
          data: [{
            value: ratio*0.01,
            name: '占比',
            itemStyle: {
              normal: {
                color: { // 颜色渐变
                  colorStops: [{
                    offset: 0,
                    color: '#3a99ef' // 0% 处的颜色
                  }, {
                    offset: 1,
                    color: '#5eb0fb' // 100% 处的颜色
                  }]
                },
                label: {
                  show: false
                },
                labelLine: {
                  show: false
                }
              }
            }
          }, {
            name: '剩余',
            value: 1-ratio*0.01,
            itemStyle: {
              normal: {
                color: '#d8ebfc'
              }
            }
          }]
        }]
      })
    }

    return {
      myRef,
    }
  }
}

</script>

<style scoped>

</style>
