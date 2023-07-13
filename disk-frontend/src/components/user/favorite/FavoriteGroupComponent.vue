<script setup>
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import {useSiteStore} from "@/stores/site";
import {computed} from "vue";

const props = defineProps({
    name: {
        type: String,
        required: true
    },
    active: {
        type: Boolean,
        default: false
    }
})

const siteStore = useSiteStore()

const classes = computed(() => {
    if (props.active) {
        return "bg-amber-500 bg-opacity-[15%] "
    }
    if (siteStore.isDark) {
        return "bg-opacity-20 bg-neutral-700 hover:bg-neutral-300 hover:bg-opacity-20"
    }
    return "bg-opacity-25 bg-neutral-200 hover:bg-neutral-300 hover:bg-opacity-30"
})

</script>

<template>
    <div class="flex flex-row p-1.5 py-2 cursor-pointer transition-all ">
        <div :class="[
                'flex flex-col h-32 w-48 ease-in-out duration-300 rounded-xl px-2 ',
                 classes]">
            <div class="p-5">
                <n-icon :class="[active ? 'text-amber-500 ' : '']" size="40">
                    <slot name="icon">
                        <Folder24Regular/>
                    </slot>
                </n-icon>
                <div :class="['mt-2 ', active ? 'text-amber-500 ' : '']">
                    {{ name }}
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>