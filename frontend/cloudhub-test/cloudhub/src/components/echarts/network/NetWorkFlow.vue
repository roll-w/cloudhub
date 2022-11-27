<template>
  <!-- 图表的大小(width与height)在调用该组件时定义 -->
  <div ref="myRef" data-bs-toggle="tooltip" data-bs-placement="top"
       title="">
  </div>
</template>

<script>
import {ref, onMounted, getCurrentInstance} from 'vue'
import $ from 'jquery';
import url from '@/store/api';

export default {
  name: "netFlowAndSend",
  setup() {
    const {proxy} = getCurrentInstance() // 获取全局配置项
    const myRef = ref(null) // 获取dom实例

    onMounted(() => {
      renderChart() // 生命周期挂载函数渲染图表
    })

    const renderChart = () => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = proxy.$echarts.init(myRef.value)


      // 一次展示的数据的条数
      let arrLen = 10;

      // 网络的接收速率与发送速率
      let netFlow = [];
      let x = []; // x 轴

      // 一次展示 10 条数据
      for (let i = 0; i < arrLen; i++){
        netFlow.push(0);
        x.push(0);
      }

      // 指定图表的配置项和数据
      myChart.setOption({
        backgroundColor: '#fff',
        tooltip: {
          trigger: 'axis',
        },
        title: {
          text: `{a|网络流量(bytes/s)}`,
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
          data: ['网络流量'],
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
        // 设置x轴的样式
        xAxis: {
          type: 'category',
          show: false,
          boundaryGap: false, //坐标轴两边留白
        },
        // 设置y轴的样式
        yAxis: [
          {
            type: 'value',
            axisLabel: {
              textStyle: {
                color: '#a8aab0',
                fontStyle: 'normal',
                fontFamily: '微软雅黑',
                fontSize: 12,
              },
              // formatter: '{value}' // 设置y轴数据样式
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
          },
        ],
      })
      setInterval(function () {
        // 删除数组的首个元素,再数组尾部添加新的元素(实现从右向左动态更新的视觉效果)
        function addData(shift) {
          x.push(0);

          netFlow.push((Math.random()*10).toFixed(2));     // ######## 从接口获取数据

          if (shift) {
            x.shift();  // 移除数组的首个元素
            netFlow.shift();
          }
        }
        addData(true);
        myChart.setOption({
          series: [
            {
              name: '网络流量',
              type: 'line',
              itemStyle: {
                normal: {
                  color: '#5DADE2',
                  lineStyle: {
                    color: '#5DADE2',
                    width: 1,
                  },
                  areaStyle: {
                    color: '#AED6F1'
                  },
                },
              },
              data: netFlow,
            }
          ]
        });
      }, 1000); // 定时器
    }

    //url.url_metaServer该url获取的信息是全部的cpu，jvm等建议封装一个公共js，或者在父组件传递。

    const netMessage = ref([]);

    const getNet = () =>{
      $.ajax({
        url:url.url_metaServer,
        type:"GET",
        data:{
          serverId:"meta"
        },
        xhrFields: {
          withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain: true,
        success(resp){
          //由于获取的类型过多，只把net的数据传给netMessage
          if (resp.errorCode === "00000"){
            netMessage.value = resp.data.net;
            console.log("获取成功")
            console.log(resp.data.net);
          }
        },
        error(){
          console.log("获取失败")
        }
      })
    };
    //执行获取数据
    getNet()



    return {
      myRef,
    }
  }
}

</script>

<style scoped>

</style>
