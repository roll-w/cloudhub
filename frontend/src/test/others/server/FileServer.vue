<template>
  <ContentBase>
    <div class="row">
      <div class="col-sm-6">
        <div class="card">
          <h5 class="card-header">Disk</h5>
          <div class="card-body">
            <div class="row">
              <div class="col">
                <div class="row" style="font-size: 18px">&nbsp;&nbsp;total(MB):&nbsp; {{
                    diskInformation.totalMB
                  }}
                </div>
                <div class="row" style="font-size: 18px">&nbsp;&nbsp;used(MB): &nbsp;{{ diskInformation.usedMB }}</div>
                <div class="row" style="font-size: 18px">&nbsp;&nbsp;total(GB): &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{
                    parseInt(diskInformation.totalMB / 1024)
                  }}
                </div>
                <div class="row" style="font-size: 18px">&nbsp;&nbsp;used(GB): &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{
                    parseInt(diskInformation.usedMB / 1024)
                  }}
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
              <tr v-for="todo in dockerlist" :key="todo.name">
                <td>{{ todo.id }}</td>
                <td>{{ todo.name }}</td>
                <td>{{ (todo.cpuRatio * 100).toFixed(2) + '%' }}</td>
                <td>{{ todo.memMessage.memUsage + 'MiB / ' + todo.memMessage.memLimit + 'MiB' }}</td>
                <td>{{ (todo.memRatio * 100).toFixed(2) + '%' }}</td>
                <td>{{ todo.netIO.netIn + 'B / ' + todo.netIO.netOut + 'B' }}</td>
                <td>{{ todo.blockIO.blockIn + 'B / ' + todo.blockIO.blockOut + 'B' }}</td>
                <td>{{ todo.pids }}</td>
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
import ContentBase from "@/components/ContentBase";
import {onMounted, ref} from "vue";

export default {
  name: "FileServer",
  setup() {
    onMounted(() => {

    })
    // 磁盘信息
    const diskInformation = ref({
          totalMB: 114514,
          usedMB: 123456,
        }
    )
    // 容器使用信息
    const dockerlist = [
      {
        id: "9253881a6eef", // CONTAINER ID：容器ID
        name: "stress", // NAME：容器名称
        cpuRatio: 1.9799, // CPU %：容器使用的主机 CPU百分比
        memMessage: {memUsage: 197.7, memLimit: 256},// MEM USAGE / LIMIT：容器使用的总内存、以及允许使用的内存总量
        memRatio: 0.7724, // MEM %：容器所使用的内存百分比
        netIO: {netIn: 656, netOut: 0}, // NET I/O：容器通过网络接口接收和发送的数据量
        blockIO: {blockIn: 0, blockOut: 0},// BLOCK I/O：容器从主机上的块设备写入和的读取数据量
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
    ]
    return {
      diskInformation,
      dockerlist,
    }
  },

  components: {
    ContentBase
  }
}
</script>

<style scoped>

</style>
