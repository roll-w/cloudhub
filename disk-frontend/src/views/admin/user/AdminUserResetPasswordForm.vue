<template>
    <div>
        <n-form ref="form" :model="formValue" :rules="formRules">
            <n-form-item label="新密码" path="newPassword">
                <n-input v-model:value="formValue.newPassword"
                         placeholder="请输入新密码" show-password-on="click"
                         type="password"/>
            </n-form-item>
            <n-form-item label="确认新密码" path="confirmNewPassword">
                <n-input v-model:value="formValue.confirmNewPassword"
                         placeholder="请再次输入新密码" show-password-on="click"
                         type="password"/>
            </n-form-item>
            <div class="pt-3">
                <n-button-group>
                    <n-button type="primary" @click="handleConfirm">
                        确认
                    </n-button>
                    <n-button secondary type="default" @click="handleCancel">
                        取消
                    </n-button>
                </n-button-group>
            </div>
        </n-form>
    </div>
</template>

<script setup>

import {getCurrentInstance, ref} from "vue";
import {useNotification, useMessage} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {login} from "@/router";
import {useRouter} from "vue-router";

const notification = useNotification()
const message = useMessage()
const {proxy} = getCurrentInstance()
const userStore = useUserStore()
const router = useRouter()

const props = defineProps({
    onCancel: {
        type: Function,
        default: () => {
        }
    },
    onConfirm: {
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
    userId: {
        type: [Number, String],
        required: true
    }
})

const form = ref()
const formValue = ref({
    newPassword: '',
    confirmNewPassword: ''
})

const formRules = ref({
    newPassword: [
        {required: true, message: '请输入新密码', trigger: 'blur'},
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
    confirmNewPassword: [
        {required: true, message: '请再次输入新密码', trigger: 'blur'},
        {
            validator: (rule, value, callback) => {
                if (value !== formValue.value.newPassword) {
                    callback(new Error('两次输入密码不一致'))
                } else {
                    callback()
                }
            },
            trigger: ['input', 'blur']
        }
    ]
})

const handleConfirm = () => {
    props.onBeforeAction()
    form.value.validate().then(() => {
        props.onConfirm(formValue.value)
        sendResetPasswordRequest()
    })
}

const handleCancel = () => {
    props.onCancel()
}

const sendResetPasswordRequest = () => {
    const config = createConfig(true)

    proxy.$axios.put(api.userPassword(props.userId), {
        value: formValue.value.newPassword
    }, config).then(response => {
        message.success("修改密码成功")
        props.onAfterAction()
    }).catch(error => {
        props.onAfterAction()
        popUserErrorTemplate(notification, error,
                "修改密码失败", "用户请求错误")
    })


}

</script>

<style scoped>

</style>