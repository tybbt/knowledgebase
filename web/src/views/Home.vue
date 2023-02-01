<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="onClick"
          @openChange="showFullEbooks"
      >
        <a-menu-item key="welcome">
          <DingtalkOutlined />
          <span>
            欢迎
          </span>
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
      <div class="welcome" v-show="isShowWelcome">
        <h1>欢迎</h1>
        <h3>
          启动前，请确认已启动redis，本地 cd -> D/downloads/REDIS
          redis-server --service-start
          使用后请结束
          redis-server --service-stop
          重启电脑后若报错先确认windows服务中redis服务是否启动。
        </h3>
      </div>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :data-source="ebooks" :grid="{ gutter: 20, column: 3 }">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span v-for="{ type, text } in actions" :key="type">
                <component v-bind:is="type" style="margin-right: 8px" />
                {{ text }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{item.name}}
                </router-link>
<!--                <a :href="item.href">{{ item.name }}</a>-->
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
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
    name: 'Home',
    setup() {
      console.log("set up");
      const isShowWelcome = ref(true);

      const level1 = ref();
      const ebooks = ref();
      // const ebook_re = reactive({books: []});

      const handleQueryEbook = (params) => {
        axios.get("/ebook/list", {
          params: {
            page: params.page,
            size: params.size,
            categoryId2: params.categoryId2
          }
        }).then((response) => {
          const data = response.data;
          ebooks.value = data.content.list;
          // ebook_re.books = data.content;
        });
      };

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

          handleQueryEbook({
            page: 1,
            size: 1000
          })
        });
      };
      let categoryId2 = 0;

      const onClick = (value) => {
        if (value.key === "welcome") {
          isShowWelcome.value = true;
        } else {
          isShowWelcome.value = false;
          categoryId2 = value.key;
        }
        // console.log("menu click", value);
        handleQueryEbook({
          page: 1,
          size: 1000,
          categoryId2: categoryId2
        })
      };

      const showFullEbooks = () => {
        isShowWelcome.value = false;
        handleQueryEbook({
          page: 1,
          size: 1000
        })
      }

      // 控制生命周期函数
      onMounted(() => {
        console.log("onMounted");
        handleQueryCategory();

      });

      return {
        ebooks,
        // book_list: toRef(ebook_re, "books")
        pagination : {
          onChange: page => {
            console.log(page);
          },
          pageSize: 3,
        },
        actions: [
          { type: 'StarOutlined', text: '156' },
          { type: 'LikeOutlined', text: '156' },
          { type: 'MessageOutlined', text: '2' },
        ],
        handleQueryCategory,
        onClick,
        showFullEbooks,
        level1,
        isShowWelcome
      };
    }
  });
</script>

<!--scoped 表示改样式仅在当前文件Home生效-->
<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>
