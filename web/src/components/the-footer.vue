<template>
  <a-layout-footer style="text-align: center">
    Ant wiki ©2022 Created by Conner
    <span v-if="!!user.name">登录用户:   {{user.name}}</span>
  </a-layout-footer>
</template>

<script lang="ts">
import {defineComponent, computed, onMounted} from 'vue';
import store from "@/store";
import {Tool} from "@/util/tool";
import { notification } from "ant-design-vue";

export default defineComponent({
  name : 'the-footer',
  setup() {
    const user = computed( ()=> {
      return store.state.user;
    });

    let websocket: WebSocket;
    let token: any;
    const initWebSocket = (websocket: WebSocket) => {
      debugger;
      // 连接成功
      websocket.onopen = ()=> {
        console.log('WebSocket连接成功,状态码:', websocket.readyState);
      };
      // 收到消息的回调
      websocket.onmessage = (event: any) => {
        debugger;
        console.log('WebSocket收到消息:', event.data);
        notification['success']({
          message:'收到通知',
          description:event.data,
        });
      };
      // 连接错误
      websocket.onerror = () => {
        console.log('WebSocket连接错误,状态码:', websocket.readyState);
      };
      // 连接关闭的回调
      websocket.onclose = () => {
        console.log('WebSocket连接关闭,状态码:', websocket.readyState);
      };
    }

    onMounted(()=> {
      // WebSocket
      if ('WebSocket' in window) {
        token = Tool.uuid(10);
        // 连接地址: ws://127.0.0.1:8880/ws/xxx
        debugger;
        websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token );
        initWebSocket(websocket);
      } else {
        alert('当前浏览器 不支持');
      }
    })

    return {
      user
    }
  }
});
</script>