const viteBase = import.meta.env.VITE_BASE_URL;
const httpPrefix = import.meta.env.VITE_HTTP_PREFIX;

export const baseUrl = `${httpPrefix}${viteBase}`;
export const wsBaseUrl = `ws://${viteBase}`;

const prefix = `${baseUrl}/api/v1`;
const adminPrefix = `${baseUrl}/api/v1/admin`;

export const passwordLogin = `${prefix}/user/login/password`;
export const logout = `${prefix}/user/logout`;
export const register = `${prefix}/user/register`;

export const file = (ownerType, ownerId, fileId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/file/${fileId}`;
export const recycles = (ownerType, ownerId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/recycles`;

export const fileType = (ownerType, ownerId, fileType) =>
    `${prefix}/${ownerType}/${ownerId}/disk/file/category/${fileType}`;
export const fileToken = (ownerType, ownerId, fileId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/file/${fileId}/token`;
export const uploadFile = (ownerType, ownerId, folderId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${folderId}`;
export const getStorageInfo = (ownerType, ownerId, storageType, storageId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${storageId}/info`;
export const getStorageAttributes = (ownerType, ownerId, storageType, storageId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${storageId}/tags`;
export const getStorageVersions = (ownerType, ownerId, storageType, id) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}/versions`;
export const getStorageVersion = (ownerType, ownerId, storageType, id, version) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}/versions/${version}`;
export const getStoragePermissions = (ownerType, ownerId, storageType, id) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}/permissions`;

export const folder = (ownerType, ownerId, directory) =>
    `${prefix}/${ownerType}/${ownerId}/disk/folder/${directory}`;
export const storage = (ownerType, ownerId, storageType, id) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}`;
export const storageName = (ownerType, ownerId, storageType, id) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}/info/name`;
export const storageParent = (ownerType, ownerId, storageType, id) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}/info/parent`;
export const search = (ownerType, ownerId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/search`;
export const storageShare = (ownerType, ownerId, storageType, id) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}/shares`;
export const shareTokenInfo = (token) =>
    `${prefix}/shares/${token}/metadata`;
export const shareToken = (token) =>
    `${prefix}/shares/${token}`;
export const userShares = `${prefix}/user/shares`;

export const quickfire = (token) =>
    `${prefix}/quickfire/disk/${token}`;

export const getOperationLogsByResource = (resourceType, resourceId) =>
    `${prefix}/${resourceType}/${resourceId}/operations/logs`;

export const getOperationLogsAdmin =
    `${adminPrefix}/operations/logs`;
export const tagGroups = (admin = false) =>
    `${admin ? adminPrefix : prefix}/tags/groups`;
export const tags = (admin = false) =>
    `${admin ? adminPrefix : prefix}/tags`;
export const files = (admin = false) =>
    `${admin ? adminPrefix : prefix}/disk/files`;
export const folders = (admin = false) =>
    `${admin ? adminPrefix : prefix}/disk/folders`;
export const storages = (admin = false) =>
    `${admin ? adminPrefix : prefix}/disk/storages`;

export const shares = (admin = false) =>
    `${admin ? adminPrefix : prefix}/shares`;

export const getCurrentUserOperationLogs = `${prefix}/user/operations/logs`;
export const getCurrentUserLoginLogs = `${prefix}/user/login/logs`;
export const getCurrentUserInfo = `${prefix}/user`;

export const userInfo = (userId, admin = false) =>
    `${admin ? adminPrefix : prefix}/users/${userId}`;

export const getUsers = `${adminPrefix}/users`;
export const getLoginLogs = `${adminPrefix}/users/login/logs`;
export const getErrorLogs = `${adminPrefix}/system/errors`;

export const serverStatus = `${adminPrefix}/server/cfs/status`;
export const cfsStatus = (serverId) =>
    `${adminPrefix}/server/cfs/status/${serverId}`;
export const fileServers = `${adminPrefix}/server/cfs/connected`;