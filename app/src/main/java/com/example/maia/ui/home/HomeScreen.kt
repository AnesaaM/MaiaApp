package com.example.maia.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaText

@Composable
fun HomeScreen(navController: NavController) {
    val blobColor = MaiaBlob

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

        // MAIA text
        Text(
            text = "MAIA",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 16.dp, top = 40.dp),
            fontSize = 72.sp,
            fontFamily = FontFamily.Serif,
            color = MaiaText,
            letterSpacing = 6.sp,
            fontWeight = FontWeight.Light
        )
    }
}
