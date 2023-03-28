<!--
  - Copyright (C) 2023 RollW
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -        http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
<!--  <n-watermark-->
<!--      :content="username  + ' ID:' + userId + ' ' + role"-->
<!--      :font-size="16"-->
<!--      :height="384"-->
<!--      :line-height="16"-->
<!--      :rotate="-15"-->
<!--      :width="384"-->
<!--      :x-offset="12"-->
<!--      :y-offset="60"-->
<!--      cross-->
<!--      fullscreen-->
<!--  />-->
  <!--  TODO: collapse sidebar -->
  <n-layout-sider :collapsed-width="0"
                  :native-scrollbar="false"
                  bordered
                  position="absolute"
                  width="var(--sidebar-width)">
    <div>
      <n-menu :options="menuOptions"/>
    </div>
  </n-layout-sider>
</template>

<script setup>

import {RouterLink, useRouter} from "vue-router";
import {onBeforeMount, ref} from "vue";

import {useMessage} from "naive-ui";

const message = useMessage()

const router = useRouter()

onBeforeMount(() => {

})

let routerName = router.currentRoute.value.name
const chooseOn = ref()
const calcChooseOption = () => {
  //return routerName
  return "1"
}

chooseOn.value = calcChooseOption()

router.afterEach(() => {
  routerName = router.currentRoute.value.name
  chooseOn.value = calcChooseOption()
})

const menuOptions = [
  {
    label: "文件",
    key: "1",
  },
  {
    label: "收藏夹",
    key: "2",
  },
  {
    label: "订阅",
    key: "3",
  },
  {
    label: "回收站",
    key: "4",
  }
];

const option = menuOptions;



</script>

<style scoped>

</style>