<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-space size="small">
        <p>
          <a-input-search
              v-model:value="value"
              placeholder="input search text"
              enter-button="Search"
              size="large"
              style="width: 400px"
              @search="onSearch"
          />
        </p>
        <p>
          <a-button type="primary" @click="add" size="large">新增</a-button>
        </p>
      </a-space>

      <a-table
          :columns="columns"
          :rowKey="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #LoginName="{ text: user }">
          {{user.loginName}}
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
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

      <a-form-item label="用户名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>

      <a-form-item label="昵称">
        <a-input v-model:value="user.name" />
      </a-form-item>

      <a-form-item label="密码">
        <a-input v-model:value="user.password" />
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
    name: 'AdminUser',
    setup() {

      const users = ref();
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });

      const loading = ref(false);

      const columns = [
        {
          title: '用户名',
          dateIndex: 'loginName',
          slots: { customRender: 'LoginName' }
        },
        {
          title: '昵称',
          dataIndex: 'name',
        },
        {
          title: '密码',
          dataIndex: 'password',
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
        // console.log(params)
        axios.get("/user/list", {params: params}).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            users.value = data.content.list;

            pagination.value.current = params.page;
            pagination.value.total = data.content.total;
          } else {
            message.error(data.message);
          }
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
      const user = ref();
      const modelVisible = ref(false);
      const modelLoading = ref(false);
      const handleModelOK = () => {
        modelLoading.value = true;
        axios.post("/user/save", user.value).then((response) => {
          modelLoading.value = false;
          // console.log("save user:", user);
          const data = response.data; // data = CommonResp
          if (data.success) {
            modelVisible.value = false;

            //重新加载列表
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize
            });
          } else {
            message.error(data.message);
          }
        });
      };

      const value = ref<string>();
      const onSearch = (searchValue: string) => {
        handleQuery({
          page: pagination.value.current,
          size: pagination.value.pageSize,
          loginName: searchValue
        });
      };

      /**
       * 编辑页面
       */
      const edit = (record: any) => {
        modelVisible.value = true;
        user.value = Tool.copy(record);
        // console.log("edit user:", user);
        // console.log("edit categoryid:", categoryIds);
      };

      const add = () => {
        modelVisible.value = true;
        user.value = {}
      };

      const del = (id: number) => {
        axios.delete("/user/delete/" + id).then((response) => {
          const data = response.data; // data = CommonResp
          // console.log(data)
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
        users,
        pagination,
        columns,
        loading,
        handleTableChange,
        edit,
        modelVisible,
        modelLoading,
        handleModelOK,
        user,
        add,
        del,
        value,
        onSearch,
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
