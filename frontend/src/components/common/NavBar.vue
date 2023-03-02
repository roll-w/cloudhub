<template>
  <button class="navbar-toggler float-start bg-secondary" type="button" data-bs-toggle="collapse"
          data-bs-target="#navSidebar"
          aria-expanded="false" aria-controls="navSidebar">
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse collapse-horizontal show" id="navSidebar">
    <nav class="d-flex flex-grow-0 flex-column flex-shrink-0 p-4 bg-light border-end border-1 h-100"
         data-bs-scroll="true"
         data-bs-backdrop="false" tabindex="-1">
      <h2>
        <router-link class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-dark text-decoration-none"
                     :to="{name:'cloudhub_index'}">CloudHub
        </router-link>
      </h2>
      <hr>
      <ul class="nav nav-pills flex-column mb-auto">
        <li>
          <router-link :class="route_name === 'cloudhub_index' ? 'nav-link active' : 'nav-link link-dark' "
                       :to="{name:'cloudhub_index'}">总览
          </router-link>
        </li>
        <li>
          <router-link :class="route_name === 'bucket_index' ? 'nav-link active' : 'nav-link link-dark' "
                       :to="{name:'bucket_index'}">桶管理
          </router-link>
        </li>
        <li v-if="is_ADMIN()">
          <router-link :class="route_name === 'bucket_admin_index' ? 'nav-link active' : 'nav-link link-dark' "
                       :to="{name:'bucket_admin_index'}">管理员桶管理
          </router-link>
        </li>
        <li v-if="is_ADMIN()">
          <router-link :class="route_name === 'userList_index' ? 'nav-link active' : 'nav-link link-dark' "
                       :to="{name:'userList_index'}">用户管理
          </router-link>
        </li>
        <li>
          <router-link :class="route_name === 'cluster_index' ? 'nav-link active' : 'nav-link link-dark' "
                       :to="{name:'cluster_index'}">文件集群信息
          </router-link>
        </li>
      </ul>
      <hr>
      <div class="dropdown">
        <a href="#" class="d-flex align-items-center link-dark text-decoration-none dropdown-toggle"
           id="dropdownUser2" data-bs-toggle="dropdown" aria-expanded="false">
          <strong>{{ $store.state.user.username }}</strong>
        </a>
        <ul class="dropdown-menu text-small shadow" aria-labelledby="dropdownUser2">
          <li><a class="dropdown-item">Settings</a></li>
          <li>
            <hr class="dropdown-divider">
          </li>
          <li><a class="dropdown-item" @click="logout">Logout</a></li>
        </ul>
      </div>
    </nav>
  </div>
</template>

<script>
import {useRoute} from 'vue-router'
import {computed} from "vue";
import {useStore} from "vuex";
import $ from "jquery";
import url from "@/store/api";

export default {
  name: "NavBar",
  setup() {
    const route = useRoute();
    const store = useStore();
    // eslint-disable-next-line
    let route_name = computed(() => {
      return route.name
    });

    const logout = () => {
      store.dispatch("logout")
    }

    const is_ADMIN = () => {
      return store.state.user.role === "ADMIN";
    };

    const checkState = () => {
      $.ajax({
        url: url.url_getCurrent,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        success(resp) {
          if (resp.errorCode !== "00000") {
            logout()
          }
        },
        error() {
          logout()
        }
      });
    };

    checkState()

    return {
      route_name,
      logout,
      is_ADMIN
    }

  }
}
</script>

<style scoped>

</style>
