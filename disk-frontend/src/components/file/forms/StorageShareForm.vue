<template>
    <div>
        <n-form ref="form" :model="formValue" :rules="formRules">
            <n-form-item label="选择分享有效期" path="time">
                <n-select v-model:value="formValue.time" :options="timeOptions" placeholder="分享有效期"/>
            </n-form-item>
            <n-form-item label="选择分享类型" path="type">
                <n-select v-model:value="formValue.type" :options="typeOptions" placeholder="分享类型"/>
            </n-form-item>
            <div v-if="formValue.type === 1">
                <div class="py-2">
                    <n-alert type="info">
                        密码需要为6位，且只能包含数字和字母。区分大小写。留空则由系统自动生成。
                    </n-alert>
                </div>

                <n-form-item label="分享密码" path="password">
                    <n-input v-model:value="formValue.password" placeholder="请输入密码"
                             show-password-on="click"
                             type="password"/>
                </n-form-item>
            </div>


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
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {useNotification, useMessage} from "naive-ui";
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
    }

})

const formValue = ref({
    time: null,
    type: null,
    password: ''
})

const formRules = {
    time: [
        {required: true, message: '请选择分享有效期'}
    ],
    type: [
        {required: true, message: '请选择分享类型'}
    ],
    password: [
        {
            validator: (rule, value) => {
                if (formValue.value.type !== 1) {
                    return true
                }
                if (value === '') {
                    return true
                }

                const reg = /^[a-zA-Z0-9]{6}$/
                if (!reg.test(value)) {
                    return new Error('密码需要为6位，且只能包含数字和字母。区分大小写。')
                }
                return true
            },
            trigger: ['change', 'input']
        }

    ]
}

const form = ref()

const timeOptions = [
    {
        label: "24小时内有效",
        value: -1
    },
    {
        label: "7天内有效",
        value: -7
    },
    {
        label: "30天内有效",
        value: -30
    },
    {
        label: "永久有效",
        value: 0
    }
]

const typeOptions = [
    {
        label: "公开分享",
        description: "创建一个公开链接，任何人都可以访问",
        value: 0
    },
    {
        label: "私密分享",
        description: "创建一个私密链接，需要密码才能访问分享内容",
        value: 1
    }
]


const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onClickConfirm(formValue.value)
        requestCreateShare()
    }).catch(() => {
    })
}

const handleCancel = () => {
    props.onClickCancel()
}


const requestCreateShare = () => {
    const config = createConfig(true)
    props.onBeforeAction()
    proxy.$axios.post(api.storageShare(props.ownerType.toLowerCase(),
        props.ownerId, props.storageType.toLowerCase(), props.storageId), {
        time: formValue.value.time,
        type: formValue.value.type,
        password: formValue.value.password
    }, config).then((res) => {
        props.onAfterAction(res.data)
    }).catch((err) => {
        popUserErrorTemplate(notification, err, "创建分享失败")
    })
}

</script>

<style scoped>

</style>