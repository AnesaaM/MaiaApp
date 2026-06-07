package com.example.maia.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.data.TokenManager
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.BlobHeader
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.viewmodel.AuthState
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.delay

private const val RESEND_COOLDOWN = 60

@Preview(showBackground = true, name = "Verification Screen")
@Composable
fun VerificationScreenPreview() {
    val context = LocalContext.current
    VerificationScreen(
        navController = rememberNavController(),
        email = "user@example.com",
        tokenManager = TokenManager(context)
    )
}

@Composable
fun VerificationScreen(
    navController: NavController,
    email: String,
    tokenManager: TokenManager
) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    val resendState = vm.resendState.value

    var countdown by remember { mutableIntStateOf(RESEND_COOLDOWN) }
    var resendSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
    }

    LaunchedEffect(resendState) {
        if (resendState is AuthState.Success) {
            resendSuccess = true
            countdown = RESEND_COOLDOWN
            while (countdown > 0) {
                delay(1000L)
                countdown--
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        BlobHeader()

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaiaBlob.copy(alpha = 0.25f), CircleShape)
                    .border(1.dp, MaiaBlob, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaiaText,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "CHECK YOUR EMAIL",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "We sent a verification link to",
                fontSize = 14.sp,
                color = MaiaTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Text(
                email,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaiaText,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Click the link in the email to verify your account before logging in.",
                fontSize = 12.sp,
                color = MaiaTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaiaButton)
            ) {
                Text("CONTINUE TO LOGIN", letterSpacing = 1.5.sp, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            if (resendSuccess) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EDE8)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Verification email resent successfully.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        color = MaiaText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            if (resendState is AuthState.Error) {
                Text(
                    resendState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
            }

            val canResend = countdown == 0 && resendState !is AuthState.Loading

            TextButton(
                onClick = { vm.resendVerification(email) },
                enabled = canResend,
                contentPadding = PaddingValues(0.dp)
            ) {
                if (resendState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color = MaiaText,
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 1.5.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sending...", color = MaiaTextSecondary, fontSize = 13.sp)
                } else if (countdown > 0) {
                    Text(
                        "Resend email in ${countdown}s",
                        color = MaiaTextSecondary,
                        fontSize = 13.sp
                    )
                } else {
                    Text("Resend verification email", color = MaiaText, fontSize = 13.sp)
                }
            }
        }
    }
}
