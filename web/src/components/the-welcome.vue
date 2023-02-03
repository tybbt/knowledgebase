<template>
  <h1 :style="{align: 'center'}">欢迎1111</h1>
  <h3>
    启动前，请确认已启动redis，本地 cd -> D/downloads/REDIS
    redis-server --service-start
    使用后请结束
    redis-server --service-stop
    重启电脑后若报错先确认windows服务中redis服务是否启动。
  </h3>
</template>

<script lang="ts">
import {defineComponent, onMounted} from 'vue';

export default defineComponent({
  name: 'the-welcome',

  setup () {
   onMounted(() => {
    console.log("welcome")
   });
   return {

   }
  }
});
</script>
