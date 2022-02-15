<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
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
    </a-layout-content>
  </a-layout>

  <a-modal
      title="分类表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOK"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="wrapperCol">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="父分类">
        <a-select
          v-model:value="categorys.parent"
          ref="select"
        >
          <a-select-option value="0">
            无
          </a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
import { defineComponent, onMounted,ref} from 'vue';
import { message } from 'ant-design-vue';
import axios from "axios";
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const categorys = ref();
    const loading = ref(false);
    const searchValue = ref();
    const level1 = ref();
    searchValue.value = {};

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
      axios.get("/category/all", ).then((response) => {
        loading.value = false;
        if(response.data.success) {
          categorys.value = response.data.content;
          console.log("原始数据" + categorys.value)
          level1.value = [];
          level1.value = Tool.array2Tree(categorys.value, 0);
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
    const category = ref({});
    const handleModalOK = ()=> {
      modalLoading.value = true;
      axios.post("/category/save", category.value).then((response)=> {
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
      category.value = Tool.copy(record);
    }

    /**
     * 新增
     * @param record
     */
    const add = () => {
      modalVisible.value = true;
      category.value = {};
    }

    /**
     * 删除
     * @param id
     */
    const del = (id: number) => {
      axios.delete("/category/delete/" + id).then((response)=> {
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
    }

    onMounted(()=> {
      handleQuery({
      });
    })

    return {
      categorys,
      category,
      columns,
      loading,
      searchValue,
      level1,

      edit,
      add,
      del,

      modalVisible,
      modalLoading,
      handleModalOK,
      handleQuery
    };
  },
  components: {

  },
});
</script>
