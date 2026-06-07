package com.example.maia.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.data.TokenManager
import com.example.maia.ui.components.BlobHeader
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextField
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.viewmodel.AuthState
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory

@Preview(showBackground = true, name = "Forgot Password Screen")
@Composable
fun ForgotPasswordScreenPreview() {
    val context = LocalContext.current
    ForgotPasswordScreen(
        navController = rememberNavController(),
        tokenManager = com.example.maia.data.TokenManager(context)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    val state = vm.forgotPasswordState.value

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        BlobHeader()

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(start = 0.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaiaText)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "FORGOT PASSWORD",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Enter your email address and we'll send you instructions to reset your password.",
                fontSize = 13.sp,
                color = MaiaTextSecondary,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            if (state is AuthState.Success) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EDE8)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Reset instructions sent! Check your email.",
                        modifier = Modifier.padding(16.dp),
                        color = MaiaText,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
                TextButton(
                    onClick = { navController.popBackStack() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Back to Login", color = MaiaText, fontSize = 13.sp)
                }
            } else {
                MaiaTextField(
                    label = "EMAIL",
                    value = email,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )

                Spacer(Modifier.height(8.dp))

                if (state is AuthState.Error) {
                    Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { vm.forgotPassword(email) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = state !is AuthState.Loading && email.isNotBlank(),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaiaButton)
                ) {
                    if (state is AuthState.Loading)
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else
                        Text("SEND RESET LINK", letterSpacing = 1.5.sp, fontSize = 12.sp)
                }
            }
        }
    }
}
