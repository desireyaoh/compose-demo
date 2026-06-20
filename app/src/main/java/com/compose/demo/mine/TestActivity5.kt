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

/**
 * 状态提升（State Hoisting）综合演示页面。
 *
 * 四个递进示例，逐步展示状态提升的不同层级：
 * 1. [BasicHoistingDemo]：最简单的父→子单向数据流
 * 2. [SiblingShareDemo]：父作为中间层，让兄弟组件共享同一状态
 * 3. [ThemePickerScreen]：三层嵌套（祖→父→孙），状态在顶层统一管理
 * 4. [CartScreen]：真实业务场景，将状态外置到 ViewModel
 */
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

/**
 * 状态提升演示的容器屏幕，提供 TopAppBar 导航栏和可滚动内容区域。
 *
 * @param onBack 返回按钮点击回调，默认无操作（Preview 场景）
 */
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

/**
 * 最基础的状态提升示例：父组件持有 [text] 状态，将值和修改回调分别传给子组件。
 *
 * 提升的好处：父组件可以同时在 Text 中读取最新值并展示，子组件只负责触发变更。
 */
@Composable
fun BasicHoistingDemo(modifier: Modifier = Modifier) {
    // 状态定义在父组件，子组件通过回调驱动变更
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

/**
 * 无状态输入框，状态由调用方持有。
 *
 * @param value       当前输入值，来自父组件
 * @param onValueChange 用户输入时通知父组件更新状态
 */
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

/**
 * 兄弟组件共享同一状态的示例：[ProgressSlider] 和 [ColorPreviewBox] 都不持有状态，
 * 而是由父组件 [SiblingShareDemo] 统一管理 [progress]，并分别将数据和回调传下去。
 *
 * 这正是状态提升的典型场景：当两个组件需要同步时，将状态提升到它们的最近公共父组件。
 */
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

/**
 * 无状态滑块，[value] 和 [onValueChange] 均由父组件提供。
 *
 * @param value       当前进度，范围 0f..1f
 * @param onValueChange 用户拖动时回调给父组件
 */
@Composable
fun ProgressSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth())
}

/**
 * 颜色预览色块，根据 [value] 在红色和绿色之间线性插值显示过渡色。
 *
 * @param value 进度值 0f..1f；0 为纯红，1 为纯绿，中间值为混合色
 */
@Composable
fun ColorPreviewBox(value: Float, modifier: Modifier = Modifier) {
    // lerp：线性插值，fraction=0 返回 start，fraction=1 返回 stop
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

/**
 * 三层嵌套状态提升示例：状态集中在顶层（祖组件），逐层向下透传。
 *
 * 层级：ThemePickerScreen（持有状态）→ ThemeSection（中间层）→ ColorOptionRow（叶子层）
 * 回调沿反方向从叶子层冒泡到顶层，顶层更新状态后触发全树重组。
 */
@Composable
fun ThemePickerScreen(modifier: Modifier = Modifier) {
    // 选中颜色集中管理在顶层，避免各层各持一份导致状态不同步
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

/**
 * 主题选择的中间层，负责将 [selectedColor] 和 [onColorChange] 继续向下传递给 [ColorOptionRow]。
 * 此层本身不持有状态，是典型的"数据穿透"角色。
 */
@Composable
fun ThemeSection(selectedColor: Color, onColorChange: (Color) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.sh_s3_section_label),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ColorOptionRow(selectedColor = selectedColor, onColorChange = onColorChange)
    }
}

/**
 * 颜色选项行（叶子层），展示所有可选颜色的圆形色块。
 *
 * @param selectedColor 当前选中颜色，用于渲染选中边框
 * @param onColorChange 用户点击色块时，将新颜色通过回调冒泡给顶层
 */
@Composable
fun ColorOptionRow(selectedColor: Color, onColorChange: (Color) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        themeColors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape)
                    // Modifier.then：条件性追加 Modifier，选中时才绘制黑色边框，避免占用空间
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

/**
 * 购物车页面，将状态外置到 [CartViewModel]，这是真实业务中最常见的状态提升终点。
 *
 * ViewModel 的优势：
 * - 状态在 Activity 重建（如横竖屏旋转）时不会丢失
 * - 业务逻辑集中在 ViewModel，Composable 只负责 UI 展示
 * - 多个 Composable 可共享同一个 ViewModel 实例（在相同作用域内）
 *
 * @param viewModel 默认通过 [viewModel] 函数获取，测试时可注入 mock 实例
 */
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // collectAsState：将 StateFlow 转为 Compose State，Flow 发新值时自动触发重组
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

/**
 * 单个购物车商品行，无状态，所有操作通过回调通知调用方。
 *
 * @param item        当前商品数据（名称、数量）
 * @param onIncrement 点击"+"回调
 * @param onDecrement 点击"-"回调；数量为 0 时按钮自动禁用，防止出现负数
 */
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
        // enabled = quantity > 0：数量归零后禁用减号，由 UI 层保证不触发非法状态
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
