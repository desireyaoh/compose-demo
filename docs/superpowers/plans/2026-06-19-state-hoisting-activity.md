# State Hoisting 演示 Activity 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增 `TestActivity5` 演示 Compose 状态提升（State Hoisting）的四个层次：基础传值、兄弟共享、三层嵌套、ViewModel 购物车。

**Architecture:** 全部 Composable 遵循单向数据流——状态从父/ViewModel 流向子，事件通过回调向上冒泡；子组件均为无状态（stateless）。Section 4 使用 `CartViewModel`（StateFlow）管理购物车列表，父 Composable 用 `collectAsState()` 订阅，子组件 `CartItemRow` 只接收数据和回调。

**Tech Stack:** Kotlin 2.2, Jetpack Compose (Material3), AndroidX ViewModel, StateFlow, JUnit4

---

## 涉及文件

| 操作 | 文件 |
|---|---|
| 修改 | `app/src/main/res/values/strings.xml` |
| 修改 | `app/src/main/AndroidManifest.xml` |
| 新建（测试） | `app/src/test/java/com/compose/demo/mine/CartViewModelTest.kt` |
| 新建 | `app/src/main/java/com/compose/demo/mine/CartViewModel.kt` |
| 新建 | `app/src/main/java/com/compose/demo/mine/TestActivity5.kt` |

---

## Task 1: 添加 strings.xml 字符串资源

**Files:**
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: 在 strings.xml 末尾 `</resources>` 之前追加以下字符串**

```xml
    <!-- State Hoisting 演示 -->
    <string name="title_activity_test5">State Hoisting 演示</string>
    <!-- Section 1 -->
    <string name="sh_s1_title">1. 基础：单父→子传值</string>
    <string name="sh_s1_parent_label">父组件状态：%s</string>
    <string name="sh_s1_input_label">子组件输入框</string>
    <!-- Section 2 -->
    <string name="sh_s2_title">2. 进阶：兄弟共享（Slider ↔ 色块联动）</string>
    <string name="sh_s2_hint">拖动 Slider，两个兄弟组件从同一父状态读取并同步更新</string>
    <!-- Section 3 -->
    <string name="sh_s3_title">3. 高阶：三层嵌套（祖→父→孙）</string>
    <string name="sh_s3_preview_label">顶部预览（祖父层持有状态）</string>
    <string name="sh_s3_section_label">父层 → 点击色块触发事件冒泡至祖父</string>
    <!-- Section 4 -->
    <string name="sh_s4_title">4. 实战：购物车（ViewModel）</string>
    <string name="sh_s4_total_fmt">总计 %d 件</string>
    <string name="sh_s4_increment">增加数量</string>
    <string name="sh_s4_decrement">减少数量</string>
```

- [ ] **Step 2: 提交**

```bash
git add app/src/main/res/values/strings.xml
git commit -m "feat: 添加 State Hoisting 演示页字符串资源"
```

---

## Task 2: 在 AndroidManifest 注册 TestActivity4 和 TestActivity5

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: 在 `</application>` 之前补充两条 `<activity>` 声明**

在现有 `TestActivity3` 注册块之后添加：

```xml
        <activity
            android:name=".mine.TestActivity4"
            android:exported="false"
            android:label="@string/title_activity_test4" />

        <activity
            android:name=".mine.TestActivity5"
            android:exported="false"
            android:label="@string/title_activity_test5"
            android:windowSoftInputMode="adjustResize" />
```

- [ ] **Step 2: 提交**

```bash
git add app/src/main/AndroidManifest.xml
git commit -m "feat: AndroidManifest 补注 TestActivity4，注册 TestActivity5"
```

---

## Task 3: 编写 CartViewModel 单元测试（TDD 先写测试）

**Files:**
- Create: `app/src/test/java/com/compose/demo/mine/CartViewModelTest.kt`

- [ ] **Step 1: 创建测试文件**

```kotlin
package com.compose.demo.mine

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartViewModelTest {

    private lateinit var viewModel: CartViewModel

    @Before
    fun setUp() {
        viewModel = CartViewModel()
    }

    @Test
    fun initialState_hasThreeItems() {
        assertEquals(3, viewModel.cartItems.value.size)
    }

    @Test
    fun increment_increasesQuantityByOne() {
        viewModel.increment(1)
        val item = viewModel.cartItems.value.first { it.id == 1 }
        assertEquals(2, item.quantity)
    }

    @Test
    fun decrement_decreasesQuantityByOne() {
        viewModel.increment(1) // 初始 1 → 2
        viewModel.decrement(1) // 2 → 1
        val item = viewModel.cartItems.value.first { it.id == 1 }
        assertEquals(1, item.quantity)
    }

    @Test
    fun decrement_doesNotGoBelowZero() {
        viewModel.decrement(3) // 初始数量为 0
        val item = viewModel.cartItems.value.first { it.id == 3 }
        assertEquals(0, item.quantity)
    }

    @Test
    fun increment_doesNotAffectOtherItems() {
        viewModel.increment(1)
        val unchanged = viewModel.cartItems.value.first { it.id == 2 }
        assertEquals(2, unchanged.quantity)
    }
}
```

- [ ] **Step 2: 运行测试，确认编译失败（CartViewModel 尚未存在）**

```bash
./gradlew test --tests "com.compose.demo.mine.CartViewModelTest"
```

预期：编译错误 `Unresolved reference: CartViewModel`

- [ ] **Step 3: 提交测试文件**

```bash
git add app/src/test/java/com/compose/demo/mine/CartViewModelTest.kt
git commit -m "test: 新增 CartViewModel 单元测试（TDD 先行）"
```

---

## Task 4: 实现 CartViewModel

**Files:**
- Create: `app/src/main/java/com/compose/demo/mine/CartViewModel.kt`

- [ ] **Step 1: 创建 CartViewModel.kt**

```kotlin
package com.compose.demo.mine

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartItem(val id: Int, val name: String, val quantity: Int)

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow(
        listOf(
            CartItem(id = 1, name = "可乐", quantity = 1),
            CartItem(id = 2, name = "薯片", quantity = 2),
            CartItem(id = 3, name = "矿泉水", quantity = 0)
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun increment(id: Int) {
        _cartItems.update { items ->
            items.map { if (it.id == id) it.copy(quantity = it.quantity + 1) else it }
        }
    }

    fun decrement(id: Int) {
        _cartItems.update { items ->
            items.map { if (it.id == id) it.copy(quantity = maxOf(0, it.quantity - 1)) else it }
        }
    }
}
```

- [ ] **Step 2: 运行测试，确认全部通过**

```bash
./gradlew test --tests "com.compose.demo.mine.CartViewModelTest"
```

预期输出（5 tests，全部 PASSED）：

```
CartViewModelTest > initialState_hasThreeItems PASSED
CartViewModelTest > increment_increasesQuantityByOne PASSED
CartViewModelTest > decrement_decreasesQuantityByOne PASSED
CartViewModelTest > decrement_doesNotGoBelowZero PASSED
CartViewModelTest > increment_doesNotAffectOtherItems PASSED
```

- [ ] **Step 3: 提交**

```bash
git add app/src/main/java/com/compose/demo/mine/CartViewModel.kt
git commit -m "feat: 实现 CartViewModel（StateFlow + increment/decrement）"
```

---

## Task 5: 创建 TestActivity5.kt（完整实现）

**Files:**
- Create: `app/src/main/java/com/compose/demo/mine/TestActivity5.kt`

- [ ] **Step 1: 创建 TestActivity5.kt，写入以下完整内容**

```kotlin
package com.compose.demo.mine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme

class TestActivity5 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                StateHoistingScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateHoistingScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_test5)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { BasicHoistingDemo() }
            item { SiblingShareDemo() }
            item { ThemePickerScreen() }
            item { CartScreen() }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

// ─── Section 1: 基础：单父→子传值 ───────────────────────────────────────────

@Composable
fun BasicHoistingDemo(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.sh_s1_title))
        Text(
            text = stringResource(R.string.sh_s1_parent_label, text),
            fontWeight = FontWeight.Bold
        )
        NameInputField(value = text, onValueChange = { text = it })
    }
}

@Composable
fun NameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.sh_s1_input_label)) },
        modifier = modifier.fillMaxWidth()
    )
}

// ─── Section 2: 进阶：兄弟共享（Slider ↔ 色块联动）────────────────────────

@Composable
fun SiblingShareDemo(modifier: Modifier = Modifier) {
    var progress by remember { mutableFloatStateOf(0f) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.sh_s2_title))
        Text(
            text = stringResource(R.string.sh_s2_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ProgressSlider(value = progress, onValueChange = { progress = it })
        ColorPreviewBox(value = progress)
    }
}

@Composable
fun ProgressSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth())
}

@Composable
fun ColorPreviewBox(value: Float, modifier: Modifier = Modifier) {
    val color = lerp(Color.Red, Color.Green, value)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${(value * 100).toInt()}%",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// ─── Section 3: 高阶：三层嵌套（祖→父→孙）──────────────────────────────────

private val themeColors = listOf(
    Color(0xFFE53935),
    Color(0xFF1E88E5),
    Color(0xFF43A047),
    Color(0xFFFB8C00)
)

@Composable
fun ThemePickerScreen(modifier: Modifier = Modifier) {
    var selectedColor by remember { mutableStateOf(themeColors[0]) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.sh_s3_title))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(selectedColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.sh_s3_preview_label),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        ThemeSection(selectedColor = selectedColor, onColorChange = { selectedColor = it })
    }
}

@Composable
fun ThemeSection(selectedColor: Color, onColorChange: (Color) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.sh_s3_section_label),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ColorOptionRow(selectedColor = selectedColor, onColorChange = onColorChange)
    }
}

@Composable
fun ColorOptionRow(selectedColor: Color, onColorChange: (Color) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        themeColors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape)
                    .then(
                        if (isSelected) Modifier.border(3.dp, Color.Black, CircleShape)
                        else Modifier
                    )
                    .clickable { onColorChange(color) }
            )
        }
    }
}

// ─── Section 4: 实战：购物车（ViewModel）────────────────────────────────────

@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val items by viewModel.cartItems.collectAsState()
    val totalCount = items.sumOf { it.quantity }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(R.string.sh_s4_title))
        Text(
            text = stringResource(R.string.sh_s4_total_fmt, totalCount),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        HorizontalDivider()
        items.forEach { item ->
            CartItemRow(
                item = item,
                onIncrement = { viewModel.increment(item.id) },
                onDecrement = { viewModel.decrement(item.id) }
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp
        )
        IconButton(onClick = onDecrement, enabled = item.quantity > 0) {
            Icon(
                Icons.Default.Remove,
                contentDescription = stringResource(R.string.sh_s4_decrement)
            )
        }
        Text(
            text = item.quantity.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onIncrement) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(R.string.sh_s4_increment)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateHoistingScreenPreview() {
    ComposedemoTheme {
        StateHoistingScreen()
    }
}
```

- [ ] **Step 2: 执行 git add 将新文件纳入版本控制**

```bash
git add app/src/main/java/com/compose/demo/mine/TestActivity5.kt
```

- [ ] **Step 3: 构建，确认编译通过**

```bash
./gradlew assembleDebug
```

预期：`BUILD SUCCESSFUL`

- [ ] **Step 4: 提交**

```bash
git commit -m "feat: 新增 TestActivity5 演示 State Hoisting 四层递进场景"
```

---

## Task 6: 构建验证

**Files:** 无新增修改

- [ ] **Step 1: 运行所有单元测试**

```bash
./gradlew test
```

预期：`BUILD SUCCESSFUL`，包含 `CartViewModelTest` 5 个用例全部 PASSED

- [ ] **Step 2: 安装到设备并手动验证**

```bash
./gradlew installDebug
```

逐一验证：
1. **Section 1**：在输入框输入文字，顶部 Text 实时同步显示
2. **Section 2**：拖动 Slider，色块颜色从红→绿渐变，百分比数字同步更新
3. **Section 3**：点击 4 个色块（红/蓝/绿/橙），顶部大预览块颜色立即同步，被选中色块出现黑色描边
4. **Section 4**：点击 `+` 增加数量，顶部"总计 N 件"同步更新；数量为 0 时 `-` 按钮变灰禁用
5. **返回按钮**：点击可正常退出 Activity

---

## 验收标准

| 验收项 | 预期 |
|---|---|
| 编译 | `assembleDebug` 无报错 |
| 单元测试 | 5 个 CartViewModelTest 用例全部通过 |
| Section 1 | 输入框→父层 Text 实时同步 |
| Section 2 | Slider 拖动→色块颜色和百分比同步 |
| Section 3 | 点色块→三层同步，选中高亮 |
| Section 4 | +/- 按钮→数量和总计同步；0 时禁用 `-` |
| 导航 | 返回按钮正常退出 |
