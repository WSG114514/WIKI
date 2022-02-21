<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <h1 v-if="level1.length === 0">未找到相关文档</h1>
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="level1.length > 0"
              :tree-data="level1"
              @select="onSelect"
              :replaceFields="{title: 'name', key: 'id', value:'id'}"
              :defaultExpandAll="true"
              :defaultSelectedKeys="defaultSelectedKeys"
          >
          </a-tree>
        </a-col>
        <a-col :span="18">
          <div>
            <h2>{{doc.name}}</h2>
            <div>
              <span>阅读数: {{doc.viewCount}}</span>&nbsp;&nbsp;
              <span>点赞数: {{doc.voteCount}}</span>
            </div>
            <a-divider style="height: 2px; background-color: #72a9e2" />
          </div>
          <div :innerHTML="html" class="wangeditor"></div>
          <div style="text-align: center">
            <a-button type="primary" shape="round" :size="size" @click="LikeClick">
              <template #icon>
                <LikeOutlined />
                点赞: {{doc.voteCount}}
              </template>
            </a-button>
          </div>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted,ref, createVNode} from 'vue';
import { message, Modal} from 'ant-design-vue';
import axios from "axios";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";

export default defineComponent({
  name: 'Doc',
  setup() {
    const docs = ref();
    const doc = ref();
    doc.value = {};
    const level1 = ref();
    level1.value = [];
    // 通过route可以得到各种信息
    const route = useRoute();
    const html = ref();
    const defaultSelectedKeys = ref();
    defaultSelectedKeys.value = [];

    /**
     * 数据查询
     */
    const handleQuery = (p: any) => {
      // 如果不清空现有数据，则编辑保存重新加载后再次点击编辑还是原来的数据。
      level1.value = [];
      axios.get("/doc/all/"+ route.query.ebookId).then((response) => {
        if(response.data.success) {
          docs.value = response.data.content;
          console.log("route.query.ebookId" + route.query.ebookId)
          console.log("原始数据" + docs.value)
          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          if(Tool.isNotEmpty(level1)) {
            doc.value = level1.value[0];
            defaultSelectedKeys.value = [level1.value[0].id];
            handleQueryContent(level1.value[0].id);
          }
          message.success("加载成功");
        }else {
          message.error(response.data.message);
        }
      });
    }

    /**
     * 文档内容查询
     */
    const handleQueryContent = (p: any) => {
      // 如果不清空现有数据，则编辑保存重新加载后再次点击编辑还是原来的数据。
      axios.get("/doc/find-content/" + p).then((response) => {
        if(response.data.success) {
          html.value = response.data.content;
        }else {
          message.error(response.data.message);
        }
      });
    }

    const onSelect = (selectedKeys: any, info: any) => {
      console.log('selected', selectedKeys, info);
      if (Tool.isNotEmpty(selectedKeys)) {
        // 加载内容
        doc.value = info.selectedNodes[0].props;
        handleQueryContent(selectedKeys[0]);
      }
    }

    const LikeClick = ()=> {
      axios.get("/doc/vote/" + doc.value.id).then((response)=> {
        const data = response.data;
        if(data.success) {
          doc.value.voteCount++;
        }else {
          message.error(data.message);
        }
      });
    };

    onMounted(()=> {
      handleQuery({
      });
    })

    return {
      level1,
      html,
      onSelect,
      defaultSelectedKeys,
      doc,
      LikeClick
    };
  },
  components: {

  },
});
</script>
<style>
/* table 样式 */
.wangeditor table {
  border-top: 1px solid #ccc;
  border-left: 1px solid #ccc;
}
.wangeditor table td,
.wangeditor table th {
  border-bottom: 1px solid #ccc;
  border-right: 1px solid #ccc;
  padding: 3px 5px;
}
.wangeditor table th {
  border-bottom: 2px solid #ccc;
  text-align: center;
}

/* blockquote 样式 */
.wangeditor blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 5px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}

/* code 样式 */
.wangeditor code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}
.wangeditor pre code {
  display: block;
}

/* ul ol 样式 */
.wangeditor  ul, ol {
  margin: 10px 0 10px 20px;
}
</style>