import {h} from "vue";
import {NIcon} from "naive-ui";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FileIcon from "@/components/icon/FileIcon.vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";

const fileViewMenuOptions = [
    {
        label: "新建文件夹",
        key: "folder",
        icon() {
            return h(NIcon, null, {
                default: () => h(Folder24Regular)
            })
        },
    },
    {
        label: "上传文件",
        key: "upload",
        icon() {
            return h(NIcon, null, {
                default: () => h(FileIcon)
            })
        },
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "刷新",
        key: "refresh",
        icon() {
            return h(NIcon, null, {
                default: () => h(RefreshRound)
            })
        },
    },
    {
        label: "排序",
        key: "sort",
        icon() {
            return "S"
        },
    },
];

const fileTypeViewMenuOptions = [
    {
        label: "上传文件请到文件夹中操作",
        key: "non-action",
    },
    {
        key: 'header-divider',
        type: 'divider'
    },
    {
        label: "刷新",
        key: "refresh",
        icon() {
            return h(NIcon, null, {
                default: () => h(RefreshRound)
            })
        },
    },
    {
        label: "排序",
        key: "sort",
        icon() {
            return "S"
        },
    },
];

export const getFileViewMenuOptions = (type = null) => {
    if (type) {
        return fileTypeViewMenuOptions;
    }
    return fileViewMenuOptions;
}

import * as ops from './option_names'

export const options = ops;
