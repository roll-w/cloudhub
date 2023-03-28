<template>
  <div class=" flex-fill" @contextmenu="handleContextmenu">
    <div class="p-5">
      <n-h2>
        文件
      </n-h2>
      <div>
        Selected file: {{ getCheckedList()  }}
      </div>
      <div class="flex">
        <div v-for="i in [0, 1, 2, 3, 4]"
             class="flex flex-col items-center p-6 cursor-pointer
             rounded-2xl transition-all duration-300
             hover:bg-gray-100 hover:bg-opacity-50 m-2"
             @mouseenter="fileMenuShowState[i] = true"
             @mouseleave="fileMenuShowState[i] = false"
        >
          <div :class="['w-100 flex justify-start items-start align-baseline ',
          fileMenuShowState[i] || checkedState[i] ? '' : 'invisible']">
            <n-checkbox v-model:checked="checkedState[i]" />
            <div class="pl-3 flex flex-fill justify-end">
              <n-button circle>
                <template #icon>
                  <n-icon size="20">
                    <RefreshRound/>
                  </n-icon>
                </template>
              </n-button>
            </div>
          </div>

          <div class="px-5 pb-3">
            <n-icon size="80">
              <FileIcon/>
            </n-icon>
          </div>
          <div>文件夹 {{ i }}</div>
          <div class="text-gray-400">
            3/22 12:00
          </div>
        </div>

      </div>
    </div>

    <n-dropdown
        :on-clickoutside="onClickOutside"
        :options="options"
        :show="showDropdown"
        :x="xRef"
        :y="yRef"
        placement="bottom-start"
        trigger="manual"
        @select="handleSelect"/>
  </div>


</template>

<script setup>
import {h, ref} from "vue";
import {NIcon} from "naive-ui";
import Folder24Regular from "@/components/icon/Folder24Regular.vue";
import FileIcon from "@/components/icon/FileIcon.vue";
import RefreshRound from "@/components/icon/RefreshRound.vue";

const fileMenuShowState = ref([
  false, false, false, false, false])
const checkedState = ref([
  false, false, false, false, false
])

const getCheckedList = () => {
  let checkedList = []
  for (let i = 0; i < checkedState.value.length; i++) {
    if (checkedState.value[i]) {
      checkedList.push(i)
    }
  }
  return checkedList
}

const xRef = ref(0)
const yRef = ref(0)
const showDropdown = ref(false)
const options = [
  {
    label: "新建文件夹",
    key: "folder",
    icon() {
      return h(NIcon, null, {
        default: () => h(Folder24Regular)
      })
    },
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: "上传文件",
    key: "upload",
    icon() {
      return h(NIcon, null, {
        default: () => h(FileIcon)
      })
    },
  },
  {
    label: "上传文件夹",
    key: "uploadFolder",
    icon() {
      return "U"
    },
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: "粘贴",
    key: "paste",
    icon() {
      return "P"
    },
  },
  {
    label: "刷新",
    key: "refresh",
    icon() {
      return h(NIcon, null, {
        default: () => h(RefreshRound)
      })
    },
  },
  {
    label: "排序",
    key: "sort",
    icon() {
      return "S"
    },
  },
];

const handleContextmenu = (e) => {
  e.preventDefault();
  xRef.value = e.clientX;
  yRef.value = e.clientY;
  showDropdown.value = true;
}

const handleSelect = () => {
  showDropdown.value = false;
}

const onClickOutside = () => {
  showDropdown.value = false;
}

</script>
