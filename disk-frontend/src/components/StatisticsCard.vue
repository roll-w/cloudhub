<script setup>
const props = defineProps({
    option: {
        type: Object,
        required: true
    }
})

const colors = [
    {
        breakpoint: 90,
        color: '#F44336'
    },
    {
        breakpoint: 65,
        color: '#FF9800'
    },
    {
        breakpoint: 0,
        color: '#4CAF50'
    },
    {
        breakpoint: 200,
        color: '#d7d7d7'
    }
]


const getColor = (percentage) => {
    for (const color of colors) {
        if (percentage >= color.breakpoint) {
            return color.color
        }
    }
    return colors[colors.length - 1].color
}

</script>

<template>
    <div>
        <n-card :bordered="false" embedded>
            <div class="text-base">
                {{ option.name }}
            </div>
            <div class="flex pt-5 items-baseline">
                <div class="text-3xl text-amber-500">
                    {{ option.value }}
                </div>
                <div v-if="option.restrict"
                     class="text-base flex flex-grow justify-end">
                    <div> / {{ option.restrict }}</div>
                </div>
            </div>
        </n-card>
        <div class="rounded-2xl">
            <n-progress :border-radius="30"
                        :color="getColor(option.percentage || 0)"
                        :height="2"
                        :percentage="option.percentage || 0"
                        :show-indicator="false"
                        :stroke-width="1"
                        type="line"/>
        </div>
    </div>
</template>

<style scoped>

</style>