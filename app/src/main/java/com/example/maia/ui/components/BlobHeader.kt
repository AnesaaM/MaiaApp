package com.example.maia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val MaiaBackground = Color(0xFFFBF8F5)
val MaiaBlob = Color(0xFFD4BAB0)
val MaiaText = Color(0xFF1C0A06)
val MaiaTextSecondary = Color(0xFF7A5C52)
val MaiaButton = Color(0xFF1C0A06)
val MaiaBorder = Color(0xFFD8C8C2)
val MaiaAccent = Color(0xFFB5381A)

@Preview(showBackground = true, name = "Blob Header")
@Composable
fun BlobHeaderPreview() {
    BlobHeader()
}

@Composable
fun BlobHeader(
    modifier: Modifier = Modifier,
    height: Dp = 220.dp,
    leading: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(MaiaBackground)
            .drawBehind { drawBlob(this) }
    ) {
        Text(
            text = "MAIA",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .statusBarsPadding()
                .padding(start = 28.dp, top = 16.dp),
            fontSize = 58.sp,
            fontFamily = FontFamily.Serif,
            color = MaiaText,
            letterSpacing = 6.sp
        )
        if (leading != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 8.dp, top = 4.dp)
            ) {
                leading()
            }
        }
        if (actions != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 8.dp, top = 4.dp)
            ) {
                actions()
            }
        }
    }
}

private fun drawBlob(scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height
    val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(w, 0f)
        lineTo(w, h * 0.62f)
        cubicTo(w * 0.80f, h * 1.0f, w * 0.58f, h * 0.68f, w * 0.42f, h * 0.86f)
        cubicTo(w * 0.26f, h * 1.05f, w * 0.10f, h * 0.75f, 0f, h * 0.82f)
        close()
    }
    scope.drawPath(path, MaiaBlob)
}
