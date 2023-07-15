<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import StorageShareConfirm from "@/components/file/forms/StorageShareConfirm.vue";
import FileComponent from "@/components/file/FileComponent.vue";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {formatTimestamp} from "@/util/format";
import {popUserErrorTemplate} from "@/views/util/error";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const props = defineProps({
    userId: {
        type: [String, Number],
        required: true
    }
})

const showShareDetailModal = ref(false)

const data = ref([])
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

    proxy.$axios.get(api.userShares(props.userId), config).then(res => {
        data.value = res.data
        data.value.forEach(item => {
            item.createTime = formatTimestamp(item.createTime)
            item.expireTime = item.expireTime ? formatTimestamp(item.expireTime) : '永久'
        })
    }).catch(err => {
        popUserErrorTemplate(notification, err, '获取个人分享失败')
    })
}

requestPersonalShares()

</script>

<template>
    <div>
        <n-h2>分享</n-h2>
        <div>
            <n-data-table :bordered="false"
                          :columns="columns"
                          :data="data"
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
                            :file="currentShare.storage"
                            :show-option="false">
                    </FileComponent>
                </div>
                <div>
                    <StorageShareConfirm
                            :share-info="currentShare"
                            :show-cancel="true"/>
                </div>
            </div>
        </n-modal>
    </div>
</template>

<style scoped>

</style>