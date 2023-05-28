<template>
    <div class="p-5">
        <n-h2>个人分享</n-h2>
        <div>
            <n-data-table :bordered="false"
                          :columns="columns"
                          :data="personalShares"
            />

        </div>
    </div>
</template>

<script setup>

import {getCurrentInstance, h, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButtonGroup, NButton} from "naive-ui";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {formatTimestamp} from "@/util/format";
import {driveShareTokenPage} from "@/router";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const personalShares = ref([])
const columns = [
    {
        title: "分享ID",
        key: "shareCode"
    },
    {
        title: "分享密码",
        key: "password"
    },
    {
        title: "创建时间",
        key: "createTime"
    },
    {
        title: "到期时间",
        key: "expireTime",
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
                            }
                        },
                        {default: () => "取消分享"}),
                    h(NButton,
                        {
                            size: 'small',
                            onClick: () => {
                                router.push({
                                    name: driveShareTokenPage,
                                    params: {
                                        token: row.shareCode
                                    },
                                    query: {
                                        pwd: row.password
                                    }
                                })
                            }
                        },
                        {default: () => "打开"}),
                ]
            );
        }
    }
]


const requestPersonalShares = () => {
    const config = createConfig()

    proxy.$axios.get(api.userShares, config).then(res => {
        personalShares.value = res.data
        personalShares.value.forEach(item => {
            item.createTime = formatTimestamp(item.createTime)
            item.expireTime = item.expireTime ? formatTimestamp(item.expireTime) : '永久'
        })
    }).catch(err => {
        popUserErrorTemplate(notification, err, '获取个人分享失败')
    })
}

requestPersonalShares()

</script>

<style scoped>

</style>