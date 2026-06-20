package com.compose.demo.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

data class Contact(val name: String)

private val avatarColors = listOf(
    Color(0xFF07C160),
    Color(0xFF576B95),
    Color(0xFFFA9D3B),
    Color(0xFFE64340),
    Color(0xFF4CABF6),
    Color(0xFF9B59B6),
)

private val fakeContacts: List<Pair<String, List<Contact>>> = listOf(
    "A" to listOf(Contact("阿里"), Contact("安然")),
    "B" to listOf(Contact("白雪"), Contact("北辰")),
    "C" to listOf(Contact("陈晨"), Contact("崔磊")),
    "D" to listOf(Contact("邓超"), Contact("丁香")),
    "F" to listOf(Contact("方圆"), Contact("冯雪")),
    "G" to listOf(Contact("高明"), Contact("郭静")),
    "H" to listOf(Contact("韩梅梅"), Contact("黄磊")),
    "J" to listOf(Contact("江海"), Contact("金鑫")),
    "L" to listOf(Contact("李雷"), Contact("刘洋"), Contact("陆小凤")),
    "M" to listOf(Contact("马云"), Contact("明月")),
    "N" to listOf(Contact("牛顿"), Contact("宁静")),
    "Q" to listOf(Contact("钱学森")),
    "R" to listOf(Contact("任正非")),
    "S" to listOf(Contact("沈腾"), Contact("孙俪")),
    "T" to listOf(Contact("唐僧"), Contact("田亮")),
    "W" to listOf(Contact("王芳"), Contact("吴京")),
    "X" to listOf(Contact("小明"), Contact("谢霆锋")),
    "Y" to listOf(Contact("杨幂"), Contact("叶问")),
    "Z" to listOf(Contact("张伟"), Contact("赵云"), Contact("周杰伦")),
)

@Composable
fun ContactsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        fakeContacts.forEach { (letter, contacts) ->
            stickyHeader(key = "header_$letter") {
                Text(
                    text = letter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7F7F7))
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF888888),
                    fontWeight = FontWeight.Medium
                )
            }
            items(contacts, key = { "${letter}_${it.name}" }) { contact ->
                ContactItem(contact)
                HorizontalDivider(
                    modifier = Modifier.padding(start = 68.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFEEEEEE)
                )
            }
        }
    }
}

@Composable
private fun ContactItem(contact: Contact) {
    val color = avatarColors[abs(contact.name.hashCode()) % avatarColors.size]
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.take(1),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = contact.name,
            fontSize = 16.sp,
            color = Color(0xFF191919)
        )
    }
}
