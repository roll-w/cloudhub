//import url from '@/store/api' 建议使用此方法调用url
const server = "http://localhost:7010";

// 认证
const url_login = server + "/api/user/login";
const url_register = server + "/api/user/register";
const url_getCurrent = server + "/api/user/current"
const url_logout = server + "/api/user/logout";

//桶管理
const url_addBucket = server + "/api/bucket/create";
const url_getBucket = server + "/api/bucket/get/all";
const url_getBucketByName = server + "/api/bucket/get"
const url_deleteBucket = server + "/api/bucket/delete";
const url_settingVisibility = server + "/api/bucket/setting/visibility";

const url_getBucketCount = server + "/api/admin/bucket/get/size";
const url_adminAddBucket = server + "/api/admin/bucket/create";
const url_adminGetBucket = server + "/api/admin/bucket/get/all";
const url_adminGetBucketByName = server + "/api/admin/bucket/get"
const url_adminDeleteBucket = server + "/api/admin/bucket/delete";
const url_adminSettingVisibility = server + "/api/admin/bucket/setting/visibility";
//管理员:  用户管理
const url_getUser = server + "/api/admin/user/get/all"
const url_deleteUserById = server + "/api/admin/user/delete"
const url_getUserByName = server + "/api/admin/user/get";


//文件
const url_getObjectByBucketName = server + "/api/object/get";
const url_getObjectDetail = server + "/api/object/get/detail";
const url_getObjectMetadata = server + "/api/object/metadata/get";
const url_getVersionedObjects = server + "/api/object/version/get";
const url_objectV1 = server + "/api/object/v1"

const url_adminGetObjectByBucketName = server + "/api/admin/object/get";
const url_adminGetObjectDetail = server + "/api/admin/object/get/detail";
const url_adminGetObjectMetadata = server + "/api/admin/object/metadata/get";
const url_adminGetVersionedObjects = server + "/api/admin/object/version/get";
const url_adminObjectV1 = server + "/api/admin/object/v1"

//元数据服务器信息serverId=meta
const url_metaServer = server + "/api/admin/status/server/get"
const url_servers = server + "/api/admin/status/connected"

export default {
    url_login,
    url_register,
    url_getCurrent,
    url_logout,
    url_addBucket,
    url_getBucket,
    url_getBucketByName,
    url_deleteBucket,
    url_getBucketCount,
    url_adminAddBucket,
    url_adminGetBucket,
    url_adminGetBucketByName,
    url_adminDeleteBucket,
    url_adminSettingVisibility,
    url_settingVisibility,
    url_getObjectDetail,
    url_adminGetObjectDetail,
    url_adminGetObjectByBucketName,
    url_adminObjectV1,
    url_getObjectMetadata,
    url_adminGetObjectMetadata,
    url_getUser,
    url_deleteUserById,
    url_getUserByName,
    url_getObjectByBucketName,
    url_objectV1,
    url_adminGetVersionedObjects,
    url_getVersionedObjects,
    url_metaServer,
    url_servers,
}

