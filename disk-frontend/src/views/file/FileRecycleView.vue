<template>
    <div class="p-5">
        <n-h2>
            回收站
        </n-h2>
        <div class="flex">
            <div class="mr-3">
                <n-checkbox>
                </n-checkbox>
            </div>
            <div>
                <div v-if="!getCheckedList().length">
                    共 {{ files.length }} 项
                </div>
                <div v-else>
                    已选择 {{ getCheckedList().length }} 项
                </div>
            </div>
            <div class="pl-5 text-neutral-500">
                提示：回收站中的文件会在10天内自动清除，或者您可以手动清除。
            </div>

        </div>
        <div class="my-3">
            <n-button-group>
                <n-button secondary type="error" @click="">清空回收站</n-button>
            </n-button-group>
        </div>

        <div class="py-3 min-h-[30vh]">
            <div class="flex flex-fill flex-wrap transition-all duration-300">
                <FileComponent v-for="(file, i) in files"
                               v-model:checked="checkedState[i]"
                               :file="file"
                               :onClickMoreOptions="handleClickMoreOptions"
                               @click="handleClickMoreOptions($event, file)"
                               @contextmenu="handleFileOptionContextMenu($event, file)"/>
            </div>
        </div>


        <n-dropdown
                :on-clickoutside="onClickOutside"
                :options="fileOptions"
                :show="showFileDropdown"
                :x="xRef"
                :y="yRef"
                placement="bottom-start"
                trigger="manual"
                @select="handleFileOptionSelect"/>
    </div>
</template>

<script setup>
import FileComponent from "@/components/file/FileComponent.vue";
import {ref, getCurrentInstance} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {popAdminErrorTemplate} from "@/views/util/error";
import {createConfig} from "@/request/axios_config";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userStore = useUserStore()

const xRef = ref(0)
const yRef = ref(0)
const showFileDropdown = ref(false)

let showFileDropdownState = false
let lastTarget = null
const curTargetFile = ref({})

const checkedState = ref([])

const files = ref([]);
const fileOptions = [
    {
        label: "还原",
        key: "revert",
    },
    {
        label: "删除",
        key: "delete",
    }
]

const getCheckedList = () => {
    let checkedList = []
    for (let i = 0; i < checkedState.value.length; i++) {
        if (checkedState.value[i]) {
            checkedList.push(i)
        }
    }
    return checkedList
}

const handleFileOptionContextMenu = (e, target) => {
    e.preventDefault();
    showFileDropdownState = false

    xRef.value = e.clientX;
    yRef.value = e.clientY;

    showFileDropdown.value = true;
}

const onClickOutside = () => {
    showFileDropdown.value = false;
}

const handleClickMoreOptions = (e, target) => {
    e.preventDefault();
    if (lastTarget === target) {
        showFileDropdownState = !showFileDropdownState
    } else {
        showFileDropdownState = true
    }
    lastTarget = target
    showFileDropdown.value = showFileDropdownState

    if (!showFileDropdownState) {
        return
    }
    xRef.value = e.clientX
    yRef.value = e.clientY
}

const requestRecycleBinFiles = () => {
    const config = createConfig()

    proxy.$axios.get(
            api.recycles('user', userStore.user.id),
            config).then(res => {
        files.value = res.data
        checkedState.value = new Array(files.value.length).fill(false)
    }).catch(err => {
        popAdminErrorTemplate(notification, err, "获取回收站文件失败")
    })
}

requestRecycleBinFiles()

</script>

<style scoped>

</style>