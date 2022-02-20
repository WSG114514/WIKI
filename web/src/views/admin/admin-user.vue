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
          :data-source="users"
          :row-key="record => record.id"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >

        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="resetPassword(record)">
              重置密码
            </a-button>
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
      title="用户管理"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOK"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="wrapperCol">
      <a-form-item label="登录名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password" />
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal
      title="重置密码"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetmodalLoading"
      @ok="handleResetModalOK"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="wrapperCol">
      <a-form-item label="新密码">
        <a-input v-model:value="user.password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
import { defineComponent, onMounted,ref} from 'vue';
import { message } from 'ant-design-vue';
import axios from "axios";
import {Tool} from "@/util/tool";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'AdminUser',
  setup() {
    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);
    const searchValue = ref();

    const level1 = ref();
    searchValue.value = {};

    const columns = [
      {
        title: '登录名',
        key: 'loginName',
        dataIndex: 'loginName',
      },
      {
        title: '名称',
        key: 'name',
        dataIndex: 'name',
      },
      {
        title: '密码',
        key: 'password',
        dataIndex: 'password',
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
      //查询之前先把现有数据eBook里的数据清空，如果不清空则编辑保存重新加载数据后，再次点击编辑，则列表显示的还是原来的数据。
      users.value = [];
      axios.get("/user/list", {
        params: {
          page: p.page,
          size: p.size,
          name: searchValue.value.name
        }
      }).then((response) => {
        loading.value = false;
        if(response.data.success) {

          users.value = response.data.content.list;
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
    const resetModalVisible = ref(false);
    const resetmodalLoading = ref(false);
    const user = ref();
    const handleModalOK = ()=> {
      modalLoading.value = true;

      user.value.password = hexMd5(user.value.password + KEY);
      debugger;
      axios.post("/user/save", user.value).then((response)=> {
            const data = response.data;
            modalVisible.value = false;
            if(!data.success) {
              message.error(data.message);
              modalLoading.value = false;
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
      user.value = Tool.copy(record);
    }

    /**
     * 新增
     * @param record
     */
    const add = () => {
      modalVisible.value = true;
      user.value = {};
    }

    /**
     * 删除
     * @param id
     */
    const del = (id: number) => {
      axios.delete("/user/delete/" + id).then((response)=> {
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

    const handleResetModalOK = ()=> {
      resetmodalLoading.value = true;

      user.value.password = hexMd5(user.value.password + KEY);
      axios.post("/user/reset-password", user.value).then((response)=> {
        const data = response.data;
        resetModalVisible.value = false;
        if(!data.success) {
          message.error(data.message);
          modalLoading.value = false;
        }else {
          modalLoading.value = false;
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        }}
      );
    }

    const resetPassword = (record: any)=>{
      resetModalVisible.value = true;
      user.value = Tool.copy(record);
      user.value.password = null;
    }

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    })

    return {
      users,
      user,
      pagination,
      columns,
      loading,
      handleTableChange,
      searchValue,
      level1,
      resetModalVisible,
      resetmodalLoading,

      edit,
      add,
      del,

      modalVisible,
      modalLoading,
      handleModalOK,
      handleQuery,
      handleResetModalOK,
      resetPassword
    };
  },
  components: {

  },
});


</script>
