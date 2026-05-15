package com.example.smartplantbox.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Initialize preferences and load saved credentials (call from composable)
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

    // Login form

    fun updateRememberMe(rememberMe: Boolean) {
        _loginUiState.value = _loginUiState.value.copy(rememberMe = rememberMe)
    }

    fun updateLoginEmail(email: String) {
        _loginUiState.value = _loginUiState.value.copy(
            email = email,
            emailError = ValidationUtils.validateEmail(email),
            errorMessage = null
        )
        validateLoginForm()
    }

    fun updateLoginPassword(password: String) {
        _loginUiState.value = _loginUiState.value.copy(
            password = password,
            passwordError = ValidationUtils.validatePassword(password),
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

    // Register form

    fun updateRegisterFullName(fullName: String) {
        _registerUiState.value = _registerUiState.value.copy(
            fullName = fullName,
            fullNameError = ValidationUtils.validateFullName(fullName),
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    fun updateRegisterEmail(email: String) {
        _registerUiState.value = _registerUiState.value.copy(
            email = email,
            emailError = ValidationUtils.validateEmail(email),
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    fun updateRegisterPassword(password: String) {
        _registerUiState.value = _registerUiState.value.copy(
            password = password,
            passwordError = ValidationUtils.validatePassword(password),
            errorMessage = null, successMessage = null
        )
        validateRegisterForm()
    }

    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerUiState.value = _registerUiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = ValidationUtils.validateConfirmPassword(
                _registerUiState.value.password, confirmPassword
            ),
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

    // Actions
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
                    401 -> setLoginError("Incorrect password")
                    404 -> setLoginError("User not found")
                    else -> setLoginError(response.message ?: "Login failed with status $status")
                }
            } catch (e: Exception) {
                _loginUiState.value = state.copy(isLoading = false, errorMessage = "Network error: ${e.message}")
                _authResult.value = AuthResult.Error("Network error: ${e.message}")
            }
        }
    }

    private fun setLoginError(msg: String) {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = msg)
        _authResult.value = AuthResult.Error(msg)
    }

    fun registerUser(onSuccess: () -> Unit) {
        val state = _registerUiState.value
        _authResult.value = AuthResult.Loading
        _registerUiState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.register(state.fullName, state.email, state.password)
                _registerUiState.value = state.copy(isLoading = false)

                when (status) {
                    201 if response.success -> {
                        _registerUiState.value = _registerUiState.value.copy(
                            successMessage = "Registration successful! Please login."
                        )
                        _authResult.value = AuthResult.Success
                        onSuccess()
                    }
                    409 -> setRegisterError("Email already exists")
                    400 -> setRegisterError("All fields must be filled in")
                    else -> setRegisterError(response.message ?: "Registration failed with status $status")
                }
            } catch (e: Exception) {
                _registerUiState.value = state.copy(isLoading = false, errorMessage = "Network error: ${e.message}")
                _authResult.value = AuthResult.Error("Network error: ${e.message}")
            }
        }
    }

    private fun setRegisterError(msg: String) {
        _registerUiState.value = _registerUiState.value.copy(errorMessage = msg)
        _authResult.value = AuthResult.Error(msg)
    }

    fun logout() {
        if (::userPreferences.isInitialized) userPreferences.clearRememberMeCredentials()
        _loginUiState.value = LoginUiState()
        _registerUiState.value = RegisterUiState()
    }

    fun clearAuthResult() { _authResult.value = null }

    fun clearMessages() {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = null)
        _registerUiState.value = _registerUiState.value.copy(errorMessage = null, successMessage = null)
    }
}