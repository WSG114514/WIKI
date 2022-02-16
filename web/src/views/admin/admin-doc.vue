<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">

      <a-row>
        <a-col :span="8">
          <a-from layout="inline" :model="param">
            <a-form-item>
              <a-input-search
                  v-model:value="searchValue.name"
                  placeholder="名称"
                  enter-button="Search"
                  size="large"
                  @search="handleQuery()"
              />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="add()" size="large">
                新增
              </a-button>
            </a-form-item>
          </a-from>
          <a-table
              :columns="columns"
              :data-source="level1"
              :row-key="record => record.id"
              :loading="loading"
              :pagination="false"
          >
            <template #cover="{ text: cover }">
              <img v-if="cover" :src="cover" alt="avatar"/>
            </template>

            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)">
                  编辑
                </a-button>

                <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="Yes"
                    cancel-text="No"
                    @confirm="del(record.id)"
                >
                  <a-button type="danger">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="wrapperCol">
            <a-form-item label="名称">
              <a-input v-model:value="doc.name" />
            </a-form-item>
            <a-form-item label="父分类">
              <a-tree-select
                  v-model:value="doc.parent"
                  show-search
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeSelectData"
                  placeholder="请选择父文档"
                  allow-clear
                  tree-default-expand-all
                  :replaceFields="{title:'name', key:'id', value: 'id'}"
              >

              </a-tree-select>

            </a-form-item>
            <a-form-item label="顺序">
              <a-input v-model:value="doc.sort" />
            </a-form-item>
            <a-form-item label="内容">
              <div id="docContent"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

    </a-layout-content>
  </a-layout>

<!--  <a-modal-->
<!--      title="分类表单"-->
<!--      v-model:visible="modalVisible"-->
<!--      :confirm-loading="modalLoading"-->
<!--      @ok="handleModalOK"-->
<!--  >-->
<!--  </a-modal>-->
</template>
<script lang="ts">
import { defineComponent, onMounted,ref, createVNode} from 'vue';
import { message, Modal} from 'ant-design-vue';
import axios from "axios";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
import E from 'wangeditor';

export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const docs = ref();
    const loading = ref(false);
    const searchValue = ref();
    const level1 = ref();
    searchValue.value = {};
    // 通过route可以得到各种信息
    const route = useRoute();
    console.log("路由:", route);
    const editor = new E("#docContent");


    const columns = [
      {
        title: '名称',
        key: 'name',
        dataIndex: 'name',
      },
      {
        title: '父分类',
        key: 'parent',
        dataIndex: 'parent',
      },
      {
        title: '顺序',
        key: 'sort',
        dataIndex: 'sort',
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      },
    ];

    /**
     * 数据查询
     */
    const handleQuery = (p: any) => {
      // 如果不清空现有数据，则编辑保存重新加载后再次点击编辑还是原来的数据。
      level1.value = [];
      loading.value = true;
      axios.get("/doc/all", ).then((response) => {
        loading.value = false;
        if(response.data.success) {
          docs.value = response.data.content;
          console.log("原始数据" + docs.value)
          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("树形结构: " + level1.value);
          message.success("加载成功");
        }else {
          message.error(response.data.message);
        }
      });
    }

    //---------------------表单-----------------------
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const doc = ref({});
    const treeSelectData = ref();
    treeSelectData.value = [];

    const handleModalOK = ()=> {
      modalLoading.value = true;
      debugger;
      axios.post("/doc/save", doc.value).then((response)=> {
            const data = response.data;
            modalVisible.value = false;
            if(!data.success) {
              message.error(data.message);

            }else {

              modalLoading.value = false;
              //重新加载列表
              handleQuery({
              });
            }
          }
      );
    }

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modalVisible.value = true;
      doc.value = Tool.copy(record);

      // 不能选择当前节点以及所有的子节点
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      treeSelectData.value.unshift({id: 0, name: '无'});
      setTimeout(()=>{
        editor.create();
      }, 100)
    }

    /**
     * 新增
     * @param record
     */
    const add = () => {
      modalVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value);
      treeSelectData.value.unshift({id: 0, name: '无'});
      setTimeout(()=>{
        editor.create();
      }, 100)
    }

    /**
     * 删除
     * @param id
     * 一次性删除节点和节点下的子树，将id以数组转化为字符串传到后端
     */
    const del = (id: number) => {
      ids.length = 0;
      delName.length = 0;
      getDeleteIds(level1.value, id);
      const delNames = delName.join(", ");

      //***使用响应式变量注意取值时用value
      Modal.confirm({
        title: () => '重要提醒',
        icon: () => createVNode(ExclamationCircleOutlined),
        content: () => '文档: ' + delNames + '删除后不可恢复，确认删除',
        onOk() {
          axios.delete("/doc/delete/" + ids.join(",")).then((response)=> {
                const data = response.data; // data = commonResp
                if(data.success) {
                  //重新加载列表
                  message.success('删除成功');
                  handleQuery({
                  });
                }else {
                  message.error('删除失败');
                }
              }
          );
        },
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        onCancel() {},
      });
    }

    /**
     * 将某一节点及其子节点下面所有设置为disabled
     */
    const setDisable = (treeSelectData: any, id:any ) => {
      //遍历数组，即遍历某一层节点
      for(let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //目标节点
          node.disabled = true;
          const children = node.children;
          if (Tool.isNotEmpty(children)){
            for (let j=0; j<children.length; j++) {
              setDisable(children, children[j].id);
            }
          }
        }else {
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    }

    const ids:Array<string> = [];
    const delName:Array<string> = [];
    /**
     * 查找树枝
     * @param treeSelectData
     * @param id
     */
    const getDeleteIds = (treeSelectData: any, id:any ) => {
      //遍历数组，即遍历某一层节点
      for(let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //目标节点
          ids.push(node.id);
          delName.push(node.name);
          const children = node.children;
          if (Tool.isNotEmpty(children)){
            for (let j=0; j<children.length; j++) {
              getDeleteIds(children, children[j].id);
            }
          }
        }else {
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    }

    onMounted(()=> {

      handleQuery({
      });
    })

    return {
      docs,
      doc,
      columns,
      loading,
      searchValue,
      level1,
      treeSelectData,

      edit,
      add,
      del,

      modalVisible,
      modalLoading,
      handleModalOK,
      handleQuery,
    };
  },
  components: {

  },
});
</script>
