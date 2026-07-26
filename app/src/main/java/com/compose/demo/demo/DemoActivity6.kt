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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.ui.theme.ComposedemoTheme
import kotlinx.coroutines.launch

/**
 * Compose Pager 组件演示页面。
 *
 * 集中展示 Compose Foundation Pager 的三种典型用法：
 * 1. HorizontalPager 结合 TabRow 实现 Tab + 页面联动切换；
 * 2. HorizontalPager 结合自定义圆点指示器；
 * 3. VerticalPager 实现类似抖音的垂直翻页效果。
 */
class DemoActivity6 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().safeContentPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PagerDemoScreen()
                }
            }
        }
    }
}

/**
 * Pager 演示主界面，纵向排列展示三种 Pager 用法示例。
 *
 * 包含：Tab 联动的水平翻页、带圆点指示器的水平翻页、垂直翻页三个示例模块。
 */
@Composable
fun PagerDemoScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Compose Pager 示例",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        // 1. 结合 TabRow 的 HorizontalPager (水平翻页)
        PagerSectionHeader("1. HorizontalPager + TabRow")
        HorizontalPagerWithTabs()

        // 2. 带有指示器的 HorizontalPager
        PagerSectionHeader("2. 带有指示器的 Pager")
        PagerWithIndicator()

        // 3. VerticalPager (垂直翻页 - 类似抖音)
        PagerSectionHeader("3. VerticalPager (垂直翻页)")
        VerticalPagerExample()
    }
}

/**
 * HorizontalPager 结合 TabRow 示例：点击 Tab 或左右滑动页面，两者状态双向联动。
 *
 * 通过同一个 pagerState 同步 TabRow 选中项与 Pager 当前页，
 * 点击 Tab 时在协程中调用 animateScrollToPage 平滑滚动到对应页面。
 */
@Composable
fun HorizontalPagerWithTabs() {
    val titles = listOf("主页", "消息", "设置")
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        // 点击 Tab 切换页面
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "这是 ${titles[page]} 页面", fontSize = 20.sp)
            }
        }
    }
}

/**
 * HorizontalPager 结合自定义圆点指示器示例。
 *
 * Pager 下方绘制一排圆点，根据当前页切换圆点颜色（当前页深色、其余浅色）。
 */
@Composable
fun PagerWithIndicator() {
    val pageCount = 5
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) { page ->
            CardContent(index = page, color = Color(0xFFBBDEFB))
        }

        // 自定义页面指示器
        Row(
            Modifier
                .height(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

/**
 * VerticalPager 示例：上下垂直翻页，类似短视频应用的滑动切换效果。
 */
@Composable
fun VerticalPagerExample() {
    val pagerState = rememberPagerState(pageCount = { 3 })

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFE1BEE7))
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "垂直翻页 - 页面 $page", fontSize = 18.sp)
        }
    }
}

/**
 * Pager 页面内的卡片内容组件，居中显示卡片序号文本。
 * @param index 当前页索引，用于展示卡片编号
 * @param color 卡片背景色
 */
@Composable
private fun CardContent(index: Int, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "卡片 $index", fontSize = 24.sp)
    }
}

/**
 * 示例模块的分区标题组件，用于标识各 Pager 示例的序号和名称。
 * @param title 标题文本
 */
@Composable
private fun PagerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}
