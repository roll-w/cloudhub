<template>
    <div class="p-5 flex flex-col items-stretch">
        <div class="flex flex-grow-1 flex-fill mb-5 mr-5">
            <n-h2>
                <span class="text-amber-500">{{ fileInfo.name }}</span> 文件信息
            </n-h2>
            <div class="flex flex-fill justify-end">
                <n-button secondary type="primary" @click="handleBack">返回</n-button>
            </div>
        </div>

        <div class="flex items-stretch">
            <div class="w-2/5 max-w-[40%] min-w-[40%] mr-4">
                <div class="mb-4 text-xl">
                    属性
                </div>
                <div class="h-100 flex flex-col items-stretch place-items-stretch">
                    <div>
                        <n-table :bordered="false">
                            <thead>
                            <tr>
                                <th>名称</th>
                                <th>值</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr v-for="tag in fileAttrs || []">
                                <td>{{ tag.name }}</td>
                                <td>
                                    <n-tag :bordered="false" type="primary">{{ tag.value }}</n-tag>
                                </td>
                            </tr>
                            </tbody>
                        </n-table>
                    </div>
                    <div class="py-3 self-end justify-end flex-fill">标记错误？
                        <n-button secondary type="primary">重新标记</n-button>
                    </div>
                </div>
            </div>
            <n-divider style="height: auto" vertical/>
            <div class="ml-4">
                <div class="text-xl mb-4">
                    文件操作日志
                </div>
                <n-timeline>
                    <n-timeline-item v-for="(log, i) in fileLogs"
                                     :time="formatTimestamp(log.timestamp)"
                                     :title="log.name" :type="i === 0 ? 'success' : 'default'">
                        <div>
                            <span class="text-amber-500">{{ log.username }}</span>
                            {{ log.description }}
                        </div>
                    </n-timeline-item>
                </n-timeline>
            </div>
        </div>
        <div class="pb-3">
            <div class="text-xl mb-3">版本信息</div>
            <n-alert class="my-4" type="info">
                只有内容修改会被计入版本。如修改文件名、修改权限等操作不会被计入版本。
            </n-alert>

            <div>
                <n-table>
                    <thead>
                    <tr>
                        <th>版本</th>
                        <th>修改时间</th>
                        <th>修改人</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="version in versionInfos">
                        <td>{{ version.version }}</td>
                        <td>{{ formatTimestamp(version.createTime) }}</td>
                        <td>{{ version.username }}</td>
                        <td>
                            <n-button-group>
                                <n-button secondary type="primary">查看</n-button>
                                <n-button secondary type="default">回退</n-button>
                                <n-button secondary type="error" @click="onDeleteVersion(version)">删除</n-button>
                            </n-button-group>
                        </td>
                    </tr>
                    </tbody>
                </n-table>
            </div>
        </div>
    </div>

</template>

<script setup>

import {useRouter} from "vue-router";
import {useDialog, useNotification} from "naive-ui";
import {getCurrentInstance, ref} from "vue";
import {driveFilePage} from "@/router";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {getFileType} from "@/views/names";
import {formatTimestamp} from "@/util/time";

const router = useRouter();

const id = router.currentRoute.value.params.id;
const storageType = router.currentRoute.value.params.type;
const ownerType = router.currentRoute.value.params.ownerType;
const ownerId = router.currentRoute.value.params.ownerId;

const {proxy} = getCurrentInstance()

const fileInfo = ref({})
const fileAttrs = ref([])
const fileLogs = ref([])

const notification = useNotification()
const dialog = useDialog()

const versionInfos = ref([])

const onDeleteVersion = (versionInfo) => {
    dialog.error({
        title: '删除版本',
        content: '确定删除版本 ' + versionInfo.version + ' 吗？',
        positiveText: '删除',
        negativeText: '取消',
        onPositiveClick: () => {
        }
    })
}

const handleBack = () => {
    router.push({
        name: driveFilePage
    })
}

const requestFileAttr = () => {
    const config = createConfig()
    proxy.$axios.get(api.getStorageAttributes(ownerType, ownerId, storageType, id), config)
        .then(res => {
            const index = res.data.findIndex(item => item.name === 'fileType')
            if (index >= 0) {
                res.data[index].name = '文件类型'
                res.data[index].value = getFileType(res.data[0].value)
            }
            fileAttrs.value = res.data

        }).catch(err => {
        popUserErrorTemplate(notification, err,
            '获取文件属性失败', '文件请求错误')
    })
}

const requestFileVersion = () => {
    const config = createConfig()
    proxy.$axios.get(api.getStorageVersions(ownerType, ownerId, storageType, id), config)
        .then(res => {
            versionInfos.value = res.data
        }).catch(err => {
        popUserErrorTemplate(notification, err,
            '获取文件版本信息失败', '文件请求错误')
    })
}

const requestFileLogs = () => {
    const config = createConfig()
    proxy.$axios.get(api.getOperationLogsByResource(storageType, id), config)
        .then(res => {
            res.data.forEach(item => item.description =
                item.description.format(
                    item.originalContent,
                    item.changedContent
                )
            )
            fileLogs.value = res.data
            console.log(res.data)
        }).catch(err => {
        popUserErrorTemplate(notification, err,
            '获取文件日志信息失败', '文件请求错误')
    })
}

const requestFileInfos = () => {
    requestFileAttr()
    requestFileLogs()
    requestFileVersion()
}

const requestFileInfo = () => {
    const config = createConfig()
    proxy.$axios.get(api.getStorageInfo(ownerType, ownerId, storageType, id), config)
        .then(res => {
            fileInfo.value = res.data
            requestFileInfos()
        }).catch(err => {
        popUserErrorTemplate(notification, err,
            '获取文件信息失败', '文件请求错误')
    })
}

requestFileInfo()

</script>