// 该文件用于创建vuex最为核心的store

import Vue from 'vue'
// 引入Vuex
import Vuex from 'vuex'

Vue.use(Vuex)

// 准备actions——用于响应组件中的动作
const actions = {}
// 准备mutations——用于操作数据（state）
const mutations = {
    ChangeIsMobile(state) {
        state.isMobile = window.innerWidth <= 768
        state.innerWidth = window.innerWidth
    },
}
const state = {
    isMobile: window.innerWidth <= 768,// // 设置阈值，小于等于768px认为是移动端
    innerWidth: window.innerWidth
}

// 创建并暴露store
export default new Vuex.Store({
    actions,
    mutations,
    state
})