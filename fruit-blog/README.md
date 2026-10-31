# 🍓 水果小百科 · 学习用静态网站项目

一个用来**学习 GitHub Actions 部署**的示例项目：用 **Spring Boot（后端）** + **Vue 3（前端）** 做一个「水果介绍」网站。

- 前台首页：卡片列表展示所有水果文章，点进去看详情（支持多图）。
- 后台（admin）：一个入口页面，填写标题/正文并上传图片即可发布文章。
- 暂无登录注册（按你的要求），打开 `/admin` 就能写。

> 说明：本项目把「前端构建出来的静态文件」作为静态网站部署到 GitHub Pages；后端 Spring Boot 负责文章接口和图片上传，需要单独运行/部署。下面会讲清楚两者关系，非常适合入门。

---

## 一、目录结构

```
fruit-blog/
├── backend/                # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/example/fruitblog/
│       ├── FruitBlogApplication.java   # 启动类
│       ├── Article.java                # 文章实体
│       ├── ArticleRepository.java      # JPA 仓库
│       ├── ArticleController.java      # 文章接口 + 图片上传
│       └── WebConfig.java              # 静态资源映射 + 跨域
│   └── src/main/resources/
│       ├── application.properties      # 端口/上传目录/数据库
│       └── data.sql                    # 启动时灌入 2 篇示例文章
├── frontend/               # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js      # 开发代理 / 构建配置
│   ├── index.html
│   └── src/
│       ├── main.js
│       ├── App.vue                    # 顶栏 + 路由出口
│       ├── router/index.js
│       ├── api/article.js             # 调用后端接口
│       ├── views/Home.vue             # 首页列表
│       ├── views/ArticleDetail.vue    # 文章详情
│       ├── views/Admin.vue            # 写文章（含图片上传）
│       └── styles/main.css
├── .github/workflows/deploy.yml   # GitHub Actions 工作流（核心学习文件）
└── README.md
```

---

## 二、本地运行（先看效果）

需要：JDK 17+、Node.js 20+。

### 1) 启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

看到 `Started FruitBlogApplication` 即成功。打开 http://localhost:8080/api/articles 能看到示例文章（JSON）。
上传的图片会存到 `backend/uploads/`，通过 `/uploads/文件名` 访问。

### 2) 启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

打开 http://localhost:5173 ：
- 首页：看到示例水果文章卡片。
- 点卡片 → 文章详情页。
- 右上角「✍️ 写文章」→ `/admin`，填标题、正文，选 1 张或多张图片，点「发布文章」→ 自动跳到新文章。

> 本地之所以不用管跨域，是因为 `vite.config.js` 里把 `/api` 和 `/uploads` 代理到了 8080 后端。

---

## 三、用 GitHub Actions 部署（重点学习）

### 3.1 把项目推到 GitHub

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
之后每次 push，Actions 会自动：构建前端 → 部署到 Pages，并生成一个可访问的网址（如 `https://<用户名>.github.io/<仓库名>/`）。

### 3.3 工作流在做什么（对照 `deploy.yml` 看）

| 概念 | 在本项目里的对应 |
|------|----------------|
| **Workflow** | 整个 `deploy.yml`，文件名即工作流名 |
| **Job** | `build`（构建）和 `deploy`（部署）两个任务，串行（`needs: build`） |
| **Step** | 每个 Job 里的一条条 `run` / `uses` 动作 |
| **Action** | `actions/checkout@v4`、`actions/setup-node@v4` 等可复用组件 |
| **Artifact** | 构建产物：`frontend/dist`（给 Pages）和 `backend-jar`（JAR 包） |
| **Environment** | `github-pages`，Pages 部署的环境 |

关键权限：`permissions` 里必须给 `pages: write` 和 `id-token: write`，否则部署会失败。

### 3.4 一个重要的认知：静态前端 vs 后端

部署到 Pages 的**只是前端静态文件**。它默认走相对路径 `/api` 调接口——但 Pages 是纯静态托管，没有后端，所以：

- 在 Pages 上**浏览文章**需要后端接口可达（见 3.5）。
- 在 Pages 上**用 admin 发布文章**也必须连到真实后端。

这是“静态网站”和“带后端的应用”的本质区别，理解了这点，你就掌握了部署的核心。

### 3.5 让 Pages 上的功能真正可用（二选一）

**方案 A（推荐学习）：把后端也部署出去**
把 `backend-jar` 这个产物下载下来，跑在任意一台有 Java 的服务器 / 云平台（如 Render、Railway、阿里云等）：
```bash
java -jar fruit-blog-1.0.0.jar
```
然后在 `deploy.yml` 的「构建前端」步骤里把 `VITE_API_BASE` 改成你的后端地址：
```yaml
env:
  VITE_API_BASE: 'https://你的后端地址'
```
重新 push，Pages 上的前端就会去这个地址取数据和上传图片。

**方案 B（纯静态思路，进阶）：把数据构建进前端**
不用运行时后端，改为：文章写成 Markdown/JSON 文件，在 `npm run build` 阶段生成静态页面（类似 VitePress / Hugo）。这属于“无后端静态站”，适合只需要展示、不需要在线发布的场景。

---

## 四、常见坑

1. **前端白屏 / 接口报错**：确认后端在跑；本地用 proxy，部署后注意 `VITE_API_BASE`。
2. **图片上传 413 / 失败**：默认限制单文件 10MB（`application.properties` 里可改）。
3. **`npm ci` 报错**：仓库里没有 `package-lock.json` 时，工作流已用 `npm ci || npm install` 兜底。
4. **Pages 部署失败**：检查仓库 `Settings → Actions → General` 里工作流权限是否为 `Read and write`。
5. **首页没文章**：示例数据来自 `data.sql`，只有后端用 H2 内存库时才会灌入；重启后端数据清空（学习用）。

---

## 五、可以继续练手的方向

- 给 admin 加一个简单的密码保护（前端传 token，后端校验）。
- 把图片存到对象存储（OSS / S3）而不是本地目录。
- 用富文本编辑器（如 TipTap）实现“正文中插入图片”。
- 把前端直接打进 Spring Boot 的 `static/` 目录，打包成一个 JAR 整体部署（前后端同端口同源，最省心）。
