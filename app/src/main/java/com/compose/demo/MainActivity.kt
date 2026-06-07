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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.compose.demo.ui.theme.ComposedemoTheme

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
        "跳转到 DemoActivity1",
        "跳转到 SettingsActivity",
        "功能示例 3"
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(items) { index, item ->
            ListItem(
                headlineContent = { Text(text = item) },
                modifier = Modifier.clickable {
                    when (index) {
                        0 -> context.startActivity(Intent(context, DemoActivity1::class.java))
                        1 -> context.startActivity(Intent(context, SettingsActivity::class.java))
                    }
                }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}
