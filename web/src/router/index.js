// 该文件专门用于创建整个应用的路由器
import VueRouter from "vue-router"
import Home from '../pages/Home.vue'
import Login from "@/pages/Login.vue"
import Admin from "@/pages/Admin.vue"
import GoodsDesc from "@/pages/GoodsDesc.vue"

const router = new VueRouter({
    mode: 'history',
    routes: [
        {
            path: '/',
            component: Home,
            meta: {
                title: '秀金副食店'
            }
        },
        {
            path: '/login',
            component: Login,
            meta: {
                title: '后台登录'
            }
        }, {
            path: '/admin',
            component: Admin,
            meta: {
                title: '后台管理'
            }
        },
        {
            path: '/goods/:id',
            component: GoodsDesc,
            meta: {
                title: '商品详情'
            }
        }
    ]
})

//挂载路由导航守卫,控制页面访问权限
router.beforeEach((to, from, next) => {
    /* 路由发生变化修改页面title */
    if (to.meta.title) {
        document.title = to.meta.title
    }
    if (to.path === '/admin') {
        // 获取token
        const tokenStr = localStorage.getItem('token')
        if (!tokenStr) return next('/login')
  
    }
    next()
})



export default router;