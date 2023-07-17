<template>
    <div :class="['fixed left-1/2 z-10 flex items-center -translate-x-1/2 ease-in-out transition-all duration-300 ',
         checkedList.length ? 'opacity-100 bottom-16' : 'opacity-0 -bottom-20']">
        <div :style="{
            boxShadow: boxShadow2
        }"
             class="py-2 bg-neutral-700 rounded-xl px-7">
            <n-space size="large">
                <n-button v-for="option in options"
                          :disabled="option.disabled"
                          @click="handleOptionSelect(option.key)"
                          :theme-overrides="{
                              colorQuaternaryHover: 'rgba(172, 172, 172, 0.20)',
                              colorQuaternaryPressed: 'rgba(172, 172, 172, 0.20)',
                          }"
                          circle
                          quaternary>
                    <n-tooltip placement="top" trigger="hover">
                        <template #trigger>
                            <div v-if="option.icon">
                                <n-icon :component="option.icon()" :size="24" color="#fff">
                                </n-icon>
                            </div>
                            <n-icon v-else :size="24" color="#fff">
                                {{ option.label }}
                            </n-icon>
                        </template>
                        <template #default>
                            {{ option.label }}
                        </template>
                    </n-tooltip>


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
    },
    options: {
        type: Array,
        default: []
    },
    onOptionSelect: {
        type: Function,
        default: (key) => {
        }
    }
})

const handleOptionSelect = (key) => {
    props.onOptionSelect(key)
}

</script>