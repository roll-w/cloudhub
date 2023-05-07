<template>
    <div>
        <div class="p-5">
            <div class="pb-5">
                <n-alert closable type="info">
                    <div class="text-xl">
                        欢迎使用 Cloudhub 法律文件资料库，您可以在这里上传、下载、分享、管理您的资料文件。
                    </div>
                </n-alert>
            </div>
            <n-card closable>
                <div class="text-4xl pb-5">
                    使用指引
                </div>
                <n-steps>
                    <n-step
                            description="进入文件资料库，点击右上角的“上传文件”按钮，选择您要上传的文件。"
                            status="process"
                            title="上传文件"/>
                    <n-step
                            description="在文件列表中找到您需要下载的文件，点击“下载”按钮，即可下载文件。"
                            status="process"
                            title="下载文件"/>
                    <n-step
                            description="在文件列表中找到您需要分享的文件，点击“分享”按钮，即可分享文件。"
                            status="process"
                            title="分享文件"/>
                    <n-step
                            description="如果您是文件的所有者，或者您为部门管理员，您可以在文件列表中找到您需要设置的文件，点击“设置权限”按钮，即可管理文件。"
                            status="process"
                            title="设置文件权限"/>
                </n-steps>
            </n-card>
        </div>

        <div class="flex-fill" @contextmenu="handleContextmenu">
            <div class="p-5">
                <div class="text-2xl py-2">
                    文件
                </div>
                <div v-if="getCheckedList().length">
                    已选择共 {{ getCheckedList().length }} 个文件
                </div>
                <div class="flex flex-fill flex-wrap transition-all duration-300">
                    <FileComponent v-for="(file, i) in files"
                                   v-model:checked="checkedState[i]"
                                   :file="file"
                                   :onClickMoreOptions="handleClickMoreOptions"
                                   @contextmenu="handleFileOptionContextMenu($event, file)"
                                   @dblclick="handleDblClick($event, file)"/>
                </div>
            </div>
        </div>
        <n-dropdown
                :on-clickoutside="onClickOutside"
                :options="options"
                :show="showDropdown"
                :x="xRef"
                :y="yRef"
                placement="bottom-start"
                trigger="manual"
                @select="handleSelectItem"/>

        <n-dropdown
                :on-clickoutside="onClickOutside"
                :options="fileOptions"
                :show="showFileDropdown"
                :x="xRef"
                :y="yRef"
                placement="bottom-start"
                trigger="manual"
                @select="handleSelect"/>

        <n-modal :bordered="false"
                 preset="dialog"
                 size="large"
                 title="文件权限">

        </n-modal>
    </div>
</template>

<script setup>
import {h, ref, getCurrentInstance} from "vue";
import {RouterLink, useRouter} from "vue-router";
import {NIcon} from "naive-ui";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FileIcon from "@/components/icon/FileIcon.vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";
import FileComponent from "@/components/file/FileComponent.vue";
import {driveFileAttrsPage, driveFilePermissionPage} from "@/router";
import {createConfig} from "@/request/axios_config";
import {useUserStore} from "@/stores/user";
import api from "@/request/api";

const {proxy} = getCurrentInstance()
const userStore = useUserStore()
const router = useRouter()

const files = ref([])

const fileMenuShowState = ref([])
const checkedState = ref([])

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
const showDropdown = ref(false)
const showFileDropdown = ref(false)
let showFileDropdownState = false
let lastTarget = null

const options = [
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
        key: 'header-divider',
        type: 'divider'
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
        label: "上传文件夹",
        key: "uploadFolder",
        icon() {
            return "U"
        },
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "粘贴",
        key: "paste",
        icon() {
            return "P"
        },
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

const handleDblClick = (e, target) => {
}

const handleSelect = (key) => {
    showDropdown.value = false
    showFileDropdown.value = false
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

const handleSelectItem = (key) => {
    showDropdown.value = false;
    showFileDropdown.value = false;

    console.log(key)
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
    proxy.$axios.get(
        api.getFiles('user', userStore.user.id, directoryId), config)
        .then(res => {
            files.value = res.data
            console.log(res)
            remappingStates()
        })
        .catch(err => {
            console.log(err)
        })
}

requestFilesBy(0)

</script>
