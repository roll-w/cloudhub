<template>
  <div class="p-5 ">
    <AdminBreadcrumb :location="adminSystemLogs" :menu="adminMenuSystem"/>
    <n-h1>系统日志</n-h1>
    <n-text class="mt-5">
      系统运行错误日志，包含最近500条。
    </n-text>
    <n-data-table
        :bordered="false"
        :columns="columns"
        :data="data"
        :pagination="{pageSize: 20}"
        class="mt-5"
    />

    <n-modal v-model:show="showModal" preset="card" size="huge"
             title="堆栈信息" type="error">
      <n-space vertical>
        <n-space>
          <n-text>错误码</n-text>
          <n-text>{{ stacktraceInfo.code }}</n-text>
        </n-space>
        <n-space>
          <n-text>错误信息</n-text>
          <n-text>{{ stacktraceInfo.message }}</n-text>
        </n-space>
        <n-space>
          <n-text>错误类</n-text>
          <n-text>{{ stacktraceInfo.className }}</n-text>
        </n-space>
        <n-space>
          <n-text>时间</n-text>
          <n-text>{{ stacktraceInfo.time }}</n-text>
        </n-space>
        <n-space vertical>
          <n-text>堆栈信息</n-text>
          <n-card embedded>
            <n-code :code="stacktraceInfo.stacktrace"
                    language="java"
                    show-line-numbers/>
          </n-card>
        </n-space>
      </n-space>
    </n-modal>
  </div>
</template>

<script setup>
import {NButton, useNotification} from "naive-ui";
import {useUserStore} from "@/stores/user";
import api from "@/request/api"
import {ref, h, getCurrentInstance} from "vue";
import {formatTimestamp} from "@/util/time";
import AdminBreadcrumb from "@/components/admin/AdminBreadcrumb.vue";
import {createConfig} from "@/request/axios_config";
import {adminSystemLogs} from "@/router";
import {adminMenuSystem} from "@/views/menu";

const {proxy} = getCurrentInstance()

const notification = useNotification()
const userStorage = useUserStore()

const showModal = ref(false)
const stacktraceInfo = ref({})


const columns = [
  {
    title: "错误码",
    key: "code"
  },
  {
    title: "错误信息",
    key: "message",
    ellipsis: true
  },
  {
    title: "错误类",
    key: "className"
  },
  {
    title: "时间",
    key: "time"
  },
  {
    title: "操作",
    key: "actions",
    render(row) {
      return h(
          NButton,
          {
            size: 'small',
            onClick: () => {
              showModal.value = true
              stacktraceInfo.value = row
            }
          },
          {default: () => "查看堆栈"}
      )
    }
  }
]

const data = ref([])

const getLogs = () => {
  const config = createConfig()
  proxy.$axios.get(api.getErrorLogs, config).then((res) => {
    data.value = []
    res.data.forEach((item) => {
      data.value.push({
        code: item.errorCode,
        message: item.message,
        className: item.exceptionName,
        time: formatTimestamp(item.timestamp),
        stacktrace: item.stacktrace
      })
    })
    console.log(res);
  }).catch((err) => {
    console.log(err);
    const msg = err.tip +
        "\n信息： " + err.message
    notification.error({
      title: "请求错误",
      content: msg,
      meta: "日志请求错误",
      duration: 3000,
      keepAliveOnHover: true
    })
  })
}

getLogs()



</script>

<style scoped>

</style>