<template>
  <div>
    <b-card no-body class="overflow-hidden">
      <b-row no-gutters>
        <b-col md="6">
          <b-card-img :src="goods.cover" alt="Image" class="rounded-0"></b-card-img>
        </b-col>
        <b-col md="6">
          <el-descriptions direction="vertical" :column="4" border>
            <el-descriptions-item label="名称">{{ goods.name }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag >{{ goods.category ? goods.category : "暂无"}}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="售价" :span="2">{{ goods.sellingPrice }}</el-descriptions-item>
            <el-descriptions-item label="进价">{{ goods.purchasePrice }}</el-descriptions-item>
            <el-descriptions-item label="最低价">{{ goods.upsetPrice }}</el-descriptions-item>
          </el-descriptions>
        </b-col>
      </b-row>
    </b-card>
  </div>
</template>

<script>
import { messageType } from '../enum'
export default {
  name: 'GoodsDesc',
  data() {
    return {
      goods: {}
    }
  },
  methods: {

  },
  mounted() {
    let id = this.$route.params.id
    this.$axios.get(`/goods/detail/${id}`).then(resp => {
      const { code, message, data } = resp.data
      if (code == 200) {
        this.goods = data
        this.msgAlert(message, messageType.Success)
      } else {
        this.msgAlert(message, messageType.Error)
      }
    }).catch(error => {
      this.msgAlert("商品列表接口异常", messageType.Error)
    })
  }
}

</script>