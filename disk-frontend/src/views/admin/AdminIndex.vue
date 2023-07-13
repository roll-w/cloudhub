<template>
    <div class="p-5">
        <n-h1>
            Cloudhub 后台系统管理首页
        </n-h1>

        <div class="py-3">
            <n-card>
                <n-space vertical>
                    <StatusIndicator v-if="errorChecks.length"
                                     :messages="getMessages(errorChecks)"
                                     status="error"
                                     title="系统出现严重错误！"/>
                    <StatusIndicator v-if="warningChecks.length"
                                     :messages="getMessages(warningChecks)"
                                     status="warning"
                                     title="需要检查系统状态"/>
                    <StatusIndicator v-if="warningChecks.length === 0 && errorChecks.length === 0"
                                     status="success"
                                     title="系统当前运行状态正常"/>
                </n-space>
            </n-card>
        </div>

        <div class="py-3">
            <n-h2 class="pl-2">
                系统数据概览
            </n-h2>
            <n-grid cols="3" x-gap="10" y-gap="10">
                <n-gi v-for="option in dataOptions">
                    <n-card v-if="!option.hide" :bordered="false" embedded>
                        <div class="text-md">
                            {{ option.name }}
                        </div>
                        <div class="pt-5 text-3xl text-amber-500">
                            <div v-if="option.value !== null">
                                {{ option.value }}
                            </div>
                            <div class="pb-2" v-else>
                                <n-skeleton
                                        height="1.875rem"
                                        width="55%" :sharp="false"/>
                            </div>
                        </div>
                    </n-card>
                </n-gi>
            </n-grid>
        </div>

        <div class="py-3">
            <n-h2 class="pl-2">
                系统快捷入口
            </n-h2>
            <AdminFastEntries :entries="systemEntries"/>
        </div>

    </div>
</template>

<script setup>
import {adminLoginLogs, adminOperationLogs, adminSystemLogs, adminTagGroups, adminUserLists} from "@/router";
import AdminFastEntries from "@/components/admin/AdminFastEntries.vue";
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popAdminErrorTemplate} from "@/views/util/error";
import {formatDuration, formatFileSize} from "@/util/format";
import StatusIndicator from "@/components/StatusIndicator.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const getCFSStatusName = (name) => {
    switch (name) {
        case "SUCCESS":
            return "正常"
        case "UNAVAILABLE":
            return "不可用"
        default:
            return "出现错误"
    }
}

const dataOptions = ref([
    {
        key: "fileCount",
        name: "文件数量",
        value: 136
    },
    {
        key: "userCount",
        name: "用户数量",
        value: 3
    },
    {
        key: "runtime",
        name: "系统运行时间",
        value: "",
        format: (value) => {
            return formatDuration(value)
        }
    },
    {
        key: "memoryUsage",
        name: "内存总占用",
        value: '',
        format: (value) => {
            console.log('formatMemoryUsage', value)
            if (!value) {
                return ''
            }
            return formatFileSize(value.used)
        }
    },
    {
        key: "diskUsage",
        name: "当前磁盘使用",
        value: '',
        format: (value) => {
            if (!value) {
                return ''
            }
            return formatFileSize(value.used)
        }
    },
    {
        key: "cfsStatus",
        name: "CFS集群状态",
        value: "",
        format: (value) => {
            return getCFSStatusName(value)
        }
    },
    {
        key: "activeFileServers",
        name: "在线文件服务器",
        value: ""
    },
    {
        key: "deadFileServers",
        name: "离线文件服务器",
        value: ""
    },
])

const warningChecks = ref([])
const errorChecks = ref([])

const getMessages = (checks) => {
    const messages = []
    checks.forEach(check => {
        if (check.message) {
            messages.push(check.message)
        }
    })
    return messages
}

const systemEntries = [
    {
        name: "存储管理",
    },
    {
        name: "用户管理",
        route: adminUserLists
    },
    {
        name: "登录日志",
        route: adminLoginLogs
    },
    {
        name: "标签管理",
        route: adminTagGroups
    },
    {
        name: "操作日志",
        route: adminOperationLogs
    },
    {
        name: "系统日志",
        route: adminSystemLogs
    }
]

const requestSystemStatusSummary = () => {
    const config = createConfig()
    proxy.$axios.get(api.serverStatusSummary, config).then(resp => {
        const data = resp.data
        dataOptions.value.forEach(option => {
            if (data[option.key] === undefined) {
                return
            }
            if (option.format) {
                option.rawValue = data[option.key]
                option.value = option.format(option.rawValue)
            } else {
                option.value = data[option.key]
                option.rawValue = option.value
            }
            checkAndPushMessage(option)
        })
    }).catch(error => {
        popAdminErrorTemplate(notification, error, '获取系统状态失败')
    })
}

const checkAndPushMessage = (option) => {
    console.log(option)
    switch (option.key) {
        case "cfsStatus":
            if (option.rawValue !== "SUCCESS") {
                errorChecks.value.push({
                    key: option.key,
                    message: "CFS集群状态异常"
                })
            } else {
                removeByKey(errorChecks, option.key)
            }
            break

        case "deadFileServers":
            if (option.rawValue > 0) {
                warningChecks.value.push({
                    key: option.key,
                    message: "有 " + option.value + " 个文件服务器处于离线状态"
                })
            } else {
                removeByKey(warningChecks, option.key)
            }
            break
        case "activeFileServers":
            if (option.rawValue === 0) {
                errorChecks.value.push({
                    key: option.key,
                    message: "没有可用的文件服务器"
                })
            } else {
                removeByKey(errorChecks, option.key)
            }
            break
        case "diskUsage":
            const usage = (option.rawValue.used / option.rawValue.total) * 100
            if (usage > 80) {
                warningChecks.value.push({
                    key: option.key,
                    message: `系统磁盘使用率已达到 ${usage.toFixed(1)}%，可能会影响存储服务`
                })
                return
            }
            if (usage > 90) {
                errorChecks.value.push({
                    key: option.key,
                    message: `系统磁盘使用率已达到 ${usage.toFixed(1)}%，将影响存储服务`
                })
                return
            }
            removeByKey(warningChecks, option.key)
            break

        case "memoryUsage":
            const memoryUsage = (option.rawValue.used / option.rawValue.total) * 100
            if (memoryUsage > 80) {
                warningChecks.value.push({
                    key: option.key,
                    message: `系统内存使用率已达到 ${memoryUsage.toFixed(1)}%，可能会影响系统性能`
                })
                return
            }
            if (memoryUsage > 90) {
                errorChecks.value.push({
                    key: option.key,
                    message: `系统内存使用率已达到 ${memoryUsage.toFixed(1)}%，可能严重影响系统性能`
                })
                return
            }
            removeByKey(warningChecks, option.key)
            break
    }

}

const removeByKey = (checks, key) => {
    checks.value = checks.value.filter(check => {
        return check.key !== key
    })
}

requestSystemStatusSummary()

</script>
