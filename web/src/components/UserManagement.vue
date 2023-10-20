<template>
  <div>
    <!-- 编辑表单 -->
    <el-dialog v-if="form" :title="formTitle" :visible.sync="formVisible" :show-close="false" :before-close="closeform">
      <el-form  :model="form" label-position="right" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <template>
            <el-radio v-model="form.role" :label="0">普通用户</el-radio>
            <el-radio v-model="form.role" :label="1">超级用户</el-radio>
          </template>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.state" :active-value="1"  :inactive-value="0" active-color="#13ce66" inactive-color="#ff4949" active-text="正常"
            inactive-text="禁用">
          </el-switch>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" autocomplete="off"></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="formTitle == '新增用户' ? addUser() : editUser()">确 定</el-button>
      </div>
    </el-dialog>
    <div>
      <el-button @click="changeMultipleState">{{ multipleState ? '关闭多选' : '开启多选' }}</el-button>
      <el-button v-if="multipleState" @click="toggleSelection()">取消选中</el-button>
      <el-button v-if="multipleState" @click="deleteUserList" type="danger">删除选中</el-button>
      <el-button @click="clearFilter">清除所有过滤器</el-button>
      <el-button type="primary" @click="addUserDialog">新增用户</el-button>
    </div>

    <el-table ref="userTable" :data="userList"
      style="width: 100%" :default-sort="{ prop: 'type', order: 'descending' }" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" v-if="multipleState">
      </el-table-column>
      <el-table-column prop="id" label="ID" width="80">
      </el-table-column>
      <el-table-column prop="username" label="账号" width="80">
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" width="100">
      </el-table-column>
      <el-table-column prop="role" label="角色" width="100"
        :filters="[{ text: '超级用户', value: 1 }, { text: '普通用户', value: 0 }]" :filter-method="filterType"
        filter-placement="bottom-end">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.role" type="danger">超级用户</el-tag>
          <el-tag v-else>普通用户</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" width="220">
      </el-table-column>
      <el-table-column prop="state" label="状态" width="150">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.state" :active-value="1"  :inactive-value="0" active-color="#13ce66" inactive-color="#ff4949" active-text="正常"
            inactive-text="禁用">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" sortable width="150">
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" sortable width="150">
      </el-table-column>
      <el-table-column>
        <template slot="header" slot-scope="scope">
          <el-input v-model="search" size="mini" placeholder="输入昵称搜索" />
        </template>
        <template slot-scope="scope">
          <el-button type="primary" @click="editUserDialog(scope.row)" icon="el-icon-edit" circle></el-button>
          <el-button type="danger" @click="deleteUser(scope.row)" icon="el-icon-delete" circle></el-button>
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
      userList: [
      ],
      multipleSelection: [],
      multipleState: false,
      search: '',
      pageNumber: 1,
      pageSize: 5,
      total: 0,
      formVisible: false,
      form: {},
      formTitle: "",
      defaultForm: {
        email: "",
        nickname: "",
        password: "",
        role: 0,
        state: 1,
        username: ""
      }
    }
  },
  methods: {
    // 清除过滤器
    clearFilter() {
      this.$refs.userTable.clearFilter();
    },
    // 类型过滤
    filterType(value, row) {
      return row.type === value;
    },
    // 取消选中
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.userTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.userTable.clearSelection();
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val.map(v => v.id);
    },
    // 改变多选状态
    changeMultipleState() {
      if (this.multipleState) {
        this.$refs.userTable.clearSelection();
      }
      this.multipleState = !this.multipleState
    },
    // 修改用户表单
    editUserDialog(row) {
      this.formTitle = "修改用户"
      this.formVisible = true
      // 不能这样子哦，这样用的是同一个地址
      // this.form = row
      this.form = { ...row }
    },
    // 修改用户
    editUser() {
      if (this.form.username && this.form.email) {
        // this.form.state = this.form.state ? 1 : 0
        this.$axios.post('/user/update', this.form).then(resp => {
          const { code, message } = resp.data
          if (code == 200) {
            this.msgAlert(message, messageType.Success)
            // 更新 goodlist
            this.getUserList()
          } else {
            this.msgAlert(message, messageType.Warning)
          }
        }).catch(error => {
          this.msgAlert("修改用户接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("账号和邮箱不得为空！", messageType.Warning)
      }
    },
    // 新增用户表单
    addUserDialog() {
      this.formTitle = "新增用户"
      this.formVisible = true
      this.form = this.defaultForm
    },
    // 新增用户
    addUser() {
      if (this.form.username && this.form.email && this.form.password) {
        this.$axios.post('/user/add', this.form).then(resp => {
          const { code, message } = resp.data
          if (code == 200) {
            this.msgAlert(message, messageType.Success)
            // 更新 goodlist
            this.getUserList()
            this.form = this.defaultForm
          } else {
            this.msgAlert(message, messageType.Warning)
          }
        }).catch(error => {
          this.msgAlert("新增用户接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("账号和邮箱和密码不得为空！", messageType.Warning)
      }
    },
    // 删除用户
    deleteUser(row) {
      this.$axios.post('/user/delete', [row.id]).then(resp => {
          const { code, message } = resp.data
        if (code == 200) {
          this.msgAlert(message, messageType.Success)
          // 更新 goodlist
          this.getUserList()
        } else {
          this.msgAlert(message, messageType.Warning)
        }
      }).catch(error => {
        this.msgAlert("删除用户接口异常", messageType.Error)
      })
    },
    // 删除多个用户
    deleteUserList() {
      if (this.multipleSelection) {
        this.$axios.post('/user/delete', this.multipleSelection).then(resp => {
          const { code, message } = resp.data
          if (code == 200) {
            this.msgAlert(message, messageType.Success)
            // 更新 goodlist
            this.getUserList()
          } else {
            this.msgAlert(message, messageType.Warning)
          }
        }).catch(error => {
          this.msgAlert("删除用户接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("至少选择一个用户操作", messageType.Warning)
      }
    },
    // 关闭修改表单的处理
    closeform() {
      this.formVisible = false
    },
    // 获取用户列表
    getUserList() {
      this.$axios.get('/user/page', {
        params: {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize
        }
      }).then(resp => {
        const { code, message, data } = resp.data
        if (code == 200) {
          this.userList = data.records
          this.total = data.totalRow
          this.msgAlert(message, messageType.Success)
        } else {
          this.msgAlert(message, messageType.Error)
        }
      }).catch(error => {
        this.msgAlert("用户列表接口异常", messageType.Error)
      })
    },
    // 搜索用户
    selectSearch() {
      console.log("搜索" + this.search)
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getUserList()
    },
    handleCurrentChange(val) {
      this.pageNumber = val
      this.getUserList()
    },
  },
  mounted() {
    this.getUserList()
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
</style>