package com.compose.demo.mine

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.compose.demo.R

class TestActivity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(Modifier.safeDrawingPadding()) {
                val name by remember { mutableStateOf("freed") }
                Text(name, modifier = Modifier.safeDrawingPadding()
                    .background(Color.LightGray),
                    fontSize = 28.sp, fontWeight = FontWeight.Black)
                Image(
                    painter = painterResource(id = R.drawable.ic_language),
                    contentDescription = "avator" // 必须提供 contentDescription，即使为 null
                )

                var buttonText by remember { mutableStateOf("点我") }
                Button({
                    Log.d("TestActivity1", "onClick")
                    buttonText = "点了"
                }, modifier = Modifier.safeDrawingPadding()) { Text(buttonText) }
            }
        }
    }
}