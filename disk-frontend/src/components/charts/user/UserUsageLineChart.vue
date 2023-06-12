<script setup>
import {ref} from "vue";

const props = defineProps({
    percentage: {
        type: Number,
        default: 0
    },
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
    <div class="w-full h-full">
        <n-progress :border-radius="15"
                    :height="6"
                    :color="getColor(percentage)"
                    rail-color="#dedfe1"
                    :percentage="percentage"
                    :show-indicator="false"
                    :stroke-width="1"
                    type="line"/>
    </div>
</template>

<style scoped>

</style>