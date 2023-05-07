<template>
    <n-layout-footer bordered>
        <div class="p-10 leading-loose">
            <div class="text-sm leading-loose">
                <p>©2023 Cloudhub. All rights reserved. 版权所有</p>
                <a v-if="icp" href="https://beian.miit.gov.cn/" target="_blank">
                    {{ icp }}
                </a>
                <br>
                <a v-if="beian" href="https://www.beian.gov.cn/" target="_blank">
                    {{ beian }}
                </a>
            </div>
        </div>
    </n-layout-footer>
</template>

<script setup>

const icp = ((window.cloudhub || {}).server || {}).icp
const beian = ((window.cloudhub || {}).server || {}).beian

</script>

<style scoped>

</style>