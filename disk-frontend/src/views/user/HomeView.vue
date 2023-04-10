<template>
    <div class="flex flex-fill flex-grow w-100 h-full bg-no-repeat bg-cover bg-center bg ">
        <div class="w-100 p-5">
            <div class="p-5">
                <!--TODO: place logo here -->
                <div class="text-6xl leading-normal font-extrabold text-black ">
                    Cloudhub<br>法律案件资料库
                </div>
                <div class="py-7 text-gray-400 text-2xl leading-relaxed sm:w-2/5">
                    Cloudhub 法律文件资料库是一个专业的法律文件资料库，
                    快速、安全、便捷的管理您的法律文件资料。
                </div>

                <div class="flex flex-fill align-bottom justify-start px-3">
                    <button class="text-xl transition-all bg-amber-500 hover:bg-amber-600
                    focus:outline-none focus:ring-2 focus:ring-amber-400
                     focus:ring-offset-2 focus:ring-offset-amber-50
                     text-white font-semibold h-14 px-6 rounded-xl
                     w-full flex items-center justify-center sm:w-auto ">
                        立即登录
                    </button>
                </div>
            </div>
        </div>
    </div>

</template>

<script setup>
import {ref} from "vue";

const current = ref(null)

</script>

<style scoped>
.bg {
    background-image: url('https://img.alicdn.com/imgextra/i1/O1CN010wJ9Hw21g0Hlwo9RT_!!6000000007013-2-tps-2880-1412.png');
}

</style>