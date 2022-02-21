<template>
  <a-layout-footer style="text-align: center">
    Ant wiki ©2022 Created by Conner
    <span v-if="!!user.name">登录用户:   {{user.name}}</span>
  </a-layout-footer>
</template>

<script>
import { defineComponent, computed } from 'vue';
import store from "@/store";

export default defineComponent({
  name : 'the-footer',
  setup() {
    const user = computed( ()=> {
      return store.state.user;
    });

    return {
      user
    }
  }
});
</script>