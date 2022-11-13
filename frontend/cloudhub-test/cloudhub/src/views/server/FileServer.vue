<template>
  <ContentBase>
<!--    <div class="card">-->
<!--      <div class="card-body">-->

<!--        <MetadataServer></MetadataServer>-->

<!--      </div>-->
<!--    </div>-->
<!--    <br>-->
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
          <tr v-for="server in servers" :key="server.serverIp">
            <th scope="row">{{ server.serverId }}</th>
            <th scope="row">{{ server.serverIp }}</th>
            <th scope="row">
              <div class="d-grid gap-2 d-md-flex justify-content-center">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#ModalCondition">
                  运行状态
                </button>
                <!-- Modal: 服务器运行状态信息 -->
                <ModalCondition :server="server"></ModalCondition>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#ModalNode">
                  节点信息
                </button>
                <!-- Modal: 服务器节点信息 -->
                <ModalNode :server="server">
                </ModalNode>

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
import ModalCondition from "@/components/modal/ModalCondition";
import ModalNode from "@/components/modal/ModalNode";
// import MetadataServer from "@/views/server/MetadataServer";
import {ref} from "vue";

export default {
  name: "ServerView",
  components: {
    ContentBase,
    ModalCondition,
    ModalNode,
    // MetadataServer
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



        // 参考博客: https://blog.csdn.net/qq_36949278/article/details/115455917

        // 服务器节点信息
        nodeList:[
          {
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
            id: "9253881a6eef", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 0.1145, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量（单位: 字节B）
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量（单位: 字节B）
            pids: 5 // PIDS：容器创建的进程或线程数
          },
          {
            id: "4156881a6e2a", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 6 // PIDS：容器创建的进程或线程数
          },
          {
            id: "9253881a1155", // CONTAINER ID：容器ID
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
            id: "3881925a6eef", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 0.5566, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量（单位: 字节B）
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量（单位: 字节B）
            pids: 5 // PIDS：容器创建的进程或线程数
          },
          {
            id: "5688411a6e2a", // CONTAINER ID：容器ID
            name: "stress", // NAME：容器名称
            cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
            memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
            memRatio: 0.7724, // MEM %：容器所使用的内存百分比
            netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
            blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
            pids: 6 // PIDS：容器创建的进程或线程数
          },
          {
            id: "11559253881a", // CONTAINER ID：容器ID
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
      str: "ModalCondition"
    }
  }
}
</script>

<style scoped>

</style>
