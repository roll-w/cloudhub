<template>
  <ContentBase>
    <h4>MetadataServer</h4>
    <h5>{{ server.serverInfo.coreNum }}核-{{ server.serverInfo.ram }}GB内存-系统盘 {{
        server.serverInfo.systemDisk
      }}GB
      {{ server.serverInfo.cloudDesk }}-{{ server.serverInfo.address }}</h5>
    <!-- 展示详情信息 -->
    <hr>
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
                <h6 style="color: red">磁盘使用率: {{ server.diskInfo.usedMB / server.diskInfo.totalMB }}(饼状图)</h6>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-sm-6">
        <div class="card" style="height: 100%">
          <h5 class="card-header">CPU</h5>
          <div class="card-body">

            <div v-for="core in server.coreList" :key="core.coreId" class="row">
              <div class="col" style="font-size: 18px">
                core {{ core.coreId }}:
              </div>
              <div class="col">
                <h6 style="color: red">CPU使用率: {{ core.coreUsage }}(条形图)</h6>
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
  </ContentBase>
</template>

<script>
import {ref} from "vue";
import ContentBase from "@/components/ContentBase";

export default {
  name: "MetadataServer",
  components: {
    ContentBase
  },
  setup() {
    const server = ref(   /*
       * 第一台服务器
       */

        {
          // 服务器最基本的信息
          serverId: "1", // 服务器ID
          serverIp: "192.168.10.101", // 服务器IP地址

          // 服务器运行信息
          serverInfo: {
            coreNum: 3, // 服务器核数
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

          // 服务器每个CPU的使用率(列表长度与serverInfo.coreNum的值相对性)
          coreList: [
            {
              coreId: 1,
              coreUsage: 0.4567
            },
            {
              coreId: 2,
              coreUsage: 0.1234
            },
            {
              coreId: 3,
              coreUsage: 0.6666
            },
          ],

          // 一个服务器中可以有多个容器: 下述为某个服务器的容器列表
          dockerList: [
            {
              id: "9253881a6eef", // CONTAINER ID：容器ID
              name: "stress", // NAME：容器名称
              cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
              memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
              memRatio: 0.7724, // MEM %：容器所使用的内存百分比
              netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量（单位: 字节B）
              blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量（单位: 字节B）
              pids: 5 // PIDS：容器创建的进程或线程数
            },
            {
              id: "4156881a6e2a", // CONTAINER ID：容器ID
              name: "stress", // NAME：容器名称
              cpuRatio: 0.888, // CPU %：容器使用的主机 CPU百分比
              memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
              memRatio: 0.7724, // MEM %：容器所使用的内存百分比
              netIO: {netIn: 456, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
              blockIO: {blockIn: 1, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
              pids: 6 // PIDS：容器创建的进程或线程数
            },
            {
              id: "9253881a1155", // CONTAINER ID：容器ID
              name: "stress", // NAME：容器名称
              cpuRatio: 1.234, // CPU %：容器使用的主机 CPU百分比
              memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
              memRatio: 0.7724, // MEM %：容器所使用的内存百分比
              netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
              blockIO: {blockIn: 0, blockOut: 2},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
              pids: 8 // PIDS：容器创建的进程或线程数
            },
          ],
        },
    )
    return {
      server
    }
  }
}
</script>

<style scoped>

</style>