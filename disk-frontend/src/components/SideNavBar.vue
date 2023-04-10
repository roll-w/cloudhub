<template>
  <!--  TODO: collapse sidebar -->
    <n-layout-sider :collapsed-width="0"
                    :native-scrollbar="false"
                    bordered
                    position="absolute"
                    width="var(--sidebar-width)">
        <div>
            <n-menu :options="option" :value="chooseOn"/>
        </div>
    </n-layout-sider>
</template>

<script setup>

import {RouterLink, useRouter} from "vue-router";
import {h, onBeforeMount, ref} from "vue";

import {useMessage, NIcon} from "naive-ui";
import {driveFilePage} from "@/router";
import FileIcon from "@/components/icon/FileIcon.vue";

const message = useMessage()

const router = useRouter()

onBeforeMount(() => {

})

let routerName = router.currentRoute.value.name
const chooseOn = ref()

const calcChooseOption = () => {
    return routerName
}

chooseOn.value = calcChooseOption()

router.afterEach(() => {
    routerName = router.currentRoute.value.name
    chooseOn.value = calcChooseOption()
})

const menuOptions = [
    {
        label: "文件",
        key: "menu-file",
        children: [
            {
                key: driveFilePage,
                label: () => h(
                    RouterLink,
                    {
                        to: {
                            name: driveFilePage,
                        }
                    },
                    {default: () => "个人主页"}
                ),

                icon() {
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