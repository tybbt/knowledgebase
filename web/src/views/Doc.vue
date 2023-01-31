<template>
  <a-layout>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-row>
        <a-col :span="6">
          <a-tree
            v-if="level1.length > 0"
            :tree-data="level1"
            :replaceFields=" {
                    title: 'name',
                    key: 'id',
                    value: 'id'
                  } "
            :defaultExpandAll="true"
          >
          </a-tree>
        </a-col>

        <a-col :span="18">

        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import E from 'wangeditor';
import {message} from "ant-design-vue";

export default defineComponent({
  name: 'Doc',
  setup() {
    const editor = new E('#content');
    editor.config.zIndex=0;

    const route = useRoute();
    const docs = ref();

    const level1 = ref();
    level1.value = []

    /**
     * 数据查询
     */
    const handleQuery = () => {
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
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

    onMounted(() => {
      handleQuery();
    });

    return {
      level1,
      handleQuery,
    }
  }
});
</script>