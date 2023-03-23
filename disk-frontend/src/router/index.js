import {createRouter, createWebHistory} from 'vue-router'

export const homePage = "home"

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: homePage,
            component: () => import("@/views/Layout.vue"),
            children: [

            ]
        },
    ]
})

export default router
