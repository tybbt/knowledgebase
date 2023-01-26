<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <p>
        <a-button type="primary" @click="add" size="large">新增</a-button>
      </p>
      <a-table
          :columns="columns"
          :rowKey="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>

        <template v-slot:action="{ text, record }">
          <span>
            <a-space size="small">
              <a-button type="primary" @click="edit(record)">编辑</a-button>
              <a-popconfirm
                  title="删除后不可恢复，确认删除？"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="del(record.id)"
              >
                <a-button type="primary" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </span>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="Detail"
      v-model:visible="modelVisible"
      :confirm-loading="modelLoading"
      @ok="handleModelOK"
  >
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>

      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>

      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id" />
      </a-form-item>

      <a-form-item label="分类二">
        <a-input v-model:value="ebook.category2Id" />
      </a-form-item>

      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea" />
      </a-form-item>

    </a-form>
  </a-modal>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';

  export default defineComponent({
    name: 'AdminEbook',
    setup() {
      const ebooks = ref();
      const pagination = ref({
        current: 1,
        pageSize: 3,
        total: 0
      });

      const loading = ref(false);

      const columns = [
        {
          title: '封面',
          dateIndex: 'cover',
          slots: { customRender: 'cover' }
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '分类一',
          key: 'category1Id',
          dataIndex: 'category1Id',
        },
        {
          title: '分类二',
          dataIndex: 'category2Id'
        },
        {
          title: '文档数',
          dataIndex: 'docCount'
        },
        {
          title: '阅读数',
          dataIndex: 'viewCount'
        },
        {
          title: '点赞数',
          dataIndex: 'voteCount'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      /**
       * 数据查询
       * @param params
       */
      const handleQuery = (params: any) => {
        loading.value = true;
        console.log(params)
        axios.get("/ebook/list", {params: {
            page: params.page,
            size: params.size
          }}).then((response) => {
          loading.value = false;
          const data = response.data;
          ebooks.value = data.content.list;

          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        });
      };

      /**
       * 点击页码时触发
       */
      const handleTableChange = (pagination: any) => {
        console.log("看看自带的分页参数：" + pagination);
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };

      /**
       * 表单
       */
      const ebook = ref({});
      const modelVisible = ref(false);
      const modelLoading = ref(false);
      const handleModelOK = () => {
        modelLoading.value = true;
        axios.post("/ebook/save", ebook.value).then((response) => {
          const data = response.data; // data = CommonResp
          if (data.success) {
            modelVisible.value = false;
            modelLoading.value = false;

            //重新加载列表
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize
            });
          }
        });
      };

      /**
       * 编辑页面
       */
      const edit = (record: any) => {
        modelVisible.value = true;
        ebook.value = record;
      };

      const add = () => {
        modelVisible.value = true;
        ebook.value = {}
      };

      const del = (id: number) => {
        axios.delete("/ebook/delete/" + id).then((response) => {
          const data = response.data; // data = CommonResp
          console.log(data)
          if (data.success) {
            //重新加载列表
            handleQuery({
              page: 1,
              size: pagination.value.pageSize
            });
          }
        });
      };

      onMounted(() => {
        handleQuery({
          page: 1,
          size: pagination.value.pageSize
        });
      });

      return {
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,
        edit,
        modelVisible,
        modelLoading,
        handleModelOK,
        ebook,
        add,
        del
      }
    }
  });
</script>

<style scoped>
  img {
    width: 50px;
    height: 50px;
  }

  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>
