<template>
  <!-- 图表的大小(width与height)在调用该组件时定义 -->
  <div ref="myRef" data-bs-toggle="tooltip" data-bs-placement="top"
       title="">
  </div>
</template>

<script>
import {ref, onMounted, getCurrentInstance} from 'vue'

export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Receive",
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
       receiveRate.push((Math.random() - 0.4) * 10 +receiveRate[receiveRate.length - 1]);
        if (shift) {
          date.shift();
         receiveRate.shift();
        }
        now = new Date(+new Date(now) + oneDay);
      }

      for (let i = 1; i < 50; i++) {
        addData();
      }

      // 指定图表的配置项和数据
      myChart.setOption({
        xAxis: {
          type: 'category',
          boundaryGap: false,
          //receiveRate: date
          show:false
        },
        yAxis: {
          boundaryGap: [0, '50%'],
          type: 'value'
        },
        series: [
          {
            name:'',
            type:'line',
            smooth:true,
            symbol: 'none',
            stack: 'a',
            areaStyle: {
              normal: {
                color: "#ABEBC6",
              }
            },
           receiveRate:receiveRate
          }
        ]
      })
      setInterval(function () {
        addData(true);
        myChart.setOption({
          xAxis: {
          },
          series: [{
            name:'',
            data:receiveRate
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
