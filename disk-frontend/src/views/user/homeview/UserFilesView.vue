<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {driveFilePageFolder, userPersonalPage, userPersonalPageWithFolder} from "@/router";
import FolderBreadcrumbs from "@/components/file/FolderBreadcrumbs.vue";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import {getFileViewMenuOptions} from "@/views/file/options";
import {hackFileOptions, handleStorageDownload} from "@/views/file/storage_actions";
import StorageFavoriteForm from "@/components/file/forms/StorageFavoriteForm.vue";
import FileUploadForm from "@/components/file/forms/FileUploadForm.vue";
import FolderCreateForm from "@/components/file/forms/FolderCreateForm.vue";
import {useFileStore} from "@/stores/files";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()
const fileStore = useFileStore()

const props = defineProps({
    userId: {
        type: [String, Number],
        required: true
    },
    folderId: {
        type: [String, Number],
        default: 0
    }
})

const showFavoriteStorageModal = ref(false)
const showUploadFileModal = ref(false)
const showCreateFolderModal = ref(false)

const userId = props.userId
const personalFiles = ref([])
const folder = props.folderId || 0

const fileOptions = [
    {
        label: "下载",
        key: "download",
    },
    {
        label: "收藏",
        key: "favorite",
    },
    {
        label: "跳转到文件夹",
        key: "folder",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "日志",
        key: "log",
    },
    {
        label: "权限",
        key: "permission",
    },
]

const menuOptions = getFileViewMenuOptions()

const curStorage = ref({})
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

const handleFileOptionSelect = (key, options, storage) => {
    curStorage.value = storage
    switch (key) {
        case "download":
            handleStorageDownload(storage, 'user',
                    storage.ownerId, proxy.$axios)
            break
        case "favorite":
            showFavoriteStorageModal.value = true
            break
        case "folder":
            if (storage.ownerId === userStore.user.id) {
                router.push({
                    name: driveFilePageFolder,
                    params: {
                        folder: storage.parentId
                    }
                })
            } else {
                router.push({
                    name: userPersonalPageWithFolder,
                    params: {
                        id: storage.ownerId,
                        folder: storage.parentId
                    }
                })
            }
            break
        case "log":
        case "permission":
            break
        case "delete":
            break
    }
}

const handleMenuOptionSelect = (key, options) => {
    switch (key) {
        case "upload":
            showUploadFileModal.value = true
            break
        case "folder":
            showCreateFolderModal.value = true
            break
        case "refresh":
            getPageInfo()
            break
    }
}

const hackFileOptionsOf = (file) => {
    hackFileOptions(file, fileOptions, {
        refer: 'home_files',
        source: ''
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

const refresh = () => {
    requestPersonalFile()
}

getPageInfo()
</script>

<template>
    <div>
        <FileComponentsView
                :disable-click="false"
                :disable-preview="false"
                :file-options="fileOptions"
                :files="personalFiles"
                :menu-options="menuOptions"
                :on-file-option-select="handleFileOptionSelect"
                :on-folder-click="handleFolderClick"
                :on-menu-select="handleMenuOptionSelect"
                :on-show-file-option="hackFileOptionsOf">
            <template #title>
                <n-h2>个人文件列表</n-h2>
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

        <div>
            <n-modal v-model:show="showFavoriteStorageModal"
                     :show-icon="false"
                     :title="'收藏 ' + curStorage.name"
                     preset="dialog"
                     transform-origin="center">
                <StorageFavoriteForm
                        :on-after-action="() => {
                            showFavoriteStorageModal = false
                        }"
                        :on-click-cancel="() => showFavoriteStorageModal = false"
                        :owner-id="curStorage.ownerId"
                        :storage-id="curStorage.storageId"
                        :storage-type="curStorage.storageType"/>
            </n-modal>

            <n-modal v-model:show="showUploadFileModal"
                     :show-icon="false"
                     preset="dialog"
                     title="上传文件"
                     transform-origin="center">
                <FileUploadForm
                        :folder-id="folder"
                        :on-after-action="() => {
                            refresh()
                        }"
                        :on-before-action="() => {
                            fileStore.showTransferDialog()
                        }"
                        :on-click-cancel="() => showUploadFileModal = false"
                        :on-click-confirm="() => showUploadFileModal = false"
                        :owner-id="userId"
                        owner-type="user"
                />
            </n-modal>

            <n-modal v-model:show="showCreateFolderModal"
                     :show-icon="false"
                     preset="dialog"
                     title="新建文件夹"
                     transform-origin="center">
                <div>
                    <FolderCreateForm
                            :folder-id="folder"
                            :on-after-action="() => {
                                showCreateFolderModal = false
                                refresh()
                            }"
                            :on-click-cancel="() => showCreateFolderModal = false"
                            :on-click-confirm="() => showCreateFolderModal = false"
                            :owner-id="userId"
                            owner-type="user"
                    />
                </div>
            </n-modal>
        </div>
    </div>
</template>

<style scoped>

</style>