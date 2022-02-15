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
          :data-source="ebooks"
          :row-key="record => record.id"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>

        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>

        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <router-link to="/admin/doc">
              <a-button type="primary">
                文档管理
              </a-button>
            </router-link>
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
      title="电子书表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOK"
  >
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="wrapperCol">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader v-model:value="categoryIds" :options="level1" :field-names="{ label:'name', value:'id', children:'children'}" placeholder="Please select" />
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
  name: 'AdminEbook',
  setup() {
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);
    const searchValue = ref();
    const categoryIds = ref();

    const level1 = ref();
    searchValue.value = {};

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
        title: '分类',
        slots: { customRender: 'category' }
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
      //查询之前先把现有数据eBook里的数据清空，如果不清空则编辑保存重新加载数据后，再次点击编辑，则列表显示的还是原来的数据。
      ebooks.value = [];
      axios.get("/ebook/list", {
        params: {
          page: p.page,
          size: p.size,
          name: searchValue.value.name
        }
      }).then((response) => {
        loading.value = false;
        if(response.data.success) {

          ebooks.value = response.data.content.list;
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
    const ebook = ref();
    const handleModalOK = ()=> {
      modalLoading.value = true;
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
      axios.post("/ebook/save", ebook.value).then((response)=> {
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
      ebook.value = Tool.copy(record);
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id];
    }

    let categorys: any;
    /**
     * 数据查询
     */
    const handleQueryCategory = () => {
      loading.value = true;
      axios.get("/category/all", ).then((response) => {
        loading.value = false;
        if(response.data.success) {
          //加载完分类后，再加载电子书，否则分类加载慢的话电子书渲染会报错。（asiox是异步执行）
          handleQuery({
            page: 1,
            size: pagination.value.pageSize
          });

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

    const getCategoryName = (cid: number) => {
      let result = "";
      categorys.forEach((item: any) => {
        if (item.id === cid) {
          result = item.name;
        }
      });
      return result;
    }

    /**
     * 新增
     * @param record
     */
    const add = () => {
      modalVisible.value = true;
      ebook.value = {};
    }

    /**
     * 删除
     * @param id
     */
    const del = (id: number) => {
      axios.delete("/ebook/delete/" + id).then((response)=> {
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
      handleQueryCategory();
    })

    return {
      ebooks,
      ebook,
      pagination,
      columns,
      loading,
      handleTableChange,
      getCategoryName,
      searchValue,
      categoryIds,
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
