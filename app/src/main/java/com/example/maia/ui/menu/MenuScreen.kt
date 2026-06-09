package com.example.maia.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary

private val womanCategories = listOf(
    "VIEW ALL", "TOPS", "DRESSES", "BOTTOMS",
    "OUTERWEAR", "SWIMWEAR", "MATCHING SETS", "FOOTWEAR", "ACCESSORIES"
)
private val manCategories = listOf(
    "VIEW ALL", "TOPS", "BOTTOMS", "SUITS & FORMALWEAR",
    "OUTERWEAR", "SWIMWEAR", "FOOTWEAR", "ACCESSORIES"
)
private val kidsCategories = listOf(
    "VIEW ALL", "BABY", "GIRLS", "BOYS", "SWIMWEAR", "FOOTWEAR", "ACCESSORIES", "SALE"
)

// categoryId = 0 → VIEW ALL (no filter)
private val womanCategoryIds = mapOf(
    "VIEW ALL" to 0, "TOPS" to 1, "DRESSES" to 2, "BOTTOMS" to 3,
    "OUTERWEAR" to 4, "SWIMWEAR" to 5, "MATCHING SETS" to 6,
    "FOOTWEAR" to 7, "ACCESSORIES" to 8
)
private val manCategoryIds = mapOf(
    "VIEW ALL" to 0, "TOPS" to 1, "BOTTOMS" to 2, "SUITS & FORMALWEAR" to 3,
    "OUTERWEAR" to 4, "SWIMWEAR" to 5, "FOOTWEAR" to 6, "ACCESSORIES" to 7
)
private val kidsCategoryIds = mapOf(
    "VIEW ALL" to 0, "BABY" to 1, "GIRLS" to 2, "BOYS" to 3,
    "SWIMWEAR" to 5, "FOOTWEAR" to 6, "ACCESSORIES" to 7, "SALE" to 8
)
// SALE: womanCategoryId=9, menCategoryId=8, kidsCategoryId=8
private val saleCategoryIds = listOf(9, 8, 8)

private val tabs = listOf("WOMAN", "MAN", "KIDS")

@Preview(showBackground = true, name = "Menu Screen")
@Composable
fun MenuScreenPreview() {
    MenuScreen(navController = rememberNavController())
}

@Composable
fun MenuScreen(navController: NavController, initialTab: Int = 0) {
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }
    val blobColor = MaiaBlob

    val categories = when (selectedTab) {
        0 -> womanCategories
        1 -> manCategories
        else -> kidsCategories
    }
    val categoryIdMap = when (selectedTab) {
        0 -> womanCategoryIds
        1 -> manCategoryIds
        else -> kidsCategoryIds
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Header blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
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
                    drawPath(path, blobColor)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hamburger icon
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaiaText,
                    modifier = Modifier
                        .size(22.dp)
                )

                Spacer(Modifier.width(16.dp))

                // Tabs: WOMAN MAN KIDS
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    tabs.forEachIndexed { index, label ->
                        val selected = selectedTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedTab = index }
                        ) {
                            Text(
                                text = label,
                                fontSize = if (selected) 22.sp else 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Light,
                                color = if (selected) MaiaText else MaiaTextSecondary,
                                letterSpacing = 1.sp
                            )
                            if (selected) {
                                Spacer(Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(MaiaText)
                                )
                            }
                        }
                    }
                }

                // MAIA italic
                Text(
                    text = "MAIA",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaiaText,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Collection + category list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "COLLECTION",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary,
                modifier = Modifier.width(90.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                categories.forEach { category ->
                    val catId = categoryIdMap[category] ?: 0
                    Text(
                        text = category,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp,
                        color = MaiaText,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.clickable {
                            navController.navigate("shop/$selectedTab/$catId")
                        }
                    )
                }
                // SALE in green
                Text(
                    text = "SALE",
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        navController.navigate("shop/$selectedTab/${saleCategoryIds[selectedTab]}")
                    }
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column {
                if (searchQuery.isEmpty()) {
                    Text(
                        "WHAT ARE YOU LOOKING FOR?",
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        color = Color(0xFFBBAA9F)
                    )
                } else {
                    Text(
                        searchQuery,
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        color = MaiaText
                    )
                }
                Spacer(Modifier.height(6.dp))
                HorizontalDivider(color = Color(0xFFD0BDB5), thickness = 0.8.dp)
            }
            // Invisible click area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clickable { navController.navigate("search") }
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}
