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

export const toFileType = (type = '其他') => {
    switch (type) {
        case '图片':
            return 'IMAGE'
        case '视频':
            return 'VIDEO'
        case '音频':
            return 'AUDIO'
        case '文档':
            return 'DOCUMENT'
        case '文本':
            return 'TEXT'
        case '压缩包':
            return 'COMPRESSED'
        case '文件':
            return 'FILE'
        case '文件夹':
            return 'FOLDER'
        case '链接':
            return 'LINK'
        default:
            return 'OTHER'
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
        case 'USER':
        case 'USER_SETTING':
            return '用户'
        case 'GROUP':
        case 'GROUP_SETTING':
            return '用户组'
        case 'ORGANIZATION':
            return '组织'
        case 'STORAGE_USER_PERMISSION':
            return '存储用户权限'
        case 'FAVORITE_GROUP':
            return '收藏夹'
        case 'FAVORITE_ITEM':
            return '收藏'
        default:
        case 'UNKNOWN':
            return '未知'
    }
}

export const getKeywordSearchScopeName = (name) => {
    switch (name) {
        case 'NAME':
            return '名称'
        case 'DESCRIPTION':
            return '描述'
        case 'CONTENT':
            return '内容'
        case 'ALL':
            return '所有'
    }
}
