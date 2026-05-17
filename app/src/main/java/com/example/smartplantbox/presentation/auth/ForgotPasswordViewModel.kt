package com.example.smartplantbox.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartplantbox.R
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.domain.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordEmailState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

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

    private var savedEmail: String = ""
    private var savedCode: String = ""


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
                    val successMsg = getLocalizedMessage(context, "code_sent", state.email)
                    _emailState.value = state.copy(
                        isLoading = false,
                        successMessage = successMsg,
                        errorMessage = null
                    )
                    onSuccess()
                } else {
                    val errorMsg = getLocalizedErrorMessage(context, status, response.message)
                    _emailState.value = state.copy(isLoading = false, errorMessage = errorMsg, successMessage = null)
                }
            } catch (e: Exception) {
                val errorMsg = getLocalizedMessage(context, "network_error", e.message ?: "")
                _emailState.value = state.copy(isLoading = false, errorMessage = errorMsg, successMessage = null)
            }
        }
    }

    fun startCodeTimer() {
        if (!_codeState.value.canResend) {
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

    fun verifyCode(context: Context, onSuccess: () -> Unit) {
        val state = _codeState.value
        val code = "${state.code1}${state.code2}${state.code3}${state.code4}"
        savedCode = code
        _codeState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.verifyResetCode(savedEmail, code)
                if (response.success) {
                    val successMsg = getLocalizedMessage(context, "code_verified")
                    _codeState.value = state.copy(
                        isLoading = false,
                        successMessage = successMsg,
                        errorMessage = null,
                        codeError = null
                    )
                    onSuccess()
                } else {
                    val errorMsg = getLocalizedMessage(context, "invalid_code")
                    _codeState.value = state.copy(
                        isLoading = false,
                        codeError = errorMsg,
                        errorMessage = errorMsg,
                        successMessage = null
                    )
                }
            } catch (e: Exception) {
                val errorMsg = getLocalizedMessage(context, "network_error", e.message ?: "")
                _codeState.value = state.copy(isLoading = false, errorMessage = errorMsg, successMessage = null)
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
                    val successMsg = getLocalizedMessage(context, "code_resent")
                    _codeState.value = _codeState.value.copy(
                        isLoading = false,
                        code1 = "", code2 = "", code3 = "", code4 = "",
                        successMessage = successMsg,
                        errorMessage = null
                    )
                    startResendTimer()
                } else {
                    val errorMsg = getLocalizedMessage(context, "resend_failed", response.message ?: "")
                    _codeState.value = _codeState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg,
                        successMessage = null
                    )
                }
            } catch (e: Exception) {
                val errorMsg = getLocalizedMessage(context, "network_error", e.message ?: "")
                _codeState.value = _codeState.value.copy(isLoading = false, errorMessage = errorMsg, successMessage = null)
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


    fun updateNewPassword(password: String) {
        val currentState = _newPasswordState.value
        _newPasswordState.value = currentState.copy(
            newPassword = password,
            newPasswordError = ValidationUtils.validatePassword(password),
            confirmPasswordError = validateConfirmPasswordMatch(password, currentState.confirmPassword),
            errorMessage = null,
            successMessage = null
        )
        validateNewPasswordForm()
    }

    fun updateConfirmPassword(confirmPassword: String) {
        val currentState = _newPasswordState.value
        _newPasswordState.value = currentState.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPasswordMatch(currentState.newPassword, confirmPassword),
            errorMessage = null,
            successMessage = null
        )
        validateNewPasswordForm()
    }

    private fun validateConfirmPasswordMatch(password: String, confirmPassword: String): String? {
        return if (password != confirmPassword) {
            "Passwords do not match"
        } else {
            ValidationUtils.validateConfirmPassword(password, confirmPassword)
        }
    }

    private fun validateNewPasswordForm() {
        val s = _newPasswordState.value
        _newPasswordState.value = s.copy(
            isFormValid = s.newPasswordError == null && s.confirmPasswordError == null &&
                    s.newPassword.isNotBlank() && s.confirmPassword.isNotBlank()
        )
    }

    fun changePassword(context: Context, onSuccess: () -> Unit) {
        val state = _newPasswordState.value
        _newPasswordState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val (status, response) = repository.changePassword(savedEmail, savedCode, state.newPassword)
                if (response.success) {
                    val successMsg = getLocalizedMessage(context, "password_changed")
                    _newPasswordState.value = state.copy(
                        isLoading = false,
                        successMessage = successMsg,
                        errorMessage = null
                    )
                    onSuccess()
                } else {
                    val errorMsg = getLocalizedErrorMessage(context, status, response.message)
                    _newPasswordState.value = state.copy(isLoading = false, errorMessage = errorMsg, successMessage = null)
                }
            } catch (e: Exception) {
                val errorMsg = getLocalizedMessage(context, "network_error", e.message ?: "")
                _newPasswordState.value = state.copy(isLoading = false, errorMessage = errorMsg, successMessage = null)
            }
        }
    }

    fun resetEmailState() {
        _emailState.value = ForgotPasswordEmailState()
    }

    fun resetCodeState() {
        _codeState.value = ForgotPasswordCodeState()
    }

    fun resetNewPasswordState() {
        _newPasswordState.value = ForgotPasswordNewPasswordState()
    }

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

    private fun getLocalizedMessage(context: Context, key: String, detail: String = ""): String {
        return when (key) {
            "code_sent" -> context.getString(R.string.forgot_code_sent, detail)
            "code_verified" -> context.getString(R.string.forgot_code_verified)
            "code_resent" -> context.getString(R.string.forgot_code_resent)
            "invalid_code" -> context.getString(R.string.forgot_invalid_code)
            "resend_failed" -> context.getString(R.string.forgot_resend_failed)
            "password_changed" -> context.getString(R.string.forgot_password_changed)
            "network_error" -> context.getString(R.string.error_network) + if (detail.isNotEmpty()) ": $detail" else ""
            else -> detail
        }
    }

    private fun getLocalizedErrorMessage(context: Context, status: Int, message: String?): String {
        return when (status) {
            404 -> context.getString(R.string.error_user_not_found)
            400 -> context.getString(R.string.forgot_invalid_code)
            500 -> context.getString(R.string.forgot_server_error)
            else -> message ?: context.getString(R.string.forgot_request_failed)
        }
    }
}