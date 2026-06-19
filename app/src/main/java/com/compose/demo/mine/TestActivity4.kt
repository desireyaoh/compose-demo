package com.compose.demo.mine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.demo.R
import com.compose.demo.ui.theme.ComposedemoTheme

/**
 * 演示 Compose 常见动画 API
 */
class TestActivity4 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposedemoTheme {
                AnimationDemoScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationDemoScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_test4)) },
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
            item { AnimatedVisibilityDemo() }
            item { AnimateFloatDemo() }
            item { AnimateDpDemo() }
            item { AnimatedContentDemo() }
            item { InfiniteTransitionDemo() }
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

@Composable
private fun AnimatedVisibilityDemo() {
    var visible by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.anim_s1_title))

        Button(onClick = { visible = !visible }) {
            Text(
                if (visible) stringResource(R.string.anim_s1_hide)
                else stringResource(R.string.anim_s1_show)
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -it / 2 },
            exit = fadeOut(tween(400)) + slideOutVertically(tween(400)) { -it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.anim_s1_title),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun AnimateFloatDemo() {
    var opaque by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (opaque) 1f else 0.1f,
        animationSpec = tween(durationMillis = 600),
        label = "alpha"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.anim_s2_title))

        Button(onClick = { opaque = !opaque }) {
            Text(stringResource(R.string.anim_s2_toggle))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .graphicsLayer { this.alpha = alpha }
                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(stringResource(R.string.anim_s2_alpha_fmt), alpha),
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AnimateDpDemo() {
    var expanded by remember { mutableStateOf(false) }
    val width by animateDpAsState(
        targetValue = if (expanded) 280.dp else 80.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "width"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.anim_s3_title))

        Button(onClick = { expanded = !expanded }) {
            Text(
                if (expanded) stringResource(R.string.anim_s3_collapse)
                else stringResource(R.string.anim_s3_expand)
            )
        }

        Box(
            modifier = Modifier
                .width(width)
                .height(60.dp)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(stringResource(R.string.anim_s3_width_fmt), width.value.toInt()),
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AnimatedContentDemo() {
    var index by remember { mutableIntStateOf(0) }
    val texts = listOf(
        stringResource(R.string.anim_s4_text_0),
        stringResource(R.string.anim_s4_text_1),
        stringResource(R.string.anim_s4_text_2)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.anim_s4_title))

        Button(onClick = { index = (index + 1) % texts.size }) {
            Text(stringResource(R.string.anim_s4_next))
        }

        AnimatedContent(
            targetState = index,
            transitionSpec = {
                slideInHorizontally(tween(400)) { it } togetherWith
                        slideOutHorizontally(tween(400)) { -it }
            },
            label = "content"
        ) { targetIndex ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = texts[targetIndex],
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfiniteTransitionDemo() {
    var running by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(stringResource(R.string.anim_s5_title))

        Button(onClick = { running = !running }) {
            Text(
                if (running) stringResource(R.string.anim_s5_stop)
                else stringResource(R.string.anim_s5_start)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (running) {
            val infiniteTransition = rememberInfiniteTransition(label = "infinite")
            val animatedColor by infiniteTransition.animateColor(
                initialValue = MaterialTheme.colorScheme.primary,
                targetValue = MaterialTheme.colorScheme.secondary,
                animationSpec = infiniteRepeatable(tween(1000)),
                label = "color"
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(animatedColor, CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimationDemoScreenPreview() {
    ComposedemoTheme {
        AnimationDemoScreen()
    }
}
