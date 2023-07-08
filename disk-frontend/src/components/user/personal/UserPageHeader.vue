<!--
  - Copyright (C) 2023 RollW
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -        http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
    <div class="rounded-2xl bg-no-repeat bg-cover bg-center bg">
        <div class="rounded-2xl pt-10 backdrop-blur-3xl">
            <div class="rounded-b-2xl p-7 pb-7 flex items-center mt-14 w-100
            bg-opacity-20 bg-gradient-to-b from-transparent to-gray-500">
                <div>
                    <n-avatar :round="true" :size="80"
                              :style="{
                                backgroundColor: getUserNameColor(),
                          }"
                              class="mr-5">
                        <div class="select-none text-2xl">
                            {{ nickname }}
                        </div>
                    </n-avatar>
                </div>

                <div>
                    <div class="leading-tight">
                        <div class="text-white text-2xl font-bold">
                            {{ userInfo.nickname }}
                        </div>
                        <div class="text-gray-200 text-xl font-light">
                            @{{ userInfo.username }}
                        </div>
                    </div>
                </div>
                <div class="flex-fill"></div>
                <div class="justify-end self-end pr-5">
                    <div class="flex">
                        <div class="text-gray-100 bg-opacity-50 rounded bg-neutral-300">
                            <div class="py-0.5 px-2">
                                UID
                            </div>
                        </div>
                        <div class="ml-3 p-0.5">
                            <div class="text-gray-100">
                                {{ userInfo.id }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import {MD5} from "@/util/crypto";
import {NAvatar} from "naive-ui";

const userInfo = defineProps({
    id: {
        type: Number,
        required: true
    },
    nickname: {
        type: String,
        required: true
    },
    username: {
        type: String,
        required: true
    },
    role: {
        type: String,
        required: true
    },
})

const getUserNameColor = () => {
    const colorExtracted = MD5(userInfo.username + userInfo.role || '').substring(4, 10)
    return `#${colorExtracted}`
}

</script>


<style scoped>
.bg {
    background: radial-gradient(110vw 250vh ellipse at 0% 11%,
    #b4cfe8,
    #5faffd 28%,
    #ff5858 35%,
    #e754db 40%,
    #fb206e 50%,
    #f09819 65%,
    #ffffff 80%);
}

</style>