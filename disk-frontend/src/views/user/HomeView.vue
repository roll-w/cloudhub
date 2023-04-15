<template>
    <div class="flex flex-fill flex-grow w-100 h-100vh h-full bg-no-repeat bg-cover bg-center bg ">
        <div class="w-100 h-100 backdrop-brightness-125">
            <div class="flex flex-col w-100 px-16 p-6 h-100">
                <div class="flex flex-col p-5 flex-1">
                    <!--TODO: place logo here -->
                    <div class="leading-tight sm:leading-tight text-4xl sm:text-6xl font-extrabold text-black ">
                        Cloudhub<br>法律案件资料库
                    </div>
                    <div class="flex-fill py-7 text-gray-500 text-2xl leading-relaxed sm:w-2/5">
                        Cloudhub 法律案件资料库是一个专业的法律文件资料库。可以快速、安全、便捷的管理您的法律文件资料。
                    </div>
                </div>

                <div class="flex flex-fill flex-col px-5">
                    <div class="flex flex-fill align-bottom">
                        <a class="text-xl transition-all bg-amber-500 hover:bg-amber-600
                    focus:outline-none focus:ring-2 focus:ring-amber-400
                    focus:ring-offset-2 focus:ring-offset-amber-50
                    text-white font-semibold h-14 px-6 rounded-xl
                    cursor-pointer
                    w-full flex items-center justify-center sm:w-auto "
                           href="/user/login">
                            立即登录
                        </a>
                    </div>
                    <div class="text-gray-500 opacity-70 transition-all hover:text-gray-800 hover:opacity-100">
                        ©2023 Cloudhub. All rights reserved. 版权所有
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>

<script setup>
import {ref} from "vue";
import {useUserStore} from "@/stores/user";

const current = ref(null)
const userStore = useUserStore()

const checkLogin = () => {
    if (userStore.isLogin) {
    }
}

</script>

<style scoped>
.bg {
    /*background-image: url('https://img.alicdn.com/imgextra/i1/O1CN010wJ9Hw21g0Hlwo9RT_!!6000000007013-2-tps-2880-1412.png');*/
    background-image: url('/public/img/mind.jpg');
}

</style>