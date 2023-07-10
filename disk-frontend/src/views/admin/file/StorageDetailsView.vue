<script setup>
import {computed, getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButton, NButtonGroup} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate, popUserErrorTemplate} from "@/views/util/error";
import {adminFileLists, adminFolderLists} from "@/router";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {adminMenuFile} from "@/views/menu";
import {getFileType, getUserTypeName} from "@/views/names";
import DisplayInput from "@/components/admin/DisplayInput.vue";
import {formatFileSize, formatTimestamp} from "@/util/format";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const storageInfo = ref({})
const storageAttrs = ref([])
const formValues = ref({
    name: ''
})

const storageId = computed(() => {
    return router.currentRoute.value.params.id
})
const ownerId = computed(() => {
    return router.currentRoute.value.params.ownerId
})
const ownerType = computed(() => {
    return router.currentRoute.value.params.ownerType
})
const storageType = computed(() => {
    const type = router.currentRoute.value.params.type
    if (type === 'folder') {
        return 'folder'
    } else if (type === 'file') {
        return 'file'
    }
    return 'file'
})
const typeName = computed(() => {
    return getFileType(storageType.value)
})

const menuLocation = computed(() => {
    switch (storageType.value) {
        case 'folder':
            return adminFolderLists
        case 'file':
        default:
            return adminFileLists
    }
})

const back = () => {
    router.push({
        name: menuLocation.value,
        query: {}
    })
}

const requestStorageInfo = () => {
    const config = createConfig()
    proxy.$axios.get(api.getStorageInfo(ownerType.value, ownerId.value,
            storageType.value, storageId.value, true), config).then(resp => {
        requestStorageAttr()
        storageInfo.value = resp.data
        formValues.value = {
            name: storageInfo.value.name
        }
    }).catch(error => {
        popAdminErrorTemplate(notification, error, '获取存储信息失败')
    })
}

const requestStorageAttr = () => {
    const config = createConfig()
    proxy.$axios.get(api.getStorageAttributes(
            ownerType.value, ownerId.value,
            storageType.value, storageId.value, true), config)
            .then(res => {
                const index = res.data.findIndex(item => item.name === 'fileType')
                if (index >= 0) {
                    res.data[index].name = '文件类型'
                    res.data[index].dataType = 'fileType'
                    res.data[index].value = getFileType(res.data[0].value)
                }
                storageAttrs.value = res.data
            }).catch(err => {
        popUserErrorTemplate(notification, err,
                '获取存储属性失败', '存储请求错误')
    })
}


requestStorageInfo()


</script>

<template>
    <div class="p-5">
        <AdminBreadcrumb :location="menuLocation" :menu="adminMenuFile"/>
        <div class="flex items-baseline mt-5">
            <n-h1>
                <span class="text-amber-400">
                    {{ storageInfo.name }}
                </span>
                {{ typeName }}
            </n-h1>
            <div class="flex flex-grow justify-end">
                <n-button @click="back">返回</n-button>
            </div>

        </div>
        <div class="grid grid-cols-10 py-3">
            <div class="text-gray-600 mr-3 ">
                {{ typeName }}信息
            </div>
            <div class="col-span-9">
                <DisplayInput :modify="false"
                              :name="typeName + 'ID'"
                              :value="storageInfo.storageId"/>
                <DisplayInput v-model:value="formValues.name"
                              :modify="true"
                              :name="typeName + '名称'"/>
                <DisplayInput :modify="false"
                              :render="getUserTypeName"
                              :value="storageInfo.ownerType"
                              name="所有者类型"/>
                <DisplayInput :modify="false"
                              :value="storageInfo.ownerId"
                              name="所有者ID"/>
                <DisplayInput v-if="storageInfo.size"
                              :modify="false"
                              :render="formatFileSize"
                              :value="storageInfo.size"
                              name="文件大小"/>
                <DisplayInput v-if="storageInfo.fileId"
                              :modify="false"
                              :value="storageInfo.fileId"
                              name="文件系统ID"/>
                <DisplayInput :modify="false"
                              :render="formatTimestamp"
                              :value="storageInfo.createTime"
                              name="创建时间"/>
                <DisplayInput :modify="false"
                              :render="formatTimestamp"
                              :value="storageInfo.updateTime"
                              name="修改时间"/>
                <div class="flex">
                    <div class="flex-grow"></div>
                    <div class="py-5">
                        <n-button-group>
                            <n-button type="primary">保存</n-button>
                        </n-button-group>
                    </div>
                </div>
            </div>
        </div>
        <div class="grid grid-cols-10 py-3">
            <div class="text-gray-600 mr-3 ">
                {{ typeName }}属性
            </div>
            <div class="col-span-9">
                <n-table striped>
                    <thead>
                    <tr>
                        <th>名称</th>
                        <th>值</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="tag in storageAttrs || []">
                        <td>{{ tag.name }}</td>
                        <td>
                            <n-tag :bordered="false" type="primary">{{ tag.value }}</n-tag>
                        </td>
                    </tr>
                    </tbody>
                </n-table>
            </div>
        </div>
        <div class="grid grid-cols-10 py-3">
            <div class="text-gray-600 mr-3 ">
                版本信息
            </div>
            <div class="col-span-9">
                <div v-if="storageType === 'folder'">
                    <n-alert type="info">
                        文件夹不支持版本管理。
                    </n-alert>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>