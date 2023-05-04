<template>
  <n-breadcrumb>
    <n-breadcrumb-item @click="$router.push({name: adminIndex})">
      系统
    </n-breadcrumb-item>
    <n-breadcrumb-item :clickable="false">
      <n-dropdown :options="menuOptions">
        <div>
          {{ menu.name }}
        </div>
      </n-dropdown>
    </n-breadcrumb-item>
    <n-breadcrumb-item>
      {{ current.name }}
    </n-breadcrumb-item>
  </n-breadcrumb>
</template>

<script setup>
import {adminIndex} from "@/router";
import {
    convertsToNMenuOptions,
    keyAdmin,
    requestMenusByName
} from "@/views/menu";

const properties = defineProps({
  /**
   * Parent menu
   */
  menu: {
    type: String,
    required: true
  },
  /**
   * Current location
   */
  location: {
    type: String,
    required: true
  }
})

const menu = requestMenusByName(keyAdmin, properties.menu)
const current = menu.children.find(it => it.key === properties.location)
const children = [...menu.children]
const menuOptions = convertsToNMenuOptions(children)

</script>
