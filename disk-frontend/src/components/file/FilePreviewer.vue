<template>
    <div>
        <div v-if="file.fileType === 'IMAGE'">
            <div class="frame">
                <n-image
                        :img-props="{class: 'h-[70vh] m-auto w-auto'}"

                        :src="url"
                        :theme-overrides="themeOverrides"
                        class="h-[70vh] m-auto w-full" object-fit="contain"/>
            </div>

        </div>
        <div v-else-if="file.fileType === 'TEXT'">
            <div class="frame border border-neutral-200 rounded-md">
                <n-scrollbar class="frame">
                    <n-code :code="text"
                            class="whitespace-pre-wrap "
                            show-line-numbers word-wrap/>
                </n-scrollbar>
            </div>

        </div>
        <div v-else-if="isPdf()">
            <iframe :src="url" class="frame rounded-md" width="auto"></iframe>
        </div>
        <div v-else-if="file.fileType === 'AUDIO'">
            <audio :src="url" class="m-auto" controls/>
        </div>
        <div v-else-if="file.fileType === 'VIDEO'">
            <video :src="url" class="frame rounded-md" controls></video>
        </div>
        <div v-else>
            <div class="w-full h-full flex justify-center items-center">
                暂不支持预览该文件
            </div>
        </div>
    </div>

</template>

<script setup>
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {getCurrentInstance, ref} from "vue";
import {useThemeVars} from "naive-ui";

const {proxy} = getCurrentInstance()

const props = defineProps({
    file: {
        type: Object,
        required: true
    },
})

const userStore = useUserStore()
const url = api.file(props.file.ownerType.toLowerCase(),
        props.file.ownerId, props.file.storageId) +
    "?disposition=inline&token=" + userStore.getToken

const text = ref('')
const requestText = async () => {
    if (props.file.fileType === 'TEXT') {
        await proxy.$axios.get(url).then(res => {
            text.value = res.data
        })
    }
}
requestText()

const isPdf = () => {
    return props.file.name.toLowerCase().endsWith('.pdf')
}

const {popoverColor, boxShadow2, textColor2, borderRadius} = useThemeVars().value;

const themeOverrides = {
    toolbarColor: popoverColor,
    toolbarBoxShadow: boxShadow2,
    toolbarIconColor: textColor2,
    toolbarBorderRadius: borderRadius,
};

</script>

<style scoped>
.frame {
    width: auto;
    margin: auto;
    height: 70vh;
}

</style>

<style>
.n-base-icon {
    width: auto !important;
}
</style>
