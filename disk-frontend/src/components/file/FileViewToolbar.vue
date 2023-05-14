<template>
    <div :class="['left-1/2 absolute z-10 flex items-center -translate-x-1/2 ease-in-out transition-all duration-300 ',
         checkedList.length ? 'opacity-100 bottom-16' : 'opacity-0 -bottom-20']">
        <div :style="{
            boxShadow: boxShadow2
        }"
             class="py-4 bg-neutral-500 rounded-xl px-7">
            <n-space size="large">
                <n-button
                        :disabled="!checkedList.length"
                        circle
                        quaternary
                        tertiary>
                    <n-icon :size="28" color="#fff">
                        A
                    </n-icon>
                </n-button>
                <n-button
                        :disabled="!checkedList.length"
                        circle
                        quaternary
                        tertiary>
                    <n-icon :size="28" color="#fff">
                        B
                    </n-icon>
                </n-button>
            </n-space>
        </div>
    </div>
</template>

<script setup>
import {useThemeVars} from "naive-ui";

const {boxShadow2} = useThemeVars().value


const props = defineProps({
    checkedList: {
        type: Array,
        required: true
    }
})

</script>