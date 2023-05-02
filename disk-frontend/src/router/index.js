import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from "@/stores/user";

const layout = "layout"
const adminLayout = "admin-layout"
const headerLayout = "header-layout"

export const index = "index"
export const login = "login-page"
export const register = "register-page"

export const driveFilePage = "drive-file-page"
export const driveFileAttrsPage = "drive-file-attrs-page"
export const driveFilePermissionPage = "drive-file-permission-page"

export const page404 = "page-404"
export const driveTagPage = "drive-tag-page"

export const adminIndex = "admin-index"

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
                    path: '/drive/files',
                    name: driveFilePage,
                    component: () => import("@/views/file/FileView.vue"),
                    meta: {
                        title: "文件"
                    }
                },
                {
                    path: '/drive/files/:type/:id/attrs',
                    name: driveFileAttrsPage,
                    component: () => import("@/views/file/FileAttrsView.vue"),
                    meta: {
                        title: "文件属性"
                    }
                },
                {
                    path: '/drive/files/:type/:id/permission',
                    name: driveFilePermissionPage,
                    component: () => import("@/views/file/FilePermissionPage.vue"),
                    meta: {
                        title: "文件权限"
                    }
                },
                {
                    path: '/drive/tags',
                    name: driveTagPage,
                    component: () => import("@/views/tag/FileTagsView.vue"),
                    meta: {
                        title: "标签"
                    }
                },
                {
                    path: '/data',
                    name: "echarts",
                    component: () => import("@/views/EchartsIndexView.vue"),
                    meta: {
                        title: "数据分析"
                    }
                },
            ]
        },
        {
            path: '/layout/admin',
            name: adminLayout,
            redirect: '/admin',
            component: () => import("@/views/AdminLayout.vue"),
            children: [
                {
                    path: '/admin',
                    name: adminIndex,
                    component: () => import("@/views/admin/AdminIndex.vue"),
                    meta: {
                        title: "管理首页"
                    }
                },
            ]
        },
        {
            path: '/',
            name: index,
            component: () => import("@/views/user/HomeView.vue"),
            meta: {
                title: "首页"
            }
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
                },
                {
                    path: '/user/register',
                    name: register,
                    component: () => import("@/views/user/LoginView.vue"),
                    meta: {
                        title: "注册"
                    }
                },
                {
                    path: '/error/404',
                    name: page404,
                    component: () => import('@/views/NotFound.vue'),
                    meta: {
                        title: "404"
                    }
                },
                {
                    path: '/:path(.*)*',
                    redirect: '/error/404'
                },
            ]
        }
    ]
})

const defaultTitle = "Cloudhub 法律案件资料库";

export const getTitleSuffix = () => {
    return " | Cloudhub 法律案件资料库 "
}

router.afterEach((to, from) => {
    document.title = to.meta.title ? to.meta.title + getTitleSuffix() : defaultTitle
})


router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    if (to.meta.requireLogin && !userStore.isLogin) {
        return next({
            name: login,
        })
    }

    if (!to.name.startsWith("admin")) {
        return next()
    }
    const role = userStore.user.role
    if (!userStore.isLogin || !role || role.value === "USER") {
        return next({
            name: page404
        })
    }
    return next()
})


export default router
