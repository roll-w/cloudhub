export const getFileType = (type = 'OTHER') => {
    switch (type) {
        case 'IMAGE':
            return '图片'
        case 'VIDEO':
            return '视频'
        case 'AUDIO':
            return '音频'
        case 'DOCUMENT':
            return '文档'
        case 'TEXT':
            return '文本'
        case 'COMPRESSED':
            return '压缩包'
        case 'FILE':
            return '文件'
        case 'FOLDER':
            return '文件夹'
        case 'LINK':
            return '链接'
        default:
            return '其他'
    }
}


export const getActionName = (action = 'UNKNOWN') => {
    switch (action) {
        case 'CREATE':
            return '创建'
        case 'UPDATE':
            return '更新'
        case 'DELETE':
            return '删除'
        case 'MOVE':
            return '移动'
        case 'COPY':
            return '复制'
        case 'RENAME':
            return '重命名'
        case 'ACCESS':
            return '访问'
        case 'EDIT':
            return '编辑'
        case 'UNKNOWN':
            return '未知'
    }
}

export const getSystemResourceKindName = (kind = 'UNKNOWN') => {
    switch (kind) {
        case 'FILE':
            return '文件'
        case 'FOLDER':
            return '文件夹'
        case 'LINK':
            return '链接'
        case 'STORAGE_PERMISSION':
            return '存储权限'
        case 'VERSIONED_FILE':
            return '版本化文件'
        case 'VERSIONED_FOLDER':
            return '版本化文件夹'
        case 'STORAGE_SHARE':
            return '存储共享'
        case 'TAG':
            return '标签'
        case 'TAG_GROUP':
            return '标签组'
        case 'USER_SETTING':
            return '用户设置'
        case 'GROUP_SETTING':
            return '组设置'
        case 'ORGANIZATION_SETTING':
            return '组织设置'
        case 'UNKNOWN':
            return '未知'
    }
}
