package com.example.smartplantbox.ui.main.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.presentation.auth.AuthViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import com.example.smartplantbox.utils.LocalizationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogoutClick: () -> Unit) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val currentLanguage = LocalizationManager.getCurrentLanguage(context)

    val min25CharsError = stringResource(R.string.minimum_25_characters)
    val max16CharsHint = stringResource(R.string.maximum_16_characters)
    val nameCannotBeEmpty = stringResource(R.string.name_cannot_be_empty)
    val saveText = stringResource(R.string.save)
    val cancelText = stringResource(R.string.cancel)
    val editText = stringResource(R.string.edit)
    val profileSettings = stringResource(R.string.profile_settings)
    val languageText = stringResource(R.string.language)
    val helloText = stringResource(R.string.hello)
    val youHaveDevices = stringResource(R.string.you_have_devices)
    val fullNameText = stringResource(R.string.full_name)
    val emailText = stringResource(R.string.email)
    val yourPasswordText = stringResource(R.string.your_password)
    val setNewPasswordText = stringResource(R.string.set_new_password)
    val currentPasswordText = stringResource(R.string.current_password)
    val newPasswordText = stringResource(R.string.new_password)
    val confirmNewPasswordText = stringResource(R.string.confirm_new_password)
    val saveSettingsText = stringResource(R.string.save_the_settings)
    val logoutText = stringResource(R.string.logout)
    val areYouSureLogout = stringResource(R.string.are_you_sure_logout)
    val min8CharsNoSpaces = stringResource(R.string.minimum_8_characters_no_spaces)
    val currentPasswordRequired = stringResource(R.string.current_password_required)
    val newPasswordRequired = stringResource(R.string.new_password_required)
    val pleaseConfirmPassword = stringResource(R.string.please_confirm_password)
    val currentPasswordCannotContainSpaces = stringResource(R.string.current_password_cannot_contain_spaces)
    val newPasswordCannotContainSpaces = stringResource(R.string.new_password_cannot_contain_spaces)
    val confirmPasswordCannotContainSpaces = stringResource(R.string.confirm_password_cannot_contain_spaces)
    val passwordMustBe8Characters = stringResource(R.string.password_must_be_8_characters)
    val passwordsDoNotMatch = stringResource(R.string.passwords_do_not_match)
    val currentPasswordIncorrect = stringResource(R.string.current_password_incorrect)
    val passwordChangedSuccessfully = stringResource(R.string.password_changed_successfully)
    val failedToChangePassword = stringResource(R.string.failed_to_change_password)
    val nameUpdatedSuccessfully = stringResource(R.string.name_updated_successfully)
    val failedToUpdateName = stringResource(R.string.failed_to_update_name)

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var deviceCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    var isEditingName by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var isUpdatingName by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isChangingPassword by remember { mutableStateOf(false) }
    var isUpdatingPassword by remember { mutableStateOf(false) }

    var isCurrentPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    fun filterPassword(input: String) = input.filter { !it.isWhitespace() }

    fun changeLanguage(language: LocalizationManager.Language) {
        LocalizationManager.setLanguage(context, language)
        (context as? android.app.Activity)?.recreate()
    }

    suspend fun verifyCurrentPassword(email: String, password: String): Boolean = try {
        val (status, response) = AuthRepositoryImpl().login(email, password, context)
        status == 200 && response.success
    } catch (_: Exception) { false }

    suspend fun updatePasswordViaAPI(pwd: String): Pair<Boolean, String?> {
        val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("jwt_token", null) ?: return Pair(false, "Authentication token missing")
        return try {
            val (status, response) = AuthRepositoryImpl().updatePasswordOnly(token, pwd)
            if (status == 200 && response.success) Pair(true, null)
            else Pair(false, response.message ?: failedToChangePassword)
        } catch (e: Exception) { Pair(false, "Network error: ${e.message}") }
    }

    suspend fun loadUserData() {
        try {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val email = prefs.getString("user_email", null)
            val token = prefs.getString("jwt_token", null)
            if (email != null && token != null) {
                val repo = AuthRepositoryImpl()
                val name = repo.getUserName(email) ?: prefs.getString("user_name", "User") ?: "User"
                val devs = repo.getBoundDevices(email, token)
                prefs.edit { putString("user_name", name) }
                userName = name
                userEmail = email
                deviceCount = if (devs.success && devs.keys != null) devs.keys.size else 0
            } else {
                userName = "User"; userEmail = ""; deviceCount = 0
            }
        } catch (_: Exception) {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            userName = prefs.getString("user_name", "User") ?: "User"
            userEmail = prefs.getString("user_email", "") ?: ""
            deviceCount = 0
        } finally { isLoading = false }
    }

    suspend fun updateUserName(newName: String): Boolean {
        val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("jwt_token", null) ?: return false
        return try {
            val (status, response) = AuthRepositoryImpl().updateProfile(token, newName)
            if (status == 200 && response.success) {
                context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    .edit { putString("user_name", newName) }
                true
            } else false
        } catch (_: Exception) { false }
    }

    fun saveChanges() {
        if (isEditingName && tempName.isNotBlank() && tempName.length <= 25 && !isUpdatingName) {
            isUpdatingName = true
            scope.launch {
                val ok = updateUserName(tempName)
                isUpdatingName = false
                if (ok) {
                    userName = tempName; isEditingName = false
                    android.widget.Toast.makeText(context, nameUpdatedSuccessfully, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(context, failedToUpdateName, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (isChangingPassword && !isUpdatingPassword) {
            passwordError = when {
                currentPassword.isBlank() -> currentPasswordRequired
                newPassword.isBlank() -> newPasswordRequired
                confirmNewPassword.isBlank() -> pleaseConfirmPassword
                currentPassword.contains(" ") -> currentPasswordCannotContainSpaces
                newPassword.contains(" ") -> newPasswordCannotContainSpaces
                confirmNewPassword.contains(" ") -> confirmPasswordCannotContainSpaces
                newPassword.length < 8 -> passwordMustBe8Characters
                newPassword != confirmNewPassword -> passwordsDoNotMatch
                else -> null
            }
            if (passwordError != null) return

            isUpdatingPassword = true
            scope.launch {
                try {
                    if (!verifyCurrentPassword(userEmail, currentPassword)) {
                        passwordError = currentPasswordIncorrect; isUpdatingPassword = false; return@launch
                    }
                    val (ok, msg) = updatePasswordViaAPI(newPassword)
                    if (ok) {
                        android.widget.Toast.makeText(context, passwordChangedSuccessfully, android.widget.Toast.LENGTH_LONG).show()
                        currentPassword = ""; newPassword = ""; confirmNewPassword = ""
                        isChangingPassword = false; passwordError = null
                        delay(2000)
                        authViewModel.logout(); onLogoutClick()
                    } else {
                        passwordError = msg ?: failedToChangePassword
                    }
                } catch (e: Exception) {
                    passwordError = "Error: ${e.message}"
                } finally { isUpdatingPassword = false }
            }
        }
    }

    LaunchedEffect(Unit) { loadUserData() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.top_overlay), null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(profileSettings, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.wrapContentWidth().clickable { showLanguageDialog = true },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentLanguage == LocalizationManager.Language.ENGLISH) "🇬🇧 EN" else "🇵🇱 PL",
                                fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium
                            )
                            Icon(Icons.Default.ArrowDropDown, languageText, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Box(modifier = Modifier.wrapContentSize()) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Menu", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.wrapContentSize().background(Color.White, RoundedCornerShape(8.dp)),
                            offset = DpOffset(0.dp, 0.dp)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_logout),
                                            contentDescription = logoutText,
                                            tint = Color(0xFFD32F2F),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(logoutText, color = Color(0xFFD32F2F), fontWeight = FontWeight.Medium)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showLogoutDialog = true
                                }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(70.dp).clip(CircleShape).background(Color(0xFF4CAF50).copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userName.take(1).uppercase(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("$helloText $userName", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(youHaveDevices.replace("%d", deviceCount.toString()), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {

                            Text(fullNameText, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))

                            if (isEditingName) {
                                OutlinedTextField(
                                    value = tempName,
                                    onValueChange = { v ->
                                        val f = v.filter { it.isLetterOrDigit() || it.isWhitespace() }
                                        if (f.length <= 25) { tempName = f; nameError = null } else nameError = min25CharsError
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    isError = nameError != null,
                                    supportingText = {
                                        if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)
                                        else Text(max16CharsHint, fontSize = 10.sp, color = Color.Black)
                                    },
                                    singleLine = true,
                                    enabled = !isUpdatingName,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        disabledTextColor = Color.Black
                                    )
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(onClick = { isEditingName = false; tempName = userName; nameError = null }, enabled = !isUpdatingName) {
                                        Text(cancelText, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (tempName.isBlank()) nameError = nameCannotBeEmpty
                                            else if (tempName.length <= 25) saveChanges()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        enabled = !isUpdatingName
                                    ) {
                                        if (isUpdatingName) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                        else Text(saveText, color = Color.White)
                                    }
                                }
                            } else {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(userName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20))
                                    Text(editText, fontSize = 14.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable { tempName = userName; isEditingName = true; nameError = null })
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(emailText, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                            Text(userEmail, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20))

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(yourPasswordText, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                            Text("*********", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20))

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    if (!isUpdatingPassword) { isChangingPassword = !isChangingPassword; passwordError = null }
                                },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(setNewPasswordText, fontSize = 14.sp, color = Color.Gray)
                                Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
                            }

                            if (isChangingPassword) {
                                Spacer(modifier = Modifier.height(12.dp))

                                PasswordField(
                                    value = currentPassword,
                                    onValueChange = { currentPassword = filterPassword(it); if (passwordError?.contains("Current") == true) passwordError = null },
                                    label = currentPasswordText,
                                    isVisible = isCurrentPasswordVisible,
                                    onToggleVisibility = { isCurrentPasswordVisible = !isCurrentPasswordVisible },
                                    isError = passwordError != null && passwordError!!.contains("Current"),
                                    enabled = !isUpdatingPassword
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                PasswordField(
                                    value = newPassword,
                                    onValueChange = { newPassword = filterPassword(it); if (passwordError?.contains("New") == true || passwordError?.contains("8") == true) passwordError = null },
                                    label = newPasswordText,
                                    isVisible = isNewPasswordVisible,
                                    onToggleVisibility = { isNewPasswordVisible = !isNewPasswordVisible },
                                    isError = passwordError != null && (passwordError!!.contains("New") || passwordError!!.contains("8")),
                                    supportingText = if (passwordError != null && (passwordError!!.contains("New") || passwordError!!.contains("8")))
                                        passwordError else min8CharsNoSpaces,
                                    isErrorText = passwordError != null && (passwordError!!.contains("New") || passwordError!!.contains("8")),
                                    enabled = !isUpdatingPassword
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                PasswordField(
                                    value = confirmNewPassword,
                                    onValueChange = { confirmNewPassword = filterPassword(it); if (passwordError?.contains("match") == true || passwordError?.contains("Confirm") == true) passwordError = null },
                                    label = confirmNewPasswordText,
                                    isVisible = isConfirmPasswordVisible,
                                    onToggleVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                                    isError = passwordError != null && (passwordError!!.contains("match") || passwordError!!.contains("Confirm")),
                                    enabled = !isUpdatingPassword
                                )

                                if (passwordError != null &&
                                    !passwordError!!.contains("Current") &&
                                    !passwordError!!.contains("New") &&
                                    !passwordError!!.contains("match") &&
                                    !passwordError!!.contains("8")
                                ) {
                                    Text(passwordError!!, color = Color(0xFFD32F2F), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                }

                                if (isUpdatingPassword) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF4CAF50), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Changing password…", fontSize = 12.sp, color = Color(0xFF4CAF50))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { saveChanges() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                enabled = !isUpdatingName && !isUpdatingPassword
                            ) {
                                Text(saveSettingsText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(languageText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)) },
            text = {
                Column {
                    LanguageRow("🇬🇧", "English", currentLanguage == LocalizationManager.Language.ENGLISH) {
                        if (currentLanguage != LocalizationManager.Language.ENGLISH) changeLanguage(LocalizationManager.Language.ENGLISH)
                        showLanguageDialog = false
                    }
                    HorizontalDivider()
                    LanguageRow("🇵🇱", "Polski", currentLanguage == LocalizationManager.Language.POLISH) {
                        if (currentLanguage != LocalizationManager.Language.POLISH) changeLanguage(LocalizationManager.Language.POLISH)
                        showLanguageDialog = false
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text(cancelText) } },
            containerColor = Color.White, shape = RoundedCornerShape(16.dp)
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = logoutText,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = logoutText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            },
            text = {
                Text(
                    text = areYouSureLogout,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.logout()
                        onLogoutClick()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(logoutText, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(cancelText, color = Color(0xFF4CAF50))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false,
    supportingText: String? = null,
    isErrorText: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Black) },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility, enabled = enabled) {
                Icon(
                    painter = painterResource(if (isVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                    contentDescription = if (isVisible) "Hide password" else "Show password",
                    modifier = Modifier.size(24.dp), tint = Color(0xFF4CAF50)
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        supportingText = supportingText?.let {
            { Text(it, color = if (isErrorText) MaterialTheme.colorScheme.error else Color.Black, fontSize = if (isErrorText) 12.sp else 10.sp) }
        },
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black
        )
    )
}

@Composable
private fun LanguageRow(flag: String, name: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(flag, fontSize = 20.sp)
        Text(name, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Text("✓", color = Color(0xFF4CAF50), fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    SmartPlantBoxTheme { ProfileScreen(onLogoutClick = {}) }
}