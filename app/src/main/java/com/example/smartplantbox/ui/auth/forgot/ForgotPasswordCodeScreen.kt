package com.example.smartplantbox.ui.auth.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.presentation.auth.ForgotPasswordViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordCodeScreen(
    onBackClick: () -> Unit,
    onCodeVerified: () -> Unit
) {
    val viewModel: ForgotPasswordViewModel = viewModel()
    val codeState by viewModel.codeState.collectAsState()
    val savedEmail = viewModel.getSavedEmail()
    val context = LocalContext.current

    val codeText = stringResource(R.string.code)
    val verificationText = stringResource(R.string.verification)
    val enterCodeDescriptionText = stringResource(R.string.enter_code_description)
    val dontReceiveCodeText = stringResource(R.string.dont_receive_code)
    val resendText = stringResource(R.string.resend)
    val sendCodeText = stringResource(R.string.send_code)
    val backButtonDesc = stringResource(R.string.back_button_code)
    val errorInvalidCode = stringResource(R.string.forgot_invalid_code)

    var tempSuccessMessage by remember { mutableStateOf<String?>(null) }
    var tempErrorMessage by remember { mutableStateOf<String?>(null) }

    var codeValue by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(codeValue.text) {
        val digits = codeValue.text
        viewModel.updateCode(1, digits.getOrNull(0)?.toString() ?: "")
        viewModel.updateCode(2, digits.getOrNull(1)?.toString() ?: "")
        viewModel.updateCode(3, digits.getOrNull(2)?.toString() ?: "")
        viewModel.updateCode(4, digits.getOrNull(3)?.toString() ?: "")
    }

    LaunchedEffect(Unit) {
        codeValue = TextFieldValue("")
        viewModel.resetCodeState()
        focusRequester.requestFocus()
        viewModel.startCodeTimer()
    }

    LaunchedEffect(codeState.successMessage) {
        if (codeState.successMessage != null) {
            tempSuccessMessage = codeState.successMessage
            delay(2000)
            tempSuccessMessage = null
        }
    }

    LaunchedEffect(codeState.errorMessage) {
        if (codeState.errorMessage != null && codeState.codeError == null) {
            tempErrorMessage = codeState.errorMessage
            delay(2000)
            tempErrorMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                Text(codeText, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), textAlign = TextAlign.Center)
                Text(verificationText, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            Image(
                painter = painterResource(id = R.drawable.forgot_email_leaf_right),
                contentDescription = null,
                modifier = Modifier.size(200.dp).align(Alignment.TopEnd).offset(y = (-85).dp, x = 20.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.forgot_code_leaf_left),
                contentDescription = null,
                modifier = Modifier.size(220.dp).align(Alignment.BottomStart).offset(x = (-20).dp)
            )
            Image(
                painter = painterResource(id = R.drawable.forgot_code_leaf_center),
                contentDescription = null,
                modifier = Modifier.size(175.dp).align(Alignment.BottomCenter).offset(x = 30.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = enterCodeDescriptionText, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 8.dp))
        Text(text = savedEmail, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), modifier = Modifier.padding(bottom = 24.dp))

        if (tempErrorMessage != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_error), null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tempErrorMessage!!, color = Color(0xFFD32F2F), fontSize = 14.sp)
                }
            }
        }

        if (tempSuccessMessage != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_check), null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tempSuccessMessage!!, color = Color(0xFF2E7D32), fontSize = 14.sp)
                }
            }
        }

        Box(contentAlignment = Alignment.Center) {
            BasicTextField(
                value = codeValue,
                onValueChange = { newVal ->
                    val digits = newVal.text.filter { it.isDigit() }.take(4)
                    codeValue = TextFieldValue(digits, selection = TextRange(digits.length))
                },
                modifier = Modifier
                    .size(1.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(Color.Transparent),
                textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { focusRequester.requestFocus() },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val digits = codeValue.text
                val isError = codeState.codeError != null

                for (i in 0..3) {
                    val char = digits.getOrNull(i)?.toString() ?: ""
                    val isFocused = digits.length == i

                    val borderColor = when {
                        isError -> Color(0xFFD32F2F)
                        isFocused -> Color(0xFF4CAF50)
                        char.isNotEmpty() -> Color(0xFF4CAF50)
                        else -> Color(0xFFE0E0E0)
                    }
                    val bgColor = when {
                        isError -> Color(0xFFFFEBEE)
                        else -> Color(0xFFF5F5F5)
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(60.dp)
                            .height(70.dp)
                            .background(bgColor, RoundedCornerShape(12.dp))
                            .border(
                                width = if (isFocused) 2.dp else 1.5.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        if (char.isNotEmpty()) {
                            Text(
                                text = char,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isError) Color(0xFFD32F2F) else Color(0xFF1B5E20),
                                textAlign = TextAlign.Center
                            )
                        }
                        if (isFocused) {
                            var cursorVisible by remember { mutableStateOf(true) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(500)
                                    cursorVisible = !cursorVisible
                                }
                            }
                            if (cursorVisible) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(Color(0xFF4CAF50))
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (codeState.codeError != null && tempErrorMessage == null) {
            Text(text = errorInvalidCode, fontSize = 12.sp, color = Color(0xFFD32F2F), modifier = Modifier.padding(bottom = 8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = dontReceiveCodeText, fontSize = 14.sp, color = Color.Black)
            if (codeState.canResend) {
                Text(
                    text = resendText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.clickable(enabled = !codeState.isLoading) {
                        codeValue = TextFieldValue("")
                        viewModel.resendCode(context)
                    }
                )
            } else {
                Text(text = String.format(stringResource(R.string.wait_seconds), codeState.timerSeconds), fontSize = 14.sp, color = Color.Black)
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.forgot_code_middle_small),
                contentDescription = null,
                modifier = Modifier.size(95.dp).padding(vertical = 5.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.verifyCode(context, onCodeVerified) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                disabledContainerColor = Color(0xFFA5D6A7)
            ),
            enabled = codeState.isFormValid && !codeState.isLoading
        ) {
            if (codeState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(sendCodeText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordCodeScreenPreview() {
    SmartPlantBoxTheme {
        ForgotPasswordCodeScreen(onBackClick = {}, onCodeVerified = {})
    }
}