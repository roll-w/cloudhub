<template>
    <div>
        <div class="pb-5">
            <n-alert type="info">
                <div>
                    若上传的文件名与已有文件名相同，将会覆盖原文件。
                </div>
            </n-alert>
        </div>

        <n-form ref="form" :model="formValue" :rules="formRules">
            <n-form-item label="文件" path="file">
                <n-upload
                        v-model:file-list="formValue.file"
                        :default-upload="false"
                        :directory="false"
                        :directory-dnd="false"
                        :max="10"
                        :multiple="true"
                        name="file">
                    <n-upload-dragger>
                        <n-text class="text-xl">
                            点击或者拖动文件到此区域来上传
                        </n-text>
                    </n-upload-dragger>
                </n-upload>
            </n-form-item>
            <div class="pb-3">
                如果你不想按照原文件名上传，可以在这里修改文件名
            </div>
            <n-form-item label="文件名" path="name">
                <n-input v-model:value="formValue.name" placeholder="修改文件名，不填写即为原文件名" type="text"/>
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

<script setup>

import {getCurrentInstance, ref} from "vue";
import {useMessage, useNotification} from "naive-ui";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {useFileStore} from "@/stores/files";
import axios from "axios";
import * as uuid from "uuid";


const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const fileStore = useFileStore()

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
    folderId: {
        type: Number,
        default: 0
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

const form = ref()
const formValue = ref({
    name: '',
    file: []
})

const formRules = {
    name: [
        {max: 120, message: '文件名长度不能超过120个字符', trigger: 'blur'}
    ],
    file: [
        {
            validator: (rule, value) => {
                if (value.length === 0) {
                    return Promise.reject('请上传文件')
                }
                return Promise.resolve()
            }, trigger: 'change'
        }
    ]
}

const handleCancel = () => {
    props.onClickCancel()
}

const handleConfirm = () => {
    form.value.validate().then(() => {
        props.onClickConfirm()
        uploadFile()
    })
}

const source = ref(null)

const uploadFile = () => {
    props.onBeforeAction()
    for (const file of formValue.value.file) {
        handleUpload(file.file)
    }
    props.onAfterAction()
}

const handleUpload = (file) => {
    const config = createConfig()
    const formData = new FormData()
    if ((formValue.value.name || '').trim()) {
        formData.append('file', file, formValue.value.name.trim())
    } else {
        formData.append('file', file)
    }
    let cancelToken = axios.CancelToken
    source.value = cancelToken.source()
    config.cancelToken = source.value.token

    const newFileName = formData.get('file').name

    const uploadFile = {
        id: uuid.v4(),
        progress: 0,
        folderId: props.folderId,
        name: newFileName,
        size: file.size,
        type: file.type,
        status: 'uploading',
        onUploadCallback: (progress, status) => {
        },
        cancel: () => {
            source.value.cancel('CANCEL')
        },
    }
    fileStore.updateUpload(uploadFile)

    config.headers['Content-Type'] = 'multipart/form-data'
    config.onUploadProgress = (progressEvent) => {
        let percentCompleted =
                Math.round((progressEvent.loaded * 100) / progressEvent.total)
        if (percentCompleted > 70) {
            percentCompleted = Math.max(70, Math.round(percentCompleted * 0.93))
        }
        uploadFile.progress = percentCompleted
        uploadFile.onUploadCallback(uploadFile.progress, uploadFile.status)
        fileStore.updateUpload(uploadFile)
    }

    proxy.$axios.post(
            api.uploadFile(props.ownerType, props.ownerId, props.folderId), formData, config).then(res => {
        uploadFile.status = 'success'
        uploadFile.progress = 100
        uploadFile.onUploadCallback(uploadFile.progress, uploadFile.status)
        fileStore.updateUpload(uploadFile)
    }).catch(err => {
        if (err.errorCode === 'CANCEL') {
            message.error('上传已取消')
            uploadFile.status = 'cancel'
            uploadFile.onUploadCallback(uploadFile.progress, uploadFile.status)
            fileStore.updateUpload(uploadFile)
            return
        }
        uploadFile.status = 'error'
        uploadFile.onUploadCallback(uploadFile.progress, uploadFile.status)
        fileStore.updateUpload(uploadFile)
        popUserErrorTemplate(notification, err, '上传失败', '上传请求错误')
    })
}

</script>

<style scoped>

</style>