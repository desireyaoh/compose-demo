package com.compose.demo.demo

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.demo.ui.theme.ComposedemoTheme

/**
 * 列表布局模式枚举，作为页面的核心状态机。
 *
 * 使用 enum 而非 Boolean/Int 标志位，可通过 when 穷举分支，
 * 编译器会强制处理所有状态，避免遗漏。
 */
enum class LayoutMode {
    LIST,       // 普通列表：LazyColumn，单列纵向滚动
    GRID,       // 网格布局：LazyVerticalGrid，固定列数等高排列
    STAGGERED   // 瀑布流布局：LazyVerticalStaggeredGrid，固定列数不等高排列
}

/**
 * 懒加载列表布局切换演示页面。
 *
 * 演示要点：
 * - 三种懒加载容器（LazyColumn / LazyVerticalGrid / LazyVerticalStaggeredGrid）的切换
 * - mutableStateListOf 管理可观察列表，列表元素变化时触发局部重组
 * - 点击条目后原地更新文字，验证状态驱动 UI 的响应
 */
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

/**
 * 布局切换演示的顶层 Composable。
 *
 * 状态设计说明：
 * - itemsList 使用 mutableStateListOf，对列表内单个元素的修改（索引赋值）
 *   也能触发重组；若用 mutableStateOf(listOf(...)) 则需整体替换才能触发重组。
 * - currentMode 使用 mutableStateOf(LayoutMode.LIST)，三种模式间循环切换。
 */
@Composable
fun LayoutSwitchDemo() {
    // mutableStateListOf 使列表可被 Compose 追踪，单个元素变化即可触发重组
    val itemsList = remember {
        mutableStateListOf<String>().apply {
            addAll(List(50) { "条目 $it" })
        }
    }

    var currentMode by remember { mutableStateOf(LayoutMode.LIST) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 切换按钮：在三种模式间顺序循环，按钮文字实时反映当前模式及下一目标
        Button(
            onClick = {
                currentMode = when (currentMode) {
                    LayoutMode.LIST      -> LayoutMode.GRID
                    LayoutMode.GRID      -> LayoutMode.STAGGERED
                    LayoutMode.STAGGERED -> LayoutMode.LIST
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val btnText = when (currentMode) {
                LayoutMode.LIST      -> "当前：列表 (点击切换网格)"
                LayoutMode.GRID      -> "当前：网格 (点击切换瀑布流)"
                LayoutMode.STAGGERED -> "当前：瀑布流 (点击切换列表)"
            }
            Text(btnText)
        }

        // 根据当前模式渲染对应的懒加载布局，切换模式时 Compose 会销毁旧布局并创建新布局
        when (currentMode) {
            LayoutMode.LIST -> {
                // LazyColumn：仅渲染可见区域的条目，性能优于 Column + verticalScroll
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    // itemsIndexed 提供 index，便于点击后精确更新对应位置的数据
                    itemsIndexed(itemsList) { index, itemText ->
                        ListItemRow(text = itemText, height = 80.dp) {
                            itemsList[index] = "$itemText (已点)"
                        }
                    }
                }
            }

            LayoutMode.GRID -> {
                // LazyVerticalGrid：固定两列，每个条目等高，适合图片墙等规整场景
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
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
                // LazyVerticalStaggeredGrid：固定两列，每列条目高度独立，形成瀑布流效果
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(itemsList) { index, itemText ->
                        // remember(index) 以 index 为 key：同一 index 复用已有随机值，
                        // 避免滚动时因重组导致高度跳变；不同 index 各自独立生成
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

/**
 * 通用列表条目卡片，适配三种布局的高度需求。
 *
 * @param text    显示的文字内容
 * @param height  卡片高度，由调用方传入以支持等高（列表/网格）和随机高（瀑布流）两种场景
 * @param onClick 点击回调，由调用方负责更新数据源
 */
@Composable
fun ListItemRow(text: String, height: Dp, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(height)
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

/** 整页预览：与 Activity 中一致，包裹主题和 Surface，默认展示列表模式 */
@Preview(showBackground = true, showSystemUi = true, name = "布局切换示例整页")
@Composable
private fun LayoutSwitchDemoPreview() {
    ComposedemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LayoutSwitchDemo()
        }
    }
}

/** 列表条目卡片单独预览 */
@Preview(showBackground = true, name = "列表条目卡片")
@Composable
private fun ListItemRowPreview() {
    ComposedemoTheme {
        ListItemRow(text = "条目 0", height = 80.dp) {}
    }
}
