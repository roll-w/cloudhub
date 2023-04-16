<template>
    <div>
        <div id="mainBar" style="width: 100%; height: 450px"></div>
    </div>
</template>

<script setup>
import * as echarts from "echarts";
//引入onMounted钩子
import { onMounted } from "vue";
// echarts标准格式
const Init = () => {

    let myChart = echarts.init(document.getElementById("mainBar"));

    let option = {
        title: {
            text: '系统使用前后对比'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {},
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: [ '下载速度', '上传速度',  '存储效率']
        },
        series: [
            {
                name: '使用前',
                type: 'bar',
                data: [  229034, 104970,  530230]
            },
            {
                name: '使用后',
                type: 'bar',
                data: [ 331000, 221594,  681807]
            }
        ]
    };

    myChart.setOption(option);
};

onMounted(() => {
    Init();
});
</script>

<style scoped>

</style>