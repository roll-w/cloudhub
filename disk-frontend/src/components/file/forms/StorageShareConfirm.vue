<template>
    <div class="p-5 leading-loose">
        <div class="pb-3">
            <div class="text-xl font-bold">
                分享链接
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
        </div>
    </div>
</template>

<script setup>

import {useMessage} from "naive-ui";

const message = useMessage()

const props = defineProps({
    shareInfo: {
        type: Object,
        required: true
    },
})


const getUrl = (password = props.shareInfo.password) => {
    return `${window.location.protocol}//${window.location.host}/s/${props.shareInfo.shareCode}` +
        `${password ? '?pwd=' + props.shareInfo.password : ''}`
}


const handleCopy = () => {
    const url = getUrl()
    navigator.clipboard.writeText('Cloudhub 法律案件资料库分享给你：' + url +
        (props.shareInfo.password ? " 提取码：" + props.shareInfo.password + "，"  : " ")
        + "点击链接查看或保存文件").then(() => {
        message.success('复制成功')
    }, () => {
    })
}

</script>

<style scoped>

</style>