import {useRouter} from "vue-router";

const getPageInternal = (router) => {
    const page = router.currentRoute.value.query.page || '1'
    const size = router.currentRoute.value.query.size || '10'

    return {
        page: page ? parseInt(page) : 1,
        size: size ? parseInt(size) : 10
    }
}

export const getPage = () => {
    return getPageInternal(useRouter())
}