<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="onClick"
      >
        <a-menu-item key="welcome">
          <router-link to="/">
            <MailOutlined />
            <span>欢迎</span>
          </router-link>
        </a-menu-item>

        <a-sub-menu v-for="item in level1" :key="item.id">
          <template #title>
              <span>
                <BarsOutlined />
                {{item.name}}
              </span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <span>
              <DropboxOutlined />
              {{child.name}}
            </span>
          </a-menu-item>
        </a-sub-menu>

        <a-menu-item key="about">
          <router-link to="/about">
            <MailOutlined />
            <span>关于我们</span>
          </router-link>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <h1>Contact Us with 2222222</h1>
    </a-layout-content>
  </a-layout>
</template>

<script>
import {defineComponent, onMounted, ref } from "vue";
import axios from 'axios';
import {Tool} from "@/util/tool";
import {message} from "ant-design-vue";

// const listData = [];
//
// for (let i = 0; i < 23; i++) {
//   listData.push({
//     href: 'https://www.antdv.com/',
//     title: `ant design vue part ${i}`,
//     avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
//     description:
//         'Ant Design, a design language for background applications, is refined by Ant UED Team.',
//     content:
//         'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
//   });
// }

export default defineComponent({
  name: 'About',
  setup() {
    console.log("set up");

    const level1 = ref();
    // const ebook_re = reactive({books: []});
    /**
     * 数据查询
     */
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          const categorys = data.content;

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
        } else {
          message.error(data.message);
        }
      });
    };

    const isShowWelcome = ref(true);
    const onClick = (value) => {
      isShowWelcome.value = value.key === 'welcome';
      console.log("menu click", value);
    };

    // 控制生命周期函数
    onMounted(() => {
      console.log("onMounted");
      handleQueryCategory();
    });

    return {
      actions: [
        { type: 'StarOutlined', text: '156' },
        { type: 'LikeOutlined', text: '156' },
        { type: 'MessageOutlined', text: '2' },
      ],
      handleQueryCategory,
      onClick,
      level1
    };
  }
});
</script>

