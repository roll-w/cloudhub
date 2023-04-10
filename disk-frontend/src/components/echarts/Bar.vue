<template>
    <div>
        <div id="main" style="width: 100%; height: 450px"></div>
    </div>
</template>

<script setup>
import { onMounted } from "vue";
import * as echarts from "echarts";

const data = [];
for (let i = 0; i < 5; ++i) {
    data.push(Math.round(Math.random() * 200));
}

const Init = () => {

    let myChart = echarts.init(document.getElementById("main"));

    let option = {
        xAxis: {
            max: 'dataMax'
        },
        yAxis: {
            type: 'category',
            data: ['公证书', '证据材料', '律师函', '调解协议书', '申诉材料'],
            inverse: true,
            animationDuration: 300,
            animationDurationUpdate: 300,
            max: 5 // only the largest 3 bars will be displayed
        },
        series: [
            {
                realtimeSort: true,
                name: '各类型案件数量分析',
                type: 'bar',
                data: data,
                label: {
                    show: true,
                    position: 'right',
                    valueAnimation: true
                }
            }
        ],
        legend: {
            show: true
        },
        animationDuration: 0,
        animationDurationUpdate: 3000,
        animationEasing: 'linear',
        animationEasingUpdate: 'linear'
    };
    myChart.setOption(option);

    function run() {
        for (var i = 0; i < data.length; ++i) {
            if (Math.random() > 0.9) {
                data[i] += Math.round(Math.random() * 2000);
            } else {
                data[i] += Math.round(Math.random() * 200);
            }
        }
        myChart.setOption({
            series: [
                {
                    type: 'bar',
                    data
                }
            ]
        });
    }
    setTimeout(function () {
        run();
    }, 0);
    setInterval(function () {
        run();
    }, 3000);

};

onMounted(() => {
    Init();
});

</script>

<style scoped>

</style>