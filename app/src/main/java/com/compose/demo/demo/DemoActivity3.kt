package com.compose.demo.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.ui.theme.ComposedemoTheme

/**
 * Compose 状态管理示例页面入口 Activity。
 * 演示 `mutableStateOf` / `mutableIntStateOf` 配合 `remember` 的四种典型用法：
 * 数值状态、布尔状态、对象状态（data class）和字符串状态。
 */
class DemoActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().safeContentPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StateUsageDemo()
                }
            }
        }
    }
}

@Composable
fun StateUsageDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "mutableStateOf 使用详解",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "在 Compose 中，UI 是不可变的。要改变界面，你需要改变状态，引起“重组”。",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 示例 1: 基础计数器 ---
        SectionHeader("1. 基础数值状态")
        CounterExample()

        Spacer(modifier = Modifier.height(32.dp))

        // --- 示例 2: 布尔切换状态 ---
        SectionHeader("2. 布尔切换 (显示/隐藏/样式)")
        ToggleStyleExample()

        Spacer(modifier = Modifier.height(32.dp))

        // --- 示例 3: 对象状态 ---
        SectionHeader("3. 对象状态 (Data Class)")
        ObjectStateExample()

        Spacer(modifier = Modifier.height(32.dp))

        // --- 示例 4: 字符串状态 ---
        SectionHeader("4. 基础字符串状态")
        StringStateExample()
    }
}

@Composable
fun CounterExample() {
    // 使用 remember 保证在重组时不会丢失状态
    // mutableIntStateOf 是专门为 Int 优化的版本，减少装箱开销
    var count by remember { mutableIntStateOf(0) }

    Column {
        Text(text = "当前计数: $count", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Button(onClick = { count++ }) {
            Text("点击加 1")
        }
    }
}

@Composable
fun ToggleStyleExample() {
    // 控制是否激活状态
    var isActive by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isActive) Color.Green else Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(if (isActive) "开" else "关", color = Color.White)
        }
        
        Button(onClick = { isActive = !isActive }) {
            Text("切换颜色和文字")
        }
    }
}

/**
 * 对象状态示例用的数据类。
 * @property name 姓名
 * @property age 年龄
 */
data class UserInfo(val name: String, val age: Int)

@Composable
fun ObjectStateExample() {
    // 初始状态
    var user by remember { mutableStateOf(UserInfo("张三", 20)) }

    Column {
        Text(text = "姓名: ${user.name}, 年龄: ${user.age}")
        
        Button(onClick = {
            // 注意：必须要创建新对象或调用 copy() 才会触发 UI 刷新
            // 因为 Compose 观察的是对象的引用变化
            user = user.copy(age = user.age + 1)
        }) {
            Text("涨一岁 (修改对象属性)")
        }
    }
}

@Composable
fun StringStateExample() {
    // 字符串状态示例
    var text by remember { mutableStateOf("初始文字") }

    Column {
        Text(text = "当前文字内容: $text", fontSize = 18.sp)
        
        Button(onClick = {
            // 直接赋值新字符串即可触发重组
            text = "文字已更新 (${(100..999).random()})"
        }) {
            Text("修改字符串内容")
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

/** 整页预览：与 Activity 中一致，包裹主题和 Surface */
@Preview(showBackground = true, showSystemUi = true, name = "状态示例整页")
@Composable
private fun StateUsageDemoPreview() {
    ComposedemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            StateUsageDemo()
        }
    }
}

/** 计数器示例单独预览 */
@Preview(showBackground = true, name = "计数器示例")
@Composable
private fun CounterExamplePreview() {
    ComposedemoTheme {
        CounterExample()
    }
}

/** 布尔切换示例单独预览 */
@Preview(showBackground = true, name = "布尔切换示例")
@Composable
private fun ToggleStyleExamplePreview() {
    ComposedemoTheme {
        ToggleStyleExample()
    }
}

/** 对象状态示例单独预览 */
@Preview(showBackground = true, name = "对象状态示例")
@Composable
private fun ObjectStateExamplePreview() {
    ComposedemoTheme {
        ObjectStateExample()
    }
}

/** 字符串状态示例单独预览 */
@Preview(showBackground = true, name = "字符串状态示例")
@Composable
private fun StringStateExamplePreview() {
    ComposedemoTheme {
        StringStateExample()
    }
}
