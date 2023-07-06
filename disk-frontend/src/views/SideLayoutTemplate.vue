<script setup>
import {keyUser} from "@/views/menu";
import SideNavBar from "@/components/SideNavBar.vue";
import TopNavBar from "@/components/TopNavBar.vue";

const props = defineProps({
    optionKey: {
        type: String,
        default: keyUser
    }
})

</script>

<template>
    <n-layout position="absolute">
        <TopNavBar/>
        <n-layout
                :native-scrollbar="false" has-sider
                position="absolute"
                style="top: var(--header-height);">
            <!--sidebar-->
            <SideNavBar :option-key="optionKey"/>
            <n-layout
                    :native-scrollbar="false"
                    content-style="min-height: calc(100vh - var(--header-height)); display: flex; flex-direction: column;"
                    position="absolute"
                    style="left: var(--sidebar-width);">
                <slot name="header"></slot>
                <slot name="router"></slot>
                <router-view v-if="!$slots.router" v-slot="{ Component }">
                    <transition mode="out-in" name="slide-fade">
                        <component :is="Component" :key="$route.fullPath"/>
                    </transition>
                </router-view>
                <n-back-top :right="100"/>
                <Footer/>
            </n-layout>
        </n-layout>
    </n-layout>
</template>

<style scoped>

</style>