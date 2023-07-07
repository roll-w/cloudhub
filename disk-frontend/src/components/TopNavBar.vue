<template>
    <n-layout-header bordered style="height: var(--header-height)">
        <div class="p-1 flex content-center">
            <div class="ml-1 flex rounded-2xl justify-start
             hover:bg-opacity-20 hover:bg-neutral-300 transition-all ease-in">
                <n-text :depth="1" class="ui-logo " @click="handleLogoClick">
                    <Logo/>
                </n-text>
            </div>

            <div class="p-3 flex flex-grow items-center justify-end justify-items-end ">
                <n-space class="flex items-center">
                    <div v-if="userStore.isLogin" class="pr-3">
                        <n-space>
                            <n-button circle quaternary
                                      @click="$router.push({name: driveFileSearchPage})">
                                <n-icon size="22">
                                    <SearchFilled/>
                                </n-icon>
                            </n-button>
                            <n-dropdown :on-clickoutside="handleClickOutside"
                                        :options="uploadViewOptions"
                                        :show="showTransferDropdown"
                                        trigger="manual">
                                <n-button circle quaternary @click="handleUploadClick">
                                    <n-icon size="22">
                                        <ArrowUpload16Filled/>
                                    </n-icon>
                                </n-button>
                            </n-dropdown>
                            <n-button circle quaternary
                                      @click="$router.push({name: userSearchPage})">
                                <n-icon size="22">
                                    <UsersOutlined/>
                                </n-icon>
                            </n-button>
                        </n-space>
                    </div>

                    <n-button secondary @click="handleThemeClick">切换主题</n-button>
                    <div class="h-9 self-center">
                        <n-button v-if="!userStore.isLogin" @click="handleLoginClick">登录</n-button>
                        <n-dropdown v-else :options="options" trigger="hover" @select="handleSelect">
                            <n-avatar v-if="userStore.userData.setup"
                                      :src="userStore.userData.avatar"
                                      class="border"/>
                            <n-avatar v-else
                                      :style="{
                                        backgroundColor: hexUserColor,
                                      }"
                            >
                                <div class="select-none">
                                    {{ username }}
                                </div>
                            </n-avatar>
                        </n-dropdown>
                    </div>
                </n-space>
            </div>
        </div>
    </n-layout-header>
</template>


<script setup>
import {RouterLink, useRouter} from "vue-router";
import {useUserStore} from "@/stores/user";
import {getCurrentInstance, h, onMounted, ref} from "vue";
import {NAvatar, NText} from "naive-ui";
import {useSiteStore} from "@/stores/site";
import {
    adminIndex,
    driveFilePage,
    driveFileSearchPage,
    index,
    login,
    userPersonalPage, userSearchPage,
    userSettingPage,
    userSharePage
} from "@/router";
import {MD5} from "@/util/crypto";
import Logo from "@/components/icon/Logo.vue";
import {useFileStore} from "@/stores/files";
import ArrowUpload16Filled from "@/components/icon/ArrowUpload16Filled.vue";
import SearchFilled from "@/components/icon/SearchFilled.vue";
import FileUploadStateView from "@/components/file/FileUploadStateView.vue";
import UsersOutlined from "@/components/icon/UsersOutlined.vue";


const router = useRouter();
const {proxy} = getCurrentInstance()

const siteStore = useSiteStore()
const userStore = useUserStore()
const fileStore = useFileStore()

const username = ref('')
const role = ref(userStore.user.role)

const showTransferDropdown = ref(false)
const transferDropdownOutside = ref(false)

const hexUserColor = ref('#2876c7')

const loadUsername = (newUsername, newRole) => {
    username.value = newUsername
    const colorExtracted = MD5(newUsername + newRole || '').substring(4, 10)
    hexUserColor.value = `#${colorExtracted}`
    role.value = newRole
}

onMounted(() => {
    if (!userStore.userData.setup) {
        // requestPersonalData()
    }
})

loadUsername(userStore.userData.nickname || userStore.user.username, userStore.user.role)

const uploadViewOptions = [
    {
        key: "header",
        type: "render",
        render: () => {
            return h(FileUploadStateView, {})
        }
    }
]

const userOptions = [
    {
        label: `${userStore.user.username}`,
        key: "username",
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: userPersonalPage,
                        params: {
                            id: userStore.user.id
                        }
                    }
                },
                {default: () => "个人主页"}
        ),
        key: "space",
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: userSharePage,
                    }
                },
                {default: () => "个人分享"}
        ),
        key: "share",
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: driveFilePage,
                    }
                },
                {default: () => "文件管理"}
        ),
        key: "storage",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: userSettingPage,
                    }
                },
                {default: () => "个人设置"}
        ),
        key: "settings",
    },
    {
        label: "退出",
        key: "logout",
    }
]

const adminOptions = [
    {
        label: `${userStore.user.username}`,
        key: "username",
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: userPersonalPage,
                        params: {
                            id: userStore.user.id
                        }
                    }
                },
                {default: () => "个人主页"}
        ),
        key: "space",
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: userSharePage,
                    }
                },
                {default: () => "个人分享"}
        ),
        key: "share",
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: driveFilePage,
                    }
                },
                {default: () => "文件管理"}
        ),
        key: "storage",
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: adminIndex
                    }
                },
                {default: () => "系统管理"}
        ),
        key: "system",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: () => h(
                RouterLink,
                {
                    to: {
                        name: userSettingPage,
                    }
                },
                {default: () => "个人设置"}
        ),
        key: "settings",
    },
    {
        label: "退出",
        key: "logout",
    }
]

const options = ref(userOptions)

const updatesOption = (options, username, id) => {
    options[0].label = username
    return options
}

const chooseOptions = (username, role, id) => {
    if (!role) {
        options.value = null
        return
    }
    if (role !== "USER") {
        options.value = updatesOption(adminOptions, username, id)
    } else {
        options.value = updatesOption(userOptions, username, id)
    }
}

chooseOptions(userStore.user.username, userStore.user.role, userStore.user.id)

userStore.$subscribe((mutation, state) => {
    if (!state.user) {
        loadUsername(null, 'USER')
        chooseOptions(null, null, 0)
        return
    }
    loadUsername(state.userData.nickname || state.user.username, state.user.role)
    chooseOptions(state.user.username, state.user.role, state.user.id)
})


const handleUploadClick = () => {
    if (transferDropdownOutside.value) {
        transferDropdownOutside.value = false
        return
    }
    showTransferDropdown.value = true
}

const handleClickOutside = () => {
    fileStore.hideTransferDialog()
    showTransferDropdown.value = false
    transferDropdownOutside.value = true
}

fileStore.$subscribe((mutation, state) => {
    showTransferDropdown.value = true
})

const handleLogoClick = () => {
    router.push({
        name: index
    });
};

const handleLoginClick = () => {
    router.push({
        name: login
    });
};

const handleSelect = (key) => {
    switch (key) {
        case "logout":
            userStore.logout();
            router.push({
                name: index
            })
            break;
    }
}

const handleThemeClick = () => {
    siteStore.toggleTheme()
}

</script>

<style scoped>
.nav {
    display: grid;
    align-items: center;
}

.ui-logo {
    cursor: pointer;
    display: flex;
    align-items: center;
    font-size: 18px;
}

.ui-logo > img {
    margin-right: 12px;
    height: 32px;
    width: 32px;
}

.nav-menu {
    padding-left: 36px;
}

.nav-picker {
    margin-right: 4px;
}

.nav-picker.padded {
    padding: 0 10px;
}

.nav-picker:last-child {
    margin-right: 0;
}

.nav-end {
    display: flex;
    align-items: center;
}
</style>
