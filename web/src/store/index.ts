import { createStore } from 'vuex'

declare let SessionStorage: any;
const USER = "USER";

const store = createStore({
  // 变量
  state: {
    user: SessionStorage.get(USER) || {}
  },
  // 对变量操作（同步）
  mutations: {
    setUser (state, user) {
      debugger;
      state.user = user;
      SessionStorage.set(USER, user);
    }
  },
  // 对变量操作（异步）
  actions: {
  },
  modules: {
  }
})

export default store;
