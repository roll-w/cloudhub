<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminOperationLogs" :menu="adminMenuSystem"/>
        <n-h1>操作日志</n-h1>
        <n-text class="mt-5">
            系统中用户的操作日志。按时间倒序排列。
        </n-text>
        <div>
            注：备注中存在关联提示时，表示该操作为关联操作。
        </div>
        <n-data-table
                :bordered="false"
                :columns="columns"
                :data="data"
                :scroll-x="1500"
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
import {NButton, useNotification, useDialog} from "naive-ui";
import api from "@/request/api"
import {ref, h, getCurrentInstance} from "vue";
import {formatTimestamp} from "@/util/format";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {createConfig} from "@/request/axios_config";
import {adminOperationLogs} from "@/router";
import {adminMenuSystem} from "@/views/menu";
import {popAdminErrorTemplate} from "@/views/util/error";
import {getActionName, getFileType} from "@/views/names";
import SystemResourceAction from "@/components/admin/system/SystemResourceAction.vue";

const {proxy} = getCurrentInstance()

const notification = useNotification()

const showModal = ref(false)
const stacktraceInfo = ref({})

const page = ref({
    page: 1,
    count: 1
})

const dialog = useDialog()

const columns = [
    {
        title: "ID",
        key: "id"
    },
    {
        title: "操作人",
        key: "username"
    },
    {
        title: "操作",
        key: "name"
    },
    {
        title: "操作信息",
        key: "description",
        resizable: true,
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
        resizable: true,
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "IP",
        key: "address",
    },
    {
        title: "操作资源",
        key: "action",
        render(row) {
            return h(SystemResourceAction, {
                resourceId : row.resourceId,
                resourceKind : row.resourceKind
            })
        }
    },
    {
        title: "操作前",
        key: "originContent",
        resizable: true,
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "操作后",
        key: "changedContent",
        resizable: true,
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "备注",
        resizable: true,
        key: "note",
    },

]

const data = ref([])

const getLogs = () => {
    const config = createConfig()
    config.params = {
        page: page.value.page
    }
    proxy.$axios.get(api.getOperationLogsAdmin, config).then((res) => {
        page.value.count = Math.ceil(res.total / res.size)
        page.value.page = res.page
        res.data.forEach((item) => {
            item.timestamp = formatTimestamp(item.timestamp)
            item.action = getActionName(item.action)
            item.description = item.description.format(
                item.originContent,
                item.changedContent
            )
            if (item.associatedTo) {
                item.note = "关联到 " + item.associatedTo
            }
        })
        data.value = res.data
    }).catch((error) => {
        popAdminErrorTemplate(notification, error, "获取操作日志失败")
    })
}

getLogs()


</script>

<style scoped>

</style>