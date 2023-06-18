import {useRouter} from "vue-router";
import {ref} from "vue";

const getPageInternal = (router) => {
    const page = router.currentRoute.value.query.page || '1'
    const size = router.currentRoute.value.query.size || '10'

    return {
        page: page ? parseInt(page) : 1,
        size: size ? parseInt(size) : 10
    }
}

export const usePage = () => {
    return ref(getPageInternal(useRouter()))
}