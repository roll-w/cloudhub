<template>
    <div class=" h-[90vh] w-100 bg-no-repeat bg-cover bg-center">
        <div class="h-full w-full backdrop-blur-2xl backdrop-brightness-150 backdrop-hue-rotate-[200deg]">
            <div class="text-center m-auto p-5 leading-relaxed">
                <div v-if="shareError.tip" class="w-[30vw] m-auto">
                    <div class="py-3 text-3xl font-bold">
                        {{ shareError.tip }}
                    </div>
                    <div class="pt-10">
                        <n-button class="" secondary
                                  style="height: 3rem"
                                  type="primary" @click="() => $router.push('/')">
                            返回首页
                        </n-button>
                    </div>
                </div>
                <div v-else-if="showPasswordView" class="w-[30vw] m-auto">
                    <div class="text-2xl pt-10 pb-16">
                        {{ getUsername() }} 给您分享了文件
                    </div>
                    <div>
                        <n-form ref="form" :model="formValue" :rules="formRules">
                            <n-form-item path="password">
                                <n-input id="share-code"
                                         v-model:value="formValue.password"
                                         placeholder="请输入提取码"
                                         show-password-on="click"
                                         size="large"
                                         type="password"/>
                            </n-form-item>
                        </n-form>

                        <div class="pb-3 w-full">
                            <n-button class="w-100" secondary size="large"
                                      style="height: 3rem"
                                      type="primary" @click="handlePasswordInputConfirm">
                                查看文件
                            </n-button>
                        </div>
                        <div class="text-neutral-400">
                            {{ getExpireTime() }}
                        </div>
                    </div>
                </div>
                <div v-else>
                    <div class="flex p-5 text-left">
                        <div class="w-[20%]">
                            <n-space>
                                <div class="text-2xl ">
                                    <span class="font-bold mr-5">{{ getUsername() }}</span>给您的分享
                                </div>
                                <div>
                                    <n-grid cols="3">
                                        <n-grid-item class="text-neutral-400">
                                            <div>
                                                分享时间
                                            </div>
                                        </n-grid-item>
                                        <n-grid-item span="2">
                                            <div>
                                                {{ formatTimestamp(shareStructureInfo.createTime) }}
                                            </div>
                                        </n-grid-item>
                                    </n-grid>
                                </div>
                                <div>
                                    <n-grid cols="3">
                                        <n-grid-item class="text-neutral-400">
                                            <div>
                                                有效期至
                                            </div>
                                        </n-grid-item>
                                        <n-grid-item span="2">
                                            <div v-if="shareStructureInfo.expireTime">
                                                {{ formatTimestamp(shareStructureInfo.expireTime) }}
                                            </div>
                                            <div v-else>
                                                永久有效
                                            </div>
                                        </n-grid-item>
                                    </n-grid>
                                </div>
                            </n-space>
                        </div>
                        <n-divider style="width: 2px; height: 30vh" vertical/>
                        <Transition mode="out-in" name="fade">
                            <div>
                                <FileComponentsView
                                        :file-options="fileOptions"
                                        :on-storage-click="handleStorageClick"
                                        :files="shareStructureInfo.storages">
                                    <template #folder>
                                        <n-breadcrumb separator=">">
                                            <n-breadcrumb-item>
                                                <span class="text-xl"
                                                     @click="setCurFolderId(0)">
                                                    分享
                                                </span>
                                            </n-breadcrumb-item>
                                            <n-breadcrumb-item
                                                    v-for="folder in (shareStructureInfo.parents || [])">
                                                <span class="text-xl"
                                                     @click="setCurFolderId(folder.storageId)">
                                                    {{ folder.name }}
                                                </span>
                                            </n-breadcrumb-item>
                                            <n-breadcrumb-item v-if="shareStructureInfo.current.storageId !== 0">
                                                <span class="text-xl">
                                                    {{ shareStructureInfo.current.name }}
                                                </span>
                                            </n-breadcrumb-item>
                                        </n-breadcrumb>
                                    </template>

                                </FileComponentsView>
                            </div>
                        </Transition>

                    </div>
                </div>
            </div>
        </div>
        <n-modal v-model:show="showFilePreviewModal"
                 :show-icon="false"
                 :title="curTargetFile.name + ' 预览'"
                 class="w-full h-full"
                 preset="card"
                 style="width: 100vw; height: 100vh">
            <div class="p-5">
                <div>
                    <n-alert type="warning">
                        当前文件预览功能仅支持图片、文本、音频、视频文件，其他文件请下载后打开。
                    </n-alert>
                </div>
                <div class="pt-3">
                    <FilePreviewer
                            :file="curTargetFile"
                    />
                </div>
            </div>
        </n-modal>
    </div>
</template>

<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {formatTimestamp} from "@/util/format";
import {popUserErrorTemplate} from "@/views/util/error";
import FilePreviewer from "@/components/file/FilePreviewer.vue";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import {driveFilePage, index} from "@/router";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const token = router.currentRoute.value.params.token
const passwordParam = router.currentRoute.value.query.pwd

const formValue = ref({
    password: ''
})

const form = ref()

const formRules = ref({
    password: [
        {required: true, message: '请输入提取码'},
        {len: 6, message: '提取码长度为 6 位'}
    ]
})

const showPasswordView = ref(true)
const shareMetaInfo = ref({
    isPublic: false,
})

const shareStructureInfo = ref()
const sharePassword = ref(passwordParam)
const shareError = ref({
    tip: ''
})

const showFilePreviewModal = ref(false)
const curTargetFile = ref({})

const folderInfo = ref({
    parents: []
})

const parseHash = () => {
    const hash = router.currentRoute.value.hash
    if (hash) {
        const path = hash.split('=')[1]
        if (path) {
            return path
        }
    }
    return 0
}

const folder = 0
const curFolderId = ref(folder)


const setCurFolderId = (folder) => {
    curFolderId.value = folder
    requestGetShareInfoWithinPassword()
}

const fileOptions = [
    {
        label: "下载",
        key: "download",
    },
    {
        label: "转存",
        key: "transfer",
    }
]

const previewableTypes = ['IMAGE', 'TEXT', 'PDF', 'VIDEO', 'AUDIO', 'DOCUMENT']

const handleStorageClick = (e, target) => {
    if (target.storageType === 'FOLDER') {
        setCurFolderId(target.storageId)
        return
    }
    if (previewableTypes.includes(target.fileType)) {
        curTargetFile.value = target
        showFilePreviewModal.value = true
        return
    }

    message.warning('暂不支持预览此类型文件')
}


const getShareMetaInfo = () => {
    proxy.$axios.get(api.shareTokenInfo(token)).then(res => {
        shareMetaInfo.value = res.data
        if (res.data.isPublic || passwordParam) {
            requestGetShareInfoWithinPassword(curFolderId.value, passwordParam)
        }
    }).catch(err => {
        shareError.value = err
        showPasswordView.value = false
    })
}

const getExpireTime = () => {
    if (!shareMetaInfo.value.expireTime) {
        return '永久有效'
    }
    return '有效期至 ' + formatTimestamp(shareMetaInfo.value.expireTime)
}

const getUsername = () => {
    const username = shareMetaInfo.value.nickname;
    if (!username) {
        return '**'
    }
    const len = username.length || 1
    if (len <= 3) {
        return username[0] + '**'
    }
    return username.substring(0, 2) + '*'.repeat(len - 3) + username[len - 1]
}

const requestGetShareInfoWithinPassword = (parentId = curFolderId.value,
                                           code = sharePassword.value) => {
    proxy.$axios.get(api.shareToken(token), {
        params: {
            password: code,
            parent: parentId
        }
    }).then((res) => {
        sharePassword.value = code
        showPasswordView.value = false
        shareStructureInfo.value = res.data
    }).catch((err) => {
        popUserErrorTemplate(notification, err, '获取分享信息失败')
    })
}

const handlePasswordInputConfirm = () => {
    form.value.validate().then(() => {
        requestGetShareInfoWithinPassword(0, formValue.value.password)
    }).catch(() => {
    })
}


getShareMetaInfo()

</script>