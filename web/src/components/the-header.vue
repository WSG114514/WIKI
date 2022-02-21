<template>
  <a-layout-header class="header">
    <div class="logo" />
    <a-menu
        theme="dark"
        mode="horizontal"
        v-model:selectedKeys="selectedKeys1"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/"><router-link to="/">首页</router-link></a-menu-item>
      <a-menu-item v-if="!!user.name" key="/admin/user"><router-link to="/admin/user">用户管理</router-link></a-menu-item>
      <a-menu-item v-if="!!user.name" key="/admin/ebook"><router-link to="/admin/ebook">电子书管理</router-link></a-menu-item>
      <a-menu-item v-if="!!user.name" key="/admin/category"><router-link to="/admin/category">分类管理</router-link></a-menu-item>
      <a-menu-item key="/about"><router-link to="/about">关于我们</router-link></a-menu-item>
      <a-menu-item v-if="!!user.name" @click="showDrawer"><span>用户:  {{user.name}}</span></a-menu-item>
      <a-menu-item v-if="!user.name" @click="showLoginModel"><span>登录</span></a-menu-item>

    </a-menu>
  </a-layout-header>

  <a-modal
      title="登录"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="Login"
  >
    <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="wrapperCol">
      <a-form-item label="登录名">
        <a-input v-model:value="loginUser.loginName" placeholder="loginName"/>
      </a-form-item>
      <a-form-item label="密码">
        <a-input-password v-model:value="loginUser.password" placeholder="password"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-drawer
      title="欢迎回来"
      placement="right"
      :closable="false"
      v-model:visible="visible"
      :after-visible-change="afterVisibleChange"
  >
    <p>Some contents...</p>
    <p>Some contents...</p>
    <a-popconfirm
        title="确认退出登录?"
        ok-text="Yes"
        cancel-text="No"
        @confirm="logOut"
        @cancel="cancel"
    >
      <a>退出登录</a>
    </a-popconfirm>
  </a-drawer>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import store from "@/store";
declare let hexMd5: any;
declare let KEY: any;
export default defineComponent({
  name: 'the-header',
  setup() {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const loginUser = ref();
    const user = computed( ()=> {
      return store.state.user;
    });
    const visible = ref<boolean>(false);

    loginUser.value = {};
    const showLoginModel = ()=> {
      loginUser.value.password = null;
      modalVisible.value = true;
    }

    const showDrawer = () => {
      visible.value = true;
    };

    const Login =()=> {
      console.log("开始登录");
      modalLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password + KEY);
      axios.post("/user/login", loginUser.value).then((response)=> {
        const data = response.data;
        modalVisible.value = false;
        if(!data.success) {
          message.error(data.message);
        }else {
          message.success("登录成功");
          store.commit("setUser", data.content);
        }
        modalLoading.value = false;
      });
    }

    const logOut = ()=> {
      console.log("退出登录");
      axios.get("/user/logout/" + user.value.token).then((response)=> {
        const data = response.data;
        if(!data.success) {
          message.error(data.message);
        }else {
          message.success("用户退出成功");
          store.commit('setUser', {});
          visible.value = false;
        }
      })
    }

    return {
      modalVisible,
      showLoginModel,
      modalLoading,
      Login,
      loginUser,
      user,
      visible,
      showDrawer,
      logOut
    };
  }
});

</script>

<style>
 .login-menu {
   float: right;
   color: white;
 }
</style>