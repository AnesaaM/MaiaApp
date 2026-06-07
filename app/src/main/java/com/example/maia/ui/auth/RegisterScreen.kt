package com.example.maia.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.data.TokenManager
import com.example.maia.navigation.Screen
import com.example.maia.navigation.Screen.VerifyEmail
import com.example.maia.ui.components.BlobHeader
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextField
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.viewmodel.AuthState
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory

@Preview(showBackground = true, name = "Register Screen")
@Composable
fun RegisterScreenPreview() {
    val context = LocalContext.current
    RegisterScreen(
        navController = rememberNavController(),
        tokenManager = com.example.maia.data.TokenManager(context)
    )
}

@Composable
fun RegisterScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    val state = vm.registerState.value

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            navController.navigate(VerifyEmail.createRoute(email)) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        BlobHeader()

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text(
                "PERSONAL DETAILS",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(20.dp))

            MaiaTextField(label = "FIRST NAME", value = firstName, onValueChange = { firstName = it })
            Spacer(Modifier.height(14.dp))
            MaiaTextField(label = "LAST NAME", value = lastName, onValueChange = { lastName = it })
            Spacer(Modifier.height(14.dp))
            MaiaTextField(label = "EMAIL", value = email, onValueChange = { email = it }, keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(14.dp))
            MaiaTextField(
                label = "PASSWORD",
                value = password,
                onValueChange = { password = it },
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(14.dp))
            MaiaTextField(
                label = "CONFIRM PASSWORD",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(8.dp))

            val errorMsg = validationError ?: (state as? AuthState.Error)?.message
            if (errorMsg != null) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(vertical = 6.dp))
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    validationError = when {
                        firstName.isBlank() -> "First name is required"
                        lastName.isBlank() -> "Last name is required"
                        email.isBlank() -> "Email is required"
                        password.length < 6 -> "Password must be at least 6 characters"
                        password != confirmPassword -> "Passwords do not match"
                        else -> null
                    }
                    if (validationError == null) {
                        vm.register(firstName, lastName, email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = state !is AuthState.Loading,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaiaButton)
            ) {
                if (state is AuthState.Loading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else
                    Text("CREATE ACCOUNT", letterSpacing = 1.5.sp, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate(Screen.Login.route) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Already have an account? ", color = MaiaTextSecondary, fontSize = 12.sp)
                Text("Login", color = MaiaText, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
