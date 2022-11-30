<template>
  <ContentBase>
    <div class="mb-4 ms-2 d-flex flex-grow-1 flex-fill">
      <h3>{{ objectName }}</h3>
      <div class="d-flex flex-fill justify-content-end">
        <button type="button" class="btn btn-outline-primary ms-2" @click="back">返回对象列表</button>
      </div>
    </div>
    <hr class="p-3">
    <h4 class="mb-2 ms-2">对象信息</h4>
    <hr>
    <div class="row p-2 mb-3">
      <div class="row">
        <div class="col">对象名称</div>
        <div class="col">{{ objectName }}</div>
      </div>
      <div class="row">
        <div class="col">从属桶</div>
        <div class="col">{{ bucketName }}</div>
      </div>
      <div class="row">
        <div class="col">对象大小</div>
        <div class="col">{{ object.objectSize }}</div>
      </div>
      <div class="row">
        <div class="col">创建时间</div>
        <div class="col">{{ convertTimestamp(object.createTime) }}</div>
      </div>
    </div>
    <ul class="nav nav-tabs" id="infoTab">
      <li class="nav-item">
        <button class="nav-link active" id="meta-tab"
                data-bs-toggle="tab" data-bs-target="#meta-tab-pane"
                type="button" role="tab" aria-controls="meta-tab-pane"
                aria-selected="true">元数据信息
        </button>
      </li>
      <li class="nav-item">
        <button class="nav-link" id="version-tab"
                data-bs-toggle="tab" data-bs-target="#version-tab-pane"
                type="button" role="tab" aria-controls="version-tab-pane"
                aria-selected="true">版本信息
        </button>
      </li>
    </ul>
    <div class="tab-content mt-3">
      <div class="tab-pane fade show active" id="meta-tab-pane">
        <div class="d-flex flex-grow-1 w-100 flex-fill">
          <h4 class="mb-2 ms-2">元数据信息</h4>
          <div class="d-flex flex-fill justify-content-end">
            <button class="btn btn-outline-primary align-self-end">添加元数据</button>
          </div>
        </div>
        <hr>
        <table class="table table-hover text-center">
          <thead class="table-light">
          <tr>
            <th scope="col">名称</th>
            <th scope="col">值</th>
            <th scope="col">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="meta in metadata" :key="meta.name">
            <th scope="row">{{ meta.name }}</th>
            <th scope="row">{{ meta.value }}</th>
            <th>
              <div class="d-grid gap-2 d-md-flex justify-content-center" role="group">
                <button type="button" class="btn btn-link text-decoration-none">修改</button>
                <button type="button" class="btn btn-link text-decoration-none">移除</button>
              </div>
            </th>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="tab-pane fade" id="version-tab-pane">
        <div class="d-flex flex-grow-1 w-100 flex-fill">
          <h4 class="mb-2 ms-2">版本信息</h4>
        </div>
        <hr>
        <table class="table table-hover text-center">
          <thead class="table-light">
          <tr>
            <th scope="col">版本号</th>
            <th scope="col">创建时间</th>
            <th scope="col">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="object in versioned" :key="object.version">
            <th scope="row">{{ object.version }}</th>
            <th scope="row">{{ convertTimestamp(object.lastModified) }}</th>
            <th>
              <div class="d-grid gap-2 d-md-flex justify-content-center" role="group">
                <button type="button" class="btn btn-link text-decoration-none">回退</button>
                <button type="button" class="btn btn-link text-decoration-none">移除</button>
              </div>
            </th>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import {useRouter} from "vue-router";
import {ref} from "vue";
import {convertTimestamp} from "@/util/time";
import $ from "jquery";
import url from "@/store/api";

export default {
  name: "ObjectInfoView",
  components: {ContentBase},
  setup() {
    const router = useRouter()
    const isAdminPage = () => {
      return router.currentRoute.value.name === "object_admin_info";
    }

    const objectName = router.currentRoute.value.params.object
    const bucketName = router.currentRoute.value.params.bucket
    const object = ref({})
    const metadata = ref({})
    const versioned = ref({})
    const getObjectInfo = () => {
      let realUrl = isAdminPage()
          ? url.url_adminGetObjectDetail
          : url.url_getObjectDetail
      $.ajax({
        url: realUrl,
        type: "get",
        data: {
          bucketName: bucketName,
          objectName: objectName
        },
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            object.value = resp.data
          }
        },
        error() {
        }
      })
    }

    const getMetadata = () => {
      let realUrl = isAdminPage()
          ? url.url_adminGetObjectMetadata
          : url.url_getObjectMetadata
      $.ajax({
        url: realUrl,
        type: "get",
        data: {
          bucketName: bucketName,
          objectName: objectName
        },
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            metadata.value = Object.entries(resp.data)
                .map(d => ({
                  name: d[0],
                  value: d[1]
                }))
          }
        },
        error() {
        }
      })
    }

    const getVersioned = () => {
      let realUrl = isAdminPage()
          ? url.url_adminGetVersionedObjects
          : url.url_getVersionedObjects
      $.ajax({
        url: realUrl,
        type: "get",
        data: {
          bucketName: bucketName,
          objectName: objectName
        },
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            versioned.value = resp.data
          }
        },
        error() {
        }
      })
    }

    getMetadata()
    getObjectInfo()
    getVersioned()

    const back = () => {
      if (isAdminPage()) {
        router.push({
          name: "object_admin_list", params: {
            bucket: bucketName
          }
        })
        return
      }
      router.push({
        name: "object_list", params: {
          bucket: bucketName
        }
      })
    }


    return {
      objectName,
      bucketName,
      metadata,
      object,
      versioned,
      convertTimestamp,
      back
    }
  }
}

</script>