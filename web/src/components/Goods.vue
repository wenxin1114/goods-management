<template>
    <div class="goods-box" :style="{width: Width + 'px'}" @click="toDesc">
        <div class="img">
            <el-image class="pic" :style="{width: Width + 'px', height: Width + 'px'}" :src="goods.cover" lazy></el-image>
        </div>
        <div class="content">
            <div class="title">
                 {{ goods.name }}
            </div>
            <div class="price">
                售价: ￥ {{ goods.sellingPrice }}
            </div>
        </div>
    </div>
</template>
<script>
export default {
    name: 'Goods',
    data() {
        return {
        }
    },
    methods: {
        toDesc() {
            this.$router.push(`/goods/${this.goods.id}`)
        }
    },
    props: {
        goods: {
            type: Object,
            default: {}
        }
    },
    computed: {
        Width() {
            return this.$store.state.isMobile ? 300 : 200
        }
    }
}
</script>

<style scoped>
.goods-box {
    background: #ffffff;
    border: 1px solid #000;
    box-sizing: border-box;
    margin-bottom: 10px;
}

.goods-box:hover {
    cursor: pointer;
}

.content {
    height: 100px;
    padding: 15px;
}
.title {
    font-size: 20px;
}
.price {
    color: #ff0000;
    font-size: 20px;
}


</style>