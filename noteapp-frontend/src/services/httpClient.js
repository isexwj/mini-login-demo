import axios from 'axios';
import { config } from './config';
import { authUtils } from './utils';

// 创建axios实例
const httpClient = axios.create({
  baseURL: config.api.baseUrl,
  timeout: config.api.timeout,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 自动添加认证token
httpClient.interceptors.request.use(
  (config) => {
    let token = authUtils.getAuthData().token;
    if (token) {
      // 防御性处理：移除字符串两端可能存在的引号，并去掉意外存在的 'Bearer ' 前缀
      try {
        token = String(token).replace(/^"|"$/g, '');
      } catch (e) {
        token = String(token);
      }
      if (token.startsWith('Bearer ')) {
        token = token.substring(7);
      }
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一处理响应和错误
httpClient.interceptors.response.use(
  (response) => {
    // 直接返回响应数据
    return response.data;
  },
  (error) => {
    // 统一错误处理
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response;
      
      switch (status) {
        case 401:
          // 未授权，清除认证信息并跳转到登录页
          authUtils.clearAuthData();
          window.location.href = '/';
          break;
        case 403:
          // 禁止访问
          console.error('访问被拒绝:', data);
          break;
        case 404:
          // 资源不存在
          console.error('请求的资源不存在:', data);
          break;
        case 500:
          // 服务器内部错误
          console.error('服务器内部错误:', data);
          break;
        default:
          console.error(`HTTP错误 ${status}:`, data);
      }
      
      return Promise.reject({
        status,
        message: data?.message || data?.msg || `HTTP错误 ${status}`,
        data
      });
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('网络错误，请检查网络连接');
      return Promise.reject({
        status: 0,
        message: '网络错误，请检查网络连接',
        data: null
      });
    } else {
      // 请求配置错误
      console.error('请求配置错误:', error.message);
      return Promise.reject({
        status: 0,
        message: error.message,
        data: null
      });
    }
  }
);

// 导出HTTP方法
export const http = {
  get: (url, config = {}) => httpClient.get(url, config),
  post: (url, data = {}, config = {}) => httpClient.post(url, data, config),
  put: (url, data = {}, config = {}) => httpClient.put(url, data, config),
  delete: (url, config = {}) => httpClient.delete(url, config),
  patch: (url, data = {}, config = {}) => httpClient.patch(url, data, config)
};

// 导出axios实例（如果需要直接使用）
export default httpClient; 