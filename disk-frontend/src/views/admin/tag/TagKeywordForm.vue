<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";
import {tagsKeywords} from "@/request/api_urls";

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
    tagId: {
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
            message: '请输入关键词',
            trigger: ['blur', 'input']
        },
        {
            max: 20,
            message: '关键词长度不能超过20个字符',
            trigger: ['blur', 'input']
        }
    ],
    weight: [
        {
            required: true,
            type: 'number',
            message: '请输入权重',
            trigger: ['blur', 'input']
        },
    ]
}


const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onClickConfirm()
        requestKeyword()
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

const requestKeyword = () => {
    const config = createConfig()
    proxy.$axios.put(api.tagsKeywords(props.tagId), {
        name: formValue.value.name,
        weight: formValue.value.weight
    }, config)
            .then((res) => {
                message.success('操作成功')
                props.onAfterAction()
            })
            .catch((e) => {
                props.onAfterAction()
                popAdminErrorTemplate(notification, e,
                        '操作失败', '标签请求错误')
            })
}

</script>

<template>
    <div>
        <n-form ref="form" :model="formValue" :rules="rules">
            <n-form-item label="关键词名称" path="name">
                <n-input v-model:value="formValue.name"
                         :disabled="type === 'edit'"
                         placeholder="请输入关键词"/>
            </n-form-item>
            <n-form-item class="w-full" label="权重" path="weight">
                <n-input-number v-model:value="formValue.weight" class="w-full"
                                placeholder="请输入权重"/>
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