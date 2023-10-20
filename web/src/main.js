import Vue from 'vue'
import App from './App.vue'
// 引入路由
import VueRouter from 'vue-router'
import router from './router'
// 引入 bootstrapVue
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios'
import utils from './utils'
import store from './store'

Vue.mixin(utils)
Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

axios.defaults.baseURL = `/api`

// 添加请求拦截器，在请求头中添加token
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 添加响应拦截器，处理响应中的状态码
axios.interceptors.response.use(
  response => {
    if (response.data.code === 401) {
      localStorage.removeItem("token")
      // 跳转到登录页
      router.push('/login');
    }
    return response;
  },
  error => {
    return Promise.reject(error);
  }
);

Vue.prototype.$axios = axios
Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  router,
  store
}).$mount('#app')
                