# 体重管理助手 (Weight Manager)

## 项目简介

体重管理助手是一款基于 Kotlin + Jetpack Compose 开发的 Android 体重管理应用，帮助用户记录和管理每日体重数据，查询食品营养信息，追踪体重变化趋势。

## 运行环境

- Android Studio: Ladybug 或更新版本
- Gradle: 8.9+
- Kotlin: 2.1.0
- minSdk: 26
- targetSdk: 35
- compileSdk: 35

## 技术栈

- **UI**: Jetpack Compose + Material 3
- **架构**: MVVM + Repository 模式
- **数据持久化**: Room Database + DataStore
- **网络请求**: Retrofit + OkHttp + Gson
- **异步处理**: Kotlin Coroutines + Flow
- **导航**: Compose Navigation
- **图片加载**: Coil

## 核心功能

1. 体重记录管理（增删改查）
2. 体重变化趋势统计（折线图）
3. BMI 指数计算
4. 食品营养信息查询（OpenFoodFacts API）
5. 搜索与筛选体重记录
6. 用户偏好设置（主题、单位等）
7. 深色/浅色模式支持

## 快速开始

1. 用 Android Studio 打开项目
2. 同步 Gradle 依赖
3. 连接模拟器或真机
4. 点击 Run 运行

## 项目结构

```
app/src/main/java/com/example/weightmanager/
├── MainActivity.kt
├── data/
│   ├── entity/          # Room Entity 定义
│   ├── dao/             # Room DAO 接口
│   ├── database/        # Room 数据库
│   ├── network/         # 网络层 (Retrofit + DTO)
│   └── repository/      # Repository 层
├── datastore/           # DataStore 偏好存储
├── navigation/          # 导航路由
├── ui/
│   ├── screens/         # 各页面 Composable
│   ├── components/      # 可复用组件
│   └── theme/           # Material 3 主题
└── viewmodel/           # ViewModel + UiState
```
