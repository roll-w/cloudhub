<script setup>
import {computed, getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useDialog, useMessage, useNotification} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import FolderBreadcrumbs from "@/components/file/FolderBreadcrumbs.vue";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const props = defineProps({
    storageId: {
        type: Number,
        required: true
    },
    storageType: {
        type: String,
        required: true
    },
    onClickCancel: {
        type: Function,
        default: () => {
        }
    },
    onClickConfirm: {
        type: Function,
        default: () => {
        }
    },
    onBeforeAction: {
        type: Function,
        default: () => {
        }
    },
    onAfterAction: {
        type: Function,
        default: () => {
        }
    },
    ownerType: {
        type: String,
        default: 'user'
    },
    ownerId: {
        type: Number,
        required: true
    },
    fileName: {
        type: String,
        default: ''
    },
    folderId: {
        type: Number,
        default: 0
    },
    type: {
        type: String,
        default: 'move'
    }
})

const folders = ref([])
const folderInfo = ref({
    parents: []
})

const typeText = computed(() => {
    if (props.type === 'move') {
        return '移动'
    } else {
        return '复制'
    }
})

const action = computed(() => {
    if (props.type === 'move') {
        return 'parent'
    } else {
        return 'copy'
    }
})

const requestFolders = (id) => {
    const config = createConfig()

    proxy.$axios.get(api.foldersIn(props.ownerType, props.ownerId, id), config)
            .then((response) => {
                folders.value = response.data
            })
            .catch((error) => {
                popUserErrorTemplate(notification, error, "获取文件夹列表失败")
            })
}

const requestFolderStructure = (id) => {
    const config = createConfig()

    proxy.$axios.get(api.getStorageInfo(props.ownerType, props.ownerId,
            'folder', id), config)
            .then((response) => {
                folderInfo.value = response.data
            })
            .catch((error) => {
                popUserErrorTemplate(notification, error, "获取文件夹失败")
            })
}

const requestMove = () => {
    const config = createConfig(true)
    proxy.$axios.put(api.storageAction(props.ownerType, props.ownerId,
            props.storageType, props.storageId, 'parent'), {
        value: folderInfo.value.storageId
    }, config)
            .then((response) => {
                message.success("移动成功")
                props.onAfterAction()
            })
            .catch((error) => {
                props.onAfterAction()
                popUserErrorTemplate(notification, error, "移动失败")
            })
}

const requestCopy = () => {
    const config = createConfig(true)
    proxy.$axios.post(api.storageAction(props.ownerType, props.ownerId,
            props.storageType, props.storageId, 'copy'), {
        value: folderInfo.value.storageId
    }, config)
            .then((response) => {
                message.success("复制成功")
                props.onAfterAction()
            })
            .catch((error) => {
                props.onAfterAction()
                popUserErrorTemplate(notification, error, "复制失败")
            })
}

const handleConfirm = () => {
    props.onClickConfirm()
    props.onBeforeAction()
    switch (props.type) {
        case 'move':
            requestMove()
            break
        case 'copy':
            requestCopy()
            break
    }
}

const handleCancel = () => {
    props.onClickCancel()
}

const handleFolderClick = (event, folder) => {
    requestFolders(folder.storageId)
    requestFolderStructure(folder.storageId)
}

requestFolderStructure(props.folderId)
requestFolders(props.folderId)


</script>

<template>
    <div>
        <FolderBreadcrumbs :folder-info="folderInfo"
                           :on-folder-click="handleFolderClick"
                           size="small">
            <template #root>
                <div @click="handleFolderClick($event, {storageId: 0})">
                    文件
                </div>
            </template>
        </FolderBreadcrumbs>
        <div class="py-3 h-[50vh]">
            <n-scrollbar>
                <TransitionGroup name="slide-fade">
                    <div v-for="folder in folders" v-if="folders.length" :key="folder.storageId">
                        <div>
                            <div class="rounded-lg transition-all
                     hover:bg-neutral-300 hover:bg-opacity-30 cursor-pointer"
                                 @click="handleFolderClick($event, folder)">
                                <div class="flex items-end py-3 pl-3">
                                    <n-icon class="mr-1 mb-1" size="26">
                                        <Folder24Regular/>
                                    </n-icon>
                                    <div class="text-lg">{{ folder.name }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div v-else key="empty">
                        <n-empty description="此文件夹下无文件夹"/>
                    </div>
                </TransitionGroup>
            </n-scrollbar>
        </div>
        <div class="pt-3">
            <n-button-group>
                <n-button secondary type="primary" @click="handleConfirm">
                    {{ typeText }}到此处
                </n-button>
                <n-button secondary type="default" @click="handleCancel">
                    取消
                </n-button>
            </n-button-group>
        </div>
    </div>
</template>

<style scoped>

</style>