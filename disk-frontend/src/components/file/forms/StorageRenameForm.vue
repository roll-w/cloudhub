<template>
    <div>
        <div class="pb-5">
            <n-alert type="info">
                <div>
                    名称首尾不包含空格，且长度在 1 到 80 个字符间。
                </div>
            </n-alert>
        </div>

        <n-form ref="form"
                :model="formValue"
                :rules="formRules">
            <n-form-item label="新文件名" path="name">
                <n-input v-model:value="formValue.name"
                         placeholder="请输入新的文件名"
                         type="text"/>
            </n-form-item>
            <div class="pt-3">
                <n-button-group>
                    <n-button type="primary" @click="handleConfirm">
                        确认
                    </n-button>
                    <n-button secondary type="default" @click="handleCancel()">
                        取消
                    </n-button>
                </n-button-group>
            </div>
        </n-form>
    </div>
</template>

<script setup>
import {getCurrentInstance, ref} from "vue";
import {useMessage, useNotification} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";

const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()


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
    }
})

const form = ref()
const formValue = ref({
    name: props.fileName
})

const formRules = ref({
    name: [
        {
            required: true,
            message: '请输入新的文件名',
            trigger: 'blur'
        },
        {
            min: 1,
            max: 80,
            message: '文件名长度在 1 到 80 个字符间',
            trigger: 'blur'
        }
    ]
})

const handleCancel = () => {
    props.onClickCancel()
}

const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onClickConfirm(formValue.value)
        requestRename()
    })
}

const requestRename = () => {
    props.onBeforeAction()
    const config = createConfig(true)
    proxy.$axios.put(api.storageName(props.ownerType, props.ownerId,
        props.storageType.toLowerCase(), props.storageId), {
        value: formValue.value.name
    }, config).then(response => {
        message.success('重命名成功')
        props.onAfterAction()
    }).catch(error => {
        props.onAfterAction()
        popUserErrorTemplate(notification, error, '重命名失败')
    })

}

</script>

<style scoped>

</style>