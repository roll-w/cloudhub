<template>
  <div class="p-5">
    <n-result
              status="404"
              title="404 请求的资源不存在。"
    >
      <template #default>
        <div class="text-center text-xl">
          <div>{{ result }}</div>
          <div>{{ tip }}</div>
          <div>{{ message }}</div>
        </div>
      </template>
      <template #footer>
        <n-button type="primary" @click="() => $router.push('/')">回到首页</n-button>
      </template>
    </n-result>
  </div>
</template>

<script setup>

import {useRouter} from "vue-router";

const router = useRouter()
const result = router.currentRoute.value.query.result
const tip = router.currentRoute.value.query.tip
const message = router.currentRoute.value.query.message
const source = router.currentRoute.value.query.source

const desc = `${result}<br/>${tip}<br/>${message}`

</script>

<style scoped>

</style>