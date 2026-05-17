package com.example.smartplantbox.ui.auth.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.presentation.auth.ForgotPasswordViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme

@Composable
fun ForgotPasswordEmailScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val viewModel: ForgotPasswordViewModel = viewModel()
    val emailState by viewModel.emailState.collectAsState()
    val context = LocalContext.current

    val forgotText = stringResource(R.string.forgot)
    val passwordQuestionText = stringResource(R.string.password_question)
    val forgotPasswordDescriptionText = stringResource(R.string.forgot_password_description)
    val emailHint = stringResource(R.string.email_hint)
    val emailIconDesc = stringResource(R.string.email_icon)
    val secureEncryptedText = stringResource(R.string.secure_encrypted)
    val continueButtonText = stringResource(R.string.continue_button)
    val backIconDesc = stringResource(R.string.back_icon)
    val secureIconDesc = stringResource(R.string.secure_icon)

    val errorEmailRequired = stringResource(R.string.error_email_required)
    val errorEmailInvalidDomain = stringResource(R.string.error_email_invalid_domain)
    val errorEmailEmptyLocal = stringResource(R.string.error_email_empty_local)
    val errorEmailLocalMax25 = stringResource(R.string.error_email_local_max_25)
    val errorEmailInvalidFormat = stringResource(R.string.error_email_invalid_format)
    val errorEmailForbidden = stringResource(R.string.error_email_forbidden)

    LaunchedEffect(Unit) {
        viewModel.resetEmailState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = backIconDesc,
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
                        text = forgotText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = passwordQuestionText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.forgot_email_leaf_right),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.TopEnd)
                        .offset(y = (-85).dp)
                        .offset(x = 20.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.forgot_email_leaf_left),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.BottomStart)
                        .offset(x =(-30).dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.forgot_email_leaf_center),
                    contentDescription = null,
                    modifier = Modifier
                        .size(155.dp)
                        .align(Alignment.BottomCenter)
                        .offset(x = 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = forgotPasswordDescriptionText,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = emailState.email,
                onValueChange = { viewModel.updateEmail(it) },
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
                isError = emailState.emailError != null,
                supportingText = {
                    when (emailState.emailError) {
                        "Email is required" -> Text(errorEmailRequired, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email must be @gmail.com" -> Text(errorEmailInvalidDomain, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email local part cannot be empty" -> Text(errorEmailEmptyLocal, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email local part must not exceed 25 characters" -> Text(errorEmailLocalMax25, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email can only contain letters, numbers, and . _ -" -> Text(errorEmailInvalidFormat, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email contains forbidden words" -> Text(errorEmailForbidden, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        else -> {}
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !emailState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (emailState.errorMessage != null && emailState.successMessage == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_error),
                            null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = emailState.errorMessage!!,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (emailState.successMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = emailState.successMessage!!,
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = secureIconDesc,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = secureEncryptedText,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.register_middle_small),
                    contentDescription = null,
                    modifier = Modifier
                        .size(95.dp)
                        .padding(vertical = 5.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.sendResetCode(context, onContinueClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color(0xFFA5D6A7)
                ),
                enabled = emailState.isFormValid && !emailState.isLoading
            ) {
                if (emailState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = continueButtonText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordEmailScreenPreview() {
    SmartPlantBoxTheme {
        ForgotPasswordEmailScreen(
            onBackClick = {},
            onContinueClick = {}
        )
    }
}