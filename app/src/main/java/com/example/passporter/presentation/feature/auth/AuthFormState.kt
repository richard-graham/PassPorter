package com.example.passporter.presentation.feature.auth

data class AuthFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val displayName: String = "",
    val displayNameError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val preferredLanguage: String = "",
    val preferredLanguageError: String? = null,
    val isLoading: Boolean = false,
    val isRegistrationMode: Boolean = false
) {
    val isValid: Boolean
        get() = when {
            isRegistrationMode -> {
                email.isNotBlank() && password.isNotBlank() &&
                        displayName.isNotBlank() && preferredLanguage.isNotBlank() &&
                        emailError == null && passwordError == null &&
                        displayNameError == null && phoneNumberError == null &&
                        preferredLanguageError == null
            }
            else -> {
                email.isNotBlank() && password.isNotBlank() &&
                        emailError == null && passwordError == null
            }
        }
}