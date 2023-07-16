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
        key: "shareCode"
    },
    {
        title: "创建时间",
        key: "createTime"
    },
    {
        title: "到期时间",
        key: "expireTime",
        render: (row) => {
            if (row.expireTime === 1) {
                return '已取消'
            }

            return row.expireTime ? formatTimestamp(row.expireTime) : '永久'
        }
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
                <div>
                    <StorageShareConfirm
                            :share-info="currentShare"
                            :show-cancel="false"/>
                </div>
            </div>
        </n-modal>
    </div>
</template>

<style scoped>

</style>