<template>
    <div class="p-5 ">
        <AdminBreadcrumb :location="adminLoginLogs" :menu="adminMenuUser"/>
        <n-h1>登录日志</n-h1>
        <n-text class="mt-5">
            用户登录日志，包含最近1000条。
        </n-text>
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
import {useNotification} from "naive-ui";
import api from "@/request/api"
import {ref, getCurrentInstance} from "vue";
import {formatTimestamp} from "@/util/format";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {createConfig} from "@/request/axios_config";
import {adminLoginLogs} from "@/router";
import {adminMenuUser} from "@/views/menu";
import {popAdminErrorTemplate} from "@/views/util/error";

const {proxy} = getCurrentInstance()

const notification = useNotification()
const page = ref({
    page: 1,
    count: 1
})

const columns = [
    {
        title: "序号",
        key: "id"
    },
    {
        title: "用户ID",
        key: "userId"
    },
    {
        title: "用户名",
        key: "username",
    },
    {
        title: "IP",
        key: "ip"
    },
    {
        title: "时间",
        key: "timestamp"
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
    console.log(page.value.page)
    config.params = {
        page: page.value.page
    }

    proxy.$axios.get(api.getLoginLogs, config).then((res) => {
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
        popAdminErrorTemplate(notification, err, "获取登录日志失败")
    })
}

getLogs()


</script>

<style scoped>

</style>