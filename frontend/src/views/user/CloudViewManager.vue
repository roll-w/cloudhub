<template>
  <!-- 项目概述 -->
  <ContentBase>
    <div class="card">
      <div class="card-body">
        <div class="container">
          <div class="row">
            <div class="col-7">
              <div class="container">
                <div class="col">
                  <div class="row">
                    <h5>
                      欢迎，{{ store.state.user.username }}！
                    </h5>
                  </div>
                  <br>
                  <br>
                  <div class="row">
                    <span>项目概述: <a href="https://gitee.com/rollw/cloudhub" class="link-primary">https://gitee.com/rollw/cloudhub</a></span>
                  </div>
                  <br>

                  <div class="row">
                    <span>仓库地址: <a href="https://gitee.com/rollw/cloudhub" class="link-primary">https://gitee.com/rollw/cloudhub</a></span>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-4">
              <img :src="Img" height="300" width="300" class="img-fluid" alt="哎呀，图像好像不见了~">
            </div>
          </div>
        </div>
      </div>
    </div>
  </ContentBase>

  <!-- 客户端基本概述 -->
  <ContentBase>
    <br>
    <br>
    <div class="row">
      <div class="col">
        <div class="row">
          <div class="container">
            <div class="row">
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    桶数量
                    <br>
                    <br>
                    <span class="font">{{ bucketNum }}</span>
                  </div>
                </div>
              </div>
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    对象数量
                    <br>
                    <br>
                    <span class="font">{{ objectNum }}</span>
                  </div>
                </div>
              </div>
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    对象请求数
                    <br>
                    <br>
                    <span class="font">{{ objectRequest }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <br>
        <br>
        <br>
        <div class="row">
          <div class="container">
            <div class="row">
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    运行服务器
                    <br>
                    <br>
                    <span class="font">{{ runningServer }}</span>
                  </div>
                </div>
              </div>
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    宕机服务器
                    <br>
                    <br>
                    <span class="font">{{ downServer }}</span>
                  </div>
                </div>
              </div>
              <div class="col">
                <div class="card">
                  <div class="card-body">
                    系统启动时间
                    <br>
                    <br>
                    <span class="font">{{ runTime + 's' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col">

        <NetWorkFlow style="width: 600px;height: 350px">

        </NetWorkFlow>

      </div>
    </div>
  </ContentBase>

</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import Img from "@/assets/images/home.png"
import NetWorkFlow from "@/components/echarts/network/NetWorkFlow";
import {ref} from "vue";
import $ from "jquery"
import url from "@/store/api";
import {useStore} from "vuex";

export default {
  name: 'HomeView',
  components: {
    ContentBase,
    NetWorkFlow
  },

  setup() {
    const store = useStore()
    const bucketNum = ref();
    const objectNum = ref();
    const objectRequest = ref();
    const downServer = ref();
    const runningServer = ref();
    const runTime = ref();


    const fetchBucketNum = () => {
      $.ajax({
        url: url.url_getBucketCount,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            bucketNum.value = resp.data
          }
        },
        error() {
        }
      });
    }

    const fetchObjectNum = () => {
      $.ajax({
        url: url.url_getObjectCount,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            objectNum.value = resp.data
          }
        },
        error() {
        }
      });
    }

    const fetchRequest = () => {
      $.ajax({
        url: url.url_getRequestCount,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            objectRequest.value = resp.data
          }
        },
        error() {
        }
      });
    }

    const fetchServers = () => {
      $.ajax({
        url: url.url_getServer,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            runningServer.value = resp.data.activeServers.length
            downServer.value = resp.data.deadServers.length
          }
        },
        error() {
        }
      });
    }

    const fetchRunTime = () => {
      $.ajax({
        url: url.url_getRunTime,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode === "00000") {
            runTime.value = resp.data
          }
        },
        error() {
        }
      });
    }

    fetchBucketNum()
    fetchObjectNum()
    fetchRequest()
    fetchServers()
    fetchRunTime()


    return {
      Img,
      store,
      bucketNum,
      objectNum,
      objectRequest,
      downServer,
      runningServer,
      runTime
    }
  }
}
</script>

<style scoped>
.font {
  font-size: x-large;
}
</style>