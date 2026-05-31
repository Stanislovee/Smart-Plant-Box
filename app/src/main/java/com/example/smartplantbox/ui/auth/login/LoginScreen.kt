package com.example.smartplantbox.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.presentation.auth.AuthResult
import com.example.smartplantbox.presentation.auth.AuthViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val loginState by authViewModel.loginUiState.collectAsState()
    val authResult by authViewModel.authResult.collectAsState()
    val context = LocalContext.current

    val welcomePlantsText = stringResource(R.string.welcome_plants)
    val loginToYourAccountText = stringResource(R.string.login_to_your_account)
    val emailHint = stringResource(R.string.email_hint)
    val passwordHint = stringResource(R.string.password_hint)
    val rememberMeText = stringResource(R.string.remember_me)
    val forgetPasswordText = stringResource(R.string.forget_password)
    val loginButtonText = stringResource(R.string.login_button)
    val dontHaveAnAccountText = stringResource(R.string.dont_have_an_account)
    val signUpButtonText = stringResource(R.string.sign_up_button)
    val emailIconDesc = stringResource(R.string.email_icon)
    val lockIconDesc = stringResource(R.string.lock_icon)
    val hidePasswordDesc = stringResource(R.string.hide_password)
    val showPasswordDesc = stringResource(R.string.show_password)
    val noSpacesAllowedText = stringResource(R.string.no_spaces_allowed)

    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.initPreferences(context)
    }

    LaunchedEffect(authResult) {
        when (authResult) {
            is AuthResult.Error -> {
                authViewModel.clearAuthResult()
            }
            else -> {}
        }
    }

    fun filterPassword(input: String): String {
        return input.filter { !it.isWhitespace() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_top_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopStart)
                .graphicsLayer {
                    translationX = 0f
                    translationY = -140f
                },
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.8f))

            Text(
                text = welcomePlantsText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF326032),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = loginToYourAccountText,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (loginState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_error), null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = loginState.errorMessage!!,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            OutlinedTextField(
                value = loginState.email,
                onValueChange = { authViewModel.updateLoginEmail(it, context) },
                label = { Text(emailHint, color = Color.Black) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = emailIconDesc,
                        tint = Color(0xFF4CAF50)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = loginState.emailError != null,
                supportingText = loginState.emailError?.let {
                    { Text(loginState.emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !loginState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = loginState.password,
                onValueChange = {
                    val filtered = filterPassword(it)
                    authViewModel.updateLoginPassword(filtered, context)
                },
                label = { Text(passwordHint, color = Color.Black) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = lockIconDesc,
                        tint = Color(0xFF4CAF50)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible },
                        enabled = !loginState.isLoading
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                            ),
                            contentDescription = if (isPasswordVisible) hidePasswordDesc else showPasswordDesc,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF4CAF50)
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = loginState.passwordError != null,
                supportingText = loginState.passwordError?.let {
                    { Text(loginState.passwordError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                } ?: {
                    Text(noSpacesAllowedText, fontSize = 10.sp, color = Color.Gray)
                },
                enabled = !loginState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        enabled = !loginState.isLoading,
                        onClick = { authViewModel.updateRememberMe(!loginState.rememberMe) }
                    )
                ) {
                    Checkbox(
                        checked = loginState.rememberMe,
                        onCheckedChange = { authViewModel.updateRememberMe(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50)
                        ),
                        enabled = !loginState.isLoading
                    )
                    Text(
                        text = rememberMeText,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Text(
                    text = forgetPasswordText,
                    fontSize = 14.sp,
                    color = if (loginState.isLoading) Color.LightGray else Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(
                        enabled = !loginState.isLoading,
                        onClick = onForgotPasswordClick
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    authViewModel.loginUser(context) {
                        onLoginClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color(0xFFA5D6A7)
                ),
                enabled = loginState.isFormValid && !loginState.isLoading
            ) {
                if (loginState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = loginButtonText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 32.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dontHaveAnAccountText,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = signUpButtonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (loginState.isLoading) Color.LightGray else Color(0xFF4CAF50),
                    modifier = Modifier.clickable(enabled = !loginState.isLoading) {
                        authViewModel.clearMessages()
                        onSignUpClick()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(0.07f))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    SmartPlantBoxTheme {
        LoginScreen(
            onLoginClick = {},
            onSignUpClick = {},
            onForgotPasswordClick = {}
        )
    }
}