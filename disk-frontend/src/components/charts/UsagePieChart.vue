<template>
    <div ref="myRef" class="w-full h-full"
         title="">
    </div>
</template>

<script setup>
import {ref, getCurrentInstance, watch, onMounted, onBeforeUnmount} from 'vue'

const props = defineProps({
    ratio: {
        type: Number,
        required: true,
    },
    title: {
        type: String,
        required: true,
    },
})

const {proxy} = getCurrentInstance()
const myRef = ref()

const ratio = ref(props.ratio)

watch(props, async () => {
    ratio.value = props.ratio
    renderChart()
})

onMounted(() => {
    renderChart()
})

let chartInit = null

const resizeListener = () => {
    if (chartInit !== null) {
        chartInit.resize()
    }
}


const renderChart = () => {
    if (!chartInit) {
        chartInit = proxy.$echarts.init(myRef.value)
    }

    chartInit.setOption({
        title: {
            text: props.title + "\n\n" + ratio.value + '%',
            x: 'center',
            y: 'center',
            textStyle: {
                fontWeight: 'normal',
                color: '#ec7d22',
                fontSize: '1.5rem'
            },
            subtext: null,
            subtextStyle: {
                fontWeight: 'normal',
                color: '#000',
                fontSize: '1.4rem'
            },
        },
        tooltip: {
            formatter: function (params) {
                if (!params.name) {
                    return ''
                }
                return params.name + '：' + params.percent + ' %'
            }
        },

        series: [{
            name: 'circle',
            type: 'pie',
            clockWise: true,
            radius: ['50%', '66%'],
            itemStyle: {
                normal: {
                    label: {
                        show: false
                    },
                    labelLine: {
                        show: false
                    }
                }
            },
            hoverAnimation: false,
            data: [{
                value: ratio.value * 0.01,
                name: props.title,
                itemStyle: {
                    normal: {
                        color: { // 颜色渐变
                            colorStops: [{
                                offset: 0,
                                color: '#ef7f3a' // 0% 处的颜色
                            }, {
                                offset: 1,
                                color: '#fbad5e' // 100% 处的颜色
                            }]
                        },
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false
                        }
                    }
                }
            }, {
                name: '',
                value: 1 - ratio.value * 0.01,
                itemStyle: {
                    normal: {
                        color: '#EABA9666'
                    }
                }
            }]
        }]
    })
    window.addEventListener('resize', resizeListener)
}

onBeforeUnmount(() => {
    window.onresize = null
})

</script>

<style scoped>

</style>