package com.compose.demo.mine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.compose.demo.ui.theme.ComposedemoTheme
import kotlinx.coroutines.launch

class TestActivity0 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                MyScaffoldScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffoldScreen() {
    // 1. 初始化 SnackbarState 和协程作用域（用于显示 Snackbar）
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var clickCount by remember { mutableIntStateOf(0) }

    Scaffold(
        // 2. 绑定 SnackbarHost
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        
        // 3. 顶部应用栏
        topBar = {
            TopAppBar(
                title = { Text("Scaffold 示例") },
                navigationIcon = {
                    IconButton(onClick = { /* 打开抽屉等 */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },

        // 4. 底部导航栏
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* 导航到主页 */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("首页") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* 导航到收藏 */ },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("收藏") }
                )
            }
        },

        // 5. 悬浮按钮 (FloatingActionButton)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    clickCount++
                    // 触发 Snackbar 弹窗
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "点击了 FAB 按钮！当前次数: $clickCount",
                            actionLabel = "确定",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        
        // 6. FAB 放置位置（默认在右下角，也可以放在中间）
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        // 7. 页面主体内容：必须应用 innerPadding 避免内容被遮挡
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // 关键步骤！
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "这是页面的主体内容，已经被 innerPadding 自动修正了位置。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyScaffoldScreenPreview() {
    ComposedemoTheme {
        MyScaffoldScreen()
    }
}