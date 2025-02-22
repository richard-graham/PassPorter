package com.example.passporter.presentation.feature.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.passporter.R
import com.example.passporter.presentation.feature.auth.components.RegisterForm
import com.example.passporter.presentation.feature.auth.components.SignInForm

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val resetPasswordState by viewModel.resetPasswordState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo and welcome text
        Spacer(modifier = Modifier.height(48.dp))
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Pass Porter Login",
            style = MaterialTheme.typography.headlineMedium
        )

        // Email sign-in/register form
        Spacer(modifier = Modifier.height(32.dp))
        var isRegistering by remember { mutableStateOf(false) }

        AnimatedContent(
            targetState = isRegistering,
            label = "auth_form"
        ) { registering ->
            if (registering) {
                RegisterForm(
                    onEmailChange = viewModel::validateEmail,
                    formState = formState,
                    onPasswordChange = viewModel::validatePassword,
                    onRegister = viewModel::onRegister,
                    onNavigateToSignIn = { isRegistering = false }
                )
            } else {
                SignInForm(
                    onSignIn = viewModel::onEmailSignIn,
                    onNavigateToRegister = { isRegistering = true },
                    onForgotPassword = viewModel::onResetPassword,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState is AuthUiState.Loading,
                    resetPasswordState = resetPasswordState,
                    onResetPasswordStateHandled = viewModel::resetPasswordState
                )
            }
        }
    }

    // Handle UI states
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onAuthSuccess()
            is AuthUiState.Error -> {
                // Show error snackbar using scaffold state
            }
            else -> Unit
        }
    }
}