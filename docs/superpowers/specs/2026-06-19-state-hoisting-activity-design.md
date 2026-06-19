# State Hoisting 演示 Activity 设计文档

## Context

在现有 `mine` 包下新增 `TestActivity5`，以层层递进的方式演示 Jetpack Compose 的 **State Hoisting（状态提升）** 模式。目标是帮助学习者从基础概念到生产级 ViewModel 用法，建立完整的心智模型。

现有 `TestActivity1.kt` 中已有一个极简的 `StateHoistingDemo`（单输入框→父显示），本 Activity 在此基础上深入，覆盖兄弟共享、多层传递和 ViewModel 三个进阶场景。

---

## 涉及文件清单

### 新建文件

| 文件路径 | 说明 |
|---|---|
| `app/src/main/java/com/compose/demo/mine/TestActivity5.kt` | Activity 本体 + 全部 Section Composable |
| `app/src/main/java/com/compose/demo/mine/CartViewModel.kt` | Section 4 专用 ViewModel |

### 修改文件

| 文件路径 | 改动说明 |
|---|---|
| `app/src/main/AndroidManifest.xml` | 补注 TestActivity4（原遗漏），新增 TestActivity5 |
| `app/src/main/res/values/strings.xml` | 新增标题及各 Section 字符串资源 |

---

## 详细设计

### 整体页面结构

```
TestActivity5 (ComponentActivity)
  └── StateHoistingScreen (Scaffold + TopAppBar + 返回按钮)
        └── LazyColumn
              ├── Section 1: 基础传值
              ├── Section 2: 兄弟共享（Slider 联动）
              ├── Section 3: 多层嵌套提升（三层）
              └── Section 4: 购物车（ViewModel）
```

### 单向数据流原则

- **状态**始终从父/ViewModel 流向子组件（`value: T` 参数传入）
- **事件**始终从子向上冒泡（`onXxx: (T) -> Unit` 回调传出）
- 所有子 Composable 均为**无状态（stateless）**，不在内部持有 `remember`

---

### Section 1 — 基础：单父→子传值

**父 Composable**：`BasicHoistingDemo`
- 持有 `text by remember { mutableStateOf("") }`
- 渲染顶部 Text 实时显示状态值
- 将 `text` 和 `onValueChange` 传给子组件

**子 Composable**：`NameInputField(value: String, onValueChange: (String) -> Unit)`
- 仅含一个 OutlinedTextField
- 完全不感知状态从哪里来，只负责展示和触发

---

### Section 2 — 进阶：兄弟共享（Slider ↔ 色块联动）

**父 Composable**：`SiblingShareDemo`
- 持有 `progress by remember { mutableFloatStateOf(0f) }`（范围 0f..1f）

**兄弟 Composable 1**：`ProgressSlider(value: Float, onValueChange: (Float) -> Unit)`
- 无状态 Slider，值变化时通知父组件

**兄弟 Composable 2**：`ColorPreviewBox(value: Float)`
- 色块颜色从红（`Color.Red`）到绿（`Color.Green`）按 `value` 线性插值
- 显示当前百分比文字

两者从同一父状态读取，Slider 拖动时色块实时同步。

---

### Section 3 — 高阶：三层嵌套（祖→父→孙）

**场景**：跨三层组件同步"选中主题色"

- **祖父** `ThemePickerScreen`：持有 `selectedColor: Color`，顶部展示大预览色块
- **父** `ThemeSection`：透传 `selectedColor` + `onColorChange` 回调给子
- **孙** `ColorOptionRow`：展示 4 个可点击色块（红/蓝/绿/橙），选中时高亮，点击触发 `onColorChange`

状态从祖父流下，事件从孙冒泡至祖父，演示跨层状态提升的正确姿势。

---

### Section 4 — 实战：购物车（ViewModel）

**数据模型**：
```kotlin
data class CartItem(val id: Int, val name: String, val quantity: Int)
```

**CartViewModel**：
- `_cartItems: MutableStateFlow<List<CartItem>>` 持有购物车列表（初始 3 条）
- `increment(id: Int)`：对应商品数量 +1
- `decrement(id: Int)`：对应商品数量 -1（最小为 0）
- `val totalCount: Int` 衍生属性，所有商品数量之和

**父 Composable** `CartScreen(viewModel: CartViewModel)`：
- `val items by viewModel.cartItems.collectAsStateWithLifecycle()`
- 渲染顶部"总计 N 件"汇总行
- 遍历 items，每条渲染 `CartItemRow`

**子 Composable** `CartItemRow(item: CartItem, onIncrement: () -> Unit, onDecrement: () -> Unit)`：
- 展示商品名 + 数量 + IconButton（`+` / `-`）
- 完全无状态，所有行为通过回调上报

---

## 复用组件

| 组件 / 方法 | 来源 | 说明 |
|---|---|---|
| `SectionTitle` | 参考 TestActivity3/4 私有实现 | 在 TestActivity5 中独立声明同名私有函数 |
| `ComposedemoTheme` | `ui/theme/Theme.kt` | Activity 根节点主题包裹 |
| `TopAppBar` + 返回按钮 | 参考 TestActivity3/4 | 完全相同的 Scaffold 结构 |

颜色直接使用 `MaterialTheme.colorScheme.*`，无需新增自定义颜色。

---

## 实现完成状态

| 文件 | 状态 |
|---|---|
| `TestActivity5.kt` | 待实现 |
| `CartViewModel.kt` | 待实现 |
| `AndroidManifest.xml` | 待修改 |
| `strings.xml` | 待修改 |

---

## 验证方式

1. 编译通过（`./gradlew assembleDebug`）
2. Section 1：输入框内容实时反映在父层 Text 上
3. Section 2：Slider 拖动时色块颜色和百分比数字同步变化
4. Section 3：点击不同色块，顶部预览色块立即同步到对应颜色
5. Section 4：点击 `+` / `-` 时单行数量和顶部总计同步更新；数量为 0 时 `-` 按钮禁用
6. 返回按钮可正常退出 Activity
