<template>
    <div class="p-5 ">
        <div class="flex flex-grow-1 flex-fill mb-5 mr-5">
            <n-h2>
                <span class="text-amber-500">{{ fileInfo.name }}</span>
                {{ getFileType(fileInfo.storageType) }}权限
            </n-h2>
            <div class="flex flex-fill justify-end">
                <n-button secondary type="primary" @click="handleBack">返回</n-button>
            </div>
        </div>

        <div class="pb-4">
            <n-alert type="info">
                基于公共权限的基础上，还可以设置对单个用户的权限，优先级高于公共权限。
            </n-alert>
        </div>
        <div>
            <div class="grid grid-cols-10">
                <div class="text-gray-600 mr-3">公共权限</div>
                <div class="col-span-9">
                    <n-radio-group v-model:value="publicPermissionSelect">
                        <n-radio-button
                                v-for="permission in permissions"
                                :key="permission.value"
                                :label="permission.label"
                                :value="permission.value"
                        />
                    </n-radio-group>
                    <div class="pt-2 pb-4">
                        <n-alert :show-icon="false" type="default">
                            {{ (permissions.find(permission => permission.value === publicPermissionSelect) || {}).info }}
                        </n-alert>
                    </div>
                </div>
            </div>
            <div class="grid grid-cols-10">
                <div class="text-gray-600 mr-3">用户权限</div>
                <div class="col-span-9">
                    <div class="pb-4">
                        <n-alert type="info">
                            <div>
                                文件的创建者或所有者始终拥有完整权限，不受此处的限制。
                            </div>
                        </n-alert>
                    </div>
                    <n-table striped>
                        <thead>
                        <tr>
                            <th>用户名</th>
                            <th>权限</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="permission in userPermissionsInput">
                            <td>{{ permission.userId }}</td>
                            <td>{{ permission.permissions }}</td>
                            <td>
                                <n-button-group>
                                    <n-button secondary type="primary">修改</n-button>
                                    <n-button secondary type="error" @click="handleDeleteUserPermission(permission.id)">
                                        删除
                                    </n-button>
                                </n-button-group>
                            </td>
                        </tr>
                        <tr>
                            <td class="text-center" colspan="3">
                                <n-button @click="showUserPermissionModal = true">添加用户</n-button>
                            </td>
                        </tr>
                        </tbody>
                    </n-table>
                </div>
            </div>
        </div>


        <div class="py-4">
            <n-button class="mt-5" type="primary" @click="message.success('保存成功')">保存</n-button>
        </div>

        <n-modal v-model:show="showUserPermissionModal"
                 :show-icon="false"
                 preset="dialog"
                 title="用户权限"
                 transform-origin="center">
            <div>
                <n-form-item label="用户名">
                    <n-input v-model:value="usernameValue" placeholder="输入用户名" type="text"/>
                </n-form-item>
                <n-form-item label="权限">
                    <n-checkbox-group v-model:value="userPermissionCheckValue">
                        <n-checkbox label="读取" value="read"/>
                        <n-checkbox label="写入" value="write"/>
                        <n-checkbox label="拒绝访问" value="denied"/>
                    </n-checkbox-group>
                </n-form-item>
                <n-button-group>
                    <n-button type="primary" @click="handleAddUserPermission(usernameValue, userPermissionCheckValue)">
                        添加
                    </n-button>
                    <n-button secondary type="default" @click="showUserPermissionModal = false">取消</n-button>
                </n-button-group>
            </div>
        </n-modal>
    </div>

</template>

<script setup>

import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useDialog, useMessage, useNotification} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import {
    driveFilePage,
    driveFilePageFolder,
    driveFilePageTypeAudio, driveFilePageTypeDocument,
    driveFilePageTypeImage,
    driveFilePageTypeVideo
} from "@/router";
import {getFileType} from "@/views/names";

const router = useRouter();

const id = router.currentRoute.value.params.id;
const storageType = router.currentRoute.value.params.type;
const ownerType = router.currentRoute.value.params.ownerType;
const ownerId = router.currentRoute.value.params.ownerId;

const {proxy} = getCurrentInstance()

const fileInfo = ref({})
const storagePermission = ref()

const error = ref({
    status: 0,
    message: null
})

const notification = useNotification()
const dialog = useDialog()
const message = useMessage()

const userPermissionCheckValue = ref([])
const usernameValue = ref()

const showUserPermissionModal = ref(false)

const userPermissionsInput = ref([])
const publicPermissionSelect = ref()

const permissions = ref([
    {
        value: "PUBLIC",
        label: '公开读写',
        info: '所有人都可以读取和修改文件',
    },
    {
        value: "PUBLIC_READ",
        label: '公开读私有写',
        info: '所有人都可以读取文件，但只有你可以修改文件',
    },
    {
        value: "PRIVATE",
        label: '私有读写',
        info: '只有你可以读取和修改文件',
    },
])

const handleDeleteUserPermission = (id) => {
    dialog.warning({
        title: '删除用户权限',
        content: '确定删除该用户权限吗？',
        positiveText: '确定',
        negativeText: '取消',
        onPositiveClick: () => {
            userPermissionsInput.value = userPermissionsInput.value.filter(userPermission =>
                userPermission.id !== id)
        }
    })
}

const handleAddUserPermission = (username, permissions = []) => {
    showUserPermissionModal.value = false
}

const resetInput = () => {
    usernameValue.value = ''
    userPermissionCheckValue.value = []
}

const urlSource = (source) => {
    switch (source) {
        case 'audio':
            return driveFilePageTypeAudio
        case 'video':
            return driveFilePageTypeVideo
        case 'image':
            return driveFilePageTypeImage
        case 'document':
            return driveFilePageTypeDocument
        default:
            return driveFilePage
    }
}

const handleBack = () => {
    const refer = router.currentRoute.value.query.refer
    const source = router.currentRoute.value.query.source
    if (refer === 'type' && source) {
        const url = urlSource(source)
        router.push({
            name: url
        })
        return;
    }


    if (error.value.message || fileInfo.value.parentId === 0) {
        router.push({
            name: driveFilePage
        })
        return
    }
    router.push({
        name: driveFilePageFolder,
        params: {
            folder: fileInfo.value.parentId
        }
    })
}

const requestGetStoragePermissions = () => {
    const config = createConfig()
    proxy.$axios.get(api.getStoragePermissions(ownerType, ownerId, storageType, id), config)
        .then(res => {
            const permissions = res.data
            storagePermission.value = permissions

            publicPermissionSelect.value = permissions.publicPermission
            userPermissionsInput.value = permissions.userPermissions
        }).catch(err => {
        popUserErrorTemplate(notification, err,
            '获取存储权限失败', '存储请求错误')
    })
}

const requestFileInfos = () => {
    requestGetStoragePermissions()
}

const requestFileInfo = () => {
    const config = createConfig()
    proxy.$axios.get(api.getStorageInfo(ownerType, ownerId, storageType, id), config)
        .then(res => {
            fileInfo.value = res.data
            requestFileInfos()
        }).catch(err => {
            error.value = {
                status: err.status,
                message: err.tip
            }
            popUserErrorTemplate(notification, err,
                '获取文件信息失败', '存储请求错误')
    })
}

requestFileInfo()

</script>