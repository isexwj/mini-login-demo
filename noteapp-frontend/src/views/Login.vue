<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">用户登录</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <input 
            v-model="username"
            type="text" 
            class="form-control" 
            placeholder="用户名" 
            required
            @input="clearError"
          />
        </div>
        <div class="form-group">
          <input 
            v-model="password"
            type="password" 
            class="form-control" 
            placeholder="密码" 
            required
            @input="clearError"
          />
        </div>
        
        <!-- 错误信息显示 -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <!-- 成功信息显示 -->
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
        
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>
      
      <!-- 演示账号信息 -->
      <div class="divider"></div>
      <div class="demo-info">
        <p style="text-align: center; color: #909399; font-size: 12px; margin-bottom: 10px;">
          API测试账号
        </p>
        <div style="text-align: center; color: #909399; font-size: 12px;">
          <div>用户名：liyans 密码：123456</div>
          <div>用户名：liuj 密码：123456</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { authService } from '@/services';

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      errorMessage: '',
      successMessage: ''
    }
  },
  methods: {
    async handleLogin() {
      if (!this.username || !this.password) {
        this.errorMessage = '请输入用户名和密码';
        return;
      }
      
      this.loading = true;
      this.errorMessage = '';
      
      try {
        // 调用真实的登录接口
        const result = await authService.login(this.username, this.password);
        
        if (result.success || result.code === 200) {  // 检查 success 或 code 字段
          // 登录成功
          console.log('登录成功:', result.msg || result.message || '登录成功');
          
          // 验证token是否已保存
          const token = localStorage.getItem('auth_token');
          if (token) {
            this.username = '';
            this.password = '';
            // 跳转到个人笔记页面
            this.$router.push('/note');
          } else {
            console.error('Token保存失败');
            this.errorMessage = '登录成功但认证信息保存失败';
          }
        } else {
          // 登录失败
          this.errorMessage = result.msg || result.message || '登录失败';
        }
      } catch (error) {
        console.error('登录错误:', error);
        this.errorMessage = error.message || '网络请求失败';
      } finally {
        this.loading = false;
      }
    },
    
    // 清除错误信息
    clearError() {
      this.errorMessage = '';
      this.successMessage = '';
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", Arial, sans-serif;
  color: #333;
  /* 背景图设置 */
  background: url('http://58.250.74.98:17730/static/img/background.68ff496.png') no-repeat center center fixed;
  background-size: cover;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 登录卡片容器 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  padding: 20px;
  min-height: 100vh;
  background: url('http://58.250.74.98:17730/static/img/background.68ff496.png') no-repeat center center fixed;
  background-size: cover;
}

/* 登录卡片 */
.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
  padding: 40px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}


/* 标题 */
.login-title {
  font-size: 24px;
  color: #333;
  text-align: center;
  margin-bottom: 30px;
  font-weight: 500;
  position: relative;
  padding-bottom: 15px;
}

.login-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: #409eff;
  border-radius: 3px;
}

/* 表单元素样式 */
.form-group {
  margin-bottom: 24px;
  position: relative;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.form-control {
  width: 100%;
  height: 40px;
  padding: 0 15px;
  font-size: 14px;
  color: #606266;
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: border-color 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 记住密码 */
.remember-me {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.remember-checkbox {
  margin-right: 8px;
}

/* 错误信息 */
.error-message {
  color: #f56c6c;
  font-size: 14px;
  text-align: center;
  margin-bottom: 20px;
  padding: 8px 12px;
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
}

/* 成功信息 */
.success-message {
  color: #67c23a;
  font-size: 14px;
  text-align: center;
  margin-bottom: 20px;
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 42px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover:not(:disabled) {
  background: #66b1ff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
}

.login-btn:active:not(:disabled) {
  background: #3a8ee6;
}

.login-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
  box-shadow: none;
}

/* 页脚 */
.footer {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.7);
  margin-top: auto;
}

.divider {
  height: 1px;
  background: #ebeef5;
  margin: 15px 0;
}

.demo-info {
  margin-top: 15px;
}

/* 响应式调整 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    max-width: 95%;
  }
  
  .login-title {
    font-size: 20px;
    margin-bottom: 25px;
  }
}
</style> 