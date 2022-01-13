<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-table
          :columns="columns"
          :data-source="ebooks"
          :row-key="record => record.id"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover}">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>

        <template v-slot:action="{ text, record}">
          <a-space size="small">
            <a-button type="primary" @click="edit">
              编辑
            </a-button>
            <a-button type="danger">
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="电子书表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOK"
  >
    <p>test</p>
  </a-modal>
</template>
<script lang="ts">
import { defineComponent, onMounted,ref } from 'vue';
import axios from "axios";

export default defineComponent({
  name: 'AdminEbook',
  setup() {
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0
    });
    const loading = ref(false);

    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: { customRender: 'cover'}
      },
      {
        title: '名称',
        key: 'name',
        dataIndex: 'name',
      },
      {
        title: '分类1',
        key: 'category1Id',
        dataIndex: 'category1Id',
      },
      {
        title: '分类2',
        key: 'category2Id',
        dataIndex: 'category2Id',
      },
      {
        title: '文档数',
        dataIndex: 'docCount',
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount',
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount',
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
      axios.get("/ebook/list", {
        params: {
          page: p.page,
          size: p.size
        }
      }).then((response) => {
        loading.value = false;
        ebooks.value = response.data.content.list;

        //重置分页配置
        pagination.value.current = p.page;
        pagination.value.total = response.data.content.total;
      });
    }

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("pagination" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    }

    //---------------------表单-----------------------
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOK = ()=> {
      modalLoading.value = true;
      setTimeout(()=>{
        modalVisible.value = false;
        modalLoading.value = true;
      }, 2000);
    }

    /**
     * 编辑
     */
    const edit = () => {
      modalVisible.value = true;
    }

    onMounted(()=> {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    })

    return {
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange,

      edit,
      modalVisible,
      modalLoading,
      handleModalOK
    };
  },
  components: {

  },
});


</script>
