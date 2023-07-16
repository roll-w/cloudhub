<script setup>
import {getCurrentInstance, h, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import FavoriteGroupComponent from "@/components/user/favorite/FavoriteGroupComponent.vue";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import {driveFilePageFolder, userPersonalPageWithFolder} from "@/router";
import StorageFavoriteForm from "@/components/file/forms/StorageFavoriteForm.vue";
import {hackFileOptions} from "@/views/file/storage_actions";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const props = defineProps({
    userId: {
        type: [String, Number],
        required: true
    }
})

const showFavoriteStorageModal = ref(false)

const curStorage = ref({})
const favoriteGroups = ref([])
const favorites = ref([])
const favoriteGroupId = ref(-1)

const fileOptions = [
    {
        label: "下载",
        key: "download",
    },
    {
        label: "收藏",
        key: "favorite",
    },
    {
        label: "跳转到文件夹",
        key: "folder",
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
]

const requestFavoriteGroups = () => {
    const config = createConfig()

    proxy.$axios.get(api.userFavorites(props.userId), config).then(resp => {
        favoriteGroups.value = resp.data
        if (resp.data.length > 0) {
            favoriteGroupId.value = resp.data[0].id
            requestFavoriteItems()
        }

    }).catch(error => {
        popUserErrorTemplate(notification, error, '获取收藏夹失败', "收藏请求失败")
    })
}

const requestFavoriteItems = () => {
    const config = createConfig()

    proxy.$axios.get(api.userFavorites(props.userId,
            false, favoriteGroupId.value), config).then(resp => {
        favorites.value = resp.data
    }).catch(error => {
        popUserErrorTemplate(notification, error, '获取收藏失败', "收藏请求失败")
    })
}

const handleClickFavoriteGroup = (group) => {
    if (group.id === favoriteGroupId.value) {
        return
    }
    favoriteGroupId.value = group.id
    requestFavoriteItems()
}

const handleFolderClick = (e, folder) => {
    router.push({
        name: userPersonalPageWithFolder,
        params: {
            id: folder.ownerId,
            folder: folder.storageId
        }
    })
}

const handleFileOptionSelect = (key, options, storage) => {
    curStorage.value = storage
    switch (key) {
        case "download":
            break
        case "favorite":
            showFavoriteStorageModal.value = true
            break
        case "folder":
            if (storage.ownerId === userStore.user.id) {
                router.push({
                    name: driveFilePageFolder,
                    params: {
                        folder: storage.parentId
                    }
                })
            } else {
                router.push({
                    name: userPersonalPageWithFolder,
                    params: {
                        id: storage.ownerId,
                        folder: storage.parentId
                    }
                })
            }
            break
        case "log":
        case "permission":
            break
        case "delete":
            break
    }
}

const hackFileOptionsOf = (file) => {
    hackFileOptions(file, fileOptions, {
        refer: 'home_favorite',
        source: favoriteGroupId.value
    })
}

requestFavoriteGroups()

</script>

<template>
    <div>
        <n-h2>收藏夹</n-h2>
        <div class="flex">
            <n-scrollbar trigger="none" x-scrollable>
                <div class="flex flex-row py-5">
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
                    :disable-preview="false"
                    :file-options="fileOptions"
                    :files="favorites"
                    :on-show-file-option="hackFileOptionsOf"
                    :on-file-option-select="handleFileOptionSelect"
                    :on-folder-click="handleFolderClick">
                <template #title>
                    收藏夹内容
                </template>
                <template #empty>
                    <n-empty description="暂无收藏内容" size="large"/>
                </template>
            </FileComponentsView>
        </div>

        <div>
            <n-modal v-model:show="showFavoriteStorageModal"
                     :show-icon="false"
                     :title="'收藏 ' + curStorage.name"
                     preset="dialog"
                     transform-origin="center">
                <StorageFavoriteForm
                        :on-after-action="() => {
                            showFavoriteStorageModal = false
                        }"
                        :on-click-cancel="() => showFavoriteStorageModal = false"
                        :owner-id="curStorage.ownerId"
                        :storage-id="curStorage.storageId"
                        :storage-type="curStorage.storageType"/>
            </n-modal>
        </div>
    </div>


</template>

<style scoped>


</style>