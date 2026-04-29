<div align="center">
  <h1>Mo</h1>
  <p>待办 · 备忘 · 专注</p>

  <img alt="Android" src="https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.1.10-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Compose" src="https://img.shields.io/badge/Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-green">
  <img alt="API" src="https://img.shields.io/badge/API-34%2B-blue">
</div>

---

## 截图

<!-- 替换为实际截图 -->
<p align="center">
  <img src="screenshots/home.png" width="24%" />
  <img src="screenshots/todo.png" width="24%" />
  <img src="screenshots/memo.png" width="24%" />
  <img src="screenshots/profile.png" width="24%" />
</p>

## 功能

**待办**
- 创建、编辑、删除待办项，三种优先级色条标识
- 标签筛选（工作/个人/购物/全部）+ 到期时间 + 后台提醒
- 左滑删除，勾选完成缩放动画

**备忘**
- 创建、编辑、删除备忘录，星标收藏
- 7 种颜色标签，按分类筛选，网格/列表视图切换
- 网格列数自适应（手机 2 列，平板 3-4 列）

**个性化**
- 5 套莫兰迪配色（森林绿 / 深海蓝 / 晚霞橙 / 薰衣草紫 / 玫瑰金）
- 浅色 / 深色 / 跟随系统，字体大小和圆角风格可调

**数据**
- JSON 导出导入，WebDAV 云端同步
- 标签管理（增删改重命名 + 批量操作）
- 默认提醒时间设置

## 技术

| 类别 | 依赖 |
|------|------|
| 语言 | Kotlin 2.1.10 |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Flow / StateFlow |
| 数据库 | Room 2.6.1 |
| DI | Hilt 2.55 |
| 后台 | WorkManager 2.10.0 |
| 存储 | DataStore Preferences |
| 最低 SDK | 34 (Android 14) |

## 构建

```bash
# 需要 JDK 21, Android SDK 35
./gradlew assembleDebug
```

APK 输出 `app/build/outputs/apk/debug/app-debug.apk`。

## 结构

```
app/src/main/java/com/mo/todo/
├── MainActivity.kt
├── MoApplication.kt
├── data/
│   ├── model/        Todo / Memo
│   ├── dao/          TodoDao / MemoDao
│   └── database/     AppDatabase
├── di/               Hilt 模块
├── repository/       TodoRepository / MemoRepository
├── ui/
│   ├── theme/        Color / Type / Theme
│   ├── navigation/   BottomNavItem / MoNavHost
│   ├── component/    Common
│   ├── screen/
│   │   ├── todo/     TodoScreen / AddEditTodoScreen
│   │   ├── memo/     MemoScreen / AddEditMemoScreen
│   │   └── profile/  Profile / About / Label / Reminder / WebDAV / ...
│   └── viewmodel/    TodoVM / MemoVM / SettingsVM
└── worker/           ReminderWorker
```

## License

MIT
