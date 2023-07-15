<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminUserLists" :menu="adminMenuUser"/>
        <div class="flex items-baseline mt-5">
            <n-h1>
                <span class="text-amber-400">
                    {{ userInfo.username }}
                </span>
                用户详情
            </n-h1>
            <div class="flex flex-grow justify-end">
                <n-button @click="back()">回退至用户管理</n-button>
            </div>
        </div>
        <n-h2>用户关联资源管理入口</n-h2>
        <div>
            <n-space>
                <n-button v-for="entry in userRelatedResourceEntries"
                          @click="handleUserRelatedClick(entry.key)"
                          secondary size="large" type="primary">
                    {{ entry.name }}
                </n-button>
            </n-space>
        </div>
        <n-h2>
            重置用户密码
        </n-h2>
        <div>
            <n-button secondary type="error" @click="showResetPasswordModal = true">
                重置用户密码
            </n-button>
        </div>

        <n-h2>基本用户信息</n-h2>
        <div>
            <n-form ref="form" :rules="rules" :model="formValues">
                <div v-for="info in userInfoPairs">
                    <DisplayInput :config="findFieldConfig(info.key)"
                                  v-model:value="formValues[info.key]" />
                </div>
                <div>
                    <DisplayInput name="用户组ID" :modify="false"
                                  :value="userGroup.id" />
                </div>
                <div>
                    <DisplayInput name="用户组名称" :modify="false"
                                  :value="userGroup.name" />
                </div>
            </n-form>
            <n-button secondary type="primary" @click="handleSaveConfirm">保存</n-button>
        </div>

        <div>

            <n-modal v-model:show="showResetPasswordModal"
                     :show-icon="false"
                     preset="dialog"
                     title="重置密码"
                     transform-origin="center">
                <AdminUserResetPasswordForm
                        :user-id="userId"
                        :on-cancel="() => showResetPasswordModal = false"
                        :on-after-action="() => showResetPasswordModal = false"

                />
            </n-modal>

        </div>

    </div>
</template>

<script setup>
import {useNotification, useMessage, useDialog} from "naive-ui";
import {useRouter} from "vue-router";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {adminUserGroupDetails, adminUserLists} from "@/router";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {getCurrentInstance, ref} from "vue";
import {adminMenuUser} from "@/views/menu";
import {popAdminErrorTemplate} from "@/views/util/error";
import {formatTimestamp} from "@/util/format";
import DisplayInput from "@/components/admin/DisplayInput.vue";
import {useUserRulesOf} from "@/views/rules";
import AdminUserResetPasswordForm from "@/views/admin/user/AdminUserResetPasswordForm.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const form = ref()
const rules = useUserRulesOf(['nickname', 'email', 'role'])
const formValues = ref({})

const userId = router.currentRoute.value.params.userId
const source = router.currentRoute.value.query.source

const showResetPasswordModal = ref(false)

const userInfoPairs = ref([])
const userInfo = ref({})

const userGroup = ref({})

const menuLocation = ref(adminUserLists)

const userRelatedResourceEntries = [
    {
        name: "用户所属用户组",
        key: "group",
    },
    {
        name: "用户文件",
        key: "file",
    },
    {
        name: "用户分享",
        key: "share",
    },
    {
        name: "用户操作日志",
        key: "operationLog",
    },
    {
        name: "用户登录日志",
        key: "loginLog",
    },
    {
        name: "用户数据",
        key: "data",
    },
]


const fieldConfig = [
    {
        key: "userId",
        name: "用户ID",
        modify: false,
    },
    {
        key: "username",
        name: "用户名",
        modify: false,
    },
    {
        key: "nickname",
        name: "昵称",
        modify: true,
        placeholder: "请输入昵称，不填写则默认为用户名",
    },
    {
        key: "role",
        name: "角色",
        modify: true,
        type: 'select',
        options: [
            {
                label: "管理员",
                value: "ADMIN"
            },
            {
                label: "普通用户",
                value: "USER"
            },
        ]
    },
    {
        key: "email",
        name: "邮箱",
        modify: true,
        placeholder: "请输入邮箱",
    },
    {
        key: "enabled",
        name: "是否启用",
        modify: true,
        type: "checkbox",
    },
    {
        key: "locked",
        name: "是否锁定",
        modify: true,
        type: "checkbox",
    },
    {
        key: "canceled",
        name: "是否注销",
        modify: true,
        type: "checkbox",
    },
    {
        key: "createdAt",
        name: "注册时间",
        modify: false,
        render: (value) => {
            return formatTimestamp(value)
        }
    },
    {
        key: "updatedAt",
        name: "更新时间",
        modify: false,
        render: (value) => {
            return formatTimestamp(value)
        }
    },

]

const findFieldConfig = (key) => {
    for (let i = 0; i < fieldConfig.length; i++) {
        if (fieldConfig[i].key === key) {
            return fieldConfig[i]
        }
    }
    return null
}

const userPairToUserInfo = (pairs) => {
    const result = {}
    for (let i = 0; i < pairs.length; i++) {
        result[pairs[i].key] = pairs[i].value
    }
    return result
}

const handleUserRelatedClick = (key) => {
    switch (key) {
        case "group":
            router.push({
                name: adminUserGroupDetails,
                params: {
                    id: userGroup.value.id
                }
            })
            break
        case "file":
            break
        case "share":
            break
        case "operationLog":
            break
        case "loginLog":
            break
        case "data":
            break
    }
}


const handleSaveConfirm = () => {
    form.value.validate().then(() => {
        const userInfo = formValues.value
        requestUserUpdate(userInfo)
    }).catch(() => {
        message.error("保存失败，请检查输入")
    })


}

const back = () => {
    router.push({
        name: adminUserLists
    })
}

const sortByFieldConfig = () => {
    const result = []
    for (let i = 0; i < fieldConfig.length; i++) {
        for (let j = 0; j < userInfoPairs.value.length; j++) {
            if (fieldConfig[i].key === userInfoPairs.value[j].key) {
                result.push(userInfoPairs.value[j])
            }
        }
    }
    userInfoPairs.value = result
}

const requestUserGroup = () => {
    const config = createConfig()
    proxy.$axios.get(api.ownerUserGroup(userId, 'user', true), config).then((res) => {
        userGroup.value = res.data
    }).catch((error) => {
        popAdminErrorTemplate(notification, error, "获取用户所属用户组失败")
    })
}

const requestUserDetails = () => {
    const config = createConfig()
    proxy.$axios.get(api.userInfo(userId, true), config).then((res) => {
        const pairs = []
        userInfo.value = res.data
        formValues.value = res.data
        for (let dataKey in res.data) {
            pairs.push({
                key: dataKey,
            })
        }
        userInfoPairs.value = pairs
        sortByFieldConfig()
    }).catch((error) => {
        popAdminErrorTemplate(notification, error)
    })
}

const requestUserUpdate = (userInfo) => {
    const config = createConfig()
    proxy.$axios.put(api.userInfo(userId, true),{
        nickname: userInfo.nickname,
        role: userInfo.role,
        email: userInfo.email,
        enabled: userInfo.enabled,
        locked: userInfo.locked,
        canceled: userInfo.canceled,
    }, config).then((res) => {
        message.success("用户信息更新成功")
        requestUserDetails()
    }).catch((error) => {
        popAdminErrorTemplate(notification, error, "用户信息更新失败")
    })
}



requestUserDetails()
requestUserGroup()

</script>