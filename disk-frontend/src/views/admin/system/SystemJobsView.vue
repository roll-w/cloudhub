<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {adminSystemJobs} from "@/router";
import {adminMenuSystem} from "@/views/menu";
import {getJobStatusName} from "@/views/names";
import {formatTimestamp} from "@/util/format";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popAdminErrorTemplate} from "@/views/util/error";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const columns = [
    {
        title: "任务ID",
        key: "id"
    },
    {
        title: "任务类型",
        key: "taskType",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "触发器类型",
        key: "triggerType",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "任务状态",
        key: "status",
        render: (row) => {
            return getJobStatusName(row.status)
        }
    },
    {
        title: "上次执行时间",
        key: "lastExecuteTime",
        ellipsis: {
            tooltip: true
        },
        render: (row) => {
            return formatTimestamp(row.lastExecuteTime)
        }
    },
    {
        title: "预期下次执行时间",
        key: "nextExecuteTime",
        ellipsis: {
            tooltip: true
        },
        render: (row) => {
            return formatTimestamp(row.nextExecuteTime, "无预期时间")
        }
    }
]

const data = ref([])

const requestSystemJobs = () => {
    const config = createConfig()
    proxy.$axios.get(api.jobs, config)
        .then((response) => {
            data.value = response.data
        })
        .catch((error) => {
            popAdminErrorTemplate(notification, error,
                    "获取系统任务失败", "系统任务请求失败")
        })
}

requestSystemJobs()
</script>

<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminSystemJobs" :menu="adminMenuSystem"/>
        <div>
            <n-h1 class="pt-2">
                系统任务
            </n-h1>
            <div>
                <n-p>
                    系统任务是系统内部执行的任务，包括定时任务和延时任务。
                </n-p>
            </div>
            <div>
                <n-data-table :bordered="false"
                              :columns="columns"
                              :data="data"
                              class="mt-5"
                />
            </div>

        </div>
    </div>

</template>

<style scoped>

</style>