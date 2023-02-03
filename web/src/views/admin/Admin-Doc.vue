<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-row :gutter="24">
        <a-col :span="8">
          <a-space size="small">
            <p>
              <a-button type="primary" @click="handleQuery" size="large">刷新</a-button>
            </p>
            <p>
              <a-button type="primary" @click="add" size="large">新增</a-button>
            </p>
          </a-space>

          <a-table
              v-if="level1.length > 0"
              :columns="columns"
              :rowKey="record => record.id"
              :data-source="level1"
              :pagination="false"
              :loading="loading"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
             {{record.sort}}  {{text}}
            </template>

            <template v-slot:action="{ text, record }">
              <span>
                <a-space size="small">
                  <a-button type="primary" @click="edit(record)" size="small">编辑</a-button>
                  <a-popconfirm
                      title="删除后不可恢复，确认删除？"
                      ok-text="是"
                      cancel-text="否"
                      @confirm="del(record.id)"
                  >
                    <a-button type="primary" size="small" danger >删除</a-button>
                  </a-popconfirm>
                </a-space>
              </span>
            </template>
          </a-table>
        </a-col>

        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" layout="vertical">

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
              </a-tree-select>
            </a-form-item>

            <a-form-item label="顺序">
              <a-input v-model:value="doc.sort" />
            </a-form-item>

            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent">
                <EyeOutlined /> 内容预览
              </a-button>
            </a-form-item>

            <a-form-item label="内容">
              <div id="content"></div>
            </a-form-item>

          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHTML"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>

<!--  <a-modal-->
<!--      title="文档"-->
<!--      v-model:visible="modelVisible"-->
<!--      :confirm-loading="modelLoading"-->
<!--      @ok="handleModelOK"-->
<!--  >-->
<!--    -->
<!--  </a-modal>-->
</template>

<script lang="ts">
  import {createVNode, defineComponent, onMounted, ref} from 'vue';
  import axios from 'axios';
  import {message, Modal} from "ant-design-vue";
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
  import E from 'wangeditor';

  export default defineComponent({
    name: 'AdminDoc',
    setup() {
      const editor = new E('#content');
      editor.config.zIndex=0;

      const route = useRoute();
      const docs = ref();
      const level1 = ref();
      level1.value = []
      const treeSelectData = ref();
      treeSelectData.value = [];

      const loading = ref(false);

      const columns = [

        {
          title: '名称',
          dataIndex: 'name',
          slots: { customRender: 'name' }
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
        axios.get("/doc/all/" + route.query.ebookId).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            docs.value = data.content;

            level1.value = [];
            level1.value = Tool.array2Tree(docs.value, 0);

            treeSelectData.value = Tool.copy(level1.value) || [];
            treeSelectData.value.unshift({id: 0, name: '无'});
          } else {
            message.error(data.message);
          }
        });
      };


      const setDisable = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
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
       * 表单
       */
      const doc = ref();
      doc.value = { ebookId: route.query.ebookId };
      const param = ref();
      const handleSave = () => {
        doc.value.content = editor.txt.html();
        axios.post("/doc/save", doc.value).then((response) => {
          const data = response.data; // data = CommonResp
          if (data.success) {
            message.success("保存成功！");
            //重新加载列表
            handleQuery();
            setTimeout(function () {
              treeSelectData.value = Tool.copy(level1.value);
              setDisable(treeSelectData.value, doc.value.id);
              treeSelectData.value.unshift({id: 0, name: '无'});
            }, 100);
            editor.txt.html("");
            doc.value = { ebookId: route.query.ebookId };
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


      let ids: Array<string> = [];
      const getDeleteIds = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            // 将目标节点设置为disabled
            ids.push(id);

            // 遍历所有子节点，将所有子节点全部都加上disabled
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                getDeleteIds(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              getDeleteIds(children, id);
            }
          }
        }
      };

      // 在弹窗框使用时的懒加载
      // let isCreated = false;
      // const createEditor = () => {
      //   setTimeout(function (){
      //     editor.create();
      //     editor.config.zIndex = 0;
      //   }, 100);
      // }
      /**
       * 编辑页面
       */

      const handleQueryContent = () => {
        axios.get("/doc/find-content/" + doc.value.id).then((response) => {
          const data = response.data;
          if (data.success) {
            editor.txt.html(data.content);
          } else {
            message.error(data.message);
          }
        });
      }

      const edit = (record: any) => {
        console.log("edit:", record);
        editor.txt.html("");
        doc.value = Tool.copy(record);
        handleQueryContent();

        treeSelectData.value = Tool.copy(level1.value);
        setDisable(treeSelectData.value, record.id);

        treeSelectData.value.unshift({id: 0, name: '无'});
        console.log('treeSelectData:', treeSelectData);

        // if (!isCreated) {
        //   createEditor();
        //   isCreated = true;
        // }

      };

      const add = () => {
        editor.txt.html("");
        doc.value = {
          ebookId: route.query.ebookId
        }

        treeSelectData.value = Tool.copy(level1.value);
        if (Tool.isEmpty(treeSelectData.value)) {
          treeSelectData.value = [];
        }
        treeSelectData.value.unshift({id:0, name: '无'});
        // if (!isCreated) {
        //   createEditor();
        //   isCreated = true;
        // }

      };


      const del = (id: number) => {
        ids = [];
        getDeleteIds(level1.value, id);
        Modal.confirm({
          title: () => 'Are you sure delete this category?',
          icon: () => createVNode(ExclamationCircleOutlined),
          content: () => '该操作将删除【'+ ids.join(",") +'】且不可恢复，是否确认删除？',
          okText: () => '是',
          okType: 'danger',
          cancelText: () => '否',
          onOk() {
            console.log('OK');
            axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
              const data = response.data; // data = CommonResp
              console.log(data)
              if (data.success) {
                //重新加载列表
                handleQuery();
              }
            });
          },
          onCancel() {
            console.log('Cancel');
          },
        });

      };

      const drawerVisible = ref(false);
      const previewHTML = ref();
      const handlePreviewContent = () => {
        const html = editor.txt.html();
        previewHTML.value = html;
        drawerVisible.value = true;
      };
      const onDrawerClose = () => {
        drawerVisible.value = false;
      };


      onMounted(() => {
        handleQuery();
        editor.create();
        // setTimeout(function (){
        //   add();
        // }, 100);

      });

      return {
        drawerVisible,
        previewHTML,
        handlePreviewContent,
        onDrawerClose,
        level1,
        columns,
        loading,
        edit,
        handleSave,
        doc,
        add,
        del,
        value,
        onSearch,
        handleQuery,
        treeSelectData,
        param
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
