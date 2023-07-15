<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";

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
    name: {
        type: String,
        default: ''
    },
    groupId: {
        type: [String, Number],
        required: true
    },
    settingKey: {
        type: String,
        required: true
    },
    value: {
        type: String,
        default: ''
    },
})

const form = ref()

const formValue = ref({
    value: props.value === '-1' ? '' : props.value
})

const unlimitedChecked = ref(props.value === '-1')

const handleConfirm = () => {
    props.onClickConfirm()
    form.value.validate().then(() => {
        props.onBeforeAction()
        requestSetSetting()
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

const requestSetSetting = () => {
    const config = createConfig()
    proxy.$axios.put(api.userGroupSetting(true, props.groupId), {
        key: props.settingKey,
        value: unlimitedChecked.value ? '-1' : formValue.value.value
    }, config).then(() => {
        message.success('设置成功')
        props.onAfterAction()
    }).catch((e) => {
        props.onAfterAction()
        popUserErrorTemplate(notification, e, "修改用户组设置失败",
                "用户组请求错误")
    })
}

</script>

<template>
    <div>
        <n-form ref="form" :model="formValue">
            <n-form-item label="名称" >
                <div>
                    {{ props.name }}
                </div>
            </n-form-item>
            <n-form-item label="值" path="value">
                <div class="w-100">
                    <n-input v-model:value="formValue.value"
                             :disabled="unlimitedChecked"
                             placeholder="请输入新的值"
                             type="text"/>
                    <div class="pt-1">
                        <n-checkbox v-model:checked="unlimitedChecked">
                            无限制
                        </n-checkbox>
                    </div>
                </div>
            </n-form-item>
        </n-form>
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
    </div>
</template>

<style scoped>

</style>