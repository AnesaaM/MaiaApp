package com.example.maia.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val sizes = listOf("XS", "S", "M", "L", "XL", "XXL")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizePickerSheet(
    productName: String,
    onDismiss: () -> Unit,
    onAddToCart: (size: String) -> Unit
) {
    var selectedSize by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaiaBackground,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "SELECT SIZE",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                productName,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp,
                color = MaiaText,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sizes) { size ->
                    val selected = selectedSize == size
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = if (selected) 1.5.dp else 0.8.dp,
                                color = if (selected) MaiaText else Color(0xFFD8C8C2),
                                shape = RoundedCornerShape(2.dp)
                            )
                            .clickable { selectedSize = size }
                    ) {
                        Text(
                            size,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) MaiaText else MaiaTextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    selectedSize?.let { onAddToCart(it) }
                },
                enabled = selectedSize != null,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaiaButton,
                    disabledContainerColor = Color(0xFFD8C8C2)
                )
            ) {
                Text(
                    if (selectedSize != null) "ADD TO BAG — $selectedSize" else "SELECT A SIZE",
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
