package com.compose.demo.mine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme
import com.compose.demo.ui.theme.Purple80
import com.compose.demo.ui.theme.WeatherIconBg
import com.compose.demo.ui.theme.WeatherIconTint
import com.compose.demo.ui.theme.WeatherLabelColor
import com.compose.demo.ui.theme.WeatherPageBg
import com.compose.demo.ui.theme.WeatherValueColor

/**
 * 天气详情卡片数据模型。
 *
 * subtitle 仅用于需要附加说明的条目（如风向 "ESE"），默认为空时不渲染。
 */
private data class WeatherItem(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val subtitle: String = ""
)

/**
 * 天气详情页面：2 列网格布局，每个卡片展示一项天气指标。
 *
 * 演示要点：
 * - LazyVerticalGrid 固定 2 列 + spacedBy 实现等间距网格
 * - 圆形图标容器（Box + CircleShape + background）
 * - 可选副标题（subtitle 非空时才渲染，避免空 Text 占位）
 */
class WeatherInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                Box(
                    modifier = Modifier.background(Purple80)
                        .fillMaxSize()
                        .safeContentPadding()
                ) {
                    WeatherInfoScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherInfoScreen() {
    val items = listOf(
        WeatherItem(
            icon = Icons.Filled.Thermostat,
            label = stringResource(R.string.weather_feels_like),
            value = stringResource(R.string.weather_value_feels_like)
        ),
        WeatherItem(
            icon = Icons.Filled.Air,
            label = stringResource(R.string.weather_wind_speed),
            value = stringResource(R.string.weather_value_wind_speed),
            subtitle = stringResource(R.string.weather_wind_direction)
        ),
        WeatherItem(
            icon = Icons.Filled.WaterDrop,
            label = stringResource(R.string.weather_humidity),
            value = stringResource(R.string.weather_value_humidity)
        ),
        WeatherItem(
            icon = Icons.Filled.Umbrella,
            label = stringResource(R.string.weather_precipitation),
            value = stringResource(R.string.weather_value_precipitation)
        ),
        WeatherItem(
            icon = Icons.Filled.WbSunny,
            label = stringResource(R.string.weather_sunrise),
            value = stringResource(R.string.weather_value_sunrise)
        ),
        WeatherItem(
            icon = Icons.Filled.WbTwilight,
            label = stringResource(R.string.weather_sunset),
            value = stringResource(R.string.weather_value_sunset)
        )
    )

    // spacedBy 使横竖间距均匀，无需在每张卡片内手动加 padding
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            WeatherInfoCard(item)
        }
    }
}

@Composable
private fun WeatherInfoCard(item: WeatherItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 圆形浅蓝色图标背景，与图标颜色形成对比
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(WeatherIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = WeatherIconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.label,
                    color = WeatherLabelColor,
                    fontSize = 13.sp
                )
                Text(
                    text = item.value,
                    color = WeatherValueColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                // 副标题仅在有内容时渲染（当前仅"风向"使用）
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        color = WeatherLabelColor,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
