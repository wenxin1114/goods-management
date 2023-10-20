<template>
  <div>
    <!-- 表单 -->
    <el-dialog v-if="form" :title="formTitle" :visible.sync="formVisible" :show-close="false" :before-close="closeform">
      <el-form :inline="true" :model="form" label-position="right" label-width="80px">
        <el-form-item label="封面">
          <el-upload class="avatar-uploader" action="/api/upload" :show-file-list="false" :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload">
            <el-image v-if="form.cover" :src="form.cover" class="avatar">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.category" filterable placeholder="请选择">
            <el-option v-for="option in categories" :key="option" :label="option" :value="option">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="进价">
          <el-input v-model="form.purchasePrice" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="售价">
          <el-input v-model="form.sellingPrice" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="最低价">
          <el-input v-model="form.upsetPrice" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="formTitle == '新增商品' ? addGoods() : editGoods()">确 定</el-button>
      </div>
    </el-dialog>
    <div>
      <el-button @click="changeMultipleState">{{ multipleState ? '关闭多选' : '开启多选' }}</el-button>
      <el-button v-if="multipleState" @click="toggleSelection()">取消选中</el-button>
      <el-button v-if="multipleState" @click="deleteGoodsList" type="danger">删除选中</el-button>
      <el-button @click="clearFilter">清除所有过滤器</el-button>
      <el-button type="primary" @click="addGoodsDialog">新增商品</el-button>
    </div>

    <div style="margin-top: 10px; display: flex; align-items: center;">
      <span style="margin-right: 10px;">商品类型管理: </span>
      <el-tag :key="category" v-for="category in categories" closable :disable-transitions="false"
        @close="handleClose(category)">
        {{ category }}
      </el-tag>
      <el-input class="input-new-tag" v-if="inputVisible" v-model="inputValue" ref="saveTagInput" size="small"
        @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm">
      </el-input>
      <el-button v-else class="button-new-tag" size="small" @click="showInput">添加分类</el-button>
    </div>
    <el-table ref="goodsTable" :data="goodsList"
      style="width: 100%" :default-sort="{ prop: 'createTime', order: 'descending' }"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" v-if="multipleState">
      </el-table-column>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="商品名称">
              <span>{{ props.row.name }}</span>
            </el-form-item>
            <el-form-item label="商品分类">
              <span>{{ props.row.category }}</span>
            </el-form-item>
            <el-form-item label="商品 ID">
              <span>{{ props.row.id }}</span>
            </el-form-item>
            <el-form-item label="操作用户">
              <span>{{ props.row.createUser }}</span>
            </el-form-item>
            <el-form-item label="更新用户">
              <span>{{ props.row.updateUser }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column prop="cover" label="图片" width="80">
        <template slot-scope="scope">
          <el-image :preview-src-list="[scope.row.cover]"
            style="width: 50px; height: 50px; display: flex; align-items: center; justify-content: center;border: 1px solid black"
            :src="scope.row.cover">
            <div slot="error">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" width="100">
      </el-table-column>

      <el-table-column prop="category" label="类型" width="100"
        :filters="[{ text: '饮料', value: '饮料' }, { text: '酒', value: '酒' }]" :filter-method="filterType"
        filter-placement="bottom-end">
      </el-table-column>
      <el-table-column prop="purchasePrice" label="进价(元)" sortable width="100">
      </el-table-column>
      <el-table-column prop="sellingPrice" label="售价(元)" sortable width="100">
      </el-table-column>
      <el-table-column prop="upsetPrice" label="最低价(元)" sortable width="120">
      </el-table-column>
      <el-table-column prop="createTime" label="上架时间" sortable width="150">
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" sortable width="150">
      </el-table-column>
      <el-table-column>
        <template slot="header" slot-scope="scope">
          <el-input v-model="search" size="mini" placeholder="输入商品名称搜索" />
        </template>
        <template slot-scope="scope">
          <el-button type="primary" @click="editGoodsDialog(scope.row)" icon="el-icon-edit" circle></el-button>
          <el-button type="danger" @click="deleteGoods(scope.row)" icon="el-icon-delete" circle></el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="block">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNumber"
        :page-sizes="[5, 10, 15, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { messageType } from '../enum'
export default {
  data() {
    return {
      goodsList: [],
      multipleSelection: [],
      multipleState: false,
      search: '',
      formVisible: false,
      pageNumber: 1,
      pageSize: 5,
      total: 0,
      formTitle: "新增商品",
      form: {},
      defaultForm: {
        id: "",
        cover: "",
        createTime: "",
        createUser: "",
        name: "",
        purchasePrice: "",
        sellingPrice: "",
        upsetPrice: "",
        category: "",
        updateTime: "",
        updateUser: ""
      },
      categories: [],
      inputVisible: false,
      inputValue: ''
    }
  },
  methods: {
    // 清除过滤器
    clearFilter() {
      this.$refs.goodsTable.clearFilter();
    },
    // 类型过滤
    filterType(value, row) {
      return row.category === value;
    },
    // 取消选中
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.goodsTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.goodsTable.clearSelection();
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val.map(v => v.id);
    },
    // 改变多选状态
    changeMultipleState() {
      if (this.multipleState) {
        this.$refs.goodsTable.clearSelection();
      }
      this.multipleState = !this.multipleState
    },
    // 修改商品表单
    editGoodsDialog(row) {
      this.formTitle = "修改商品"
      this.formVisible = true
      // 不能这样子哦，这样用的是同一个地址
      // this.form = row
      this.form = { ...row }
    },
    // 修改商品
    editGoods() {
      if (this.form.name && this.form.sellingPrice) {
        this.$axios.post('/goods/update', this.form).then(resp => {
          const { code, message, data } = resp.data
          if (code == 200) {
            this.msgAlert(message, messageType.Success)
            // 更新 goodlist
            this.getGoodsList()
          } else {
            this.msgAlert(message, messageType.Warning)
          }
        }).catch(error => {
          this.msgAlert("新增商品接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("商品名称和售价不得为空！", messageType.Warning)
      }
    },
    // 新增商品表单
    addGoodsDialog() {
      this.formTitle = "新增商品"
      this.formVisible = true
      this.form = this.defaultForm
    },
    // 新增商品
    addGoods() {
      if (this.form.name && this.form.sellingPrice) {
        this.$axios.post('/goods/add', this.form).then(resp => {
          const { code, message, data } = resp.data
          if (code == 200) {
            this.msgAlert(message, messageType.Success)
            // 更新 goodlist
            this.getGoodsList()
            this.form = this.defaultForm
          } else {
            this.msgAlert(message, messageType.Warning)
          }
        }).catch(error => {
          this.msgAlert("新增商品接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("商品名称和售价不得为空！", messageType.Warning)
      }
    },
    // 关闭修改表单的处理
    closeform() {
      this.formVisible = false
    },
    // 删除商品
    deleteGoods(row) {
      this.$axios.post('/goods/delete', [row.id]).then(resp => {
        const { code, message } = resp.data
        if (code == 200) {
          this.msgAlert(message, messageType.Success)
          // 更新 goodlist
          this.getGoodsList()
        } else {
          this.msgAlert(message, messageType.Warning)
        }
      }).catch(error => {
        this.msgAlert("删除商品接口异常", messageType.Error)
      })
    },
    // 删除多个商品
    deleteGoodsList() {
      if (this.multipleSelection) {
        this.$axios.post('/goods/delete', this.multipleSelection).then(resp => {
          const { code, message } = resp.data
          if (code == 200) {
            this.msgAlert(message, messageType.Success)
            // 更新 goodlist
            this.getGoodsList()
          } else {
            this.msgAlert(message, messageType.Warning)
          }
        }).catch(error => {
          this.msgAlert("删除商品接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("至少选择一个商品操作", messageType.Warning)
      }
    },
    // 搜索商品
    selectSearch() {
      console.log("搜索" + this.search)
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getGoodsList()
    },
    handleCurrentChange(val) {
      this.pageNumber = val
      this.getGoodsList()
    },
    handleCoverSuccess(resp) {
      if (resp.code == 200) {
        this.form.cover = `/api/image/${resp.data}`
        this.msgAlert(resp.message, messageType.Success)
      } else {
        this.msgAlert(message, messageType.Error)
      }
    },
    beforeCoverUpload() {

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
    handleClose(category) {
      this.$axios.post('/category/delete', { name: category }).then(resp => {
        const { code, message, data } = resp.data
        if (code == 200) {
          this.categories = data
          this.msgAlert(message, messageType.Success)
        } else {
          this.msgAlert(message, messageType.Error)
        }
      }).catch(error => {
        this.msgAlert("删除分类接口异常", messageType.Error)
      })
    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.$axios.post('/category/add', { name: inputValue }).then(resp => {
          const { code, message, data } = resp.data
          if (code == 200) {
            this.categories = data
            this.msgAlert(message, messageType.Success)
          } else {
            this.msgAlert(message, messageType.Error)
          }
        }).catch(error => {
          this.msgAlert("添加分类接口异常", messageType.Error)
        })
      }
      this.inputVisible = false;
      this.inputValue = '';
    }
  },
  mounted() {
    this.getGoodsList()
    this.getCategoryList()
  },
  watch: {
    search(newValue) {
      this.selectSearch()
    },
  },
}
</script>

<style scoped>
.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.el-tag+.el-tag {
  margin-left: 10px;
}

.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>