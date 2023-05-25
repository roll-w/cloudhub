<template>
    <div class="p-5 leading-relaxed">
        <n-h1 v-if="type === local">
            服务器监控
        </n-h1>
        <n-h1 v-else-if="type === meta">
            元数据服务器监控
        </n-h1>
        <n-h1 v-else-if="type === file">
            <span class="text-amber-500">{{ fileServerId }}</span> 文件服务器监控
        </n-h1>
        <div>
            系统数据每5秒更新一次。
        </div>
        <n-divider/>
        <div class="py-3">
            <n-h2>
                运行信息
            </n-h2>
            <div>
                <n-grid cols="2">
                    <n-gi>
                        <n-grid cols="2">
                            <n-gi>
                                主机名
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.hostName }}
                            </n-gi>
                        </n-grid>
                        <n-grid cols="2">
                            <n-gi>
                                主机地址
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.hostAddress }}
                            </n-gi>
                        </n-grid>
                        <n-grid cols="2">
                            <n-gi>
                                运行用户
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.runUser }}
                            </n-gi>
                        </n-grid>
                        <n-grid cols="2">
                            <n-gi>
                                OS 架构
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.osArch }}
                            </n-gi>
                        </n-grid>
                    </n-gi>
                    <n-gi>
                        <n-grid cols="2">
                            <n-gi>
                                工作文件夹
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.workDir }}
                            </n-gi>
                        </n-grid>
                        <n-grid cols="2">
                            <n-gi>
                                Java 版本
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.javaVersion }}
                            </n-gi>
                        </n-grid>
                        <n-grid cols="2">
                            <n-gi>
                                Java Home
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.javaHome }}
                            </n-gi>
                        </n-grid>
                        <n-grid cols="2">
                            <n-gi>
                                OS 名称
                            </n-gi>
                            <n-gi>
                                {{ serverInfo.env.osName }}
                            </n-gi>
                        </n-grid>
                    </n-gi>
                </n-grid>
            </div>
            <n-divider/>
            <div class="py-3">
                <n-h2>
                    内存使用信息
                </n-h2>
                <div>
                    <n-grid cols="2">
                        <n-gi>
                            <n-grid cols="2">
                                <n-gi>
                                    总内存
                                </n-gi>
                                <n-gi>
                                    {{ formatFileSize(serverInfo.mem.total) }}
                                </n-gi>
                            </n-grid>
                            <n-grid cols="2">
                                <n-gi>
                                    已使用
                                </n-gi>
                                <n-gi>
                                    {{ formatFileSize(serverInfo.mem.used) }}
                                </n-gi>
                            </n-grid>
                            <n-grid cols="2">
                                <n-gi>
                                    剩余
                                </n-gi>
                                <n-gi>
                                    {{ formatFileSize(serverInfo.mem.free) }}
                                </n-gi>
                            </n-grid>
                        </n-gi>
                        <n-gi>
                            <n-grid cols="2">
                                <n-gi>
                                    JVM最大分配内存
                                </n-gi>
                                <n-gi>
                                    {{ formatFileSize(serverInfo.jvm.max) }}
                                </n-gi>
                            </n-grid>
                            <n-grid cols="2">
                                <n-gi>
                                    JVM已分配内存
                                </n-gi>
                                <n-gi>
                                    {{ formatFileSize(serverInfo.jvm.total) }}
                                </n-gi>
                            </n-grid>

                            <n-grid cols="2">
                                <n-gi>
                                    JVM程序已使用
                                </n-gi>
                                <n-gi>
                                    {{ formatFileSize(serverInfo.jvm.used) }}
                                </n-gi>
                            </n-grid>
                            <n-grid cols="2">
                                <n-gi>
                                    JVM剩余
                                </n-gi>
                                <n-gi>
                                    {{ formatFileSize(serverInfo.jvm.free) }}
                                </n-gi>
                            </n-grid>
                        </n-gi>
                    </n-grid>
                    <n-grid cols="2">
                        <n-gi class=" h-[40vh]">
                            <UsagePieChart :ratio="((serverInfo.mem.used / serverInfo.mem.total) * 100).toFixed(3) || 0"
                                           title="内存使用率"/>
                        </n-gi>
                        <n-gi class="h-[40vh]">
                            <UsagePieChart :ratio="((serverInfo.jvm.used / serverInfo.jvm.total)* 100).toFixed(3) || 0"
                                           title="JVM内存使用率"/>
                        </n-gi>
                    </n-grid>
                </div>
            </div>
            <n-divider/>
            <div class="py-3">
                <n-h2>CPU占用信息</n-h2>
                <div>
                    <n-grid cols="2">
                        <n-gi>
                            <n-grid cols="2">
                                <n-gi>
                                    核心数
                                </n-gi>
                                <n-gi>
                                    {{ serverInfo.cpu.cpuCores }}
                                </n-gi>
                            </n-grid>
                            <n-grid cols="2">
                                <n-gi>
                                    总占用
                                </n-gi>
                                <n-gi>
                                    {{ (serverInfo.cpu.sysUsed + serverInfo.cpu.userUsed).toFixed(3) }} %
                                </n-gi>
                            </n-grid>
                        </n-gi>
                    </n-grid>
                </div>
                <n-grid cols="4">
                    <n-gi class="w-[20vw] h-[40vh]">
                        <UsagePieChart :ratio="serverInfo.cpu.sysUsed || 0" title="系统使用率"/>
                    </n-gi>
                    <n-gi class="w-[20vw] h-[40vh]">
                        <UsagePieChart :ratio="serverInfo.cpu.userUsed || 0" title="用户使用率"/>
                    </n-gi>
                    <n-gi class="w-[20vw] h-[40vh]">
                        <UsagePieChart :ratio="serverInfo.cpu.wait || 0" title="等待率"/>
                    </n-gi>
                    <n-gi class="w-[20vw] h-[40vh]">
                        <UsagePieChart :ratio="serverInfo.cpu.free || 0" title="空闲率"/>
                    </n-gi>
                </n-grid>
            </div>
        </div>
        <n-divider/>
        <div>
            <n-h2>
                当前挂载磁盘信息
            </n-h2>
            <n-grid cols="2">
                <n-gi>
                    <div>
                        <n-grid cols="2">
                            <n-gi>
                                <n-grid cols="2">
                                    <n-gi>
                                        总容量
                                    </n-gi>
                                    <n-gi>
                                        {{ formatFileSize(serverInfo.disk.total) }}
                                    </n-gi>
                                </n-grid>
                                <n-grid cols="2">
                                    <n-gi>
                                        已占用容量
                                    </n-gi>
                                    <n-gi>
                                        {{ formatFileSize(serverInfo.disk.total - serverInfo.disk.free) }}
                                    </n-gi>
                                </n-grid>
                                <n-grid cols="2">
                                    <n-gi>
                                        剩余容量
                                    </n-gi>
                                    <n-gi>
                                        {{ formatFileSize(serverInfo.disk.free) }}
                                    </n-gi>
                                </n-grid>
                            </n-gi>
                            <n-gi>

                            </n-gi>
                        </n-grid>
                    </div>
                    <div class="h-[40vh] py-3">
                        <DoubleLineChart :change="changeFlag"
                                         :recv="((serverInfo.disk.write || 0) / 1024).toFixed(3)"
                                         :send="((serverInfo.disk.read || 0) / 1024).toFixed(3)"
                                         recv-label="写入速率"
                                         send-label="读取速率"
                                         title="磁盘IO速率 (KB/s)"
                                         y-axis-label="(KB/s)"/>
                    </div>
                </n-gi>
                <n-gi>
                    <div class="h-[40vh]">
                        <UsagePieChart
                                :ratio="((serverInfo.disk.total - serverInfo.disk.free) / serverInfo.disk.total * 100).toFixed(3) || 0"
                                title="磁盘占用"/>
                    </div>

                </n-gi>
            </n-grid>


        </div>
        <n-divider/>
        <div>
            <n-h2>
                网络流量信息
            </n-h2>
            <div class="h-[40vh] py-3">
                <DoubleLineChart :change="changeFlag"
                                 :recv="((serverInfo.net.recv || 0) / 1024).toFixed(3)"
                                 :send="((serverInfo.net.sent || 0) / 1024).toFixed(3)"
                                 recv-label="接收速率"
                                 send-label="发送速率"
                                 title="网络IO速率 (KB/s)"
                                 y-axis-label="(KB/s)"/>
            </div>
        </div>
        <n-divider/>
        <div v-if="type === meta">
            <n-h2>
                文件服务器信息
            </n-h2>
            <n-table striped>
                <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">服务器地址</th>
                    <th scope="col">服务器状态</th>
                    <th scope="col">详情信息</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="server in servers" :key="server.id">
                    <th scope="row">{{ server.serverId }}</th>
                    <th scope="row">{{ server.host }}</th>
                    <th scope="row">{{ server.state }}</th>
                    <th scope="row">
                        <div class="d-grid gap-2 d-md-flex justify-content-center">
                            <n-button :disabled="!server.active" @click="handleClickFileServer(server)">
                                运行状态
                            </n-button>
                        </div>
                    </th>
                </tr>
                </tbody>
            </n-table>
        </div>

    </div>

</template>

<script setup>

import {getCurrentInstance, onUnmounted, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popAdminErrorTemplate} from "@/views/util/error";
import UsagePieChart from "@/components/charts/UsagePieChart.vue";
import DoubleLineChart from "@/components/charts/DoubleLineChart.vue";
import {adminClusterMonitor, adminFileServerMonitor, adminSystemMonitor} from "@/router";
import {formatFileSize} from "@/util/format";

const router = useRouter()
const getType = (routerName) => {
    switch (routerName) {
        case adminSystemMonitor:
            return local
        case adminClusterMonitor:
            return meta
        case adminFileServerMonitor:
            return file
    }
}
const routerName = router.currentRoute.value.name
const type = ref(getType(routerName))
const fileServerId = router.currentRoute.value.params.serverId || ""

const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const changeFlag = ref(false)
const servers = ref([])
const serverInfo = ref({
    cpu: {},
    jvm: {},
    mem: {},
    disk: {},
    net: {},
    env: {}
})

const getServerUrl = () => {
    switch (type.value) {
        case local:
            return api.serverStatus
        case meta:
            return api.cfsStatus("meta")
        case file:
            return api.cfsStatus(fileServerId)
    }
}
const handleClickFileServer = (server) => {
    router.push({
        name: adminFileServerMonitor,
        params: {
            serverId: server.serverId
        }
    })
}


const getConnectedFileServers = () => {
    proxy.$axios.get(api.fileServers, createConfig()).then(resp => {
        let serversTemp = []
        serversTemp = serversTemp.concat(resp.data.activeServers.map(server => ({
            state: "活动",
            active: true,
            ...server
        })))
        serversTemp = serversTemp.concat(resp.data.deadServers.map(server => ({
            state: "宕机",
            active: false,
            ...server
        })))
        servers.value = serversTemp
    }).catch(error => {
        popAdminErrorTemplate(notification, error, "获取文件服务器信息失败")
    })
}

const requestServerInfo = () => {
    proxy.$axios.get(getServerUrl(), createConfig()).then(resp => {
        serverInfo.value = resp.data
        changeFlag.value = !changeFlag.value
        if (type.value === meta) {
            getConnectedFileServers()
        }
    }).catch(error => {
        popAdminErrorTemplate(notification, error, "获取服务器信息失败")
    })
}


requestServerInfo()

const timer = setInterval(() => {
    requestServerInfo()
}, 5000)

onUnmounted(() => {
    clearInterval(timer)
})

</script>


<script>
const local = "local"
const meta = "meta"
const file = "file"

</script>
<style scoped>

</style>