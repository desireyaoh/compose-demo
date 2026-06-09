package com.compose.demo.mine

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme


/**
 * 测试Activity
 */
class TestActivity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 顶层容器负责安全区域适配
            Column(modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(32.dp)) {
                // 原有的组件
                TestSection1(name = "这里显示名称") {
                    Log.d("TestActivity1", "onClick")
                }

                //                Spacer(modifier = Modifier.height(24.dp))
                //                HorizontalDivider()
                //                Spacer(modifier = Modifier.height(24.dp))

                // 状态提升演示组件
                //                StateHoistingDemo()
            }
        }
    }
}

/**
 * 封装后的用户信息组件
 *
 * @param name 显示的名称
 * @param modifier 修饰符，允许外部调用者调整布局
 * @param onActionClick 点击按钮时的逻辑回调
 */
@Composable
fun TestSection1(
    name: String,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {}
) {
    // 按钮文案作为组件内部状态
    var showName by remember { mutableStateOf(name) }
    val defaultText = stringResource(id = R.string.user_info_button_default)
    val clickedText = stringResource(id = R.string.user_info_button_clicked)
    var buttonText by remember { mutableStateOf(defaultText) }

    Column(modifier = modifier) {
        Text(
            text = showName,
            modifier = Modifier.background(Color.LightGray),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black
        )

        Image(
            painter = painterResource(id = R.drawable.ic_language),
            contentDescription = "avatar"
        )

        Button(onClick = {
            onActionClick()
            buttonText = clickedText
            showName = "随便改个名称"
        }) {
            Text(buttonText)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserInfoSectionPreview() {
    ComposedemoTheme {
        TestSection1(name = "这里显示名称")
    }
}

/**
 * 演示：状态提升 (State Hoisting)
 * 父组件负责管理状态，子组件只接收数据和回调
 */
@Composable
fun StateHoistingDemo(modifier: Modifier = Modifier) {
    // 1. 在父组件定义状态
    var textState by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Text(text = "父组件中显示的状态: $textState", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // 2. 将状态(textState)和改变状态的逻辑(onValueChange)传递给子组件
        StatelessChild(
            inputValue = textState,
            onValueChange = { newValue ->
                textState = newValue // 父组件更新状态
            }
        )
    }
}

/**
 * 无状态子组件 (Stateless Composable)
 * 它不知道状态是如何存储的，只负责显示和触发事件
 */
@Composable
fun StatelessChild(
    inputValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = inputValue,
            onValueChange = onValueChange, // 发生变化时通知父组件
            label = { Text("子组件输入框") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StateHoistingDemoPreview() {
    ComposedemoTheme {
        StateHoistingDemo()
    }
}
