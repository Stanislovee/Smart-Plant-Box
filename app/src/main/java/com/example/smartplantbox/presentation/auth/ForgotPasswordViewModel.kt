package com.example.smartplantbox.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.domain.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State for screen 1 — email input
data class ForgotPasswordEmailState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

// State for screen 2 — code input
data class ForgotPasswordCodeState(
    val code1: String = "",
    val code2: String = "",
    val code3: String = "",
    val code4: String = "",
    val codeError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val timerSeconds: Int = 60,
    val canResend: Boolean = false
)

// State for screen 3 — new password input
data class ForgotPasswordNewPasswordState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
class ForgotPasswordViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()

    private val _emailState = MutableStateFlow(ForgotPasswordEmailState())
    val emailState: StateFlow<ForgotPasswordEmailState> = _emailState.asStateFlow()

    private val _codeState = MutableStateFlow(ForgotPasswordCodeState())
    val codeState: StateFlow<ForgotPasswordCodeState> = _codeState.asStateFlow()

    private val _newPasswordState = MutableStateFlow(ForgotPasswordNewPasswordState())
    val newPasswordState: StateFlow<ForgotPasswordNewPasswordState> = _newPasswordState.asStateFlow()

    // Shared between screens
    private var savedEmail: String = ""
    private var savedCode: String = ""

    // Screen 1: Email

    fun updateEmail(email: String) {
        _emailState.value = _emailState.value.copy(
            email = email,
            emailError = ValidationUtils.validateEmail(email),
            errorMessage = null, successMessage = null
        )
        _emailState.value = _emailState.value.copy(
            isFormValid = _emailState.value.emailError == null && email.isNotBlank()
        )
    }

    fun sendResetCode(context: Context, onSuccess: () -> Unit) {
        val state = _emailState.value
        savedEmail = state.email
        _emailState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.requestPasswordReset(state.email)
                if (response.success) {
                    _emailState.value = state.copy(isLoading = false, successMessage = "Code sent to ${state.email}")
                    onSuccess()
                } else {
                    val msg = when (status) {
                        404 -> "User not found"
                        500 -> "Server error: ${response.message}"
                        else -> response.message ?: "Failed to send code"
                    }
                    _emailState.value = state.copy(isLoading = false, errorMessage = msg)
                }
            } catch (e: Exception) {
                _emailState.value = state.copy(isLoading = false, errorMessage = "Network error: ${e.message}")
            }
        }
    }

    // Screen 2: Code
    // Start countdown timer
    fun startCodeTimer() {
        if (_codeState.value.timerSeconds == 60 && !_codeState.value.canResend) {
            startResendTimer()
        }
    }

    fun updateCode(index: Int, value: String) {
        if (value.isNotEmpty() && !value.all { it.isDigit() }) return
        val digit = if (value.length > 1) value.last().toString() else value
        val s = _codeState.value
        _codeState.value = when (index) {
            1 -> s.copy(code1 = digit)
            2 -> s.copy(code2 = digit)
            3 -> s.copy(code3 = digit)
            4 -> s.copy(code4 = digit)
            else -> s
        }.copy(codeError = null, errorMessage = null, successMessage = null)

        val updated = _codeState.value
        _codeState.value = updated.copy(
            isFormValid = updated.code1.isNotBlank() && updated.code2.isNotBlank() &&
                    updated.code3.isNotBlank() && updated.code4.isNotBlank()
        )
    }

    fun verifyCode(onSuccess: () -> Unit) {
        val state = _codeState.value
        val code = "${state.code1}${state.code2}${state.code3}${state.code4}"
        savedCode = code
        _codeState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.verifyResetCode(savedEmail, code)
                if (response.success) {
                    _codeState.value = state.copy(isLoading = false, successMessage = "Code verified!")
                    onSuccess()
                } else {
                    val msg = if (status == 400) "Invalid code" else response.message ?: "Verification failed"
                    _codeState.value = state.copy(isLoading = false, codeError = msg, errorMessage = msg)
                }
            } catch (e: Exception) {
                _codeState.value = state.copy(isLoading = false, errorMessage = "Network error: ${e.message}")
            }
        }
    }

    fun resendCode(context: Context) {
        if (!_codeState.value.canResend) return
        _codeState.value = _codeState.value.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (_, response) = repository.requestPasswordReset(savedEmail)
                if (response.success) {
                    _codeState.value = _codeState.value.copy(
                        isLoading = false, code1 = "", code2 = "", code3 = "", code4 = "",
                        successMessage = "New code sent to your email"
                    )
                    startResendTimer()
                } else {
                    _codeState.value = _codeState.value.copy(
                        isLoading = false, errorMessage = response.message ?: "Failed to resend code"
                    )
                }
            } catch (e: Exception) {
                _codeState.value = _codeState.value.copy(isLoading = false, errorMessage = "Network error: ${e.message}")
            }
        }
    }
    private fun startResendTimer() {
        _codeState.value = _codeState.value.copy(timerSeconds = 60, canResend = false)
        viewModelScope.launch {
            for (i in 60 downTo 1) {
                kotlinx.coroutines.delay(1000)
                _codeState.value = _codeState.value.copy(timerSeconds = i - 1, canResend = i <= 1)
            }
        }
    }
    // Screen 3: New password
    fun updateNewPassword(password: String) {
        _newPasswordState.value = _newPasswordState.value.copy(
            newPassword = password,
            newPasswordError = ValidationUtils.validatePassword(password),
            errorMessage = null, successMessage = null
        )
        validateNewPasswordForm()
    }
    fun updateConfirmPassword(confirmPassword: String) {
        _newPasswordState.value = _newPasswordState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = ValidationUtils.validateConfirmPassword(
                _newPasswordState.value.newPassword, confirmPassword
            ),
            errorMessage = null, successMessage = null
        )
        validateNewPasswordForm()
    }
    private fun validateNewPasswordForm() {
        val s = _newPasswordState.value
        _newPasswordState.value = s.copy(
            isFormValid = s.newPasswordError == null && s.confirmPasswordError == null &&
                    s.newPassword.isNotBlank() && s.confirmPassword.isNotBlank()
        )
    }
    fun changePassword(onSuccess: () -> Unit) {
        val state = _newPasswordState.value
        _newPasswordState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.changePassword(savedEmail, savedCode, state.newPassword)
                if (response.success) {
                    _newPasswordState.value = state.copy(
                        isLoading = false,
                        successMessage = "Password changed successfully! You can now login."
                    )
                    onSuccess()
                } else {
                    val msg = when (status) {
                        400 -> "Invalid code or data"
                        500 -> "Server error"
                        else -> response.message ?: "Failed to change password"
                    }
                    _newPasswordState.value = state.copy(isLoading = false, errorMessage = msg)
                }
            } catch (e: Exception) {
                _newPasswordState.value = state.copy(isLoading = false, errorMessage = "Network error: ${e.message}")
            }
        }
    }
    // Helpers
    fun getSavedEmail(): String = savedEmail

    fun clearMessages() {
        _emailState.value = _emailState.value.copy(errorMessage = null, successMessage = null)
        _codeState.value = _codeState.value.copy(errorMessage = null, successMessage = null)
        _newPasswordState.value = _newPasswordState.value.copy(errorMessage = null, successMessage = null)
    }

    fun resetToEmailScreen() {
        savedEmail = ""; savedCode = ""
        _emailState.value = ForgotPasswordEmailState()
        _codeState.value = ForgotPasswordCodeState()
        _newPasswordState.value = ForgotPasswordNewPasswordState()
    }
}