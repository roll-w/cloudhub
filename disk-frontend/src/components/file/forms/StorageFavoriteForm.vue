<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FormFavoriteGroupItem from "@/components/file/forms/FormFavoriteGroupItem.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()


const props = defineProps({
    storageId: {
        type: Number,
        required: true
    },
    storageType: {
        type: String,
        required: true
    },
    onClickCancel: {
        type: Function,
        default: () => {
        }
    },
    onClickConfirm: {
        type: Function,
        default: () => {
        }
    },
    onBeforeAction: {
        type: Function,
        default: () => {
        }
    },
    onAfterAction: {
        type: Function,
        default: () => {
        }
    },
    // ownerType: {
    //     type: String,
    //     default: 'user'
    // },
    // ownerId: {
    //     type: Number,
    //     required: true
    // },
})

const favoriteGroups = ref([])

const requestFavoriteGroups = () => {
    const config = createConfig()

    proxy.$axios.get(api.favorites(), config).then(resp => {
        favoriteGroups.value = resp.data
    }).catch(error => {
        popUserErrorTemplate(notification, error, '获取收藏夹失败')
    })
}

const selectFavoriteGroup = ref()

const handleClickDefault = () => {
    selectFavoriteGroup.value = {
        id: 0,
        name: '默认收藏夹'
    }

}

const handleFavoriteClick = (event, favorite) => {
    selectFavoriteGroup.value = favorite

}

const handleConfirm = () => {
    props.onClickConfirm()
    props.onBeforeAction()
    requestCreateFavorite()

}

const handleCancel = () => {
    props.onClickCancel()
}

const requestCreateFavorite = () => {
    if (!selectFavoriteGroup.value) {
        message.error('请选择收藏夹')
        return
    }

    const config = createConfig()
    const data = {
        storageId: props.storageId,
        storageType: props.storageType,
    }

    proxy.$axios.post(api.favorites(selectFavoriteGroup.value.id), data, config).then(resp => {
        message.success('收藏成功')
        props.onAfterAction()
    }).catch(error => {
        props.onAfterAction()
        popUserErrorTemplate(notification, error, '创建收藏失败', "收藏请求")
    })
}

requestFavoriteGroups()


</script>

<template>
    <div>
        <div>
            <div class="flex items-center text-lg">
                <div>收藏到</div>
                <div v-if="selectFavoriteGroup" class="ml-2 text-amber-500">
                    <n-icon class="pt-0.5" size="20">
                        <Folder24Regular/>
                    </n-icon>
                    <span class="ml-1">
                        {{ selectFavoriteGroup.name }}
                    </span>
                </div>
            </div>
        </div>

        <div class="py-3 h-[50vh]">
            <n-scrollbar class="pr-4">
                <FormFavoriteGroupItem :active="(selectFavoriteGroup || {}).id === 0"
                                       name="默认收藏夹"
                                       @click="handleClickDefault"/>
                <FormFavoriteGroupItem v-for="favorite in favoriteGroups"
                                       :active="(selectFavoriteGroup || {}).id === favorite.id"
                                       :name="favorite.name"
                                       @click="handleFavoriteClick($event, favorite)"/>
            </n-scrollbar>
        </div>
        <div class="pt-3">
            <n-button-group>
                <n-button :disabled="!selectFavoriteGroup"
                          secondary type="primary" @click="handleConfirm">
                    确定
                </n-button>
                <n-button secondary type="default" @click="handleCancel">
                    取消
                </n-button>
            </n-button-group>
        </div>

    </div>
</template>

<style scoped>

</style>