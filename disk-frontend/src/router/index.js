import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from "@/stores/user";

const layout = "layout"
const headerLayout = "header-layout"

export const index = "index"
export const login = "login-page"
export const register = "register-page"

export const driveFilePage = "drive-file-page"

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/layout/side',
            name: layout,
            redirect: '/',
            component: () => import("@/views/Layout.vue"),
            children: [
                {
                    path: '/',
                    name: index,
                    component: () => import("@/views/user/HomeView.vue"),
                    meta: {
                        title: "首页"
                    }
                },{
                    path: '/drive/files',
                    name: driveFilePage,
                    component: () => import("@/views/file/FileView.vue"),
                    meta: {
                        title: "文件"
                    }
                }
            ]
        },
        {
            path: '/layout/header',
            name: headerLayout,
            redirect: '/',
            component: () => import("@/views/HeaderLayout.vue"),
            children: [
                {
                    path: '/user/login',
                    name: login,
                    component: () => import("@/views/user/LoginView.vue"),
                    meta: {
                        title: "登录"
                    }
                },{
                    path: '/user/register',
                    name: register,
                    component: () => import("@/views/user/LoginView.vue"),
                    meta: {
                        title: "注册"
                    }
                }
            ]
        }
    ]
})

const defaultTitle = "档案数据中心";

export const getTitleSuffix = () => {
    return " | 档案数据中心 "
}

router.afterEach((to, from) => {
    document.title = to.meta.title ? to.meta.title + getTitleSuffix() : defaultTitle
})


// router.beforeEach((to, from, next) => {
//     const userStore = useUserStore()
//
//     if (to.meta.requireLogin && !userStore.isLogin) {
//         return next({
//             name: login,
//         })
//     }
//
//     if (!to.name.startsWith("admin")) {
//         return next()
//     }
//     const role = userStore.user.role
//     if (!userStore.isLogin || !role || role.value === "USER") {
//         return next({
//             name: page404
//         })
//     }
//     return next()
// })


export default router