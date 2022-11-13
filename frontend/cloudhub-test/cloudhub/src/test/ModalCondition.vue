<template>
  <!-- Modal -->
  <div class="modal fade" id="ModalCondition" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">

    <div class="modal-dialog">
      <div class="modal-content" style="width: 900px">
        <!--<div class="modal-header">-->
        <!--<h4 class="modal-title" id="exampleModalLabel">服务器运行状态</h4>-->
        <!--<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>-->
        <!--</div>-->
        <div class="modal-body">
          <h4>CentOS</h4>
          <h5>{{ server.serverIp }}核-{{ server.serverInfo.ram }}GB内存-系统盘 {{
              server.serverInfo.systemDisk
            }}GB
            {{ server.serverInfo.cloudDesk }}-{{ server.serverInfo.address }}</h5>
          <!-- 展示详情信息 -->
          <div class="row">
            <div class="col-sm-6">
              <div class="card">
                <h5 class="card-header">Disk</h5>
                <div class="card-body">


                  <div class="row">
                    <div class="col">
                      <div class="row" style="font-size: 18px">
                        <div class="row">
                          <div class="col">
                            total(MB):
                          </div>
                          <div class="col">
                            <div class="div-right-float">
                              {{ server.diskInfo.totalMB }}
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="row" style="font-size: 18px">
                        <div class="row">
                          <div class="col">
                            used(MB):
                          </div>
                          <div class="col">
                            <div class="div-right-float">
                              {{ server.diskInfo.usedMB }}
                            </div>
                          </div>
                        </div>

                      </div>

                      <div class="row" style="font-size: 18px">
                        <div class="row">
                          <div class="col">
                            total(GB):
                          </div>
                          <div class="col">
                            <div class="div-right-float">
                              {{ parseInt(server.diskInfo.totalMB / 1024) }}
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="row" style="font-size: 18px">
                        <div class="row">
                          <div class="col">
                            used(GB):
                          </div>
                          <div class="col">
                            <div class="div-right-float">
                              {{ parseInt(server.diskInfo.usedMB / 1024) }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>


                    <div class="col">
                      <h1 style="color: red">展示饼状图</h1>
                    </div>
                  </div>


                </div>
              </div>
            </div>

            <div class="col-sm-6">
              <div class="card" style="height: 100%">
                <h5 class="card-header">CPU</h5>
                <div class="card-body">
                  <div class="row" style="height: 50%">

                    <!-- 多核!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
                    <div class="col" style="font-size: 18px">
                      core 1:
                    </div>
                    <div class="col">
                      <h6 style="color: red">展示CPU利用率条形图</h6>
                    </div>
                  </div>
                  <div class="row" style="height: 50%">
                    <div class="col" style="font-size: 18px">
                      core 2:
                    </div>
                    <div class="col">
                      <h6 style="color: red">展示CPU利用率条形图</h6>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <br>
          <div class="row">
            <div class="col-sm-12">
              <div class="card">
                <h5 class="card-header">Docker</h5>
                <div class="card-body" style="text-align: center">
                  <table class="table table-hover">
                    <thead style="color: #909399">
                    <tr>
                      <!-- 参考博客:https://blog.csdn.net/weixin_40482816/article/details/117980908 -->
                      <th scope="col">Container ID</th>
                      <th scope="col">Name</th>
                      <th scope="col">CPU</th>
                      <th scope="col">MEM USAGE / LIMIT</th>
                      <th scope="col">MEM</th>
                      <th scope="col">NET I/O</th>
                      <th scope="col">BLOCK I/O</th>
                      <th scope="col">PIDS</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="docker in server.dockerList" :key="docker.id">
                      <td>{{ docker.id }}</td>
                      <td>{{ docker.name }}</td>
                      <td>{{ (docker.cpuRatio * 100).toFixed(2) + '%' }}</td>
                      <td>{{ docker.memMessage.memUsage + 'MiB / ' + docker.memMessage.memLimit + 'MiB' }}</td>
                      <td>{{ (docker.memRatio * 100).toFixed(2) + '%' }}</td>
                      <td>{{ docker.netIO.netIn + 'B / ' + docker.netIO.netOut + 'B' }}</td>
                      <td>{{ docker.blockIO.blockIn + 'B / ' + docker.blockIO.blockOut }}</td>
                      <td>{{ docker.pids }}</td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
export default {
  name: "ModalCondition",

  props: ['server'],
  setup() {

  },
}
</script>

<style scoped>

</style>