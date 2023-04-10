<template>
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
            <n-steps >
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
            <div>
                Selected file: {{ getCheckedList() }}
            </div>
            <div class="flex flex-fill flex-wrap transition-all duration-300">
                <div v-for="(file, i) in files"
                     class="flex h-58 flex-col items-center p-6 cursor-pointer
             rounded-2xl transition-all duration-300
             ease-in-out w-[220px]
             hover:bg-gray-100 hover:bg-opacity-50 m-2"
                     @contextmenu="handleContextmenu2($event, file)"
                     @dblclick="handleDblClick($event, file)"
                     @mouseenter="fileMenuShowState[i] = true"
                     @mouseleave="fileMenuShowState[i] = false"
                >
                    <div :class="['w-100 block flex justify-start transition-all duration-300 items-start align-baseline ',
          fileMenuShowState[i] || checkedState[i] ? 'opacity-100' : 'opacity-0']">
                        <n-checkbox v-model:checked="checkedState[i]"/>
                        <div class="pl-3 flex flex-fill justify-end">
                            <n-button circle
                                      @click="handleClickMoreOptions($event, file)">
                                <template #icon>
                                    <n-icon size="20">
                                        <MoreHorizonal20Regular/>
                                    </n-icon>
                                </template>
                            </n-button>
                        </div>
                    </div>

                    <div class="px-5 pb-3">
                        <n-icon v-if="file.type === 'folder' " size="80">
                            <Folder24Regular/>
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
                        {{ file.time }}
                    </div>
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
                @select="handleSelect"/>

        <n-dropdown
                :on-clickoutside="onClickOutside"
                :options="fileOptions"
                :show="showFileDropdown"
                :x="xRef"
                :y="yRef"
                placement="bottom-start"
                trigger="manual"
                @select="handleSelect"/>
    </div>


</template>

<script setup>
import {h, ref} from "vue";
import {NIcon} from "naive-ui";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FileIcon from "@/components/icon/FileIcon.vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";
import MoreHorizonal20Regular from "@/components/icon/MoreHorizonal20Regular.vue";

const files = [
    {
        name: "测试文件.txt",
        type: "file",
        time: "2023/03/22 14:12",
    },
    {
        name: "项目文档.pdf",
        type: "file",
        time: "2023/03/23 15:23",
    },
    {
        name: "测试文件夹",
        type: "folder",
        time: "2023/03/22 14:11",
    },
    {
        name: "长沙米拓信息技术有限公司河南天一航天科技有限公司民事一审民事判决书.doc",
        type: "file",
        time: "2023/04/02 12:46",
    },
    {
        name: "河南省优悠商贸有限公司河南福汇泽置业有限公司等民事二审民事判决书.doc",
        type: "file",
        time: "2023/04/02 12:47",
    }
]

const fileMenuShowState = ref([])
const checkedState = ref([])

const remappingStates = () => {
    fileMenuShowState.value = []
    checkedState.value = []
    for (let i = 0; i < files.length; i++) {
        fileMenuShowState.value.push(false)
        checkedState.value.push(false)
    }
}

remappingStates()

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

    console.log('click', target)
    if (!showFileDropdownState) {
        return
    }

    xRef.value = e.clientX
    yRef.value = e.clientY
}

const handleDblClick = (e, target) => {
    console.log('double', target)
}

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
        label: "文件日志",
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

const handleContextmenu = (e) => {
    e.preventDefault();
    xRef.value = e.clientX;
    yRef.value = e.clientY;
    if (showFileDropdown.value) {
        return
    }
    showDropdown.value = true;
}

const handleContextmenu2 = (e, target) => {
    e.preventDefault();
    console.log('context', target)

    showFileDropdownState = false

    xRef.value = e.clientX;
    yRef.value = e.clientY;
    showDropdown.value = false;
    showFileDropdown.value = true;
}

const handleSelect = () => {
    showDropdown.value = false;
    showFileDropdown.value = false;
}

const onClickOutside = () => {
    showDropdown.value = false;
    showFileDropdown.value = false;
}

</script>
