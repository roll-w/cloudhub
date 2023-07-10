<template>
    <div class="p-5">
        <n-h1 class="py-2">
            文件搜索
        </n-h1>
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
                <n-input v-model:value="inputRef" clearable
                         placeholder="筛选条件输入框"
                         type="text"
                />
                <n-button-group>
                    <n-button type="primary" @click="handleSearch">搜索</n-button>
                </n-button-group>
            </n-input-group>
            <div class="flex py-3">
                <div class="text-neutral-500">
                    快捷选项：
                </div>
                <div class="pl-3">
                    <div v-for="item in quickOptions" class="cursor-pointer mr-2 mb-2 inline-block">
                        <n-tag :bordered="false" :type="isInConditions(item) ? 'primary' : 'default'"
                               round
                               style="cursor: pointer;" @click="handleQuickOptionClick(item)">
                            {{ item.value }}
                        </n-tag>
                    </div>

                </div>
            </div>

        </div>


        <div class="text-xl py-3">
            搜索结果
        </div>
        <div>
            <div class="py-3 min-h-[30vh]">
                <FileComponentsView
                        :disable-click="false"
                        :disable-preview="false"
                        :file-options="fileOptions"
                        :files="result"
                        :menu-options="contextMenuOptions"
                        :on-menu-select="handleMenuSelect"
                        :on-show-file-option="hackFileOptions"
                >
                    <template #title>
                        共搜索到 {{ result.length }} 个结果
                    </template>
                    <template #empty>
                        <div class="py-5">
                            <n-empty description="没有搜索到文件"/>
                        </div>
                    </template>
                </FileComponentsView>
            </div>
        </div>

    </div>
</template>

<script setup>
import {ref, getCurrentInstance, h, watch} from "vue";
import {RouterLink, useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NIcon} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {popAdminErrorTemplate, popUserErrorTemplate} from "@/views/util/error";
import {driveFileAttrsPage, driveFilePermissionPage} from "@/router";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";
import {toFileType} from "@/views/names";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userStore = useUserStore()

const queryKeyword = decodeURI(router.currentRoute.value.query.keyword || '') || ''
const inputRef = ref(queryKeyword)


// TODO: extract to a util function
const contextMenuOptions = [
    {
        label: "刷新",
        key: "refresh",
        icon() {
            return h(NIcon, null, {
                default: () => h(RefreshRound)
            })
        },
    }
]

const fileOptions = [
    {
        label: "下载",
        key: "download",
    },
    {
        label: "分享",
        key: "share",
    },
    {
        label: "收藏",
        key: "collect",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "日志",
        key: "log",
    },
    {
        label: "权限",
        key: "permission",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "重命名",
        key: "rename",
    },
    {
        label: "移动",
        key: "move",
    },
    {
        label: "复制",
        key: "copy",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: () => h(
                'div',
                {
                    class: "text-red-500 mr-10"
                },
                {default: () => "删除"}
        ),
        key: "delete",
    },
]

const hackFileOptions = (file) => {
    fileOptions.find(option => option.key === 'log').label = () => {
        return h(RouterLink, {
            to: {
                name: driveFileAttrsPage,
                params: {
                    ownerType: 'user',
                    ownerId: userStore.user.id,
                    id: file.storageId,
                    type: file.storageType.toLowerCase()
                },
                query: {
                    refer: 'search',
                    source: encodeURI(inputRef.value)
                }
            },
        }, {
            default: () => '属性与日志'
        });
    }
    fileOptions.find(option => option.key === 'permission').label = () => {
        return h(RouterLink, {
            to: {
                name: driveFilePermissionPage,
                params: {
                    ownerType: 'user',
                    ownerId: userStore.user.id,
                    id: file.storageId,
                    type: file.storageType.toLowerCase()
                },
                query: {
                    refer: 'search',
                    source: encodeURI(inputRef.value)
                }
            },
        }, {
            default: () => '权限'
        });
    }
}


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

const parsedInputConditions = ref([])

const parseInput = (input) => {
    const result = []
    const options = input.replace(/：/g, ':').split(' ')
    let index = 0
    for (const option of options) {
        if (!option) {
            continue
        }

        const [key, value] = option.split(':')
        result[index] = {
            key: key,
            value: value
        }
        index++
    }
    return result
}

const toExpr = (conditions) => {
    let result = ''
    for (const condition of conditions) {
        result += `${condition.key}:${condition.value} `
    }
    return result
}

const isInConditions = (item) => {
    for (let valueElement of parsedInputConditions.value) {
        if (valueElement.key === item.value) {
            return true
        }
    }
    return false
}

const handleQuickOptionClick = (item) => {
    if (isInConditions(item)) {
        const newConditions = []
        for (let valueElement of parsedInputConditions.value) {
            if (valueElement.key !== item.value) {
                newConditions.push(valueElement)
            }
        }
        inputRef.value = newConditions.map(valueElement => `${valueElement.key}：${valueElement.value}`).join(' ')
        return
    }
    if (inputRef.value === '') {
        inputRef.value += `${item.value}：`
        return
    }

    inputRef.value += ` ${item.value}：`
}

const onInputChange = () => {
    parsedInputConditions.value = parseInput(inputRef.value)
}


const errorTipText = ref()

const checkConditions = () => {
    const checkResults = []

    if (parsedInputConditions.value.length === 0) {
        return []
    }
    for (let valueElement of parsedInputConditions.value) {
        if (!valueElement.value) {
            checkResults.push({
                key: valueElement.key,
                tip: '值不能为空'
            })
        }
    }
    return checkResults
}

watch(inputRef, onInputChange)

const replaceSearchExpression = (original) => {
    const replaced = original.replace(/：/g, ':')
            .replace(/名称:/g, 'name:')
            .replace(/文件类型:/g, 'type:')
            .replace(/创建时间:/g, 'time:')
            .replace(/修改时间:/g, 'last_modified:')
            .replace(/创建者:/g, 'owner:')
            .replace(/最后修改者:/g, 'last_modifier:')
    const parsed = parseInput(replaced)
    const index = parsed.findIndex(item => item.key === 'type')
    if (index < 0) {
        return replaced
    }
    parsed[index].value = toFileType(parsed[index].value)
    return toExpr(parsed)
}

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

    const checkResults = checkConditions()

    if (checkResults.length) {
        errorTipText.value = checkResults.map(item => item.key + "：" + item.tip).join('\n')
        popUserErrorTemplate(notification, {
            tip: errorTipText.value
        }, "搜索失败")
        return
    }

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

const handleMenuSelect = (key) => {
    if (key === 'refresh') {
        requestSearch()
        message.success('刷新成功')
    }
}


onInputChange()
requestSearch()

</script>

<style scoped>

</style>