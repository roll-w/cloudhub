<template>
    <n-grid cols="3" x-gap="10" y-gap="10">
        <n-gi v-for="option in entries">
            <n-card :bordered="false" embedded>
                <div class="flex py-3 text-2xl items-center">
                    <div class="text-amber-500">
                        {{ option.name }}
                    </div>
                    <div class="flex flex-fill items-center h-12 justify-end justify-items-stretch">
                        <router-link v-if="option.route" :to="{name: option.route}"
                                     class="entry-card">
                            &gt;
                        </router-link>
                        <div v-else class="entry-card">
                            &gt;
                        </div>
                    </div>
                </div>
            </n-card>
        </n-gi>
    </n-grid>
</template>

<script setup>

const props = defineProps({
    entries: {
        type: Array,
        required: true
    }
})

</script>

<style scoped>
.entry-card {
    @apply select-none text-xl transition-all duration-150
    font-semibold font-sans
    cursor-pointer
    hover:text-4xl hover:text-amber-500
    text-neutral-400 ease-in;
}

</style>