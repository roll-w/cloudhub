<template>
  <ContentBase>
    <div class="mb-4 ms-2 d-flex flex-grow-1 flex-fill">
      <h3>{{ fileServerId }}</h3>
      <div class="d-flex flex-fill justify-content-end">
        <button type="button" class="btn btn-outline-primary ms-2" @click="back">返回集群信息</button>
      </div>
    </div>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import {useRouter} from "vue-router";

export default {
  name: "FileServerInfo",
  components: {
    ContentBase
  },
  setup() {
    const router = useRouter()
    const fileServerId = router.currentRoute.value.params.id

    const back = () => {
      router.push({name: "cluster_index"})
    }

    return {
      fileServerId,
      back,
    }
  }
}

</script>
