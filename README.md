# 🏖️ 智能旅游助手系统

> 基于 AI 技术的旅游规划平台，为用户提供个性化的旅游行程推荐和实时旅游咨询服务

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-blue.svg)](https://vuejs.org/)
[![Vant](https://img.shields.io/badge/Vant-4.x-teal.svg)](https://vant.pro/)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

---

## ✨ 功能特性

| 功能 | 说明 |
|------|------|
| 🧠 **智能旅游规划** | 根据目的地、预算、天数，AI 自动生成详细行程安排 |
| 💬 **AI 旅游咨询** | 实时旅游问答，支持流式响应（打字机效果） |
| 🌊 **流式响应** | SSE 技术实时显示 AI 生成内容，提升体验 |
| 📱 **移动端适配** | 响应式设计，完美适配手机屏幕 |
| 💰 **预算明细** | 住宿、餐饮、交通、门票等费用自动分配 |
| 🌏 **多模型支持** | 支持 DeepSeek、硅基流动等大模型切换 |

---

## 🏗️ 技术栈

### 后端技术
- **核心框架**：Spring Boot 3.2.5 + Spring Web
- **开发语言**：Java 17
- **HTTP 客户端**：OkHttp 4.12.0（调用 LLM API）
- **参数校验**：Spring Validation
- **JSON 处理**：Jackson
- **代码简化**：Lombok
- **流式响应**：Server-Sent Events (SSE)
- **构建工具**：Maven
- **大模型接口**：DeepSeek / 硅基流动

### 前端技术
- **前端框架**：Vue 3 + Composition API
- **UI 组件库**：Vant UI 4.x
- **路由管理**：Vue Router 4.x
- **状态管理**：Pinia
- **HTTP 请求**：Axios
- **构建工具**：Vite

---

## 📁 项目结构

```
travel/
├── travel-server-java/         # 后端项目 (Spring Boot)
│   ├── src/main/java/com/travel/server/
│   │   ├── controller/         # 控制器层
│   │   │   └── TravelController.java
│   │   ├── service/            # 业务逻辑层
│   │   │   └── TravelService.java
│   │   ├── dto/                # 数据传输对象
│   │   │   ├── TravelRequestDTO.java
│   │   │   └── ChatRequestDTO.java
│   │   ├── vo/                 # 视图对象
│   │   │   ├── Result.java
│   │   │   ├── TravelRecommendVO.java
│   │   │   ├── ChatVO.java
│   │   │   └── Stream*.java
│   │   ├── config/             # 配置类
│   │   │   ├── CorsConfig.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── utils/              # 工具类
│   │   │   └── LLMUtils.java
│   │   └── TravelServerApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml     # 应用配置
│   │   └── application-dev.yml # 开发环境配置
│   └── pom.xml
│
└── travel-h5/                  # 前端项目 (Vue 3)
    ├── src/
    │   ├── views/              # 页面组件
    │   │   ├── Home.vue        # 首页
    │   │   ├── Detail.vue      # 行程详情
    │   │   ├── Chat.vue        # AI 对话
    │   │   └── Profile.vue     # 个人中心
    │   ├── components/         # 业务组件
    │   │   ├── SpotItem.vue
    │   │   ├── BudgetTable.vue
    │   │   └── ChatBubble.vue
    │   ├── router/index.js     # 路由配置
    │   ├── stores/chat.js      # 状态管理
    │   ├── utils/request.js    # HTTP 工具
    │   ├── styles/common.css   # 公共样式
    │   ├── App.vue
    │   └── main.js
    ├── .env
    ├── vite.config.js
    ├── package.json
    └── index.html
```

---

## 🚀 快速开始

### 环境要求

| 依赖 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Node.js | v20+ |
| Maven | 3.8+ |
| 浏览器 | 现代浏览器 (Chrome/Edge/Safari) |

---

### 1️⃣ 配置 API Key

编辑 `travel-server-java/src/main/resources/application-dev.yml`：

```yaml
llm:
  provider: deepseek          # 或 siliconflow
  deepseek:
    api-key: sk-你的api-key   # 替换成你的真实 Key
```

> **获取 API Key**：
> - DeepSeek：https://platform.deepseek.com/api_keys
> - 硅基流动：https://www.siliconflow.cn/

---

### 2️⃣ 启动后端

```bash
cd travel-server-java
mvn spring-boot:run
```

服务启动后访问：http://localhost:8080

---

### 3️⃣ 启动前端

```bash
cd travel-h5
npm install
npm run dev
```

服务启动后访问：http://localhost:5174

---

## 🧩 API 接口

### 智能推荐（非流式）
```http
POST /api/travel/recommend
Content-Type: application/json

{
  "city": "北京",
  "budget": 5000,
  "days": 3
}
```

### AI 对话（流式 SSE）
```http
POST /api/travel/chat/stream
Content-Type: application/json

{
  "message": "北京有哪些好吃的？"
}
```

**响应格式**（SSE 流式数据块）：
```
data: {"type":"chunk","content":"北京的美食有..."}
data: {"type":"chunk","content":"很多，比如..."}
data: {"type":"done"}
```

---

## 📱 页面预览

| 页面 | 功能说明 |
|------|----------|
| 🏠 **首页** | 城市选择、预算输入、天数调整、热门目的地推荐 |
| 📋 **详情页** | 每日行程展示、预算明细、温馨提示、注意事项 |
| 💬 **对话页** | AI 聊天、流式响应、快捷问题 |
| 👤 **我的** | 个人中心、使用指南、关于我们 |

---

## 🛣️ 路线图 (Roadmap)

- [ ] 用户系统（登录/注册）
- [ ] 行程历史记录保存
- [ ] 行程导出 PDF
- [ ] 图片/短视频景点介绍
- [ ] 多语言支持（中/英/日/韩）
- [ ] 天气 API 集成
- [ ] 机票/酒店比价功能

---

## 🤝 贡献指南

欢迎贡献代码！请按以下步骤：

1. Fork 本仓库
2. 创建新分支：`git checkout -b feature/AmazingFeature`
3. 提交改动：`git commit -m 'Add some AmazingFeature'`
4. 推送到分支：`git push origin feature/AmazingFeature`
5. 发起 Pull Request

---

## 📄 开源协议

本项目基于 [MIT License](LICENSE) 协议开源，请自由使用。

---

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 后端框架
- [Vue 3](https://vuejs.org/) - 前端框架
- [Vant UI](https://vant.pro/) - 移动端组件库
- [DeepSeek](https://www.deepseek.com/) - 大语言模型
- [硅基流动](https://www.siliconflow.cn/) - 大语言模型服务

---

<div align="center">

**Made with ❤️ by 贺雪峰**

如果你觉得这个项目不错，别忘了 ⭐ Star 支持一下！

</div>
