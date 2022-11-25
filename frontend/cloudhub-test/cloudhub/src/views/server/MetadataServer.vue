<template>
  <div class="col">
    <div class="row">
      <div class="col">
        <ContentBase style="width: 480px; margin-left: 242px">
          <div>Runtime</div>
          <hr>
          <!-- 展示运行环境信息 -->
          <div class="col">
            <div class="row">
              <div class="col">
                host name
              </div>
              <div class="col">
                {{ data.env.hostName }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                host address
              </div>
              <div class="col">
                {{ data.env.hostAddress }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                run user
              </div>
              <div class="col">
                {{ data.env.runUser }}
              </div>
            </div>
            <!--            <div class="row">-->
            <!--              <div class="col">-->
            <!--                user home-->
            <!--              </div>-->
            <!--              <div class="col">-->
            <!--                {{ data.env.userHome }}-->
            <!--              </div>-->
            <!--            </div>-->
            <!--            <div class="row">-->
            <!--              <div class="col">-->
            <!--                work directory-->
            <!--              </div>-->
            <!--              <div class="col">-->
            <!--                {{ data.env.workDir }}-->
            <!--              </div>-->
            <!--            </div>-->
            <!--            <div class="row">-->
            <!--              <div class="col">-->
            <!--                java version-->
            <!--              </div>-->
            <!--              <div class="col">-->
            <!--                {{ data.env.javaVersion }}-->
            <!--              </div>-->
            <!--            </div>-->
            <!--            <div class="row">-->
            <!--              <div class="col">-->
            <!--                java home-->
            <!--              </div>-->
            <!--              <div class="col">-->
            <!--                {{ data.env.javaHome }}-->
            <!--              </div>-->
            <!--            </div>-->
            <!--            <div class="row">-->
            <!--              <div class="col">-->
            <!--                OS name-->
            <!--              </div>-->
            <!--              <div class="col">-->
            <!--                {{ data.env.osName }}-->
            <!--              </div>-->
            <!--            </div>-->
            <!--            <div class="row">-->
            <!--              <div class="col">-->
            <!--                OS version-->
            <!--              </div>-->
            <!--              <div class="col">-->
            <!--                {{ data.env.osVersion }}-->
            <!--              </div>-->
            <!--            </div>-->

            <div class="row">
              <div class="col">
                OS architecture
              </div>
              <div class="col">
                {{ data.env.osArch }}
              </div>
            </div>
          </div>
        </ContentBase>
      </div>

      <div class="col">
        <ContentBase style="width: 480px; margin-right: 242px">
          <div>JVM</div>
          <hr>
          <div class="col">
            <div class="row">
              <div class="col">
                total (byte)
              </div>
              <div class="col">
                {{ data.jvm.total }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                max (byte)
              </div>
              <div class="col">
                {{ data.jvm.max }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                free (byte)
              </div>
              <div class="col">
                {{ data.jvm.free }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                used (byte)
              </div>
              <div class="col">
                {{ data.jvm.used }}
              </div>
            </div>
          </div>
        </ContentBase>
      </div>
    </div>

    <div class="row">
      <div class="col">
        <ContentBase style="width: 480px; margin-left: 242px">
          <div>System Memory</div>
          <hr>
          <div class="col">
            <div class="row">
              <div class="col">
                total (byte)
              </div>
              <div class="col">
                {{ data.mem.total }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                used (byte)
              </div>
              <div class="col">
                {{ data.mem.used }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                free (byte)
              </div>
              <div class="col">
                {{ data.mem.free }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                write (byte/s)
              </div>
              <div class="col">
                {{ data.mem.write }}
              </div>
            </div>
          </div>
        </ContentBase>
      </div>
      <div class="col">
        <ContentBase style="width: 480px; margin-right: 242px">
          <div>System Disk</div>
          <hr>
          <div class="col">
            <div class="row">
              <div class="col">
                total (byte)
              </div>
              <div class="col">
                {{ data.disk.total }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                free (byte)
              </div>
              <div class="col">
                {{ data.disk.free }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                read (byte/s)
              </div>
              <div class="col">
                {{ data.disk.read }}
              </div>
            </div>
            <div class="row">
              <div class="col">
                write (byte/s)
              </div>
              <div class="col">
                {{ data.disk.write }}
              </div>
            </div>
          </div>
        </ContentBase>
      </div>
    </div>

    <!-- CPU运行信息 -->
    <div class="row">
      <ContentBase style="width: 1000px">
        <div>CPU 16 核</div>
        <hr>
        <div class="row">
          <div class="col">
            <!-- 系统使用率 -->
            <SysUsed></SysUsed>
          </div>
          <div class="col">
            <!-- 用户使用率 -->
            <UserUsed></UserUsed>
          </div>
          <div class="col">
            <!-- IO 等待率 -->
            <Wait></Wait>
          </div>
          <div class="col">
            <!-- 空闲率 -->
            <Free></Free>
          </div>
        </div>
      </ContentBase>
    </div>
    <!-- 网络信息 -->
    <div class="row">
      <ContentBase style="width: 1000px">
        <div>Network</div>
        <hr>
        <div class="row">
          <div class="col">
            <span class="font-two">网络接收速率</span>
            <Receive class="net"></Receive>
          </div>
          <div class="col">
            <span class="font-two">网络发送速率</span>
            <Send class="net"></Send>
          </div>
        </div>
      </ContentBase>
    </div>
    <!-- 已连接文件服务器信息 -->
    <div class="row">
      <ContentBase style="width: 1000px">
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
                  容器信息
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
      </ContentBase>
    </div>
    <br>
    <br>
  </div>

</template>

<script>

import ContentBase from "@/components/common/ContentBase";
import SysUsed from "@/components/cpu/SysUsed";
import UserUsed from "@/components/cpu/UserUsed";
import Free from "@/components/cpu/Free";
import Wait from "@/components/cpu/Wait";
import Send from "@/components/network/Send";
import Receive from "@/components/network/Receive"

import {ref} from "vue";

export default {
  name: "FileView",
  components: {
    SysUsed,
    UserUsed,
    Free,
    Wait,
    ContentBase,
    Send,
    Receive
  },
  setup() {

    // 元数据服务器信息
    const data = ref({
      // CPU 返回使用率的百分比
      "cpu": {
        "cpuCores": 16,// CPU核数
        "sysUsed": 3.16,// 系统使用率
        "userUsed": 14.81,// 用户使用率
        "wait": 0.0,// IO等待率
        "free": 80.97// 空闲率
      },
      "jvm": {// JVM信息 均为字节数
        "total": 71303168,// JVM总内存
        "max": 4240441344,
        "free": 35657160,
        "used": 35646008
      },
      "mem": {// 系统内存 均为字节数
        "total": 16953597952,
        "used": 14245462016,
        "free": 2708135936,
        "write": 1122// 写速率
      },
      "disk": {// 磁盘信息
        "total": 330966233088,// 当前分区总空间
        "free": 86880907264,// 剩余空间
        "read": 16384,// 读速率 均为bytes/s
        "write": 1234// 写速率
      },
      "net": {// 网络信息 均为bytes/s
        "recv": 18178.0,// 接收
        "sent": 450020.0,// 发送
        "speed": 1000000000// 理论最大速度
      },
      "env": {// 运行环境信息
        "hostName": "Dawn",// 主机名
        "hostAddress": "10.100.159.31",
        "runUser": "user",
        "userHome": "C:\\Users\\user",
        "workDir": "D:\\Code\\Java\\cloudhub",
        "javaVersion": "17.0.1",
        "javaHome": "D:\\tools\\jdk17",
        "osName": "Windows 10",
        "osVersion": "10.0",
        "osArch": "amd64"
      },

    })

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
      data,
      servers,
      deleteServer
    }
  }
}

</script>

<style scoped>
.font {
  font-size: x-large;
}
.font-two{
  font-weight: bold;
}

.net {
  width: 450px;
  height: 300px;
}
</style>
