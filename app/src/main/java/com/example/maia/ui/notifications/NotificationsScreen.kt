package com.example.maia.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.maia.model.notification.NotificationItem
import com.example.maia.network.RetrofitInstance
import com.example.maia.ui.components.MaiaBackground
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
            } catch (_: Exception) {
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
            } catch (_: Exception) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val vm: NotificationsViewModel = viewModel()

    LaunchedEffect(Unit) { vm.load() }

    val notifications = vm.items.value
    val isLoading = vm.isLoading.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NOTIFICATIONS",
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaiaText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaiaText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaiaBackground)
            )
        },
        containerColor = MaiaBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaiaText,
                    strokeWidth = 1.5.dp
                )
                notifications.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFD4BAB0),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("No notifications", fontSize = 14.sp, color = MaiaTextSecondary, letterSpacing = 1.sp)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        NotificationRow(notif = notif, onRead = { vm.markRead(notif.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notif: NotificationItem, onRead: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.isRead) Color.White else Color(0xFFF5EDE9)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = { if (!notif.isRead) onRead() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(6.dp)
                    .background(
                        if (notif.isRead) Color.Transparent else MaiaText,
                        RoundedCornerShape(3.dp)
                    )
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    notif.title,
                    fontSize = 12.sp,
                    fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.SemiBold,
                    color = MaiaText,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(notif.message, fontSize = 11.sp, color = MaiaTextSecondary)
                if (notif.createdAt.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(notif.createdAt.take(10), fontSize = 10.sp, color = Color(0xFFAA9990))
                }
            }
        }
    }
}
