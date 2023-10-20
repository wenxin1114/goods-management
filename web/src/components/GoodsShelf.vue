<template>
    <div>
        <div class="center">
            <el-form :inline="true" :model="form">
                <el-form-item>
                    <el-select v-model="form.category" filterable placeholder="商品分类-全部">
                        <el-option v-for="option in categories" :key="option" :label="option" :value="option">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="form.text" placeholder="请输入搜索内容"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="GoodsSearch">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="goods-shelf" v-if="goodsList.length">
            <div v-for="(goods, index) in goodsList" :key="goods.id" style="margin: 10px;">
                <Goods :goods="goods" />
            </div>
        </div>
        <div class="center">
            <el-pagination background layout="prev, pager, next" :current-page.sync="pageNumber" :total="total"
                style="margin: 20px 0;" @size-change="handleSizeChange" @current-change="handleCurrentChange">
            </el-pagination>
        </div>
    </div>
</template>
<script>
import Goods from '../components/Goods.vue'
import { messageType } from '../enum'
export default {
    name: 'GoodsShelf',
    components: {
        Goods
    },
    data() {
        return {
            selected: '0',
            show: true,
            categories: [],
            goodsList: {},
            form: {
                category: null,
                text: null
            },
            total: 0,
            pageNumber: 1,
            pageSize: 10,
        };
    },
    methods: {
        checkMobile() {
            this.$store.commit("ChangeIsMobile")
        },
        getGoodsList() {
            this.$axios.get('/goods/page', {
                params: {
                    pageNumber: this.pageNumber,
                    pageSize: this.pageSize
                }
            }).then(resp => {
                const { code, message, data } = resp.data
                if (code == 200) {
                    this.goodsList = data.records
                    this.total = data.totalRow
                    this.msgAlert(message, messageType.Success)
                } else {
                    this.msgAlert(message, messageType.Error)
                }
            }).catch(error => {
                this.msgAlert("商品列表接口异常", messageType.Error)
            })
        },
        getCategoryList() {
            this.$axios.get('/category/list').then(resp => {
                const { code, message, data } = resp.data
                if (code == 200) {
                    this.categories = data
                    this.msgAlert(message, messageType.Success)
                } else {
                    this.msgAlert(message, messageType.Error)
                }
            }).catch(error => {
                this.msgAlert("分类列表接口异常", messageType.Error)
            })
        },
        GoodsSearch() {
            this.$axios.get('/goods/search', { params: this.form }).then(resp => {
                const { code, message, data } = resp.data
                if (code == 200) {
                    this.goodsList = data
                    this.msgAlert(message, messageType.Success)
                } else {
                    this.msgAlert(message, messageType.Error)
                }
            }).catch(error => {
                this.msgAlert("分类列表接口异常", messageType.Error)
            })
        },
        handleSizeChange(val) {
            this.getGoodsList()
        },
        handleCurrentChange(val) {
            this.getGoodsList()
        }

    },
    mounted() {
        this.checkMobile();
        window.addEventListener("resize", this.checkMobile);
        this.getGoodsList()
        this.getCategoryList()
    },
    beforeDestroy() {
        window.removeEventListener("resize", this.checkMobile);
    }
}
</script>

<style scoped>
* {
    /* 初始化 */
    margin: 0;
    padding: 0;

}
.goods-shelf {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
}

.center {
    display: flex;
    justify-content: center;
    align-items: center;
}

</style>