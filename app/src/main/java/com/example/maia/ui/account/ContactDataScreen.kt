package com.example.maia.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.data.TokenManager
import com.example.maia.model.auth.UpdateMeRequest
import com.example.maia.network.RetrofitInstance
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaBorder
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import kotlinx.coroutines.launch

class ContactDataViewModel : ViewModel() {
    var isLoading = mutableStateOf(false); private set
    var error = mutableStateOf<String?>(null); private set
    var saved = mutableStateOf(false); private set

    fun update(firstName: String, lastName: String, email: String, tokenManager: TokenManager) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val response = RetrofitInstance.authApi.updateMe(UpdateMeRequest(firstName, lastName, email))
                tokenManager.saveUsername("${response.firstName} ${response.lastName}")
                tokenManager.saveEmail(response.email)
                saved.value = true
            } catch (e: Exception) {
                error.value = e.message ?: "Update failed"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun resetSaved() { saved.value = false }
}

@Preview(showBackground = true, name = "Contact Data Screen")
@Composable
fun ContactDataScreenPreview() {
    ContactDataScreen(navController = rememberNavController(), tokenManager = TokenManager(LocalContext.current))
}

@Composable
fun ContactDataScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: ContactDataViewModel = viewModel()

    val fullName = tokenManager.getUsername() ?: ""
    val spaceIdx = fullName.indexOf(' ')
    val initialFirst = if (spaceIdx >= 0) fullName.substring(0, spaceIdx) else fullName
    val initialLast  = if (spaceIdx >= 0) fullName.substring(spaceIdx + 1) else ""

    var firstName by remember { mutableStateOf(initialFirst) }
    var lastName  by remember { mutableStateOf(initialLast) }
    var email     by remember { mutableStateOf(tokenManager.getEmail() ?: "") }

    val isLoading = vm.isLoading.value
    val error     = vm.error.value
    val saved     = vm.saved.value

    val blobColor = MaiaBlob

    LaunchedEffect(saved) {
        if (saved) {
            vm.resetSaved()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
            .verticalScroll(rememberScrollState())
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

        Spacer(Modifier.height(32.dp))

        Text(
            "CONTACT DATA",
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Medium,
            color = MaiaTextSecondary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ContactEditField(label = "FIRST NAME", value = firstName, onValueChange = { firstName = it })
            ContactEditField(label = "LAST NAME",  value = lastName,  onValueChange = { lastName  = it })
            ContactEditField(label = "EMAIL",       value = email,     onValueChange = { email     = it })

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            val formValid = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()

            Button(
                onClick = { if (formValid) vm.update(firstName, lastName, email, tokenManager) },
                enabled = formValid && !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaiaButton,
                    disabledContainerColor = Color(0xFFD8C8C2)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 1.5.dp, modifier = Modifier.size(18.dp))
                } else {
                    Text("SAVE CHANGES", letterSpacing = 2.sp, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ContactEditField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 10.sp, letterSpacing = 1.5.sp) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaiaBorder,
            focusedBorderColor = MaiaText,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedLabelColor = MaiaTextSecondary,
            focusedLabelColor = MaiaText
        )
    )
}
