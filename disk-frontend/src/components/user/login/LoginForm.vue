<template>
    <div class="flex flex-grow-1 flex-fill">
        <n-h2>
            <n-text type="primary">登录</n-text>
        </n-h2>
        <div class="flex flex-fill justify-end">
            <n-h3>
                <n-text type="info">尚未拥有账号？
                    <n-a @click="handleToRegister">点此注册</n-a>
                </n-text>
            </n-h3>
        </div>
    </div>
    <n-form ref="loginForm" :model="formValue" :rules="formRules">
        <n-form-item label="用户名/电子邮箱" path="identity">
            <n-input v-model:value="formValue.identity" placeholder="请输入用户名或电子邮箱"
                     @keydown.enter.prevent/>
        </n-form-item>
        <n-form-item label="密码" path="token">
            <n-input v-model:value="formValue.token" placeholder="请输入密码" show-password-on="click"
                     type="password"
                     @keydown.enter.prevent/>
        </n-form-item>
        <n-form-item path="rememberMe">
            <n-checkbox v-model:checked="formValue.rememberMe">
                记住我
            </n-checkbox>
        </n-form-item>
        <n-button-group class="w-full">
            <n-button class="w-full flex-grow-0" type="primary" @click="onLoginClick">
                登录
            </n-button>
            <n-button class="w-full" type="tertiary" @click="onResetClick">
                重置
            </n-button>
        </n-button-group>
        <n-a>忘记密码</n-a>
    </n-form>

</template>

<script setup>
import {getCurrentInstance, ref} from "vue";
import axios from "axios";
import api from "@/request/api";
import {useNotification} from "naive-ui";
import {useRouter} from "vue-router";
import {useUserStore} from "@/stores/user";
import {driveFilePage, index, register} from "@/router";
import {popUserErrorTemplate} from "@/views/util/error";

const userStore = useUserStore()
const loginForm = ref(null)
const router = useRouter()
const notification = useNotification()
const {proxy} = getCurrentInstance()

const formValue = ref({
    identity: null,
    token: null,
    rememberMe: false
})

const formRules = {
    identity: [{
        required: true,
        validator(rule, value) {
            if (!value) {
                return new Error("需要填写用户名或电子邮箱地址")
            }
            return true
        },
        trigger: ['input']
    }],
    token: [{
        required: true,
        message: "请输入密码",
        trigger: ['input']
    }],
}

const validateFormValue = (callback) => {
    loginForm.value?.validate((errors) => {
        if (errors) {
            return
        }
        callback()
    });
}

const onLoginClick = (e) => {
    e.preventDefault()
    validateFormValue(() => {
        if (formValue.value.identity === 'user' && formValue.value.token === '123456') {
            router.push({
                name: driveFilePage
            })
            userStore.loginUser({
                username: 'user',
                id: 1,
                role: 'ADMIN'
            }, 'none', false)
            return
        }
        popUserErrorTemplate(notification, {
            tip: "用户名或密码错误",
            message: "登录错误"
        }, "登录错误", "登录错误")

    })
}

const onResetClick = () => {
    formValue.value = {
        identity: null,
        token: null,
        rememberMe: false
    }
    loginForm.value?.restoreValidation()
}

const handleToRegister = () => {
    router.push({
        name: register
    })
}
</script>
