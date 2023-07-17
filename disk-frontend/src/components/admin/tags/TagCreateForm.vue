<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

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
})

const formRef = ref(null)
const modalValue = ref({
    name: '',
    description: '',
})

const formRules = {
    name: [
        {required: true, message: '请输入名称', trigger: 'blur'},
        {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
    ],
    description: [
        {min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur'}

    ],
}

const handleConfirm = () => {
    formRef.value.validate().then(() => {
        props.onClickConfirm()
        requestCreateTag()
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

const requestCreateTag = () => {
    const config = createConfig(true)
    props.onBeforeAction()
    proxy.$axios.post(api.tags(null, true), modalValue.value, config).then((response) => {
        props.onAfterAction()
        message.success('创建标签成功')
    }).catch((error) => {
        props.onAfterAction()
        popAdminErrorTemplate(notification, error,
                '创建标签失败', '标签请求错误')
    })
}

</script>

<template>
    <div>
        <n-form ref="formRef" :model="modalValue" :rules="formRules">
            <n-form-item label="名称" path="name">
                <n-input v-model:value="modalValue.name"
                         clearable
                         placeholder="请输入名称" type="text"/>
            </n-form-item>
            <n-form-item label="描述" path="description">
                <n-input v-model:value="modalValue.description"
                         clearable
                         placeholder="请输入描述" type="text"/>
            </n-form-item>
            <n-button-group>
                <n-button type="primary" @click="handleConfirm">
                    确认
                </n-button>
                <n-button secondary type="default" @click="handleCancel">取消</n-button>
            </n-button-group>
        </n-form>

    </div>
</template>

<style scoped>

</style>