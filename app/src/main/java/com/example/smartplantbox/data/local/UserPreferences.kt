package com.example.smartplantbox.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EMAIL = "saved_email"
        private const val KEY_PASSWORD = "saved_password"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    // Save credentials for Remember Me feature
    fun saveRememberMeCredentials(email: String, password: String, rememberMe: Boolean) {
        prefs.edit().apply {
            putString(KEY_EMAIL, if (rememberMe) email else null)
            putString(KEY_PASSWORD, if (rememberMe) password else null)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            apply()
        }
    }

    fun getSavedEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getSavedPassword(): String? = prefs.getString(KEY_PASSWORD, null)

    fun isRememberMeEnabled(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)

    // Clear saved credentials on logout
    fun clearRememberMeCredentials() {
        prefs.edit().apply {
            remove(KEY_EMAIL)
            remove(KEY_PASSWORD)
            putBoolean(KEY_REMEMBER_ME, false)
            apply()
        }
    }
}