# Mo

一个简洁的 Android 待办和备忘录应用，使用 Kotlin + Jetpack Compose 构建。

## 功能

**待办列表**
- 创建、编辑、删除待办事项
- 三种优先级（高/中/低），卡片左侧色条区分
- 按标签筛选（工作/个人/购物/全部）
- 设置到期时间，WorkManager 后台提醒
- 左滑删除，勾选完成（带缩放动画）

**备忘录**
- 创建、编辑、删除备忘录
- 星标收藏，7 种颜色标签
- 按分类筛选（便签/阅读笔记/项目）
- 列表视图和网格视图切换
- 网格列数自适应屏幕宽度（手机 2 列，平板 3-4 列）

**个性化**
- 5 套莫兰迪配色主题（森林绿/深海蓝/晚霞橙/薰衣草紫/玫瑰金）
- 浅色/深色/跟随系统
- 字体大小调节
- 卡片圆角风格

**数据管理**
- JSON 导出/导入
- WebDAV 云端同步
- 默认提醒时间设置
- 标签管理

## 技术

- Kotlin 2.1.10
- Jetpack Compose + Material 3
- Room 数据库
- Hilt 依赖注入
- WorkManager 后台任务
- DataStore 偏好存储

## 构建

环境：JDK 21，Android SDK 35。

```bash
./gradlew assembleDebug
```

APK 输出在 `app/build/outputs/apk/debug/`。

## 目录

```
app/src/main/java/com/mo/todo/
├── MainActivity.kt
├── MoApplication.kt
├── data/
│   ├── model/     Todo.kt  Memo.kt
│   ├── dao/       TodoDao.kt  MemoDao.kt
│   └── database/  AppDatabase.kt
├── di/
├── repository/
├── ui/
│   ├── theme/     Color.kt  Type.kt  Theme.kt
│   ├── navigation/
│   ├── component/  Common.kt
│   ├── screen/
│   │   ├── MainScreen.kt
│   │   ├── todo/
│   │   ├── memo/
│   │   └── profile/
│   └── viewmodel/
└── worker/  ReminderWorker.kt
```

## License

MIT
