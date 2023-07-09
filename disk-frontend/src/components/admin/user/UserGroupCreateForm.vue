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

const form = ref()
const formValue = ref({
    name: '',
    description: '',
})

const formRules = {
    name: [
        {required: true, message: "用户组名不能为空"},
        {
            min: 1,
            max: 20,
            message: "用户组名长度在1-20个字符之间",
            trigger: ["blur", "input"]
        },
    ],
    description: [
        {max: 100, message: "描述长度不能超过100个字符"},
    ],
}

const resetForm = () => {
    formValue.value.name = ''
    formValue.value.description = ''
}

const handleCancel = () => {
    resetForm()
    props.onClickCancel()
}

const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onClickConfirm()
        props.onBeforeAction()
        requestCreateUserGroup()
    })
}

const requestCreateUserGroup = () => {
    const config = createConfig()

    proxy.$axios.post(api.userGroups(true), {
        name: formValue.value.name,
        description: formValue.value.description,
    }, config)
            .then((response) => {
                message.success("创建成功")
                props.onAfterAction()
            })
            .catch((error) => {
                props.onAfterAction()
                popAdminErrorTemplate(notification, error)
            })
}

</script>

<template>
    <div>
        <n-form ref="form" v-model:model="formValue"
                :rules="formRules">
            <n-form-item label="用户组名" path="name">
                <n-input v-model:value="formValue.name"
                         placeholder="请输入用户组名"/>
            </n-form-item>
            <n-form-item label="描述" path="description">
                <n-input v-model:value="formValue.description"
                         placeholder="请输入描述"/>
            </n-form-item>
        </n-form>
        <div>
            <n-button-group>
                <n-button type="primary" @click="handleConfirm">确认</n-button>
                <n-button secondary type="primary" @click="handleCancel">取消</n-button>
            </n-button-group>
        </div>
    </div>
</template>

<style scoped>

</style>