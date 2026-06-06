package com.example.maia.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary

@Composable
fun HomeScreen(
    navController: NavController
) {
    val blobColor = MaiaBlob

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
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
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, h * 0.4f)
                        cubicTo(w * 0.15f, h * 0.1f, w * 0.35f, h * 0.55f, w * 0.55f, h * 0.3f)
                        cubicTo(w * 0.75f, h * 0.05f, w * 0.88f, h * 0.45f, w, h * 0.25f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(path, blobColor)
                }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // MAIA title
            Text(
                text = "MAIA",
                modifier = Modifier.padding(start = 28.dp),
                fontSize = 72.sp,
                fontFamily = FontFamily.Serif,
                color = MaiaText,
                letterSpacing = 6.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(Modifier.height(48.dp))

            // Editorial tagline
            Text(
                text = "NEW\nCOLLECTION",
                modifier = Modifier.padding(start = 28.dp),
                fontSize = 13.sp,
                letterSpacing = 4.sp,
                color = MaiaText,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "SS 2025",
                modifier = Modifier.padding(start = 28.dp),
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                color = MaiaTextSecondary
            )

            Spacer(Modifier.height(40.dp))

            TextButton(
                onClick = { navController.navigate(Screen.Shop.route) },
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    "SHOP NOW",
                    fontSize = 11.sp,
                    letterSpacing = 3.sp,
                    color = MaiaText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(140.dp))
        }
    }
}
