<template>
    <div>
        <n-data-table
                :bordered="false"
                :columns="loginLogsColumns"
                :data="data"
                class="mt-5"
        />
        <div class="flex items-start justify-start mt-5">
            <div>
                <n-pagination
                        v-model:page="page.page"
                        :on-update-page="getLogs"
                        :page-count="page.count"
                        show-quick-jumper
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import {getCurrentInstance, ref} from "vue";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {formatTimestamp} from "@/util/time";
import {popUserErrorTemplate} from "@/views/util/error";
import {useNotification} from "naive-ui";

const {proxy} = getCurrentInstance()
const notification = useNotification()

const page = ref({
    page: 1,
    count: 1
})

const loginLogsColumns = [
    {
        title: "序号",
        key: "id"
    },
    {
        title: "IP",
        key: "ip"
    },
    {
        title: "登录时间",
        key: "timestamp",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "客户端",
        key: "userAgent",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "是否成功登录",
        key: "success",
    },
]

const data = ref([])

const getLogs = () => {
    const config = createConfig()
    config.params = {
        page: page.value.page,
        size: 10
    }

    proxy.$axios.get(api.getCurrentUserLoginLogs, config).then((res) => {
        page.value.count = Math.ceil(res.total / res.size)
        page.value.page = res.page
        let index = (res.page - 1) * res.size + 1
        res.data.forEach((item) => {
            item.id = index++
            item.timestamp = formatTimestamp(item.timestamp)
            item.success = item.success ? "是" : "否"
        })
        data.value = res.data
    }).catch((err) => {
        popUserErrorTemplate(notification, err, "获取登录日志失败")
    })
}

getLogs()
</script>

<style scoped>

</style>