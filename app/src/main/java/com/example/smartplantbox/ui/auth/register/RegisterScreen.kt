package com.example.smartplantbox.ui.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.presentation.auth.AuthResult
import com.example.smartplantbox.presentation.auth.AuthViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import com.example.smartplantbox.utils.LocalizationManager

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val registerState by authViewModel.registerUiState.collectAsState()
    val authResult by authViewModel.authResult.collectAsState()
    val configuration = LocalConfiguration.current
    val currentLanguage = LocalizationManager.getCurrentLanguage(context)
    val isPolish = currentLanguage == LocalizationManager.Language.POLISH

    val registerText = stringResource(R.string.register)
    val createYourNewAccountText = stringResource(R.string.create_your_new_account)
    val fullNameHint = stringResource(R.string.full_name_hint)
    val emailHint = stringResource(R.string.email_hint)
    val passwordHint = stringResource(R.string.password_hint)
    val confirmPasswordHint = stringResource(R.string.confirm_password_hint)
    val minimum8CharsNoSpacesHint = stringResource(R.string.minimum_8_characters_no_spaces_hint)
    val confirmYourPasswordHint = stringResource(R.string.confirm_your_password_hint)
    val bySigningYouAgreeToOur = stringResource(R.string.by_signing_you_agree_to_our)
    val termOfUseText = stringResource(R.string.term_of_use)
    val andText = stringResource(R.string.and)
    val privacyText = stringResource(R.string.privacy)
    val signUpText = stringResource(R.string.sign_up)
    val alreadyHaveAnAccount = stringResource(R.string.already_have_an_account)
    val loginText = stringResource(R.string.login)
    val personIconDesc = stringResource(R.string.person_icon)
    val emailIconDesc = stringResource(R.string.email_icon)
    val lockIconDesc = stringResource(R.string.lock_icon)
    val hidePasswordDesc = stringResource(R.string.hide_password)
    val showPasswordDesc = stringResource(R.string.show_password)
    val backButtonDesc = stringResource(R.string.back_button)

    val termsOfUseTitle = stringResource(R.string.terms_of_use_title)
    val termsAcceptanceTitle = stringResource(R.string.terms_acceptance_title)
    val termsAcceptanceText = stringResource(R.string.terms_acceptance_text)
    val termsAccountRegistrationTitle = stringResource(R.string.terms_account_registration_title)
    val termsAccountRegistrationText = stringResource(R.string.terms_account_registration_text)
    val termsDeviceUsageTitle = stringResource(R.string.terms_device_usage_title)
    val termsDeviceUsageText = stringResource(R.string.terms_device_usage_text)
    val termsDataCollectionTitle = stringResource(R.string.terms_data_collection_title)
    val termsDataCollectionText = stringResource(R.string.terms_data_collection_text)
    val termsTerminationTitle = stringResource(R.string.terms_termination_title)
    val termsTerminationText = stringResource(R.string.terms_termination_text)
    val termsChangesTitle = stringResource(R.string.terms_changes_title)
    val termsChangesText = stringResource(R.string.terms_changes_text)
    val termsContactTitle = stringResource(R.string.terms_contact_title)
    val termsContactText = stringResource(R.string.terms_contact_text)
    val iUnderstandText = stringResource(R.string.i_understand)
    val privacyNoticeTitle = stringResource(R.string.privacy_notice_title)
    val privacyInfoCollectTitle = stringResource(R.string.privacy_info_collect_title)
    val privacyInfoCollectText = stringResource(R.string.privacy_info_collect_text)
    val privacyUseInfoTitle = stringResource(R.string.privacy_use_info_title)
    val privacyUseInfoText = stringResource(R.string.privacy_use_info_text)
    val privacyDataSharingTitle = stringResource(R.string.privacy_data_sharing_title)
    val privacyDataSharingText = stringResource(R.string.privacy_data_sharing_text)
    val privacyDataSecurityTitle = stringResource(R.string.privacy_data_security_title)
    val privacyDataSecurityText = stringResource(R.string.privacy_data_security_text)
    val privacyYourRightsTitle = stringResource(R.string.privacy_your_rights_title)
    val privacyYourRightsText = stringResource(R.string.privacy_your_rights_text)
    val privacyDataRetentionTitle = stringResource(R.string.privacy_data_retention_title)
    val privacyDataRetentionText = stringResource(R.string.privacy_data_retention_text)
    val privacyContactTitle = stringResource(R.string.privacy_contact_title)
    val privacyContactText = stringResource(R.string.privacy_contact_text)

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    fun filterPassword(input: String): String {
        return input.filter { !it.isWhitespace() }
    }

    LaunchedEffect(authResult) {
        when (authResult) {
            is AuthResult.Error -> {
                println("Error: ${(authResult as AuthResult.Error).message}")
                authViewModel.clearAuthResult()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Image(
            painter = painterResource(id = R.drawable.register_top_small),
            contentDescription = null,
            modifier = Modifier
                .padding(top = if (isPolish) 40.dp else 50.dp, end = 0.dp)
                .size(if (isPolish) 120.dp else 140.dp)
                .align(Alignment.TopEnd)
        )
        // Backk
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = backButtonDesc,
            tint = Color(0xFF4CAF50),
            modifier = Modifier
                .padding(start = 16.dp, top = 50.dp)
                .size(32.dp)
                .clickable {
                    println("🔙 Back button clicked - navigating to LoginScreen")
                    onBackClick()
                }
                .zIndex(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isPolish) 80.dp else 100.dp))

            Text(
                text = registerText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = createYourNewAccountText,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Full Name field
            OutlinedTextField(
                value = registerState.fullName,
                onValueChange = { authViewModel.updateRegisterFullName(it) },
                label = { Text(fullNameHint, color = Color.Black) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = personIconDesc,
                        tint = Color(0xFF4CAF50)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = registerState.fullNameError != null,
                supportingText = registerState.fullNameError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email field
            OutlinedTextField(
                value = registerState.email,
                onValueChange = { authViewModel.updateRegisterEmail(it) },
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = registerState.emailError != null,
                supportingText = registerState.emailError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = registerState.password,
                onValueChange = {
                    val filtered = filterPassword(it)
                    authViewModel.updateRegisterPassword(filtered)
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
                        enabled = !registerState.isLoading
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = registerState.passwordError != null,
                supportingText = registerState.passwordError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                } ?: {
                    Text(minimum8CharsNoSpacesHint, fontSize = 10.sp, color = Color.Black)
                },
                enabled = !registerState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = registerState.confirmPassword,
                onValueChange = {
                    val filtered = filterPassword(it)
                    authViewModel.updateRegisterConfirmPassword(filtered)
                },
                label = { Text(confirmPasswordHint, color = Color.Black) },
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
                        enabled = !registerState.isLoading
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
                isError = registerState.confirmPasswordError != null,
                supportingText = registerState.confirmPasswordError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                } ?: {
                    Text(confirmYourPasswordHint, fontSize = 10.sp, color = Color.Black)
                },
                enabled = !registerState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isPolish) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = registerState.agreeToTerms,
                            onCheckedChange = { authViewModel.updateAgreeToTerms(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = bySigningYouAgreeToOur,
                            fontSize = 13.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = termOfUseText,
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { showTermsDialog = true }
                        )
                        Text(
                            text = " $andText ",
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                        Text(
                            text = privacyText,
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { showPrivacyDialog = true }
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = registerState.agreeToTerms,
                        onCheckedChange = { authViewModel.updateAgreeToTerms(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = bySigningYouAgreeToOur,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        text = termOfUseText,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showTermsDialog = true }
                    )
                    Text(
                        text = " $andText ",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        text = privacyText,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showPrivacyDialog = true }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isPolish) 70.dp else 85.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.register_middle_small),
                    contentDescription = null,
                    modifier = Modifier
                        .size(if (isPolish) 65.dp else 80.dp)
                        .padding(vertical = 5.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Button(
                onClick = {
                    authViewModel.registerUser {
                        onRegisterClick()
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
                enabled = registerState.isFormValid && !registerState.isLoading
            ) {
                if (registerState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = signUpText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = alreadyHaveAnAccount,
                    fontSize = if (isPolish) 14.sp else 16.sp,
                    color = Color.Black
                )
                Text(
                    text = loginText,
                    fontSize = if (isPolish) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.clickable {
                        authViewModel.clearAuthResult()
                        onLoginClick()
                    }
                )
            }
            Spacer(modifier = Modifier.height(if (isPolish) 16.dp else 24.dp))
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = termsOfUseTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    Text(
                        text = termsAcceptanceTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = termsAcceptanceText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = termsAccountRegistrationTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = termsAccountRegistrationText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = termsDeviceUsageTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = termsDeviceUsageText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = termsDataCollectionTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = termsDataCollectionText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = termsTerminationTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = termsTerminationText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = termsChangesTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = termsChangesText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = termsContactTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = termsContactText,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(iUnderstandText, color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = privacyNoticeTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    Text(
                        text = privacyInfoCollectTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = privacyInfoCollectText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = privacyUseInfoTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = privacyUseInfoText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = privacyDataSharingTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = privacyDataSharingText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = privacyDataSecurityTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = privacyDataSecurityText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = privacyYourRightsTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = privacyYourRightsText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = privacyDataRetentionTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = privacyDataRetentionText,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = privacyContactTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = privacyContactText,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(iUnderstandText, color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    SmartPlantBoxTheme {
        RegisterScreen(
            onRegisterClick = {},
            onLoginClick = {},
            onBackClick = {}
        )
    }
}