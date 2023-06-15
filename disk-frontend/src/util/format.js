export function formatTimestamp(timestamp) {
    let date = new Date(timestamp)
    let Y = date.getFullYear() + '-'
    let M = (date.getMonth() + 1 < 10
        ? '0' + (date.getMonth() + 1)
        : date.getMonth() + 1) + '-'
    let D = (date.getDate() < 10
        ? '0' + date.getDate()
        : date.getDate()) + ' '
    let h = date.getHours() + ':'
    let mm = date.getMinutes() < 10
        ? '0' + date.getMinutes()
        : date.getMinutes()
    let m = mm + ':'
    let s = date.getSeconds() < 10
        ? '0' + date.getSeconds()
        : date.getSeconds()
    return Y + M + D + h + m + s
}

const gb = 1024 * 1024 * 1024
const mb = 1024 * 1024
const kb = 1024

export const formatFileSize = (sizeInBytes) => {
    if (sizeInBytes === Infinity) {
        return '∞'
    }

    if (sizeInBytes < kb) {
        return sizeInBytes + ' B'
    }
    if (sizeInBytes < mb) {
        return (sizeInBytes / kb).toFixed(2) + ' KB'
    }
    if (sizeInBytes < gb) {
        return (sizeInBytes / mb).toFixed(2) + ' MB'
    }
    return (sizeInBytes / gb).toFixed(2) + ' GB'
}
