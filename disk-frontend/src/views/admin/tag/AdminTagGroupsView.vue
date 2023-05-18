<template>
  <div class="p-5">
    <AdminBreadcrumb :location="adminTagGroups"
                     :menu="adminMenuTag"/>
    <n-h2>标签组管理</n-h2>
    <div class="mb-5">
      <n-h3>
        导入标签组文件
      </n-h3>
      <div>
        <n-text class="mb-3" tag="p">
          从keywords文件创建并导入标签组。
        </n-text>

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
    </div>

    <div class="my-3">
      <div class="flex">
        <n-h3>
          标签组列表
        </n-h3>
        <div class="flex flex-grow justify-end">
          <n-button>创建新标签组</n-button>
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
              :on-update-page="refresh"
              :page-count="page.count"
              show-quick-jumper
          />
        </div>
      </div>
    </div>

    <n-modal
        v-model:show="showTagGroupInfoModal"
        :show-icon="false"
        :title="tagGroupInfo.name"
        preset="card"
        transform-origin="center">
      <AdminTagGroupInfo
          :tag-group="tagGroupInfo"/>
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
import {adminTagGroups} from "@/router";
import {adminMenuTag} from "@/views/menu";
import AdminTagGroupInfo from "@/views/admin/tag/AdminTagGroupInfo.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const tagGroups = ref([])
const tagGroupInfo = ref({})
const showTagGroupInfoModal = ref(false)

const page = ref({
  page: 1,
  count: 1
})

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
                  size: 'small',
                  onClick: () => {
                  }
                },
                () => '查看/编辑'
            ),
            h(NButton,
                {
                  size: 'small',
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
}

refresh()

</script>

<style scoped>
</style>