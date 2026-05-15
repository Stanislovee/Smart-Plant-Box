package com.example.smartplantbox.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale
import androidx.core.content.edit

object LocalizationManager {

    enum class Language(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        POLISH("pl", "Polski")
    }

    private const val PREF_LANGUAGE = "selected_language"
    private const val PREF_FIRST_LAUNCH = "first_launch"
    private const val DEFAULT_LANGUAGE_CODE = "pl"

    fun getCurrentLanguage(context: Context): Language {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString(PREF_LANGUAGE, DEFAULT_LANGUAGE_CODE)
        return when (langCode) {
            "en" -> Language.ENGLISH
            "pl" -> Language.POLISH
            else -> Language.POLISH
        }
    }

    fun getCurrentLanguageCode(context: Context): String {
        return getCurrentLanguage(context).code
    }

    fun setLanguage(context: Context, language: Language) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString(PREF_LANGUAGE, language.code) }
        setAppLocale(context, language.code)
    }

    fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }

    fun applySavedLanguage(context: Context) {
        val languageCode = getCurrentLanguageCode(context)
        setAppLocale(context, languageCode)
    }

    fun isFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit { putBoolean(PREF_FIRST_LAUNCH, false) }
    }
}