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
    // const info = reactive({title:'使用率',total:1234,used:123})


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

    return {
      data,
      // info
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
