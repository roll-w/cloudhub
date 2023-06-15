<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminTagGroups"
                         :menu="adminMenuTag"/>
        <n-h1>标签组管理</n-h1>

        <div class="my-3">
            <div class="flex">
                <n-h2>
                    标签组列表
                </n-h2>
                <div class="flex flex-grow justify-end">
                    <n-button @click="showCreateTagGroupModal = true">
                        创建新标签组
                    </n-button>
                </div>
            </div>


            <div>
                <n-data-table :bordered="false"
                              :columns="columns"
                              :data="tagGroups"
                              class="mt-5"
                />
            </div>
            <div class="flex items-start justify-start mt-5">
                <div>
                    <n-pagination
                            v-model:page="page.page"
                            :on-update-page="switchPage"
                            :page-count="page.count"
                            show-quick-jumper
                    />
                </div>
            </div>
        </div>

        <n-modal v-model:show="showCreateTagGroupModal"
                 :show-icon="false"
                 closable
                 preset="dialog"
                 title="创建标签组"
                 transform-origin="center">
            <TagGroupCreateForm
                    :on-after-action="refresh"
                    :on-click-cancel="() => showCreateTagGroupModal = false"
                    :on-click-confirm="() => showCreateTagGroupModal = false"
            />
        </n-modal>
    </div>
</template>

<script setup>
import {getCurrentInstance, h, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButtonGroup, NButton} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";
import {formatTimestamp} from "@/util/format";
import {getKeywordSearchScopeName} from "@/views/names";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {adminTagGroupInfo, adminTagGroups} from "@/router";
import {adminMenuTag} from "@/views/menu";
import AdminTagGroupInfo from "@/views/admin/tag/AdminTagGroupInfoView.vue";
import TagGroupCreateForm from "@/components/admin/tags/TagGroupCreateForm.vue";
import {getPage} from "@/views/util/pages";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const tagGroups = ref([])
const tagGroupInfo = ref({})
const showCreateTagGroupModal = ref(false)

const page = ref(getPage())


const columns = [
    {
        title: "标签组ID",
        key: "id"
    },
    {
        title: "标签组名称",
        key: "name"
    },
    {
        title: "标签组描述",
        key: "description",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "创建时间",
        key: "createTime",
    },
    {
        title: "更新时间",
        key: "updateTime",
    },
    {
        title: "搜索范围",
        key: "keywordSearchScope",
    },
    {
        title: "操作",
        key: "actions",
        render(row) {
            return h(
                    NButtonGroup,
                    {},
                    () => [
                        h(NButton,
                                {
                                    onClick: () => {
                                        tagGroupInfo.value = row
                                        router.push({
                                            name: adminTagGroupInfo,
                                            params: {
                                                id: row.id
                                            }
                                        })
                                    }
                                },
                                () => '查看/编辑'
                        ),
                        h(NButton,
                                {
                                    onClick: () => {
                                    }
                                },
                                () => '删除'
                        )
                    ]
            )
        }
    }
]

const requestTagGroups = () => {
    const config = createConfig()
    config.params = {
        page: page.value.page,
        size: 10
    }
    proxy.$axios.get(api.tagGroups(true), config).then(res => {
        page.value.count = Math.ceil(res.total / res.size)
        page.value.page = res.page

        tagGroups.value = res.data
        tagGroups.value.forEach(tagGroup => {
            tagGroup.createTime = formatTimestamp(tagGroup.createTime)
            tagGroup.updateTime = formatTimestamp(tagGroup.updateTime)
            tagGroup.keywordSearchScope = getKeywordSearchScopeName(tagGroup.keywordSearchScope)
        })
    }).catch(error => {
        popAdminErrorTemplate(notification, error, "获取标签组失败")
    })
}

const refresh = () => {
    requestTagGroups()
    if (tagGroupInfo.value.id) {
        tagGroupInfo.value = tagGroups.value.find(tagGroup =>
                tagGroup.id === tagGroupInfo.value.id)
    }
}

const switchPage = () => {
    router.push({
        name: adminTagGroups,
        query: {
            page: page.value.page
        }
    })
}

refresh()

</script>

<style scoped>
</style>