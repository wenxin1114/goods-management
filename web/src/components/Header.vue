<template>
  <div>
    <b-navbar toggleable="lg" type="dark" variant="info">
      <b-navbar-brand href="#">
        <img src="../assets/logo.png" class="d-inline-block align-top" width="30px">
        {{ shopName }}
      </b-navbar-brand>
      <b-button-group class="ml-auto">
        <b-button size="md" to="/" variant="outline-light">首页</b-button>
        <b-button size="md" to="/admin" variant="outline-light">后台管理</b-button>
      </b-button-group>
    </b-navbar>
  </div> 
</template>


<script>


export default {
  name: 'Header',
  data() {
    return {
      shopName: "秀金副食店"
    }
  }
}
</script>
