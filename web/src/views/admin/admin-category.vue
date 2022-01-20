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
              @search="handleQuery(1, pagination.pageSize)"
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
          :data-source="categorys"
          :row-key="record => record.id"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
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
        <a-input v-model:value="category.parent" />
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
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);
    const searchValue = ref();
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
      loading.value = true;
      axios.get("/category/list", {
        params: {
          page: p.page,
          size: p.size,
          name: searchValue.value.name
        }
      }).then((response) => {
        loading.value = false;
        if(response.data.success) {

          categorys.value = response.data.content.list;
          //重置分页配置
          pagination.value.current = p.page;
          pagination.value.total = response.data.content.total;
          message.success("加载成功");
        }else {

          message.error(response.data.message);
        }
      });
    }

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("pagination" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize,
        name: searchValue.value.name
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
                page: pagination.value.current,
                size: pagination.value.pageSize
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
                page: pagination.value.current,
                size: pagination.value.pageSize
              });
            }else {
              message.error('删除失败');
            }
          }
      );
    }

    onMounted(()=> {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    })

    return {
      categorys,
      category,
      pagination,
      columns,
      loading,
      handleTableChange,
      searchValue,

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
