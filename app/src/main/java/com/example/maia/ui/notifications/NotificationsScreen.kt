package com.example.maia.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.model.notification.NotificationItem
import com.example.maia.network.RetrofitInstance
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaBorder
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    var items = mutableStateOf<List<NotificationItem>>(emptyList())
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun load() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                items.value = RetrofitInstance.notificationApi.getMyNotifications()
            } catch (ex: Exception) {
                items.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun markRead(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitInstance.notificationApi.markAsRead(id)
                items.value = items.value.map { if (it.id == id) it.copy(isRead = true) else it }
            } catch (ex: Exception) {}
        }
    }
}

@Preview(showBackground = true, name = "Notifications Screen")
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(navController = rememberNavController())
}

@Composable
fun NotificationsScreen(navController: NavController) {
    val vm: NotificationsViewModel = viewModel()
    LaunchedEffect(Unit) { vm.load() }

    val notifications = vm.items.value
    val isLoading = vm.isLoading.value
    val blobColor = MaiaBlob

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Blob header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .drawBehind {
                    val w = size.width; val h = size.height
                    val path = Path().apply {
                        moveTo(0f, 0f); lineTo(w, 0f)
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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaiaText)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "MAIA",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaiaText,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "NOTIFICATIONS",
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Medium,
            color = MaiaTextSecondary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
            }
            notifications.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No notifications",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = MaiaText,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "You're all caught up",
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = MaiaTextSecondary
                    )
                }
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationRow(notif = notif, onRead = { vm.markRead(notif.id) })
                    HorizontalDivider(color = MaiaBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notif: NotificationItem, onRead: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (notif.isRead) Color.Transparent else Color(0xFFFAF3F0))
            .clickable(enabled = !notif.isRead) { onRead() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(5.dp)
                .background(
                    if (notif.isRead) Color.Transparent else MaiaText,
                    RoundedCornerShape(3.dp)
                )
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                notif.title,
                fontSize = 12.sp,
                fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.SemiBold,
                color = MaiaText,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(3.dp))
            Text(notif.message, fontSize = 11.sp, color = MaiaTextSecondary, lineHeight = 16.sp)
            if (notif.createdAt.isNotBlank()) {
                Spacer(Modifier.height(5.dp))
                Text(notif.createdAt.take(10), fontSize = 10.sp, color = Color(0xFFAA9990), letterSpacing = 0.5.sp)
            }
        }
    }
}
