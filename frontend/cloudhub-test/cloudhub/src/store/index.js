import { createStore } from 'vuex'
import ModelUser from './user'
import ModelMeta from './meta'
export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user:ModelUser,
    meta:ModelMeta,
  }
})
