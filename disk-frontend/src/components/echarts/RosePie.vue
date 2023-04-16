<template>
    <div>
        <div id="mainRose" style="width: 100%; height: 450px"></div>
    </div>
</template>

<script setup>
import * as echarts from "echarts";
//引入onMounted钩子
import { onMounted } from "vue";
// echarts标准格式
const Init = () => {

    let myChart = echarts.init(document.getElementById("mainRose"));

    let option = {
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        toolbox: {
            show: true,
            feature: {
                mark: { show: true },
                dataView: { show: true, readOnly: false },
                restore: { show: true },
                saveAsImage: { show: true }
            }
        },
        series: [
            {
                name: '需求分析',
                type: 'pie',
                radius: [40, 185],
                center: ['50%', '50%'],
                roseType: 'area',
                itemStyle: {
                    borderRadius: 8
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