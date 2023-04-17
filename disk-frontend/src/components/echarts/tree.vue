<template>
    <div>
        <h5 style="text-align: center;font-weight: bolder">关键词提取</h5>
        <div id="Tree" style="width: 100%; height: 550px"></div>
    </div>
</template>

<script setup>
import * as echarts from "echarts";
//引入onMounted钩子
import { onMounted } from "vue";
// echarts标准格式
const Init = () => {

    let myChart = echarts.init(document.getElementById("Tree"));

    let  option = {
        series: [
            {
                type: 'treemap',
                data: [
                    {
                        name: '合同',
                        value: 50,
                        children: [
                            {
                                name: '销售合同',
                                value: 20
                            },
                            {
                                name: '租赁合同',
                                value: 15
                            },
                            {
                                name: '服务合同',
                                value: 5
                            },
                            {
                                name: '劳动合同',
                                value: 5
                            },
                            {
                                name: '房地产合同',
                                value: 3
                            },
                            {
                                name: '金融合同',
                                value: 2
                            }
                        ]
                    },
                    {
                        name: '诉讼',
                        value: 30,
                        children: [
                            {
                                name: '劳动纠纷',
                                value: 10
                            },
                            {
                                name: '知识产权侵权',
                                value: 5
                            },
                            {
                                name: '合同纠纷',
                                value: 5
                            },
                            {
                                name: '人身损害赔偿',
                                value: 5
                            },
                            {
                                name: '财产损害赔偿',
                                value: 3
                            },
                            {
                                name: '行政诉讼',
                                value: 2
                            }
                        ]
                    },
                    {
                        name: '法规',
                        value: 20,
                        children: [
                            {
                                name: '环境保护法',
                                value: 5
                            },
                            {
                                name: '消费者权益保护法',
                                value: 3
                            },
                            {
                                name: '公司法',
                                value: 3
                            },
                            {
                                name: '劳动法',
                                value: 2
                            },
                            {
                                name: '合同法',
                                value: 2
                            },
                            {
                                name: '知识产权法',
                                value: 2
                            },
                            {
                                name: '刑法',
                                value: 2
                            },
                            {
                                name: '行政法',
                                value: 1
                            }
                        ]
                    },
                    {
                        name: '知识产权',
                        value: 10,
                        children: [
                            {
                                name: '商标注册',
                                value: 4
                            },
                            {
                                name: '专利申请',
                                value: 3
                            },
                            {
                                name: '著作权保护',
                                value: 2
                            },
                            {
                                name: '域名注册',
                                value: 1
                            }
                        ]
                    },
                    {
                        name: '合规',
                        value: 5,
                        children: [
                            {
                                name: '反垄断合规',
                                value: 2
                            },
                            {
                                name: '金融合规',
                                value: 2
                            },
                            {
                                name: '数据保护合规',
                                value: 1
                            }
                        ]
                    },
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