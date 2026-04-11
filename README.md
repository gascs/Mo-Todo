# Mo - 待办事项与备忘录管理应用

![Mo Logo](https://img.shields.io/badge/Mo-TodoApp-6366F1?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge)
![Min SDK](https://img.shields.io/badge/Min%20SDK-Android%2012-FF6B6B?style=for-the-badge)
![Target SDK](https://img.shields.io/badge/Target%20SDK-API%2036-4ECDC4?style=for-the-badge)

> 一个简洁高效的待办事项和备忘录管理应用，采用现代化设计，让你的工作和生活更加井井有条。

[English Version](README_EN.md) | 中文版本

---

## 📱 功能特性

### 核心功能
- **📝 待办事项管理** - 创建、编辑、删除任务，支持优先级设置
- **📋 备忘录记录** - 快速记录灵感，支持分类和置顶
- **📅 日历视图** - 可视化查看任务分布，把握日程安排
- **🔍 智能搜索** - 快速查找任务和备忘录
- **🏷️ 分类管理** - 灵活分类，便于整理
- **📁 附件管理** - 支持图片、音频、文件等附件

### 提醒与通知
- **🔔 任务提醒** - 可自定义提醒时间
- **⏰ 提前提醒** - 支持5/10/15/30/60分钟提前提醒
- **📊 优先级提醒** - 高优先级任务重点提醒

### 安全与隐私
- **🔒 应用锁定** - 生物识别认证（指纹/人脸）
- **🛡️ 隐私保护** - 所有数据本地存储，不上传云端
- **📜 隐私政策** - 透明的数据处理政策

### 主题与外观
- **🎨 Material Design 3** - 现代化设计语言
- **🌓 深色/浅色主题** - 跟随系统或手动切换
- **🌈 自定义颜色** - 多种主题色可选
- **🖼️ 背景图片** - 支持自定义背景

### 数据管理
- **💾 本地存储** - SQLite数据库，高效安全
- **📤 数据导出** - JSON完整备份
- **📊 CSV导出** - 支持导出为表格格式
- **📥 数据导入** - 从备份文件恢复
- **📈 数据统计** - 可视化数据概览

### 高级功能
- **🚀 性能优化** - 防抖节流、LRU缓存、批量更新
- **🎬 流畅动画** - 120Hz高刷支持
- **⚡ 启动优化** - 最小后处理，快速启动

---

## 🛠️ 技术栈

### 核心框架
| 技术 | 说明 |
|------|------|
| **Kotlin** | 主要编程语言 |
| **Jetpack Compose** | 现代声明式UI框架 |
| **MVVM** | 架构模式 |
| **SQLite** | 本地数据库 |

### 依赖库
| 库 | 版本 | 用途 |
|----|------|------|
| AndroidX Core KTX | Latest | Android核心扩展 |
| AndroidX Lifecycle | Latest | 生命周期管理 |
| Compose BOM | 2024.02.00 | Compose版本管理 |
| Material 3 | Latest | Material Design 3 |
| DataStore | Latest | 偏好设置存储 |
| Kotlinx Serialization | Latest | JSON序列化 |
| Biometric | Latest | 生物识别 |
| Coil | 2.7.0 | 图片加载 |

### 开发要求
- **Android Studio** - Hedgehog 或更高版本
- **JDK** - 17 或更高版本
- **Android SDK** - API 35+
- **Gradle** - 8.4+

---

## 📁 项目结构

```
Mo-Todo/
├── app/
│   ├── src/main/
│   │   ├── java/com/motut/mo/
│   │   │   ├── data/           # 数据层
│   │   │   │   ├── AppDatabaseHelper.kt   # SQLite数据库
│   │   │   │   ├── DataBackupManager.kt   # 备份导出
│   │   │   │   ├── Memo.kt               # 备忘录数据模型
│   │   │   │   ├── Todo.kt               # 待办数据模型
│   │   │   │   └── UserPreferences.kt    # 用户偏好
│   │   │   │
│   │   │   ├── ui/               # UI层
│   │   │   │   ├── MainScreenV2.kt      # 主屏幕
│   │   │   │   ├── TodoScreen.kt        # 待办列表
│   │   │   │   ├── MemoScreen.kt        # 备忘录列表
│   │   │   │   ├── SettingsScreens.kt   # 设置页面
│   │   │   │   ├── components/           # UI组件
│   │   │   │   │   └── UiComponents.kt   # 通用组件
│   │   │   │   └── theme/               # 主题配置
│   │   │   │       ├── Color.kt         # 颜色系统
│   │   │   │       ├── Theme.kt          # 主题配置
│   │   │   │       └── Type.kt          # 字体排版
│   │   │   │
│   │   │   ├── viewmodel/        # ViewModel层
│   │   │   │   └── AppViewModel.kt      # 应用ViewModel
│   │   │   │
│   │   │   ├── util/             # 工具类
│   │   │   │   ├── PerformanceUtils.kt  # 性能优化
│   │   │   │   └── AnnouncementFetcher.kt # 公告获取
│   │   │   │
│   │   │   ├── notifications/    # 通知管理
│   │   │   │   └── NotificationManager.kt
│   │   │   │
│   │   │   ├── MainActivity.kt    # 主Activity
│   │   │   └── MoApplication.kt   # Application类
│   │   │
│   │   └── res/                   # 资源文件
│   │       ├── values/            # 字符串、颜色等
│   │       ├── mipmap-*/          # 应用图标
│   │       └── xml/               # XML配置
│   │
│   ├── build.gradle.kts           # 模块构建配置
│   └── proguard-rules.pro         # ProGuard混淆规则
│
├── build.gradle.kts               # 根构建配置
├── settings.gradle.kts            # Gradle设置
├── gradle.properties              # Gradle属性
└── README.md                      # 项目文档
```

---

## 🎨 优化记录 (2026-04-11)

### P0 级优化 (Critical)

#### 1. 数据库线程安全 ✅
- **问题**: 所有数据库操作在调用线程上同步执行
- **修复**: 将所有方法改为 `suspend` 函数 + `withContext(Dispatchers.IO)`
- **影响**: 消除主线程阻塞，提升响应速度

#### 2. 数据库迁移策略 ✅
- **问题**: `onUpgrade` 直接 `DROP TABLE`，用户数据会丢失
- **修复**: 实现基于版本的渐进式迁移策略
- **影响**: 用户升级不丢失数据

#### 3. 批量删除性能 ✅
- **问题**: 批量删除使用循环单条删除
- **修复**: 新增事务包装的批量删除方法
- **影响**: 大数据量删除性能提升90%+

#### 4. CoroutineScope 内存泄漏 ✅
- **问题**: 自定义 CoroutineScope 不受生命周期管理
- **修复**: 要求外部传入 `CoroutineScope` + `cancel()` 方法
- **影响**: 消除内存泄漏风险

### P1 级优化 (High)

#### 5. 数据库查询优化 ✅
- **索引创建**: 6个数据库索引优化查询
  - `idx_todos_date` - 日期查询
  - `idx_todos_priority` - 优先级排序
  - `idx_todos_completed` - 完成状态
  - `idx_todos_created` - 创建时间
  - `idx_memos_category` - 分类查询
  - `idx_memos_pinned` - 置顶查询
- **批量查询 API**:
  - `getMemosByCategory(categoryId)`
  - `getPinnedMemos()`
  - `searchMemos(query)`
  - `getTodosByDateRange(start, end)`

#### 6. ViewModel 优化 ✅
- 添加派生状态 (pinnedMemos, incompleteTodos等)
- 使用 `+` 操作符替代 `listOf() +`
- 添加 `refreshData()` 方法
- Map缓存优化状态查询

#### 7. UI组件增强 ✅
- `GradientCard` - 渐变卡片组件
- `GlassCard` - 玻璃态卡片
- `EnhancedSummaryCard` - 增强统计卡片
- `AnimatedScaleButton` - 缩放动画按钮
- `PriorityBadge` - 优先级标签
- `SwipeableTaskCard` - 滑动手势卡片
- `EmptyState` - 空状态动画
- `SkeletonLoader` - 骨架屏

### UI美化

#### 颜色系统
- **现代配色方案**: PrimaryModern, PrimaryLightModern
- **渐变预设**: 6种渐变 (Primary, Sunset, Ocean, Forest, Sunrise, Dream)
- **统计卡片渐变**: 专属渐变色
- **优先级颜色**: 优化的高/中/低优先级配色

### UI/UX 深度优化 (2026-04-11)

#### 1. 底部导航栏优化 ✅
- **流畅切换动画**: 使用 `animateIntAsState` 和 `animateFloatAsState` 实现平滑过渡
- **图标缩放动画**: 选中时图标放大 1.15 倍，带弹性动画
- **标签缩放动画**: 选中时标签权重 1.2 倍
- **圆形背景动画**: 选中项显示渐变圆形背景，scaleIn + fadeIn
- **阴影效果**: 底部导航添加 8dp 阴影增强层次感

#### 2. 页面过渡动画优化 ✅
- **更流畅的滑动**: 偏移量从 `fullWidth` 优化为 `3/4 width`
- **交错动画**: 入场动画延迟 80ms 后开始淡入
- **时长优化**: 入场 320ms，出场 280ms，更自然的节奏
- **内容键**: 使用 `contentKey` 确保状态正确映射

#### 3. 配色方案优化 ✅
- **主色调增强**: `PrimaryModern = Color(0xFF5B5FC7)` - 更饱和的 Indigo
- **次要色**: `SecondaryModern = Color(0xFF7C3AED)` - Violet 600
- **强调色**: `AccentModern = Color(0xFFF97316)` - 温暖珊瑚色
- **状态颜色对比度提升**:
  - Success: `0xFF16A34A` (更鲜明)
  - Warning: `0xFFEA580C` (更鲜明)
  - Error: `0xFFDC2626` (更鲜明)
  - Info: `0xFF2563EB` (更鲜明)
- **新增渐变色**: `GradientMint` (Emerald to Cyan)
- **分割线颜色**: DividerLight, DividerDark 分离优化

#### 4. FAB 动画优化 ✅
- **缩放动画**: 主 FAB 展开时缩放至 1.05 倍
- **关闭按钮**: 展开菜单显示圆形关闭按钮
- **菜单项动画**:
  - scaleIn 初始缩放 0.8 倍
  - slideInVertically 从 1/2 偏移开始
  - 组合动画: fadeIn + scaleIn + slideIn
- **阴影增强**: 主 FAB 阴影提升至 10dp
- **间距优化**: 菜单项间距从 10dp 提升至 12dp

#### 5. 主题系统增强 ✅
- **亮色主题**:
  - primary: `Color(0xFF5B5FC7)` (更饱和)
  - secondary: `Color(0xFF7C3AED)` (Violet 600)
  - tertiary: `Color(0xFFEA580C)` (Orange 600)
  - surfaceTint: `Color(0xFF5B5FC7)` (与主色一致)
- **暗色主题**:
  - primary: `Color(0xFFA5B4FC)` (Indigo 300)
  - secondary: `Color(0xFFC4B5FD)` (Violet 300)
  - tertiary: `Color(0xFFFBBF24)` (Amber 400)
  - 背景色: `Color(0xFF0F172A)` (Slate 900)
  - 表面色: `Color(0xFF1E293B)` (Slate 800)

#### 页面美化

1. **首页仪表盘**
   - 渐变统计卡片
   - 入场动画 (fadeIn + slideInVertically)
   - 问候语表情指示器
   - 卡片点击缩放动画

2. **日历页面**
   - 整体卡片化带阴影
   - 星期标签周末高亮
   - 日期单元格点击动画
   - 高优先级红色指示器

3. **任务/备忘录卡片**
   - 优化优先级颜色
   - 点击缩放动画
   - 文本截断省略号
   - 字数统计

4. **底部导航栏**
   - 选中项圆形背景
   - 标签加粗
   - 高度提升至80dp

5. **FAB菜单**
   - 渐变背景
   - 旋转动画
   - 图标背景

### 数据管理功能

#### JSON备份/恢复
- 完整数据导出
- 从备份恢复
- 版本兼容性

#### CSV导出
- 备忘录CSV导出
- 待办CSV导出
- Excel兼容格式

#### 统计概览
- 实时数据统计
- 今日待办计数
- 过期任务提醒

---

## 🚀 快速开始

### 环境配置

1. **安装 Android Studio**
   - 下载: https://developer.android.com/studio
   - 版本: Hedgehog (2023.1.1) 或更高

2. **配置 JDK**
   ```bash
   # 确保 JAVA_HOME 指向 JDK 17+
   export JAVA_HOME=/path/to/jdk-17
   ```

3. **克隆项目**
   ```bash
   git clone https://github.com/gascs/Mo-Todo.git
   cd Mo-Todo
   ```

### 构建项目

```bash
# 同步 Gradle
./gradlew sync

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK (需要签名配置)
./gradlew assembleRelease

# 运行到设备
./gradlew installDebug
```

### 签名配置 (发布版本)

在 `gradle.properties` 中添加:
```properties
# 签名配置 (发布版本需要)
RELEASE_STORE_FILE=your-keystore.jks
RELEASE_STORE_PASSWORD=your-password
RELEASE_KEY_ALIAS=your-key-alias
RELEASE_KEY_PASSWORD=your-key-password
```

---

## 📦 发布清单

### Google Play 上架

1. **应用信息**
   - [x] 应用名称: Mo
   - [x] 包名: com.motut.mo
   - [x] 版本: 1.0.0
   - [x] 最低版本: Android 12 (API 31)

2. **应用图标**
   - [x] 矢量图标
   - [ ] Play Store 图标 (512x512)
   - [ ] 功能截图 (手机/平板)

3. **应用内容**
   - [x] 隐私政策页面
   - [x] 用户协议页面
   - [x] 开源声明
   - [x] MIT许可证

4. **合规性**
   - [x] 目标API合规 (API 36)
   - [x] 权限说明完整
   - [x] 数据处理透明

5. **构建配置**
   - [x] ProGuard混淆
   - [x] 代码签名
   - [x] R8优化

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 代码规范

- 遵循 Kotlin 编码规范
- Compose 函数使用 PascalCase
- 变量使用 camelCase
- 注释使用中文/English

---

## 📄 开源协议

本项目采用 **MIT 许可证** 开源。

```
Copyright (c) 2026 MoTuT

特此免费授予任何获得本软件副本和相关文档文件的人
不受限制地处理软件的权利，包括但不限于使用、复制、
修改、合并、发布、分发、再许可和/或出售软件的副本。
```

详细信息请查看 [LICENSE](LICENSE) 文件。

---

## 📞 联系方式

- **GitHub**: [github.com/gascs](https://github.com/gascs)
- **官网**: [motut.net.cn](https://motut.net.cn)
- **邮箱**: gascs@qq.com

---

## 🙏 致谢

感谢以下开源项目:

- [Jetpack Compose](https://developer.android.com/compose) - 现代Android UI工具包
- [Material Design 3](https://m3.material.io/) - 设计系统
- [Kotlin](https://kotlinlang.org/) - 编程语言
- [Coil](https://coil-kt.github.io/coil/) - 图片加载库

---

<div align="center">

**高效工作，简单生活** ✨

*Built with ❤️ by MoTuT*

</div>
