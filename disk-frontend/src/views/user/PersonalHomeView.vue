<template>
    <div class="p-5">
        <FileComponentsView
                :disable-preview="false"
                :files="personalFiles"
                :on-folder-click="handleFolderClick">
            <template #title>
                个人文件列表
            </template>
            <template #folder>
                <FolderBreadcrumbs :folder-info="folderInfo"
                                   :on-folder-click="handleFolderClick">
                    <template #root>
                            <span class="text-xl"
                                  @click="$router.push({name: userPersonalPage, params: {id: userId}})">
                                个人文件
                            </span>
                    </template>
                </FolderBreadcrumbs>
            </template>
        </FileComponentsView>
    </div>
</template>
<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import {userPersonalPage, userPersonalPageWithFolder} from "@/router";
import FolderBreadcrumbs from "@/components/file/FolderBreadcrumbs.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userId = router.currentRoute.value.params.id

const personalFiles = ref([])
const folder = router.currentRoute.value.params.folder || 0

const folderInfo = ref({
    parents: []
})

const handleFolderClick = (e, storage) => {
    if (storage.storageId === 0) {
        router.push({
            name: userPersonalPage,
            params: {
                id: userId
            }
        })
        return
    }

    router.push({
        name: userPersonalPageWithFolder,
        params: {
            id: userId,
            folder: storage.storageId
        }
    })
}

const requestPersonalFile = () => {
    const config = createConfig()
    proxy.$axios.get(api.folder('user', userId, folder), config).then(resp => {
        personalFiles.value = resp.data
    }).catch(error => {
        popUserErrorTemplate(notification, error)
    })
}


const requestFolderInfo = () => {
    const config = createConfig()
    if (folder === 0) {
        folderInfo.value = {
            storageId: 0,
            storageType: 'FOLDER',
            parents: []
        }
        return
    }

    proxy.$axios.get(
            api.getStorageInfo('user', userId, 'folder', folder), config)
            .then(res => {
                folderInfo.value = res.data
            })
            .catch(err => {
                popUserErrorTemplate(notification, err,
                        '获取文件夹信息失败', '文件夹信息请求失败')
            })
}

const getPageInfo = () => {
    requestFolderInfo()
    requestPersonalFile()
}

getPageInfo()

</script>

<style scoped>

</style>