package com.compose.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.compose.demo.mine.TestActivity0
import com.compose.demo.mine.TestActivity1
import com.compose.demo.mine.TestActivity2
import com.compose.demo.mine.TestActivity3
import com.compose.demo.mine.TestActivity4
import com.compose.demo.mine.TestActivity5
import com.compose.demo.mine.TestActivity6
import com.compose.demo.mine.WeatherInfoActivity
import com.compose.demo.ui.theme.ComposedemoTheme

/**
 * mine 包下的 TestActivity 系列跳转列表（含同属 mine 包的 WeatherInfoActivity，
 * 以及不属于 demo/mine 包、按最接近分类归入此处的 SettingsActivity）。
 */
class TestActivityListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                TestActivityListScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestActivityListScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val items = listOf(
        stringResource(R.string.test_list_item_1),
        stringResource(R.string.test_list_item_2),
        stringResource(R.string.test_list_item_3),
        stringResource(R.string.test_list_item_4),
        stringResource(R.string.test_list_item_5),
        stringResource(R.string.test_list_item_6),
        stringResource(R.string.test_list_item_7),
        stringResource(R.string.test_list_item_8),
        stringResource(R.string.test_list_item_9),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_test_activity_list)) },
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
        ) {
            itemsIndexed(items) { index, item ->
                ListItem(
                    headlineContent = { Text(text = item) },
                    modifier = Modifier.clickable {
                        when (index) {
                            0 -> context.startActivity(Intent(context, WeatherInfoActivity::class.java))
                            1 -> context.startActivity(Intent(context, TestActivity0::class.java))
                            2 -> context.startActivity(Intent(context, TestActivity1::class.java))
                            3 -> context.startActivity(Intent(context, TestActivity2::class.java))
                            4 -> context.startActivity(Intent(context, TestActivity3::class.java))
                            5 -> context.startActivity(Intent(context, TestActivity4::class.java))
                            6 -> context.startActivity(Intent(context, TestActivity5::class.java))
                            7 -> context.startActivity(Intent(context, TestActivity6::class.java))
                            8 -> context.startActivity(Intent(context, SettingsActivity::class.java))
                        }
                    }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}
