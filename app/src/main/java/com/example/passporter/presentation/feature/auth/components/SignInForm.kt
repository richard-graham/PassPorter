package com.example.passporter.presentation.feature.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.passporter.presentation.feature.auth.ResetPasswordState

@Composable
fun SignInForm(
    onSignIn: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    resetPasswordState: ResetPasswordState = ResetPasswordState.Initial,
    onResetPasswordStateHandled: () -> Unit = {}
) {
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(resetPasswordState) {
        when (resetPasswordState) {
            is ResetPasswordState.Success -> {
                showForgotPasswordDialog = false
                snackbarHostState.showSnackbar(
                    "Password reset link sent to your email",
                    duration = SnackbarDuration.Short
                )
                onResetPasswordStateHandled()
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSignIn(email, password)
                }
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { showForgotPasswordDialog = true },
                enabled = !isLoading
            ) {
                Text("Forgot Password?")
            }
        }

        Button(
            onClick = { onSignIn(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign In")
            }
        }

        TextButton(
            onClick = onNavigateToRegister,
            enabled = !isLoading
        ) {
            Text("Don't have an account? Sign up")
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                onDismiss = {
                    showForgotPasswordDialog = false
                    onResetPasswordStateHandled()
                },
                onSubmit = onForgotPassword,
                isLoading = resetPasswordState is ResetPasswordState.Loading,
                errorMessage = (resetPasswordState as? ResetPasswordState.Error)?.message
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(16.dp)
        )
    }
}
