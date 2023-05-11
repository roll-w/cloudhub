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

import {useRouter} from "vue-router";
import {onBeforeMount, ref} from "vue";

import {useMessage} from "naive-ui";
import {convertsToNMenuOptions, findMenuOptionByKey, keyUser} from "@/views/menu";

const props = defineProps({
    optionKey: {
        type: String,
        default: keyUser
    }
})

const message = useMessage()

const router = useRouter()

onBeforeMount(() => {

})

let routerName = router.currentRoute.value.name
const chooseOn = ref()

const calcChooseOption = () => {
    const menus = findMenuOptionByKey(props.optionKey).menus
    const menu = findByKeyOrAlias(routerName, menus)
    if (menu) {
        return menu.key
    }
    return routerName
}

const findByKeyOrAlias = (name, menus) => {
    if (!menus) {
        return null
    }
    for (const menu of menus) {
        if (menu.key === name || (menu.alias || []).includes(name)) {
            return menu
        }
        if (!menu.children) {
            continue
        }
        const child = findByKeyOrAlias(name, menu.children)
        if (child) {
            return child
        }
    }
}


chooseOn.value = calcChooseOption()

router.afterEach(() => {
    routerName = router.currentRoute.value.name
    chooseOn.value = calcChooseOption()
})

const option = convertsToNMenuOptions(findMenuOptionByKey(props.optionKey).menus);
</script>

<style scoped>

</style>