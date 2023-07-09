<script setup>
import {getCurrentInstance, h, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButtonGroup, NButton} from "naive-ui";
import {adminUserDetails, adminUserGroupDetails, adminUserGroupLists} from "@/router";
import {adminMenuUser} from "@/views/menu";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";
import {usePage} from "@/views/util/pages";
import FastPagination from "@/components/FastPagination.vue";
import {formatFileSize, formatTimestamp} from "@/util/format";
import UserGroupCreateForm from "@/components/admin/user/UserGroupCreateForm.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const page = usePage()
const userGroups = ref([])
const defaultUserGroup = ref({})

const showCreateUserGroupModal = ref(false)

const columns = [
    {
        title: "ID",
        key: "id"
    },
    {
        title: "用户组名",
        key: "name",
    },
    {
        title: "用户组描述",
        key: "description",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "创建时间",
        key: "createTime",
        render: (row) => {
            return formatTimestamp(row.createTime)
        }
    },
    {
        title: "更新时间",
        key: "updateTime",
        render: (row) => {
            return formatTimestamp(row.updateTime)
        }
    },

    {
        title: "操作",
        key: "actions",
        render(row) {
            return h(
                    NButtonGroup,
                    {},
                    () => [
                        h(NButton,
                                {
                                    onClick: () => {
                                        router.push({
                                            name: adminUserGroupDetails,
                                            params: {
                                                id: row.id
                                            }
                                        })
                                    }
                                },
                                {default: () => "查看/编辑"}),
                        h(NButton,
                                {
                                    onClick: () => {
                                    }
                                },
                                {default: () => "删除"}),
                    ]
            );
        }
    }
]

const settings = [
    {
        key: "group_file-number-limit",
        name: "文件数量限制",
        format: (value) => {
            if (parseInt(value) === -1) {
                return "无限制"
            }
            return value
        }
    },
    {
        key: "group_quota",
        name: "用户组配额",
        format: (value) => {
            return formatFileSize(value * 1024 * 1024, "无限制")
        }
    }
]

const transformUserGroup = (userGroup) => {
    if (!userGroup || !(userGroup.settings || []).length) {
        return {}
    }
    const result = {}
    result.id = userGroup.id
    result.name = userGroup.name
    result.description = userGroup.description
    result.settings = []
    for (const setting of settings) {
        const settingValue = userGroup.settings
                .find(value => value.key === setting.key)
        if (!settingValue) {
            continue
        }
        if (setting.format) {
            result.settings.push({
                key: setting.key,
                name: setting.name,
                value: setting.format(settingValue.value)
            })
        } else {
            result.settings.push({
                key: setting.key,
                name: setting.name,
                value: settingValue.value
            })
        }
    }
    return result
}


const requestDefaultUserGroup = () => {
    const config = createConfig()
    proxy.$axios.get(api.userGroups(true, 0), config).then(resp => {
        defaultUserGroup.value = resp.data
    }).catch(error => {
        popAdminErrorTemplate(notification, error,
                "获取用户组列表失败", "用户组请求失败")
    })
}

const requestUserGroups = () => {
    const config = createConfig()
    config.params = {
        page: page.value.page,
    }
    proxy.$axios.get(api.userGroups(true), config).then(resp => {
        page.value.count = Math.ceil(resp.total / resp.size)
        page.value.page = resp.page
        userGroups.value = resp.data
    }).catch(error => {
        popAdminErrorTemplate(notification, error,
                "获取用户组列表失败", "用户组请求失败")
    })
}


requestUserGroups()
requestDefaultUserGroup()

const refresh = () => {
    requestUserGroups()
}

</script>

<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminUserGroupLists"
                         :menu="adminMenuUser"/>
        <n-h1>
            用户组列表
        </n-h1>
        <div class="py-2">
            <n-card>
                <n-h2>
                    默认用户组信息
                </n-h2>
                <div>
                    <n-alert type="info">
                        <div>
                            注意：默认用户组的设置项不可编辑，如果需要修改用户的设置项，请创建新的用户组
                        </div>
                    </n-alert>
                </div>
                <div>
                    <div class="text-xl py-2">
                        默认配置
                    </div>
                    <n-table striped>
                        <thead>
                        <tr>
                            <th>设置项</th>
                            <th>设置值</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="setting in transformUserGroup(defaultUserGroup).settings"
                            :key="setting.key">
                            <td>{{ setting.name }}</td>
                            <td>{{ setting.value }}</td>
                        </tr>
                        </tbody>
                    </n-table>
                </div>
            </n-card>
        </div>
        <div class="my-3">
            <div class="flex">
                <n-h2>
                    用户组列表
                </n-h2>
                <div class="flex flex-grow justify-end">
                    <n-button @click="showCreateUserGroupModal = true">
                        创建新用户组
                    </n-button>
                </div>
            </div>
            <n-data-table
                    :bordered="false"
                    :columns="columns"
                    :data="userGroups"
                    class="mt-5"
            />
            <FastPagination :route-name="adminUserGroupLists"/>
        </div>

        <n-modal v-model:show="showCreateUserGroupModal"
                 :show-icon="false"
                 closable
                 preset="dialog"
                 title="创建用户组"
                 transform-origin="center">
            <UserGroupCreateForm
                    :on-after-action="() => {
                        showCreateUserGroupModal = false
                        refresh()
                    }"
                    :on-click-cancel="() => showCreateUserGroupModal = false"/>
        </n-modal>
    </div>
</template>

<style scoped>

</style>