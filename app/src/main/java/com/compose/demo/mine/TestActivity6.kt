package com.compose.demo.mine

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
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.launch

/**
 * Compose 协程综合演示页面。
 *
 * 四个递进示例：
 * 1. [LaunchedEffectDemo]：以 key 驱动协程启动与取消（倒计时）
 * 2. [RememberCoroutineScopeDemo]：在事件处理器中手动启动协程（模拟提交）
 * 3. [FlowCollectDemo]：cold Flow + collectAsState 自动刷新 UI（心跳计数）
 * 4. [NewsScreen]：ViewModel + StateFlow + Loading/Success/Error 三态（模拟网络请求）
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

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.co_s4_title))
        when (val state = uiState) {
            is NewsUiState.Idle -> {
                Button(onClick = { viewModel.loadData() }) {
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
                state.items.forEach { item ->
                    Text(text = "• $item", fontSize = 14.sp)
                }
                OutlinedButton(onClick = { viewModel.loadData() }) {
                    Text(stringResource(R.string.co_s4_refresh))
                }
            }

            is NewsUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = { viewModel.loadData() }) {
                    Text(stringResource(R.string.co_s4_retry))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoroutineScreenPreview() {
    ComposedemoTheme {
        CoroutineScreen()
    }
}
