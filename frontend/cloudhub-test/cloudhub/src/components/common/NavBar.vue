<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <router-link class="navbar-brand" :to="{name:'cloudhub_index'}">CloudHub</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText"
              aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link :class="route_name === 'cloudhub_index' ? 'navbar-active' :'nav-link' "
                         :to="{name:'cloudhub_index'}">用户主页
            </router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name === 'file_index' ? 'nav-link active' :'nav-link' "
                         :to="{name:'file_index'}">文件管理
            </router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name === 'bucket_index' ? 'nav-link active' :'nav-link' "
                         :to="{name:'bucket_index'}">桶管理
            </router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name === 'userList_index' ? 'nav-link active' :'nav-link' "
                         :to="{name:'userList_index'}">用户管理
            </router-link>
          </li>

<!--          <li class="nav-item">-->
<!--            <router-link :class="route_name === 'server_index' ? 'nav-link active' :'nav-link' "-->
<!--                         :to="{name:'server_index'}">服务器信息-->
<!--            </router-link>-->
<!--          </li>-->

          <li class="nav-item">
            <router-link :class="route_name === 'metadataserver_index' ? 'nav-link active' :'nav-link' "
                         :to="{name:'metadataserver_index'}">元数据服务器
            </router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name === 'fileserver_index' ? 'nav-link active' :'nav-link' "
                         :to="{name:'fileserver_index'}">文件服务器
            </router-link>
          </li>

        </ul>
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link :class="route_name === 'login_index' ? 'nav-link active' :'nav-link' " :to="{name:''}">Logout
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>

</template>

<script>
import {useRoute} from 'vue-router'
import {computed} from "vue";

export default {
  name: "NavBar",
  setup() {
    const route = useRoute();
    // eslint-disable-next-line vue/return-in-computed-property
    let route_name = computed(() => {
      route.name
    })
    return {
      route_name
    }

  }
}
</script>

<style scoped>

</style>
