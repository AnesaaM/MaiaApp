package com.example.maia.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.maia.model.KidsCards

@Composable
fun KidsCardItem(card: KidsCards) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Column {

            AsyncImage(
                model = card.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Column(modifier = Modifier.padding(12.dp)) {

                Text(text = card.title)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "€ ${card.price}",
                    color = Color(0xFF6C5CE7)
                )
            }
        }
    }
}