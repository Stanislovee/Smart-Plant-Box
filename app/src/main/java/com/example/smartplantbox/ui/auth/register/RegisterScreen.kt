package com.example.smartplantbox.ui.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.smartplantbox.presentation.auth.AuthViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val registerState by authViewModel.registerUiState.collectAsState()
    val context = LocalContext.current

    val registerText = stringResource(R.string.register)
    val createYourNewAccountText  = stringResource(R.string.create_your_new_account)
    val fullNameHint = stringResource(R.string.full_name_hint)
    val emailHint= stringResource(R.string.email_hint)
    val passwordHint = stringResource(R.string.password_hint)
    val confirmPasswordHint = stringResource(R.string.confirm_password_hint)
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
    val iUnderstandText = stringResource(R.string.i_understand)

    val errorPasswordMin8NoSpaces = stringResource(R.string.error_password_min_8_no_spaces)

    val errorFullNameRequired = stringResource(R.string.error_full_name_required)
    val errorFullNameMax25 = stringResource(R.string.error_full_name_max_25)
    val errorFullNameInvalid = stringResource(R.string.error_full_name_invalid)
    val errorFullNameForbidden = stringResource(R.string.error_full_name_forbidden)
    val errorFullNameInvalidChars = stringResource(R.string.error_full_name_invalid_chars)
    val errorEmailRequired  = stringResource(R.string.error_email_required)
    val errorEmailInvalidDomain = stringResource(R.string.error_email_invalid_domain)
    val errorEmailEmptyLocal = stringResource(R.string.error_email_empty_local)
    val errorEmailLocalMax25 = stringResource(R.string.error_email_local_max_25)
    val errorEmailInvalidFormat = stringResource(R.string.error_email_invalid_format)
    val errorEmailForbidden = stringResource(R.string.error_email_forbidden)
    val errorPasswordRequired = stringResource(R.string.error_password_required)
    val errorPasswordMin8 = stringResource(R.string.error_password_min_8)
    val errorPasswordMax25 = stringResource(R.string.error_password_max_25)
    val errorPasswordForbidden = stringResource(R.string.error_password_forbidden)
    val errorPasswordsDoNotMatch = stringResource(R.string.error_passwords_do_not_match)
    val errorConfirmRequired = stringResource(R.string.error_confirm_password_required)

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
    var confirmPasswordTouched by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun filterPassword(input: String) = input.filter { !it.isWhitespace() }

    LaunchedEffect(Unit) {
        authViewModel.clearAuthResult()
        authViewModel.clearMessages()
        showSuccessMessage = false
        errorMessage = null
    }

    fun onSignUpClick() {
        if (!registerState.isFormValid || registerState.isLoading) return
        errorMessage = null
        showSuccessMessage = false
        authViewModel.clearMessages()

        authViewModel.registerUser(
            context = context,
            onSuccess = {
                showSuccessMessage = true
                scope.launch {
                    delay(1500)
                    authViewModel.clearAuthResult()
                    authViewModel.clearMessages()
                    onLoginClick()
                }
            }
        )
    }

    LaunchedEffect(registerState.errorMessage) {
        if (registerState.errorMessage != null) {
            errorMessage = registerState.errorMessage
            showSuccessMessage = false
        }
    }

    LaunchedEffect(registerState.successMessage) {
        if (registerState.successMessage != null && !showSuccessMessage) {
            showSuccessMessage = true
            errorMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Image(
            painter = painterResource(R.drawable.register_top_small),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 50.dp)
                .size(140.dp)
                .align(Alignment.TopEnd)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = backButtonDesc,
            tint = Color(0xFF4CAF50),
            modifier = Modifier
                .padding(start = 8.dp, top = 50.dp)
                .size(32.dp)
                .clickable {
                    authViewModel.clearMessages()
                    authViewModel.clearAuthResult()
                    onBackClick()
                }
                .align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(registerText, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), modifier = Modifier.padding(bottom = 8.dp))
            Text(createYourNewAccountText, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))

            if (errorMessage != null && !showSuccessMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_error), null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(errorMessage!!, color = Color(0xFFD32F2F), fontSize = 14.sp)
                    }
                }
            }

            if (showSuccessMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_check), null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = registerState.successMessage ?: stringResource(R.string.registration_success),
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            OutlinedTextField(
                value = registerState.fullName,
                onValueChange = { authViewModel.updateRegisterFullName(it) },
                label = { Text(fullNameHint) },
                leadingIcon = { Icon(Icons.Default.Person, personIconDesc, tint = Color(0xFF4CAF50)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = registerState.fullNameError != null,
                supportingText = {
                    when (registerState.fullNameError) {
                        "Full name is required" -> Text(errorFullNameRequired, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Full name must not exceed 25 characters" -> Text(errorFullNameMax25, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Full name can only contain letters and spaces" -> Text(errorFullNameInvalid, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Name contains forbidden words" -> Text(errorFullNameForbidden, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Name contains invalid characters" -> Text(errorFullNameInvalidChars, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        else -> {}
                    }
                },
                enabled = !registerState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = registerState.email,
                onValueChange = { authViewModel.updateRegisterEmail(it) },
                label = { Text(emailHint) },
                leadingIcon = { Icon(Icons.Default.Email, emailIconDesc, tint = Color(0xFF4CAF50)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = registerState.emailError != null,
                supportingText = {
                    when (registerState.emailError) {
                        "Email is required" -> Text(errorEmailRequired, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email must be @gmail.com" -> Text(errorEmailInvalidDomain, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email local part cannot be empty" -> Text(errorEmailEmptyLocal, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email local part must not exceed 25 characters" -> Text(errorEmailLocalMax25, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email can only contain letters, numbers, and . _ -" -> Text(errorEmailInvalidFormat, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Email contains forbidden words" -> Text(errorEmailForbidden, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        else -> {}
                    }
                },
                enabled = !registerState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = registerState.password,
                onValueChange = {
                    authViewModel.updateRegisterPassword(filterPassword(it))
                    confirmPasswordTouched = true
                },
                label = { Text(passwordHint) },
                leadingIcon = { Icon(Icons.Default.Lock, lockIconDesc, tint = Color(0xFF4CAF50)) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }, enabled = !registerState.isLoading) {
                        Icon(
                            painter = painterResource(if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                            contentDescription = if (isPasswordVisible) hidePasswordDesc else showPasswordDesc,
                            modifier = Modifier.size(24.dp), tint = Color(0xFF4CAF50)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = registerState.passwordError != null,
                supportingText = {
                    when (registerState.passwordError) {
                        "Password is required" -> Text(errorPasswordRequired, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Password must be at least 8 characters" -> Text(errorPasswordMin8, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Password must not exceed 25 characters" -> Text(errorPasswordMax25, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        "Password contains forbidden words" -> Text(errorPasswordForbidden, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        else -> Text(errorPasswordMin8NoSpaces, fontSize = 10.sp, color = Color.Gray)
                    }
                },
                enabled = !registerState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = registerState.confirmPassword,
                onValueChange = {
                    authViewModel.updateRegisterConfirmPassword(filterPassword(it))
                    confirmPasswordTouched = true
                },
                label = { Text(confirmPasswordHint) },
                leadingIcon = { Icon(Icons.Default.Lock, lockIconDesc, tint = Color(0xFF4CAF50)) },
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }, enabled = !registerState.isLoading) {
                        Icon(
                            painter = painterResource(if (isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                            contentDescription = if (isConfirmPasswordVisible) hidePasswordDesc else showPasswordDesc,
                            modifier = Modifier.size(24.dp), tint = Color(0xFF4CAF50)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = registerState.confirmPasswordError != null && confirmPasswordTouched,
                supportingText = {
                    if (registerState.confirmPasswordError != null && confirmPasswordTouched) {
                        val msg = if (registerState.confirmPasswordError == "Passwords do not match")
                            errorPasswordsDoNotMatch else errorConfirmRequired
                        Text(msg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    } else {
                        Text(confirmYourPasswordHint, fontSize = 10.sp, color = Color.Gray)
                    }
                },
                enabled = !registerState.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50)),
                        modifier = Modifier.size(32.dp),
                        enabled = !registerState.isLoading
                    )
                    Text(bySigningYouAgreeToOur, fontSize = 13.sp, color = Color.Gray)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 17.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(termOfUseText, fontSize = 14.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showTermsDialog = true })
                    Text(" $andText ", fontSize = 13.sp, color = Color.Gray)
                    Text(privacyText, fontSize = 14.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showPrivacyDialog = true })
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(85.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.register_middle_small),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).padding(vertical = 5.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Button(
                onClick = { onSignUpClick() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color(0xFFA5D6A7)
                ),
                enabled = registerState.isFormValid && !registerState.isLoading
            ) {
                if (registerState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(signUpText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.padding(bottom = 24.dp), horizontalArrangement = Arrangement.Center) {
                Text(alreadyHaveAnAccount, fontSize = 16.sp, color = Color.Gray)
                Text(
                    text = loginText,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = if (registerState.isLoading) Color.LightGray else Color(0xFF4CAF50),
                    modifier = Modifier.clickable(enabled = !registerState.isLoading) {
                        authViewModel.clearMessages()
                        authViewModel.clearAuthResult()
                        onLoginClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text(termsOfUseTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    listOf(
                        termsAcceptanceTitle to termsAcceptanceText,
                        termsAccountRegistrationTitle to termsAccountRegistrationText,
                        termsDeviceUsageTitle to termsDeviceUsageText,
                        termsDataCollectionTitle to termsDataCollectionText,
                        termsTerminationTitle to termsTerminationText,
                        termsChangesTitle to termsChangesText,
                        termsContactTitle to termsContactText
                    ).forEach { (title, body) ->
                        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        Text(body, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(bottom = 12.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(8.dp)) {
                    Text(iUnderstandText, color = Color.White)
                }
            },
            containerColor = Color.White, shape = RoundedCornerShape(16.dp)
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(privacyNoticeTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    listOf(
                        privacyInfoCollectTitle to privacyInfoCollectText,
                        privacyUseInfoTitle to privacyUseInfoText,
                        privacyDataSharingTitle to privacyDataSharingText,
                        privacyDataSecurityTitle to privacyDataSecurityText,
                        privacyYourRightsTitle to privacyYourRightsText,
                        privacyDataRetentionTitle to privacyDataRetentionText,
                        privacyContactTitle to privacyContactText
                    ).forEach { (title, body) ->
                        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        Text(body, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(bottom = 12.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(8.dp)) {
                    Text(iUnderstandText, color = Color.White)
                }
            },
            containerColor = Color.White, shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    SmartPlantBoxTheme {
        RegisterScreen(onRegisterClick = {}, onLoginClick = {}, onBackClick = {})
    }
}