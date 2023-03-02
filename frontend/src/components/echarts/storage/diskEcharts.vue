<template>
  <div ref="myRef" style="width: 200px;height: 200px" data-bs-toggle="tooltip" data-bs-placement="top"
       title="">
  </div>
</template>

<script>
import {ref, getCurrentInstance, toRefs, watch} from 'vue'

export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "diskEcharts",
  props:{
    Info:{
      type:Object,
      required:true,
    },
  },
  setup(props) {
    const {proxy} = getCurrentInstance() // 获取全局配置项
    const myRef = ref(null) // 获取dom实例

    const {Info} = toRefs(props)
    watch(Info, async () => {
      renderChart()
    })

    const renderChart = () => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = proxy.$echarts.init(myRef.value)
      let used = Info.value.total - Info.value.free
      let value = (( used / Info.value.total) * 100).toFixed(2)
      let datas = [{
        "value":  value,
        "name": "磁盘"
      }]
      let maxArr = (new Array(datas.length)).fill(100);

      // 指定图表的配置项和数据
      myChart.setOption({
        backgroundColor: "#fff",
        grid: {
          left:40,
          right: 40,
          bottom: 20,
          top: 100,
          containLabel: true
        },
        tooltip: {
          trigger: 'item',
          axisPointer: {
            type: 'none'
          },
        },
        xAxis: {
          show: false,
          type: 'value'
        },
        yAxis: [{
          type: 'category',
          inverse: true,
          axisLabel: {
            show: true,
            align: 'right',
          },
          splitLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLine: {
            show: false
          },
          data: datas.map(item => item.name)
        }, {
          type: 'category',
          inverse: true,
          axisTick: 'none',
          axisLine: 'none',
          show: true,
          axisLabel: {
            textStyle: {
              color: '#3196fa',
              fontSize: '12'
            },
            formatter: '{value}%'
          },
          data: datas.map(item => item.value)
        }],
        series: [{
          name: '已用百分比',
          type: 'bar',
          zlevel: 1,
          itemStyle: {
            normal: {
              barBorderRadius: 30,
              color: '#4B8BFF'
            },
          },
          barWidth: 20,
          data: datas
        },
          {
            name: "总量",
            type: 'bar',
            barWidth: 20,
            barGap: '-100%',
            data: maxArr,
            itemStyle: {
              normal: {
                color: '#ededed',
                barBorderRadius: 30,
              }
            },
          },
        ]


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