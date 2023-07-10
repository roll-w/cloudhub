<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import {userPersonalPage} from "@/router";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const users = ref([])

const keyword = router.currentRoute.value.query.keyword || ''
const inputRef = ref(keyword)

const handleSearch = () => {
    router.push({
        query: {
            keyword: encodeURI(inputRef.value)
        }
    })
}

const requestSearch = () => {
    if (!inputRef.value) {
        return
    }
    const config = createConfig()
    config.params = {
        keyword: inputRef.value
    }

    proxy.$axios.get(api.searchUsers(), config).then(resp => {
        users.value = resp.data
    }).catch(error => {
        popUserErrorTemplate(notification, error, '搜索用户失败')
    })
}

const handleUserClick = (user) => {
    router.push({
        name: userPersonalPage,
        params: {
            id: user.userId
        }
    })
}

requestSearch()

</script>

<template>
    <div class="p-5">
        <n-h1 class="pt-2">
            用户搜索
        </n-h1>
        <div class="py-2">
            <n-input-group>
                <n-input v-model:value="inputRef" clearable
                         placeholder="通过ID/用户名/昵称搜索用户..."
                         type="text"
                />
                <n-button-group>
                    <n-button type="primary" @click="handleSearch">搜索</n-button>
                </n-button-group>
            </n-input-group>
        </div>
        <div class="text-xl py-3">
            搜索结果
        </div>
        <n-empty v-if="users.length === 0"
                 class="py-5"
                 description="没有搜索到用户"/>

        <div class="">
            <n-grid cols="3" x-gap="10">
                <n-gi v-for="user in users" class="py-2">
                    <router-link #="{ navigate, href }"
                                 :to="{name: userPersonalPage, params: {id: user.userId}}" custom>
                        <n-a :href="href" class="cursor-pointer" @click="navigate">
                            <n-card>
                                <div class="flex flex-row">
                                    <div class="flex flex-col">
                                        <div class="text-xl">
                                            {{ user.nickname }}
                                        </div>
                                        <div class="text-lg">
                                            @{{ user.username }}
                                        </div>
                                    </div>
                                    <div class="flex-grow"/>
                                    <div class="flex flex-col justify-end">
                                        <div class="text-gray-500 text-sm">
                                            {{ user.userId }}
                                        </div>
                                    </div>
                                </div>
                            </n-card>
                        </n-a>
                    </router-link>
                </n-gi>
            </n-grid>
        </div>
    </div>
</template>

<style scoped>

</style>