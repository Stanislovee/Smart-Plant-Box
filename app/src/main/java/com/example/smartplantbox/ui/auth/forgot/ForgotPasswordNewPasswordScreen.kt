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
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
@Composable
fun ForgotPasswordNewPasswordScreen(
    onBackClick: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    val viewModel: ForgotPasswordViewModel = viewModel()
    val passwordState by viewModel.newPasswordState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    val errorPasswordRequired = stringResource(R.string.error_password_required)
    val errorPasswordMin8 = stringResource(R.string.error_password_min_8)
    val errorPasswordMax25 = stringResource(R.string.error_password_max_25)
    val errorPasswordForbidden = stringResource(R.string.error_password_forbidden)
    val errorPasswordsDoNotMatch = stringResource(R.string.error_passwords_do_not_match)
    val errorConfirmRequired = stringResource(R.string.error_confirm_password_required)

    var tempSuccessMessage by remember { mutableStateOf<String?>(null) }
    var tempErrorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.resetNewPasswordState()
    }

    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    fun filterPassword(input: String): String {
        return input.filter { !it.isWhitespace() }
    }

    LaunchedEffect(passwordState.successMessage) {
        if (passwordState.successMessage != null) {
            tempSuccessMessage = passwordState.successMessage
            delay(2000)
            tempSuccessMessage = null
            onPasswordChanged()
        }
    }

    LaunchedEffect(passwordState.errorMessage) {
        if (passwordState.errorMessage != null) {
            tempErrorMessage = passwordState.errorMessage
            delay(2000)
            tempErrorMessage = null
        }
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
                .size(280.dp)
        )

        if (tempErrorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_error), null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tempErrorMessage!!, color = Color(0xFFD32F2F), fontSize = 14.sp)
                }
            }
        }

        if (tempSuccessMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_check), null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tempSuccessMessage!!, color = Color(0xFF2E7D32), fontSize = 14.sp)
                }
            }
        }

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
            supportingText = {
                when (passwordState.newPasswordError) {
                    "Password is required" -> Text(errorPasswordRequired, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    "Password must be at least 8 characters" -> Text(errorPasswordMin8, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    "Password must not exceed 25 characters" -> Text(errorPasswordMax25, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    "Password contains forbidden words" -> Text(errorPasswordForbidden, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    else -> Text(minimum8CharsNoSpaces, fontSize = 10.sp, color = Color.Black)
                }
            },
            enabled = !passwordState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            supportingText = {
                when (passwordState.confirmPasswordError) {
                    "Passwords do not match" -> Text(errorPasswordsDoNotMatch, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    "Please confirm your password" -> Text(errorConfirmRequired, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    else -> Text(confirmYourPasswordHint, fontSize = 10.sp, color = Color.Black)
                }
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
            onClick = { viewModel.changePassword(context, onPasswordChanged) },
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