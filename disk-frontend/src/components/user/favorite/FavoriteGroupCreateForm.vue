<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {popUserErrorTemplate} from "@/views/util/error";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";

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
    isPublic: false
})

const formRules = ref({
    name: [
        {
            required: true,
            message: '请输入收藏夹名称',
            trigger: ['blur', 'input']
        },
        {
            max: 20,
            min: 1,
            message: '收藏夹名称长度需在 1 到 20 个字符间',
            trigger: ['blur', 'input']
        }
    ],
})

const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onClickConfirm()
        props.onBeforeAction()
        requestCreateFavoriteGroup()
    })
}

const requestCreateFavoriteGroup = () => {
    const config = createConfig(true)
    proxy.$axios.post(api.favorites(), {
        name: formValue.value.name,
        isPublic: formValue.value.isPublic
    }, config).then((response) => {
        message.success("创建成功")
        props.onAfterAction()
    }).catch((error) => {
        props.onAfterAction()
        popUserErrorTemplate(notification, error,
                "创建收藏夹失败", "收藏夹请求错误")
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

</script>

<template>
    <div>
        <n-form ref="form"
                :model="formValue"
                :rules="formRules">
            <n-form-item label="收藏夹名称" path="name">
                <n-input v-model:value="formValue.name"
                         placeholder="请输入收藏夹名称"
                         type="text"/>
            </n-form-item>
            <n-form-item label="公开" name="isPublic">
                <n-checkbox v-model:checked="formValue.isPublic">
                    设置为公开收藏夹
                </n-checkbox>
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

<style scoped>

</style>