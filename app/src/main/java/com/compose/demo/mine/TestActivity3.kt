package com.compose.demo.mine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme
import com.compose.demo.ui.theme.DemoBlue
import com.compose.demo.ui.theme.DemoCyan
import com.compose.demo.ui.theme.DemoContainerGray
import com.compose.demo.ui.theme.DemoGreen
import com.compose.demo.ui.theme.DemoIndigo
import com.compose.demo.ui.theme.DemoLightBlue
import com.compose.demo.ui.theme.DemoOrange
import com.compose.demo.ui.theme.DemoPurple
import com.compose.demo.ui.theme.DemoRed

/**
 * 演示 Compose 对齐
 */
class TestActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                AlignmentConstraintScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlignmentConstraintScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_test3)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { SectionTitle("1. Box contentAlignment — 九宫格对齐") }
            item { BoxContentAlignmentDemo() }

            item { SectionTitle("2. Box Modifier.align — 子元素独立定位") }
            item { BoxChildAlignDemo() }

            item { SectionTitle("3. Column verticalArrangement — 垂直排列方式") }
            item { ColumnArrangementDemo() }

            item { SectionTitle("4. Row horizontalArrangement — 水平排列方式") }
            item { RowArrangementDemo() }

            item { SectionTitle("5. 尺寸约束 — fillMaxWidth / wrapContent / aspectRatio / offset") }
            item { SizeConstraintDemo() }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

// ─── Section 1: Box contentAlignment 九宫格 ───────────────────────────────

private val alignmentItems = listOf(
    Alignment.TopStart to "TopStart",
    Alignment.TopCenter to "TopCenter",
    Alignment.TopEnd to "TopEnd",
    Alignment.CenterStart to "CenterStart",
    Alignment.Center to "Center",
    Alignment.CenterEnd to "CenterEnd",
    Alignment.BottomStart to "BottomStart",
    Alignment.BottomCenter to "BottomCenter",
    Alignment.BottomEnd to "BottomEnd",
)

@Composable
fun BoxContentAlignmentDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        alignmentItems.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { (alignment, label) ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 外层蓝底 Box，contentAlignment 控制子元素在容器内的默认位置
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .background(DemoLightBlue, RoundedCornerShape(6.dp)),
                            contentAlignment = alignment
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(DemoRed, RoundedCornerShape(4.dp))
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ─── Section 2: Box Modifier.align — 每个子元素独立指定对齐 ───────────────

@Composable
fun BoxChildAlignDemo() {
    // 与 contentAlignment 不同，Modifier.align 让每个子元素独立选择对齐点
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(DemoLightBlue, RoundedCornerShape(8.dp))
    ) {
        LabelChip("TopStart", DemoRed, Modifier.align(Alignment.TopStart))
        LabelChip("TopCenter", DemoBlue, Modifier.align(Alignment.TopCenter))
        LabelChip("TopEnd", DemoGreen, Modifier.align(Alignment.TopEnd))
        LabelChip("Center", DemoOrange, Modifier.align(Alignment.Center))
        LabelChip("BottomStart", DemoPurple, Modifier.align(Alignment.BottomStart))
        LabelChip("BottomCenter", DemoCyan, Modifier.align(Alignment.BottomCenter))
        LabelChip("BottomEnd", DemoIndigo, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun LabelChip(text: String, bgColor: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        color = Color.White,
        fontSize = 10.sp
    )
}

// ─── Section 3: Column verticalArrangement ────────────────────────────────

private val columnArrangements = listOf(
    Arrangement.Top to "Top",
    Arrangement.Center to "Center",
    Arrangement.Bottom to "Bottom",
    Arrangement.SpaceBetween to "SpaceBetween",
    Arrangement.SpaceAround to "SpaceAround",
    Arrangement.SpaceEvenly to "SpaceEvenly",
)

private val demoColors = listOf(DemoRed, DemoBlue, DemoGreen)

@Composable
fun ColumnArrangementDemo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        columnArrangements.forEach { (arrangement, label) ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 固定高度容器，内部三个色块演示不同排列
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(DemoLightBlue, RoundedCornerShape(6.dp))
                        .padding(4.dp),
                    verticalArrangement = arrangement,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    demoColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(color, RoundedCornerShape(2.dp))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── Section 4: Row horizontalArrangement ────────────────────────────────

private val rowArrangements = listOf(
    Arrangement.Start to "Start",
    Arrangement.Center to "Center",
    Arrangement.End to "End",
    Arrangement.SpaceBetween to "SpaceBetween",
    Arrangement.SpaceAround to "SpaceAround",
    Arrangement.SpaceEvenly to "SpaceEvenly",
)

@Composable
fun RowArrangementDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rowArrangements.forEach { (arrangement, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(DemoLightBlue, RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp),
                horizontalArrangement = arrangement,
                verticalAlignment = Alignment.CenterVertically
            ) {
                demoColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
            }
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Section 5: 尺寸约束 ─────────────────────────────────────────────────

@Composable
fun SizeConstraintDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // fillMaxWidth：撑满父容器宽度
        ConstraintLabel("fillMaxWidth — 撑满父宽")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(DemoRed, RoundedCornerShape(6.dp))
        )

        // wrapContentWidth：按内容自适应宽度
        ConstraintLabel("wrapContentWidth — 按内容撑开")
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .height(36.dp)
                .background(DemoBlue, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("内容撑开宽度", color = Color.White, fontSize = 12.sp)
        }

        // size：固定宽高
        ConstraintLabel("size(width=150.dp, height=48.dp) — 固定宽高")
        Box(
            modifier = Modifier
                .size(width = 150.dp, height = 48.dp)
                .background(DemoGreen, RoundedCornerShape(6.dp))
        )

        // aspectRatio：等比缩放，宽度填满后高度按比例自动计算
        ConstraintLabel("fillMaxWidth + aspectRatio(16f/9f) — 16:9 等比")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(DemoOrange, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("16 : 9", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // padding vs offset 对比
        ConstraintLabel("padding vs offset — 两者对父容器尺寸影响不同")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // padding：向内挤压，影响内容区域，撑大容器时也撑大整体尺寸
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(DemoContainerGray, RoundedCornerShape(6.dp))
                        .padding(16.dp)   // padding 在背景之后：内容区从 16dp 内边距开始
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(DemoPurple, RoundedCornerShape(4.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("padding(16.dp)", fontSize = 10.sp, textAlign = TextAlign.Center)
                Text("容器随内边距撑大", fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center)
            }

            // offset：平移子元素，不影响父容器尺寸，可能溢出父边界
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(72.dp)
                        .background(DemoContainerGray, RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = 16.dp, y = 16.dp)  // 平移，不撑大父容器
                            .background(DemoCyan, RoundedCornerShape(4.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("offset(16.dp, 16.dp)", fontSize = 10.sp, textAlign = TextAlign.Center)
                Text("容器尺寸不变", fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ConstraintLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
fun AlignmentConstraintScreenPreview() {
    ComposedemoTheme {
        AlignmentConstraintScreen()
    }
}

@Preview(showBackground = true, name = "1. Box contentAlignment 九宫格")
@Composable
fun BoxContentAlignmentDemoPreview() {
    ComposedemoTheme {
        BoxContentAlignmentDemo()
    }
}

@Preview(showBackground = true, name = "2. Box Modifier.align 独立定位")
@Composable
fun BoxChildAlignDemoPreview() {
    ComposedemoTheme {
        BoxChildAlignDemo()
    }
}

@Preview(showBackground = true, name = "3. Column verticalArrangement")
@Composable
fun ColumnArrangementDemoPreview() {
    ComposedemoTheme {
        ColumnArrangementDemo()
    }
}

@Preview(showBackground = true, name = "4. Row horizontalArrangement")
@Composable
fun RowArrangementDemoPreview() {
    ComposedemoTheme {
        RowArrangementDemo()
    }
}

@Preview(showBackground = true, name = "5. 尺寸约束")
@Composable
fun SizeConstraintDemoPreview() {
    ComposedemoTheme {
        SizeConstraintDemo()
    }
}
