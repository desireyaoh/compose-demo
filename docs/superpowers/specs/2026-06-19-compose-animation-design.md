# Compose 常见动画演示页 — 设计文档

## 1. Context — 背景与目标

在 `com.compose.demo.mine` 包下新建 `TestActivity4`，用一个完整的页面演示 Jetpack Compose 中最常用的 5 种动画 API，作为学习参考示例。风格与现有 `TestActivity3`（对齐约束演示）完全一致：`Scaffold` + `TopAppBar` + `LazyColumn` 分 Section。

目标读者：正在学习 Compose 动画的 Android 开发者。

---

## 2. 涉及文件清单

### 新建文件

| 文件路径 | 说明 |
|---|---|
| `app/src/main/java/com/compose/demo/mine/TestActivity4.kt` | 动画演示 Activity 及全部 Composable |

### 修改文件

| 文件路径 | 改动说明 |
|---|---|
| `app/src/main/res/values/strings.xml` | 新增动画页相关字符串 |

---

## 3. 详细设计

### Activity 结构

```
TestActivity4 : ComponentActivity
  └── AnimationDemoScreen(onBack)          ← 主 Screen，带 Scaffold
        └── LazyColumn
              ├── Section 1: AnimatedVisibilityDemo
              ├── Section 2: AnimateFloatDemo
              ├── Section 3: AnimateDpDemo
              ├── Section 4: AnimatedContentDemo
              └── Section 5: InfiniteTransitionDemo
```

### Section 详细说明

#### Section 1 — `AnimatedVisibility`（显隐动画）
- 状态：`var visible by remember { mutableStateOf(true) }`
- UI：一个绿色 Box（60dp 高）+ 切换按钮
- 动画：`AnimatedVisibility(visible, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically())`
- 按钮文案：显示时为"隐藏"，隐藏时为"显示"

#### Section 2 — `animateFloatAsState`（透明度动画）
- 状态：`var opaque by remember { mutableStateOf(true) }`
- UI：一个蓝色 Box + 切换按钮
- 动画：`val alpha by animateFloatAsState(if (opaque) 1f else 0.1f, animationSpec = tween(600))`
- 将 alpha 应用于 `Modifier.graphicsLayer { this.alpha = alpha }`
- 按钮文案："切换透明度"

#### Section 3 — `animateDpAsState`（尺寸动画）
- 状态：`var expanded by remember { mutableStateOf(false) }`
- UI：一个橙色 Box，宽度可变 + 切换按钮
- 动画：`val width by animateDpAsState(if (expanded) 240.dp else 80.dp, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))`
- 按钮文案：收起时为"展开"，展开时为"收起"

#### Section 4 — `AnimatedContent`（内容切换动画）
- 状态：`var index by remember { mutableIntStateOf(0) }`，循环 0→1→2→0
- 三段内容文案：`["第一段内容", "第二段内容", "第三段内容"]`（定义在 strings.xml）
- 动画：`AnimatedContent(index, transitionSpec = { slideInHorizontally { it } togetherWith slideOutHorizontally { -it } })`
- 按钮文案："下一条"

#### Section 5 — `InfiniteTransition`（无限循环动画）
- 状态：`var running by remember { mutableStateOf(false) }`
- 使用 `rememberInfiniteTransition()`，当 `running == true` 时激活
- 颜色在主题 Primary ↔ Secondary 之间无限渐变（`animateColor`，`infiniteRepeatable + tween(1000)`）
- 当 `running == false` 时展示静态颜色（Primary）
- UI：60dp 圆形（`CircleShape`）+ 开始/停止按钮
- 按钮文案：停止时为"开始动画"，运行时为"停止动画"

### 字符串规范（strings.xml 新增）

```xml
<string name="title_activity_test4">Compose 动画演示</string>
<!-- Section 1 -->
<string name="anim_s1_title">1. AnimatedVisibility — 显隐动画</string>
<string name="anim_s1_show">显示</string>
<string name="anim_s1_hide">隐藏</string>
<!-- Section 2 -->
<string name="anim_s2_title">2. animateFloatAsState — 透明度动画</string>
<string name="anim_s2_toggle">切换透明度</string>
<!-- Section 3 -->
<string name="anim_s3_title">3. animateDpAsState — 尺寸动画</string>
<string name="anim_s3_expand">展开</string>
<string name="anim_s3_collapse">收起</string>
<!-- Section 4 -->
<string name="anim_s4_title">4. AnimatedContent — 内容切换动画</string>
<string name="anim_s4_next">下一条</string>
<string name="anim_s4_text_0">第一段：山高云淡，望断南飞雁</string>
<string name="anim_s4_text_1">第二段：不到长城非好汉，屈指行程二万</string>
<string name="anim_s4_text_2">第三段：六盘山上高峰，红旗漫卷西风</string>
<!-- Section 5 -->
<string name="anim_s5_title">5. InfiniteTransition — 无限循环动画</string>
<string name="anim_s5_start">开始动画</string>
<string name="anim_s5_stop">停止动画</string>
```

---

## 4. 复用组件

- `SectionTitle`：复用 TestActivity3 的 `SectionTitle` 风格（私有函数，各 Activity 自己定义）
- `ComposedemoTheme`：主题包装
- `TopAppBar` + 返回按钮：与 TestActivity3 完全一致
- 颜色：使用 `MaterialTheme.colorScheme.primary/secondary` 等 token，不硬编码色值

---

## 5. 实现完成状态

| 文件 | 状态 |
|---|---|
| `TestActivity4.kt` | 待创建 |
| `strings.xml`（新增字符串） | 待修改 |

---

## 6. 验证方式

1. `./gradlew assembleDebug` 编译通过，无警告
2. 安装到设备后手动验证：
   - Section 1：点击按钮，色块淡出/淡入 + 上下滑动，动画流畅
   - Section 2：点击按钮，色块透明度平滑过渡，0.1f 时依然可见轮廓
   - Section 3：点击按钮，色块宽度有弹簧感（bouncy）变化
   - Section 4：点击"下一条"，文案向左滑入/旧文案向左滑出，循环三条
   - Section 5：点击"开始动画"，圆形颜色无限渐变；点击"停止动画"，恢复静态颜色
3. Android Studio Preview 可渲染各 Section 的初始状态
