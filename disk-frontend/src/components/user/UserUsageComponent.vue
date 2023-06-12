<script setup>
import UserUsageLineChart from "@/components/charts/user/UserUsageLineChart.vue";
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {useSiteStore} from "@/stores/site";
import {formatFileSize} from "@/util/format";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {useUserStore} from "@/stores/user";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userStore = useUserStore()
const siteStore = useSiteStore()

const userStats = ref([])

const userUsage = ref({
    used: 1234567890,
    total: 10 * 1024 * 1024 * 1024
})


const handleClick = () => {
}

const USER_USAGE = "user_storage_used"

const requestUserUsage = () => {
    const config = createConfig()
    proxy.$axios.get(api.restrictByKey('user', userStore.user.id, USER_USAGE),
            config)
            .then((response) => {
                const usage = response.data
                userUsage.value = {
                    used: usage.value,
                    total: (usage.restrict < 0) ? 10240000000 : usage.restrict
                }
            })
            .catch((error) => {
                console.log(error)
                popUserErrorTemplate(notification, error, "获取用户数据失败")
            })

}

requestUserUsage()

</script>

<template>
    <div :class="['px-4 py-5 border rounded-xl cursor-pointer ' +
          'duration-300 ' +
          'hover:bg-opacity-30 ' +
          'transition-all', siteStore.isDark
          ? 'border-neutral-800 hover:bg-neutral-500'
          : 'border-neutral-200 hover:bg-neutral-300 ']"
         @click="handleClick">
        <div class="text-xs text-neutral-500">用量统计</div>
        <div class="flex pb-1 text-sm">
            <div class="flex-fill select-none">
                {{ formatFileSize(userUsage.used) }}
                /
                {{ formatFileSize(userUsage.total) }}
            </div>
        </div>

        <UserUsageLineChart :percentage="(userUsage.used / userUsage.total) * 100"/>
    </div>
</template>

<style scoped>

</style>