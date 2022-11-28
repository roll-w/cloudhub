<template>
  <div ref="myRef" style="width: 200px;height: 200px" data-bs-toggle="tooltip" data-bs-placement="top"
       title="">
  </div>
</template>

<script>
import {ref, onMounted, getCurrentInstance, toRefs} from 'vue'

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

    onMounted(() => {
      renderChart() // 生命周期挂载函数渲染图表
    })

    const renderChart = () => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = proxy.$echarts.init(myRef.value)

      let datas = [{
        //Info.value.ratio * 100).toFixed(2
        "value":  (((Info.value.free) / (Info.value.total)) * 100),
        "name": "内存"
      }]
      // var colorList=['#C467FF','#2CAF70 ','#FFA23F','#625AFF','#4B8BFF'];
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
          name: '值',
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
            name: '背景',
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