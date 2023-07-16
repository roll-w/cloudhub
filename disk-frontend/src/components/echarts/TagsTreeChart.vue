<template>
    <div class="w-full h-full">
        <n-h1 class="text-center">
            关键词提取
        </n-h1>
        <div ref="chartRef" class="w-full h-[80%]"></div>
    </div>
</template>

<script setup>
import * as echarts from "echarts";
import {onBeforeUnmount, ref} from "vue";
import {getCurrentInstance} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popAdminErrorTemplate} from "@/views/util/error";
import {formatTimestampDash} from "@/util/format";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const data = ref([])
const convertedData = ref([])
const chartRef = ref()

let chartInit = null

const resizeListener = () => {
    if (chartInit !== null) {
        chartInit.resize()
    }
}


const generateFileName = () => {
    return `关键词提取-${formatTimestampDash(new Date().getTime())}`
}

const init = () => {
    if (chartInit === null) {
        chartInit = echarts.init(chartRef.value);
        window.addEventListener('resize', resizeListener)
    }

    let option = {
        series: [
            {
                type: 'treemap',
                data: data.value,
            }
        ],
        toolbox: {
            show: true,
            feature: {
                saveAsImage: {
                    show: true,
                    excludeComponents: ['toolbox'],
                    pixelRatio: 2,
                    name: generateFileName(),
                    title: '保存为图片',
                },
                myToolExport: {
                    show: true,
                    title: "导出为CSV",
                    icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                    onclick: () => {
                        message.info("正在导出CSV文件")
                        const rows = []
                        for (const group of convertedData.value) {
                            for (const tag of group.children) {
                                rows.push({
                                    "id": `${tag.id}`,
                                    "group_id": `${group.id}`,
                                    "group": group.name,
                                    "tag": tag.name,
                                    "count": `${tag.value}`
                                })
                            }
                        }
                        const headers = ["标签ID", "组ID", "组名称", "标签名称", "数量"]
                        const rowsStr = rows.map(row => {
                            return `${row.id},${row.group_id},${row.group},${row.tag},${row.count}`
                        }).join("\n")
                        const csvContent = "data:text/csv;charset=utf-8,\uFEFF" +
                                headers.join(',') + "\n" + rowsStr
                        const link = document.createElement("a");
                        link.href = encodeURI(csvContent);
                        link.download = generateFileName() + ".csv";
                        document.body.appendChild(link);
                        link.click();
                        document.body.removeChild(link);
                    }
                }
            }
        }
    };

    chartInit.setOption(option);
};

const requestStatistics = () => {
    const config = createConfig()

    proxy.$axios.get(api.statistics('tag_count'), config).then(resp => {
        const recvedData = resp.data
        const converted = []

        for (const groupKey in Object.keys(recvedData)) {
            if (!recvedData.hasOwnProperty(groupKey)) {
                continue
            }
            const group = recvedData[groupKey]
            const groupData = {
                name: group.name,
                value: 0,
                children: [],
                id: group.groupId
            }
            for (const tag of group.counts) {
                groupData.children.push({
                    name: tag.name,
                    value: tag.count,
                    id: tag.tagId
                })
                groupData.value += tag.count
            }
            converted.push(groupData)
        }
        convertedData.value = converted
        data.value = converted
        init()
    }).catch(error => {
        popAdminErrorTemplate(notification, error,
                "获取标签统计数据失败", "统计数据请求失败")
    })
}

onBeforeUnmount(() => {
    window.onresize = null
})

requestStatistics()

</script>

<style scoped>

</style>