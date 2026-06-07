package com.compose.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.ui.theme.ComposedemoTheme
import com.compose.demo.ui.theme.SettingsBg
import com.compose.demo.ui.theme.SettingsCardBg
import com.compose.demo.ui.theme.SettingsDivider
import com.compose.demo.ui.theme.SettingsItemText
import com.compose.demo.ui.theme.SettingsTitleText

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                SettingsScreen(onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun SettingsScreen(onBackClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsBg)
    ) {
        // 顶部背景装饰图（覆盖页面上方约 1/3 区域）
        Image(
            painter = painterResource(R.drawable.bg_settings),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(276.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
        ) {
            SettingsTitleBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(20.dp))

            SettingsProfileCard()

            Spacer(modifier = Modifier.height(16.dp))

            // 第一组：语言设置、单位设置
            SettingsCardSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp)
            ) {
                SettingsItem(R.drawable.ic_language, stringResource(R.string.settings_language))
                SettingsItemDivider()
                SettingsItem(R.drawable.ic_unit, stringResource(R.string.settings_unit))
            }

            Spacer(modifier = Modifier.height(15.dp))

            // 第二组：意见反馈、隐私政策、使用协议、注销账号、关于应用
            SettingsCardSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp)
            ) {
                SettingsItem(R.drawable.ic_feedback, stringResource(R.string.settings_feedback))
                SettingsItemDivider()
                SettingsItem(R.drawable.ic_privacy, stringResource(R.string.settings_privacy))
                SettingsItemDivider()
                SettingsItem(R.drawable.ic_service, stringResource(R.string.settings_service))
                SettingsItemDivider()
                SettingsItem(R.drawable.ic_cancel_account, stringResource(R.string.settings_cancel_account))
                SettingsItemDivider()
                SettingsItem(R.drawable.ic_about, stringResource(R.string.settings_about))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsTitleBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.cd_back),
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onBackClick)
        )
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = SettingsTitleText
        )
        // 右侧占位，使标题视觉居中
        Spacer(modifier = Modifier.size(44.dp))
    }
}

@Composable
private fun SettingsProfileCard() {
    Image(
        painter = painterResource(R.drawable.profile_card),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp)
            .height(91.dp)
            .clip(RoundedCornerShape(13.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun SettingsCardSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(SettingsCardBg)
    ) {
        content()
    }
}

@Composable
private fun SettingsItem(
    iconRes: Int,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(53.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(13.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = SettingsItemText,
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(5.dp, 11.dp)
        )
    }
}

@Composable
private fun SettingsItemDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 59.dp)
            .height(0.5.dp)
            .background(SettingsDivider)
    )
}
