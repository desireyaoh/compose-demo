package com.compose.demo.demo

import android.os.Bundle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

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

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ---- Section 1: DisposableEffect ----
// 当 key 变化或 Composable 离开组合树时，onDispose 负责清理资源（如注销监听器）
@Composable
private fun DisposableEffectSection() {
    var isObserving by remember { mutableStateOf(false) }
    var eventLog by remember { mutableStateOf("未开始") }

    if (isObserving) {
        DisposableEffect(Unit) {
            eventLog = "监听器已注册"
            onDispose {
                // 模拟注销监听器、取消订阅等清理操作
                eventLog = "监听器已注销（onDispose 触发）"
            }
        }
    }

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
    var syncedValue by remember { mutableIntStateOf(0) }

    SideEffect {
        externalAnalyticsCounter++
        syncedValue = externalAnalyticsCounter
    }

    SideEffectCard {
        Text(
            text = stringResource(R.string.se_s2_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.se_s2_synced_fmt, syncedValue))
        Text(text = stringResource(R.string.se_s2_click_fmt, clickCount))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { clickCount++ }) {
            Text(text = stringResource(R.string.se_s2_trigger))
        }
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
    }
}

// ---- Section 4: derivedStateOf ----
// 只有当派生结果真正变化时才触发依赖它的重组，避免 searchText 每次击键都重组下游 UI
@Composable
private fun DerivedStateSection() {
    var searchText by remember { mutableStateOf("") }
    val allItems = remember { listOf("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape") }

    val filteredItems by remember {
        derivedStateOf {
            if (searchText.isBlank()) allItems
            else allItems.filter { it.contains(searchText, ignoreCase = true) }
        }
    }

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
        Text(
            text = stringResource(R.string.se_s4_result_fmt, filteredItems.size, filteredItems.joinToString()),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ---- Section 5: snapshotFlow ----
// 将 Compose State 转为 Flow，可叠加 distinctUntilChanged、collect 等操作符实现防抖
@Composable
private fun SnapshotFlowSection() {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    val initialText = stringResource(R.string.se_s5_initial)
    val debouncedFmt = stringResource(R.string.se_s5_debounced_fmt)
    var debouncedText by remember { mutableStateOf(initialText) }

    LaunchedEffect(Unit) {
        snapshotFlow { sliderValue }
            .distinctUntilChanged()
            .collect { value ->
                delay(400L)  // 400ms 防抖：频繁滑动时只处理停止后的最终值
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
