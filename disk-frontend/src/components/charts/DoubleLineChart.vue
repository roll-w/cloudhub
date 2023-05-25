<template>
    <div ref="myRef"
         class="w-full h-full"
         title="">
    </div>
</template>

<script setup>
import {ref, getCurrentInstance, onMounted, watch, onBeforeUnmount} from 'vue'

const props = defineProps({
    recv: {
        type: Number,
        required: true,
    },
    send: {
        type: Number,
        required: true,
    },
    change: {
        type: Boolean,
        required: true,
    },
    title: {
        type: String,
        default: '网络IO'
    },
    recvLabel: {
        type: String,
        default: '接收速率'
    },
    sendLabel: {
        type: String,
        default: '发送速率'
    },
    yAxisLabel: {
        type: String,
        default: '(bytes/s)'
    },
})

const {proxy} = getCurrentInstance() // 获取全局配置项
const myRef = ref(null) // 获取dom实例

const receives = ref([])
const sends = ref([])
const times = ref([])

let chartInit = null
const resizeListener = () => {
    if (chartInit !== null) {
        chartInit.resize()
    }
}

const loadData = () => {
    if (receives.value.length > 20) {
        receives.value.shift()
        sends.value.shift()
        times.value.shift()
    }

    receives.value.push(props.recv)
    sends.value.push(props.send)
    times.value.push(new Date().toLocaleTimeString())

    renderChart()
}
onMounted(() => {
    loadData()
})

watch(props, async () => {
    loadData()
})

const renderChart = () => {
    if (!chartInit) {
        chartInit = proxy.$echarts.init(myRef.value)
    }
    chartInit.setOption({
        backgroundColor: 'rgba(0,0,0,0)',
        tooltip: {
            trigger: 'axis',
        },
        title: {
            text: `{a|${props.title}}`,
            textStyle: {
                rich: {
                    a: {
                        fontSize: 16,
                        fontWeight: 600,
                    },
                },
            },
            top: '2%',
        },
        legend: {
            data: [props.sendLabel, props.recvLabel],
            textStyle: {
                align: 'right',
            },
            top: '2%',
            right: '2%',
        },
        grid: {
            left: '0%',
            right: '3%',
            bottom: '0%',
            containLabel: true,
        },
        // 设置x轴的样式
        xAxis: {
            type: 'category',
            show: false,
            boundaryGap: false, //坐标轴两边留白
            data: times.value,

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
                    formatter: `{value} ${props.yAxisLabel}` // 设置y轴数据样式
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
                        opacity: 0.1
                    },
                },
            },
        ],
        series: [
            {
                name: props.recvLabel,
                type: 'line',
                itemStyle: {
                    normal: {
                        color: '#fc824c',
                        lineStyle: {
                            color: '#ec8f50',
                            width: 1,
                        },
                        areaStyle: {
                            color: '#f5b99c'
                        },
                    },
                },
                smooth: true,
                data: receives.value,
            },
            {
                name: props.sendLabel,
                type: 'line',
                itemStyle: {
                    normal: {
                        color: 'rgb(9,188,253)',
                        lineStyle: {
                            color: 'rgb(9,123,253)',
                            width: 1,
                        },
                        areaStyle: {
                            color: '#9fc3f9'
                        },
                    },
                },
                smooth: true,
                data: sends.value,
            },
        ]
    })
    window.addEventListener('resize', resizeListener)
}

onBeforeUnmount(() => {
    window.onresize = null
})
</script>

<style scoped>

</style>
