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
    groupId: {
        type: [String, Number],
        required: true
    },
})

const form = ref()

const formValue = ref({
    name: ''
})

const formRules = ref({
    name: [
        {
            required: true,
            message: '请输入用户名或用户ID',
            trigger: ['blur', 'input']
        }
    ]
})

const handelConfirm = () => {
    props.onClickConfirm()
    form.value.validate().then(() => {
        props.onBeforeAction()
        requestAddMember()
    })
}

const handleCancel = () => {
    props.onClickCancel()
}

const requestAddMember = () => {
    const config = createConfig()
    proxy.$axios.put(api.userGroupsMembers(true, props.groupId), {
        name: formValue.value.name,
        type: "USER"
    }, config).then((response) => {
        props.onAfterAction()
        message.success('添加成功')
        handleCancel()
    }).catch((error) => {
        props.onAfterAction()
        popAdminErrorTemplate(notification, error,
                "添加用户组成员失败", "用户组请求错误")
    })


}

</script>

<template>
    <div>
        <n-form ref="form" :model="formValue" :rules="formRules">
            <n-form-item label="用户名或ID" path="name">
                <n-input v-model:value="formValue.name"
                         placeholder="输入用户名或用户ID" type="text"/>
            </n-form-item>
            <n-button-group>
                <n-button type="primary" @click="handelConfirm">
                    确认
                </n-button>
                <n-button secondary type="default"
                          @click="handleCancel">
                    取消
                </n-button>
            </n-button-group>
        </n-form>
    </div>
</template>

<style scoped>

</style>