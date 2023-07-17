<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminTags"
                         :menu="adminMenuTag"/>
        <div class="flex items-baseline mt-5">
            <n-h1>
                <span class="text-amber-400">
                    {{ tag.name }}
                </span>
                标签信息
            </n-h1>
            <div class="flex flex-grow justify-end">
                <n-button @click="back">返回</n-button>
            </div>
        </div>

        <div class="grid grid-cols-10 py-3">
            <div class="text-gray-600 mr-3 ">
                标签信息
            </div>
            <div class="col-span-9">
                <n-form ref="form" :model="formValue" :rules="formRules">
                    <n-form-item label="标签ID">
                        <div class="text-xl">
                            {{ tag.id }}
                        </div>
                    </n-form-item>
                    <n-form-item label="标签名称" path="name">
                        <n-input v-model:value="formValue.name"
                                 placeholder="输入标签名称"
                                 size="large"/>
                    </n-form-item>
                    <n-form-item label="标签描述" path="description">
                        <n-input v-model:value="formValue.description"
                                 placeholder="输入标签描述"
                                 size="large"/>
                    </n-form-item>
                    <n-form-item label="标签创建时间">
                        <div class="text-xl">
                            {{ formatTimestamp(tag.createTime) }}
                        </div>
                    </n-form-item>
                    <n-form-item label="标签更新时间">
                        <div class="text-xl">
                            {{ formatTimestamp(tag.updateTime) }}
                        </div>
                    </n-form-item>
                </n-form>
            </div>
        </div>
        <div class="flex">
            <div class="flex-grow"></div>
            <div class="py-5">
                <n-button-group>
                    <n-button type="primary" @click="handleSaveInfo">保存</n-button>
                </n-button-group>
            </div>
        </div>
        <div class="grid grid-cols-10 py-3">
            <div class="text-gray-600 mr-3">
                标签关键词
            </div>
            <div class="col-span-9">
                <n-table striped>
                    <thead>
                    <tr>
                        <th>名称</th>
                        <th>权重</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="keyword in tag.keywords">
                        <td>
                            <n-tag :bordered="false" size="large" type="primary">
                                {{ keyword.name }}
                            </n-tag>

                        </td>
                        <td>{{ keyword.weight }}</td>
                        <td>
                            <n-button-group>
                                <n-button secondary type="primary" @click="handleEditKeyword(keyword)">
                                    修改
                                </n-button>
                                <n-button secondary type="error" @click="handleDeleteKeyword(keyword)">
                                    删除
                                </n-button>
                            </n-button-group>
                        </td>
                    </tr>
                    <tr>
                        <td class="text-center" colspan="3">
                            <n-button @click="handleAddKeyword">添加关键词</n-button>
                        </td>
                    </tr>
                    </tbody>
                </n-table>
            </div>
        </div>

        <n-modal v-model:show="showEditKeywordModal"
                 :show-icon="false"
                 closable
                 preset="dialog"
                 title="关键词"
                 transform-origin="center">
            <TagKeywordForm
                    :name="(editKeyword || {}).name"
                    :on-after-action="() => {
                        showEditKeywordModal = false
                        refresh()
                    }"
                    :on-click-cancel="() => showEditKeywordModal = false"
                    :tag-id="tagId"
                    :type="keywordModalType"
                    :weight="(editKeyword || {}).weight"
            />
        </n-modal>
    </div>
</template>

<script setup>
import {formatTimestamp} from "@/util/format";
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButton} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";
import {adminTagGroupInfo, adminTagGroups, adminTags} from "@/router";
import {adminMenuTag} from "@/views/menu";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import TagKeywordForm from "@/views/admin/tag/TagKeywordForm.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const tagId = ref(router.currentRoute.value.params.id)
const tag = ref({})
const formValue = ref({})
const form = ref()
const showEditKeywordModal = ref(false)

const keywordModalType = ref('edit')

const editKeyword = ref()

const formRules = {
    name: [
        {
            required: true,
            message: '请输入标签名称',
            trigger: ['blur', 'input']
        },
        {
            min: 1,
            max: 32,
            message: '长度在 1 到 32 个字符',
            trigger: ['blur', 'input']
        }
    ],
    description: [
        {
            max: 128,
            message: '长度在120字符以下',
            trigger: ['blur', 'input']
        }
    ]
}

const requestTagInfo = () => {
    const config = createConfig()
    proxy.$axios.get(api.tags(tagId.value, true), config).then(resp => {
        tag.value = resp.data
        formValue.value = {
            name: tag.value.name,
            description: tag.value.description
        }
    }).catch(error => {
        popAdminErrorTemplate(notification, error,
                "获取标签信息失败", "标签请求失败")
    })
}

const backIfGroup = (refer, source) => {
    if (source) {
        router.push({
            name: adminTagGroupInfo,
            params: {
                id: source
            }
        })
        return
    }
    router.push({
        name: adminTagGroups
    })
}

const back = () => {
    const refer = router.currentRoute.value.query.refer
    const source = router.currentRoute.value.query.source

    if (refer === 'group') {
        backIfGroup(refer, source)
        return
    }

    router.push({
        name: adminTags
    })
}

const refresh = () => {
    requestTagInfo()
}

refresh()

const handleEditKeyword = (keyword) => {
    editKeyword.value = keyword
    showEditKeywordModal.value = true
}

const handleAddKeyword = () => {
    editKeyword.value = {}
    keywordModalType.value = 'add'
    showEditKeywordModal.value = true
}

const handleDeleteKeyword = (keyword) => {
    dialog.error({
        title: "删除关键词",
        content: `确定删除关键词 ${keyword.name} 吗？`,
        positiveText: "删除",
        negativeText: "取消",
        onPositiveClick: () => {
            requestDeleteKeyword(keyword)
        }
    })
}

const requestUpdateTag = () => {
    const config = createConfig()
    proxy.$axios.put(api.tags(tagId.value, true), {
        name: formValue.value.name,
        description: formValue.value.description
    }, config).then(resp => {
        message.success("修改标签成功")
        refresh()
    }).catch(error => {
        popAdminErrorTemplate(notification, error,
                "修改标签失败", "标签请求失败")
    })
}

const requestDeleteKeyword = (keyword) => {
    const config = createConfig()
    config.params = {
        name: keyword.name
    }

    proxy.$axios.delete(api.tagsKeywords(tagId.value), config).then(resp => {
        message.success("删除关键词成功")
        refresh()
    }).catch(error => {
        popAdminErrorTemplate(notification, error,
                "删除关键词失败", "标签请求失败")
    })
}

const handleSaveInfo = () => {
    form.value.validate().then(() => {
        requestUpdateTag()
    }).catch(() => {
    })
}

</script>

<style scoped>

</style>