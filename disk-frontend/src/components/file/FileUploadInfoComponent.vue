<template>
    <div class="py-3"
         @mouseenter="fileMenuShowState = true"
         @mouseleave="fileMenuShowState = false"
    >
        <div class="flex px-3 items-center">
            <div>
                <n-icon size="35">
                    <FileIcon/>
                </n-icon>
            </div>
            <div class="w-[75%] flex flex-col pl-2 pb-2">
                <div class="truncate ">
                    <n-tooltip trigger="hover">
                        <template #trigger>
                            <div class="truncate">
                                {{ file.name }}
                            </div>
                        </template>
                        <template #default>
                            {{ file.name }}
                        </template>
                    </n-tooltip>
                </div>
                <div class="text-sm text-neutral-400">
                    <span>{{ formatFileSize(file.size) }}</span>
                    <span class="mx-2 select-none">
                        ·
                    </span>
                    <span>{{ progressValue }} </span>
                </div>
            </div>
            <div v-if="!uploaded" :class="['flex flex-fill pr-3 transition-all justify-end ',
            fileMenuShowState  ? 'opacity-100' : 'opacity-0']">
                <n-space>
                    <n-tooltip trigger="hover">
                        <template #trigger>
                            <n-button circle secondary size="small" @click="handleCancel">
                                <n-icon>
                                    X
                                </n-icon>
                            </n-button>
                        </template>
                        <template #default>
                            取消上传
                        </template>
                    </n-tooltip>
                    <n-tooltip trigger="hover">
                        <template #trigger>
                            <n-button circle secondary size="small">
                                <n-icon>
                                    <RefreshRound/>
                                </n-icon>
                            </n-button>
                        </template>
                        <template #default>
                            继续上传
                        </template>
                    </n-tooltip>
                </n-space>
            </div>
        </div>
        <n-progress v-if="!uploaded"
                    :border-radius="0"
                    :color="colors[file.status]"
                    :height="2"
                    :percentage="percentage"
                    :show-indicator="false"
                    :stroke-width="1"
                    processing
                    type="line"/>
        <div v-else class="h-[2px]"></div>
    </div>

</template>

<script setup>

import FileIcon from "@/components/icon/FileIcon.vue";
import {ref, watch} from "vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";
import {formatFileSize} from "@/util/format";

const colors = {
    uploading: '#FFC107',
    success: '#4CAF50',
    error: '#F44336',
    warning: '#FF9800',
    cancel: '#E0E0E0',
    default: '#E0E0E0',

}

const fileMenuShowState = ref(false)

const props = defineProps({
    file: {
        type: Object,
        required: true
    }
})


const uploaded = ref(props.file.status === 'success')

const getProgress = () => {
    const status = props.file.status
    if (status === 'success') {
        return '文件已成功上传'
    }
    if (status === 'error') {
        return '文件上传失败'
    }
    if (status === 'cancel') {
        return '文件上传已取消'
    }
    return props.file.progress + '%'
}

console.log(props.file)

const progressValue = ref(getProgress())
const percentage = ref(props.file.progress)

props.file.onUploadCallback = (progress, status) => {
    uploaded.value = status === 'success'
    progressValue.value = getProgress()
    percentage.value = progress
}

const handleCancel = () => {
    if (props.file.cancel) {
        props.file.cancel()
    }
}

</script>

<style scoped>

</style>