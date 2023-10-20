<template>
  <!-- 路由匹配到的组件将渲染在这里 -->
  <div id="app">
    <Header/>
    <router-view></router-view>
    <el-backtop></el-backtop>
  </div>

</template>

<script>
import Header from './components/Header.vue'

export default {
  name: "App",
  components: {
    Header
  },
  data() {
    return {}
  }
}
</script>

