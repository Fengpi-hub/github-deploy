# 🍓 水果小百科 · Spring Boot + Vue 全栈示例项目

一个用 **Spring Boot（后端）** + **Vue 3（前端）** 打造的「水果介绍」全栈小项目，具备**登录注册**和**文章完整增删改查（CRUD）**，后端使用 **SQL 数据库（H2 / PostgreSQL）** 持久化数据。适合作为「能跑起来的合格小项目」入门。

功能清单：

- ✅ **注册 / 登录**：基于 Spring Security + JWT（BCrypt 加密密码，令牌无状态）。
- ✅ **文章增删改查（CRUD）**：
  - 查（公开）：首页列表、文章详情。
  - 增（需登录）：写文章，支持多图上传。
  - 改（需登录 + 作者本人）：编辑标题/正文，替换或清除图片。
  - 删（需登录 + 作者本人）：删除文章及关联图片。
- ✅ **权限控制**：只能修改 / 删除自己发布的文章。
- ✅ **SQL 数据库**：开发用文件型 H2（重启不丢数据），生产用 PostgreSQL。

> 本项目把「前端构建出来的静态文件」作为静态网站部署到 GitHub Pages；后端 Spring Boot 负责接口、鉴权和图片上传，需要单独运行/部署。下面会讲清楚两者关系。

---

## 一、目录结构

```
fruit-blog/
├── backend/                # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/example/fruitblog/
│       ├── FruitBlogApplication.java   # 启动类
│       ├── Article.java                # 文章实体（标题/正文/图片/作者/时间）
│       ├── ArticleRepository.java      # JPA 仓库
│       ├── ArticleController.java      # 文章 CRUD 接口 + 图片上传
│       ├── User.java / UserRepository.java   # 用户实体与仓库
│       ├── AuthController.java / AuthService.java  # 登录注册
│       ├── JwtService / JwtAuthFilter / SecurityConfig  # JWT 认证
│       ├── UserDetailsServiceImpl / GlobalExceptionHandler / DataSeeder
│       └── WebConfig.java              # 静态资源映射
│   └── src/main/resources/
│       ├── application.properties      # 端口/上传目录/H2/JWT
│       └── application-prod.properties # 生产(PG)配置
├── frontend/               # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js      # 开发代理 / 构建配置
│   ├── index.html
│   └── src/
│       ├── main.js
│       ├── App.vue                    # 顶栏(含登录态) + 路由出口
│       ├── auth.js                    # 全局登录态(store)
│       ├── router/index.js            # 路由 + 登录守卫
│       ├── api/http.js                # axios + JWT 拦截器
│       ├── api/article.js             # 文章接口(增删改查)
│       ├── api/auth.js                # 登录注册接口
│       ├── views/Home.vue             # 首页列表
│       ├── views/ArticleDetail.vue    # 文章详情(作者可见编辑/删除)
│       ├── views/Admin.vue            # 写文章（增）
│       ├── views/EditArticle.vue      # 编辑文章（改）
│       ├── views/Login.vue / Register.vue
│       └── styles/main.css
├── .github/workflows/deploy.yml   # GitHub Actions 工作流
└── README.md
```

---

## 二、本地运行

需要：JDK 17+、Node.js 20+。

### 1) 启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

看到 `Started FruitBlogApplication` 即成功。首次启动会自动建表并灌入示例数据：
- 管理员账号：`admin` / `admin123`
- 两篇示例水果文章（作者为 admin）

打开 http://localhost:8080/api/articles 能看到示例文章（JSON）。上传的图片存到 `backend/uploads/`，通过 `/uploads/文件名` 访问。数据保存在 `backend/data/`（文件型 H2），**重启不会丢失**。

### 2) 启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

打开 http://localhost:5173 ：
- 首页：看到示例水果文章卡片。
- 点卡片 → 文章详情页（作者本人可见「编辑 / 删除」）。
- 右上角「注册 / 登录」→ 登录后才能「✍️ 写文章」「编辑」「删除」。

> 本地跨域由 `vite.config.js` 把 `/api` 和 `/uploads` 代理到 8080 解决；生产跨域由后端 SecurityConfig 的 CORS 配置处理。

---

## 三、用 GitHub Actions 部署（重点学习）

### 3.1 推送到 GitHub

```bash
git init
git add .
git commit -m "fruit blog first commit"
git branch -M main
git remote add origin <你的仓库地址>
git push -u origin main
```

推送 `main` 分支后，`deploy.yml` 会自动运行。

### 3.2 开启 GitHub Pages

仓库 `Settings → Pages → Build and deployment → Source` 选择 **GitHub Actions**。

### 3.3 让 Pages 上的功能真正可用（二选一）

**方案 A（推荐）：把后端也部署出去**（如 Render / Railway）
```bash
java -jar fruit-blog-1.0.0.jar
```
然后在 `deploy.yml` 的「构建前端」步骤把 `VITE_API_BASE` 改成你的后端地址：
```yaml
env:
  VITE_API_BASE: 'https://你的后端地址'
```
注意：生产环境务必通过环境变量 `JWT_SECRET`（≥32 字符）设置 JWT 密钥，并在 Render 上设置 `SPRING_PROFILES_ACTIVE=prod`。

**方案 B（纯静态）**：把数据构建进前端（Markdown/JSON → 静态页），无需运行时后端。

---

## 四、常见坑

1. **前端白屏 / 接口报错**：确认后端在跑；本地用 proxy，部署后注意 `VITE_API_BASE`。
2. **401 未登录**：写/改/删文章需要先登录；令牌存于 localStorage，过期或被服务端拒绝会自动登出。
3. **403 无权限**：只能修改 / 删除自己发布的文章。
4. **图片上传 413 / 失败**：默认限制单文件 10MB（`application.properties` 可改）。
5. **数据丢失**：开发用文件型 H2 存于 `backend/data/`，正常重启不丢；若删除该目录则重置（示例数据会重新灌入）。

---

## 五、可以继续练手的方向

- 把图片存到对象存储（OSS / S3）而不是本地目录（Render 文件系统是临时的）。
- 用富文本编辑器（如 TipTap）实现“正文中插入图片”。
- 给文章加分类 / 标签 / 分页 / 搜索。
- 把前端直接打进 Spring Boot 的 `static/` 目录，打包成一个 JAR 整体部署（前后端同源，最省心）。
- 增加「管理员可删除任意文章」的角色权限。
