import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import {useNotification} from "naive-ui";
import {h} from "vue";
import {RouterLink} from "vue-router";
import {driveFileAttrsPage, driveFilePermissionPage} from "@/router";

export const handleStorageDownload = (storage, ownerType, ownerId, axios, callback = null) => {
    const notification = useNotification()

    if (storage.storageType !== 'FILE') {
        return
    }
    const config = createConfig()
    axios.post(
        api.fileToken(ownerType, ownerId, storage.storageId), null, config).then((res) => {
        const token = res.data
        const url = api.quickfire(token)
        if (callback) {
            callback(url)
            return
        }
        window.open(url, '_self')
    }).catch((err) => {
        popUserErrorTemplate(notification, err, '下载文件失败')
    })
}

export const hackFileOptions = (file, fileOptions, queryParams) => {
    fileOptions.find(option => option.key === 'log').label = () => {
        return h(RouterLink, {
            to: {
                name: driveFileAttrsPage,
                params: {
                    ownerType: file.ownerType.toLowerCase(),
                    ownerId: file.ownerId,
                    id: file.storageId,
                    type: file.storageType.toLowerCase()
                },
                query: queryParams
            },
        }, {
            default: () => '属性与日志'
        });
    }
    fileOptions.find(option => option.key === 'permission').label = () => {
        return h(RouterLink, {
            to: {
                name: driveFilePermissionPage,
                params: {
                    ownerType: file.ownerType.toLowerCase(),
                    ownerId: file.ownerId,
                    id: file.storageId,
                    type: file.storageType.toLowerCase()
                },
                query: queryParams
            },
        }, {
            default: () => '权限'
        });
    }
}