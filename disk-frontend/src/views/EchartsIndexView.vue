<template>
    <div>
        <div class="p-5">
            <n-card closable>
                <div class="text-4xl pb-5">
                    <Mix></Mix>
                </div>
            </n-card>
            <n-card closable>
                <div class="pb-5">
                    <RosePie></RosePie>
                </div>
            </n-card>
            <n-card closable>
                <div class="text-4xl pb-5">
                    <Bar></Bar>
                </div>
            </n-card>
            <n-card closable>
                <div class="text-4xl pb-5">
                    <tree></tree>
                </div>
            </n-card>
            <n-card closable>
                <div class="text-4xl pb-5">
                    <PolarHistogram></PolarHistogram>
                </div>
            </n-card>
        </div>
    </div>


</template>

<script setup>
import Mix from "@/components/echarts/mix.vue";
import RosePie from "@/components/echarts/RosePie.vue";
import Bar from "@/components/echarts/Bar.vue";
import Tree from "@/components/echarts/tree.vue";
import PolarHistogram from "@/components/echarts/PolarHistogram.vue";

</script>

<style scoped>

</style>