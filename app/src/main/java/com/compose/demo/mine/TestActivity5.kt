package com.compose.demo.mine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme

class TestActivity5 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                StateHoistingScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateHoistingScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_test5)) },
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
            item { BasicHoistingDemo() }
            item { SiblingShareDemo() }
            item { ThemePickerScreen() }
            item { CartScreen() }
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

// ─── Section 1: 基础：单父→子传值 ───────────────────────────────────────────

@Composable
fun BasicHoistingDemo(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.sh_s1_title))
        Text(
            text = stringResource(R.string.sh_s1_parent_label, text),
            fontWeight = FontWeight.Bold
        )
        NameInputField(value = text, onValueChange = { text = it })
    }
}

@Composable
fun NameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.sh_s1_input_label)) },
        modifier = modifier.fillMaxWidth()
    )
}

// ─── Section 2: 进阶：兄弟共享（Slider ↔ 色块联动）────────────────────────

@Composable
fun SiblingShareDemo(modifier: Modifier = Modifier) {
    var progress by remember { mutableFloatStateOf(0f) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.sh_s2_title))
        Text(
            text = stringResource(R.string.sh_s2_hint),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ProgressSlider(value = progress, onValueChange = { progress = it })
        ColorPreviewBox(value = progress)
    }
}

@Composable
fun ProgressSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth())
}

@Composable
fun ColorPreviewBox(value: Float, modifier: Modifier = Modifier) {
    val color = lerp(Color.Red, Color.Green, value)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${(value * 100).toInt()}%",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// ─── Section 3: 高阶：三层嵌套（祖→父→孙）──────────────────────────────────

private val themeColors = listOf(
    Color(0xFFE53935),
    Color(0xFF1E88E5),
    Color(0xFF43A047),
    Color(0xFFFB8C00)
)

@Composable
fun ThemePickerScreen(modifier: Modifier = Modifier) {
    var selectedColor by remember { mutableStateOf(themeColors[0]) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.sh_s3_title))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(selectedColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.sh_s3_preview_label),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        ThemeSection(selectedColor = selectedColor, onColorChange = { selectedColor = it })
    }
}

@Composable
fun ThemeSection(selectedColor: Color, onColorChange: (Color) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.sh_s3_section_label),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ColorOptionRow(selectedColor = selectedColor, onColorChange = onColorChange)
    }
}

@Composable
fun ColorOptionRow(selectedColor: Color, onColorChange: (Color) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        themeColors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape)
                    .then(
                        if (isSelected) Modifier.border(3.dp, Color.Black, CircleShape)
                        else Modifier
                    )
                    .clickable { onColorChange(color) }
            )
        }
    }
}

// ─── Section 4: 实战：购物车（ViewModel）────────────────────────────────────

@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val items by viewModel.cartItems.collectAsState()
    val totalCount = items.sumOf { it.quantity }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(R.string.sh_s4_title))
        Text(
            text = stringResource(R.string.sh_s4_total_fmt, totalCount),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        HorizontalDivider()
        items.forEach { item ->
            CartItemRow(
                item = item,
                onIncrement = { viewModel.increment(item.id) },
                onDecrement = { viewModel.decrement(item.id) }
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp
        )
        IconButton(onClick = onDecrement, enabled = item.quantity > 0) {
            Icon(
                Icons.Default.Remove,
                contentDescription = stringResource(R.string.sh_s4_decrement)
            )
        }
        Text(
            text = item.quantity.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onIncrement) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(R.string.sh_s4_increment)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateHoistingScreenPreview() {
    ComposedemoTheme {
        StateHoistingScreen()
    }
}
