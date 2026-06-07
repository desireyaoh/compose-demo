package com.compose.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compose.demo.ui.theme.ComposedemoTheme

class DemoActivity2 : ComponentActivity() {
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
                    LayoutsAndComponentsDemo()
                }
            }
        }
    }
}

@Composable
fun LayoutsAndComponentsDemo() {
    // 使用 Column + verticalScroll 实现可滚动的垂直布局
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- 1. 基础布局容器 ---
        SectionTitle("1. 基础布局容器 (Layouts)")

        // Column: 垂直排列
        Text("Column (垂直排列):")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.3f))
                .padding(8.dp)
        ) {
            Text("第一行")
            Text("第二行")
            Text("第三行")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row: 水平排列
        Text("Row (水平排列):")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.3f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("左侧文本")
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
            Text("右侧文本")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Box: 层叠排列 (类似 FrameLayout)
        Text("Box (层叠排列):")
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text("底层", modifier = Modifier.align(Alignment.TopStart).padding(4.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp).align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
            Text("顶层", modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        // --- 2. 常见基本组件 ---
        SectionTitle("2. 基本组件 (Components)")

        // 输入框
        var textValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = { Text("输入框 (TextField)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 按钮
        Row {
            Button(onClick = { }) {
                Icon(Icons.Default.Favorite, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("普通按钮")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 选择器组件
        Row(verticalAlignment = Alignment.CenterVertically) {
            var checked by remember { mutableStateOf(true) }
            Checkbox(checked = checked, onCheckedChange = { checked = it })
            Text("复选框")

            Spacer(Modifier.width(20.dp))

            var switched by remember { mutableStateOf(false) }
            Switch(checked = switched, onCheckedChange = { switched = it })
            Text("开关")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 滑动条
        Text("滑动条 (Slider):")
        var sliderValue by remember { mutableFloatStateOf(0.5f) }
        Slider(value = sliderValue, onValueChange = { sliderValue = it })

        Spacer(modifier = Modifier.height(16.dp))

        // 包含交互的小示例
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "这是一张信息卡片，使用了 Row 和 Box 的组合。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
