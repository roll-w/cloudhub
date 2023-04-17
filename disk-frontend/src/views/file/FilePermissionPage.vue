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
                        <tr>
                            <td>user</td>
                            <td>读取、写入</td>
                            <td>
                                <n-button-group>
                                    <n-button secondary type="primary">修改</n-button>
                                    <n-button secondary type="error">删除</n-button>
                                </n-button-group>
                            </td>
                        </tr>
                        <tr>
                            <td class="text-center" colspan="3">
                                <n-button>添加用户</n-button>
                            </td>
                        </tr>
                        </tbody>
                    </n-table>
                </div>
            </div>
        </div>


        <div class="py-4">
            <n-button class="mt-5" type="primary">保存</n-button>
        </div>
    </div>

</template>

<script setup>

import {ref} from "vue";
import {useRouter} from "vue-router";
import {requestFile} from "@/views/temp/files";
import {useDialog} from "naive-ui";

const router = useRouter();

const id = router.currentRoute.value.params.id;
const file = ref(requestFile(id))

const dialog = useDialog()


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


</script>