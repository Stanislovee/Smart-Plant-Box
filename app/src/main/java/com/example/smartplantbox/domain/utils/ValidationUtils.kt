package com.example.smartplantbox.domain.utils

object ValidationUtils {

    private val sqlBlacklist = listOf(
        "drop", "delete", "insert", "update", "alter", "create",
        "truncate", "exec", "execute", "union", "select", "from",
        "where", "table", "database", "script", "javascript", "<script",
        "DROP TABLE", "DELETE FROM", "INSERT INTO", "--"
    )
    private val dangerousChars = listOf(
        "'", "\"", ";", "--", "/*", "*/", "=", ">", "<", "(", ")"
    )
    private val allowedDomains = listOf("@gmail.com",)
    fun containsSqlInjection(input: String): Boolean {
        val lowerInput = input.lowercase()
        return sqlBlacklist.any { lowerInput.contains(it) }
    }
    fun containsDangerousChars(input: String): Boolean {
        return dangerousChars.any { input.contains(it) }
    }
    fun validateFullName(name: String): String? {
        return when {
            name.isBlank() -> "Full name is required"
            name.length > 25 -> "Full name must not exceed 25 characters"
            !name.all { it.isLetter() || it.isWhitespace() } -> "Full name can only contain letters and spaces"
            containsSqlInjection(name) -> "Name contains forbidden words"
            containsDangerousChars(name) -> "Name contains invalid characters"
            else -> null
        }
    }
    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !allowedDomains.any { email.endsWith(it) } -> "Email must be @gmail.com"
            else -> {
                val domain = allowedDomains.find { email.endsWith(it) } ?: return "Email must be valid"
                val localPart = email.substringBefore(domain)
                when {
                    localPart.isBlank() -> "Email local part cannot be empty"
                    localPart.length > 25 -> "Email local part must not exceed 25 characters"
                    !localPart.all { it.isLetterOrDigit() || it == '.' || it == '_' || it == '-' } ->
                        "Email can only contain letters, numbers, and . _ -"
                    containsSqlInjection(localPart) -> "Email contains forbidden words"
                    else -> null
                }
            }
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            password.length > 25 -> "Password must not exceed 25 characters"
            containsSqlInjection(password) -> "Password contains forbidden words"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return if (password != confirmPassword) {
            "Passwords do not match"
        } else null
    }
}