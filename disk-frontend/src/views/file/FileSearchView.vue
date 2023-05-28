<template>
    <div class="p-5">
        <div class="text-3xl py-2">
            文件搜索
        </div>
        <div>
            <n-alert type="info">
                <div class="text-xl">
                    条件搜索说明
                </div>
                <div class="leading-loose mt-3">
                    <n-text tag="p">
                        搜索条件为一组关键词。格式为： 类型：内容。
                        <br>
                        搜索内容中请不要包含冒号，否则将有可能被解析为类型，无法返回正确的搜索结果。
                    </n-text>
                </div>
            </n-alert>
        </div>
        <div class="py-2">
            <n-input-group>
                <n-input v-model:value="inputRef" placeholder="筛选条件输入框" type="text"/>
                <n-button-group>
                    <n-button type="primary" @click="requestSearch">搜索</n-button>
                    <n-button secondary @click="handleReset">重置</n-button>
                </n-button-group>
            </n-input-group>

        </div>


        <div class="text-xl py-3">
            搜索结果
        </div>
        <div>
            <n-list>
                <n-list-item v-for="file in result" :key="file.storageId">
                    <n-card>
                        <div class="text-xl pb-3">
                            {{ file.name }}
                        </div>
                        <div class="text-sm">
                            创建时间： {{ formatTimestamp(file.createTime) }}
                        </div>
                    </n-card>
                    <n-divider/>
                </n-list-item>
            </n-list>
        </div>
    </div>
</template>

<script setup>
import {ref, getCurrentInstance} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {popAdminErrorTemplate} from "@/views/util/error";
import {formatTimestamp} from "@/util/format";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userStore = useUserStore()

const inputRef = ref('')

const result = ref([])

const quickOptions = [
    {
        value: '名称'
    },
    {
        value: '文件类型'
    },
    {
        value: '文书类型'
    },
    {
        value: '诉讼类型'
    },
    {
        value: '审理阶段'
    },
    {
        value: '创建者'
    },
    {
        value: '最后修改者'
    },
    {
        value: '创建时间'
    },
    {
        value: '修改时间'
    }
]

const replaceSearchExpression = (original) => {
    return original.replace(/：/g, ':')
            .replace(/名称:/g, 'name:')
            .replace(/文件类型:/g, 'type:')
            .replace(/创建时间:/g, 'time:')
            .replace(/修改时间:/g, 'last_modified:')
            .replace(/创建者:/g, 'owner:')
            .replace(/最后修改者:/g, 'last_modified_by:')
}

const requestSearch = () => {
    const query = replaceSearchExpression(inputRef.value)
    const config = createConfig()

    proxy.$axios.get(api.search('user', userStore.user.id), {
        params: {
            expr: query
        },
        ...config
    }).then((response) => {
        result.value = response.data
    }).catch((error) => {
        popAdminErrorTemplate(notification, error, "搜索失败")
    })
}

</script>

<style scoped>

</style>