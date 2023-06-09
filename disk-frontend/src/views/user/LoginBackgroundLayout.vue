<script setup>

import Footer from "@/components/Footer.vue";
import TopNavBar from "@/components/TopNavBar.vue";
</script>

<template>
    <n-layout position="absolute">
        <TopNavBar/>
        <n-layout
                :native-scrollbar="false" has-sider
                position="absolute"
                style="top: var(--header-height);">
            <n-layout
                    :native-scrollbar="false"
                    content-style="min-height: calc(100vh - var(--header-height)); display: flex; flex-direction: column;"
                    position="absolute">
                <div class="bg-img h-100vh w-100 bg-no-repeat bg-cover bg-center">
                    <div class="h-100 w-100 backdrop-blur-3xl">
                        <router-view v-slot="{ Component }">
                            <transition mode="out-in" name="slide-fade">
                                <component :is="Component" :key="$route.name"/>
                            </transition>
                        </router-view>
                    </div>
                </div>
                <n-back-top :right="100"/>
                <Footer/>
            </n-layout>
        </n-layout>
    </n-layout>
</template>

<style scoped>
.bg-img {
    background: radial-gradient(110vw 200vh ellipse at 50% 20%,
    #efc255 30%,
    #e5ecfc 35%,
    #c1dafa 60%,
    #559af5 75%,
    #ffffff 80%);
}

</style>