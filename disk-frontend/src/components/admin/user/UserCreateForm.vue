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
    username: '',
    password: '',
    role: null,
    email: '',
})

const roleOptions = [
    {
        label: "管理员",
        value: "ADMIN"
    },
    {
        label: "普通用户",
        value: "USER"
    }
]

const formRules = {
    username: [
        {required: true, message: "用户名不能为空"},
        {
            min: 3,
            max: 20,
            message: "用户名长度在 3-20 之间",
            trigger: ['input', 'blur']
        },
        {
            pattern: /^[a-zA-Z_\-][\w\-]{3,20}$/,
            message: "用户名只能包含字母、数字、下划线和横线，且不能以数字开头",
            trigger: ['input', 'blur']
        }
    ],
    password: [
        {required: true, message: "密码不能为空"},
        {
            min: 4, max: 20,
            message: '密码长度在 4 到 20 个字符',
            trigger: ['input', 'blur']
        },
        {
            pattern: /^[a-zA-Z0-9!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{4,20}$/,
            message: '密码只能包含字母、数字和特殊字符，长度在 4 到 20 个字符',
            trigger: ['input', 'blur']
        }
    ],
    email: [
        {required: true, message: "邮箱不能为空"},
        {
            type: "email",
            message: "邮箱格式不正确",
            trigger: ['input', 'blur']
        }
    ],
    role: [
        {required: true, message: "角色不能为空"},
    ]
}

const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onBeforeAction()
        props.onClickConfirm()
        requestCreateUser()
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

const requestCreateUser = () => {
    const config = createConfig(true)

    proxy.$axios.post(api.users(true), {
        username: formValue.value.username,
        password: formValue.value.password,
        email: formValue.value.email,
        role: formValue.value.role,
    }, config).then(() => {
        message.success("创建成功")
        props.onAfterAction()
    }).catch((e) => {
        popAdminErrorTemplate(notification, e)
    })
}

</script>

<template>
    <div>
        <n-form ref="form" :model="formValue" :rules="formRules">
            <n-form-item label="用户名" path="username">
                <n-input v-model:value="formValue.username"/>
            </n-form-item>
            <n-form-item label="密码" path="password">
                <n-input v-model:value="formValue.password"
                         clearable
                         show-password-on="click"
                         type="password"/>
            </n-form-item>
            <n-form-item label="邮箱" path="email">
                <n-input v-model:value="formValue.email"/>
            </n-form-item>
            <n-form-item label="角色" path="role">
                <n-select v-model:value="formValue.role"
                          :options="roleOptions"
                          placeholder="请选择角色"/>
            </n-form-item>

            <div>
                <n-button-group>
                    <n-button type="primary" @click="handleConfirm">确认</n-button>
                    <n-button secondary type="primary" @click="handleCancel">取消</n-button>
                </n-button-group>
            </div>
        </n-form>
    </div>
</template>

<style scoped>

</style>