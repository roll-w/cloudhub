<script setup>
import {computed, getCurrentInstance, h, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import api from "@/request/api";
import FavoriteGroupComponent from "@/components/user/favorite/FavoriteGroupComponent.vue";
import Add24Regular from "@/components/icon/Add24Regular.vue";
import FavoriteGroupCreateForm from "@/components/user/favorite/FavoriteGroupCreateForm.vue";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import {useUserStore} from "@/stores/user";
import {userFavoritePage, userFavoritePageWithId} from "@/router";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()
const userStore = useUserStore()

const favoriteGroups = ref([])
const favorites = ref([])

const favoriteGroupId = computed(() => {
    return parseInt(router.currentRoute.value.params.id) || 0
})

const showCreateFavoriteGroupModal = ref(false)

const handleClickFavoriteGroup = (group) => {
    if (group.id === favoriteGroupId.value) {
        return
    }
    if (group.id === 0) {
        router.push({
            name: userFavoritePage,
        })
        return
    }
    router.push({
        name: userFavoritePageWithId,
        params: {
            id: group.id
        }
    })
}


const fileOptions = [
    {
        label: "下载",
        key: "download",
    },
    {
        label: "跳转到文件夹",
        key: "favorite",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "日志",
        key: "log",
    },
    {
        label: "权限",
        key: "permission",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: () => h(
                'div',
                {
                    class: "text-red-500 mr-10"
                },
                {default: () => "取消收藏"}
        ),
        key: "delete",
    },
]

const requestFavoriteGroups = () => {
    const config = createConfig()

    proxy.$axios.get(api.favorites(), config).then(resp => {
        favoriteGroups.value = resp.data
    }).catch(error => {
        popUserErrorTemplate(notification, error, '获取收藏夹失败', "收藏请求失败")
    })
}

const requestFavoriteItems = () => {
    const config = createConfig()

    proxy.$axios.get(api.userFavorites(userStore.user.id,
            false, favoriteGroupId.value), config).then(resp => {
        favorites.value = resp.data
    }).catch(error => {
        popUserErrorTemplate(notification, error, '获取收藏失败', "收藏请求失败")
    })
}

const refreshGroups = () => {
    requestFavoriteGroups()
}

const refresh = () => {
    refreshGroups()
    requestFavoriteItems()
}

refresh()

</script>

<template>
    <div class="p-5">
        <n-h1>收藏夹</n-h1>
        <div class="flex">
            <FavoriteGroupComponent class="py-7" name="创建新收藏夹"
                                    @click="showCreateFavoriteGroupModal = true">
                <template #icon>
                    <Add24Regular/>
                </template>
            </FavoriteGroupComponent>
            <n-scrollbar x-scrollable trigger="none">
                <div class="flex flex-row py-5">
                    <FavoriteGroupComponent
                            :active="favoriteGroupId === 0"
                            name="默认收藏夹"
                            @click="handleClickFavoriteGroup({id: 0})"/>
                    <FavoriteGroupComponent v-for="group in favoriteGroups"
                                            :active="group.id === favoriteGroupId"
                                            :name="group.name"
                                            @click="handleClickFavoriteGroup(group)"
                    />
                </div>
            </n-scrollbar>
        </div>
        <div class="py-3">
            <FileComponentsView
                    :file-options="fileOptions"
                    :disable-preview="false"
                    :files="favorites">
                <template #title>
                    收藏夹内容
                </template>
                <template #empty>
                    <n-empty description="暂无收藏内容" size="large"/>
                </template>
            </FileComponentsView>
        </div>
        <n-modal v-model:show="showCreateFavoriteGroupModal"
                 :show-icon="false"
                 preset="dialog"
                 title="创建收藏夹"
                 transform-origin="center">
            <FavoriteGroupCreateForm
                    :on-after-action="() => {
                     showCreateFavoriteGroupModal = false
                     refreshGroups()
                }"
                    :on-click-cancel="() => showCreateFavoriteGroupModal = false"
            />
        </n-modal>
    </div>
</template>

<style scoped>

</style>