<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {formatFileSize} from "@/util/format";
import StatisticsCard from "@/components/StatisticsCard.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userStore = useUserStore()
const userStats = ref([])
const options = ref([])

const statsOptions = [
    {
        name: "用户存储用量",
        key: "user_storage_used",
        render: (value) => {
            return formatFileSize(value, "无限制")
        }
    },
    {
        name: "用户文件数量",
        key: "user_storage_count",
        render: (value) => {
            if (value < 0) {
                return "无限制"
            }
            return value
        }
    }
]

const personalStatsToOption = (stats) => {
    const options = []

    statsOptions.forEach(option => {
        const stat = stats.find(stat => stat.key === option.key)
        if (stat) {
            options.push({
                name: option.name,
                value: option.render(stat.value),
                restrict: option.render(stat.restrict),
                percentage: (stat.restrict < 0) ? 0 : ((stat.value / stat.restrict) * 100)
            })
        }
    })

    return options
}

const requestPersonalStats = () => {
    const config = createConfig()

    proxy.$axios.get(api.restricts('user', userStore.user.id),
            config).then(res => {
        userStats.value = res.data
        options.value = personalStatsToOption(res.data)
    }).catch(error => {
        popUserErrorTemplate(notification, error, "获取用户统计数据失败",
                "用户统计数据请求失败")
    })
}

requestPersonalStats()


</script>

<template>
    <div class="p-5">
        <n-h1>
            个人统计数据
        </n-h1>

        <n-grid cols="3" x-gap="10" y-gap="10">
            <n-gi v-for="option in options">
                <StatisticsCard :option="option"/>
            </n-gi>
        </n-grid>

    </div>
</template>

<style scoped>

</style>