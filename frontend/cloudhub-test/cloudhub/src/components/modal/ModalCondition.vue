<template>
  <!-- Modal -->
  <div class="modal fade" id="ModalCondition" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content" style="width:900px">
        <!--<div class="modal-header">-->
        <!--<h4 class="modal-title" id="exampleModalLabel">服务器运行状态</h4>-->
        <!--<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>-->
        <!--</div>-->
        <div class="modal-body">
          <div class="row">
            <div class="col">
              <div class="card" style="height: 180px">
                <ul class="list-group list-group-flush">
                  <li class="list-group-item">
                    <span class="leftspan">Runtime</span>
                  </li>
                  <li class="list-group-item">
                    <div class="col">
                      <div class="row">
                        <div class="row">
                          <div class="col">
                            <span class="leftspan">OS:</span>
                          </div>
                          <div class="col">
                            <span class="leftspan">Linux</span>
                          </div>
                        </div>
                      </div>
                      <div class="row">

                        <div class="row">
                          <div class="col">
                            <span class="leftspan">cpu nums:</span>
                          </div>
                          <div class="col">
                            <span class="leftspan"> {{ server.serverInfo.coreNum }}</span>

                          </div>
                        </div>

                      </div>
                      <div class="row">

                        <div class="row">
                          <div class="col">
                            <span class="leftspan">ram:</span>
                          </div>
                          <div class="col">
                            <span class="leftspan">{{ server.serverInfo.ram }}</span>
                          </div>
                        </div>

                      </div>
                      <div class="row">

                        <div class="row">
                          <div class="col">
                            <span class="leftspan">systemdisk:</span>
                          </div>
                          <div class="col">
                            <span class="leftspan">{{ server.serverInfo.systemDisk }}</span>
                          </div>
                        </div>

                      </div>
                      <div class="row">

                        <div class="row">
                          <div class="col">
                            <span class="leftspan">clouddesk:</span>
                          </div>
                          <div class="col">
                            <span class="leftspan">{{ server.serverInfo.cloudDesk }}</span>
                          </div>
                        </div>

                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="col">
              <div class="card" style="height: 180px">
                <ul class="list-group list-group-flush">
                  <li class="list-group-item">
                    <span class="leftspan">Disk</span>
                  </li>
                  <li class="list-group-item">
                    <div class="col">
                      <div class="row">
                        <div class="col">
                          <span class="leftspan">total(MB):</span>
                        </div>
                        <div class="col">
                          <span class="leftspan">{{ server.diskInfo.totalMB }}</span>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col">
                          <span class="leftspan">used(MB):</span>
                        </div>
                        <div class="col">
                          <span class="leftspan">{{ server.diskInfo.usedMB }}</span>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col">
                          <span class="leftspan">total(GB):</span>
                        </div>
                        <div class="col">
                          <span class="leftspan">{{ parseInt(server.diskInfo.totalMB / 1024) }}</span>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col">
                          <span class="leftspan">used(GB):</span>
                        </div>
                        <div class="col">
                          <span class="leftspan">{{ parseInt(server.diskInfo.usedMB / 1024) }}</span>
                        </div>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="col">
              <div class="card" style="height: 180px">
                <ul class="list-group list-group-flush">
                  <li class="list-group-item">
                    <span class="leftspan">CPU</span>
                  </li>
                  <li class="list-group-item">
                    <div class="col">
                      <div v-for="core in server.coreInfo" :key="core.coreId" class="row">
                        <div class="col">
                          <span class="leftspan">{{ "core " + core.coreId + ":" }}</span>
                        </div>
                        <div class="col">
                          <span class="leftspan">{{ (core.coreUsage * 100).toFixed(2) + '%' }}</span>
                        </div>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
          <br>
          <div class="container">
            <div class="row">
              <div class="card">
                <h5 class="card-header">
                  <span class="leftspan">Docker</span>
                </h5>
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
                      <td>{{ docker.blockIO.blockIn + 'B / ' + docker.blockIO.blockOut +'B'}}</td>
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

.leftspan{
  float: left;
}


</style>