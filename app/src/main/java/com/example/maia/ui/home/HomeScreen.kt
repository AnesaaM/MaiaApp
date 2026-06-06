package com.example.maia.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaText

@Preview(showBackground = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}

@Composable
fun HomeScreen(navController: NavController) {
    val blobColor = MaiaBlob

    // Marquee animation
    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetX"
    )

    val marqueeText = "MAIA  ·  MAIA  ·  MAIA  ·  MAIA  ·  MAIA  ·  MAIA  ·  "

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val tx = offsetX * screenWidthPx

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Top blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.TopCenter)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w, 0f)
                        lineTo(w, h * 0.62f)
                        cubicTo(w * 0.80f, h * 1.0f, w * 0.58f, h * 0.68f, w * 0.42f, h * 0.86f)
                        cubicTo(w * 0.26f, h * 1.05f, w * 0.10f, h * 0.75f, 0f, h * 0.82f)
                        close()
                    }
                    drawPath(path, blobColor)
                }
        )

        // Bottom blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.BottomCenter)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, h * 0.35f)
                        cubicTo(w * 0.15f, h * 0.05f, w * 0.35f, h * 0.5f, w * 0.55f, h * 0.25f)
                        cubicTo(w * 0.75f, h * 0.0f, w * 0.88f, h * 0.4f, w, h * 0.2f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(path, blobColor)
                }
        )

        // MAIA marquee
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .clipToBounds()
        ) {
            Text(
                text = marqueeText,
                modifier = Modifier
                    .graphicsLayer { translationX = tx }
                    .wrapContentWidth(unbounded = true),
                fontSize = 64.sp,
                fontFamily = FontFamily.Serif,
                color = MaiaText,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Light,
                softWrap = false
            )
        }
    }
}
