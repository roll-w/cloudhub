<template>
    <div class="p-5 ">
        <n-h2>
            <span class="text-amber-500">{{ file.name }}</span> 文件权限
        </n-h2>
        <div class="pb-4">
            <n-alert type="info">
                基于公共权限的基础上，还可以设置对单个用户的权限，优先级高于公共权限。
            </n-alert>
        </div>
        <div>
            <div class="grid grid-cols-10">
                <div class="text-gray-600 mr-3">公共权限</div>
                <div class="col-span-9">
                    <n-radio-group v-model:value="permit">
                        <n-radio-button
                                v-for="permission in permissions"
                                :key="permission.value"
                                :label="permission.label"
                                :value="permission.value"
                        />
                    </n-radio-group>
                    <div class="pt-2 pb-4">
                        <n-alert :show-icon="false" type="default">
                            {{ permissions.find(permission => permission.value === permit).info }}
                        </n-alert>
                    </div>
                </div>
            </div>
            <div class="grid grid-cols-10">
                <div class="text-gray-600 mr-3">用户权限</div>
                <div class="col-span-9">
                    <n-table striped>
                        <thead>
                        <tr>
                            <th>用户名</th>
                            <th>权限</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="permission in userPermissions">
                            <td>{{ permission.username }}</td>
                            <td>{{ permission.permission }}</td>
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

import {ref} from "vue";
import {useRouter} from "vue-router";
import {requestFile} from "@/views/temp/files";
import {useDialog, useMessage} from "naive-ui";

const router = useRouter();

const id = router.currentRoute.value.params.id;
const file = ref(requestFile(id))

const dialog = useDialog()
const message = useMessage()

const userPermissionCheckValue = ref([])
const usernameValue = ref()

const showUserPermissionModal = ref(false)

const userPermissions = ref([
    {
        id: 1,
        username: 'user',
        permission: '读取、写入',
    }
])

const permit = ref(1)

const permissions = ref([
    {
        value: 1,
        label: '公开读写',
        info: '所有人都可以读取和修改文件',
    },
    {
        value: 2,
        label: '公开读私有写',
        info: '所有人都可以读取文件，但只有你可以修改文件',
    },
    {
        value: 3,
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
            userPermissions.value = userPermissions.value.filter(userPermission => userPermission.id !== id)
        }
    })
}

const mappingPermission = (permissions) => {
    return permissions.map(permission => {
        if (permission === 'read') {
            return '读取'
        } else if (permission === 'write') {
            return '写入'
        }
    })
}

const handleAddUserPermission = (username, permissions = []) => {
    showUserPermissionModal.value = false

    const latestId = (
        userPermissions.value[userPermissions.value.length - 1] || {id: 0}
    ).id

    userPermissions.value.push({
        id: latestId + 1,
        username,
        permission: mappingPermission(permissions).join('、')
    })
    resetInput()
    console.log(userPermissions.value)

    message.success('添加成功')
}

const resetInput = () => {
    usernameValue.value = ''
    userPermissionCheckValue.value = []
}


</script>