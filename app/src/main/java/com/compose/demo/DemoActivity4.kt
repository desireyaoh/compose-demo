package com.compose.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.demo.ui.theme.ComposedemoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 1. 定义 ViewModel
// ViewModel 负责管理数据和业务逻辑，其生命周期长于 Activity，即使配置更改（如旋转屏幕）数据也不会丢失
class MyViewModel : ViewModel() {
    // 使用 StateFlow 存储数据，这是现代 Android 开发推荐的响应式数据流
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    fun increment() {
        _count.value++
    }

    fun decrement() {
        _count.value--
    }

    fun onUserInputChange(newValue: String) {
        _userInput.value = newValue
    }
}

class DemoActivity4 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().safeContentPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. 在 Composable 中获取 ViewModel
                    // 注意：需要添加 androidx.lifecycle:lifecycle-viewmodel-compose 依赖
                    // 如果没有该依赖，可以从 Activity 传进来
                    val viewModel: MyViewModel = viewModel()
                    ViewModelDemoScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ViewModelDemoScreen(viewModel: MyViewModel) {
    // 3. 将 StateFlow 转换为 Compose 能够识别的 State
    // collectAsState 会在 Flow 发射新值时自动触发重组
    val count by viewModel.count.collectAsState()
    val userInput by viewModel.userInput.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compose + ViewModel 示例",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 计数器部分
        Text(text = "计数器状态 (由 ViewModel 管理):", style = MaterialTheme.typography.titleMedium)
        Text(text = "$count", fontSize = 48.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.decrement() }) {
                Text("-1")
            }
            Button(onClick = { viewModel.increment() }) {
                Text("+1")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 输入框部分
        Text(text = "输入框状态 (由 ViewModel 管理):", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = userInput,
            onValueChange = { viewModel.onUserInputChange(it) },
            label = { Text("请输入内容") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(text = "同步显示: $userInput", color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "优势：当你旋转屏幕时，ViewModel 中的数据不会丢失，UI 会自动恢复到之前的状态。",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}
