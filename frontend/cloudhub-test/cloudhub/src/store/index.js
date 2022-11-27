import { createStore } from 'vuex'
import ModelUser from './user'
import ModelFile from './file'
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
    file:ModelFile,
  }
})
