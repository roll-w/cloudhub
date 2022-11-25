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
                      上午好，管理员，请开始一天的工作吧!
                    </h5>
                  </div>
                  <br>
                  <div class="row">
                    <h6 style="color: #909399">
                      今日晴，0℃ - 10℃，天气寒冷，注意添加衣物。
                    </h6>
                  </div>
                  <br>

                  <!--                  <div class="row">-->
                  <!--                    <div class="container">-->
                  <!--                      <div class="row">-->
                  <!--                        <div class="col">-->
                  <!--                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"-->
                  <!--                               class="bi bi-arrow-down-up" viewBox="0 0 16 16" style="color: #66acff">-->
                  <!--                            <path fill-rule="evenodd"-->
                  <!--                                  d="M11.5 15a.5.5 0 0 0 .5-.5V2.707l3.146 3.147a.5.5 0 0 0 .708-.708l-4-4a.5.5 0 0 0-.708 0l-4 4a.5.5 0 1 0 .708.708L11 2.707V14.5a.5.5 0 0 0 .5.5zm-7-14a.5.5 0 0 1 .5.5v11.793l3.146-3.147a.5.5 0 0 1 .708.708l-4 4a.5.5 0 0 1-.708 0l-4-4a.5.5 0 0 1 .708-.708L4 13.293V1.5a.5.5 0 0 1 .5-.5z"/>-->
                  <!--                          </svg>-->
                  <!--                          <span style="font-size: 2px">今日流量(123321)</span>-->
                  <!--                        </div>-->
                  <!--                        <div class="col">-->
                  <!--                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"-->
                  <!--                               class="bi bi-people-fill" viewBox="0 0 16 16" style="color: #66acff">-->
                  <!--                            <path d="M7 14s-1 0-1-1 1-4 5-4 5 3 5 4-1 1-1 1H7zm4-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"/>-->
                  <!--                            <path fill-rule="evenodd"-->
                  <!--                                  d="M5.216 14A2.238 2.238 0 0 1 5 13c0-1.355.68-2.75 1.936-3.72A6.325 6.325 0 0 0 5 9c-4 0-5 3-5 4s1 1 1 1h4.216z"/>-->
                  <!--                            <path d="M4.5 8a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z"/>-->
                  <!--                          </svg>-->
                  <!--                          <span style="font-size: 2px">用户总数(12)</span>-->
                  <!--                        </div>-->
                  <!--                        <div class="col">-->
                  <!--                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"-->
                  <!--                               class="bi bi-chat-dots" viewBox="0 0 16 16" style="color: #66acff">-->
                  <!--                            <path-->
                  <!--                                d="M5 8a1 1 0 1 1-2 0 1 1 0 0 1 2 0zm4 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0zm3 1a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/>-->
                  <!--                            <path-->
                  <!--                                d="m2.165 15.803.02-.004c1.83-.363 2.948-.842 3.468-1.105A9.06 9.06 0 0 0 8 15c4.418 0 8-3.134 8-7s-3.582-7-8-7-8 3.134-8 7c0 1.76.743 3.37 1.97 4.6a10.437 10.437 0 0 1-.524 2.318l-.003.011a10.722 10.722 0 0 1-.244.637c-.079.186.074.394.273.362a21.673 21.673 0 0 0 .693-.125zm.8-3.108a1 1 0 0 0-.287-.801C1.618 10.83 1 9.468 1 8c0-3.192 3.004-6 7-6s7 2.808 7 6c0 3.193-3.004 6-7 6a8.06 8.06 0 0 1-2.088-.272 1 1 0 0 0-.711.074c-.387.196-1.24.57-2.634.893a10.97 10.97 0 0 0 .398-2z"/>-->
                  <!--                          </svg>-->
                  <!--                          <span style="font-size: 2px">好评率(99%)</span>-->
                  <!--                        </div>-->
                  <!--                      </div>-->
                  <!--                    </div>-->
                  <!--                  </div>-->
                  <!--                  <br>-->
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
        实时折线图(待修改)
        <CategoryEchartsBase>

        </CategoryEchartsBase>
      </div>
    </div>
  </ContentBase>
  <br>
  <br>
  <br>
</template>

<script>
import ContentBase from "@/components/ContentBase";
import Img from "@/assets/images/home.png"

export default {
  name: 'HomeView',
  components: {
    ContentBase,
  },

  setup() {
    let bucketNum = 3;
    let objectNum = 6;
    let objectRequest = 10;
    let downServer = 0;
    let runningServer = 3;
    let runTime = 22;

    return {
      Img,
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
.font{
  font-size: x-large;
}
</style>