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
            name.isBlank() -> "full_name_required"
            name.length > 25 -> "full_name_max_25"
            !name.all { it.isLetter() || it.isWhitespace() } -> "full_name_invalid"
            containsSqlInjection(name) -> "full_name_forbidden"
            containsDangerousChars(name) -> "full_name_invalid_chars"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "email_required"
            !allowedDomains.any { email.endsWith(it) } -> "email_invalid_domain"
            else -> {
                val domain = allowedDomains.find { email.endsWith(it) } ?: return "email_invalid_domain"
                val localPart = email.substringBefore(domain)
                when {
                    localPart.isBlank() -> "email_empty_local"
                    localPart.length > 25 -> "email_local_max_25"
                    !localPart.all { it.isLetterOrDigit() || it == '.' || it == '_' || it == '-' } -> "email_invalid_format"
                    containsSqlInjection(localPart) -> "email_forbidden"
                    else -> null
                }
            }
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "password_required"
            password.length < 8 -> "password_min_8"
            password.length > 25 -> "password_max_25"
            !password.any { it.isDigit() } -> "password_need_digit"
            containsSqlInjection(password) -> "password_forbidden"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return if (password != confirmPassword) {
            "passwords_do_not_match"
        } else null
    }
}