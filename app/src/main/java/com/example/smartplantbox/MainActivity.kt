package com.example.smartplantbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.smartplantbox.ui.auth.forgot.ForgotPasswordCodeScreen
import com.example.smartplantbox.ui.auth.forgot.ForgotPasswordEmailScreen
import com.example.smartplantbox.ui.auth.forgot.ForgotPasswordNewPasswordScreen
import com.example.smartplantbox.ui.auth.login.LoginScreen
import com.example.smartplantbox.ui.auth.register.RegisterScreen
import com.example.smartplantbox.ui.main.MainScreen
import com.example.smartplantbox.ui.main.potsettings.PotSettingsScreen
import com.example.smartplantbox.ui.splash.SplashScreen
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import com.example.smartplantbox.ui.welcome.WelcomeScreen
import com.example.smartplantbox.utils.LocalizationManager

class MainActivity : ComponentActivity() {

    enum class Screen {
        Splash, Welcome, Login, Register, Main,
        ForgotPasswordEmail, ForgotPasswordCode, ForgotPasswordNewPassword,
        PotSettings
    }
    private var currentDeviceName = ""
    private var currentDeviceSN = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Save language
        LocalizationManager.applySavedLanguage(this)

        // is this first start?
        val isFirstLaunch = LocalizationManager.isFirstLaunch(this)

        setContent {
            SmartPlantBoxTheme {
                var currentScreen by remember { mutableStateOf(Screen.Splash) }

                when (currentScreen) {
                    Screen.Splash -> {
                        SplashScreen {

                            currentScreen = if (isFirstLaunch) {
                                Screen.Welcome
                            } else {
                                Screen.Login
                            }
                        }
                    }
                    Screen.Welcome -> {
                        WelcomeScreen(
                            onGetStartedClick = {
                                // Note, that fist start has been
                                LocalizationManager.setFirstLaunchCompleted(this@MainActivity)
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    Screen.Login -> {
                        LoginScreen(
                            onLoginClick = {
                                currentScreen = Screen.Main
                            },
                            onSignUpClick = {
                                currentScreen = Screen.Register
                            },
                            onForgotPasswordClick = {
                                currentScreen = Screen.ForgotPasswordEmail
                            }
                        )
                    }
                    Screen.Register -> {
                        RegisterScreen(
                            onRegisterClick = {
                                currentScreen = Screen.Main
                            },
                            onLoginClick = {
                                currentScreen = Screen.Login
                            },
                            onBackClick = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    Screen.ForgotPasswordEmail -> {
                        ForgotPasswordEmailScreen(
                            onBackClick = {
                                currentScreen = Screen.Login
                            },
                            onContinueClick = {
                                currentScreen = Screen.ForgotPasswordCode
                            }
                        )
                    }
                    Screen.ForgotPasswordCode -> {
                        ForgotPasswordCodeScreen(
                            onBackClick = {
                                currentScreen = Screen.ForgotPasswordEmail
                            },
                            onCodeVerified = {
                                currentScreen = Screen.ForgotPasswordNewPassword
                            }
                        )
                    }
                    Screen.ForgotPasswordNewPassword -> {
                        ForgotPasswordNewPasswordScreen(
                            onBackClick = {
                                currentScreen = Screen.ForgotPasswordCode
                            },
                            onPasswordChanged = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    Screen.Main -> {
                        MainScreen(
                            onLogout = {
                                currentScreen = Screen.Login
                            },
                            onNavigateToPotSettings = { deviceName, deviceSN ->
                                currentDeviceName = deviceName
                                currentDeviceSN = deviceSN
                                currentScreen = Screen.PotSettings
                            }
                        )
                    }
                    Screen.PotSettings -> {
                        PotSettingsScreen(
                            onBackClick = {
                                currentScreen = Screen.Main
                            },
                            onSettingsSaved = {
                                currentScreen = Screen.Main
                            },
                            deviceName = currentDeviceName,
                            deviceSN = currentDeviceSN
                        )
                    }
                }
            }
        }
    }
}