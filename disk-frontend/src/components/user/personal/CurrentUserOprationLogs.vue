<template>
    <div>
        <n-data-table
                :bordered="false"
                :columns="columns"
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
import {formatTimestamp} from "@/util/format";
import {popUserErrorTemplate} from "@/views/util/error";
import {useNotification} from "naive-ui";
import {getActionName} from "@/views/names";

const {proxy} = getCurrentInstance()
const notification = useNotification()

const page = ref({
    page: 1,
    count: 1
})

const columns = [
    {
        title: "操作",
        key: "name"
    },
    {
        title: "操作信息",
        key: "description",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "操作类型",
        key: "action"
    },
    {
        title: "时间",
        key: "timestamp",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "IP",
        key: "address",
    },
    {
        title: "操作前",
        key: "originContent",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "操作后",
        key: "changedContent",
        ellipsis: {
            tooltip: true
        }
    }
]

const data = ref([])

const getLogs = () => {
    const config = createConfig()
    console.log(page.value.page)
    config.params = {
        page: page.value.page,
        size: 10
    }

    proxy.$axios.get(api.getCurrentUserOperationLogs, config).then((res) => {
        page.value.count = Math.ceil(res.total / res.size)
        page.value.page = res.page

        res.data.forEach((item) => {
            item.timestamp = formatTimestamp(item.timestamp)
            item.action = getActionName(item.action)
            item.description = item.description.format(item.originContent, item.changedContent)
        })

        data.value = res.data
    }).catch((err) => {
        popUserErrorTemplate(notification, err, "获取操作日志失败")
    })
}

getLogs()

</script>

<style scoped>

</style>