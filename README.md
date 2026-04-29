# Mo — 待办 & 备忘录

Android 端待办+备忘录，Kotlin / Compose / Material 3.

## 功能

- 待办：增删改查，优先级（高/中/低），标签筛选，到期提醒，左滑删除
- 备忘：Markdown 快捷按钮，星标，7 色标签，网格/列表切换
- 主题：5 套莫兰迪配色，浅色/深色模式，Material You 动态取色
- 备份：JSON 导出导入，WebDAV 同步
- 其他：WorkManager 定时提醒，DataStore 持久化设置

## 技术

Kotlin 2.1.10 · Compose 1.7+ · Room 2.6.1 · Hilt 2.55 · WorkManager 2.10.0 · DataStore · Octicons 1.1.1

## 构建

```bash
# JDK 21, Android SDK 35
./gradlew assembleDebug
```

输出：`app/build/outputs/apk/debug/app-debug.apk`，Min API 34.

## 项目结构

```
app/src/main/java/com/mo/todo/
├── MainActivity.kt
├── MoApplication.kt
├── data/
│   ├── model/     Todo.kt / Memo.kt
│   ├── dao/       TodoDao.kt / MemoDao.kt
│   └── database/  AppDatabase.kt
├── di/            Hilt Modules
├── repository/    TodoRepository.kt / MemoRepository.kt
├── ui/
│   ├── theme/     Color.kt / Type.kt / Theme.kt
│   ├── navigation/ BottomNavItem.kt / MoNavHost.kt
│   ├── component/ Common.kt
│   ├── screen/
│   │   ├── MainScreen.kt
│   │   ├── todo/  TodoScreen.kt / AddEditTodoScreen.kt
│   │   ├── memo/  MemoScreen.kt / AddEditMemoScreen.kt
│   │   └── profile/ Profile / About / Label / Reminder / WebDAV / Personalization
│   └── viewmodel/ TodoViewModel / MemoViewModel / SettingsViewModel
└── worker/        ReminderWorker.kt
```

## 配色

5 套可选主题，点缀色珊瑚橙 `#E88A6E`:

| 主题 | 浅色 | 深色 |
|------|------|------|
| 森林绿 | `#5B7F6A` | `#8BB59A` |
| 深海蓝 | `#3A7CA5` | `#6EA8CF` |
| 晚霞橙 | `#E0724B` | `#F0A080` |
| 薰衣草紫 | `#7B6B9E` | `#B0A0C8` |
| 玫瑰金 | `#C0766A` | `#E0A898` |

## License

MIT
