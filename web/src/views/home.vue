<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          v-model:selectedKeys="selectedKeys2"
          v-model:openKeys="openKeys"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
            <span><MailOutlined /> welcome</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
              <span>
                <user-outlined />
                {{item.name}}
              </span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MainOutlined/><spen>{{child.name}}</spen>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <div class="welcome" v-show="isShowWelcome">
        <h1>欢迎使用Wiki知识库</h1>
      </div>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large"  :data-source="ebooks" :grid="{ gutter: 20, column: 3 }">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>

            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, reactive, toRef } from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from '@/util/tool';

const listData: any = [];
for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
    description:
        'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
        'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}



export default defineComponent({
  name: 'Home',
  setup() {
    console.log('setup');
    const ebooks = ref();
    const ebooks1 = reactive({books:[]});

    const level1 = ref();
    const isShowWelcome = ref(true);
    let categoryId2 = 0;

    let categorys: any;
    const handleQueryCategory = () => {
      axios.get("/category/all", ).then((response) => {
        if(response.data.success) {
          categorys = response.data.content;
          level1.value = [];
          level1.value = Tool.array2Tree(response.data.content, 0);
          console.log("树形结构: " + level1.value);
          message.success("加载成功");
        }else {
          message.error(response.data.message);
        }
      });
    }

    const handleQueryEbook =() => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          categoryId2: categoryId2
        }
      }).then((response) => {
        console.log(response);
        const data = response.data;
        ebooks.value = data.content.list;
        //ebooks1.books = data.content;
      });
    }

    const handleClick = (value: any)=> {
      console.log("menu click");
      isShowWelcome.value = value.key === 'welcome';
      if (!isShowWelcome.value) {
        categoryId2 = value.key;
        handleQueryEbook();
      }
    }

    const pagination = {
      onChange: (page: number) => {
        console.log(page);
      },
      pageSize: 3,
    };

    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];

    onMounted(()=> {
      handleQueryCategory();
      handleQueryEbook();
    });

    return {
      ebooks,
      books: toRef(ebooks1, "books"),
      listData,
      actions,
      pagination,
      level1,
      handleClick,
      isShowWelcome
    }
  }
});
</script>

<!--scoped表示这里的样式只在当前组件起作用-->
<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>