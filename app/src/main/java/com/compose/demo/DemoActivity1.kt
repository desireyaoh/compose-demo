package com.compose.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compose.demo.ui.theme.ComposedemoTheme

// 定义布局模式枚举
enum class LayoutMode {
    LIST,       // 普通列表
    GRID,       // 网格布局
    STAGGERED   // 瀑布流布局
}

class DemoActivity1 : ComponentActivity() {
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
                    LayoutSwitchDemo()
                }
            }
        }
    }
}

@Composable
fun LayoutSwitchDemo() {
    // 1. 状态管理
    val itemsList = remember {
        mutableStateListOf<String>().apply {
            addAll(List(50) { "条目 $it" })
        }
    }
    
    // 当前布局模式状态
    var currentMode by remember { mutableStateOf(LayoutMode.LIST) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 2. 切换按钮
        Button(
            onClick = {
                // 在三种模式间循环切换
                currentMode = when (currentMode) {
                    LayoutMode.LIST -> LayoutMode.GRID
                    LayoutMode.GRID -> LayoutMode.STAGGERED
                    LayoutMode.STAGGERED -> LayoutMode.LIST
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val btnText = when (currentMode) {
                LayoutMode.LIST -> "当前：列表 (点击切换网格)"
                LayoutMode.GRID -> "当前：网格 (点击切换瀑布流)"
                LayoutMode.STAGGERED -> "当前：瀑布流 (点击切换列表)"
            }
            Text(btnText)
        }

        // 3. 根据状态显示不同的布局组件
        when (currentMode) {
            LayoutMode.LIST -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(itemsList) { index, itemText ->
                        ListItemRow(text = itemText, height = 80.dp) {
                            itemsList[index] = "$itemText (已点)"
                        }
                    }
                }
            }
            LayoutMode.GRID -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 固定两列
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(itemsList) { index, itemText ->
                        ListItemRow(text = itemText, height = 100.dp) {
                            itemsList[index] = "$itemText (已点)"
                        }
                    }
                }
            }
            LayoutMode.STAGGERED -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2), // 固定两列
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(itemsList) { index, itemText ->
                        // 瀑布流通过随机高度体现效果
                        val randomHeight = remember(index) { (100..250).random().dp }
                        ListItemRow(text = itemText, height = randomHeight) {
                            itemsList[index] = "$itemText (已点)"
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListItemRow(text: String, height: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(height) // 设置动态高度以适配不同布局
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
