<div align="center">
  <h1>Mo</h1>
  <p><strong>待办 · 备忘录 · 提醒</strong></p>

  <p>
    <img alt="Android" src="https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android&logoColor=white">
    <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.1.10-7F52FF?logo=kotlin&logoColor=white">
    <img alt="Compose" src="https://img.shields.io/badge/Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white">
    <img alt="License" src="https://img.shields.io/badge/License-MIT-green">
    <img alt="API" src="https://img.shields.io/badge/API-34%2B-blue">
  </p>

  <p>一款简洁优雅的 Android 待办与备忘录应用，采用 Material 3 设计语言，专注本地体验与数据隐私。</p>
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

## 功能特性

### 待办管理

- 创建、编辑、删除待办项，支持标题 + 详情备注
- 三级优先级标识（高/中/低），色条一目了然
- 标签分类筛选（工作/个人/购物/便签等），支持自定义标签
- 快捷提醒（今天/明天 09:00/下周）+ 精确日期时间选择
- 左滑删除 + Snackbar 撤销恢复
- 勾选完成动画，已完成区可折叠
- 关键词实时搜索，按优先级或创建时间排序

### 备忘录

- 创建、编辑、删除备忘录，支持星标收藏
- 7 种颜色标签 + 分类筛选
- 网格/列表视图切换，网格列数自适应（手机 2 列，平板 3-4 列）
- Markdown 富文本编辑（加粗/斜体/删除线/列表）
- 关键词搜索

### 智能提醒

- **双保险调度**：AlarmManager 精确闹钟 + WorkManager 备用，兼容 MIUI/EMUI/ColorOS
- 锁屏全屏弹出通知 + Heads-up 横幅提醒
- 设备重启后自动重调度所有未触发提醒
- 点击通知直接跳转对应待办
- 电池优化检测与自启动引导

### 个性化

- 5 套莫兰迪配色主题（森林绿 / 深海蓝 / 晚霞橙 / 薰衣草紫 / 玫瑰金）
- Material You 动态取色（从壁纸提取配色）
- 浅色 / 深色 / 跟随系统三种模式
- 字体大小（小/中/大）、圆角风格（圆润/方正）可调
- 列表密度（紧凑/标准/放松）
- 新建待办默认优先级设置
- 通知振动开关

### 数据管理

- JSON 本地导出导入
- WebDAV 云端备份与恢复
- 标签管理：增删改重命名 + 批量操作

### 个人中心

- 时段问候语 + 数据概览卡片（待办/进行中/已完成/备忘录/收藏统计 + 完成率进度条）
- 自定义头像（首字母占位）+ 昵称修改
- 功能分组菜单：标签管理、提醒设置、个性化、数据管理
- 一键分享应用给朋友

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin 2.1.10 |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Flow / StateFlow |
| 数据库 | Room 2.6.1 |
| DI | Hilt 2.55 |
| 后台任务 | WorkManager 2.10.0 + AlarmManager |
| 偏好存储 | DataStore Preferences |
| 网络 | OkHttp 4 (WebDAV) |
| 图片加载 | Coil |
| 最低 SDK | 34 (Android 14) |

## 构建

```bash
# 需要 JDK 21, Android SDK 35
./gradlew assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

## 项目结构

```
app/src/main/java/com/mo/todo/
├── MainActivity.kt                 # 入口 Activity
├── MoApplication.kt                # Application，初始化通知渠道
├── data/
│   ├── model/                      # Todo / Memo 数据模型
│   ├── dao/                        # TodoDao / MemoDao
│   ├── database/                   # AppDatabase (Room)
│   ├── repository/                 # TodoRepository / MemoRepository
│   └── network/                    # WebDavClient
├── di/                             # Hilt 依赖注入模块
├── ui/
│   ├── theme/                      # Color / Type / Theme (莫兰迪配色系统)
│   ├── navigation/                 # BottomNavItem / MoNavHost / MoRoutes
│   ├── component/                  # 通用组件 (TodoItemRow 等)
│   ├── screen/
│   │   ├── MainScreen.kt           # 主界面 (底部导航 + NavHost)
│   │   ├── todo/                   # TodoScreen / AddEditTodoScreen
│   │   ├── memo/                   # MemoScreen / AddEditMemoScreen
│   │   └── profile/                # Profile / About / Label / Personalization / Reminder / WebDAV / Data
│   └── viewmodel/                  # TodoVM / MemoVM / SettingsVM
└── worker/
    ├── AlarmReceiver.kt            # 闹钟广播接收器
    ├── BootReceiver.kt             # 开机广播，重调度提醒
    ├── NotificationHelper.kt       # 通知构建与发送
    ├── ReminderScheduler.kt        # 提醒调度器 (AlarmManager + WorkManager)
    └── ReminderWorker.kt           # WorkManager Worker
```

## 许可证

[MIT](LICENSE)
