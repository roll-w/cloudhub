<template>
    <div :class="['flex h-58 flex-col items-center p-6 cursor-pointer '+
             'rounded-2xl transition-all duration-300 ' +
             'ease-in-out w-[220px] ' +
             'dark:bg-gray-800 dark:hover:bg-gray-700 ' +
             'hover:bg-opacity-30 m-2 ',
              siteStore.isDark ? 'hover:bg-neutral-500' : 'hover:bg-neutral-200']"
         @mouseenter="fileMenuShowState = true"
         @mouseleave="fileMenuShowState = false"
    >
        <div v-if="showOption"
             :class="['w-100 flex justify-start transition-all duration-300 items-start align-baseline ',
          fileMenuShowState || checkedState ? 'opacity-100' : 'opacity-0']">
            <n-checkbox
                    v-model:checked="checkedState"
                    @click="stopPropagation"
                    @dblclick="preventDefault"
                    @update:checked="$emit('update:checked', checkedState)"/>
            <div class="pl-3 flex flex-fill justify-end">
                <n-button circle @click="handleMoreOptionsClick($event, file)"
                          @dblclick="preventDefault">
                    <template #icon>
                        <n-icon size="20">
                            <MoreHorizonal20Regular/>
                        </n-icon>
                    </template>
                </n-button>
            </div>
        </div>

        <div class="px-5 pb-3">
            <n-icon v-if="file.storageType === 'FOLDER' " size="80">
                <Folder24Regular/>
            </n-icon>
            <n-icon v-else-if="file.fileType === 'IMAGE'" size="80">
                <ImageOutlined/>
            </n-icon>
            <n-icon v-else-if="file.fileType === 'AUDIO'" size="80">
                <AudioFileOutlined/>
            </n-icon>
            <n-icon v-else-if="file.fileType === 'VIDEO'" size="80">
                <VideoFileOutlined/>
            </n-icon>
            <n-icon v-else size="80">
                <FileIcon/>
            </n-icon>
        </div>
        <div class="w-100 text-center truncate">
            <n-tooltip placement="bottom" trigger="hover">
                <template #trigger>
                    <div class="truncate select-none">
                        {{ file.name }}
                    </div>
                </template>
                {{ file.name }}
            </n-tooltip>
        </div>
        <div class="text-gray-400 select-none">
            {{ formatTimestamp(file.createTime) }}
        </div>
    </div>

</template>

<script>

</script>

<script setup>
import {ref, watch} from 'vue'
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FileIcon from "@/components/icon/FileIcon.vue";
import MoreHorizonal20Regular from "@/components/icon/MoreHorizonal20Regular.vue";
import {NIcon} from "naive-ui";
import {formatTimestamp} from "@/util/format";
import ImageOutlined from "@/components/icon/ImageOutlined.vue";
import AudioFileOutlined from "@/components/icon/AudioFileOutlined.vue";
import VideoFileOutlined from "@/components/icon/VideoFileOutlined.vue";
import {useSiteStore} from "@/stores/site";


const props = defineProps({
    file: {
        type: Object,
        required: true
    },
    showOption: {
        type: Boolean,
        default: true
    },
    checked: {
        type: Boolean,
        default: false
    },
    onClickMoreOptions: {
        type: Function,
        default: (event, file) => {
        }
    },
})

const siteStore = useSiteStore()

const emits = defineEmits([
    'update:checked'
])

const fileMenuShowState = ref(false)
const checkedState = ref(props.checked)

const setCheckedState = () => {
    checkedState.value = props.checked
}

watch(props, (newValue) => {
    setCheckedState()
})

const handleMoreOptionsClick = (event, file) => {
    event.stopPropagation()
    props.onClickMoreOptions(event, file)
}

const preventDefault = (event) => {
    event.preventDefault()
    event.stopPropagation()
}

const stopPropagation = (event) => {
    event.stopPropagation()
}

</script>