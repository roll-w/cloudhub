<template>
    <div>
        <h5 style="text-align: center;font-weight: bolder">各类型案件比例</h5>
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
    setTimeout(function () {
        let option = {
            legend: {},
            tooltip: {
                trigger: 'axis',
                showContent: false
            },
            dataset: {
                source: [
                    ['文件类型', '2015年', '2016年', '2017年', '2018年', '2019年', '2020年'],
                    ['法律', 1234, 1423, 1678, 1901, 2145, 2345],
                    ['行政法规', 857, 932, 1065, 1234, 1356, 1567],
                    ['司法解释', 455, 511, 610, 711, 810, 923],
                    ['规章', 234, 289, 342, 390, 456, 501]
                ]
            },
            xAxis: { type: 'category' },
            yAxis: { gridIndex: 0 },
            grid: { top: '55%' },
            series: [
                {
                    type: 'line',
                    smooth: true,
                    seriesLayoutBy: 'row',
                    emphasis: { focus: 'series' }
                },
                {
                    type: 'line',
                    smooth: true,
                    seriesLayoutBy: 'row',
                    emphasis: { focus: 'series' }
                },
                {
                    type: 'line',
                    smooth: true,
                    seriesLayoutBy: 'row',
                    emphasis: { focus: 'series' }
                },
                {
                    type: 'line',
                    smooth: true,
                    seriesLayoutBy: 'row',
                    emphasis: { focus: 'series' }
                },
                {
                    type: 'pie',
                    id: 'pie',
                    radius: '30%',
                    center: ['50%', '25%'],
                    emphasis: {
                        focus: 'self'
                    },
                    label: {
                        formatter: '{b}: {@2012年} ({d}%)'
                    },
                    encode: {
                        itemName: '文件类型',
                        value: '2012年',
                        tooltip: '2012年'
                    }
                }
            ]
        };

        myChart.on('updateAxisPointer', function (event) {
            const xAxisInfo = event.axesInfo[0];
            if (xAxisInfo) {
                const dimension = xAxisInfo.value + 1;
                myChart.setOption({
                    series: {
                        id: 'pie',
                        label: {
                            formatter: '{b}: {@[' + dimension + '年]} ({d}%)'
                        },
                        encode: {
                            value: dimension,
                            tooltip: dimension
                        }
                    }
                });
            }
        });
        myChart.setOption(option);
    });


};

onMounted(() => {
    Init();
});

</script>

<style scoped>

</style>