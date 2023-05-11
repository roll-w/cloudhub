<template>
    <n-breadcrumb separator=">">
        <n-breadcrumb-item>
            <span class="folder-breadcrumb"
                  @click="$router.push({name: driveFilePage})">
                  文件
            </span>
        </n-breadcrumb-item>
        <n-breadcrumb-item
                v-for="folder in (folderInfo.parents || [])">
            <span class="folder-breadcrumb"
                  @click="$router.push({
                  name: driveFilePageFolder,
                  params: {
                     folder: folder.storageId
                  }})">
                {{ folder.name }}
            </span>
        </n-breadcrumb-item>
        <n-breadcrumb-item v-if="folderInfo.storageId !== 0">
            <span class="text-xl">
                {{ folderInfo.name }}
            </span>
        </n-breadcrumb-item>
    </n-breadcrumb>
</template>

<script setup>
import {driveFilePage, driveFilePageFolder} from "@/router";

const props = defineProps({
    folderInfo: {
        type: Object,
        default: () => {
        }
    }
})
</script>

<style scoped>
.folder-breadcrumb {
    @apply text-xl;
}
</style>