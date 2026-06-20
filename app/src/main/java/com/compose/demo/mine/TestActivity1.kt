package com.compose.demo.mine

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme
import com.compose.demo.ui.theme.TextGreen


/**
 * 测试Activity
 */
class TestActivity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    NativeAdCard()
                }
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
    var buttonText by remember { mutableStateOf("快点我") }
    var imgId by remember { mutableStateOf(R.drawable.ic_language) }

    Column(modifier = modifier) {
        Text(
            text = showName,
            modifier = Modifier.background(Color.LightGray),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black
        )

        Image(
            painter = painterResource(id = imgId),
            contentDescription = "avatar"
        )

        Button(onClick = {
            onActionClick()
            buttonText = "我真点了~"
            showName = "点击按钮导致名称发生改变"
            imgId = R.drawable.ic_launcher_background
        }) {
            Text(buttonText)
        }

        ElevatedButton({
            showName = "点击按钮 ElevatedButton"
        }) {
            Text(stringResource(id = R.string.app_name))
        }
        FilledTonalIconToggleButton(true, {}) {
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

@Preview
@Composable
fun DemoList() {
    val nums = (1..5).toList()
    val context = LocalContext.current
    LazyColumn {
        items(nums) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(16.dp)
                    .clickable {
                        Toast.makeText(context, "点击了第 $it 项", Toast.LENGTH_SHORT).show()
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "这是列表第 $it 项",
                    color = TextGreen,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Blue)
        }
    }
}

/**
 * 演示：等比例布局 (Proportional Layout)
 *
 * Compose 中没有 XML 的 layout_weight，等比例是通过 [Modifier.weight] 实现的：
 * - 在 Row 中，weight 按比例分配「剩余宽度」
 * - 在 Column 中，weight 按比例分配「剩余高度」
 * - weight 的数值代表相对比重，1f:2f:1f 即为 1:2:1
 * - fill = false 时只参与比例计算但不强制撑满分到的空间
 */
@Composable
fun ProportionalLayoutDemo(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        // 1. 横向等比：三列按 1:2:1 分配宽度
        Text(text = "横向 1 : 2 : 1", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            WeightBlock(weight = 1f, color = Color(0xFFEF5350), label = "1")
            WeightBlock(weight = 2f, color = Color(0xFF42A5F5), label = "2")
            WeightBlock(weight = 1f, color = Color(0xFF66BB6A), label = "1")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 等宽：每列 weight 相同即均分
        Text(text = "横向均分（三等分）", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            WeightBlock(weight = 1f, color = Color(0xFFFFA726), label = "1/3")
            WeightBlock(weight = 1f, color = Color(0xFFAB47BC), label = "1/3")
            WeightBlock(weight = 1f, color = Color(0xFF26C6DA), label = "1/3")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 固定 + 权重混合：左侧固定 80dp，右侧占满剩余空间
        Text(text = "固定 80dp + 剩余全占", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF8D6E63)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "80dp", color = Color.White)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF78909C)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "weight(1f)", color = Color.White)
            }
        }
    }
}

/**
 * 用于等比例演示的彩色块，必须放在 RowScope 中才能使用 weight。
 *
 * @param weight 该块占据的比重
 * @param color 背景色
 * @param label 块内显示的文案
 */
@Composable
private fun RowScope.WeightBlock(
    weight: Float,
    color: Color,
    label: String
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ProportionalLayoutDemoPreview() {
    ComposedemoTheme {
        ProportionalLayoutDemo()
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

@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // 广告媒体占位区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFFFB3B3)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.ad_media_label),
                    fontSize = 24.sp
                )
            }

            // 广告信息区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFFF0000))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.height(100.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.ad_title),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "这是中间的内容",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        Text(
                            text = stringResource(id = R.string.ad_content),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                ) {
                    Text(
                        text = stringResource(id = R.string.ad_button),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                var checked by remember { mutableStateOf(true) }
                Checkbox(checked, { checked = it })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NativeAdCardPreview() {
    ComposedemoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            NativeAdCard()
        }
    }
}
