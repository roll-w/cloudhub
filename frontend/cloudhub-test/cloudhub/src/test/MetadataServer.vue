<template>
  <ContentBase>
    <ul class="list-group list-group-flush">
      <li class="list-group-item font">
        Runtime
      </li>

      <!-- 展示服务器负载状态、CPU使用率、内存使用率与磁盘使用率 -->
      <li class="list-group-item">

        <div class="row">
          <div class="col">
            <EchartsBase><!-- 负载状态 --></EchartsBase>
          </div>
          <div class="col">
            <EchartsBase><!-- CPU --></EchartsBase>
          </div>
          <div class="col">
            <EchartsBase><!-- RAM --></EchartsBase>
          </div>
          <div class="col">
            <EchartsBase><!-- DISK --></EchartsBase>
          </div>
        </div>

      </li>
    </ul>
  </ContentBase>
  >


  <ContentBase class="clsOne">

    <ul class="list-group list-group-flush">
      <li class="list-group-item font">
        Dockers
      </li>
      <!-- 展示服务器负载状态、CPU使用率、内存使用率与磁盘使用率 -->
      <li class="list-group-item">
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
      </li>
    </ul>

  </ContentBase>


</template>

<script>

import ContentBase from "@/components/ContentBase";
import EchartsBase from "@/components/Echarts/PieEchasrtsBase"
import {ref} from "vue";

export default {
  name: "FileView",
  components: {
    ContentBase,
    EchartsBase,
  },
  setup() {
    const dataOne = {title: '负载状态', used: 10, total: 20}

    // 元数据服务器信息
    const server = ref(
        /*
     * 元数据服务器
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
      dataOne,
      server
    }
  }
}

</script>

<style scoped>
div.clsOne {
  margin-top: 1px;
}

div.clsTwo {
  margin-top: 25px;
}

.font {
  font-size: 20px;
}
</style>
