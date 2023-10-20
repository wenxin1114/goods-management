<template>
  <div>
    <!-- 修改密码表单 -->
    <el-dialog v-if="form" title="用户编辑" :visible.sync="formVisible" :show-close="false">
      <el-form :model="form" label-position="left" label-width="80px">
        <el-form-item label="邮箱账号">
          <el-input v-model="form.email" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="图片验证">
          <el-image style="width: 100px; border: 1px solid black; margin-right: 5px;"
            :src="`/api/kaptcha?timestamp=${form.timestamp}`"></el-image>
          <el-button type="text" @click="form.timestamp = new Date().getTime()">看不清？换一个</el-button>
          <div style="display: flex;">
            <el-input v-model="form.picCode" autocomplete="off"></el-input>
            <el-button plain @click="sendEmailCode">发送邮箱</el-button>
          </div>
        </el-form-item>
        <el-form-item label="邮箱验证">
          <el-input v-model="form.emailCode" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.password" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="editPwd">确 定</el-button>
      </div>
    </el-dialog>
    <el-descriptions class="margin-top" :title="'账号: ' + user.username" :column="3" border direction="vertical">
      <template slot="extra">
        <el-button type="primary" size="small" @click="editPwdDialog">修改密码</el-button>
      </template>
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-user"></i>
          昵称
        </template>
        {{ user.nickname }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          <b-icon icon="person-bounding-box"></b-icon>
          角色
        </template>
        <el-tag v-if="user.role" type="danger">超级用户</el-tag>
        <el-tag v-else>普通用户</el-tag>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          <b-icon icon="envelope"></b-icon>
          邮箱
        </template>
        {{ user.email }}
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <b-icon icon="clock"></b-icon>
          创建时间
        </template>
        {{ user.createTime }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          <b-icon icon="clock"></b-icon>
          更新时间
        </template>
        {{ user.updateTime }}
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>


<script>
import { messageType } from '../enum'
export default {
  name: '',
  data() {
    return {
      user: {},
      formVisible: false,
      
      form: {
        email: "",
        picCode: "",
        emailCode: "",
        password: "",
        timestamp: new Date().getTime(),
      }
    }
  },
  methods: {
    // 修改密码表单
    editPwdDialog() {
      this.formVisible = true
      this.form.email = this.user.email
    },
    // 修改密码
    editPwd() {
      const form = this.form
      for (const key in form) {
        if (form.hasOwnProperty(key) && (form[key] === "" || form[key] === undefined)) {
          this.msgAlert(`请将输入框补充完整`, messageType.Warning)
          return false
        }
      }
      this.$axios.post('/user/pwd/edit', {
          email: this.form.email,
          emailCode: this.form.emailCode,
          password: this.form.password
        }).then(resp => {
          const { code, message } = resp.data
        if (code == 200) {
          this.msgAlert(message, messageType.Success)
        } else {
          this.msgAlert(message, messageType.Warning)
        }
      }).catch(error => {
        this.msgAlert("密码修改接口异常", messageType.Error)
      })
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
    sendEmailCode() {
      if (this.form.email && this.form.picCode) {
        this.$axios.post('/sendEmail', {
          email: this.form.email,
          picCode: this.form.picCode,
          timestamp: this.form.timestamp,
        }).then(resp => {
          const { code, message } = resp.data
          if (code == 200) {
            this.msgAlert(message, messageType.Success)
            // 验证码时间
          } else {
            this.msgAlert(message, messageType.Warning)
          }
        }).catch(error => {
          this.msgAlert("发送邮箱接口异常", messageType.Error)
        })
      } else {
        this.msgAlert("邮箱和图片验证码不得为空", messageType.Warning)
      }
    },
    getCodePic() {
      
    }
  },
  mounted() {
    this.getUserInfo()
  }
}
</script>