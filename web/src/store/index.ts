import { createStore } from 'vuex'

const store = createStore({
  // 变量
  state: {
    user:{}
  },
  // 对变量操作（同步）
  mutations: {
    setUser (state, user) {
      state.user = user;
    }
  },
  // 对变量操作（异步）
  actions: {
  },
  modules: {
  }
})

export default store;
