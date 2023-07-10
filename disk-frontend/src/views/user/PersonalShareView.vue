<template>
    <div class="p-5">
        <n-h1>个人分享</n-h1>
        <div>
            <n-data-table :bordered="false"
                          :columns="columns"
                          :data="personalShares"
                          :row-props="rowProps"
            />

        </div>

        <n-modal
                v-model:show="showShareDetailModal"
                :show-icon="false"
                preset="dialog"
                title="分享详情"
                transform-origin="center">
            <div>
                <div class="flex justify-center text-center py-5">
                    <FileComponent
                            :show-option="false"
                            :file="currentShare.storage">
                    </FileComponent>
                </div>
                <div>
                    <StorageShareConfirm
                            :show-cancel="true"
                            :share-info="currentShare" />
                </div>

            </div>
        </n-modal>
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
import FileComponent from "@/components/file/FileComponent.vue";
import StorageShareConfirm from "@/components/file/forms/StorageShareConfirm.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const showShareDetailModal = ref(false)

const personalShares = ref([])
const columns = [
    {
        title: "名称",
        key: "storage.name"
    },
    {
        title: "创建时间",
        key: "createTime"
    },
    {
        title: "到期时间",
        key: "expireTime",
    },
]

const currentShare = ref({})

const rowProps = (row, index) => {
    return {
        onClick: (e) => {
            currentShare.value = row
            showShareDetailModal.value = true
        }
    }
}

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