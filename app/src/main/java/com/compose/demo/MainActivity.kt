package com.compose.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.compose.demo.ui.theme.ComposedemoTheme
import com.compose.demo.mine.TestActivity1
import com.compose.demo.mine.WeatherInfoActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                MainScreen()
            }
        }
    }
}

private data class TabItem(val title: String, val icon: ImageVector)

private val WeChatGreen = Color(0xFF07C160)


@Composable
fun MainScreen() {
    val tabs = listOf(
        TabItem("示例", Icons.AutoMirrored.Outlined.ListAlt),
        TabItem("聊天", Icons.Outlined.ChatBubbleOutline),
        TabItem("通讯录", Icons.Outlined.Contacts),
        TabItem("我", Icons.Outlined.Person)
    )
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(imageVector = tab.icon, contentDescription = tab.title)
                        },
                        label = { Text(text = tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = WeChatGreen,
                            selectedTextColor = WeChatGreen,
                            indicatorColor = Color.White,
                            unselectedIconColor = Color(0xFF333333),
                            unselectedTextColor = Color(0xFF333333)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                0 -> DemoListScreen()
                1 -> ConversationScreen()
                2 -> ContactsScreen()
                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tabs[selectedIndex].title)
                }
            }
        }
    }
}


@Composable
fun DemoListScreen() {
    val context = LocalContext.current
    val items = listOf(
        "跳转到 DemoActivity1 (列表切换)",
        "跳转到 DemoActivity2 (基础布局与组件)",
        "跳转到 DemoActivity3 (状态管理详解)",
        "跳转到 DemoActivity4 (ViewModel 结合)",
        "跳转到 DemoActivity5 (协程示例)",
        "跳转到 DemoActivity6 (Pager 示例)",
        "跳转到 TestActivity1",
        "跳转到 SettingsActivity",
        "跳转到 WeatherInfoActivity (天气详情)"
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(items) { index, item ->
            ListItem(
                headlineContent = { Text(text = item) },
                modifier = Modifier.clickable {
                    when (index) {
                        0 -> context.startActivity(Intent(context, DemoActivity1::class.java))
                        1 -> context.startActivity(Intent(context, DemoActivity2::class.java))
                        2 -> context.startActivity(Intent(context, DemoActivity3::class.java))
                        3 -> context.startActivity(Intent(context, DemoActivity4::class.java))
                        4 -> context.startActivity(Intent(context, DemoActivity5::class.java))
                        5 -> context.startActivity(Intent(context, DemoActivity6::class.java))
                        6 -> context.startActivity(Intent(context, TestActivity1::class.java))
                        7 -> context.startActivity(Intent(context, SettingsActivity::class.java))
                        8 -> context.startActivity(Intent(context, WeatherInfoActivity::class.java))
                    }
                }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}
