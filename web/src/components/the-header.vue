<template>
  <a-layout-header class="header">
    <div class="logo">
    </div>

    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px', position: 'relative' }"

    >
      <a-menu-item>
        <a-space size="small">
          <img src="/image/KBlogo.png" alt="logo" />
          知识库
        </a-space>
      </a-menu-item>

      <a-menu-item key="/">
        <router-link to="/">
          首页
        </router-link>
      </a-menu-item>

      <a-menu-item key="/admin/user" :style="user.id? {} : {display: 'none'}">
        <router-link to="/admin/user">
          用户管理
        </router-link>
      </a-menu-item>

      <a-menu-item key="/admin/ebook" :style="user.id? {} : {display: 'none'}" >
        <router-link to="/admin/ebook">
          电子书管理
        </router-link>
      </a-menu-item>

      <a-menu-item key="/admin/category" :style="user.id? {} : {display: 'none'}">
        <router-link to="/admin/category">
          分类管理
        </router-link>
      </a-menu-item>

      <a-menu-item key="/about">
        <router-link to="/about">
          关于我们
        </router-link>
      </a-menu-item>

      <a class="login-menu" @click="showLoginModal" v-show="!user.id">
        <span>
          登录
        </span>
      </a>

      <a-dropdown v-show="user.id">
        <a class="ant-dropdown-link" v-show="user.id" @click.prevent >
          您好：{{ user.name }}
          <DownOutlined />
        </a>
        <template #overlay>
          <a-menu>
            <a-menu-item>
              <a-popconfirm
                  title="确认退出登录？"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="logout"
              >
                <a class="menu-content" v-show="user.id">
                  <span>
                    退出登录
                  </span>
                </a>
              </a-popconfirm>
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>

<!--      <a-space class="login-menu" size="middle" direction="horizontal">-->
<!--        <a class="menu-content" v-show="user.id" >-->
<!--          <span>-->
<!--            <SmileOutlined />-->
<!--            您好：{{ user.name }}-->
<!--          </span>-->
<!--        </a>-->
<!--        <a-popconfirm-->
<!--            title="确认退出登录？"-->
<!--            ok-text="是"-->
<!--            cancel-text="否"-->
<!--            @confirm="logout"-->
<!--        >-->
<!--          <a class="menu-content" v-show="user.id">-->
<!--            <span>-->
<!--              退出登录-->
<!--            </span>-->
<!--          </a>-->
<!--        </a-popconfirm>-->
<!--      </a-space>-->

    </a-menu>

    <a-modal
      title="登录"
      v-model:visible="loginModalVisible"
      :confirm-loading="loginModalLoading"
      @ok="login"
    >
      <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

        <a-form-item label="登录名">
          <a-input v-model:value="loginUser.loginName" />
        </a-form-item>

        <a-form-item label="密码">
          <a-input v-model:value="loginUser.password" type="password" />
        </a-form-item>

      </a-form>
    </a-modal>

  </a-layout-header>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import store from "@/store";
declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  setup () {
    //登陆保存
    const user = computed(()=>store.state.user);

    //用于登录
    const loginUser = ref();

    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);

    const showLoginModal = () => {
      loginModalVisible.value = true;
      loginUser.value = {
        loginName: "test",
        password: "test123"
      };
    };

    // const refreshRedis = () => {
    //   axios.get("/user/login/all").then((response) => {
    //     console.log("【BUG修复】该部分用于刷新redis，存储到redis后，若不执行一次keys搜索操作，则后续的查不出来" +
    //         response.data.success
    //     )
    //   })
    // }

    const login = () => {
      console.log("开始登陆");
      loginModalLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password + KEY);
      axios.post('/user/login', loginUser.value).then((response) => {
        loginModalLoading.value = false;
        const data = response.data;
        if (data.success) {
          loginModalVisible.value = false;
          message.success("登陆成功！");
          // 设置全局变量
          store.commit("setUser", data.content);
          console.log("登陆成功，返回：", data.content);
          console.log("登陆后store：", store.state.user);
          // refreshRedis();
        } else {
          message.error(data.message);
        }
      });
    };

    const logout = () => {
      console.log("退出登陆");
      axios.get('/redis/logout/' + user.value.token).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出登陆成功！");
          // 设置全局变量
          store.commit("setUser", {});

        } else {
          message.error(data.message);
        }
      });
    };

    return {
      loginUser,
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      login,
      logout,
      user
    }
  }


});
</script>

<style>
  .login-menu {
    float: right !important;
    color: white;
    position: absolute;
    right: 70px;
  }
  .menu-content {
    color: dodgerblue;
  }
  .ant-dropdown-link{
    float: right !important;
    color: white;
    position: absolute;
    right: 70px;
  }
</style>
