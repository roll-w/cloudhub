//import url from '@/store/api' 建议使用此方法调用url
const server = "http://localhost:7010/";

// 认证
const url_login = server +"api/user/login";
const url_register = server +"api/user/register";
const url_getCurrent = server + "api/user/current"
const url_logout = server + "api/user/logout";

//桶管理
const url_addBucket = server + "api/bucket/create";
const url_getBucket =server + "api/bucket/get/all";
const url_deleteBucket =server + "api/bucket/delete";
const url_settingVisibility = server + "api/bucket/setting/visibility";

//管理员:  用户管理
const url_getUser = server + "api/admin/user/get/all"
const url_deleteUserById = server + "api/admin/user/delete"
const url_getUserByName = server + "/api/admin/user/get";

export default {
    url_login,
    url_register,
    url_getCurrent,
    url_logout,
    url_addBucket,
    url_getBucket,
    url_deleteBucket,
    url_settingVisibility,
    url_getUser,
    url_deleteUserById,
    url_getUserByName,
}

