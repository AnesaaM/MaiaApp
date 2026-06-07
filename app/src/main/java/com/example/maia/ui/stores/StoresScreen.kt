package com.example.maia.ui.stores

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaBorder
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary

private data class StoreInfo(
    val city: String,
    val address: String,
    val postalCity: String,
    val weekdayHours: String,
    val sundayHours: String,
    val phone: String
)

private val stores = listOf(
    StoreInfo("PRISHTINË", "Bulevardi Nënë Tereza 15", "10000 Prishtinë, Kosovë",
        "Hën–Sht 09:00–20:00", "Die 10:00–18:00", "+383 38 200 300"),
    StoreInfo("PRIZREN", "Sheshi Shadervan 4", "20000 Prizren, Kosovë",
        "Hën–Sht 09:00–20:00", "Die 10:00–18:00", "+383 29 230 100"),
    StoreInfo("GJAKOVË", "Rruga UÇK 22", "50000 Gjakovë, Kosovë",
        "Hën–Sht 09:00–20:00", "Die 10:00–17:00", "+383 390 320 200"),
    StoreInfo("GJILAN", "Bulevardi Bill Clinton 8", "60000 Gjilan, Kosovë",
        "Hën–Sht 09:00–20:00", "Die 10:00–17:00", "+383 280 320 400")
)

@Composable
fun StoresScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w, 0f)
                        lineTo(w, h * 0.68f)
                        cubicTo(w * 0.82f, h * 1.05f, w * 0.60f, h * 0.72f, w * 0.44f, h * 0.90f)
                        cubicTo(w * 0.28f, h * 1.08f, w * 0.12f, h * 0.78f, 0f, h * 0.85f)
                        close()
                    }
                    drawPath(path, MaiaBlob)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← BACK",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = MaiaText,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "MAIA",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaiaText,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "STORES",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = MaiaText,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "OUR STORES",
            fontSize = 34.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Light,
            color = MaiaText,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Vizitoni njërin nga dyqanet tona në Kosovë.",
            fontSize = 13.sp,
            color = MaiaTextSecondary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(28.dp))

        HorizontalDivider(
            color = MaiaBorder,
            thickness = 0.8.dp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(horizontal = 24.dp)
        ) {
            StoreCard(store = stores[0], modifier = Modifier.weight(1f))
            VerticalDivider(color = MaiaBorder, thickness = 0.8.dp)
            StoreCard(store = stores[1], modifier = Modifier.weight(1f))
        }

        HorizontalDivider(
            color = MaiaBorder,
            thickness = 0.8.dp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(horizontal = 24.dp)
        ) {
            StoreCard(store = stores[2], modifier = Modifier.weight(1f))
            VerticalDivider(color = MaiaBorder, thickness = 0.8.dp)
            StoreCard(store = stores[3], modifier = Modifier.weight(1f))
        }

        HorizontalDivider(
            color = MaiaBorder,
            thickness = 0.8.dp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StoreCard(store: StoreInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = store.city,
            fontSize = 17.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Light,
            color = MaiaText,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(6.dp))
        HorizontalDivider(
            color = MaiaBlob,
            thickness = 1.5.dp,
            modifier = Modifier.width(28.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = store.address,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaiaText,
            lineHeight = 15.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = store.postalCity,
            fontSize = 11.sp,
            color = MaiaTextSecondary,
            lineHeight = 15.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = store.weekdayHours,
            fontSize = 10.sp,
            color = MaiaTextSecondary,
            lineHeight = 14.sp
        )
        Text(
            text = store.sundayHours,
            fontSize = 10.sp,
            color = MaiaTextSecondary,
            lineHeight = 14.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = store.phone,
            fontSize = 11.sp,
            color = MaiaTextSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
