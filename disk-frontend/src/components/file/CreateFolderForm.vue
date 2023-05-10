<template>
    <div>
        <n-form
                ref="createFolderForm"
                :model="createFolderFormValue"
                :rules="createFolderFormRules"
        >
            <n-form-item label="文件夹名称" path="name">
                <n-input v-model:value="createFolderFormValue.name" placeholder="输入文件夹名称" type="text"/>
            </n-form-item>
            <n-button-group>
                <n-button type="primary" @click="handleCreateFolderConfirm">
                    确认
                </n-button>
                <n-button secondary type="default" @click="onClickCancel()">取消</n-button>
            </n-button-group>
        </n-form>
    </div>
</template>

<script setup>
import {getCurrentInstance, ref} from "vue";
import api from "@/request/api";
import {useMessage, useNotification} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {useUserStore} from "@/stores/user";

const props = defineProps({
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
    onBeforeCreateFolder: {
        type: Function,
        default: () => {
        }
    },
    onAfterCreateFolder: {
        type: Function,
        default: () => {
        }
    },
    folderId: {
        type: Number,
        default: 0
    }
})

const {proxy} = getCurrentInstance()
const userStore = useUserStore()
const notification = useNotification()
const message = useMessage()

const createFolderForm = ref()
const createFolderFormValue = ref({
    name: ''
})

const createFolderFormRules = ref({
    name: [
        {
            required: true,
            message: '请输入文件夹名称',
            trigger: 'blur'
        },
        {
            min: 1,
            max: 80,
            message: '长度在 1 到 50 个字符间',
            trigger: 'blur'
        }
    ]
})

const createFolder = (name) => {
    const config = createConfig()
    proxy.$axios.post(
        api.createFolder('user', userStore.user.id, props.folderId), {
            name: name
        },
        config
    ).then(res => {
        message.success('创建文件夹成功')
        props.onAfterCreateFolder()
    }).catch(err => {
        popUserErrorTemplate(notification, err,
            '创建文件夹失败', '文件夹请求失败')
    })
}


const handleCreateFolderConfirm = () => {
    createFolderForm.value.validate().then(() => {
        props.onBeforeCreateFolder()
        props.onClickConfirm()
        createFolder(createFolderFormValue.value.name)
    })
}

</script>

<style scoped>

</style>