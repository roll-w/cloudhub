<template>
    <ContentBase>
        <h3 class="mb-4">{{ ifAdmin() ? "管理员桶管理" : "桶信息" }}</h3>
        <div class="d-flex flex-xl-row flex-grow-1">
            <button class="btn btn-outline-primary" data-bs-target="#addBucket" data-bs-toggle="modal" type="button">
                创建桶
            </button>
            <div class="d-flex flex-fill justify-content-end">
                <button class="btn btn-outline-primary" data-bs-target="#BucketSingleMessage"
                        data-bs-toggle="modal"
                        type="button">
                    查询
                </button>
            </div>
        </div>

        <div id="BucketSingleMessage" ref="UserSingleMessage" class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">查询桶信息</h5>
                    </div>
                    <div class="modal-body">
                        <form @submit.prevent="getBucketByName">
                            <div class="input-group">
                                <input v-model="name" class="form-control" placeholder="请输入查询桶的名称"
                                       type="text">
                                <button class="btn btn-outline-primary" type="submit">查询</button>
                            </div>
                        </form>
                    </div>
                    <hr>
                    <div v-if="SingleBucket.name!=null" class="modal-body">
                        <h5 style="text-align: center">
                            桶信息如下
                        </h5>
                        <h6> 桶名称:{{ SingleBucket.name }}</h6>
                        <br>
                        <h6>桶权限:{{ SingleBucket.visibility }}</h6>
                    </div>
                    <div v-else>
                        <h5 style="text-align: center">
                            桶信息如下
                        </h5>
                        <h6 style="text-align: center">
                            待查询....
                        </h6>
                    </div>
                    <div class="modal-footer">
                        <div class="btn-group">
                            <button class="btn btn-outline-secondary" data-bs-dismiss="modal" type="button">
                                关闭
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <hr>
        <table class="table table-hover text-center">
            <thead class="table-light">
            <tr>
                <th scope="col">名称</th>
                <th scope="col">策略</th>
                <th scope="col">详情</th>
                <th scope="col">操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="bucket in buckets" :key="bucket.name">
                <td>{{ bucket.name }}</td>
                <td>{{ convertBucketVis(bucket.bucketVisibility) }}</td>
                <td>
                    <div class="btn-group" role="group">
                        <button class="btn btn-link" type="button" @click="checkFile(bucket)">查看</button>
                    </div>

                </td>
                <td>
                    <div class="btn-group" role="group">
                        <button :data-cfs-bucket-name="bucket.name" class="btn btn-link" data-bs-target="#bucketAuthority"
                                data-bs-toggle="modal"
                                type="button">
                            权限
                        </button>
                    </div>
                    <div class="btn-group" role="group">
                        <button :data-cfs-bucket-name="bucket.name" class="btn btn-link" data-bs-target="#bucketDeleteConfirm"
                                data-bs-toggle="modal" type="button">
                            删除
                        </button>
                    </div>
                </td>
            </tr>

            </tbody>
        </table>
        <div id="addBucket" aria-hidden="true" aria-labelledby="exampleModalLabel" class="modal fade" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 id="exampleModalLabel" class="modal-title">创建</h5>
                    </div>
                    <div class="modal-body">
                        <form @submit.prevent="prevent">
                            <div class="mb-3">
                                <div class="alert alert-primary" role="alert">
                                    桶名称只能包含英文字母、数字和连接符（-）。
                                    <br>
                                    且英文字母不区分大小写，不可与已经存在的桶重名。
                                </div>
                                <label class="form-label" for="name">名称:</label>
                                <input id="name" v-model="bucketName" class="form-control" placeholder="桶名称"
                                       type="text">
                            </div>
                            <div class="pb-3 form-floating">
                                <select id="bucket-vis-select" v-model="visibility" class="form-select">
                                    <option value="PRIVATE">私有读写</option>
                                    <option value="PUBLIC_READ">公共读</option>
                                    <option value="PUBLIC_READ_WRITE">公共读写</option>
                                </select>
                                <label class="form-label" for="name">设置桶策略：</label>
                            </div>
                            <div class="modal-footer">
                                <div class="btn-group">
                                    <button class="btn btn-outline-secondary" data-bs-dismiss="modal" type="button">
                                        取消
                                    </button>
                                    <button class="btn btn-primary" data-bs-dismiss="modal" type="submit"
                                            @click="addBucket">添加
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div id="bucketDeleteConfirm" ref="bucketDeleteConfirm" class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">是否确认删除</h5>
                    </div>
                    <div class="modal-body">
                        <div>是否确认删除桶：{{ bucketName }}</div>
                    </div>
                    <div class="modal-footer">
                        <div class="btn-group">
                            <button class="btn btn-outline-secondary" data-bs-dismiss="modal" type="button">
                                取消
                            </button>
                            <button class="btn btn-outline-danger" data-bs-dismiss="modal" type="submit"
                                    @click="deleteBucketConfirm">
                                确认
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="bucketAuthority" ref="bucketVisibilityModel" aria-hidden="true"
             class="modal fade" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">修改桶权限</h5>
                        <button aria-label="Close" class="btn-close" data-bs-dismiss="modal" type="button"></button>
                    </div>
                    <div class="modal-body">

                        <form @submit.prevent="prevent">
                            <div class="pb-3 form-floating">
                                <input v-model="bucketName" hidden type="text">
                                <select id="bucket-vis-select" v-model="visibility" class="form-select">
                                    <option value="PRIVATE">私有读写</option>
                                    <option value="PUBLIC_READ">公共读</option>
                                    <option value="PUBLIC_READ_WRITE">公共读写</option>
                                </select>
                                <label class="form-label" for="name">设置新的桶策略：</label>
                            </div>
                            <div class="modal-footer">
                                <div class="btn-group">
                                    <button class="btn btn-outline-secondary" data-bs-dismiss="modal" type="button">
                                        取消
                                    </button>
                                    <button class="btn btn-primary" data-bs-dismiss="modal" type="submit"
                                            @click="settingVisibility">
                                        确认
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

    </ContentBase>
</template>

<script setup>
import ContentBase from "@/components/common/ContentBase";
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import $ from "jquery";
import url from "@/store/api";

const buckets = ref([]);
const bucketName = ref([]);
const visibility = ref([]);
const name = ref([]);
const router = useRouter();
const bucketVisibilityModel = ref(null);
const bucketDeleteConfirm = ref(null);

const SingleBucket = reactive({
    name: null,
    visibility: null,
})

const ifAdmin = () => {
    return router.currentRoute.value.name === "bucket_admin_index";
}


onMounted(() => {
    bucketVisibilityModel.value.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget
        bucketName.value = button.getAttribute("data-cfs-bucket-name")
    })
    bucketDeleteConfirm.value.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget
        bucketName.value = button.getAttribute("data-cfs-bucket-name")
    })
});

const checkFile = (bucket) => {
    const name = ifAdmin()
        ? 'object_admin_list'
        : 'object_list'

    router.push({
        name: name,
        params: {
            bucket: bucket.name
        }
    })
};

const getBucket = () => {
    const request = ifAdmin() ?
        url.url_adminGetBucket :
        url.url_getBucket;
    $.ajax({
        url: request,
        type: "GET",
        xhrFields: {
            withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain: true,
        success(resp) {
            buckets.value = resp.data
        },
        error(resp) {
            window.$message({
                type: "danger",
                content: resp.responseJSON.message
            })
            console.log(resp)
        }
    });
};

getBucket();

const deleteBucketConfirm = () => {
    deleteBucket(bucketName.value)
}

const deleteBucket = (bucketName) => {
    const request = ifAdmin() ?
        url.url_adminDeleteBucket :
        url.url_deleteBucket;

    $.ajax({
        url: request,
        xhrFields: {
            withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain: true,
        type: "DELETE",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            bucketName: bucketName,
        }),
        success(resp) {
            if (resp.errorCode === "00000") {
                getBucket();
            }
            window.$message({
                type: "danger",
                content: resp.message
            })
            bucketName.value = null
        },
        error(resp) {
            console.log(resp);
            window.$message({
                type: "danger",
                content: resp.responseJSON.message
            })
            bucketName.value = null
        }
    })
};

const addBucket = () => {
    const request = ifAdmin() ?
        url.url_adminAddBucket :
        url.url_addBucket;

    $.ajax({
        url: request,
        type: "PUT",
        xhrFields: {
            withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain: true,
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            bucketName: bucketName.value,
            visibility: visibility.value,
        }),
        success(resp) {
            if (resp.errorCode === "00000") {
                bucketName.value = null;
                visibility.value = null;
                getBucket()
            }
            window.$message({
                type: "danger",
                content: resp.message
            })
        },
        error(resp) {
            window.$message({
                type: "danger",
                content: resp.responseJSON.message
            })
            console.log(resp)
        }
    });
};

const convertBucketVis = (name) => {
    switch (name) {
        case "PUBLIC_READ" :
            return "公共读"
        case "PUBLIC_READ_WRITE" :
            return "公共读写"
        case "PRIVATE" :
            return "私有读写"
    }
    return name
}

const prevent = () => {
}

const settingVisibility = () => {
    const request = ifAdmin() ?
        url.url_adminSettingVisibility :
        url.url_settingVisibility;

    $.ajax({
        url: request,
        type: "POST",
        xhrFields: {
            withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain: true,
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            bucketName: bucketName.value,
            visibility: visibility.value,
        }),
        success(resp) {
            if (resp.errorCode === "00000") {
                getBucket()
            }
            window.$message({
                type: "danger",
                content: resp.message
            })
        },
        error(resp) {
            window.$message({
                type: "danger",
                content: resp.responseJSON.message
            })
            console.log(resp);
        }
    });
};

const getBucketByName = () => {
    const request = ifAdmin() ?
        url.url_adminGetBucketByName :
        url.url_getBucketByName;

    $.ajax({
        url: request,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        type: "GET",
        data: {
            bucketName: name.value,
        },
        success(resp) {
            if (resp.errorCode === "00000") {
                name.value = null;
                SingleBucket.name = resp.data.name;
                SingleBucket.visibility = resp.data.bucketVisibility;
            }
        },
        error(resp) {
            console.log(resp.data);
        }
    });
}
</script>

<style scoped>
button {
    /*style="font-size: large; font-weight: bolder"*/
    font-size: large;
    font-weight: normal;
    text-decoration: none;
}
</style>
