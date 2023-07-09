<template>
    <n-breadcrumb separator=">">
        <n-breadcrumb-item>
            <slot name="root"></slot>
            <span v-if="!$slots.root" :class="getClass()"
                  @click="$router.push({name: driveFilePage})">
                  文件
            </span>
        </n-breadcrumb-item>
        <n-breadcrumb-item
                v-for="folder in (folderInfo.parents || [])">
            <span :class="getClass()"
                  @click="handleFolderClick($event, folder)">
                {{ folder.name }}
            </span>
        </n-breadcrumb-item>
        <n-breadcrumb-item v-if="folderInfo.storageId !== 0">
            <span :class="getClass()">
                {{ folderInfo.name }}
            </span>
        </n-breadcrumb-item>
    </n-breadcrumb>
</template>

<script setup>
import {driveFilePage, driveFilePageFolder} from "@/router";
import {useRouter} from "vue-router";

const router = useRouter()

const props = defineProps({
    folderInfo: {
        type: Object,
        default: () => {
        }
    },
    onFolderClick: {
        type: Function,
        default: null
    },
    size: {
        type: String,
        default: 'normal'
    }
})

const getClass = () => {
    if (props.size === 'normal') {
        return 'folder-breadcrumb'
    }
    if (props.size === 'small') {
        return 'folder-breadcrumb-sm'
    }
    return 'folder-breadcrumb'
}

const handleFolderClick = (e, folder) => {
    console.log("folder click")
    if (props.onFolderClick) {
        props.onFolderClick(e, folder)
        return
    }
    router.push({
        name: driveFilePageFolder,
        params: {
            folder: folder.storageId
        }
    })

}

</script>

<style scoped>
.folder-breadcrumb {
    @apply text-xl;
}

.folder-breadcrumb-sm {
    @apply text-base;
}
</style>