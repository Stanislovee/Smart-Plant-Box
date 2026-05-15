package com.example.smartplantbox.ui.auth.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.presentation.auth.ForgotPasswordViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme

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

    val focus1 = remember { FocusRequester() }
    val focus2 = remember { FocusRequester() }
    val focus3 = remember { FocusRequester() }
    val focus4 = remember { FocusRequester() }

    // Is has been downloaded eraly
    var isPasting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focus1.requestFocus()
        viewModel.startCodeTimer()
    }

    fun handlePaste(fullCode: String) {
        if (fullCode.length == 4 && fullCode.all { it.isDigit() }) {
            viewModel.updateCode(1, fullCode[0].toString())
            viewModel.updateCode(2, fullCode[1].toString())
            viewModel.updateCode(3, fullCode[2].toString())
            viewModel.updateCode(4, fullCode[3].toString())
            focus4.requestFocus()
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
                Text(
                    text = codeText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = verificationText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center
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
                    .offset(y = (-85).dp, x = 20.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.forgot_code_leaf_left),
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp)
            )

            Image(
                painter = painterResource(id = R.drawable.forgot_code_leaf_center),
                contentDescription = null,
                modifier = Modifier
                    .size(175.dp)
                    .align(Alignment.BottomCenter)
                    .offset(x = 30.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = enterCodeDescriptionText,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = savedEmail,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CodeTextField(
                value = codeState.code1,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        viewModel.updateCode(1, newValue)
                        if (newValue.isNotEmpty() && !isPasting) {
                            focus2.requestFocus()
                        }
                    }
                },
                onPaste = { pastedText ->
                    if (pastedText.length == 4 && pastedText.all { it.isDigit() }) {
                        isPasting = true
                        handlePaste(pastedText)
                        isPasting = false
                    } else if (pastedText.length == 1 && pastedText.all { it.isDigit() }) {
                        viewModel.updateCode(1, pastedText)
                        focus2.requestFocus()
                    }
                },
                focusRequester = focus1,
                isError = codeState.codeError != null,
                onDeleteEmpty = { focus1.requestFocus() }
            )

            CodeTextField(
                value = codeState.code2,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        viewModel.updateCode(2, newValue)
                        if (newValue.isNotEmpty() && !isPasting) {
                            focus3.requestFocus()
                        }
                    }
                },
                onPaste = { pastedText ->
                    if (pastedText.length == 4 && pastedText.all { it.isDigit() }) {
                        isPasting = true
                        handlePaste(pastedText)
                        isPasting = false
                    } else if (pastedText.length == 1 && pastedText.all { it.isDigit() }) {
                        viewModel.updateCode(2, pastedText)
                        focus3.requestFocus()
                    }
                },
                focusRequester = focus2,
                isError = codeState.codeError != null,
                onDeleteEmpty = { focus1.requestFocus() }
            )

            CodeTextField(
                value = codeState.code3,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        viewModel.updateCode(3, newValue)
                        if (newValue.isNotEmpty() && !isPasting) {
                            focus4.requestFocus()
                        }
                    }
                },
                onPaste = { pastedText ->
                    if (pastedText.length == 4 && pastedText.all { it.isDigit() }) {
                        isPasting = true
                        handlePaste(pastedText)
                        isPasting = false
                    } else if (pastedText.length == 1 && pastedText.all { it.isDigit() }) {
                        viewModel.updateCode(3, pastedText)
                        focus4.requestFocus()
                    }
                },
                focusRequester = focus3,
                isError = codeState.codeError != null,
                onDeleteEmpty = { focus2.requestFocus() }
            )

            CodeTextField(
                value = codeState.code4,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        viewModel.updateCode(4, newValue)
                    }
                },
                onPaste = { pastedText ->
                    if (pastedText.length == 4 && pastedText.all { it.isDigit() }) {
                        isPasting = true
                        handlePaste(pastedText)
                        isPasting = false
                    } else if (pastedText.length == 1 && pastedText.all { it.isDigit() }) {
                        viewModel.updateCode(4, pastedText)
                    }
                },
                focusRequester = focus4,
                isError = codeState.codeError != null,
                onDeleteEmpty = { focus3.requestFocus() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (codeState.codeError != null) {
            Text(
                text = codeState.codeError!!,
                fontSize = 12.sp,
                color = Color(0xFFD32F2F),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (codeState.errorMessage != null && codeState.codeError == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = codeState.errorMessage!!,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = dontReceiveCodeText,
                fontSize = 14.sp,
                color = Color.Black
            )

            if (codeState.canResend) {
                Text(
                    text = resendText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.clickable(
                        enabled = !codeState.isLoading
                    ) {
                        viewModel.resendCode(context)
                    }
                )
            } else {
                Text(
                    text = String.format(stringResource(R.string.wait_seconds), codeState.timerSeconds),
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.forgot_code_middle_small),
                contentDescription = null,
                modifier = Modifier
                    .size(95.dp)
                    .padding(vertical = 5.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.verifyCode(onCodeVerified) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                disabledContainerColor = Color(0xFFA5D6A7)
            ),
            enabled = codeState.isFormValid && !codeState.isLoading
        ) {
            if (codeState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = sendCodeText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CodeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onPaste: (String) -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean,
    onDeleteEmpty: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            when {

                newValue.length == 4 && newValue.all { it.isDigit() } -> {
                    onPaste(newValue)
                }

                newValue.length == 1 && newValue.all { it.isDigit() } -> {
                    onValueChange(newValue)
                }

                value.isNotEmpty() && newValue.isEmpty() -> {
                    onValueChange("")
                    onDeleteEmpty()
                }
                // Звичайне введення (тільки 1 цифра)
                newValue.length <= 1 && newValue.all { it.isDigit() } -> {
                    onValueChange(newValue)
                }
            }
        },
        modifier = Modifier
            .width(60.dp)
            .height(70.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        isError = isError,
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),
            textAlign = TextAlign.Center
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color(0xFFD32F2F) else Color(0xFF4CAF50),
            unfocusedBorderColor = if (isError) Color(0xFFD32F2F) else Color(0xFFE0E0E0),
            errorBorderColor = Color(0xFFD32F2F),
            focusedContainerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
            unfocusedContainerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
            cursorColor = Color(0xFF4CAF50),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordCodeScreenPreview() {
    SmartPlantBoxTheme {
        ForgotPasswordCodeScreen(
            onBackClick = {},
            onCodeVerified = {}
        )
    }
}