# Mo

一个简洁高效的待办事项和备忘录管理应用

中文版本 | [English Version](README_EN.md)

## 简介

Mo 是一款专注于简单实用的任务管理工具，帮助你高效记录和管理日常待办事项与备忘录。

## 功能特性

- 📝 待办事项管理
- 📋 备忘录记录
- 📅 日历视图
- 🎨 Material Design 3 界面
- ⚡ 流畅的操作体验

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM
- **数据库**: SQLite
- **最低SDK**: API 29 (Android 10)
- **目标SDK**: API 36

## 第三方库

本项目使用了以下开源库：

- **AndroidX Core KTX** - Android核心扩展库
- **AndroidX Lifecycle** - 生命周期管理
- **Jetpack Compose** - 现代UI工具包
  - Compose UI
  - Compose Material 3
  - Compose Material Icons Extended
- **AndroidX DataStore** - 数据存储解决方案
- **Kotlinx Serialization** - Kotlin序列化库
- **JUnit** - 单元测试框架
- **Espresso** - UI测试框架

## 快速开始

### 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 17 或更高版本
- Android SDK API 35

### 构建步骤

1. 克隆项目到本地
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击运行按钮或执行 `./gradlew installDebug`

## 项目结构

```
app/
├── src/main/
│   ├── java/com/motut/mo/
│   │   ├── data/              # 数据模型
│   │   ├── ui/                # UI 组件和屏幕
│   │   ├── viewmodel/         # ViewModel 层
│   │   └── MainActivity.kt    # 主 Activity
│   └── res/                   # 资源文件
```

## 开源协议

本项目采用 **MIT 许可证** 开源。

### MIT License

```
Copyright (c) 2026 MoTuT

特此免费授予任何获得本软件副本和相关文档文件（以下简称"软件"）的人不受限制地处理软件的权利，包括但不限于使用、复制、修改、合并、发布、分发、再许可和/或出售软件的副本，并允许向其提供软件的人这样做，符合以下条件：

上述版权声明和本许可声明应包含在软件的所有副本或重要部分中。

本软件按"原样"提供，不提供任何形式的保证，包括但不限于对适销性、特定用途适用性和非侵权性的保证。在任何情况下，作者或版权持有人均不对因软件或软件使用或其他交易引起的任何索赔、损害或其他责任承担责任，无论是合同诉讼、侵权诉讼还是其他形式的诉讼。
```

## 二次开发

欢迎基于本项目进行二次开发！在进行二次开发时，请遵守以下约定：

1. **保留原作者信息**：请在代码和文档中保留原作者信息和许可证声明
2. **注明修改**：如果对代码进行了修改，请注明修改内容
3. **分享改进**：如果你做出了有价值的改进，欢迎通过 Pull Request 分享回来

## 商业使用

本项目可**免费用于商业用途**，无需额外授权。

如果你将本项目用于商业产品，我们很高兴看到它能为你创造价值。虽然不是必须的，但如果你能在产品说明中提及本项目，我们将不胜感激。

## 作者

- **GitHub**: [gascs](https://www.github.com/gascs)
- **官网**: [MoTuT](https://motut.net.cn)

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

如有问题或建议，欢迎通过以下方式联系：

- GitHub Issues
- 官网留言

---

**高效工作，简单生活** ✨
