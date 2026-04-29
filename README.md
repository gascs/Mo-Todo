# Mo ✦ 待办 & 备忘录

<p align="center">
  <img src="https://img.shields.io/badge/Android-14%2B-34A853?style=flat&logo=android" />
  <img src="https://img.shields.io/badge/Kotlin-2.1.10-7F52FF?style=flat&logo=kotlin" />
  <img src="https://img.shields.io/badge/Compose-Material3-4285F4?style=flat&logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" />
</p>

<p align="center">
  <b>一个简约高级的 Android 效率应用</b><br/>
  莫兰迪配色 · JK少女看板娘 · 自适应平板/折叠屏
</p>

---

## ✨ 特色

|     |     |
|-----|-----|
| 🎨 **莫兰迪色系** | 森林绿、深海蓝、晚霞橙、薰衣草紫、玫瑰金 — 五组低饱和主题，护眼治愈 |
| 🧸 **JK 看板娘** | 水手服少女图标，温柔守望你的每一天 |
| ✅ **智能待办** | 优先级色条、提醒时间、标签分类、滑动删除、完成动画 |
| 📝 **备忘笔记** | Markdown 快捷键、星标收藏、多彩标签、网格/列表双视图 |
| 🌓 **深色模式** | 完美适配浅色/深色，Material You 动态取色 |
| 📱 **响应式布局** | 手机双列网格 → 平板三列 → 折叠屏四列，自适应显示 |
| ☁️ **WebDAV 备份** | 一键导出/导入 JSON，WebDAV 云端同步 |
| 🔔 **提醒通知** | WorkManager 后台精准提醒，准时不错过 |

---

## 🖼 截图

<!-- TODO: 替换为实际截图 -->
<p align="center">
  <i>截图即将更新</i>
</p>

---

## 🧱 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 2.1.10 |
| UI 框架 | Jetpack Compose | 1.7+ |
| 设计系统 | Material Design 3 | — |
| 图标 | Octicons (GitHub) | 1.1.1 |
| 架构 | MVVM + Flow + StateFlow | — |
| 数据库 | Room | 2.6.1 |
| DI | Hilt | 2.55 |
| 后台 | WorkManager | 2.10.0 |
| 存储 | DataStore Preferences | — |
| 构建 | Gradle Kotlin DSL | 9.3.1 |
| AGP | Android Gradle Plugin | 8.9.1 |

---

## 📁 项目结构

```
app/src/main/java/com/mo/todo/
├── MainActivity.kt                  # 入口 Activity
├── MoApplication.kt                 # Hilt Application
├── data/
│   ├── model/   Todo.kt · Memo.kt  # Room Entity
│   ├── dao/     TodoDao · MemoDao  # 数据访问
│   └── database/ AppDatabase.kt     # Room 数据库
├── di/                              # Hilt 依赖注入模块
├── repository/                      # 数据仓库层
├── ui/
│   ├── theme/    Color · Type · Theme    # 配色 · 字体 · 主题
│   ├── navigation/ BottomNavItem · MoNavHost  # 导航
│   ├── component/ Common.kt               # Todo卡片 · Memo卡片 · 通用组件
│   ├── screen/
│   │   ├── MainScreen.kt
│   │   ├── todo/     TodoScreen · AddEditTodoScreen
│   │   ├── memo/     MemoScreen · AddEditMemoScreen
│   │   └── profile/  Profile · About · Label · Reminder · etc.
│   └── viewmodel/    TodoVM · MemoVM · SettingsVM
└── worker/ ReminderWorker.kt        # 提醒后台任务
```

---

## 🎨 配色

| 主题 | 浅色主色 | 深色主色 | 点缀 |
|------|---------|---------|------|
| 🌲 森林绿 | `#5B7F6A` | `#8BB59A` | 珊瑚橙 `#E88A6E` |
| 🌊 深海蓝 | `#3A7CA5` | `#6EA8CF` | 珊瑚橙 |
| 🌅 晚霞橙 | `#E0724B` | `#F0A080` | 珊瑚橙 |
| 💜 薰衣草紫 | `#7B6B9E` | `#B0A0C8` | 珊瑚橙 |
| 🌹 玫瑰金 | `#C0766A` | `#E0A898` | 珊瑚橙 |

---

## 🔧 构建

```bash
# 环境要求
# JDK 21 · Android SDK 35 · Build-Tools 35.0.0

# Debug APK
./gradlew assembleDebug

# 输出路径
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 许可

MIT License

---

<p align="center">
  <sub>Made with 💚 for a more organized life</sub>
</p>
