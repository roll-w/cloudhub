import {createRouter, createWebHistory} from 'vue-router'
import NotFound from "@/views/error/NotFound";
import LoginView from "@/views/user/LoginView";
import Register from "@/views/user/Register";
import HomeView from "@/views/HomeView";

const routes = [
    {
        path: '/',
        name: "login_index",
        component: LoginView,  // 登录界面
        meta: {
            title : "登录 | Cloudhub 对象存储系统"
        }
    },
    {
        path: '/home',
        name: 'home',
        redirect: 'hub',
        component: HomeView,
        meta: {
            title : "主页 | Cloudhub 对象存储系统"
        },
        children: [
            {
                path: '/hub',
                name: 'cloudhub_index',
                component: () => import('@/views/user/CloudViewManager')
            },
            {
                path: '/file',
                name: 'file_index',
                component: () =>import('@/views/file/FileView') // 文件列表
            },
            {
                path: '/bucket',
                name: 'bucket_index',
                component: ()=>import('@/views/bucket/BucketView')  // 桶管理
            },
            {
                path: '/user',
                name: 'userList_index',
                component: () =>import('@/views/user/UserList') // 用户列表
            },
            {
                path: '/metadataserver',
                name: 'metadataserver_index',
                component:  () =>import('@/views/server/MetadataServer') // 元数据服务器
            },
            {
                path: '/fileserver',
                name: 'fileserver_index',
                component: () =>import('@/views/server/FileServer') // 文件服务器
            },
        ]
    },
    {
        path: '/register',
        name: "register_index",
        component: Register, // 注册界面
        meta: {
            title : "注册 | Cloudhub 对象存储系统"
        }
    },

    {
        path: '/404',
        name: '404',
        component: NotFound,// 404,
        meta: {
            title : "404未找到资源 | Cloudhub 对象存储系统"
        }
    },
    {
        path: '/:catchAll(.*)',
        redirect: '/404'
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})
const defaultTitle = "Cloudhub 对象储存系统"

router.beforeEach((to, from, next) => {
    document.title = to.meta.title ? to.meta.title : defaultTitle
    next()
})
export default router
