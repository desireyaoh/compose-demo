package com.compose.demo.demo

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

class DemoActivity7 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeContentPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SideEffectDemoScreen()
                }
            }
        }
    }
}

@Composable
fun SideEffectDemoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.se_screen_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        SideEffectSectionHeader(stringResource(R.string.se_s1_title))
        DisposableEffectSection()

        SideEffectSectionHeader(stringResource(R.string.se_s2_title))
        SideEffectSection()

        SideEffectSectionHeader(stringResource(R.string.se_s3_title))
        ProduceStateSection()

        SideEffectSectionHeader(stringResource(R.string.se_s4_title))
        DerivedStateSection()

        SideEffectSectionHeader(stringResource(R.string.se_s5_title))
        SnapshotFlowSection()

        SideEffectSectionHeader(stringResource(R.string.se_s6_title))
        EffectComparisonSection()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private const val TAG = "测试"

// ---- Section 1: DisposableEffect ----
// 当 key 变化或 Composable 离开组合树时，onDispose 负责清理资源（如注销监听器）
@Composable
private fun DisposableEffectSection() {
    Log.d(TAG, "DisposableEffectSection 重组111")

    var isObserving by remember { mutableStateOf(false) }
    var eventLog by remember { mutableStateOf("未开始") }

    // 每次重组都会打印；对比下方 effect 块的日志可看出：重组 N 次，effect 只执行 1 次
    //    Log.d(TAG, "DisposableEffectSection 重组，isObserving=$isObserving, eventLog=$eventLog")
    Log.d(TAG, "DisposableEffectSection 重组222")

    if (isObserving) {
        DisposableEffect(Unit) {
            // key 为 Unit：进入组合树时执行一次，之后无论重组多少次都不会再执行
            Log.d(TAG, "DisposableEffect 块执行 —— 注册监听器（key=Unit，仅首次进入组合树时触发）")
            eventLog = "监听器已注册"
            onDispose {
                // 模拟注销监听器、取消订阅等清理操作
                // 触发时机：key 变化（先 dispose 旧的再执行新的）或 Composable 离开组合树
                Log.d(TAG, "onDispose 执行 —— 注销监听器（离开组合树 / key 变化时同步调用）")
                eventLog = "监听器已注销（onDispose 触发）"
            }
        }
    }

    LaunchedEffect(Unit) {
        // key 为 Unit：进入组合树时执行一次，之后无论重组多少次都不会再执行
        Log.d(TAG, "LaunchedEffect 块执行 ================================>")
    }

    DisposableEffect(Unit) {
        // key 为 Unit：进入组合树时执行一次，之后无论重组多少次都不会再执行
        Log.d(TAG, "DisposableEffect 块执行 =============================> ")
        onDispose {
            Log.d(TAG, "DisposableEffect onDispose 块执行 =============================> ")
        }
    }

    Log.d(TAG, "DisposableEffectSection 重组333")
    SideEffectCard {
        Text(
            text = stringResource(R.string.se_s1_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.se_s1_log_fmt, eventLog))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isObserving = true }, enabled = !isObserving) {
                Text(text = stringResource(R.string.se_s1_register))
            }
            OutlinedButton(onClick = { isObserving = false }, enabled = isObserving) {
                Text(text = stringResource(R.string.se_s1_unregister))
            }
        }
    }
}

// 模拟外部非 Compose 系统（如第三方 SDK 埋点计数器）
private var externalAnalyticsCounter = 0

// ---- Section 2: SideEffect ----
// 每次成功重组后执行，用于将 Compose State 同步到外部非 Compose 系统
@Composable
private fun SideEffectSection() {
    var clickCount by remember { mutableIntStateOf(0) }

    Log.d(TAG, "SideEffectSection 执行重组 111")
    SideEffect {
        // 单向出站：只把值推给外部系统，不回写 Compose State
        // 若在此处给下游 Text 读取的 State 赋值，会再次触发重组 → SideEffect 再执行，形成每帧自激的死循环
        externalAnalyticsCounter = clickCount
        Log.d(TAG,
            "SideEffect 执行111 —— 外部计数器同步为 $externalAnalyticsCounter（每次成功重组后触发）")
    }

    LaunchedEffect(Unit) {
        // key 为 Unit：仅在首次进入组合树时执行一次，与上方 SideEffect（每次重组都执行）形成对比
        externalAnalyticsCounter = clickCount
        Log.d(TAG,
            "SideEffect 执行222 LaunchedEffect —— 外部计数器同步为 $externalAnalyticsCounter（仅首次进入组合树时触发一次）")
    }

    Log.d(TAG, "SideEffectSection 执行重组 222")

    SideEffectCard {
        Log.d(TAG, "SideEffectSection 执行重组 333")
        Text(
            text = stringResource(R.string.se_s2_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.se_s2_click_fmt, clickCount))
        Text(
            text = stringResource(R.string.se_s2_logcat_tip),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { clickCount++ }) {
            Text(text = stringResource(R.string.se_s2_trigger))
        }
    }
}

// 模拟第三方 SDK 的回调式 API：只有 register/unregister，没有挂起函数，必须显式解注册
private fun interface TickerListener {
    fun onTick(count: Int)
}

private class ExternalTickerApi {
    private val handler = Handler(Looper.getMainLooper())
    private var count = 0
    private var listener: TickerListener? = null
    private val tickRunnable = object : Runnable {
        override fun run() {
            count++
            listener?.onTick(count)
            handler.postDelayed(this, 1000L)
        }
    }

    fun register(listener: TickerListener) {
        this.listener = listener
        handler.postDelayed(tickRunnable, 1000L)
        Log.d(TAG, "ExternalTickerApi.register —— 注册监听器（回调式 API，无挂起函数）")
    }

    fun unregister() {
        handler.removeCallbacks(tickRunnable)
        listener = null
        Log.d(TAG, "ExternalTickerApi.unregister —— 注销监听器")
    }
}

// ---- Section 3: produceState ----
// 将外部异步数据源（回调、挂起函数）封装为 Compose State；key 变化时重新执行 producer
@Composable
private fun ProduceStateSection() {
    var userId by remember { mutableIntStateOf(1) }
    val mockNames = remember { listOf("Alice", "Bob", "Carol", "Dave") }
    val loadingText = stringResource(R.string.se_s3_loading)

    val userName by produceState(initialValue = loadingText, key1 = userId) {
        value = loadingText
        delay(1000L)
        value = "用户 #$userId：${mockNames[(userId - 1) % mockNames.size]}"
    }

    // 是否挂载"回调式数据源"探针；关闭 / 离开组合树时会触发 awaitDispose 清理
    var isTickerAttached by remember { mutableStateOf(false) }

    SideEffectCard {
        Text(
            text = stringResource(R.string.se_s3_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.se_s3_user_fmt, userName))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { userId++ }) {
            Text(text = stringResource(R.string.se_s3_next_user))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.se_s3_await_dispose_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isTickerAttached) {
            // key1 = Unit：探针留在组合树期间只注册一次；离开组合树时触发 awaitDispose
            val tickerValue by produceState(initialValue = 0, key1 = Unit) {
                val api = ExternalTickerApi()
                val listener = TickerListener { value = it }
                api.register(listener)
                awaitDispose {
                    // 协程被取消（key 变化 / 离开组合树）前同步执行，避免监听器泄漏
                    api.unregister()
                }
            }
            Text(text = stringResource(R.string.se_s3_ticker_fmt, tickerValue))
        } else {
            Text(
                text = stringResource(R.string.se_s3_ticker_idle),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isTickerAttached = true }, enabled = !isTickerAttached) {
                Text(text = stringResource(R.string.se_s3_attach_ticker))
            }
            OutlinedButton(onClick = { isTickerAttached = false }, enabled = isTickerAttached) {
                Text(text = stringResource(R.string.se_s3_detach_ticker))
            }
        }
    }
}

// ---- Section 4: derivedStateOf ----
// 只有当派生结果真正变化时才触发依赖它的重组，避免 searchText 每次击键都重组下游 UI
@Composable
private fun DerivedStateSection() {
    var searchText by remember { mutableStateOf("") }
    val allItems = remember { listOf("abaa", "abc", "abcd") }

    // 每次 searchText 变化都会重组（因为下方 OutlinedTextField 直接读取了 searchText）
    Log.d(TAG, "DerivedStateSection111 重组 —— searchText=\"$searchText\"")

    val filteredItems by remember {
        // 用于保存上一次的结果引用：新结果与上一次内容相等时，复用旧引用，
        // 这样传给下游 Composable 的 List 引用才会保持不变，才能被 Compose 的 === 跳过判断识别为"没变"
        var lastResult: List<String> = allItems
        derivedStateOf {
            val computed = if (searchText.isBlank()) allItems
            else allItems.filter { it.contains(searchText, ignoreCase = true) }
            // 计算块只要 searchText 变化就会重新执行
            Log.d(TAG,
                "DerivedStateSection222 计算块执行 —— searchText=\"$searchText\" -> computed=$computed")
            if (computed != lastResult) {
                lastResult = computed
            }
            lastResult
        }
    }

    Log.d(TAG, "DerivedStateSection333 重组 —— searchText=\"$searchText\"")

    SideEffectCard {
        Text(
            text = stringResource(R.string.se_s4_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text(text = stringResource(R.string.se_s4_search_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        DerivedStateResultText(filteredItems)
    }
}

// 独立成子 Composable，只读取 filteredItems：
// 只有当 derivedStateOf 的计算结果真正变化（结构不相等）时才会触发本函数重组，
// 与上方每次输入都重组的 DerivedStateSection 形成对比
@Composable
private fun DerivedStateResultText(filteredItems: List<String>) {
    Log.d(TAG,
        "DerivedStateSection444 重组 —— filteredItems=$filteredItems（仅派生结果真正变化时才会触发）")
    Text(
        text = stringResource(R.string.se_s4_result_fmt, filteredItems.size,
            filteredItems.joinToString()),
        style = MaterialTheme.typography.bodyMedium
    )
}

// ---- Section 5: snapshotFlow ----
// 将 Compose State 转为 Flow，可叠加 distinctUntilChanged、collect 等操作符实现防抖
@OptIn(FlowPreview::class)
@Composable
private fun SnapshotFlowSection() {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    val initialText = stringResource(R.string.se_s5_initial)
    val debouncedFmt = stringResource(R.string.se_s5_debounced_fmt)
    var debouncedText by remember { mutableStateOf(initialText) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            Log.d(TAG, "sliderValue ========================> ${sliderValue}")
              sliderValue
           }
            .distinctUntilChanged()
            .debounce(10L.milliseconds)
            .collect { value ->
//                delay(100L)
                debouncedText = String.format(debouncedFmt, value)
            }
    }

    SideEffectCard {
        Text(
            text = stringResource(R.string.se_s5_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.se_s5_realtime_fmt, sliderValue),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(text = debouncedText, style = MaterialTheme.typography.bodyMedium)
    }
}

// ---- Section 6: LaunchedEffect vs DisposableEffect ----
// 二者都会在 key 变化 / 离开组合树时结束上一轮，但「结束方式」和「能做什么」完全不同：
//   LaunchedEffect   —— 协程，可挂起；结束 = 取消协程，没有清理回调，只能靠 try/finally
//   DisposableEffect —— 普通代码块，不可挂起；结束 = 同步执行 onDispose，专用于释放资源
@Composable
private fun EffectComparisonSection() {
    var sessionId by remember { mutableIntStateOf(1) }
    var isAttached by remember { mutableStateOf(true) }
    // 日志提升到探针之外，探针被卸载后仍能看到它留下的收尾记录
    val eventLogs = remember { mutableStateListOf<String>() }
    val logLineFmt = stringResource(R.string.se_s6_log_line_fmt)

    SideEffectCard {
        Text(
            text = stringResource(R.string.se_s6_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.se_s6_session_fmt, sessionId))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { sessionId++ }) {
                Text(text = stringResource(R.string.se_s6_switch_key))
            }
            OutlinedButton(onClick = { isAttached = !isAttached }) {
                Text(
                    text = stringResource(
                        if (isAttached) R.string.se_s6_detach else R.string.se_s6_attach
                    )
                )
            }
        }
        OutlinedButton(onClick = { eventLogs.clear() }) {
            Text(text = stringResource(R.string.se_s6_clear_log))
        }

        if (isAttached) {
            // 两个 Effect 共用同一个 sessionId 作为 key，便于直接对比行为差异
            EffectComparisonProbe(
                sessionId = sessionId,
                onEvent = { message ->
                    eventLogs.add(String.format(logLineFmt, eventLogs.size + 1, message))
                }
            )
        } else {
            Text(
                text = stringResource(R.string.se_s6_detached),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.se_s6_log_title),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        if (eventLogs.isEmpty()) {
            Text(
                text = stringResource(R.string.se_s6_log_empty),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            eventLogs.takeLast(10).forEach { log ->
                Text(text = log, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.se_s6_summary),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

// 探针：把两种 Effect 放在同一个 key 上，卸载它即可观察各自离开组合树时的行为
@Composable
private fun EffectComparisonProbe(sessionId: Int, onEvent: (String) -> Unit) {
    val launchStartFmt = stringResource(R.string.se_s6_le_start_fmt)
    val launchDoneFmt = stringResource(R.string.se_s6_le_done_fmt)
    val launchCancelFmt = stringResource(R.string.se_s6_le_cancel_fmt)
    val disposeRegisterFmt = stringResource(R.string.se_s6_de_register_fmt)
    val disposeCleanupFmt = stringResource(R.string.se_s6_de_dispose_fmt)

    LaunchedEffect(sessionId) {
        onEvent(String.format(launchStartFmt, sessionId))
        try {
            // 挂起调用：DisposableEffect 的代码块里写不了这一行
            delay(3000L)
            onEvent(String.format(launchDoneFmt, sessionId))
        } finally {
            // 协程被取消时没有 onDispose 可用，收尾只能写在 finally 里
            if (!isActive) {
                onEvent(String.format(launchCancelFmt, sessionId))
            }
        }
    }

    DisposableEffect(sessionId) {
        // 同步执行，不能挂起；返回值必须是 onDispose
        onEvent(String.format(disposeRegisterFmt, sessionId))
        onDispose {
            onEvent(String.format(disposeCleanupFmt, sessionId))
        }
    }
}

@Composable
private fun SideEffectSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 4.dp)
    )
}

@Composable
private fun SideEffectCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
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
fun SideEffectDemoScreenPreview() {
    ComposedemoTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SideEffectDemoScreen()
        }
    }
}

@Preview(showBackground = true, name = "Section 1: DisposableEffect")
@Composable
private fun DisposableEffectSectionPreview() {
    ComposedemoTheme {
        DisposableEffectSection()
    }
}

@Preview(showBackground = true, name = "Section 2: SideEffect")
@Composable
private fun SideEffectSectionPreview() {
    ComposedemoTheme {
        SideEffectSection()
    }
}

@Preview(showBackground = true, name = "Section 3: produceState")
@Composable
private fun ProduceStateSectionPreview() {
    ComposedemoTheme {
        ProduceStateSection()
    }
}

@Preview(showBackground = true, name = "Section 4: derivedStateOf")
@Composable
private fun DerivedStateSectionPreview() {
    ComposedemoTheme {
        DerivedStateSection()
    }
}

@Preview(showBackground = true, name = "Section 5: snapshotFlow")
@Composable
private fun SnapshotFlowSectionPreview() {
    ComposedemoTheme {
        SnapshotFlowSection()
    }
}

@Preview(showBackground = true, name = "Section 6: Launched vs Disposable")
@Composable
private fun EffectComparisonSectionPreview() {
    ComposedemoTheme {
        EffectComparisonSection()
    }
}
