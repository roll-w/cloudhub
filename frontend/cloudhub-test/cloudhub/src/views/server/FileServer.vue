<template>
  <ContentBase>
    <div class="card">
      <div class="card-body">
        <button type="button" class="btn btn-outline-primary">文件服务器列表</button>
        <hr>
        <table class="table table-hover">
          <thead class="table-light">
          <tr>
            <th scope="col">服务器ID</th>
            <th scope="col">服务器IP地址</th>
            <th scope="col">详情信息</th>
          </tr>
          </thead>

          <tbody>
          <tr v-for="server in servers" :key="server.serverId">
            <th scope="row">{{ server.serverId }}</th>
            <th scope="row">{{ server.serverIp }}</th>
            <th scope="row">
              <div class="d-grid gap-2 d-md-flex justify-content-center">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" :data-bs-target="'#ModalCondition' + server.serverId">
                  运行状态
                </button>

                <div class="modal fade" :id="'ModalCondition' + server.serverId" tabindex="-1" >
                  <div class="modal-dialog">
                    <div class="modal-content" style="width:900px">

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

                <button type="button" class="btn btn-primary" data-bs-toggle="modal" :data-bs-target="'#ModalNode' + server.serverId">
                  节点信息
                </button>

                <div class="modal fade" :id="'ModalNode' + server.serverId" tabindex="-1" >
                  <div class="modal-dialog">
                    <div class="modal-content" >
                      <div class="modal-body">
                        <table class="table table-hover">
                          <thead style="color: #909399">
                          <tr>
                            <th scope="col">node</th>
                            <th scope="col">state</th>
                            <th scope="col">load</th>
                            <th scope="col">phymem</th>
                            <th scope="col">ncpus</th>
                            <th scope="col">allmem</th>
                            <th scope="col">resi</th>
                            <th scope="col">usrs</th>
                            <th scope="col">tasks</th>
                            <th scope="col">jobidlist</th>
                          </tr>
                          </thead>
                          <tbody>
                          <tr v-for="node in server.nodeList" :key="node.id">
                            <td>{{ node.name }}</td>
                            <td>{{ node.state }}</td>
                            <td>{{ node.load  }}</td>
                            <td>{{ node.phymem }}</td>
                            <td>{{ node.ncpus }}</td>
                            <td>{{ node.allmem }}</td>
                            <td>{{ node.resi }}</td>
                            <td>{{ node.usrs }}</td>
                            <td>{{ node.tasks }}</td>
                            <td>{{ node.joblist }}</td>
                          </tr>
                          </tbody>
                        </table>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                      </div>
                    </div>
                  </div>
                </div>

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
import ContentBase from "@/components/ContentBase";
import {ref} from "vue";

export default {
  name: "ServerView",
  components: {
    ContentBase,
  },
  setup() {
    //  所有服务器的信息(响应式数据)
    const servers = ref([

      /*
       * 第一台服务器
       */

      {
        // 服务器最基本的信息
        serverId: "1", // 服务器ID
        serverIp: "192.168.10.101", // 服务器IP地址


        // 服务器运行信息
        serverInfo: {
          coreNum: 2, // 服务器核数
          ram: 4, // 服务器总内存（GB）
          systemDisk: 100, // 系统盘容量（GB）（与下述"totalMB"相对应）
          cloudDesk: "ESSD", // 阿里云云盘: ESSD或SSD
          address: "河南", // 服务器所在地
        },

        // 服务器磁盘使用信息
        diskInfo: {
          // 磁盘信息
          totalMB: 100 * 1024, // 总容量（MB）（与上述“systemDisk”相对应）
          usedMB: 12345,  // 已使用容量（MB）
        },

        // 展示每个CPU的利用率，列表长度为服务器的核数
        coreInfo:[
          {
            coreId: "one",
            coreUsage:0.1234,
          },
          {
            coreId: "two",
            coreUsage: 0.4567,
          },
        ],

        // 一个服务器中可以有多个容器: 下述为某个服务器的容器列表
        dockerList: [
          {
            id: "1", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量（单位: 字节B）
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量（单位: 字节B）
            pids: 5 // PIDS：容器创建的进程或线程数
          },
          {
            id: "2", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 0.888, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 456, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 1, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 6 // PIDS：容器创建的进程或线程数
          },
          {
            id: "3", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.234, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 0, blockOut: 2},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 8 // PIDS：容器创建的进程或线程数
          },
        ],



        // 参考博客: https://blog.csdn.net/qq_36949278/article/details/115455917

        // 服务器节点信息
        nodeList:[
          {
            id:"1",
            name: 'node1',
            state: 'busy',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
          {
            id:"2",
            name: 'node2',
            state: 'excl',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
          {
            id:"3",
            name: 'node3',
            state: 'free',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
        ]
      },

      /*
       * 第二台服务器
       */

      {
        // 服务器最基本的信息
        serverId: "2", // 服务器ID
        serverIp: "192.168.10.102", // 服务器IP地址


        // 服务器运行信息
        serverInfo: {
          coreNum: 4, // 服务器核数
          ram: 6, // 服务器总内存（GB）
          systemDisk: 120, // 系统盘容量（GB）（与下述"totalMB"相对应）
          cloudDesk: "SSD", // 阿里云云盘: ESSD或SSD
          address: "河南", // 服务器所在地
        },

        // 服务器磁盘使用信息
        diskInfo: {
          // 磁盘信息
          totalMB: 120 * 1024, // 总容量（MB）（与上述“systemDisk”相对应）
          usedMB: 6688,  // 已使用容量（MB）
        },

        // 一个服务器中可以有多个容器: 下述为某个服务器的容器列表
        dockerList: [
          {
            id: "1", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 0.1145, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量（单位: 字节B）
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量（单位: 字节B）
            pids: 5 // PIDS：容器创建的进程或线程数
          },
          {
            id: "2", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 6 // PIDS：容器创建的进程或线程数
          },
          {
            id: "3", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 8 // PIDS：容器创建的进程或线程数
          },
        ],
        // 服务器节点信息
        nodeList:[
          {
            id:"1",
            name: 'node2',
            state: 'busy',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
          {
            id:"2",
            name: 'node2',
            state: 'excl',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
          {
            id:"3",
            name: 'node3',
            state: 'free',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
        ]
      },
      /*
       * 第三台服务器
       */
      {
        // 服务器最基本的信息
        serverId: "3", // 服务器ID
        serverIp: "192.168.10.103", // 服务器IP地址


        // 服务器运行信息
        serverInfo: {
          coreNum: 2, // 服务器核数
          ram: 8, // 服务器总内存（GB）
          systemDisk: 64, // 系统盘容量（GB）（与下述"totalMB"相对应）
          cloudDesk: "ESSD", // 阿里云云盘: ESSD或SSD
          address: "河南", // 服务器所在地
        },

        // 服务器磁盘使用信息
        diskInfo: {
          // 磁盘信息
          totalMB: 64 * 1024, // 总容量（MB）（与上述“systemDisk”相对应）
          usedMB: 11451,  // 已使用容量（MB）
        },

        // 一个服务器中可以有多个容器: 下述为某个服务器的容器列表
        dockerList: [
          {
            id: "22", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 0.5566, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量（单位: 字节B）
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量（单位: 字节B）
            pids: 5 // PIDS：容器创建的进程或线程数
          },
          {
            id: "2", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 6 // PIDS：容器创建的进程或线程数
          },
          {
            id: "3", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.8888, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 8 // PIDS：容器创建的进程或线程数
          },
        ],
        // 服务器节点信息
        nodeList:[
          {
            id:"1",
            name: 'node3',
            state: 'busy',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
          {
            id:"2",
            name: 'node2',
            state: 'excl',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
          {
            id:"3",
            name: 'node3',
            state: 'free',
            load: 65.19,
            phymem: 773743,
            ncpus:64,
            allmem: 839279,
            resi: 62739,
            usrs:"15/5",
            tasks:64,
            joblist:"5673[577] NONE* 5673[37] NONE*"
          },
        ]
      }
    ])


    const deleteServer = () => { // 移除服务器方法

    }

    return {
      servers,
      deleteServer,
    }
  }
}
</script>

<style scoped>

</style>
