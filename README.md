<div align="center">
  <h1>Mo</h1>
  <p><strong>待办 · 备忘录 · 提醒</strong></p>
  <p><strong>Todo · Memo · Reminder</strong></p>

  <p>
    <img alt="Android" src="https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android&logoColor=white">
    <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.1.10-7F52FF?logo=kotlin&logoColor=white">
    <img alt="Compose" src="https://img.shields.io/badge/Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white">
    <img alt="License" src="https://img.shields.io/badge/License-MIT-green">
    <img alt="API" src="https://img.shields.io/badge/API-34%2B-blue">
  </p>

  <p>一款简洁优雅的 Android 待办与备忘录应用，采用 Material 3 设计语言，专注本地体验与数据隐私。</p>
  <p>A clean and elegant Android todo & memo app built with Material 3, focused on local experience and data privacy.</p>

  <p>
    <a href="#功能特性">中文</a> | <a href="#features">English</a>
  </p>
</div>

---

> **⚠️ 已知问题 | Known Issue**
>
> 当应用不在后台运行时，提醒通知可能无法正常触发。这是由于本人技术水平有限，目前尚未找到可靠的解决方案。如果你有更好的思路，欢迎提 Issue 或 PR，非常感谢！
>
> Reminders may not fire when the app is not running in the background. This is due to my limited technical knowledge and I haven't found a reliable solution yet. If you have any ideas, feel free to open an Issue or submit a PR — thank you!
>
> ---
>
> **📌 项目状态 | Project Status**
>
> 应用主体框架和功能已全部搭建完成，核心功能均可正常使用。
>
> The main framework and all core features have been fully built and are functional.

---

<a id="功能特性"></a>

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
- 关键词搜索

### 智能提醒

- **三重保障调度**：AlarmManager 精确闹钟 + WorkManager 备用 + 每 15 分钟定期扫描兜底
- 通知渠道每次启动自动重建（解决渠道设置陈旧问题）
- 通知优先级 `CATEGORY_ALARM`，锁屏全屏弹出 + Heads-up 横幅
- 设备重启后自动重调度所有未触发提醒
- 点击通知直接跳转对应待办
- 电池优化检测 / 自启动引导 / 通知渠道设置入口
- **测试通知**：一键发送测试通知验证通知功能是否正常

### 个性化

- 多套莫兰迪配色主题（森林绿 / 深海蓝 / 晚霞橙 / 薰衣草紫 / 玫瑰金 等）
- Material You 动态取色（从壁纸提取配色）
- 浅色 / 深色 / 跟随系统三种模式
- 字体大小（小/中/大）可调
- 圆角风格（圆润/方正/超圆润）可调
- 列表密度（紧凑/标准/放松）
- 新建待办默认优先级设置
- 通知振动开关

### 多语言

- 支持 **中文 / English** 双语切换
- 在设置页面自由选择语言（跟随系统 / 中文 / English）
- 自动检测系统语言并匹配

### 数据管理

- JSON 本地导出导入
- WebDAV 云端备份与恢复
- 标签管理：增删改重命名 + 批量操作

### 个人中心

- 时段问候语 + 数据概览卡片（待办/进行中/已完成/备忘录/收藏统计 + 完成率进度条）
- 自定义头像（首字母占位）+ 昵称修改
- 功能分组菜单：标签管理、提醒设置、个性化、数据管理
- 一键分享应用给朋友

### 法律与隐私

- 应用内内置免责声明、用户协议、隐私政策、开源协议页面
- 仓库提供独立 .md 文件：[DISCLAIMER.md](DISCLAIMER.md) | [TERMS.md](TERMS.md) | [PRIVACY.md](PRIVACY.md)

---

<a id="features"></a>

## Features

### Todo Management

- Create, edit, and delete todos with title and detailed notes
- Three-level priority indicators (High/Medium/Low) with color-coded bars
- Label-based filtering (Work/Personal/Shopping/Notes etc.), with custom label support
- Quick reminders (Today/Tomorrow 09:00/Next week) + precise date-time picker
- Swipe-to-delete with Snackbar undo
- Completion animation, collapsible completed section
- Real-time keyword search, sort by priority or creation time

### Memo

- Create, edit, and delete memos with star/favorite support
- 7 color labels + category filtering
- Grid/List view toggle with adaptive columns (2 for phones, 3-4 for tablets)
- Keyword search

### Smart Reminders

- **Triple-safeguard scheduling**: AlarmManager precise alarms + WorkManager backup + 15-minute periodic scan fallback
- Notification channels auto-rebuilt on every launch (fixes stale channel settings)
- Notification priority `CATEGORY_ALARM`, full-screen lock-screen popup + Heads-up banner
- Auto-reschedule all pending reminders after device reboot
- Tap notification to navigate directly to the corresponding todo
- Battery optimization detection / Auto-launch guidance / Notification channel settings
- **Test notification**: One-tap test notification to verify notification functionality

### Personalization

- Multiple Morandi color themes (Forest Green / Deep Ocean Blue / Sunset Orange / Lavender Purple / Rose Gold, etc.)
- Material You dynamic color extraction from wallpaper
- Light / Dark / Follow system theme modes
- Font size (Small/Medium/Large) adjustable
- Corner style (Rounded/Square/Extra Rounded) adjustable
- List density (Compact/Standard/Relaxed)
- Default priority setting for new todos
- Notification vibration toggle

### Multi-language

- **Chinese / English** bilingual support
- Free language switching in Settings (Follow System / Chinese / English)
- Automatic system language detection and matching

### Data Management

- JSON local export/import
- WebDAV cloud backup and restore
- Label management: add, edit, rename, delete + batch operations

### Profile

- Time-based greeting + data overview card (Todos/In Progress/Completed/Memos/Favorites statistics + completion rate progress bar)
- Custom avatar (initials placeholder) + nickname editing
- Grouped function menu: Label Management, Reminder Settings, Personalization, Data Management
- One-tap share app with friends

### Legal & Privacy

- In-app pages for Disclaimer, Terms of Service, Privacy Policy, and Open Source License
- Standalone .md files in the repository: [DISCLAIMER.md](DISCLAIMER.md) | [TERMS.md](TERMS.md) | [PRIVACY.md](PRIVACY.md)

---

## 技术栈 | Tech Stack

| 类别 | Category | 技术 | Technology |
|------|----------|------|------------|
| 语言 | Language | Kotlin 2.1.10 | Kotlin 2.1.10 |
| UI 框架 | UI Framework | Jetpack Compose + Material 3 | Jetpack Compose + Material 3 |
| 架构 | Architecture | MVVM + Flow / StateFlow | MVVM + Flow / StateFlow |
| 数据库 | Database | Room 2.6.1 | Room 2.6.1 |
| 依赖注入 | DI | Hilt 2.55 | Hilt 2.55 |
| 后台任务 | Background | WorkManager 2.10.0 + AlarmManager | WorkManager 2.10.0 + AlarmManager |
| 偏好存储 | Preferences | DataStore Preferences | DataStore Preferences |
| 网络 | Network | OkHttp 4 (WebDAV) | OkHttp 4 (WebDAV) |
| 多语言 | i18n | AppCompat Per-App Language API | AppCompat Per-App Language API |
| 最低 SDK | Min SDK | 34 (Android 14) | 34 (Android 14) |

## 构建 | Build

```bash
# 需要 JDK 21, Android SDK 35 | Requires JDK 21, Android SDK 35
./gradlew assembleDebug
```

APK 输出路径 | APK output: `app/build/outputs/apk/debug/app-debug.apk`

## 安装 | Installation

从 [Releases](https://github.com/MoYuan00MoYuan/Mo-Todo/releases) 页面下载最新的 APK 文件。

Download the latest APK from the [Releases](https://github.com/MoYuan00MoYuan/Mo-Todo/releases) page.

## 项目结构 | Project Structure

```
app/src/main/java/com/mo/todo/
├── MainActivity.kt                 # 入口 Activity | Entry Activity
├── MoApplication.kt                # Application，初始化通知渠道 | Init notification channels
├── data/
│   ├── model/                      # Todo / Memo 数据模型 | Data models
│   ├── dao/                        # TodoDao / MemoDao
│   ├── database/                   # AppDatabase (Room)
│   ├── repository/                 # TodoRepository / MemoRepository
│   └── network/                    # WebDavClient
├── di/                             # Hilt 依赖注入 | DI modules
├── ui/
│   ├── theme/                      # Color / Type / Theme (莫兰迪配色 | Morandi palette)
│   ├── navigation/                 # BottomNavItem / MoNavHost / MoRoutes
│   ├── component/                  # 通用组件 | Common components
│   ├── screen/
│   │   ├── MainScreen.kt           # 主界面 | Main screen (bottom nav + NavHost)
│   │   ├── todo/                   # TodoScreen / AddEditTodoScreen
│   │   ├── memo/                   # MemoScreen / AddEditMemoScreen
│   │   └── profile/                # Profile / About / Legal / Label / Personalization / Reminder / WebDAV / Data
│   └── viewmodel/                  # TodoVM / MemoVM / SettingsVM / StatsVM
└── worker/
    ├── AlarmReceiver.kt            # 闹钟广播接收器 | Alarm broadcast receiver
    ├── BootReceiver.kt             # 开机广播，重调度提醒 | Boot receiver, reschedule reminders
    ├── NotificationHelper.kt       # 通知构建与发送 | Notification builder
    ├── ReminderScheduler.kt        # 提醒调度器 | Reminder scheduler
    └── ReminderWorker.kt           # WorkManager Worker
```

## 法律文档 | Legal Documents

| 文档 | Document | 链接 | Link |
|------|----------|------|------|
| 免责声明 | Disclaimer | [中文 & English](DISCLAIMER.md) |
| 用户协议 | Terms of Service | [中文 & English](TERMS.md) |
| 隐私政策 | Privacy Policy | [中文 & English](PRIVACY.md) |
| 开源协议 | Open Source License | [MIT License](LICENSE) |

## 许可证 | License

[MIT](LICENSE)
