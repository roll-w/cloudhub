<template>
  <ContentBase>

    <!--// TODO: 创建modal  -->
    <h3 class="mb-4">桶信息</h3>
    <div class="d-flex flex-xl-row flex-grow-1">
      <button type="button" class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#addBucket">
        创建桶
      </button>
      <div class="d-flex flex-fill justify-content-end">
        <form @submit.prevent="getBucketByName">
          <div class="input-group">
            <input v-model="name" type="text" class="form-control"
                   placeholder="搜索桶......">
            <button class="btn btn-outline-primary" type="submit">查询</button>
          </div>
        </form>
      </div>

    </div>
    <hr>
    <table class="table table-hover" style="text-align: center">
      <thead class="table-light">
      <tr>
        <th scope="col">名称</th>
        <th scope="col">策略</th>
        <th scope="col">详情</th>
        <th scope="col">编辑</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="bucket in buckets" :key="bucket.name">
        <td>{{ bucket.name }}</td>
        <td>{{ bucket.bucketVisibility }}</td>
        <td>
          <div class="btn-group" role="group">
            <button type="button" class="btn btn-link" @click="checkFile">查看</button>
          </div>
        </td>
        <td>
          <div class="btn-group" role="group">
            <button type="button" class="btn btn-link" data-bs-toggle="modal"
                    :data-cfs-bucket-name="bucket.name"
                    data-bs-target="#bucketAuthority">
              权限
            </button>
          </div>
          <div class="btn-group" role="group">
            <button type="button" class="btn btn-link" @click="deleteBucket(bucket)">删除</button>
          </div>
        </td>
      </tr>

      </tbody>
    </table>
    <div class="modal fade" id="addBucket" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">创建</h5>
          </div>
          <div class="modal-body">

            <form @submit.prevent="addBucket">
              <div class="mb-3">
                <label for="name" class="form-label">名称:</label>
                <input type="text" v-model="bucketName" class="form-control" id="name" placeholder="Name">
              </div>
              <div class="pb-3 form-floating">
              <select class="form-select" id="bucket-vis-select" v-model="visibility">
                <option value="PRIVATE">私有读写</option>
                <option value="PUBLIC_READ">公共读</option>
                <option value="PUBLIC_READ_WRITE">公共读写</option>
              </select>
              <label for="name" class="form-label">设置新的桶策略：</label>
              </div>
              <div class="modal-footer">
                <button style="margin-right: 5px" type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消
                </button>
                <button type="submit" class="btn btn-primary">添加</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" ref="bucketVisibilityModel" id="bucketAuthority"
         tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">修改桶权限</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">

            <form @submit.prevent="settingVisibility">
              <div class="pb-3 form-floating">
                <input type="text" v-model="bucketName" hidden>
                <select class="form-select" id="bucket-vis-select" v-model="visibility">
                  <option value="PRIVATE">私有读写</option>
                  <option value="PUBLIC_READ">公共读</option>
                  <option value="PUBLIC_READ_WRITE">公共读写</option>
                </select>
                <label for="name" class="form-label">设置新的桶策略：</label>
              </div>
              <div class="modal-footer">
                <div class="btn-group">
                  <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">取消</button>
                  <button type="submit" class="btn btn-primary">确认</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import {Modal} from 'bootstrap/dist/js/bootstrap'
import {useRouter} from 'vue-router'
import {onMounted, ref} from 'vue';
import $ from 'jquery'
import url from '@/store/api'

export default {
  name: "BucketView",
  components: {
    ContentBase,
  },

  setup() {
    //接口
    const buckets = ref([]);
    const bucketName = ref([]);
    const visibility = ref([]);
    const name = ref([]);
    const router = useRouter()
    const bucketVisibilityModel = ref(null)

    onMounted(() => {
      bucketVisibilityModel.value.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget
        let readBucketName = button.getAttribute("data-cfs-bucket-name")
        bucketName.value = readBucketName
      })
    });

    // const getRadioVal =(event) => {
    //   let radioVal = event.target.value;
    //   val.value = radioVal
    // }

      const checkFile = () => {
      router.push('file')
      //获取指定桶的文件信息
      $.ajax({
        url: '',
        type: "",
        success() {
          //  file = resp
        },
        error(resp) {
          console.log(resp)
        }
      });
    };

    //TODO:获得桶列表
    const getBucket = () => {
      $.ajax({
        url: url.url_getBucket,
        type: "GET",
        xhrFields: {
          withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain: true,
        success(resp) {
          buckets.value = resp.data;
        },
        error(resp) {
          console.log(resp)
        }
      });
    };

    getBucket();

    //TODO：删除某个桶
    const deleteBucket = (bucket) => {
      $.ajax({
        url: url.url_deleteBucket,
        xhrFields: {
          withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain: true,
        type: "DELETE",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
          bucketName: bucket.name,
        }),
        success(resp) {
          if (resp.errorCode === "00000") {
            getBucket();
          }
        },
        error(resp) {
          console.log(resp);
        }
      })
    };

    //TODO：创建桶
    const addBucket = () => {
      $.ajax({
        url: url.url_addBucket,
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
            Modal.getInstance("#addBucket").hide();
            bucketName.value=null;
            visibility.value=null;
            getBucket()
          }
        },
        error(resp) {
          console.log(resp)
        }
      });
    };

    const settingVisibility = () => {
      $.ajax({
        url: url.url_settingVisibility,
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
            Modal.getInstance("#bucketAuthority").hide();
            getBucket()
          }
        },
        error(resp) {
          console.log(resp);
        }
      });
    };

    const getBucketByName = () => {
      $.ajax({
        url: url.url_getBucketByName,
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
            name.value =null;
            alert("Name: " + resp.data.name + " Email: " + resp.data.bucketVisibility)
          }
        },
        error(resp) {
          console.log(resp.data);
        }
      });
    }


    return {
      buckets,
      name,
      bucketName,
      visibility,
      bucketVisibilityModel,
      checkFile,
      getBucketByName,
      deleteBucket,
      addBucket,
      settingVisibility,
    }
  }
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
