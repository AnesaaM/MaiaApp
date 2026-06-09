package com.example.maia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MaiaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    placeholder: String = label
) {
    Column {
        Text(
            label,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            color = if (isError) MaiaAccent else MaiaTextSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFFBBABA4), fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isError) MaiaAccent else Color(0xFFDDD0CA),
                focusedBorderColor = if (isError) MaiaAccent else MaiaText,
                errorBorderColor = MaiaAccent,
                unfocusedContainerColor = if (isError) Color(0xFFFFF5F5) else Color.White,
                focusedContainerColor = Color.White,
                errorContainerColor = Color(0xFFFFF5F5)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = MaiaText)
        )
        if (isError && errorMessage != null) {
            Text(
                errorMessage,
                fontSize = 11.sp,
                color = MaiaAccent,
                modifier = Modifier.padding(top = 3.dp, start = 2.dp)
            )
        }
    }
}
