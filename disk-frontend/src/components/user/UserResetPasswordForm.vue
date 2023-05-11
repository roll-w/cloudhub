<template>
    <div>
        <div class="pt-2 pb-5">
            <n-alert type="warning">
                <div>
                    注意：修改密码后，您的账户将会退出登录，需要重新登录。
                </div>
            </n-alert>
        </div>

        <n-form ref="form" :model="formValue" :rules="formRules">
            <n-form-item label="旧密码" path="oldPassword">
                <n-input v-model:value="formValue.oldPassword"
                         placeholder="请输入旧密码" show-password-on="click"
                         type="password"/>
            </n-form-item>
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

            <div class="pt-3">
                忘记密码？
                <n-a>点此重置</n-a>
            </div>
        </n-form>
    </div>
</template>

<script setup>

import {getCurrentInstance, ref} from "vue";
import {useNotification} from "naive-ui";

const notification = useNotification()
const {proxy} = getCurrentInstance()

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
    onBeforeConfirm: {
        type: Function,
        default: () => {
        }
    },
    onAfterConfirm: {
        type: Function,
        default: () => {
        }
    }
})

const form = ref()
const formValue = ref({
    oldPassword: '',
    newPassword: '',
    confirmNewPassword: ''
})

const formRules = ref({
    oldPassword: [
        {required: true, message: '请输入旧密码', trigger: 'blur'}
    ],
    newPassword: [
        {required: true, message: '请输入新密码', trigger: 'blur'},
        {
            min: 4, max: 20,
            message: '密码长度在 4 到 20 个字符',
            trigger: ['input', 'blur']
        },
        {
            validator: (rule, value, callback) => {
                if (value === formValue.value.oldPassword && value) {
                    callback(new Error('新密码不能与旧密码相同'))
                } else {
                    callback()
                }
            },
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
    props.onBeforeConfirm()
    form.value.validate().then(() => {
        props.onConfirm(formValue.value)
        sendResetPasswordRequest()

    })
}

const handleCancel = () => {
    props.onCancel()
}

const sendResetPasswordRequest = () => {
    props.onAfterConfirm()
}

</script>

<style scoped>

</style>