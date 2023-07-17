<template>
    <div class="flex-fill relative" @contextmenu="handleContextmenu">
        <div>
            <div v-if="$slots.title" class="text-xl py-2">
                <slot name="title"></slot>
            </div>
            <div v-if="$slots.folder" class="text-xl py-2">
                <slot name="folder"></slot>
            </div>

            <div class="flex">
                <div v-if="allowSelect" class="mr-3">
                    <n-checkbox

                            v-model:checked="selectAllCheckBox"
                            @update:checked="onSelectAllCheckBox">
                    </n-checkbox>
                </div>
                <div v-if="!getCheckedList().length">
                    共 {{ files.length }} 项
                </div>
                <div v-else>
                    已选择 {{ getCheckedList().length }} 项
                </div>
                <div class="pl-5">
                    <slot name="checkbox-tip"></slot>
                </div>
            </div>
        </div>
        <slot name="before"></slot>
        <div class="min-h-[50vh]">
            <div v-if="files.length" class="flex flex-fill flex-wrap transition-all duration-300">
                <FileComponent v-for="(file, i) in files"
                               v-model:checked="checkedState[i]"
                               :file="file"
                               :on-click-more-options="handleClickMoreOptions"
                               :show-checkbox="allowSelect"
                               :show-more-options="(fileOptions.length > 0)"
                               @click="handleStorageClick($event, file)"
                               @contextmenu="handleFileOptionContextMenu($event, file)"/>
            </div>
            <div v-else>
                <slot name="empty"></slot>
            </div>
            <div v-if="files.length" class="pb-32">
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
        <Teleport to="body" v-if="toolbarOptions.length">
            <FileViewToolbar :checked-list="getCheckedList()"
                             :on-option-select="handleToolbarSelect"
                             :options="toolbarOptions"/>
        </Teleport>

        <n-modal v-model:show="showFilePreviewModal"
                 :show-icon="false"
                 :title="curTargetFile.name + ' 预览'"
                 class="w-full h-full"
                 preset="card"
                 style="width: 100vw; height: 100vh">
            <div class="p-5">
                <div>
                    <n-alert type="warning">
                        当前文件预览功能仅支持图片、文本、音频、视频文件，其他文件请下载后打开。
                    </n-alert>
                </div>
                <div class="pt-3">
                    <FilePreviewer
                            :file="curTargetFile"
                    />
                </div>
            </div>
        </n-modal>
    </div>

</template>

<script setup>
import FileComponent from "@/components/file/FileComponent.vue";
import {h, ref, watch} from "vue";
import {driveFilePageFolder} from "@/router";
import FileViewToolbar from "@/components/file/FileViewToolbar.vue";
import FilePreviewer from "@/components/file/FilePreviewer.vue";
import {useRouter} from "vue-router";
import RefreshRound from "@/components/icon/RefreshRound.vue";

const router = useRouter()

const props = defineProps({
    files: {
        type: Array,
        required: true
    },
    loading: {
        type: Boolean,
        default: false
    },
    fileOptions: {
        type: Array,
        default: () => []
    },
    menuOptions: {
        type: Array,
        default: () => []
    },
    allowSelect: {
        type: Boolean,
        default: true
    },
    onMenuSelect: {
        type: Function,
        default: (key, options) => {
        }
    },
    onFileOptionSelect: {
        type: Function,
        default: (key, options, target) => {
        }
    },
    onShowFileOption: {
        type: Function,
        default: (target) => {
        }
    },
    // set to override default behavior
    onStorageClick: {
        type: Function,
        default: null
    },
    onFolderClick: {
        type: Function,
        default: null
    },
    onStorageClickCallback: {
        type: Function,
        default: (e, target) => {
        }
    },
    // if onStorageClick is set, this will be ignored
    disableClick: {
        type: Boolean,
        default: false
    },
    disablePreview: {
        type: Boolean,
        default: true
    },
    toolbarOptions: {
        type: Array,
        default: () => []
    },
    onToolbarSelect: {
        type: Function,
        default: (key) => {
        }
    }
})



const xRef = ref(0)
const yRef = ref(0)
const showFileDropdown = ref(false)
const showDropdown = ref(false)
const showFilePreviewModal = ref(false)

const checkedState = ref([])
const selectAllCheckBox = ref(false)

let showFileDropdownState = false
let lastTarget = null
const curTargetFile = ref({})

const initialStates = () => {
    selectAllCheckBox.value = false
    checkedState.value = new Array(props.files.length)
            .fill(false)

}

initialStates()

const getCheckedList = () => {
    let checkedList = []
    for (let i = 0; i < checkedState.value.length; i++) {
        if (checkedState.value[i]) {
            checkedList.push(props.files[i])
        }
    }
    return checkedList
}

const onClickOutside = () => {
    showDropdown.value = false;
    showFileDropdown.value = false;
}

const handleContextmenu = (e) => {
    if (!props.menuOptions.length) {
        return;
    }
    e.preventDefault();
    xRef.value = e.clientX;
    yRef.value = e.clientY;

    if (showFileDropdown.value) {
        return
    }
    showDropdown.value = true;
}

const handleToolbarSelect = (key) => {
    if (!props.onToolbarSelect) {
        return
    }
    const selected = getCheckedList()

    const isCanceled = props.onToolbarSelect(key, selected)
    if (isCanceled) {
        checkedState.value = new Array(props.files.length)
                .fill(false)
    }
}

const handleClickMoreOptions = (e, target) => {
    e.preventDefault();

    if (!props.fileOptions.length) {
        return;
    }
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
    curTargetFile.value = target
    props.onShowFileOption(target)

    xRef.value = e.clientX
    yRef.value = e.clientY
}

const handleFileOptionContextMenu = (e, target) => {
    if (!props.fileOptions.length) {
        return;
    }

    e.preventDefault();
    curTargetFile.value = target
    showFileDropdownState = false

    xRef.value = e.clientX;
    yRef.value = e.clientY;

    props.onShowFileOption(target)

    showDropdown.value = false;
    showFileDropdown.value = true;
}

const handleFileOptionSelect = (key, option) => {
    showFileDropdown.value = false
    showDropdown.value = false

    props.onFileOptionSelect(key, option, curTargetFile.value)
}

const handleMenuSelect = (key, option) => {
    showDropdown.value = false
    showFileDropdown.value = false
    props.onMenuSelect(key, option)
}

const previewableTypes = ['IMAGE', 'TEXT', 'PDF', 'VIDEO', 'AUDIO', 'DOCUMENT']

const handleStorageClick = (e, target) => {
    curTargetFile.value = target
    if (props.onStorageClick) {
        props.onStorageClick(e, target)
        return;
    }
    if (props.onFolderClick && target.storageType === 'FOLDER') {
        props.onFolderClick(e, target)
        return;
    }

    if (props.disableClick) {
        handleClickMoreOptions(e, target)
        return
    }

    e.preventDefault();
    props.onStorageClickCallback(e, target)
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
    if (props.disablePreview) {
        return
    }
    if (previewableTypes.includes(target.fileType)) {
        curTargetFile.value = target
        showFilePreviewModal.value = true
        return
    }

    message.warning('暂不支持预览此类型文件')
}

watch(checkedState, (newVal) => {
    const checkedList = getCheckedList()
    if (checkedList.length === 0) {
        selectAllCheckBox.value = false
        return
    }

    if (checkedList.length === props.files.length) {
        selectAllCheckBox.value = true
        return
    }
    selectAllCheckBox.value = false
}, {deep: true})


const onSelectAllCheckBox = () => {
    const checked = selectAllCheckBox.value
    checkedState.value = new Array(props.files.length)
            .fill(checked)
}

</script>

<style scoped>

</style>