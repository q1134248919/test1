# 家享生活

家庭共享生活助手。已实现登录注册、家庭群、待办、记忆树。

## 准备

1. 启动 MySQL、Redis
2. 执行 `backend/sql/schema.sql`（已有库还需执行新建的 `family_memory` 表）
3. 按实际账号修改 `backend/src/main/resources/application.yml`

## 启动

后端：`cd backend && set JAVA_HOME=D:\java17 && mvn spring-boot:run`

前端：`cd frontend && npm install && npm run dev`

访问 http://localhost:5173
