import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from "@/stores/user";

const layout = "layout"
const adminLayout = "admin-layout"
const headerLayout = "header-layout"

export const index = "index"
export const login = "login-page"
export const register = "register-page"

export const passwordResetPage = "password-reset-page"

export const driveFilePage = "drive-file-page"
export const driveFilePageFolder = "drive-file-page-folder"
export const driveFileSearchPage = "drive-file-search-page"
export const driveFileAttrsPage = "drive-file-attrs-page"
export const driveFilePermissionPage = "drive-file-permission-page"

export const page404 = "page-404"
export const driveTagPage = "drive-tag-page"

export const userSettingPage = "user-setting-page"

export const adminIndex = "admin-index"
export const adminUserLists = "admin-user-lists"

export const adminFileLists = "admin-file-lists"
export const adminContentTags = "admin-content-tags"

export const adminVisualData = "admin-visual-data"

export const adminSystemLogs = "admin-system-logs"
export const adminLoginLogs = "admin-login-logs"
export const adminOperationLogs = "admin-operation-logs"

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
                    path: '/drive',
                    redirect: '/drive/files'
                },
                {
                    path: '/drive/files',
                    name: driveFilePage,
                    component: () => import("@/views/file/FileView.vue"),
                    meta: {
                        title: "文件",
                        requireLogin: true
                    }
                },
                {
                    path: '/drive/files/:folder',
                    name: driveFilePageFolder,
                    component: () => import("@/views/file/FileView.vue"),
                    meta: {
                        title: "文件",
                        requireLogin: true
                    }
                },
                {
                    path: '/drive/files/search',
                    name: driveFileSearchPage,
                    component: () => import("@/views/file/FileSearchView.vue"),
                    meta: {
                        title: "文件搜索",
                        requireLogin: true
                    }
                },
                {
                    path: '/:ownerType/:ownerId/drive/files/:type/:id/attrs',
                    name: driveFileAttrsPage,
                    component: () => import("@/views/file/FileAttrsView.vue"),
                    meta: {
                        title: "文件属性",
                        requireLogin: true
                    }
                },
                {
                    path: '/:ownerType/:ownerId/drive/files/:type/:id/permission',
                    name: driveFilePermissionPage,
                    component: () => import("@/views/file/FilePermissionPage.vue"),
                    meta: {
                        title: "文件权限",
                        requireLogin: true
                    }
                },
                {
                    path: '/drive/tags',
                    name: driveTagPage,
                    component: () => import("@/views/tag/FileTagsView.vue"),
                    meta: {
                        title: "标签",
                        requireLogin: true
                    }
                },

                {
                    path: '/user/setting',
                    name: userSettingPage,
                    component: () => import("@/views/user/UserSettingView.vue"),
                    meta: {
                        title: "用户设置",
                        requireLogin: true
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
                        title: "管理首页",
                        requireLogin: true
                    }
                },
                {
                    path: '/admin/users',
                    name: adminUserLists,
                    component: () => import("@/views/admin/user/UsersList.vue"),
                    meta: {
                        title: "用户列表",
                        requireLogin: true
                    }
                },
                {
                    path: '/admin/data',
                    name: adminVisualData,
                    component: () => import("@/views/EchartsIndexView.vue"),
                    meta: {
                        title: "数据分析",
                        requireLogin: true
                    }
                },
                {
                    path: '/admin/system/logs',
                    name: adminSystemLogs,
                    component: () => import("@/views/admin/system/SystemLogs.vue"),
                    meta: {
                        title: "系统日志",
                        requireLogin: true
                    }
                },
                {
                    path: '/admin/users/login/logs',
                    name: adminLoginLogs,
                    component: () => import("@/views/admin/system/LoginLogs.vue"),
                    meta: {
                        title: "用户登录日志",
                        requireLogin: true
                    }
                },
                {
                    path: '/admin/system/operations',
                    name: adminOperationLogs,
                    component: () => import("@/views/admin/system/SystemLogs.vue"),
                    meta: {
                        title: "用户操作日志",
                        requireLogin: true
                    }
                },
            ]
        },
        {
            path: '/',
            name: index,
            component: () => import("@/views/user/HomeView.vue"),
            meta: {
                title: "Cloudhub 法律案件资料库 - 可靠、专业的法律案件资料库",
                originalTitle: true
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
                    path: '/user/reset/password',
                    name: passwordResetPage,
                    component: () => import("@/views/user/PasswordResetView.vue"),
                    meta: {
                        title: "重置密码"
                    }
                },
                {
                    path: '/about',
                    name: 'about',
                    component: () => import("@/views/system/About.vue"),
                    meta: {
                        title: "关于"
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
    if (to.meta.originalTitle) {
        document.title = to.meta.title
        return
    }

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
    if (!userStore.isLogin || !role || role === "USER") {
        return next({
            name: page404
        })
    }
    return next()
})


export default router
