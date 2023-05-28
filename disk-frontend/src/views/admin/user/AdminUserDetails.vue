<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminUserLists" :menu="adminMenuUser"/>
        <div class="flex items-baseline mt-5">
            <n-h1>用户详情</n-h1>
            <div class="flex flex-grow justify-end">
                <n-button @click="back()">回退</n-button>
            </div>
        </div>
        <n-h2>基本用户信息</n-h2>
        <div>
            <n-table :bordered="false" :single-line="true">
                <tr v-for="info in userInfoPairs">
                    <td>{{ info.name }}</td>
                    <td>{{ info.value }}</td>
                    <td>
                        <n-button>编辑</n-button>
                    </td>
                </tr>
                <!--TODO: allow edit user info -->
            </n-table>
        </div>
    </div>
</template>

<script setup>
import {useNotification} from "naive-ui";
import {useRouter} from "vue-router";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {adminUserDetails, adminUserLists} from "@/router";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {getCurrentInstance, ref} from "vue";
import {adminMenuUser} from "@/views/menu";
import {popAdminErrorTemplate} from "@/views/util/error";

const {proxy} = getCurrentInstance()

const notification = useNotification()

const router = useRouter()
const userId = router.currentRoute.value.params.userId
const source = router.currentRoute.value.query.source

const userInfoPairs = ref([])
const userInfo = ref({})

const menuLocation = ref(adminUserLists)

const back = () => {
    router.push({
        name: adminUserLists
    })
}

const requestUserDetails = () => {
    const config = createConfig()
    proxy.$axios.get(api.userInfo(userId, true), config).then((res) => {
        const pairs = []
        userInfo.value = res.data
        for (let dataKey in res.data) {
            pairs.push({
                key: dataKey,
                name: getKeyName(dataKey),
                value: res.data[dataKey]
            })
        }
        userInfoPairs.value = pairs
    }).catch((error) => {
        popAdminErrorTemplate(notification, error)
    })
}

const getKeyName = (name) => {
    switch (name) {
        case "username":
            return "用户名"
        case "nickname":
            return "昵称"
        case "userId":
            return "用户ID"
        case "role":
            return "角色"
        case "email":
            return "邮箱"
        case "enabled":
            return "是否启用"
        case "locked":
            return "是否锁定"
        case "canceled":
            return "是否注销"
        case "createdAt":
            return "注册时间"
        case "gender":
            return "性别"
        case "birthday":
            return "生日"
        case "phone":
            return "电话"
        case "website":
            return "网站"
        case "location":
            return "位置"
        case "introduction":
            return "简介"
        case "avatar":
            return "头像"
        case "lastLogin":
            return "最后登录时间"
        case "employeeId":
            return "工号"
        case "types":
            return "工作人员类型"
        case "updatedAt":
        case "updateTime":
            return "更新时间"
        case "createTime":
            return "创建时间"
        case "deleted":
            return "是否删除"
        case "allowUser":
            return "允许访问用户接口"
    }
}

requestUserDetails()

</script>