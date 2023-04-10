<template>
    <div>
        <h4 style="text-align: center;font-weight: bolder">地区需求分析</h4>
        <div id="main" style="width: 100%; height: 450px"></div>
    </div>
</template>

<script setup>
import * as echarts from "echarts";
//引入onMounted钩子
import { onMounted } from "vue";
// echarts标准格式
const Init = () => {

    let myChart = echarts.init(document.getElementById("main"));

    let option = {
        tooltip: {
            trigger: 'item'
        },
        legend: {
            top: '5%',
            left: 'center'
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                radius: ['40%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 40,
                        fontWeight: 'bold'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [
                    { value: 1048, name: '北京' },
                    { value: 938, name: '上海' },
                    { value: 890, name: '深圳' },
                    { value: 798, name: '广东' },
                    { value: 647, name: '成都' },
                ]
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