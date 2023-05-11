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
                <div v-if="getCheckedList().length">
                    已选择共 {{ getCheckedList().length }} 个文件
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
            <div>
                <n-form-item label="文件">
                    <n-upload
                            :default-upload="false"
                            :directory="false"
                            :directory-dnd="false"
                            :max="1"
                            :multiple="false"
                            name="file">
                        <n-upload-dragger>
                            <n-text class="text-xl">
                                点击或者拖动文件到此区域来上传
                            </n-text>
                        </n-upload-dragger>
                    </n-upload>
                </n-form-item>
                <div class="pb-3">
                    如果你不想按照原文件名上传，可以在这里修改文件名
                </div>
                <n-form-item label="文件名">
                    <n-input placeholder="修改文件名，不填写即为原文件名" type="text"/>
                </n-form-item>

                <n-button-group>
                    <n-button type="primary" @click="showUploadFileModal = false">
                        确认
                    </n-button>
                    <n-button secondary type="default" @click="showUploadFileModal = false">取消</n-button>
                </n-button-group>
            </div>
        </n-modal>

        <n-modal v-model:show="showCreateFolderModal"
                 :show-icon="false"
                 preset="dialog"
                 title="新建文件夹"
                 transform-origin="center">
            <div>
                <CreateFolderForm
                        :folder-id="curDirectoryId"
                        :on-after-create-folder="() => {
                            loading = false
                            refresh()
                        }"
                        :on-before-create-folder="() => loading = true"
                        :on-click-cancel="() => showCreateFolderModal = false"
                        :on-click-confirm="() => showCreateFolderModal = false"
                />
            </div>
        </n-modal>
    </div>
</template>

<script setup>
import {h, ref, getCurrentInstance} from "vue";
import {RouterLink, useRouter} from "vue-router";
import {NIcon, useNotification, useMessage} from "naive-ui";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FileIcon from "@/components/icon/FileIcon.vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";
import FileComponent from "@/components/file/FileComponent.vue";
import {driveFileAttrsPage, driveFilePage, driveFilePageFolder, driveFilePermissionPage} from "@/router";
import {createConfig} from "@/request/axios_config";
import {useUserStore} from "@/stores/user";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import CreateFolderForm from "@/components/file/CreateFolderForm.vue";
import FolderBreadcrumbs from "@/components/file/FolderBreadcrumbs.vue";
import FileSystemInstructions from "@/components/file/FileSystemInstructions.vue";

const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const userStore = useUserStore()
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

const curTargetFile = ref(null)


const handleFileOptionSelect = (key) => {
    showDropdown.value = false
    showFileDropdown.value = false

    const storage = curTargetFile.value

    switch (key) {
        case 'download':
            if (storage.storageType !== 'FILE') {
                message.error("不支持文件夹类型的下载")
            }
            break;
        case 'share':
            break;
        case 'collect':
            break;

        case 'rename':
            break;
        case 'move':
            break;
        case 'delete':
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
        api.getFiles('user', userStore.user.id, directoryId), config)
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
