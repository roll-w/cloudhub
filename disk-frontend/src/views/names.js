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

