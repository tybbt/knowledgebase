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
      title="文档"
      v-model:visible="modelVisible"
      :confirm-loading="modelLoading"
      @ok="handleModelOK"
  >
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
            v-model:value="doc.parent"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="treeSelectData"
            placeholder="Please select"
            tree-default-expand-all
            :replaceFields="{
              title: 'name',
              key: 'id',
              value: 'id'
            }"
        >
          <template #title="{ key, value }">
            <span style="color: #08c" v-if="value === doc.id">Child Node1 {{ value }}</span>
          </template>
        </a-tree-select>
      </a-form-item>

      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort" />
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
    name: 'AdminDoc',
    setup() {
      const docs = ref();
      const level1 = ref();
      const treeSelectData = ref();
      treeSelectData.value = [];

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
          title: '父文档',
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
        axios.get("/doc/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            docs.value = data.content;

            level1.value = [];
            level1.value = Tool.array2Tree(docs.value, 0);
          } else {
            message.error(data.message);
          }
        });
      };

      /**
       * 表单
       */
      const doc = ref({});
      const modelVisible = ref(false);
      const modelLoading = ref(false);
      const handleModelOK = () => {
        modelLoading.value = true;
        axios.post("/doc/save", doc.value).then((response) => {
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

      const setDisable = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            console.log("disabled", node);
            // 将目标节点设置为disabled
            node.disabled = true;

            // 遍历所有子节点，将所有子节点全部都加上disabled
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                setDisable(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              setDisable(children, id);
            }
          }
        }
      };

      /**
       * 编辑页面
       */
      const edit = (record: any) => {
        doc.value = Tool.copy(record);

        treeSelectData.value = Tool.copy(level1.value);
        setDisable(treeSelectData.value, record.id);

        treeSelectData.value.unshift({id: 0, name: '无'});
        console.log('treeSelectData:', treeSelectData);
        modelVisible.value = true;
      };

      const add = () => {
        modelVisible.value = true;
        doc.value = {}

        treeSelectData.value = Tool.copy(level1.value);
        treeSelectData.value.unshift({id:0, name: '无'});
      };

      const del = (id: number) => {
        axios.delete("/doc/delete/" + id).then((response) => {
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
        doc,
        add,
        del,
        value,
        onSearch,
        handleQuery,
        treeSelectData
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
