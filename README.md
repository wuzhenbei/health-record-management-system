# 社区医疗健康档案管理系统

基于 Spring Boot 3 + Vue 3 + Element Plus + MySQL 8 的前后端分离社区医疗健康档案管理系统。

## 环境要求

| 依赖 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |

## 快速启动

### 1. 初始化数据库

使用 MySQL 客户端（Navicat / DBeaver / 命令行）执行以下脚本：

```sql
source sql/community_health.sql
```

或直接在 MySQL 命令行中运行：

```bash
mysql -u root -p < sql/community_health.sql
```

> 默认数据库名：`community_health`

### 2. 修改后端数据库配置

编辑 `backend/src/main/resources/application.yml`，将数据库用户名和密码改为本地配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/community_health?...
    username: root      # 修改为你的 MySQL 用户名
    password: 123456    # 修改为你的 MySQL 密码
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问：
- API 服务：http://localhost:8080
- 接口文档（Knife4j）：http://localhost:8080/doc.html

### 4. 安装前端依赖并启动

```bash
cd frontend
npm install
npm run dev
```

前端访问地址：http://localhost:5173

## 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 管理员 |
| doctor01 | 123456 | 医生（张医生） |
| doctor02 | 123456 | 医生（李医生） |
| resident01 | 123456 | 居民（王建国） |
| resident02 | 123456 | 居民（李秀英） |
| resident03 | 123456 | 居民（陈明） |

## 功能模块

### 管理员
- 统计仪表盘（数据卡片 + ECharts 图表）
- 系统用户管理（CRUD、启停用、重置密码）
- 居民档案管理
- 慢病管理
- 随访记录

### 医生
- 统计仪表盘
- 居民档案管理（新增/编辑/查看详情）
- 档案详情：健康指标、病史、慢病、随访记录
- 慢病管理
- 随访记录

### 居民
- 查看自己的健康档案
- 查看体检记录、病史、随访记录（只读）

## 项目结构

```
├── backend/                    Spring Boot 3 后端
│   ├── pom.xml
│   └── src/main/java/com/health/
│       ├── HealthApplication.java
│       ├── common/             统一响应/异常处理
│       ├── config/             Security/CORS/MyBatis配置
│       ├── security/           JWT工具/过滤器/用户上下文
│       └── module/
│           ├── auth/           登录认证接口
│           ├── user/           系统用户管理
│           ├── resident/       居民档案管理
│           ├── health/         健康指标（体检）
│           ├── medical/        病史/就诊记录
│           ├── chronic/        慢病登记管理
│           ├── followup/       随访记录管理
│           └── stats/          统计分析
├── frontend/                   Vue 3 前端
│   ├── src/
│   │   ├── api/                接口封装
│   │   ├── assets/             全局样式
│   │   ├── layout/             后台主布局
│   │   ├── router/             路由配置（含权限守卫）
│   │   ├── stores/             Pinia状态管理
│   │   ├── utils/              Axios请求封装
│   │   └── views/
│   │       ├── Login.vue
│   │       ├── Dashboard.vue
│   │       ├── admin/          用户管理页面
│   │       ├── resident/       居民档案/详情/居民端
│   │       ├── chronic/        慢病管理页面
│   │       └── followup/       随访记录页面
└── sql/
    └── community_health.sql    建库建表 + 示例数据
```

## 主要技术栈

**后端**
- Spring Boot 3.2 + Spring Security + JWT
- MyBatis-Plus 3.5（分页/逻辑删除/自动填充）
- MySQL 8.0
- Knife4j（OpenAPI 3 接口文档）
- Lombok + Hutool

**前端**
- Vue 3 + TypeScript + Vite
- Element Plus + Element Plus Icons
- Pinia（状态管理）
- Vue Router 4（路由守卫）
- Axios（请求拦截/响应拦截）
- ECharts 5（统计图表）
