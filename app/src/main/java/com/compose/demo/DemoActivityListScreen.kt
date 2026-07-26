package com.compose.demo

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.compose.demo.demo.DemoActivity1
import com.compose.demo.demo.DemoActivity2
import com.compose.demo.demo.DemoActivity3
import com.compose.demo.demo.DemoActivity4
import com.compose.demo.demo.DemoActivity5
import com.compose.demo.demo.DemoActivity6
import com.compose.demo.demo.DemoActivity7

/**
 * “示例” Tab 内嵌展示的跳转列表：demo 包下的 DemoActivity 系列 +
 * 一个跳转到 mine 包 TestActivity 系列列表页（TestActivityListActivity）的入口。
 */
@Composable
fun DemoActivityListScreen() {
    val context = LocalContext.current
    val items = listOf(
        stringResource(R.string.demo_list_item_1),
        stringResource(R.string.demo_list_item_2),
        stringResource(R.string.demo_list_item_3),
        stringResource(R.string.demo_list_item_4),
        stringResource(R.string.demo_list_item_5),
        stringResource(R.string.demo_list_item_6),
        stringResource(R.string.demo_list_item_7),
        stringResource(R.string.demo_list_item_8),
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
                        6 -> context.startActivity(Intent(context, DemoActivity7::class.java))
                        7 -> context.startActivity(Intent(context, TestActivityListActivity::class.java))
                    }
                }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}
