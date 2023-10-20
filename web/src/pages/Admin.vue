<template>
    <el-container>
        <el-aside width="110px">
            <el-menu default-active="1" class="el-menu-vertical" @select="handleSelect">
                <el-menu-item index="1">
                    <b-icon icon="shop-window"></b-icon>
                    商品管理
                </el-menu-item>
                <el-menu-item index="2">
                    <b-icon icon="person"></b-icon>
                    个人信息
                </el-menu-item>
                <el-menu-item v-if="user && user.role == 1" index="3">
                    <b-icon icon="person-lines-fill"></b-icon>
                    用户管理
                </el-menu-item>
                <el-menu-item @click="logout">
                    <b-icon icon="x-circle"></b-icon>
                    退出登录
                </el-menu-item>

            </el-menu>
        </el-aside>
        <el-container>
            <el-main>
                <div v-if="activeMenu === '1'">
                    <GoodsManagement />
                </div>
                <div v-else-if="activeMenu === '2'">
                    <PersonalInfo />
                </div>
                <div v-else-if="activeMenu === '3'">
                    <UserManagement />
                </div>
            </el-main>
        </el-container>
    </el-container>
</template>


<script>
import GoodsManagement from '@/components/GoodsManagement.vue';
import PersonalInfo from '@/components/PersonalInfo.vue';
import UserManagement from '@/components/UserManagement.vue';
import {messageType} from '../enum'
import router from '@/router';

export default {
    data() {
        return {
            activeMenu: '1',
            user: {}
        };
    },
    methods: {
        handleSelect(index) {
            this.activeMenu = index;
        },
        // 获取用户信息
        getUserInfo() {
            this.$axios.get('/user/info').then(resp => {
                const { code, message, data } = resp.data
                if (code == 200) {
                    this.user = data
                    this.msgAlert(message, messageType.Success)
                } else {
                    this.msgAlert(message, messageType.Warning)
                }
            }).catch(error => {
                this.msgAlert("用户信息接口异常", messageType.Error)
            })
        },
        logout() {
            this.$axios.get('/user/logout').then(resp => {
                const { code, message } = resp.data
                if (code == 200) {
                    this.msgAlert(message, messageType.Success)
                    localStorage.removeItem("token")
                    router.push("/")
                } else {
                    this.msgAlert(message, messageType.Warning)
                }
            }).catch(error => {
                this.msgAlert("退出登录接口异常", messageType.Error)
            })
        }
    },
    mounted() {
        this.getUserInfo()
    },
    components: { GoodsManagement, PersonalInfo, UserManagement }
};
</script>

<style scoped>
.content {
    padding: 20px;
    background-color: #f0f0f0;
    height: 100%;
}
</style>