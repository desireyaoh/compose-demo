package com.compose.demo.mine

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme

// 1. compositionLocalOf：值变化时只重组读取该值的 Composable（精准重组）
val LocalUserLevel = compositionLocalOf { "普通用户" }

// 2. staticCompositionLocalOf：值变化时整个提供树全量重组，适合极少变化的配置
val LocalCardPadding = staticCompositionLocalOf<Dp> { 16.dp }

/**
 * 演示 CompositionLocal
 */
class TestActivity0 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                CompositionLocalDemo()
            }
        }
    }
}

@Composable
fun CompositionLocalDemo() {
    // 3. CompositionLocalProvider 向子树注入值，覆盖默认值
    CompositionLocalProvider(
        LocalUserLevel provides stringResource(R.string.cl_level_vip),
        LocalCardPadding provides 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.cl_screen_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // 消费外层 Provider 提供的值
            UserLevelCard()

            Text(
                text = stringResource(R.string.cl_nested_label),
                fontWeight = FontWeight.Medium
            )

            // 4. 嵌套 Provider：仅在此子树内覆盖 LocalUserLevel
            CompositionLocalProvider(LocalUserLevel provides stringResource(R.string.cl_level_admin)) {
                UserLevelCard()
            }

            // 5. 内置 CompositionLocal 演示
            BuiltInLocalsDemo()
        }
    }
}

@Composable
fun UserLevelCard() {
    // 读取最近 Provider 提供的值；若无 Provider 则使用 compositionLocalOf 的默认值
    val userLevel = LocalUserLevel.current
    val padding = LocalCardPadding.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = stringResource(R.string.cl_user_level_label, userLevel),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.cl_padding_label, padding.value.toInt()),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BuiltInLocalsDemo() {
    // LocalContext 是 Compose 内置的 CompositionLocal，提供当前 Android Context
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.cl_builtin_title),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.cl_context_label, context.javaClass.simpleName))
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                Toast.makeText(context, context.getString(R.string.cl_toast_msg), Toast.LENGTH_SHORT).show()
            }) {
                Text(stringResource(R.string.cl_toast_btn))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompositionLocalDemoPreview() {
    ComposedemoTheme {
        CompositionLocalDemo()
    }
}
