<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-space size="small">
        <p>
          <a-button type="primary" @click="handleQuery" size="large">刷新</a-button>
        </p>
        <p>
          <a-button type="primary" @click="add" size="large">新增</a-button>
        </p>
      </a-space>

      <a-table
          :columns="columns"
          :rowKey="record => record.id"
          :data-source="level1"
          :pagination="false"
          :loading="loading"
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
      title="分类"
      v-model:visible="modelVisible"
      :confirm-loading="modelLoading"
      @ok="handleModelOK"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>

      <a-form-item label="父分类">
        <a-input v-model:value="category.parent"/>
        <a-select
            ref="select"
            v-model:value="category.parent"
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
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';
  import {message} from "ant-design-vue";
  import {Tool} from "@/util/tool";

  export default defineComponent({
    name: 'AdminCategory',
    setup() {
      const categorys = ref();
      const level1 = ref();

      const loading = ref(false);

      const columns = [

        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '顺序',
          key: 'sort',
          dataIndex: 'sort',
        },
        {
          title: '父分类',
          key: 'parent',
          dataIndex: 'parent',
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      /**
       * 数据查询
       */
      const handleQuery = () => {
        loading.value = true;
        axios.get("/category/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            categorys.value = data.content;

            level1.value = [];
            level1.value = Tool.array2Tree(categorys.value, 0);
          } else {
            message.error(data.message);
          }
        });
      };

      /**
       * 表单
       */
      const category = ref({});
      const modelVisible = ref(false);
      const modelLoading = ref(false);
      const handleModelOK = () => {
        modelLoading.value = true;
        axios.post("/category/save", category.value).then((response) => {
          modelLoading.value = false;
          const data = response.data; // data = CommonResp
          if (data.success) {
            modelVisible.value = false;
            //重新加载列表
            handleQuery();
          } else {
            message.error(data.message);
          }
        });
      };

      const value = ref<string>();
      const onSearch = (searchValue: string) => {
        console.log(searchValue)
        handleQuery();
      };

      /**
       * 编辑页面
       */
      const edit = (record: any) => {
        modelVisible.value = true;
        category.value = Tool.copy(record);
      };

      const add = () => {
        modelVisible.value = true;
        category.value = {}
      };

      const del = (id: number) => {
        axios.delete("/category/delete/" + id).then((response) => {
          const data = response.data; // data = CommonResp
          console.log(data)
          if (data.success) {
            //重新加载列表
            handleQuery();
          }
        });
      };

      onMounted(() => {
        handleQuery();
      });

      return {
        level1,
        columns,
        loading,
        edit,
        modelVisible,
        modelLoading,
        handleModelOK,
        category,
        add,
        del,
        value,
        onSearch,
        handleQuery
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
