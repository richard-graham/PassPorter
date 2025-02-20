package com.example.passporter.presentation.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passporter.domain.error.AuthError
import com.example.passporter.domain.usecase.auth.RegisterWithEmailUseCase
import com.example.passporter.domain.usecase.auth.SignInWithEmailUseCase
import com.example.passporter.domain.usecase.auth.SignInWithFacebookUseCase
import com.example.passporter.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithFacebookUseCase: SignInWithFacebookUseCase,
    private val registerWithEmailUseCase: RegisterWithEmailUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    fun onEmailSignIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            signInWithEmailUseCase(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(
                        message = error.message ?: "Sign in failed",
                        errorType = mapErrorType(error)
                    )
                }
        }
    }

    fun onGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            signInWithGoogleUseCase(idToken)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(
                        message = error.message ?: "Google sign in failed",
                        errorType = mapErrorType(error)
                    )
                }
        }
    }

    fun onFacebookSignIn(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            signInWithFacebookUseCase(accessToken)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(
                        message = error.message ?: "Facebook sign in failed",
                        errorType = mapErrorType(error)
                    )
                }
        }
    }

    fun onRegister(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String,
        preferredLanguage: String
    ) {
        viewModelScope.launch {
            _formState.value = formState.value.copy(isLoading = true)
            registerWithEmailUseCase(
                email = email,
                password = password,
                displayName = displayName,
                phoneNumber = phoneNumber,
                preferredLanguage = preferredLanguage
            ).onSuccess {
                _formState.value = formState.value.copy(isLoading = false)
            }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(
                        message = error.message ?: "Registration failed",
                        errorType = mapErrorType(error)
                    )
                }
        }
    }

    private fun updateFormState(update: AuthFormState.() -> AuthFormState) {
        _formState.value = update(_formState.value)
    }

    fun validateEmail(email: String) {
        val error = when {
            email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Invalid email format"

            else -> null
        }
        updateFormState { copy(email = email, emailError = error) }
    }

    fun validatePassword(password: String) {
        val error = when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.contains(Regex("[A-Z]")) -> "Password must contain an uppercase letter"
            !password.contains(Regex("[a-z]")) -> "Password must contain a lowercase letter"
            !password.contains(Regex("[0-9]")) -> "Password must contain a number"
            !password.contains(Regex("[^A-Za-z0-9]")) -> "Password must contain a special character"
            else -> null
        }
        updateFormState { copy(password = password, passwordError = error) }
    }

    fun updatePhoneNumber(number: String) {
        updateFormState { copy(phoneNumber = number) }
    }

    fun validatePhoneNumber(phone: String) {
        val error = when {
            phone.isNotBlank() && !android.util.Patterns.PHONE.matcher(phone).matches() ->
                "Invalid phone number format"

            else -> null
        }
        updateFormState { copy(phoneNumber = phone, phoneNumberError = error) }
    }

    private fun mapErrorType(error: Throwable): AuthErrorType = when (error) {
        is IllegalArgumentException -> AuthErrorType.VALIDATION_ERROR
        is AuthError.InvalidCredentials -> AuthErrorType.INVALID_CREDENTIALS
        is AuthError.NetworkError -> AuthErrorType.NETWORK_ERROR
        is AuthError.UserCollision -> AuthErrorType.USER_COLLISION
        else -> AuthErrorType.UNKNOWN
    }
}