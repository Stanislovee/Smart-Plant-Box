package com.example.smartplantbox.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartplantbox.R
import com.example.smartplantbox.data.local.UserPreferences
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.domain.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val rememberMe: Boolean = false
)

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val agreeToTerms: Boolean = false,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()
    private lateinit var userPreferences: UserPreferences

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult.asStateFlow()

    fun initPreferences(context: Context) {
        if (!::userPreferences.isInitialized) {
            userPreferences = UserPreferences(context)
            loadSavedCredentials()
        }
    }

    private fun loadSavedCredentials() {
        if (::userPreferences.isInitialized && userPreferences.isRememberMeEnabled()) {
            _loginUiState.value = _loginUiState.value.copy(
                email = userPreferences.getSavedEmail() ?: "",
                password = userPreferences.getSavedPassword() ?: "",
                rememberMe = true
            )
            validateLoginForm()
        }
    }

    fun updateRememberMe(rememberMe: Boolean) {
        _loginUiState.value = _loginUiState.value.copy(rememberMe = rememberMe)
    }

    fun updateLoginEmail(email: String, context: Context) {
        val errorCode = ValidationUtils.validateEmail(email)
        val errorText = errorCode?.let { getLocalizedValidationError(it, context) }
        _loginUiState.value = _loginUiState.value.copy(
            email = email,
            emailError = errorText,
            errorMessage = null
        )
        validateLoginForm()
    }

    fun updateLoginPassword(password: String, context: Context) {
        val errorCode = ValidationUtils.validatePassword(password)
        val errorText = errorCode?.let { getLocalizedValidationError(it, context) }
        _loginUiState.value = _loginUiState.value.copy(
            password = password,
            passwordError = errorText,
            errorMessage = null
        )
        validateLoginForm()
    }

    private fun validateLoginForm() {
        val s = _loginUiState.value
        _loginUiState.value = s.copy(
            isFormValid = s.emailError == null && s.passwordError == null &&
                    s.email.isNotBlank() && s.password.isNotBlank()
        )
    }

    fun updateRegisterFullName(fullName: String, context: Context) {
        val errorCode = ValidationUtils.validateFullName(fullName)
        val errorText = errorCode?.let { getLocalizedValidationError(it, context) }
        _registerUiState.value = _registerUiState.value.copy(
            fullName = fullName,
            fullNameError = errorText,
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    fun updateRegisterEmail(email: String, context: Context) {
        val errorCode = ValidationUtils.validateEmail(email)
        val errorText = errorCode?.let { getLocalizedValidationError(it, context) }
        _registerUiState.value = _registerUiState.value.copy(
            email = email,
            emailError = errorText,
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    fun updateRegisterPassword(password: String, context: Context) {
        val errorCode = ValidationUtils.validatePassword(password)
        val errorText = errorCode?.let { getLocalizedValidationError(it, context) }
        val currentState = _registerUiState.value
        _registerUiState.value = currentState.copy(
            password = password,
            passwordError = errorText,
            confirmPasswordError = if (currentState.confirmPassword.isNotEmpty()) {
                val confirmErrorCode = ValidationUtils.validateConfirmPassword(password, currentState.confirmPassword)
                confirmErrorCode?.let { getLocalizedValidationError(it, context) }
            } else null,
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    fun updateRegisterConfirmPassword(confirmPassword: String, context: Context) {
        val currentState = _registerUiState.value
        val errorCode = ValidationUtils.validateConfirmPassword(currentState.password, confirmPassword)
        val errorText = errorCode?.let { getLocalizedValidationError(it, context) }
        _registerUiState.value = currentState.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = errorText,
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    fun updateAgreeToTerms(agree: Boolean) {
        _registerUiState.value = _registerUiState.value.copy(
            agreeToTerms = agree,
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    private fun validateRegisterForm() {
        val s = _registerUiState.value
        _registerUiState.value = s.copy(
            isFormValid = s.fullNameError == null && s.emailError == null &&
                    s.passwordError == null && s.confirmPasswordError == null &&
                    s.fullName.isNotBlank() && s.email.isNotBlank() &&
                    s.password.isNotBlank() && s.confirmPassword.isNotBlank() &&
                    s.agreeToTerms
        )
    }

    fun loginUser(context: Context, onSuccess: () -> Unit) {
        val state = _loginUiState.value
        _authResult.value = AuthResult.Loading
        _loginUiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.login(state.email, state.password, context)
                _loginUiState.value = state.copy(isLoading = false)

                when (status) {
                    200 if response.success -> {
                        if (::userPreferences.isInitialized) {
                            userPreferences.saveRememberMeCredentials(
                                email = state.email,
                                password = if (state.rememberMe) state.password else "",
                                rememberMe = state.rememberMe
                            )
                        }
                        _authResult.value = AuthResult.Success
                        onSuccess()
                    }
                    401 -> {
                        val errorMsg = context.getString(R.string.error_incorrect_password)
                        setLoginError(errorMsg)
                    }
                    404 -> {
                        val errorMsg = context.getString(R.string.error_user_not_found)
                        setLoginError(errorMsg)
                    }
                    else -> {
                        val errorMsg = context.getString(R.string.error_login_failed) + ": " + (response.message ?: "Status $status")
                        setLoginError(errorMsg)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = context.getString(R.string.error_network) + ": " + (e.message ?: "")
                _loginUiState.value = state.copy(isLoading = false, errorMessage = errorMsg)
                _authResult.value = AuthResult.Error(errorMsg)
            }
        }
    }

    private fun setLoginError(msg: String) {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = msg)
        _authResult.value = AuthResult.Error(msg)
    }

    fun registerUser(context: Context, onSuccess: () -> Unit) {
        val state = _registerUiState.value
        _authResult.value = AuthResult.Loading
        _registerUiState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.register(state.fullName, state.email, state.password)
                _registerUiState.value = state.copy(isLoading = false)

                when (status) {
                    201 -> {
                        val successMsg = context.getString(R.string.registration_success)
                        _registerUiState.value = _registerUiState.value.copy(
                            successMessage = successMsg,
                            errorMessage = null
                        )
                        _authResult.value = AuthResult.Success
                        onSuccess()
                    }
                    409 -> {
                        val errorMsg = context.getString(R.string.error_email_exists)
                        setRegisterError(errorMsg)
                    }
                    400 -> {
                        val errorMsg = context.getString(R.string.error_fields_required)
                        setRegisterError(errorMsg)
                    }
                    else -> {
                        val errorMsg = context.getString(R.string.error_registration_failed) + ": " + (response.message ?: "Status $status")
                        setRegisterError(errorMsg)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = context.getString(R.string.error_network) + ": " + (e.message ?: "")
                _registerUiState.value = state.copy(isLoading = false, errorMessage = errorMsg, successMessage = null)
                _authResult.value = AuthResult.Error(errorMsg)
            }
        }
    }

    private fun setRegisterError(msg: String) {
        _registerUiState.value = _registerUiState.value.copy(errorMessage = msg, successMessage = null)
        _authResult.value = AuthResult.Error(msg)
    }

    fun logout(context: Context) {
        if (::userPreferences.isInitialized) userPreferences.clearRememberMeCredentials()
        _loginUiState.value = LoginUiState()
        _registerUiState.value = RegisterUiState()
    }

    fun clearAuthResult() { _authResult.value = null }

    fun clearMessages() {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = null)
        _registerUiState.value = _registerUiState.value.copy(errorMessage = null, successMessage = null)
    }

    private fun getLocalizedValidationError(errorCode: String, context: Context): String {
        return when (errorCode) {
            // Full name errors
            "full_name_required" -> context.getString(R.string.error_full_name_required)
            "full_name_max_25" -> context.getString(R.string.error_full_name_max_25)
            "full_name_invalid" -> context.getString(R.string.error_full_name_invalid)
            "full_name_forbidden" -> context.getString(R.string.error_full_name_forbidden)
            "full_name_invalid_chars" -> context.getString(R.string.error_full_name_invalid_chars)
            // Email errors
            "email_required" -> context.getString(R.string.error_email_required)
            "email_invalid_domain" -> context.getString(R.string.error_email_invalid_domain)
            "email_empty_local" -> context.getString(R.string.error_email_empty_local)
            "email_local_max_25" -> context.getString(R.string.error_email_local_max_25)
            "email_invalid_format" -> context.getString(R.string.error_email_invalid_format)
            "email_forbidden" -> context.getString(R.string.error_email_forbidden)
            // Password errors
            "password_required" -> context.getString(R.string.error_password_required)
            "password_min_8" -> context.getString(R.string.error_password_min_8)
            "password_max_25" -> context.getString(R.string.error_password_max_25)
            "password_need_digit" -> context.getString(R.string.error_password_need_digit)
            "password_forbidden" -> context.getString(R.string.error_password_forbidden)
            // Confirm password
            "passwords_do_not_match" -> context.getString(R.string.error_passwords_do_not_match)
            else -> errorCode
        }
    }
}