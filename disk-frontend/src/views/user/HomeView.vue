<template>
    <div class="flex flex-fill flex-grow w-100 h-100vh h-full bg-no-repeat bg-cover bg-center bg ">

        <div class="w-100 h-100 backdrop-brightness-125">
            <div class="flex flex-col w-100 px-16 p-6 h-100">
                <div class="flex ">
                    <div class="p-3 rounded-2xl hover:bg-neutral-200 hover:bg-opacity-30 transition-all ease-in"
                         role="button">
                        <a>
                            <Logo/>
                        </a>
                    </div>
                </div>
                <div class="flex flex-col p-5 flex-1">
                    <div class="leading-tight sm:leading-tight text-4xl sm:text-6xl font-extrabold text-black ">
                        Cloudhub<br>法律案件资料库
                    </div>
                    <div class="flex-fill py-7 text-gray-500 text-2xl leading-relaxed sm:w-2/5">
                        Cloudhub 法律案件资料库是一个专业的法律文件资料库。可以快速、安全、便捷的管理您的法律文件资料。
                    </div>
                </div>

                <div class="flex flex-fill flex-col px-5">
                    <div class="flex flex-fill align-bottom">
                        <a v-if="!userStore.isLogin" class="link-btn" href="/user/login">
                            立即登录
                        </a>
                        <a v-else class="link-btn" href="/drive">
                            立即进入
                        </a>
                    </div>
                    <div class="text-gray-500 opacity-70
                    transition-all hover:text-gray-800 hover:opacity-100 hover:drop-shadow hover:shadow-white">
                        ©2023 Cloudhub. All rights reserved. 版权所有
                        <br>
                        <a v-if="icp" class="hover:underline hover:underline-offset-4" href="https://beian.miit.gov.cn/"
                           target="_blank">
                            {{ icp }}
                        </a>
                        <br>
                        <a v-if="beian" href="https://www.beian.gov.cn/" target="_blank">
                            {{ beian }}
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>

<script setup>
import {ref} from "vue";
import {useUserStore} from "@/stores/user";
import {useRouter} from "vue-router";
import {driveFilePage} from "@/router";
import Logo from "@/components/icon/Logo.vue";

const current = ref(null)
const userStore = useUserStore()

const router = useRouter()

const checkLogin = () => {
    if (userStore.isLogin) {
        router.push({
            name: driveFilePage
        })
    }
}

const icp = ((window.cloudhub || {}).server || {}).icp
const beian = ((window.cloudhub || {}).server || {}).beian

</script>

<style scoped>
.bg {
    background-image: url('/public/img/mind.jpg');
}

.link-btn {
    @apply text-xl transition-all bg-amber-500 hover:bg-amber-600
    focus:outline-none focus:ring-2 focus:ring-amber-400
    focus:ring-offset-2 focus:ring-offset-amber-50
    text-white font-semibold h-14 px-6 rounded-xl
    cursor-pointer
    w-full flex items-center justify-center sm:w-auto;
}

</style>