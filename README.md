# 商品分销管理系统
## 项目简介
本项目是基于 Vue3 + TypeScript + Vite 开发的**商品分销后台管理系统**，面向平台管理员使用，实现商品管理、订单处理、分销商台账、佣金结算、团队管理、数据看板一体化分销业务管控，适配中小型分销电商业务场景。

## 技术栈
### 前端核心
- 框架：Vue3 + TypeScript
- 构建工具：Vite
- 路由：Vue Router
- 状态管理：Pinia
- HTTP 请求：Axios
- UI组件库：Element Plus
- 样式：SCSS
### 后端核心
- 框架：SSM(Spring Boot、Spring MVC、MyBatis)
- ORM 持久层：MyBatis
- 数据库：MySQL 8.0
- 连接池：jdbc
- 工具依赖
- Lombok：简化实体类 get/set
- JWT：登录令牌鉴权
- 部署：Maven 打包 + Nginx 反向代理
### 配套业务模块
1. 登录/注册模块：账号权限校验、管理员/分销商双角色登录
2. 首页数据看板：订单统计、分销佣金、分销商数据可视化
3. 商品管理：商品新增、上下架、分类、库存维护
4. 分销商管理：分销商信息、等级划分、权限配置
5. 订单管理：订单列表、订单状态流转、售后处理
6. 佣金/分润台账：自动分润计算、佣金提现、结算记录
7. 团队管理：下级分销团队层级、团队业绩统计

## 项目目录结构
```
myvue
├── public              # 静态资源（图标、ico）
├── src
│   ├── layout          # 后台布局框架
│   ├── router          # 路由配置
│   ├── store           # 全局状态管理
│   │   └── user.ts     # 用户登录、权限状态
│   ├── utils           # 工具类
│   │   └── request.ts  # axios请求封装
│   ├── views           # 业务页面
│   │   ├── LoginView.vue        # 登录页
│   │   ├── RegisterView.vue     # 注册页
│   │   ├── dashboard            # 数据看板首页
│   │   ├── product              # 商品管理
│   │   ├── distributor         # 分销商管理
│   │   ├── order                # 订单管理
│   │   ├── commission           # 佣金分润结算
│   │   └── team                # 分销团队管理
│   ├── App.vue
│   └── main.ts         # 项目入口
├── .gitignore          # Git忽略文件配置
├── package.json        # 依赖配置
├── tsconfig系列文件    # TypeScript配置
└── vite.config.ts     # Vite构建配置
```

## 环境部署运行
### 1. 本地启动
```bash
# 安装依赖
npm install
# 本地开发启动
npm run dev
# 打包构建生产包
npm run build
```

### 2. Git 仓库上传（参考）
```bash
git init
git add .
git commit -m "商品分销管理系统初始化提交"
git branch -M main
git remote add origin https://github.com/aimde-bu/personproject.git
git pull --allow-unrelated-histories origin main
git push -u origin main
```

## 系统功能模块说明
### 1. 权限登录模块
- 管理员账号：拥有全平台操作权限，管理全部分销商、商品、结算
- 分销商账号：仅查看自身商品、下级团队、个人佣金与订单
- 登录状态持久化、路由权限拦截

### 2. 数据仪表盘 Dashboard
- 今日/累计订单数量、成交金额统计
- 总分销佣金、待结算佣金可视化图表
- 新增分销商、团队业绩排行展示

### 3. 商品管理 Product
- 商品新增、编辑、删除、批量上下架
- 商品库存预警、价格设置、分销分润比例配置
- 商品分类筛选、搜索分页

### 4. 分销商管理 Distributor
- 分销商信息录入、等级设置（一级/二级分销）
- 分销商状态启用/禁用、绑定上级推荐人
- 分销商业绩、历史结算记录查询

### 5. 订单管理 Order
- 用户下单订单全列表，支持多条件筛选
- 待付款/已付款/已发货/已完成/售后订单状态流转
- 订单详情、物流信息、售后退款处理

### 6. 佣金分润 Commission
- 自动根据分销层级计算分润佣金
- 佣金提现申请、平台审核、结算流水记录
- 佣金账单导出，对账统计

### 7. 分销团队 Team
- 树形展示上下级分销层级关系
- 团队整体业绩、下级成员数据统计
- 团队分销业绩排行

## 开发规范
1. 所有接口请求统一封装在 `utils/request.ts`，统一处理请求头、错误拦截
2. 页面业务代码拆分，视图与逻辑解耦
3. TypeScript 严格类型约束，避免隐式类型报错
4. Git 提交规范：`新增/修改/修复+功能名称`

## 开发说明
1. 项目为前端后台管理页面，需配合后端接口才能完成完整业务交互；
2. 分销分润逻辑、订单结算、权限区分由后端提供接口，前端负责页面展示与操作交互；
3. 打包后的 dist 文件夹可部署至 Nginx、宝塔等静态服务器。

## 开源说明
本项目为个人自研商品分销管理后台系统，仅供学习、二次开发使用，禁止商用倒卖。如有功能优化、Bug修复欢迎提交PR。

## 作者信息
GitHub：aimde-bu
项目仓库：https://github.com/aimde-bu/personproject
```
