const base = 'localhost:7015';

const frontBase = 'localhost:7016';

export const frontBaseUrl = `http://${frontBase}`;

export const baseUrl = `http://${base}`;
export const wsBaseUrl = `ws://${base}`;

const prefix = `${baseUrl}/api/v1`;
const adminPrefix = `${baseUrl}/api/v1/admin`;

export const passwordLogin = `${prefix}/user/login/password`;
export const logout = `${prefix}/user/logout`;
export const register = `${prefix}/user/register`;

export const getFiles = (ownerType, ownerId, directory) =>
    `${prefix}/${ownerType}/${ownerId}/disk/directory/${directory}`;

