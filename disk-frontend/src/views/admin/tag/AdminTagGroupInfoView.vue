<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminTagGroups" :menu="adminMenuTag"/>
        <div>
            <div class="flex items-baseline mt-5">
                <n-h1>
                    <span class="text-amber-400">
                        {{ tagGroup.name }}
                    </span>
                    标签组信息
                </n-h1>
                <div class="flex flex-grow justify-end">
                    <n-button @click="back">返回</n-button>
                </div>
            </div>

            <div class="grid grid-cols-10 py-3">
                <div class="text-gray-600 mr-3 ">
                    标签组信息
                </div>
                <div class="col-span-9">
                    <n-form-item label="标签组ID">
                        <div class="text-xl">
                            {{ tagGroup.id }}
                        </div>
                    </n-form-item>
                    <n-form-item label="标签组名称">
                        <n-input v-model:value="tagGroup.name" placeholder="输入标签名称"
                                 size="large"/>
                    </n-form-item>
                    <n-form-item label="标签组描述">
                        <n-input v-model:value="tagGroup.description"
                                 placeholder="输入标签组描述"
                                 size="large"/>
                    </n-form-item>
                    <n-form-item label="标签组创建时间">
                        <div class="text-xl">
                            {{ formatTimestamp(tagGroup.createTime) }}
                        </div>
                    </n-form-item>
                    <n-form-item label="标签组更新时间">
                        <div class="text-xl">
                            {{ formatTimestamp(tagGroup.updateTime) }}
                        </div>
                    </n-form-item>
                </div>
            </div>

            <div class="grid grid-cols-10 py-3">
                <div class="text-gray-600 mr-3 ">
                    导入信息
                </div>
                <div class="col-span-9">
                    <div class="pb-3">
                        <n-alert type="info">
                            <div class="text-xl font-semibold">
                                文件格式示例与说明
                            </div>
                            <div class="leading-loose mt-3">
                                <n-text tag="p">
                                    文件格式为keywords，使用一组方括号定义一个标签的开始。
                                </n-text>
                                <n-text tag="p">
                                    接下来每行定义关键词，关键词的格式为： 关键词=权重 。
                                    权重为整数，可省略，默认为1。
                                </n-text>
                                <n-text tag="p">
                                    关键词的权重越大，表示该关键词越重要。
                                </n-text>
                                <n-text class="my-3 " tag="p">
                                    示例：
                                </n-text>
                                <n-text class="mb-3" code tag="pre">
                                    [水果]<br>
                                    苹果=3<br>
                                    橘子=2<br>
                                    香蕉=1<br>
                                    葡萄=1<br>
                                    桃子<br>
                                    番茄=0 # 权值为0表示禁用该关键词<br>
                                </n-text>
                            </div>
                        </n-alert>
                    </div>
                    <div class="inline-block">
                        <n-button secondary type="primary" @click="showUploadKeywordsModal = true">
                            上传关键词文件
                        </n-button>
                    </div>
                    <div class="inline-block ml-4">
                        <n-text class="mb-3" tag="p">
                            从keywords文件创建并导入标签组。
                        </n-text>
                    </div>
                </div>
            </div>

            <div class="grid grid-cols-10 py-3">
                <div class="text-gray-600 mr-3 ">
                    下属标签信息
                </div>
                <div class="col-span-9">
                    <div class="pb-3">
                        <n-alert type="info">
                            <div>
                                点击修改将跳转到标签信息页面。
                            </div>
                        </n-alert>
                    </div>
                    <n-table striped>
                        <thead>
                        <tr>
                            <th>名称</th>
                            <th>关键词</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="tag in tagGroup.tags || []">
                            <td>
                                {{ tag.name }}
                            </td>
                            <td>
                                <div v-for="keyword in tag.keywords" class="mr-2 inline-block">
                                    <n-tag :bordered="false" type="primary">
                                        {{ keyword.name }}
                                    </n-tag>
                                </div>
                            </td>
                            <td>
                                <n-button-group>
                                    <n-button secondary type="primary" @click="toTagInfoPage(tag.id)">
                                        修改
                                    </n-button>
                                    <n-button secondary type="error">
                                        移除
                                    </n-button>
                                </n-button-group>
                            </td>
                        </tr>
                        <tr>
                            <td class="text-center" colspan="3">
                                <n-button>添加标签</n-button>
                            </td>
                        </tr>
                        </tbody>
                    </n-table>
                </div>
            </div>
            <div class="flex">
                <div class="flex-grow"></div>
                <div class="py-5">
                    <n-button-group>
                        <n-button type="primary">保存</n-button>
                    </n-button-group>
                </div>
            </div>
        </div>

        <n-modal :on-close="() => showUploadKeywordsModal = false"
                 :show="showUploadKeywordsModal"
                 :show-icon="false"
                 closable
                 preset="dialog"
                 title="导入标签库文件"
                 transform-origin="center">
            <div class="pb-3">
                <n-alert type="info">
                    文件格式需为keywords文件，其他格式将导致导入失败。
                </n-alert>
            </div>
            <n-upload
                    v-model:file-list="keywordFiles"
                    :default-upload="false"
                    :directory="false"
                    :directory-dnd="false"
                    :max="1"
                    :multiple="false"
                    name="file">
                <n-upload-dragger>
                    <n-text class="text-xl">
                        点击或者拖动文件到此区域来上传
                    </n-text>
                </n-upload-dragger>
            </n-upload>

            <div class="flex pt-5">
                <div class="flex-fill"></div>
                <n-button-group>
                    <n-button secondary @click="showUploadKeywordsModal = false">
                        取消
                    </n-button>
                    <n-button type="primary" @click="handleUploadKeywordsFile">
                        上传
                    </n-button>
                </n-button-group>
            </div>
        </n-modal>
    </div>
</template>

<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButtonGroup, NButton} from "naive-ui";
import {formatTimestamp} from "@/util/format";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";
import {adminTagGroups, adminTagInfo, adminUserLists} from "@/router";
import {adminMenuTag, adminMenuUser} from "@/views/menu";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const tagGroupId = ref(router.currentRoute.value.params.id)
const tagGroup = ref({})

const props = defineProps({
    tagGroup: {
        type: Object,
        default: {}
    },
    onClose: {
        type: Function,
        default: () => {
        }
    },
    onRefresh: {
        type: Function,
        default: () => {
        }
    }
})

const showUploadKeywordsModal = ref(false)
const keywordFiles = ref([])

const onClose = () => {

}

const toTagInfoPage = (id) => {
    router.push({
        name: adminTagInfo,
        params: {
            id: id
        },
        query: {
            refer: 'group',
            source: tagGroup.value.id
        }
    })
}

const requestGroupInfo = () => {
    const config = createConfig()
    proxy.$axios.get(api.tagGroups(true, tagGroupId.value), config)
            .then(res => {
                tagGroup.value = res.data
            })
            .catch(err => {
                popAdminErrorTemplate(notification, err, '获取标签组信息失败', '标签组请求错误')
            })
}

const onRefresh = () => {
    requestGroupInfo()
}

const back = () => {
    router.push({
        name: adminTagGroups
    })
}

const handleUploadKeywordsFile = () => {
    const config = createConfig()
    const formData = new FormData()
    const file = keywordFiles.value[0].file
    formData.append('file', file)
    showUploadKeywordsModal.value = false
    proxy.$axios.post(api.tagGroupsFile(tagGroup.value.id, true), formData, config)
            .then(res => {
                onRefresh()
                message.success('导入文件成功')
            })
            .catch(err => {
                popAdminErrorTemplate(notification, err, '导入文件失败', '标签组请求错误')
            })
}

onRefresh()

</script>

<style scoped>

</style>