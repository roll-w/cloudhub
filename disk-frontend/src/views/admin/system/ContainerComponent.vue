<script setup>
const props = defineProps({
    group: {
        type: Object,
        required: true
    }
})

const getSortedContainers = () => {
    return props.group.containers.sort((a, b) => {
        return a.serial - b.serial
    })
}

</script>

<template>
    <div class="py-2">
        <n-card>
            <div class="flex align-bottom py-2">
                <div class="text-xl">{{ group.containerId }}</div>
                <div class="pl-5">
                    <n-tag :bordered="false"
                           size="medium"
                           type="primary">
                        {{ group.source }}
                    </n-tag>
                </div>
            </div>
            <n-table class="my-3">
                <thead>
                <tr>
                    <th>序列号</th>
                    <th>单块大小</th>
                    <th>容量</th>
                    <th>已用块/总块</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="container in getSortedContainers()">
                    <td>{{ container.serial }}</td>
                    <td>{{ container.blockSize }} KB</td>
                    <td>{{ container.limitMbs }} MB</td>
                    <td>{{ container.usedBlocks }} / {{ container.limitBlocks }}</td>
                </tr>
                </tbody>
            </n-table>
        </n-card>
    </div>

</template>

<style scoped>

</style>