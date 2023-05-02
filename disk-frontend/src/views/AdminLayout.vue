<template>
    <n-layout position="absolute">
        <TopNavBar/>
        <n-layout
                :native-scrollbar="false" has-sider
                position="absolute"
                style="top: var(--header-height);">
            <!--sidebar-->
            <SideNavBar :option-key="keyAdmin"/>
            <n-layout
                    :native-scrollbar="false"
                    content-style="min-height: calc(100vh - var(--header-height)); display: flex; flex-direction: column;"
                    position="absolute"
                    style="left: var(--sidebar-width);">
                <router-view v-slot="{ Component }">
                    <transition mode="out-in" name="fade">
                        <component :is="Component" :key="$route.fullPath"/>
                    </transition>
                </router-view>
                <n-back-top :right="100"/>
                <Footer/>
            </n-layout>
        </n-layout>
    </n-layout>
</template>

<script setup>
import SideNavBar from "@/components/SideNavBar.vue";
import Footer from "@/components/Footer.vue";
import TopNavBar from "@/components/TopNavBar.vue";
import {keyAdmin} from "@/views/menu";
</script>

<style scoped>

</style>