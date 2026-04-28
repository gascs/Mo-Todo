# Mo App — 改动记录

> 包名：`com.mo.todo` · 最低 SDK 34 · Kotlin + Jetpack Compose + MVVM

---

## 第一轮：编译修复（2026-04-28）

### 构建配置修复

| 文件 | 改动 | 原因 |
|------|------|------|
| `build.gradle.kts` | AGP 保持 8.9.1；Hilt `2.51.1` → `2.55`；KSP `2.1.10-1.0.31` | Kotlin 2.1.10 元数据版本不兼容 |
| `app/build.gradle.kts` | hilt-android → 2.55；hilt-navigation-compose → 1.2.0；work-runtime → 2.10.0；hilt-work/hilt-compiler → 1.2.0 | 同步依赖链 |
| `gradle.properties` | 添加 `kotlin.daemon.jvmargs`、`org.gradle.parallel`、`org.gradle.caching` | 修复 Kotlin Daemon AccessDenied |
| `gradlew.bat` + `gradle-wrapper.jar` | 重新生成 | 补齐缺失的 Wrapper |

### 编辑模式数据回填

| 文件 | 改动 |
|------|------|
| `TodoDao.kt` | 新增 `getTodoById(id)` |
| `MemoDao.kt` | 新增 `getMemoById(id)` |
| `TodoRepository.kt` | 新增 `getTodoById(id)` |
| `MemoRepository.kt` | 新增 `getMemoById(id)` |
| `TodoViewModel.kt` | 新增 `getTodoById(id)` |
| `MemoViewModel.kt` | 新增 `getMemoById(id)` |
| `AddEditTodoScreen.kt` | 添加 `LaunchedEffect` 预填充表单 |
| `AddEditMemoScreen.kt` | 添加 `LaunchedEffect` 预填充表单 |

---

## 第二轮：主题与 UI 全面优化（2026-04-28）

### 主题层（3 文件）

**`ui/theme/Color.kt`** — 全新配色方案

| 色值 | 名称 | 用途 |
|------|------|------|
| `#5B7F6A` | MoPrimary | 低饱和森林绿 · 主色调 |
| `#FAFAF8` | MoLightBackground | 浅色主背景 |
| `#FFFFFF` | MoLightSurface | 浅色卡片/表面 |
| `#1A1A1A` | MoLightOnSurface | 浅色主文字 |
| `#6B6B6B` | MoLightOnSurfaceVariant | 浅色辅助文字 |
| `#EAEAEA` | MoLightOutlineVariant | 浅色分割线 |
| `#121212` | MoDarkBackground | 深色主背景 |
| `#1E1E1E` | MoDarkSurface | 深色卡片/表面 |
| `#E8E8E8` | MoDarkOnSurface | 深色主文字 |
| `#A0A0A0` | MoDarkOnSurfaceVariant | 深色辅助文字 |
| `#2E2E2E` | MoDarkOutlineVariant | 深色分割线 |
| `#8BB59A` | MoPrimaryDark | 深色模式主色调 |
| `#D9534F` | MoError | 删除按钮色 |
| `#F0C040` | StarColor | 星标金色 |
| `#E5605A` / `#E8A840` / `#5B7F6A` | PriorityHigh / Medium / Low | 优先级色 |
| 7 色数组 | MemoChipColors | 备忘卡片颜色标签 |

**`ui/theme/Type.kt`** — 字体排版

| 级别 | 字号 | 粗细 | 用途 |
|------|------|------|------|
| titleLarge | 24sp | Bold | 页面标题 |
| titleMedium | 16sp | SemiBold | 卡片标题 |
| bodyLarge | 15sp | Regular | 正文 |
| bodySmall | 12sp | Regular | 辅助信息 |
| labelLarge | 14sp | Medium | 标签/按钮 |

**`ui/theme/Theme.kt`** — 主题入口

- `MoTheme`：Android 12+ 优先使用 `dynamicColorScheme`，否则回退到 `MoLightScheme` / `MoDarkScheme`
- `MoShapes`：extraSmall=8dp, small=12dp, medium=16dp, large=20dp, extraLarge=28dp
- `SideEffect` 设置状态栏颜色与图标深浅

### 通用组件（1 文件 · 新建）

**`ui/component/Common.kt`** — 6 个可复用组件

| 组件 | 功能 |
|------|------|
| `TodoItemRow` | 待办列表项：左滑删除 + 弹性缩放动画 + 优先级色点 + 标签 |
| `MemoGridCard` | 备忘网格卡片：颜色标签背景 + 星标切换 + 日期预览 |
| `MemoListItem` | 备忘列表项：缩略图 + 标题/摘要 + 星标 |
| `EmptyPlaceholder` | 空状态：圆形图标容器 + 主副文案 |
| `SectionHeader` | 分区标题 |
| `TagChipRow` | 标签筛选栏：带动画颜色切换 |

### 页面层（5 文件）

**`ui/screen/todo/TodoScreen.kt`**

- `TodoItemRow` 替代原有 `TodoItem`，支持左滑删除（`detectHorizontalDragGestures`）
- 勾选完成时弹性缩放动画（`animateFloatAsState` + spring）
- `AnimatedVisibility` 控制已完成区域展开/收起
- `TagChipRow` 替代内联 Chip 代码
- `EmptyPlaceholder` 优雅空状态展示

**`ui/screen/todo/AddEditTodoScreen.kt`**

- `OutlinedTextField` 圆角 12dp（`MaterialTheme.shapes.small`）
- 标签/优先级使用自定义 Chip 块（`clickable` + 选中态背景色切换）
- `DatePickerDialog` + `TimePickerDialog` 提醒时间选择
- 编辑模式 `LaunchedEffect` 回填数据

**`ui/screen/memo/MemoScreen.kt`**

- `MemoGridCard` / `MemoListItem` 双视图组件
- `TagChipRow` 复用筛选
- 网格/列表视图切换按钮
- `EmptyPlaceholder` 空状态

**`ui/screen/memo/AddEditMemoScreen.kt`**

- 7 色圆点选择器（`MemoChipColors`）
- B / I / U / ≡ 富文本工具栏占位（圆形按钮）
- `OutlinedTextField` 圆角表单
- 编辑模式自动回填

**`ui/screen/profile/ProfileScreen.kt`**

- 头像圆形容器 + 用户名区域
- 5 个设置项：标签管理、提醒设置、主题切换、数据备份、关于
- 主题点击循环切换：系统 → 浅色 → 深色
- `ProfileMenuItem` 私有组件：圆形图标背景 + 主副文案 + 箭头

---

## 最终状态

| 指标 | 值 |
|------|-----|
| APK | `app/build/outputs/apk/debug/app-debug.apk` |
| 大小 | 54.26 MB (debug) |
| 编译错误 | 0 |
| Lint 警告 | 0（仅 o.html 的 CSS 警告，与 Kotlin 无关） |
| 构建命令 | `.\gradlew assembleDebug` |
