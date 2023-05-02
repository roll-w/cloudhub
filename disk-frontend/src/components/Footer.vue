<template>
    <n-layout-footer bordered>
        <div class="p-10 leading-loose">
            <n-text class="text-sm">
                <p>©2023 Cloudhub. All rights reserved. 版权所有</p>
                <a v-if="icp" href="https://beian.miit.gov.cn/" target="_blank">
                    {{ icp }}
                </a>
            </n-text>
        </div>
    </n-layout-footer>
</template>

<script setup>

const icp = ((window.cloudhub || {}).server || {}).icp

</script>

<style scoped>

</style>