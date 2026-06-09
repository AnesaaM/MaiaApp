package com.example.maia.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.data.TokenManager
import com.example.maia.ui.components.MaiaText
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
        tokenManager = TokenManager(context)
    )
}

@Composable
fun ForgotPasswordScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    val state = vm.forgotPasswordState.value

    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "MAIA",
                fontSize = 44.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                color = MaiaText,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "FORGOT PASSWORD",
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                color = MaiaTextSecondary,
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(32.dp))

            if (state is AuthState.Success) {
                Text(
                    "Reset instructions sent!\nCheck your email.",
                    fontSize = 13.sp,
                    color = MaiaText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "← Back to Login",
                    fontSize = 13.sp,
                    color = MaiaText,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "EMAIL",
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        color = MaiaTextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.height(4.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                "email@example.com",
                                fontSize = 13.sp,
                                color = Color(0xFFBBABA4)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color(0xFFCCC0BB),
                            focusedIndicatorColor = MaiaText,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedTextColor = MaiaText,
                            focusedTextColor = MaiaText
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                }

                if (state is AuthState.Error) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { vm.forgotPassword(email) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = state !is AuthState.Loading && email.isNotBlank(),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaiaText,
                        contentColor = Color.White
                    )
                ) {
                    if (state is AuthState.Loading)
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    else
                        Text(
                            "SEND RESET LINK",
                            letterSpacing = 2.sp,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "← Back to Login",
                    fontSize = 12.sp,
                    color = MaiaText,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
        }
    }
}
