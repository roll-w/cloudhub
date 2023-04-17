<template>
    <div>
        <h5 style="text-align: center;font-weight: bolder">达成和解和胜诉案件数量对比</h5>
        <div id="Polar" style="width: 100%; height: 550px"></div>
    </div>
</template>

<script setup>
import * as echarts from "echarts";
//引入onMounted钩子
import { onMounted } from "vue";
// echarts标准格式
const Init = () => {

    let myChart = echarts.init(document.getElementById("Polar"));

    let option = {
        angleAxis: {
            type: 'category',
            data: ['2019', '2020', '2021', '2022', '2023']
        },
        radiusAxis: {},
        polar: {},
        series: [
            {
                type: 'bar',
                data: [300, 500, 700, 1000, 1200],
                coordinateSystem: 'polar',
                name: 'Lawsuits filed',
                stack: 'a',
                emphasis: {
                    focus: 'series'
                }
            },
            {
                type: 'bar',
                data: [200, 400, 600, 800, 1000],
                coordinateSystem: 'polar',
                name: 'Settlements reached',
                stack: 'a',
                emphasis: {
                    focus: 'series'
                }
            },
            {
                type: 'bar',
                data: [100, 200, 300, 400, 500],
                coordinateSystem: 'polar',
                name: 'Cases won',
                stack: 'a',
                emphasis: {
                    focus: 'series'
                }
            }
        ],
        legend: {
            show: true,
            data: ['Lawsuits filed', 'Settlements reached', 'Cases won']
        }
    };


    myChart.setOption(option);
};

onMounted(() => {
    Init();
});
</script>

<style scoped>

</style>