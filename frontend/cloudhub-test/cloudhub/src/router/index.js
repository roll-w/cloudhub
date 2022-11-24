import {createRouter, createWebHistory} from 'vue-router'
import NotFound from "@/views/error/NotFound";
import LoginView from "@/views/user/LoginView";
import Register from "@/views/user/Register";
import HomeView from "@/views/HomeView";

const routes = [
    {
        path: '/',
        name: "login_index",
        component: LoginView  // 登录界面
    },
    {
        path: '/home',
        name: 'home',
        redirect: 'hub',
        component: HomeView,
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
        component: Register // 注册界面
    },

    {
        path: '/404',
        name: '404',
        component: NotFound // 404
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

export default router
