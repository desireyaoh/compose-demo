# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目简介

Android Jetpack Compose 学习项目，目标是仿微信 UI 风格实现各页面。当前阶段：底部四 Tab 导航骨架已完成，各 Tab 页面内容待开发。

## 构建与运行

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug

# 运行 JVM 单元测试
./gradlew test

# 运行仪器化测试（需连接设备/模拟器）
./gradlew connectedAndroidTest

# 运行 Lint 检查
./gradlew lint

# 运行单个测试类
./gradlew test --tests "com.compose.demo.ExampleUnitTest"
```

Windows 环境下用 `gradlew.bat` 替代 `./gradlew`。

## 代码架构

### 技术栈

- **UI**：全量 Jetpack Compose（无 XML 布局文件），Material 3
- **语言**：Kotlin 2.2.10
- **AGP**：9.2.1，依赖版本统一管理于 `gradle/libs.versions.toml`
- **SDK**：minSdk 24 / targetSdk 36，Java 11

### 包结构

```
com.compose.demo
├── MainActivity.kt          # 唯一 Activity，单 Activity 架构入口
└── ui/theme/
    ├── Color.kt             # Compose 颜色 token（Material 主题用）
    ├── Type.kt              # 字体排版定义
    └── Theme.kt             # ComposedemoTheme：支持动态颜色（Android 12+）和深色模式
```

### 主题系统

- `ui/theme/Color.kt` 定义 Material 主题颜色 token（Purple/Pink 系）
- `res/values/colors.xml` 保留 View 系统兼容色值（目前仅用于 launcher icon 等非 Compose 场景）
- 自定义业务色（如微信绿 `#07C160`）目前写在 `MainActivity.kt`，新增业务颜色应统一放入 `ui/theme/Color.kt`

### 导航架构

当前使用 `mutableIntStateOf` 管理选中 Tab 下标的最简实现。后续若页面增多，应引入 Navigation Compose (`androidx.navigation:navigation-compose`) 并将各 Tab 拆分为独立 Screen Composable。

## 关键约定

- 颜色值不得硬编码在 Composable 内，需定义在 `ui/theme/Color.kt`
- 字符串不得硬编码在代码中，需定义在 `res/values/strings.xml`
- 禁止使用 `!!`，使用 `?.` 或 `requireNotNull()`
- 新建 `.kt` / `.xml` 文件后须执行 `git add <文件路径>`
