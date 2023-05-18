<template>
  <div class="p-5">

  </div>
</template>

<script setup>
import {getCurrentInstance} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const props = defineProps({
  tagGroup: {
    type: Object,
    required: true
  }
})

</script>

<style scoped>

</style>