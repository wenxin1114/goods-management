<template>
  <div class="container">
    <div class="login-form">
      <h2>后台登录</h2>
      <div class="form-group">
        <label for="username">用户账号</label>
        <input type="text" v-model="form.username" name="username" required>
      </div>
      <div class="form-group">
        <label for="password">用户密码</label>
        <input type="password" v-model="form.password" name="password" required>
      </div>
      <button @click="login">登录</button>
    </div>
  </div>
</template>
<script>
import { messageType } from '../enum'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    login() {
      if (this.form.username && this.form.password) {
        this.$axios.post('/user/login', this.form).then(resp => {
          const { code, message, data } = resp.data
          if (code == 200) {
            // 登录成功
            this.msgAlert(message, messageType.Success)
            // 保存token
            localStorage.setItem('token', data)
            // 跳转 /admin 页面
            this.$router.push('/admin')
          } else if (code == 400) {
            this.msgAlert(message, messageType.Warning)
          }
        }
        ).catch(error => {
          this.msgAlert("登录接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("账号或密码不得为空", messageType.Warning)
      }

    }
  }

}
</script>

<style scoped>
body {
  font-family: Arial, sans-serif;
  background-color: #f2f2f2;
}

.container {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.login-form {
  display: flex;
  flex-direction: column;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 3px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  color: #fff;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>