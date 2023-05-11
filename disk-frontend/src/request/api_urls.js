const viteBase = import.meta.env.VITE_BASE_URL;
const httpPrefix = import.meta.env.VITE_HTTP_PREFIX;

export const baseUrl = `${httpPrefix}${viteBase}`;
export const wsBaseUrl = `ws://${viteBase}`;

const prefix = `${baseUrl}/api/v1`;
const adminPrefix = `${baseUrl}/api/v1/admin`;

export const passwordLogin = `${prefix}/user/login/password`;
export const logout = `${prefix}/user/logout`;
export const register = `${prefix}/user/register`;

export const getFiles = (ownerType, ownerId, directory) =>
    `${prefix}/${ownerType}/${ownerId}/disk/folder/${directory}`;
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

export const createFolder = (ownerType, ownerId, directory) =>
    `${prefix}/${ownerType}/${ownerId}/disk/folder/${directory}`;

export const getOperationLogsByResource = (resourceType, resourceId) =>
    `${prefix}/${resourceType}/${resourceId}/operations/logs`;

export const getCurrentUserOperationLogs = `${prefix}/user/operations/logs`;
export const getCurrentUserLoginLogs = `${prefix}/user/login/logs`;
export const getCurrentUserInfo = `${prefix}/user`;

export const getUsers = `${adminPrefix}/users`;
export const getLoginLogs = `${adminPrefix}/users/login/logs`;
export const getErrorLogs = `${adminPrefix}/system/errors`;

