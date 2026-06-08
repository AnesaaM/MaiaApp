package com.example.maia.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val DashSidebar     = Color(0xFF1C0A06)
internal val DashSidebarSelected = Color(0xFF3A1208)
internal val DashSidebarText = Color.White
internal val DashSidebarSubText = Color(0xFF9A7A6A)
internal val DashCardBg      = Color.White
internal val DashBorder      = Color(0xFFE8E0D8)
internal val DashSecText     = Color(0xFF9A8E82)
internal val DashRedText     = Color(0xFFB04040)
internal val DashGreenBg     = Color(0xFFE6F4EE)
internal val DashGreenText   = Color(0xFF2A7A4A)
internal val DashBg          = Color(0xFFFBF8F5)

private val ToggleBg   = Color(0xFFF0EBE5)
private val ToggleText = Color(0xFF1C0A06)

data class DashNavItem(val icon: String, val label: String)

@Composable
fun DashboardLayout(
    roleTitle: String,
    navItems: List<DashNavItem>,
    selectedIndex: Int,
    onNavSelect: (Int) -> Unit,
    username: String,
    email: String,
    onLogout: () -> Unit,
    onHomePage: () -> Unit,
    content: @Composable () -> Unit
) {
    var sidebarOpen by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {

        // ── Full-screen content column ────────────────────────────────────────
        Column(Modifier.fillMaxSize().statusBarsPadding()) {

            // Top bar with hamburger button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(DashSidebar)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(ToggleBg, RoundedCornerShape(6.dp))
                        .clickable { sidebarOpen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("≡", color = ToggleText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    "MAIA",
                    color = DashSidebarText,
                    fontSize = 18.sp,
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    navItems.getOrNull(selectedIndex)?.label ?: "",
                    color = DashSidebarSubText,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            }

            // Main content — full width, full remaining height
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DashBg)
            ) {
                content()
            }
        }

        // ── Scrim (when sidebar is open) ──────────────────────────────────────
        if (sidebarOpen) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { sidebarOpen = false }
            )
        }

        // ── Sidebar overlay (when open) ───────────────────────────────────────
        if (sidebarOpen) {
            Column(
                Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .background(DashSidebar)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ToggleBg, RoundedCornerShape(6.dp))
                            .clickable { sidebarOpen = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("<", color = ToggleText, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text(
                            "MAIA",
                            color = DashSidebarText,
                            fontSize = 18.sp,
                            letterSpacing = 4.sp,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        )
                        Text(roleTitle, color = DashSidebarSubText, fontSize = 10.sp, letterSpacing = 2.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Nav items
                navItems.forEachIndexed { i, item ->
                    val selected = i == selectedIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (selected) DashSidebarSelected else Color.Transparent)
                            .clickable {
                                onNavSelect(i)
                                sidebarOpen = false
                            }
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            item.icon,
                            color = if (selected) DashSidebarText else DashSidebarSubText,
                            fontSize = 11.sp
                        )
                        Text(
                            item.label,
                            color = if (selected) DashSidebarText else DashSidebarSubText,
                            fontSize = 10.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Bottom: user info + buttons
                Column(
                    Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(username, color = DashSidebarText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(email, color = DashSidebarSubText, fontSize = 10.sp)
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DashSidebarText),
                        border = BorderStroke(1.dp, DashSidebarSubText)
                    ) { Text("LOG OUT", fontSize = 9.sp, letterSpacing = 2.sp) }
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onHomePage,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DashSidebarText),
                        border = BorderStroke(1.dp, DashSidebarSubText)
                    ) { Text("HOME PAGE", fontSize = 9.sp, letterSpacing = 2.sp) }
                }
            }
        }
    }
}
