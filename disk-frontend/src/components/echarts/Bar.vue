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
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
            data: [
                '合同法',
                '劳动法',
                '刑法',
                '民事诉讼法',
                '知识产权法',
                '行政法',
                '宪法',
                '环境保护法',
                '海关法',
                '其他法律文件'
            ]
        },
        series: [
            {
                name: '法律文件分类',
                type: 'pie',
                selectedMode: 'single',
                radius: [0, '30%'],
                label: {
                    position: 'inner',
                    fontSize: 14
                },
                labelLine: {
                    show: false
                },
                data: [
                    { value: 1548, name: '刑法' },
                    { value: 775, name: '合同法' },
                    { value: 679, name: '劳动法', selected: true }
                ]
            },
            {
                name: '法律文件分类',
                type: 'pie',
                radius: ['45%', '60%'],
                labelLine: {
                    length: 30
                },
                label: {
                    formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
                    backgroundColor: '#F6F8FC',
                    borderColor: '#8C8D8E',
                    borderWidth: 1,
                    borderRadius: 4,
                    rich: {
                        a: {
                            color: '#6E7079',
                            lineHeight: 22,
                            align: 'center'
                        },
                        hr: {
                            borderColor: '#8C8D8E',
                            width: '100%',
                            borderWidth: 1,
                            height: 0
                        },
                        b: {
                            color: '#4C5058',
                            fontSize: 14,
                            fontWeight: 'bold',
                            lineHeight: 33
                        },
                        per: {
                            color: '#fff',
                            backgroundColor: '#4C5058',
                            padding: [3, 4],
                            borderRadius: 4
                        }
                    }
                },
                data: [
                    { value: 1048, name: '环境保护法' },
                    { value: 335, name: '刑法' },
                    { value: 310, name: '海关法' },
                    { value: 251, name: '宪法' },
                    { value: 234, name: '知识产权法' },
                    { value: 147, name: '民事诉讼法' },
                    { value: 135, name: '行政法' },
                    { value: 102, name: '其他法律文件' }
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