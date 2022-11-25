//import url from '@/store/api' 建议使用此方法调用url
const server = "http://localhost:7010/";

const url_login = server +"api/user/login";
const url_register = server +"api/user/register";
const url_addBucket = "";
// const url_getUserInfo = "";
const url_getBucket = "";
const url_deleteBucket = "";

export default {
    url_login,
    url_register,
    url_addBucket,
    url_getBucket,
    url_deleteBucket
}

