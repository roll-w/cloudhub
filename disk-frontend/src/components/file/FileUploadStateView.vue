<template>
    <div class="w-[50vw] min-h-[300px] ">
        <div class="px-3 pt-3 pb-2">
            仅显示本次上传的文件
        </div>
        <div class="min-h-[40vh] ">
            <n-scrollbar class="max-h-[60vh]">
                <FileUploadInfoComponent v-for="upload in fileUploads"
                                         :key="upload.id"
                                         :file="upload"/>
                <n-divider dashed>
                    <div class="text-center text-neutral-400">无更多内容</div>
                </n-divider>
            </n-scrollbar>

        </div>

        <div class="px-3 pt-2 pb-5">
            共 {{ fileUploads.length }} 项，成功 {{ successCount }} 项
        </div>
    </div>
</template>

<script setup>

import {useFileStore} from "@/stores/files";
import {ref, watch} from "vue";
import FileUploadInfoComponent from "@/components/file/FileUploadInfoComponent.vue";

const fileStore = useFileStore()
const fileUploads = ref(fileStore.getUploads)

const successCount = ref()
const failCount = ref()

const calcCount = (uploads) => {
    let success = 0
    let fail = 0
    uploads.forEach(upload => {
        if (upload.status === 'success') {
            success++
        }
        if (upload.status === 'error') {
            fail++
        }
    })
    successCount.value = success
    failCount.value = fail
}

calcCount(fileStore.getUploads)

const updateUploads = async (uploads) => {
    fileUploads.value = uploads
    calcCount(uploads)
}

watch(fileStore.$state, (newVal, oldVal) => {
        updateUploads(newVal.uploads)
    }, {deep: true}
)

fileStore.$onAction(
    ({
         name,
         store,
         args,
         after,
         onError,
     }) => {
        if (name !== 'updateUpload') {
            return
        }

        after((result) => {
            updateUploads(store.getUploads)
        })
    })

</script>

<style scoped>

</style>