<template>
    <main>
        <NavBar/>
        <div class="d-flex flex-column p-4 flex-grow-1 allow-rolling">
            <router-view v-slot="{ Component }">
                <component :is="Component" :key="$route.fullPath"/>
            </router-view>
            <FooterView/>
        </div>
    </main>
</template>

<script setup>
import NavBar from "@/components/common/NavBar";
import FooterView from "@/components/common/Footer";

</script>

<style scoped>
body {
    min-height: 100vh;
}

th, td {
    text-align: center;
}

.div-right-float {
    float: right;
}

main {
    display: flex;
    flex-wrap: nowrap;
    width: auto;
    height: 100vh;
    max-height: 100vh;
    overflow-x: auto;
    overflow-y: hidden
}

.allow-rolling {
    overflow: auto;
}
</style>

