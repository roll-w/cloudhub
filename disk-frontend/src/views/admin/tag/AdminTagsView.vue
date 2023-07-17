<template>
    <div class="p-5">
        <AdminBreadcrumb :location="adminTags"
                         :menu="adminMenuTag"/>
        <n-h1>标签管理</n-h1>
        <div class="flex">
            <n-h2>
                标签列表
            </n-h2>
            <div class="flex flex-grow justify-end">
                <n-button @click="showCreateTagModal = true">
                    创建新标签
                </n-button>
            </div>
        </div>
        <div>
            <n-data-table :bordered="false"
                          :columns="columns"
                          :data="tags"
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
        <n-modal v-model:show="showCreateTagModal"
                 :show-icon="false"
                 closable
                 preset="dialog"
                 title="创建标签"
                 transform-origin="center">
            <TagCreateForm
                    :on-after-action="() => {
                        refresh()
                        showCreateTagModal = false
                    }"
                    :on-click-cancel="() => showCreateTagModal = false"
                    :on-click-confirm="() => showCreateTagModal = false"
            />
        </n-modal>
    </div>
</template>

<script setup>
import {getCurrentInstance, h, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButtonGroup, NButton} from "naive-ui";
import {adminMenuTag} from "@/views/menu";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {formatTimestamp} from "@/util/format";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";
import {adminTagInfo, adminTags} from "@/router";
import {usePage} from "@/views/util/pages";
import TagCreateForm from "@/components/admin/tags/TagCreateForm.vue";
import TagGroupCreateForm from "@/components/admin/tags/TagGroupCreateForm.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const tags = ref([])
const page = usePage()

const showCreateTagModal = ref(false)

const columns = [
    {
        title: "标签ID",
        key: "id"
    },
    {
        title: "名称",
        key: "name"
    },
    {
        title: "描述",
        key: "description",
        ellipsis: {
            tooltip: true
        }
    },
    {
        title: "创建时间",
        key: "createTime",
        render: (row) => {
            return formatTimestamp(row.createTime)
        }
    },
    {
        title: "更新时间",
        key: "updateTime",
        render: (row) => {
            return formatTimestamp(row.updateTime)
        }
    },
    {
        title: "操作",
        key: "action",
        render: (row) => {
            return h(NButtonGroup, null, {
                default: () => [
                    h(NButton, {
                        onClick: () => {
                            router.push({
                                name: adminTagInfo,
                                params: {
                                    id: row.id
                                }
                            })
                        }
                    }, {
                        default: () => "详情"
                    }),
                ]
            })
        }
    }
]

const requestTags = () => {
    const config = createConfig()
    config.params = {
        page: page.value.page,
        size: 10
    }
    proxy.$axios.get(api.tags(undefined, true), config).then((res) => {
        page.value.count = Math.ceil(res.total / res.size)
        page.value.page = res.page

        tags.value = res.data
    }).catch((err) => {
        popAdminErrorTemplate(notification, err, "获取标签列表失败")
    })

}

const refresh = () => {
    requestTags()
}

refresh()

const switchPage = () => {
    router.push({
        name: adminTags,
        query: {
            page: page.value.page
        }
    })
}

</script>

<style scoped>

</style>