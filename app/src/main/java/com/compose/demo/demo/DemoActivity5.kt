package com.compose.demo.demo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.ui.theme.ComposedemoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DemoActivity5 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().safeContentPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CoroutineDemoScreen()
                }
            }
        }
    }
}

@Composable
fun CoroutineDemoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compose 协程示例",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 示例 1: LaunchedEffect ---
        CoroutineDemoSectionTitle("1. LaunchedEffect (自动启动)")
        Text("适用于：进入页面自动加载、定时器、根据某个 key 变化执行任务。", style = MaterialTheme.typography.bodySmall)
        TimerExample()

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        // --- 示例 2: rememberCoroutineScope ---
        CoroutineDemoSectionTitle("2. rememberCoroutineScope (手动点击)")
        Text("适用于：在按钮点击等事件回调中启动协程。", style = MaterialTheme.typography.bodySmall)
        ClickCoroutineExample()
    }
}

@Composable
fun TimerExample() {
    var timeLeft by remember { mutableIntStateOf(10) }
    var isRunning by remember { mutableStateOf(true) }

    // LaunchedEffect 会在 Composable 进入界面时启动协程
    // 当 key (isRunning) 发生变化时，旧的协程会取消，新的会重新启动
    LaunchedEffect(isRunning) {
        if (isRunning && timeLeft > 0) {
            while (timeLeft > 0) {
                delay(1000) // 挂起一秒
                timeLeft--
            }
            isRunning = false
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "倒计时: $timeLeft 秒", fontSize = 24.sp)
        Button(onClick = { 
            if (timeLeft == 0) timeLeft = 10 
            isRunning = !isRunning 
        }) {
            Text(if (isRunning) "暂停" else if (timeLeft == 0) "重置并开始" else "继续")
        }
    }
}

@Composable
fun ClickCoroutineExample() {
    val context = LocalContext.current
    // 获取一个与当前 Composable 生命周期绑定的协程作用域
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoading) {
            CircularProgressIndicator()
            Text("模拟网络请求中...")
        } else {
            Button(onClick = {
                // 在点击事件中直接启动协程
                scope.launch {
                    isLoading = true
                    delay(2000) // 模拟耗时操作
                    isLoading = false
                    Toast.makeText(context, "数据加载成功！", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("发起异步请求")
            }
        }
    }
}

@Composable
private fun CoroutineDemoSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}
