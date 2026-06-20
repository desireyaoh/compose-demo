package com.compose.demo.demo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

data class Message(val author: String, val body: String)

object SampleData {
    val conversationSample = listOf(
        Message("小明", "Jetpack Compose 真的很好用！声明式 UI 写起来比 XML 省事太多了。"),
        Message("王芳", "是啊，特别是状态管理，用 remember 和 mutableStateOf 配合重组机制，逻辑非常清晰。"),
        Message("李雷", "我刚学完官方教程第 4 课，LazyColumn 替代 RecyclerView 确实简洁很多，而且动画也很容易加。"),
        Message("小明", "animateContentSize 和 animateColorAsState 这两个 API 太方便了，以前做这种展开动画要写好多代码。"),
        Message("韩梅梅", "对！修饰符（Modifier）的链式调用也很舒服，padding、clip、border 一行搞定。"),
        Message("王芳", "Material Design 3 的主题系统也很好，colorScheme、typography、shapes 分工明确，深色模式自动适配。"),
        Message("李雷", "我现在在学导航组件 Navigation Compose，感觉和 Compose 配合得很自然。"),
        Message("小明", "加油！等你整合好分享一下，我们一起仿微信做个完整的 App 练手。"),
        Message("韩梅梅", "这个项目就是在仿微信 UI 吧？底部四 Tab 导航已经做好了，接下来是各个页面的内容。"),
        Message("王芳", "是的，聊天列表、通讯录都要做，还有发现页和个人中心，工作量不小哦。"),
        Message("李雷", "一步一步来，先把 Compose 基础打扎实，复杂交互慢慢加进去。"),
        Message("小明", "对，不急。Compose 的学习曲线不算陡，主要是思维方式要从命令式转到声明式。"),
    )
}

private val conversationAvatarColors = listOf(
    Color(0xFF07C160),
    Color(0xFF576B95),
    Color(0xFFFA9D3B),
    Color(0xFFE64340),
    Color(0xFF4CABF6),
    Color(0xFF9B59B6),
)

@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        val avatarColor = conversationAvatarColors[abs(msg.author.hashCode()) % conversationAvatarColors.size]
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(avatarColor, CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = msg.author.take(1),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            targetValue = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            label = "surfaceColor"
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ConversationScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(SampleData.conversationSample) { message ->
            MessageCard(message)
        }
    }
}
