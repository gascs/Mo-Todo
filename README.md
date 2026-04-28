# Mo — 待办 & 备忘录

> 包名 `com.mo.todo` · Android 14+ · Kotlin + Jetpack Compose · MVVM

一个简约高级的安卓效率应用，集待办事项与备忘录为一体。

---

## 功能

| 模块 | 功能 |
|------|------|
| **待办 (Todo)** | 增删改查、标记完成、按标签筛选（全部/工作/个人/购物）、优先级（高/中/低）、提醒时间、滑动删除 |
| **备忘 (Memo)** | 增删改查、星标切换、7 色标签、按分类筛选（便签/阅读笔记/项目）、网格/列表视图切换 |
| **我的 (Profile)** | 主题切换（系统/浅色/深色）、标签管理入口、默认提醒设置入口、数据备份导出入口、关于页 |

---

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin 2.1.10 |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM (ViewModel + Flow) |
| 数据库 | Room 2.6.1 |
| 依赖注入 | Hilt 2.55 |
| 后台任务 | WorkManager 2.10.0 |
| 偏好存储 | DataStore Preferences |
| 主题 | Material You 动态取色 + 自定义色板回退 |
| 构建 | Gradle Kotlin DSL + AGP 8.9.1 |

---

## 项目结构

```
app/src/main/java/com/mo/todo/
├── MainActivity.kt                  # 入口 Activity
├── MoApplication.kt                 # Hilt Application + WorkManager 配置
├── data/
│   ├── model/
│   │   ├── Todo.kt                  # Room Entity
│   │   └── Memo.kt                  # Room Entity
│   ├── dao/
│   │   ├── TodoDao.kt               # 待办 DAO
│   │   └── MemoDao.kt               # 备忘 DAO
│   └── database/
│       └── AppDatabase.kt           # Room Database
├── di/
│   ├── DatabaseModule.kt            # Database + DAO Hilt 绑定
│   └── RepositoryModule.kt          # Repository Hilt 绑定
├── repository/
│   ├── TodoRepository.kt            # 待办数据仓库
│   └── MemoRepository.kt            # 备忘数据仓库
├── ui/
│   ├── theme/
│   │   ├── Color.kt                 # 配色方案
│   │   ├── Type.kt                  # 字体排版
│   │   └── Theme.kt                 # 主题入口 + dynamicColor
│   ├── navigation/
│   │   ├── BottomNavItem.kt         # 底部导航定义
│   │   └── MoNavHost.kt            # 导航图
│   ├── component/
│   │   └── Common.kt               # 通用组件（TodoItemRow/MemoCard/EmptyPlaceholder 等）
│   ├── screen/
│   │   ├── MainScreen.kt           # 主框架 Scaffold
│   │   ├── todo/
│   │   │   ├── TodoScreen.kt       # 待办列表
│   │   │   └── AddEditTodoScreen.kt # 新建/编辑待办
│   │   ├── memo/
│   │   │   ├── MemoScreen.kt       # 备忘列表
│   │   │   └── AddEditMemoScreen.kt # 新建/编辑备忘
│   │   └── profile/
│   │       └── ProfileScreen.kt    # 我的/设置
│   └── viewmodel/
│       ├── TodoViewModel.kt         # 待办 ViewModel
│       ├── MemoViewModel.kt         # 备忘 ViewModel
│       └── SettingsViewModel.kt     # 设置 ViewModel
└── worker/
    └── ReminderWorker.kt            # 提醒后台任务
```

---

## 配色

| 模式 | 背景 | 表面 | 主文字 | 主色调 |
|------|------|------|--------|--------|
| 浅色 | `#FAFAF8` | `#FFFFFF` | `#1A1A1A` | `#5B7F6A` |
| 深色 | `#121212` | `#1E1E1E` | `#E8E8E8` | `#8BB59A` |

---

## 构建

```bash
# Debug APK
.\gradlew assembleDebug

# 输出路径
app\build\outputs\apk\debug\app-debug.apk
```

**环境要求**
- JDK 21
- Android SDK 35 + Build-Tools 35.0.0
- Gradle 9.3.1 (自动下载)

---

## 许可

MIT
