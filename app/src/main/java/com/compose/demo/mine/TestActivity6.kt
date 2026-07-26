package com.compose.demo.mine

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Compose 协程综合演示页面。
 *
 * 六个递进示例：
 * 1. [LaunchedEffectDemo]：以 key 驱动协程启动与取消（倒计时）
 * 2. [RememberCoroutineScopeDemo]：在事件处理器中手动启动协程（模拟提交）
 * 3. [FlowCollectDemo]：cold Flow + collectAsState 自动刷新 UI（心跳计数）
 * 4. [NewsScreen]：ViewModel + StateFlow + Loading/Success/Error 三态（模拟网络请求，sealed interface）
 * 4-2. [NewsScreen2]：与 4 相同的场景，但 [NewsUiState2] 换成普通类 + 属性组合，对照两种建模方式的差异
 * 5. [RecompositionScopeDemo]：演示重组作用域只对读取了状态的函数生效
 */
class TestActivity6 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                CoroutineScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoroutineScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_test6)) },
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
            item { LaunchedEffectDemo() }
            item { RememberCoroutineScopeDemo() }
            item { FlowCollectDemo() }
            item { NewsScreen() }
            item { NewsScreen2() }
            item { RecompositionScopeDemo() }
            item { RecompositionScopeDemo2() }
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

// ─── Section 1: LaunchedEffect — 倒计时 ────────────────────────────────────

/**
 * LaunchedEffect 示例：以 [isRunning] 为 key，key 变化时自动取消旧协程、启动新协程。
 *
 * 点击"开始"→ isRunning=true → LaunchedEffect 重启 → 协程逐秒递减 seconds。
 * 点击"重置"→ isRunning=false → LaunchedEffect 取消协程 → seconds 回到初始值。
 */
@Composable
fun LaunchedEffectDemo(modifier: Modifier = Modifier) {
    var isRunning by remember { mutableStateOf(false) }
    var isStart by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(COUNTDOWN_INITIAL) }

    LaunchedEffect(isRunning, isStart) {
        Log.d("测试", "LaunchedEffect isRunning=$isRunning")
        if (isRunning) {
            while (seconds > 0) {
                delay(1_000L)
                seconds--
            }
            isRunning = false
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s1_title))
        Text(
            text = stringResource(R.string.co_s1_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (seconds == 0)
                stringResource(R.string.co_s1_finished)
            else
                stringResource(R.string.co_s1_countdown_fmt, seconds),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { isRunning = true },
                enabled = !isRunning && seconds > 0
            ) {
                Text(stringResource(R.string.co_s1_start))
            }
            OutlinedButton(onClick = {
                isRunning = false
                seconds = COUNTDOWN_INITIAL
            }) {
                Text(stringResource(R.string.co_s1_reset))
            }
        }
    }
}

private const val COUNTDOWN_INITIAL = 10

// ─── Section 2: rememberCoroutineScope — 手动启动协程 ──────────────────────

private enum class SubmitStatus { Idle, Submitting, Success }

/**
 * rememberCoroutineScope 示例：在点击回调（非 Composable 上下文）中手动启动协程。
 *
 * 与 LaunchedEffect 的区别：scope.launch 由开发者在任意时刻手动调用，
 * 而 LaunchedEffect 由 Compose 运行时在 key 变化时自动触发。
 */
@Composable
fun RememberCoroutineScopeDemo(modifier: Modifier = Modifier) {
    // rememberCoroutineScope 返回的 scope 与当前 Composable 生命周期绑定，
    // Composable 离开组合时 scope 自动取消，防止内存泄漏
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(SubmitStatus.Idle) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s2_title))
        Text(
            text = stringResource(R.string.co_s2_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; status = SubmitStatus.Idle },
            label = { Text(stringResource(R.string.co_s2_input_label)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = status != SubmitStatus.Submitting
        )
        val statusText = when (status) {
            SubmitStatus.Submitting -> stringResource(R.string.co_s2_submitting)
            SubmitStatus.Success -> stringResource(R.string.co_s2_success)
            SubmitStatus.Idle -> stringResource(R.string.co_s2_idle)
        }
        val statusColor = when (status) {
            SubmitStatus.Success -> MaterialTheme.colorScheme.primary
            SubmitStatus.Submitting -> MaterialTheme.colorScheme.secondary
            SubmitStatus.Idle -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        Text(text = statusText, color = statusColor)
        Button(
            onClick = {
                scope.launch {
                    status = SubmitStatus.Submitting
                    delay(1_500L)
                    status = SubmitStatus.Success
                }
            },
            enabled = username.isNotBlank() && status != SubmitStatus.Submitting
        ) {
            Text(stringResource(R.string.co_s2_submit))
        }
    }
}

// ─── Section 3: Flow + collectAsState — 实时数据流 ─────────────────────────

// 每秒递增一次的 cold flow，在 Composable 外定义避免重组时重复创建
private fun tickerFlow(): Flow<Int> = flow {
    var count = 0
    while (true) {
        emit(count++)
        delay(1_000L)
    }
}

/**
 * Flow + collectAsState 示例：cold Flow 在 Composable 进入组合时开始收集，
 * 离开时自动取消，每次 Flow 发射新值都触发 UI 重组。
 */
@Composable
fun FlowCollectDemo(modifier: Modifier = Modifier) {
    // remember 保证跨重组复用同一 Flow 实例；collectAsState 负责收集并转为 State<T>
    val tick by remember { tickerFlow() }.collectAsState(initial = 0)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s3_title))
        Text(
            text = stringResource(R.string.co_s3_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.co_s3_tick_fmt, tick),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ─── Section 4: ViewModel + StateFlow — 模拟网络请求 ───────────────────────

sealed interface NewsUiState {
    data object Idle : NewsUiState
    data object Loading : NewsUiState
    data class Success(val items: List<String>) : NewsUiState
    data class Error(val message: String) : NewsUiState
}

class NewsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Idle)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val fakeNewsFeed = listOf(
        "Compose 协程最佳实践已更新",
        "Android 14 正式向所有设备推送",
        "Kotlin 2.0 性能较上一版本提升 30%",
        "Jetpack 库迎来 2024 年度重大更新",
        "Material 3 自适应布局 API 正式稳定"
    )

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            delay(2_000L)
            // 80% 概率成功，20% 概率失败，模拟真实网络波动
            if ((0..4).random() > 0) {
                _uiState.value = NewsUiState.Success(fakeNewsFeed.shuffled().take(3))
            } else {
                _uiState.value = NewsUiState.Error("网络连接超时，请检查网络后重试")
            }
        }
    }
}

/**
 * ViewModel + StateFlow 示例：协程运行在 ViewModel 的 viewModelScope 中，
 * 与 Activity/Fragment 生命周期解耦，屏幕旋转后状态不会丢失。
 *
 * UI 根据 [NewsUiState] 的不同子类型渲染对应内容（三态：加载中 / 成功 / 错误）。
 */
@Composable
fun NewsScreen(
    viewModel: NewsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    NewsSection(
        uiState = uiState,
        onLoadClick = { viewModel.loadData() },
        modifier = modifier
    )
}

/**
 * 无状态的新闻区块，方便预览和测试。
 */
@Composable
fun NewsSection(
    uiState: NewsUiState,
    onLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("测试", "NewsSection 重组，uiState=$uiState")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s4_title))
        when (uiState) {
            is NewsUiState.Idle -> {
                Button(onClick = onLoadClick) {
                    Text(stringResource(R.string.co_s4_load_btn))
                }
            }

            is NewsUiState.Loading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text(stringResource(R.string.co_s4_loading))
                }
            }

            is NewsUiState.Success -> {
                Text(
                    text = stringResource(R.string.co_s4_news_title),
                    fontWeight = FontWeight.Bold
                )
                uiState.items.forEach { item ->
                    Text(text = "• $item", fontSize = 14.sp)
                }
                OutlinedButton(onClick = onLoadClick) {
                    Text(stringResource(R.string.co_s4_refresh))
                }
            }

            is NewsUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = onLoadClick) {
                    Text(stringResource(R.string.co_s4_retry))
                }
            }
        }
    }
}

// ─── Section 4-2: 同样的场景，换成普通类 + 属性组合（不用 sealed/interface）──

/**
 * 与 [NewsUiState] 表达同一份网络请求状态，但不用"类型层级"区分状态，
 * 而是用一个普通 `data class` 的多个属性组合来表达"当前处于哪种状态"：
 * - `isLoading = true` 表示加载中
 * - `errorMessage != null` 表示失败
 * - `items` 非空表示成功
 * - 三者都不成立时表示初始态（Idle）
 *
 * 这种写法能编译、能跑，但状态之间的优先级、互斥关系全靠开发者口头约定，
 * 类型系统完全不做限制——比如 `isLoading = true` 同时 `errorMessage` 也不为空
 * 这种"非法状态"照样能被构造出来，[NewsSection2] 里 `if/else` 的判断顺序
 * 也必须自己保证覆盖完整，编译器不会像 sealed 版本那样帮忙检查。
 */
data class NewsUiState2(
    val isLoading: Boolean = false,
    val items: List<String> = emptyList(),
    val errorMessage: String? = null
)


class NewsViewModel2 : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState2())
    val uiState: StateFlow<NewsUiState2> = _uiState.asStateFlow()

    private val fakeNewsFeed = listOf(
        "Compose 协程最佳实践已更新",
        "Android 14 正式向所有设备推送",
        "Kotlin 2.0 性能较上一版本提升 30%",
        "Jetpack 库迎来 2024 年度重大更新",
        "Material 3 自适应布局 API 正式稳定"
    )

    fun loadData() {
        viewModelScope.launch {
//            _uiState.value = NewsUiState2(isLoading = true)
//            delay(2_000L)
            // 80% 概率成功，20% 概率失败，模拟真实网络波动
//            if ((0..4).random() > 0) {
//                _uiState.value = NewsUiState2(items = fakeNewsFeed.shuffled().take(3))
//            } else {
//                _uiState.value = NewsUiState2(errorMessage = "网络连接超时，请检查网络后重试")
//            }

            _uiState.update { it.copy(isLoading = true) }
            delay(2_000L)
            _uiState.update { it.copy(errorMessage = "网络连接超时，请检查网络后重试") }

        }
    }
}

/**
 * ViewModel + StateFlow 示例（属性组合版本）：交互与展示效果和 [NewsScreen] 完全一致，
 * 仅把 [NewsUiState2] 从 sealed 类型层级换成普通类的属性组合，用于对照两种写法的差异。
 */
@Composable
fun NewsScreen2(
    viewModel: NewsViewModel2 = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by remember {
        derivedStateOf { uiState.isLoading }
    }
    val errorMessage by remember {
        derivedStateOf { uiState.errorMessage }
    }
    NewsSection2(
        isLoading = isLoading,
        errorMessage = errorMessage,
        onLoadClick = { viewModel.loadData() },
        modifier = modifier
    )
}


/**
 * 无状态的新闻区块（属性组合版本），方便预览和测试。
 */
@Composable
fun NewsSection2(
    isLoading: Boolean,
    errorMessage: String?,
    onLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("测试", "NewsSection2 重组，isLoading = $isLoading, errorMessage = $errorMessage")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s4b_title))
        Text(
            text = stringResource(R.string.co_s4b_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 没有类型层级可供 when 穷尽，只能靠 if/else 手动约定优先级：
        // 加载中 > 出错 > 初始态，顺序写反或漏写都不会编译报错。
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(stringResource(R.string.co_s4_loading))
            }
        } else if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onLoadClick) {
                Text(stringResource(R.string.co_s4_retry))
            }
        } else {
            Button(onClick = onLoadClick) {
                Text(stringResource(R.string.co_s4_load_btn))
            }
        }


    }
}

// ─── Section 5: 重组作用域 — 状态只在读取它的函数内触发重组 ────────────────

/**
 * 演示 Compose 的重组作用域（Recomposition Scope）：
 * 状态在哪个函数内部被声明并读取（`remember`/`by` 取值），重组就只会发生在
 * 那个函数（及其子级），不会波及没有读取该状态的父级或兄弟 Composable。
 *
 * 点击"卡片 A 计数+1"时，Logcat 只会打印 [CounterCard]（tag=A）的重组日志，
 * 父级 [RecompositionScopeDemo] 和兄弟 [CounterCard]（tag=B）都不会重组。
 */
@Composable
fun RecompositionScopeDemo(modifier: Modifier = Modifier) {
    // 父级自身没有读取任何计数状态，理论上只会在首次组合时打印一次
    Log.d("测试", "RecompositionScopeDemo 重组（父级，未读取任何计数状态）")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s5_title))
        Text(
            text = stringResource(R.string.co_s5_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CounterCard(tag = "A")
            CounterCard(tag = "B")
        }
    }
}


/**
 * 独立计数卡片：状态在自己内部声明并读取，点击自身按钮只会触发自身函数重组。
 * @param tag 用于日志区分不同实例，便于对照 Logcat 观察重组范围
 */
@Composable
private fun CounterCard(tag: String) {
    var count by remember { mutableIntStateOf(0) }
    Log.d("测试", "CounterCard(tag=$tag) 重组，count=$count")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.co_s5_count_fmt, tag, count), fontSize = 16.sp)
        Button(onClick = { count++ }) {
            Text(stringResource(R.string.co_s5_increment_fmt, tag))
        }
    }
}

/**
 * 演示 Compose 重组作用域：父级本身不读取任何 [count] 状态，
 * 只是把状态和更新回调传递给子级 [CounterCard1]，
 * 因此点击按钮触发状态变化时，只有真正读取该状态的子级会重组，父级不会重组。
 * @param modifier 布局修饰符
 */
@Composable
fun RecompositionScopeDemo2(modifier: Modifier = Modifier) {
    // 父级自身没有读取任何计数状态，理论上只会在首次组合时打印一次
    Log.d("测试", "RecompositionScopeDemo 重组（父级，未读取任何计数状态）")
    var count by remember { mutableIntStateOf(0) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s5_title))
        Text(
            text = stringResource(R.string.co_s5_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CounterCard1(tag = "A", count) {
                count++
            }
            CounterCard1(tag = "B", count) {
                count--
            }
        }
    }
}


/**
 * 计数卡片，展示某一路计数的当前值，并提供触发计数变化的按钮。
 * 由于读取了外部传入的 [count] 状态，该状态变化时会触发本组件重组。
 * @param tag 卡片标识（如 "A"、"B"），用于区分不同的计数卡片
 * @param count 当前计数值
 * @param onClick 点击按钮时的回调，由调用方决定计数如何变化（如自增/自减）
 */
@Composable
private fun CounterCard1(tag: String, count: Int, onClick: () -> Unit) {
    Log.d("测试", "CounterCard(tag=$tag) 重组，count=$count")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.co_s5_count_fmt, tag, count), fontSize = 16.sp)
        Button(onClick = { onClick() }) {
            Text(stringResource(R.string.co_s5_increment_fmt, tag))
        }
    }
}


// ─── Previews ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CoroutineScreenPreview() {
    ComposedemoTheme {
        CoroutineScreen()
    }
}

@Preview(showBackground = true, name = "Section 1: LaunchedEffect")
@Composable
private fun LaunchedEffectDemoPreview() {
    ComposedemoTheme {
        LaunchedEffectDemo(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, name = "Section 2: CoroutineScope")
@Composable
private fun RememberCoroutineScopeDemoPreview() {
    ComposedemoTheme {
        RememberCoroutineScopeDemo(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, name = "Section 3: Flow")
@Composable
private fun FlowCollectDemoPreview() {
    ComposedemoTheme {
        FlowCollectDemo(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, name = "Section 4: News (Success)")
@Composable
private fun NewsSectionSuccessPreview() {
    ComposedemoTheme {
        NewsSection(
            uiState = NewsUiState.Success(
                listOf(
                    "Compose 协程最佳实践已更新",
                    "Android 15 正式推送",
                    "Kotlin 2.0 性能提升"
                )
            ),
            onLoadClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

//@Preview(showBackground = true, name = "Section 4-2: News（属性组合）(Success)")
//@Composable
//private fun NewsSection2SuccessPreview() {
//    ComposedemoTheme {
//        NewsSection2(
//            uiState = NewsUiState2(
//                items = listOf(
//                    "Compose 协程最佳实践已更新",
//                    "Android 15 正式推送",
//                    "Kotlin 2.0 性能提升"
//                )
//            ),
//            onLoadClick = {},
//            modifier = Modifier.padding(16.dp)
//        )
//    }
//}
