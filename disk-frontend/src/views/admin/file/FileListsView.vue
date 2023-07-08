<template>
    <div class="p-5 ">
        <AdminBreadcrumb :location="adminFileLists" :menu="adminMenuFile"/>
        <n-h1>文件列表</n-h1>
            <n-data-table
                    :bordered="false"
                    :columns="columns"
                    :data="data"
                    class="mt-5"
            />

        <FastPagination :page="page" :route-name="adminFileLists"/>
    </div>
</template>

<script setup>
import {adminFileLists} from "@/router";
import {adminMenuFile} from "@/views/menu";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {getCurrentInstance, h, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog, NButtonGroup, NButton} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popAdminErrorTemplate} from "@/views/util/error";
import {usePage} from "@/views/util/pages";
import FastPagination from "@/components/FastPagination.vue";
import {formatTimestamp} from "@/util/format";
import {getFileType} from "@/views/names";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const page = usePage()

const columns = [
    {
        title: "ID",
        key: "storageId",
        width: 50
    },
    {
        title: "从属用户",
        key: "ownerId",
        width: 90
    },
    {
        title: "文件名",
        key: "name",

        ellipsis: {
            tooltip: true
        },
    },
    {
        title: "文件夹",
        key: "parentId",
        width: 80,
    },
    {
        title: "文件类型",
        key: "fileType",
        width: 80,
        render: (row) => {
            return getFileType(row.fileType)
        }
    },
    {
        title: "创建时间",
        key: "createTime",
        ellipsis: {
            tooltip: true
        },
        render: (row) => {
            return formatTimestamp(row.createTime)
        }
    },
    {
        title: "最后修改时间",
        key: "updateTime",
        ellipsis: {
            tooltip: true
        },
        render: (row) => {
            return formatTimestamp(row.updateTime)
        }
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
                                    }
                                },
                                {default: () => "查看/编辑"}),
                        h(NButton,
                                {
                                    onClick: () => {
                                    }
                                },
                                {default: () => "删除"}),
                    ]
            );
        }
    }
]

const data = ref([])

const getFileLists = () => {
    const config = createConfig()
    config.params = {
        page: page.value.page,
    }

    proxy.$axios.get(api.files(true), config).then((res) => {
        page.value.count = Math.ceil(res.total / res.size)
        page.value.page = res.page
        data.value = res.data
    }).catch((error) => {
        popAdminErrorTemplate(notification, error)
    })
}

getFileLists()

</script>

<style scoped>

</style>