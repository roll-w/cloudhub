<script setup>
const props = defineProps({
    status: {
        type: String,
        default: "success"
    },
    title: {
        type: String,
        default: ""
    },
    messages: {
        type: Array,
        default: () => []
    }
})

const getIndicatorClass = () => {
    if (props.status === "error") {
        return "bg-red-600"
    }
    if (props.status === "warning") {
        return "bg-amber-400"
    }
    return "bg-green-600"
}

</script>

<template>
    <div class="py-2">
        <div class="flex items-center">
            <div :class="['w-4 h-4 rounded-full animate-ping opacity-75 ', getIndicatorClass()]"></div>
            <div :class="['w-4 h-4 rounded-full absolute', getIndicatorClass()]"></div>
            <div class="ml-3 text-gray-500">
                    {{ title }}
            </div>
        </div>
        <div v-if="messages && messages.length" class="pt-1 ml-7 ">
            <div v-for="message in messages">
                - {{ message }}
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>