//import url from '@/store/api' 建议使用此方法调用url
const server = "http://localhost:7010/";

const url_login = server +"api/user/login";
const url_register = server +"api/user/register";
const url_addBucket = "api/bucket/create";
// const url_getUserInfo = "";
const url_getBucket =server + "api/bucket/get/all";
const url_deleteBucket =server + "api/bucket/delete";

export default {
    url_login,
    url_register,
    url_addBucket,
    url_getBucket,
    url_deleteBucket
}

