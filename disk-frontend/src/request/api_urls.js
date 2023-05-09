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
    `${prefix}/${ownerType}/${ownerId}/disk/directory/${directory}`;
export const getStorageInfo = (ownerType, ownerId, storageType, storageId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${storageId}/info`;
export const getStorageAttributes = (ownerType, ownerId, storageType, storageId) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${storageId}/tags`;
export const getStorageVersions = (ownerType, ownerId, storageType, id) =>
    `${prefix}/${ownerType}/${ownerId}/disk/${storageType}/${id}/versions`;

export const getOperationLogsByResource = (resourceType, resourceId) =>
    `${prefix}/${resourceType}/${resourceId}/operations/logs`;

export const getCurrentUserOperationLog = `${prefix}/user/operations/logs`;

export const getUsers = `${adminPrefix}/users`;
export const getErrorLogs = `${adminPrefix}/system/errors`;

