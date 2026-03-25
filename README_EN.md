# Mo

A simple and efficient todo and memo management app

[中文版本](README.md) | English Version

## Introduction

Mo is a task management tool focused on simplicity and practicality, helping you efficiently record and manage daily todos and memos.

## Features

- 📝 Todo management
- 📋 Memo recording
- 📅 Calendar view
- 🎨 Material Design 3 interface
- ⚡ Smooth user experience

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Database**: SQLite
- **Minimum SDK**: API 29 (Android 10)
- **Target SDK**: API 36

## Third-Party Libraries

This project uses the following open source libraries:

- **AndroidX Core KTX** - Android core extensions
- **AndroidX Lifecycle** - Lifecycle management
- **Jetpack Compose** - Modern UI toolkit
  - Compose UI
  - Compose Material 3
  - Compose Material Icons Extended
- **AndroidX DataStore** - Data storage solution
- **Kotlinx Serialization** - Kotlin serialization library
- **JUnit** - Unit testing framework
- **Espresso** - UI testing framework

## Quick Start

### Requirements

- Android Studio Hedgehog or higher
- JDK 17 or higher
- Android SDK API 35

### Build Steps

1. Clone the project locally
2. Open the project with Android Studio
3. Wait for Gradle sync to complete
4. Connect an Android device or start an emulator
5. Click the run button or execute `./gradlew installDebug`

## Project Structure

```
app/
├── src/main/
│   ├── java/com/motut/mo/
│   │   ├── data/              # Data models
│   │   ├── ui/                # UI components and screens
│   │   ├── viewmodel/         # ViewModel layer
│   │   └── MainActivity.kt    # Main Activity
│   └── res/                   # Resource files
```

## Open Source License

This project is open source under the **MIT License**.

### MIT License

```
Copyright (c) 2024 MoTuT

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Secondary Development

Welcome to develop based on this project! When doing secondary development, please follow these conventions:

1. **Keep original author info**: Please keep the original author information and license statement in the code and documentation
2. **Note modifications**: If you make changes to the code, please note the modifications
3. **Share improvements**: If you make valuable improvements, welcome to share them back via Pull Request

## Commercial Use

This project can be **used for commercial purposes free of charge** without additional authorization.

If you use this project for a commercial product, we are happy to see it create value for you. Although not required, we would appreciate it if you could mention this project in your product description.

## Author

- **GitHub**: [gascs](https://www.github.com/gascs)
- **Official Website**: [MoTuT](https://motut.net.cn)

## Contributing

Welcome to submit Issues and Pull Requests!

## Contact

If you have any questions or suggestions, please contact us via:

- GitHub Issues
- Official website message board

---

**Work efficiently, live simply** ✨
