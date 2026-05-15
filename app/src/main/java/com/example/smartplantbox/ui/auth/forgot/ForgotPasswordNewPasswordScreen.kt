package com.example.smartplantbox.ui.auth.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.presentation.auth.ForgotPasswordViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme

@Composable
fun ForgotPasswordNewPasswordScreen(
    onBackClick: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    val viewModel: ForgotPasswordViewModel = viewModel()
    val passwordState by viewModel.newPasswordState.collectAsState()

    val createYourText = stringResource(R.string.create_your)
    val newPasswordTitleText = stringResource(R.string.new_password_title)
    val newPasswordLabelText = stringResource(R.string.new_password_label)
    val confirmPasswordLabelText = stringResource(R.string.confirm_password_label)
    val setPasswordButtonText = stringResource(R.string.set_password_button)
    val backButtonDesc = stringResource(R.string.back_button_new_password)
    val lockIconDesc = stringResource(R.string.lock_icon_desc)
    val hidePasswordDesc = stringResource(R.string.hide_password_desc)
    val showPasswordDesc = stringResource(R.string.show_password_desc)
    val minimum8CharsNoSpaces = stringResource(R.string.minimum_8_chars_no_spaces)
    val confirmYourPasswordHint = stringResource(R.string.confirm_your_password_hint)

    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    fun filterPassword(input: String): String {
        return input.filter { !it.isWhitespace() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backButtonDesc,
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() }
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = createYourText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = newPasswordTitleText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.forgot_password_leaf_center),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .size(400.dp)
        )

        if (passwordState.errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = passwordState.errorMessage!!,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }

        if (passwordState.successMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Text(
                    text = passwordState.successMessage!!,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }

        // New Password field
        OutlinedTextField(
            value = passwordState.newPassword,
            onValueChange = {
                val filtered = filterPassword(it)
                viewModel.updateNewPassword(filtered)
            },
            label = { Text(newPasswordLabelText, color = Color.Black) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = lockIconDesc,
                    tint = Color(0xFF4CAF50)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { isNewPasswordVisible = !isNewPasswordVisible },
                    enabled = !passwordState.isLoading
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isNewPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                        ),
                        contentDescription = if (isNewPasswordVisible) hidePasswordDesc else showPasswordDesc,
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = passwordState.newPasswordError != null,
            supportingText = passwordState.newPasswordError?.let {
                { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            } ?: {
                Text(minimum8CharsNoSpaces, fontSize = 10.sp, color = Color.Black)
            },
            enabled = !passwordState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password field
        OutlinedTextField(
            value = passwordState.confirmPassword,
            onValueChange = {
                val filtered = filterPassword(it)
                viewModel.updateConfirmPassword(filtered)
            },
            label = { Text(confirmPasswordLabelText, color = Color.Black) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = lockIconDesc,
                    tint = Color(0xFF4CAF50)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                    enabled = !passwordState.isLoading
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                        ),
                        contentDescription = if (isConfirmPasswordVisible) hidePasswordDesc else showPasswordDesc,
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = passwordState.confirmPasswordError != null,
            supportingText = passwordState.confirmPasswordError?.let {
                { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            } ?: {
                Text(confirmYourPasswordHint, fontSize = 10.sp, color = Color.Black)
            },
            enabled = !passwordState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.forgot_password_middle_small),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .padding(vertical = 5.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.changePassword(onPasswordChanged) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                disabledContainerColor = Color(0xFFA5D6A7)
            ),
            enabled = passwordState.isFormValid && !passwordState.isLoading
        ) {
            if (passwordState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = setPasswordButtonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordNewPasswordScreenPreview() {
    SmartPlantBoxTheme {
        ForgotPasswordNewPasswordScreen(
            onBackClick = {},
            onPasswordChanged = {}
        )
    }
}