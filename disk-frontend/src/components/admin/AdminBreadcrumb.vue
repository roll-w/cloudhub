<template>
  <n-breadcrumb>
    <n-breadcrumb-item @click="$router.push({name: adminIndex})">
        <n-dropdown :options="adminMenuOptions">
            <div>
                系统
            </div>
        </n-dropdown>
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
    convertsToNMenuOptions, findMenuOptionByKey,
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

const adminMenu = findMenuOptionByKey(keyAdmin)

const menu = requestMenusByName(keyAdmin, properties.menu)
const current = menu.children.find(it => it.key === properties.location)
const children = [...menu.children]
const menuOptions = convertsToNMenuOptions(children)

const adminMenuOptions = convertsToNMenuOptions(adminMenu.menus)

</script>
