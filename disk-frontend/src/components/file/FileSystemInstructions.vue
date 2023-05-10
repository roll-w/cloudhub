<template>
    <div class="p-5">
        <div class="pb-5">
            <n-alert closable type="info">
                <div class="text-xl">
                    欢迎使用 Cloudhub 法律文件资料库，您可以在这里上传、下载、分享、管理您的资料文件。
                </div>
            </n-alert>
        </div>
        <div>
            <transition mode="out-in" name="fade">
                <div v-if="!closeCard">
                    <n-card :on-close="() => closeCard = true"
                            closable>
                        <div class="text-4xl pb-5">
                            使用指引
                        </div>
                        <n-steps>
                            <n-step
                                    description="进入文件资料库，右键并点击“上传文件”按钮，选择您要上传的文件。"
                                    status="process"
                                    title="上传文件"/>
                            <n-step
                                    description="在文件列表中找到您需要下载的文件，点击“下载”按钮，即可下载文件。"
                                    status="process"
                                    title="下载文件"/>
                            <n-step
                                    description="在文件列表中找到您需要分享的文件，点击“分享”按钮，即可分享文件。"
                                    status="process"
                                    title="分享文件"/>
                            <n-step
                                    description="如果您是文件的所有者，或者您为部门管理员，您可以在文件列表中找到您需要设置的文件，点击“设置权限”按钮，即可管理文件。"
                                    status="process"
                                    title="设置文件权限"/>
                        </n-steps>
                    </n-card>
                </div>
            </transition>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";

const closeCard = ref(false)

</script>

<style scoped>

</style>