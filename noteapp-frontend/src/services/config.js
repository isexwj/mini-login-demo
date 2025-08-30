// 统一配置文件
export const config = {
  // API配置
  api: {
    baseUrl: process.env.VUE_APP_API_URL || '/api',
    timeout: 10000  ,
    retryCount: 2
  },
  
  // 认证相关配置
  auth: {
    tokenKey: 'auth_token',
    refreshTokenKey: 'refresh_token',
    userKey: 'user_info',
    tokenExpiry: 24 * 60 * 60 * 1000, // 24小时
    autoRefresh: true,
    endpoints: {
      login: '/users/login',
      register: '/users/register',
      logout: '/users/logout',
      profile: '/users/profile',
      refresh: '/users/refresh-token'
    }
  },
  
  // AI分析配置
  ai: {
    baseUrl: process.env.VUE_APP_AI_API_URL || '/api', // 使用代理路径
    endpoints: {
      analyze: '/ai/analyze-notes',
      summarize: '/ai/summarize',
      keywords: '/ai/extract-keywords',
      topics: '/ai/classify-topics',
      chat: '/chat/{userId}' // 新增聊天接口
    },
    timeout: 30000,
    retryCount: 3,
    analysis: {
      maxKeywords: 10,
      maxSummaryLength: 200,
      minContentLength: 10,
      confidenceThreshold: 0.3
    },
    topics: [
      { name: '工作相关', keywords: ['工作', '项目', '会议', '任务', 'deadline', '进度'] },
      { name: '学习笔记', keywords: ['学习', '课程', '知识', '概念', '理解', '笔记'] },
      { name: '生活记录', keywords: ['生活', '日常', '心情', '感受', '经历', '回忆'] },
      { name: '技术文档', keywords: ['技术', '代码', '开发', 'bug', '功能', '系统'] },
      { name: '计划安排', keywords: ['计划', '安排', '日程', '目标', '时间', '规划'] },
      { name: '创意想法', keywords: ['创意', '想法', '灵感', '设计', '创新', '概念'] }
    ],
    stopWords: [
      '的', '了', '在', '是', '我', '有', '和', '就', '不', '人', '都', '一', '一个',
      '上', '也', '很', '到', '说', '要', '去', '你', '会', '着', '没有', '看', '好',
      '自己', '这', '那', '他', '她', '它', '们', '什么', '怎么', '为什么', '哪里',
      '时候', '现在', '今天', '明天', '昨天', '可以', '应该', '需要', '想要', '觉得',
      '认为', '知道', '了解', '明白', '清楚', '重要', '必要', '必须', '一定', '肯定'
    ],
    mock: {
      enabled: false, // 临时启用模拟数据，避免API调用问题
      delay: 2000,
      randomizeResults: true,
      errorRate: 0.1
    }
  }
};

// 错误码定义
export const errorCodes = {
  // 认证错误码
  INVALID_CREDENTIALS: 'INVALID_CREDENTIALS',
  USER_NOT_FOUND: 'USER_NOT_FOUND',
  ACCOUNT_LOCKED: 'ACCOUNT_LOCKED',
  TOKEN_EXPIRED: 'TOKEN_EXPIRED',
  TOKEN_INVALID: 'TOKEN_INVALID',
  RATE_LIMIT: 'RATE_LIMIT',
  SERVER_ERROR: 'SERVER_ERROR',
  
  // AI错误码
  NO_CONTENT: 'NO_CONTENT',
  CONTENT_TOO_SHORT: 'CONTENT_TOO_SHORT',
  API_ERROR: 'API_ERROR',
  TIMEOUT: 'TIMEOUT',
  INVALID_RESPONSE: 'INVALID_RESPONSE'
};

// 数据结构定义
export const dataStructures = {
  // 用户数据结构
  user: {
    id: null,
    username: '',
    email: '',
    avatar: '',
    role: '',
    permissions: [],
    lastLoginAt: '',
    createdAt: ''
  },
  
  // 登录请求数据结构
  loginRequest: {
    username: '',
    password: ''
  },
  
  // 登录响应数据结构
  loginResponse: {
    code: 200,
    msg: "success",
    data: {
      userId: "",
      username: "",
    }
  },
  
  // AI分析结果数据结构
  aiAnalysisResult: {
    analysis: {
      title: '',
      summary: '',
      keywords: ''
    },
    model: '',
    usage: {
      promptTokens: null,
      completionTokens: null,
      totalTokens: null
    }
  }
}; 