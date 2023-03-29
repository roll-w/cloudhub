<template>
  <div class=" flex-fill" @contextmenu="handleContextmenu">
    <div class="p-5">
      <n-h2>
        文件
      </n-h2>
      <div>
        Selected file: {{ getCheckedList() }}
      </div>
      <div class="flex flex-fill flex-wrap transition-all duration-300">
        <div v-for="i in [0, 1, 2, 3, 4]"
             class="flex flex-col items-center p-6 cursor-pointer
             rounded-2xl transition-all duration-300
             ease-in-out
             hover:bg-gray-100 hover:bg-opacity-50 m-2"
             @contextmenu="handleContextmenu2($event, i)"
             @dblclick="handleDblClick($event, i)"
             @mouseenter="fileMenuShowState[i] = true"
             @mouseleave="fileMenuShowState[i] = false"
        >
          <div :class="['w-100 block flex justify-start transition-all duration-300 items-start align-baseline ',
          fileMenuShowState[i] || checkedState[i] ? 'opacity-100' : 'opacity-0']">
            <n-checkbox v-model:checked="checkedState[i]"/>
            <div class="pl-3 flex flex-fill justify-end">
              <n-button circle
                        @click="handleClickMoreOptions($event, i)">
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

    <n-dropdown
        :on-clickoutside="onClickOutside"
        :options="fileOptions"
        :show="showFileDropdown"
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
const showFileDropdown = ref(false)

const handleClickMoreOptions = (e, target) => {
  const before = showFileDropdown.value

  console.log('before', before)
  showDropdown.value = false
  e.preventDefault();

  console.log('click',target)

  xRef.value = e.clientX
  yRef.value = e.clientY

  showFileDropdown.value = !before

  console.log('after', showFileDropdown.value)
}

const handleDblClick = (e, target) => {
  console.log('double', target)
}

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

const fileOptions = [
  {
    label: "下载",
    key: "download",
  }, {
    label: "分享",
    key: "share",
  },
  {
    label: "收藏",
    key: "collect",
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: "重命名",
    key: "rename",
  },
  {
    label: "移动",
    key: "move",
  },
  {
    label: () => h(
        'div',
        {
          class: "text-red-500 mr-10"
        },
        {default: () => "删除"}
    ),
    key: "delete",
  },
]

const handleContextmenu = (e) => {
  e.preventDefault();
  xRef.value = e.clientX;
  yRef.value = e.clientY;
  if (showFileDropdown.value) {
    return
  }
  showDropdown.value = true;
}

const handleContextmenu2 = (e, target) => {
  e.preventDefault();
  console.log('context', target)

  xRef.value = e.clientX;
  yRef.value = e.clientY;
  showDropdown.value = false;
  showFileDropdown.value = true;
}

const handleSelect = () => {
  showDropdown.value = false;
  showFileDropdown.value = false;
}

const onClickOutside = () => {
  showDropdown.value = false;
  showFileDropdown.value = false;
}

</script>
