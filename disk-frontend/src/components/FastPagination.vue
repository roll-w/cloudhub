<script setup>

import {usePage} from "@/views/util/pages";
import {useRouter} from "vue-router";
import {ref} from "vue";

const props = defineProps({
    page: {
        type: Object,
        default: null
    },
    routeName: {
        type: String,
        required: true
    }
})

const router = useRouter();
const currentRoute = router.currentRoute;

const currentParams = currentRoute.value.params;
const currentQuery = currentRoute.value.query;

const page = props.page ? ref(props.page) : usePage();

const setRouteOption = (page) => {
    return {
        name: props.routeName,
        params: currentParams,
        query: {
            ...currentQuery,
            page: page
        }
    }
}

const switchPage = (page) => {
    router.push(setRouteOption(page))
}


</script>

<template>
    <div class="flex items-start justify-start mt-5">
        <div>
            <n-pagination
                    v-model:page="page.page"
                    :on-update-page="switchPage"
                    :page-count="page.count"
                    show-quick-jumper
            />
        </div>
    </div>
</template>

<style scoped>

</style>