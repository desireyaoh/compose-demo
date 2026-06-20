package com.compose.demo.demo

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

/**
 * Compose 布局容器 & 基础组件演示页面。
 *
 * 演示内容分为两大节：
 *  1. 基础布局容器：Column（垂直）、Row（水平）、Box（层叠）
 *  2. 常见基本组件：TextField、Button、Checkbox、Switch、Slider、信息卡片
 *
 * 整体内容通过 verticalScroll 实现可滚动，适合内容超出屏幕高度的演示场景。
 */
class DemoActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge 让内容延伸到系统栏区域，配合 safeContentPadding 避免内容被遮挡
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

/**
 * 布局与组件演示的顶层 Composable。
 *
 * 使用 Column + verticalScroll 实现可滚动列表，避免内容过多时超出屏幕。
 */
@Composable
fun LayoutsAndComponentsDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ==================== 第一节：基础布局容器 ====================
        SectionTitle("1. 基础布局容器 (Layouts)")

        // Column：子元素沿垂直方向依次排列，等同于线性布局 vertical
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

        // Row：子元素沿水平方向排列，SpaceBetween 使首尾元素贴边、中间均匀分布
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

        // Box：子元素层叠排列（类似 FrameLayout），通过 align 控制各子元素在容器内的位置
        Text("Box (层叠排列):")
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            // 三个子元素分别位于左上、中央、右下，演示 Box 的层叠对齐能力
            Text("底层", modifier = Modifier.align(Alignment.TopStart).padding(4.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp).align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
            Text("顶层", modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp))
        }

        // 分节分隔线
        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        // ==================== 第二节：基本组件 ====================
        SectionTitle("2. 基本组件 (Components)")

        // OutlinedTextField：带边框的输入框，value + onValueChange 是 Compose 单向数据流的标准用法
        var textValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = { Text("输入框 (TextField)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button：内部使用 Row 排列图标和文字，onClick 留空仅作 UI 演示
        Row {
            Button(onClick = { }) {
                Icon(Icons.Default.Favorite, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("普通按钮")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Checkbox 和 Switch：两者均为受控组件，状态由 remember 持有，变化时通过回调更新
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

        // Slider：浮点值范围默认 0f~1f，mutableFloatStateOf 是专用于 Float 的状态持有，性能优于 mutableStateOf<Float>
        Text("滑动条 (Slider):")
        var sliderValue by remember { mutableFloatStateOf(0.5f) }
        Slider(value = sliderValue, onValueChange = { sliderValue = it })

        Spacer(modifier = Modifier.height(16.dp))

        // 信息卡片：Box + Row 组合，演示容器嵌套与 secondaryContainer 色彩的实际效果
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

/**
 * 章节标题组件，统一各演示节的视觉样式。
 *
 * 抽取为私有函数避免重复代码，仅供当前文件内使用。
 */
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
