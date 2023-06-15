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
    keywordSearchScope: null
})

const formRules = {
    name: [
        {required: true, message: '请输入名称', trigger: 'blur'},
        {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
    ],
    description: [
        // not required, but once input,
        // its length should be greater than 1 and lesser than 50
        {min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur'}

    ],
    keywordSearchScope: [
        {required: true, message: '请选择关键词搜索范围', trigger: 'blur'},
    ]
}


const options = [
    {label: '全部', value: 'ALL'},
    {label: '文件名', value: 'NAME'},
    {label: '描述', value: 'DESCRIPTION'},
    {label: '内容', value: 'CONTENT'},
]

const handleConfirm = () => {
    formRef.value.validate().then(() => {
        props.onClickConfirm()
        requestCreateGroup()
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

const requestCreateGroup = () => {
    const config = createConfig(true)
    props.onBeforeAction()
    proxy.$axios.post(api.tagGroups(true), modalValue.value, config).then((response) => {
        props.onAfterAction()
    }).catch((error) => {
        props.onAfterAction()
        popAdminErrorTemplate(notification, error,
                '创建标签组失败', '标签组请求失败')
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
            <n-form-item label="关键字搜索范围" path="keywordSearchScope">
                <n-select v-model:value="modalValue.keywordSearchScope"
                          :options="options"
                          placeholder="请选择关键词搜索范围"
                />
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