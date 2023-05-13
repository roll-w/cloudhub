<template>
    <div>
        <div v-if="curDirectoryId === 0">
            <FileSystemInstructions/>
        </div>
        <div class="flex-fill" @contextmenu="handleContextmenu">
            <div class="p-5">
                <div class="text-xl py-2">
                    <FolderBreadcrumbs :folder-info="folderInfo"/>
                </div>
                <div>
                    <span v-if="!getCheckedList().length">
                        共 {{ files.length }} 项
                    </span>
                    <span v-else>
                        已选择 {{ getCheckedList().length }} 项
                    </span>
                </div>

                <div class="min-h-[400px]">
                    <n-spin :show="loading" size="large">
                        <div v-if="files.length" class="flex flex-fill flex-wrap transition-all duration-300">
                            <FileComponent v-for="(file, i) in files"
                                           v-model:checked="checkedState[i]"
                                           :file="file"
                                           :onClickMoreOptions="handleClickMoreOptions"
                                           @contextmenu="handleFileOptionContextMenu($event, file)"
                                           @dblclick="handleStorageClick($event, file)"/>
                        </div>
                        <div v-else class="w-100 h-100 flex flex-col flex-fill content-center justify-center">
                            <div class="self-center">
                                <n-empty description="暂无文件">
                                    <template #extra>
                                        <n-button-group>
                                            <n-space size="medium">
                                                <n-button secondary size="large" type="primary"
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
                    </n-spin>
                </div>
            </div>
        </div>
        <n-dropdown
                :on-clickoutside="onClickOutside"
                :options="menuOptions"
                :show="showDropdown"
                :x="xRef"
                :y="yRef"
                placement="bottom-start"
                trigger="manual"
                @select="handleMenuSelect"/>

        <n-dropdown
                :on-clickoutside="onClickOutside"
                :options="fileOptions"
                :show="showFileDropdown"
                :x="xRef"
                :y="yRef"
                placement="bottom-start"
                trigger="manual"
                @select="handleFileOptionSelect"/>


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

        <n-modal v-model:show="showRenameStorageModal"
                 :show-icon="false"
                 :title="'重命名 ' + curTargetFile.name"
                 preset="dialog"
                 transform-origin="center">
            <StorageRenameForm
                    :on-after-rename="() => {
                    loading = false
                }"
                    :on-before-rename="() => loading = true"
                    :on-click-cancel="() => showRenameStorageModal = false"
                    :on-click-confirm="() => showRenameStorageModal = false"
                    :owner-id="userStore.user.id"
                    :storage-id="curTargetFile.storageId"
                    :storage-type="curTargetFile.storageType"
                    owner-type="user"
            />

        </n-modal>
    </div>
</template>

<script setup>
import {h, ref, getCurrentInstance} from "vue";
import {RouterLink, useRouter} from "vue-router";
import {NIcon, useNotification, useMessage, useDialog} from "naive-ui";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FileIcon from "@/components/icon/FileIcon.vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";
import FileComponent from "@/components/file/FileComponent.vue";
import {driveFileAttrsPage, driveFilePageFolder, driveFilePermissionPage} from "@/router";
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

const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()
const userStore = useUserStore()
const fileStore = useFileStore()
const router = useRouter()

const files = ref([])
const fileMenuShowState = ref([])
const checkedState = ref([])
const folderInfo = ref({
    parents: []
})

const remappingStates = () => {
    fileMenuShowState.value = []
    checkedState.value = []
    for (const _ of files.value) {
        fileMenuShowState.value.push(false)
        checkedState.value.push(false)
    }
}

const getCheckedList = () => {
    let checkedList = []
    for (let i = 0; i < checkedState.value.length; i++) {
        if (checkedState.value[i]) {
            checkedList.push(i)
        }
    }
    return checkedList
}

const xRef = ref(0)
const yRef = ref(0)

const loading = ref(false)

const showDropdown = ref(false)
const showFileDropdown = ref(false)
const showUploadFileModal = ref(false)
const showCreateFolderModal = ref(false)
const showRenameStorageModal = ref(false)

let showFileDropdownState = false
let lastTarget = null

const curDirectoryId = router.currentRoute.value.params.folder || 0

const menuOptions = [
    {
        label: "新建文件夹",
        key: "folder",
        icon() {
            return h(NIcon, null, {
                default: () => h(Folder24Regular)
            })
        },
    },
    {
        label: "上传文件",
        key: "upload",
        icon() {
            return h(NIcon, null, {
                default: () => h(FileIcon)
            })
        },
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "刷新",
        key: "refresh",
        icon() {
            return h(NIcon, null, {
                default: () => h(RefreshRound)
            })
        },
    },
    {
        label: "排序",
        key: "sort",
        icon() {
            return "S"
        },
    },
];

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
        key: "collect",
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

const hackFileOptions = (file) => {
    curTargetFile.value = file
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
            },
        }, {
            default: () => '权限'
        });
    }
}

const handleStorageClick = (e, target) => {
    if (target.storageType === 'FOLDER') {
        router.push({
            name: driveFilePageFolder,
            params: {
                folder: target.storageId
            }

        })
        return
    }
    message.warning('暂不支持预览此类型文件')

}

const curTargetFile = ref({})

const handleStorageDelete = (storage) => {
    const config = createConfig()

    proxy.$axios.delete(
        api.storage('user', userStore.user.id,
            storage.storageType.toLowerCase(), storage.storageId), config).then(() => {
        message.success('删除成功')
        refresh()
    }).catch((err) => {
        popUserErrorTemplate(notification, err, '删除文件失败')
    })
}

const handleFileDownload = (storage) => {
    const token = userStore.getToken
    window.open(api.storage('user', userStore.user.id,
        storage.storageType.toLowerCase(), storage.storageId) + '?token=' + token)
    // const config = createConfig()
    // config.responseType = 'blob'
    // proxy.$axios.get(
    //     api.storage('user', userStore.user.id,
    //         storage.storageType.toLowerCase(), storage.storageId), config).then((res) => {
    //     const url = window.URL.createObjectURL(new Blob([res.data]))
    //     const link = document.createElement('a')
    //     link.style.display = 'none'
    //     link.href = url
    //     link.setAttribute('download', storage.name)
    //     document.body.appendChild(link)
    //     link.click()
    //     document.body.removeChild(link)
    // }).catch((err) => {
    //     popUserErrorTemplate(notification, err, '下载文件失败')
    // })
}

const handleFileOptionSelect = (key) => {
    showDropdown.value = false
    showFileDropdown.value = false

    const storage = curTargetFile.value

    switch (key) {
        case 'download':
            if (storage.storageType !== 'FILE') {
                message.error("不支持文件夹类型的下载")
                return
            }
            handleFileDownload(storage)
            break;
        case 'share':
            break;
        case 'collect':
            break;
        case 'rename':
            showRenameStorageModal.value = true
            break;
        case 'move':
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

const handleContextmenu = (e) => {
    e.preventDefault();
    xRef.value = e.clientX;
    yRef.value = e.clientY;
    if (showFileDropdown.value) {
        return
    }
    showDropdown.value = true;
}

const handleFileOptionContextMenu = (e, target) => {
    e.preventDefault();
    showFileDropdownState = false

    xRef.value = e.clientX;
    yRef.value = e.clientY;

    hackFileOptions(target)

    showDropdown.value = false;
    showFileDropdown.value = true;
}

const handleMenuSelect = (key) => {
    showDropdown.value = false;
    showFileDropdown.value = false;

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

const onClickOutside = () => {
    showDropdown.value = false;
    showFileDropdown.value = false;
}

const handleClickMoreOptions = (e, target) => {
    e.preventDefault();
    showDropdown.value = false
    if (lastTarget === target) {
        showFileDropdownState = !showFileDropdownState
    } else {
        showFileDropdownState = true
    }
    lastTarget = target
    showFileDropdown.value = showFileDropdownState

    if (!showFileDropdownState) {
        return
    }
    hackFileOptions(target)

    xRef.value = e.clientX
    yRef.value = e.clientY
}

const requestFilesBy = (directoryId) => {
    const config = createConfig()
    loading.value = true
    proxy.$axios.get(
        api.folder('user', userStore.user.id, directoryId), config)
        .then(res => {
            files.value = res.data
            remappingStates()
            loading.value = false
        })
        .catch(err => {
            popUserErrorTemplate(notification, err,
                '获取文件列表失败', '文件请求失败')
        })
}

const requestFolderInfo = () => {
    const config = createConfig()
    proxy.$axios.get(
        api.getStorageInfo('user', userStore.user.id, 'folder', curDirectoryId), config)
        .then(res => {
            folderInfo.value = res.data
            console.log(res)
        })
        .catch(err => {
            popUserErrorTemplate(notification, err,
                '获取文件夹信息失败', '文件夹信息请求失败')
        })
}


const refresh = () => {
    requestFolderInfo()
    requestFilesBy(curDirectoryId)
}

refresh()

</script>

<style scoped>

</style>
