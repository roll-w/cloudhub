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
    type: {
        type: String,
        default: 'edit'
    },
    groupId: {
        type: [String, Number],
        required: true
    },
    name: {
        type: String,
        default: ''
    },
    weight: {
        type: [String, Number],
        default: ''
    },

})

const form = ref()
const formValue = ref({
    name: props.name,
    weight: props.weight
})

const rules = {
    name: [
        {
            required: true,
            message: '请输入标签名称',
            trigger: ['blur', 'input']
        },
        {
            max: 30,
            message: '标签长度不能超过20个字符',
            trigger: ['blur', 'input']
        }
    ],
}


const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onClickConfirm()
        requestAddTag()
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

const requestAddTag = () => {
    const config = createConfig()
    proxy.$axios.post(api.tagGroupsTags(props.groupId), {
        value: formValue.value.name,
    }, config).then((res) => {
        message.success('添加成功')
        props.onAfterAction()
    }).catch((e) => {
        props.onAfterAction()
        popAdminErrorTemplate(notification, e,
                '添加失败', '标签组请求错误')
    })
}

</script>

<template>
    <div>
        <n-form ref="form" :model="formValue" :rules="rules">
            <n-form-item label="标签名称" path="name">
                <n-input v-model:value="formValue.name"
                         placeholder="请输入标签名称"/>
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