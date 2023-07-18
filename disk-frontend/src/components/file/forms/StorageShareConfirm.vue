<template>
    <div class="leading-loose">
        <div class="pb-3">
            <div class="flex align-bottom">
                <div class="flex-grow-1 flex-fill text-xl font-bold">
                    分享链接
                </div>
                <div v-if="shareInfo.expireTime === '1' || shareInfo.expireTime === 1"
                     class="text-sm font-normal text-neutral-500 justify-self-end">
                    已取消
                </div>
                <div v-else class="text-sm font-normal text-neutral-500 justify-self-end">
                    有效期至 {{ formatTimestamp(shareInfo.expireTime, "永久有效") }}
                </div>
            </div>

            <div>
                {{ getUrl() }}
            </div>

        </div>
        <div v-if="shareInfo.password" class="pb-3">
            <div class="text-xl font-bold">分享密码</div>
            <div>
                {{ shareInfo.password }}
            </div>
        </div>
        <div>
            <n-button secondary type="primary" @click="handleCopy">
                复制链接
            </n-button>
            <n-button v-if="showCancel" tertiary type="default" @click="handleCancel">
                取消分享
            </n-button>
        </div>
    </div>
</template>

<script setup>
import {getCurrentInstance} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import {formatTimestamp} from "../../../util/format";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const props = defineProps({
    shareInfo: {
        type: Object,
        required: true
    },
    showCancel: {
        type: Boolean,
        default: false
    },
})


const getUrl = (password = props.shareInfo.password) => {
    return `${window.location.protocol}//${window.location.host}/s/${props.shareInfo.shareCode}` +
            `${password ? '?pwd=' + props.shareInfo.password : ''}`
}


const handleCopy = () => {
    const url = getUrl()
    navigator.clipboard.writeText('Cloudhub 法律案件资料库分享给你：' + url +
            (props.shareInfo.password ? " 提取码：" + props.shareInfo.password + "，" : " ")
            + "点击链接查看或保存文件").then(() => {
        message.success('复制成功')
    }, () => {
    })
}

const handleCancel = () => {
    dialog.error({
        title: '取消分享',
        content: '确定要取消分享吗？',
        positiveText: '确定',
        negativeText: '取消',
        onPositiveClick: () => {
            requestCancel()
        }
    })
}

const requestCancel = () => {
    const config = createConfig()
    proxy.$axios.delete(api.userShare(props.shareInfo.id), config).then(() => {
        message.success('取消成功')
    }).catch(error => {
        popUserErrorTemplate(notification, error,
                '取消分享失败', "分享请求错误")
    })
}

</script>

<style scoped>

</style>