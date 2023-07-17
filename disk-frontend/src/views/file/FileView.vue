<template>
    <div>
        <div v-if="curDirectoryId === 0">
            <FileSystemInstructions/>
        </div>
        <div class="flex-fill">
            <div class="p-5">
                <div class="min-h-[400px]">
                    <n-spin :show="loading" size="large">
                        <div>
                            <FileComponentsView :disable-click="false"
                                                :disable-preview="false"
                                                :file-options="fileOptions"
                                                :files="files"
                                                :menu-options="menuOptions"
                                                :on-file-option-select="handleFileOptionSelect"
                                                :on-menu-select="handleMenuSelect"
                                                :on-show-file-option="hackFileOptions"
                                                :on-toolbar-select="handleToolbarSelect"
                                                :toolbar-options="toolbarOptions">
                                <template #folder>
                                    <div v-if="!nowOpenType" class="text-xl py-2">
                                        <FolderBreadcrumbs :folder-info="folderInfo"/>
                                    </div>
                                    <div v-else class="text-xl py-2">
                                        {{ getFileType(nowOpenType.toUpperCase()) }}
                                    </div>
                                </template>

                                <template #empty>
                                    <div class="w-100 h-100 flex flex-col flex-fill content-center justify-center">
                                        <div class="self-center">
                                            <n-empty description="暂无文件">
                                                <template #extra class="pb-[30vh]">
                                                    <n-button-group v-if="!nowOpenType">
                                                        <n-space size="medium">
                                                            <n-button
                                                                    secondary size="large" type="primary"
                                                                    @click="showCreateFolderModal = true">
                                                                新建文件夹
                                                            </n-button>
                                                            <n-button secondary size="large" type="primary"
                                                                      @click="showUploadFileModal = true">
                                                                上传文件
                                                            </n-button>
                                                        </n-space>
                                                    </n-button-group>
                                                </template>
                                            </n-empty>
                                        </div>
                                    </div>
                                </template>
                            </FileComponentsView>
                        </div>
                    </n-spin>
                </div>
            </div>
        </div>


        <n-modal v-model:show="showUploadFileModal"
                 :show-icon="false"
                 preset="dialog"
                 title="上传文件"
                 transform-origin="center">
            <FileUploadForm
                    :folder-id="curDirectoryId"
                    :on-after-action="() => {
                            loading = true
                            refresh()
                        }"
                    :on-before-action="() => {
                        fileStore.showTransferDialog()
                    }"
                    :on-click-cancel="() => showUploadFileModal = false"
                    :on-click-confirm="() => showUploadFileModal = false"
                    :owner-id="userStore.user.id"
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
                        :folder-id="curDirectoryId"
                        :on-after-action="() => {
                            showCreateFolderModal = false
                            loading = false
                            refresh()
                        }"
                        :on-before-action="() => loading = true"
                        :on-click-cancel="() => showCreateFolderModal = false"
                        :on-click-confirm="() => showCreateFolderModal = false"
                        :owner-id="userStore.user.id"
                        owner-type="user"
                />
            </div>
        </n-modal>

        <n-modal v-model:show="showMoveStorageModal"
                 :show-icon="false"
                 :title="storageCopyOrMove === 'copy' ? '复制文件' : '移动文件'"
                 preset="dialog"
                 transform-origin="center">
            <div>
                <StorageMoveOrCopyForm
                        :folder-id="curDirectoryId"
                        :on-after-action="() => {
                            showMoveStorageModal = false
                            loading = false
                            refresh()
                        }"
                        :on-before-action="() => loading = true"
                        :on-click-cancel="() => showMoveStorageModal = false"
                        :owner-id="userStore.user.id"
                        :storage-id="curTargetFile.storageId"
                        :storage-type="curTargetFile.storageType"
                        :type="storageCopyOrMove"
                        owner-type="user"
                />
            </div>
        </n-modal>

        <n-modal v-model:show="showRenameStorageModal"
                 :show-icon="false"
                 :title="'重命名 ' + curTargetFile.name"
                 preset="dialog"
                 transform-origin="center">
            <StorageRenameForm
                    :file-name="curTargetFile.name"
                    :on-after-action="() => {
                    refresh()
                }"
                    :on-before-action="() => {}"
                    :on-click-cancel="() => showRenameStorageModal = false"
                    :on-click-confirm="() => showRenameStorageModal = false"
                    :owner-id="userStore.user.id"
                    :storage-id="curTargetFile.storageId"
                    :storage-type="curTargetFile.storageType"
                    owner-type="user"
            />
        </n-modal>

        <n-modal v-model:show="showFavoriteStorageModal"
                 :show-icon="false"
                 :title="'收藏 ' + curTargetFile.name"
                 preset="dialog"
                 transform-origin="center">
            <StorageFavoriteForm
                    :on-after-action="() => {
                        showFavoriteStorageModal = false
                    }"
                    :on-click-cancel="() => showFavoriteStorageModal = false"
                    :owner-id="userStore.user.id"
                    :storage-id="curTargetFile.storageId"
                    :storage-type="curTargetFile.storageType"
            />
        </n-modal>

        <n-modal v-model:show="showShareStorageModal"
                 :show-icon="false"
                 :title="'分享 ' + curTargetFile.name"
                 preset="dialog"
                 transform-origin="center">
            <StorageShareForm
                    :on-after-action="(info) => {
                        showShareStorageModal = false
                        shareInfo = info
                        showShareConfirmStorageModal = true
                    }"
                    :on-click-cancel="() => showShareStorageModal = false"
                    :owner-id="userStore.user.id"
                    :storage-id="curTargetFile.storageId"
                    :storage-type="curTargetFile.storageType"
                    owner-type="user"
            />
        </n-modal>

        <n-modal v-model:show="showShareConfirmStorageModal"
                 :show-icon="false"
                 :title="'分享 ' + curTargetFile.name"
                 preset="dialog"
                 transform-origin="center">
            <StorageShareConfirm :share-info="shareInfo"/>
        </n-modal>

    </div>
</template>

<script setup>
import {h, ref, getCurrentInstance} from "vue";
import {RouterLink, useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {
    driveFileAttrsPage,
    driveFilePageFolder,
    driveFilePageTypeAudio,
    driveFilePageTypeDocument,
    driveFilePageTypeImage,
    driveFilePageTypeVideo,
    driveFilePermissionPage
} from "@/router";
import {createConfig} from "@/request/axios_config";
import {useUserStore} from "@/stores/user";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import FolderBreadcrumbs from "@/components/file/FolderBreadcrumbs.vue";
import FileSystemInstructions from "@/components/file/FileSystemInstructions.vue";
import StorageRenameForm from "@/components/file/forms/StorageRenameForm.vue";
import FolderCreateForm from "@/components/file/forms/FolderCreateForm.vue";
import FileUploadForm from "@/components/file/forms/FileUploadForm.vue";
import {useFileStore} from "@/stores/files";
import {getFileType} from "@/views/names";
import {options, getFileViewMenuOptions} from "@/views/file/options";
import StorageShareForm from "@/components/file/forms/StorageShareForm.vue";
import StorageShareConfirm from "@/components/file/forms/StorageShareConfirm.vue";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import StorageMoveForm from "@/components/file/forms/StorageMoveOrCopyForm.vue";
import StorageMoveOrCopyForm from "@/components/file/forms/StorageMoveOrCopyForm.vue";
import StorageFavoriteForm from "@/components/file/forms/StorageFavoriteForm.vue";
import ArrowDownload16Filled from "@/components/icon/ArrowDownload16Filled.vue";
import CloseRegualar from "@/components/icon/CloseRegualar.vue";
import Delete24Regular from "@/components/icon/Delete24Regular.vue";
import {handleStorageDownload} from "@/views/file/storage_actions";

const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()
const userStore = useUserStore()
const fileStore = useFileStore()
const router = useRouter()

const routeName = router.currentRoute.value.name

const files = ref([])
const folderInfo = ref({
    parents: []
})

const loading = ref(false)

const showUploadFileModal = ref(false)
const showCreateFolderModal = ref(false)
const showFilePreviewModal = ref(false)
const showRenameStorageModal = ref(false)
const showShareStorageModal = ref(false)
const showFavoriteStorageModal = ref(false)
const showMoveStorageModal = ref(false)
const storageCopyOrMove = ref('move')
const showShareConfirmStorageModal = ref(false)

const shareInfo = ref()

const getNowOpenType = () => {
    switch (routeName) {
        case driveFilePageTypeImage:
            return "image"
        case driveFilePageTypeVideo:
            return "video"
        case driveFilePageTypeAudio:
            return "audio"
        case driveFilePageTypeDocument:
            return "document"
        default:
            return null
    }
}

const nowOpenType = getNowOpenType()

const getCurrentFolderId = () => {
    if (nowOpenType) {
        return -1
    }
    return Number.parseInt(router.currentRoute.value.params.folder) || 0
}

const curDirectoryId = getCurrentFolderId()

const menuOptions = getFileViewMenuOptions(nowOpenType)

const fileOptions = [
    {
        label: "下载",
        key: "download",
    },
    {
        label: "分享",
        key: "share",
    },
    {
        label: "收藏",
        key: "favorite",
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
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "重命名",
        key: "rename",
    },
    {
        label: "移动",
        key: "move",
    },
    {
        label: "复制",
        key: "copy",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: () => h(
                'div',
                {
                    class: "text-red-500 mr-10"
                },
                {default: () => "删除"}
        ),
        key: "delete",
    },
]

const toolbarOptions = [
    {
        key: 'download',
        label: '下载',
        icon: () => h(ArrowDownload16Filled)
    },
    {
        key: 'delete',
        label: '删除',
        icon: () => h(Delete24Regular)
    },
    {
        key: 'cancel',
        label: '取消多选',
        icon: () => h(CloseRegualar)
    }

]

const handleToolbarSelect = (key, selected) => {
    switch (key) {
        case 'download':
            if (checkHasNotFile(selected)) {
                message.error('只能下载文件')
                return false
            }
            const urls = []
            for (let i = 0; i < selected.length; i++) {
                const file = selected[i]
                handleFileDownload(file, (url) => {
                    urls.push({
                        url: url,
                        name: file.name
                    })
                    if (urls.length !== selected.length) {
                        return
                    }
                    urls.forEach(aUrl => {
                        const xhr = new XMLHttpRequest();
                        xhr.open('GET', aUrl.url, true);
                        xhr.responseType = 'blob';
                        xhr.onload = () => {
                            const a = document.createElement('a');
                            a.href = window.URL.createObjectURL(xhr.response);
                            a.download = aUrl.name
                            document.body.appendChild(a);
                            a.click();
                        };
                        xhr.send();
                    });
                })
            }
            break
        case 'delete':
            dialog.error({
                title: '删除文件',
                content: '确定删除选中的文件吗？',
                positiveText: '删除',
                negativeText: '取消',
                onPositiveClick: () => {
                    for (let i = 0; i < selected.length; i++) {
                        const file = selected[i]
                        handleStorageDelete(file, i === selected.length - 1)
                    }
                }
            })

            break
        case 'cancel':
            break
    }
    return true
}

const checkHasNotFile = (files) => {
    for (let i = 0; i < files.length; i++) {
        if (files[i].storageType !== 'FILE') {
            return true
        }
    }
    return false
}

const hackFileOptions = (file) => {
    curTargetFile.value = file

    const queryParams = nowOpenType ? {
        refer: 'type', source: nowOpenType
    } : {}

    fileOptions.find(option => option.key === 'log').label = () => {
        return h(RouterLink, {
            to: {
                name: driveFileAttrsPage,
                params: {
                    ownerType: 'user',
                    ownerId: userStore.user.id,
                    id: file.storageId,
                    type: file.storageType.toLowerCase()
                },
                query: queryParams
            },
        }, {
            default: () => '属性与日志'
        });
    }
    fileOptions.find(option => option.key === 'permission').label = () => {
        return h(RouterLink, {
            to: {
                name: driveFilePermissionPage,
                params: {
                    ownerType: 'user',
                    ownerId: userStore.user.id,
                    id: file.storageId,
                    type: file.storageType.toLowerCase()
                },
                query: queryParams
            },
        }, {
            default: () => '权限'
        });
    }
}

const previewableTypes = ['IMAGE', 'TEXT', 'PDF', 'VIDEO', 'AUDIO', 'DOCUMENT']

const handleStorageClick = (e, target) => {
    if (target.storageType === 'FOLDER') {
        router.push({
            name: driveFilePageFolder,
            params: {
                folder: target.storageId
            }
        }).catch(() => {
        })
        return
    }
    if (previewableTypes.includes(target.fileType)) {
        curTargetFile.value = target
        showFilePreviewModal.value = true
        return
    }

    message.warning('暂不支持预览此类型文件')
}

const curTargetFile = ref({})

const handleStorageDelete = (storage, isRefresh = true) => {
    const config = createConfig()

    proxy.$axios.delete(
            api.storage('user', userStore.user.id,
                    storage.storageType.toLowerCase(), storage.storageId), config).then(() => {
        if (isRefresh) {
            refresh()
            message.success('删除成功')
        }
    }).catch((err) => {
        popUserErrorTemplate(notification, err, '删除文件失败')
    })
}

const handleFileDownload = (storage, callback = null) => {
    handleStorageDownload(storage, 'user',
            userStore.user.id, proxy.$axios,callback)
}

const handleFileOptionSelect = (key, options, target) => {
    const storage = target

    switch (key) {
        case 'download':
            if (storage.storageType !== 'FILE') {
                message.error("不支持文件夹类型的下载")
                return
            }
            handleFileDownload(storage)
            break;
        case 'share':
            showShareStorageModal.value = true
            break;
        case 'favorite':
            showFavoriteStorageModal.value = true
            break;
        case 'rename':
            showRenameStorageModal.value = true
            break;
        case 'copy':
            storageCopyOrMove.value = "copy"
            showMoveStorageModal.value = true
            break;
        case 'move':
            storageCopyOrMove.value = "move"
            showMoveStorageModal.value = true
            break;
        case 'delete':
            dialog.error({
                title: '删除' + getFileType(storage.storageType),
                content: '确定删除文件 ' + storage.name + ' 吗？',
                positiveText: '删除',
                negativeText: '取消',
                onPositiveClick: () => {
                    handleStorageDelete(storage)
                }
            })
            break;
        case 'log':
        case 'permission':
            break;
    }
}

const handleMenuSelect = (key) => {
    switch (key) {
        case 'folder':
            showCreateFolderModal.value = true
            break;
        case 'upload':
            showUploadFileModal.value = true
            break;
        case 'uploadFolder':
            break;
        case 'refresh':
            refresh()
            break;
        case 'sort':
            break;
        default:
            break;
    }
}

const requestFilesByFolder = (directoryId) => {
    const config = createConfig()
    loading.value = true
    proxy.$axios.get(
            api.folder('user', userStore.user.id, directoryId), config)
            .then(res => {
                files.value = res.data
                loading.value = false
            })
            .catch(err => {
                popUserErrorTemplate(notification, err,
                        '获取文件列表失败', '文件请求失败')
            })
}

const requestFilesByType = (type) => {
    const config = createConfig()
    loading.value = true
    proxy.$axios.get(
            api.fileType('user', userStore.user.id, type), config)
            .then(res => {
                files.value = res.data
                loading.value = false
            })
            .catch(err => {
                popUserErrorTemplate(notification, err,
                        '获取文件列表失败', '文件请求失败')
            })
}

const requestFolderInfo = () => {
    const config = createConfig()
    if (curDirectoryId === 0) {
        folderInfo.value = {
            storageId: 0,
            storageType: 'FOLDER',
            parents: []
        }
        return
    }
    proxy.$axios.get(
            api.getStorageInfo('user', userStore.user.id, 'folder', curDirectoryId), config)
            .then(res => {
                folderInfo.value = res.data
            })
            .catch(err => {
                popUserErrorTemplate(notification, err,
                        '获取文件夹信息失败', '文件夹信息请求失败')
            })
}


const refresh = () => {
    if (nowOpenType) {
        requestFilesByType(nowOpenType)
        return
    }
    requestFolderInfo()
    requestFilesByFolder(curDirectoryId)
}

refresh()

</script>

<style scoped>

</style>
