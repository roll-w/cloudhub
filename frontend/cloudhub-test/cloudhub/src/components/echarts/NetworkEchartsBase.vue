<template>
  <!-- 图表的大小(width与height)在调用该组件时定义 -->
  <div ref="myRef" data-bs-toggle="tooltip" data-bs-placement="top"
       title="">

  </div>
</template>

<script>
import {ref, onMounted, getCurrentInstance} from 'vue'

export default {
  name: "NetworkEchartsBase",
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
      let total = 100 // 总量
      let used = 30

      // 指定图表的配置项和数据
      myChart.setOption({
        title: {
          text: ((used / total) * 100).toFixed(2) + '%',
          x: 'center',
          y: 'center',
          textStyle: {
            fontWeight: 'normal',
            color: '#3a99ef',
            fontSize: '20'
          },
          subtext: '负载状态',  //
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
            value: used,
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
            value: total - used,
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
      myRef
    }
  }
}

</script>

<style scoped>

</style>
