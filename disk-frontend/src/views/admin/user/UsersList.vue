<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminUserLists" :menu="adminMenuUser"/>
        <div class="flex items-baseline mt-5">
            <n-h1>用户列表</n-h1>
            <div class="flex flex-grow justify-end">
                <n-button>创建新用户</n-button>
            </div>
        </div>
        <n-data-table
                :bordered="false"
                :columns="columns"
                :data="data"
                :pagination="false"
                class="mt-5"
        />
        <div class="flex items-start justify-start mt-5">
            <div>
                <n-pagination v-model:page="page" :page-count="1"/>
            </div>
        </div>
    </div>
</template>

<script setup>
import {getCurrentInstance, h, ref} from "vue";
import {NButton, NButtonGroup, useDialog, useNotification} from "naive-ui";
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {formatTimestamp} from "@/util/time";
import {adminUserLists} from "@/router";
import {adminMenuUser} from "@/views/menu";
import {useRouter} from "vue-router";
import {createConfig} from "@/request/axios_config";
import {popAdminErrorTemplate} from "@/views/util/error";

const {proxy} = getCurrentInstance()
const dialog = useDialog()
const userStore = useUserStore()
const router = useRouter()
const notification = useNotification()

const page = ref(1)

const columns = [
    {
        title: "用户ID",
        key: "userId"
    },
    {
        title: "用户名",
        key: "username"
    },
    {
        title: "昵称",
        key: "nickname"
    },
    {
        title: "角色",
        key: "role"
    },
    {
        title: "电子邮箱",
        key: "email"
    },
    {
        title: "注册时间",
        key: "createdAt"
    },
    {
        title: "最后更新",
        key: "updatedAt"
    },
    {
        title: "已启用",
        key: "enabled",
        render(row) {
            if (row.enabled === true) {
                return "是"
            }
            return "否"
        }
    },
    {
        title: "已锁定",
        key: "locked",
        render(row) {
            if (row.locked === true) {
                return "是"
            }
            return "否"
        }
    },
    {
        title: "已注销",
        key: "canceled",
        render(row) {
            if (row.canceled === true) {
                return "是"
            }
            return "否"
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
                            size: 'small',
                            onClick: () => {
                                // router.push({
                                //   name: adminUserDetails,
                                //   params: {
                                //     userId: row.userId
                                //   }
                                // })
                            }
                        },
                        {default: () => "查看/编辑"}),
                    h(NButton,
                        {
                            size: 'small',
                            onClick: () => {
                                handleDeleteUser(row)
                            }
                        },
                        {default: () => "删除"}),
                ]
            );
        }
    }
]

const data = ref([]);

const handleDeleteUser = (user) => {
    dialog.error({
        title: "确认删除",
        transformOrigin: "center",
        content: `请确认是否删除用户名为'${user.username}'，id为'${user.userId}'的用户？\n删除后用户将转为注销状态。`,
        negativeText: "取消",
        positiveText: "确认",
        onPositiveClick: () => {
            console.log("delete user of " + user.userId)
        },
        onNegativeClick: () => {
        }
    })
}

const requestForData = (page, size) => {
    const config = createConfig()
    config.params = {
        page: page,
        size: size
    }
    proxy.$axios.get(api.getUsers,  config).then((res) => {
        const recvData = res.data
        recvData.forEach((item) => {
            item.createdAt = formatTimestamp(item.createdAt)
            item.updatedAt = formatTimestamp(item.updatedAt)
        })
        data.value = recvData
    }).catch((err) => {
        popAdminErrorTemplate(notification, err, "获取用户列表失败",
            "用户请求错误")
    })
}

requestForData(1, 10)

</script>

<style scoped>

</style>