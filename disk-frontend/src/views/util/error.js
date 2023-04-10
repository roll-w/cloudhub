/**
 * @param {NotificationApiInjection} notification
 * @param {{tip: string, message: string}} error
 * @param {string} location
 * @param {string} errorType
 */
export function popAdminErrorTemplate(notification, error,
                                      location = "请求错误",
                                      errorType = "请求错误") {
    const msg = error.tip +
        "\n信息： " + error.message
    notification.error({
        title: location,
        content: msg,
        meta: errorType,
        duration: 3000,
        keepAliveOnHover: true
    })
}

/**
 * @param {NotificationApiInjection} notification
 * @param {{tip: string, message: string}} error
 * @param {string} location
 * @param {string} errorType
 */
export function popUserErrorTemplate(notification, error,
                                     location = "请求错误",
                                     errorType = "请求错误") {
    const msg = error.tip
    notification.error({
        title: location,
        content: msg,
        meta: errorType,
        duration: 3000,
        keepAliveOnHover: true
    })
}
