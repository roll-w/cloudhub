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
import {h, onBeforeMount, ref} from "vue";

import {useMessage, NIcon} from "naive-ui";
import {driveFilePage, index} from "@/router";
import FileIcon from "@/components/icon/FileIcon.vue";

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
        label: "首页",
        key: index,
    },
  {
    label: "文件",
    key: "menu-file",
    children: [
      {
        label: "全部文件",
        key: driveFilePage,
        icon () {
          return h(NIcon, {
            class: "text-2xl"
          }, {
            default: () => h(FileIcon)
          })
        },
      }
    ]
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