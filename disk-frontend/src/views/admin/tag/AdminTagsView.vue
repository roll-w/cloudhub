<template>
  <div class="p-5">
    <AdminBreadcrumb :location="adminTags"
                     :menu="adminMenuTag"/>
    <n-h2>标签管理</n-h2>
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
            :on-update-page="refresh"
            :page-count="page.count"
            show-quick-jumper
        />
      </div>
    </div>

    <n-modal
        v-model:show="showTagInfoModal"
        :show-icon="false"
        :title="tagInfo.name"
        preset="card"
        transform-origin="center">
      <TagInfoView
          :tag="tagInfo"/>
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
import {adminTags} from "@/router";
import TagInfoView from "@/views/admin/tag/AdminTagInfoView.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const showTagInfoModal = ref(false)
const tagInfo = ref({
  id: 0,
  name: "",
  description: "",
  createTime: 0,
  updateTime: 0
})

const tags = ref([])
const page = ref({
  page: 1,
  count: 1
})

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
              tagInfo.value = row
              showTagInfoModal.value = true
            }
          }, {
            default: () => "详情"
          }),
          h(NButton, {
            onClick: () => {
            }
          }, {
            default: () => "删除"
          })
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
  proxy.$axios.get(api.tags(true), config).then((res) => {
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

</script>

<style scoped>

</style>