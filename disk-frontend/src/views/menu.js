import {h} from "vue";
import {RouterLink} from "vue-router";
import {
    adminClusterMonitor,
    adminFileLists,
    adminFolderLists,
    adminIndex,
    adminLoginLogs,
    adminOperationLogs, adminSystemJobs,
    adminSystemLogs,
    adminSystemMonitor,
    adminTagGroups,
    adminTags,
    adminUserGroupLists,
    adminUserLists,
    adminVisualData,
    driveFilePage,
    driveFilePageFolder,
    driveFilePageTypeAudio,
    driveFilePageTypeDocument,
    driveFilePageTypeImage,
    driveFilePageTypeVideo,
    driveFileRecycleBinPage,
    driveTagPage,
    userSharePage,
} from "@/router";

import {NIcon} from "naive-ui";
import FileIcon from "@/components/icon/FileIcon.vue";
import ImageOutlined from "@/components/icon/ImageOutlined.vue";
import AudioFileOutlined from "@/components/icon/AudioFileOutlined.vue";
import VideoFileOutlined from "@/components/icon/VideoFileOutlined.vue";

export const keyUser = "user"
export const keyAdmin = "admin"

export const menuFile = "File"

export const adminMenuFile = "AdminFile"
export const adminMenuUser = "AdminUser"
export const adminMenuTag = "AdminTag"
export const adminMenuSystem = "AdminSystem"


const menuOptions = [
    {
        key: keyUser,
        menus: [
            {
                name: "文件",
                key: menuFile,
                children: [
                    {
                        name: "个人主页",
                        key: driveFilePage,
                        alias: [
                            driveFilePageFolder
                        ],
                        linked: true,
                        icon: () => {
                            return h(NIcon, {
                                class: "text-2xl"
                            }, {
                                default: () => h(FileIcon)
                            })
                        }
                    },
                    {
                        name: "图片",
                        key: driveFilePageTypeImage,
                        linked: true,
                        icon: () => {
                            return h(NIcon, {
                                class: "text-2xl"
                            }, {
                                default: () => h(ImageOutlined)
                            })
                        }
                    },
                    {
                        name: "视频",
                        key: driveFilePageTypeVideo,
                        linked: true,
                        icon: () => {
                            return h(NIcon, {
                                class: "text-2xl"
                            }, {
                                default: () => h(VideoFileOutlined)
                            })
                        }
                    },
                    {
                        name: "音频",
                        key: driveFilePageTypeAudio,
                        linked: true,
                        icon: () => {
                            return h(NIcon, {
                                class: "text-2xl"
                            }, {
                                default: () => h(AudioFileOutlined)
                            })
                        }
                    },
                    {
                        name: "文档",
                        key: driveFilePageTypeDocument,
                        linked: true,
                        icon: () => {
                            return h(NIcon, {
                                class: "text-2xl"
                            }, {
                                default: () => h(FileIcon)
                            })
                        }
                    },
                    {
                        name: "标签",
                        key: driveTagPage,
                        linked: true
                    },
                    {
                        name: "组织文件",
                        key: "Organization",
                    }
                ]
            },
            {
                name: "收藏",
                key: "Fav",
            },
            {
                name: "分享",
                key: userSharePage,
                linked: true
            },
            {
                name: "回收站",
                key: driveFileRecycleBinPage,
                linked: true
            },

        ]
    },
    {
        key: keyAdmin,
        menus: [
            {
                name: "系统首页",
                key: adminIndex,
                linked: true
            },
            {
                linked: true,
                name: "返回资料库",
                key: driveFilePage
            },
            {
                name: "用户管理",
                key: adminMenuUser,
                children: [
                    {
                        name: "用户列表",
                        key: adminUserLists,
                        linked: true
                    },
                    {
                        name: "登录日志",
                        key: adminLoginLogs,
                        linked: true
                    },
                    {
                        name: "用户组列表",
                        key: adminUserGroupLists,
                        linked: true
                    },
                ]
            },
            {
                name: "存储管理",
                key: adminMenuFile,
                children: [
                    {
                        name: "文件列表",
                        key: adminFileLists,
                        linked: true
                    },
                    {
                        name: "文件夹列表",
                        key: adminFolderLists,
                        linked: true
                    },
                    {
                        name: "分享管理",
                        key: "file-management-share"
                    },
                ]
            },
            {
                name: "标签管理",
                key: adminMenuTag,
                children: [
                    {
                        name: "标签组列表",
                        key: adminTagGroups,
                        linked: true
                    },
                    {
                        name: "标签列表",
                        key: adminTags,
                        linked: true
                    }
                ]
            },
            {
                name: "组织管理",
                key: "organization-management",
                children: [
                    {
                        name: "组织列表",
                        key: "organization-management-list"
                    },
                ]
            },
            {
                name: "可视化数据",
                key: adminVisualData,
                linked: true
            },
            {
                name: "系统管理",
                key: adminMenuSystem,
                children: [
                    {
                        name: "系统日志",
                        key: adminSystemLogs,
                        linked: true
                    },
                    {
                        name: "系统任务",
                        key: adminSystemJobs,
                        linked: true
                    },
                    {
                        name: "系统操作日志",
                        key: adminOperationLogs,
                        linked: true
                    },
                    {
                        name: "集群监控",
                        key: adminClusterMonitor,
                        linked: true
                    },
                    {
                        name: "系统监控",
                        key: adminSystemMonitor,
                        linked: true
                    },
                ]
            },

        ]
    }
]

export const findMenuOptionByKey = (key) => {
    return menuOptions.find(menuOption => menuOption.key === key)
}

export const requestMenusByName = (menuKey, key) => {
    const menuOption = findMenuOptionByKey(menuKey)
    if (!menuOption) {
        return []
    }
    return menuOption.menus.find(menu => menu.key === key) || {}
}

export const requestChildrenMenus = (menuKey, key) => {
    return requestMenusByName(menuKey, key).children || []
}

export const convertsToNMenuOptions = (menus) => {
    return menus.map(menu => {
        let children
        if (menu.children) {
            children = [...menu.children]
            children = convertsToNMenuOptions(children)
        } else {
            children = undefined
        }
        if (!menu.linked) {
            return {
                key: menu.key,
                label: menu.name,
                children: children,
                icon: menu.icon,
            }
        }
        return {
            key: menu.key,
            icon: menu.icon,
            label: () => h(
                RouterLink,
                {
                    to: {
                        name: menu.key,
                    }
                },
                {default: () => menu.name}
            ),
        }
    })
}
