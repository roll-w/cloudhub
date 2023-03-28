const base = 'localhost:7015';

const frontBase = 'localhost:7016';

export const frontBaseUrl = `http://${frontBase}`;

export const baseUrl = `http://${base}`;
export const wsBaseUrl = `ws://${base}`;

const prefix = `${baseUrl}/api/v1`;
const adminPrefix = `${baseUrl}/api/v1/admin`;

const passwordLogin = `${prefix}/user/login/password`;
const logout = `${prefix}/user/logout`;
const register = `${prefix}/user/register`;

export default {
    passwordLogin,
    logout,
    register
}
