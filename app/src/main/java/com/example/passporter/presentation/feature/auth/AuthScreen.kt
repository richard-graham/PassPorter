package com.example.passporter.presentation.feature.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.passporter.R
import com.example.passporter.presentation.feature.auth.components.RegisterForm
import com.example.passporter.presentation.feature.auth.components.SignInForm
import com.example.passporter.presentation.feature.auth.components.SocialSignInButton
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val webClientId = stringResource(R.string.default_web_client_id)
    val localContext = LocalContext.current

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

        // Social sign-in buttons
        Spacer(modifier = Modifier.height(48.dp))
        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account?.idToken?.let { token ->
                        viewModel.onGoogleSignIn(token)
                    }
                } catch (e: ApiException) {
                    // Handle sign in failure
                }
            }
        }

        SocialSignInButton(
            text = "Continue with Google",
            icon = R.drawable.ic_google,
            onClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(localContext, gso)
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            },
            isLoading = uiState is AuthUiState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))
        val callbackManager = remember { CallbackManager.Factory.create() }
        val facebookSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            callbackManager.onActivityResult(
                result.resultCode,
                result.resultCode,
                result.data
            )
        }

        SocialSignInButton(
            text = "Continue with Facebook",
            icon = R.drawable.ic_facebook,
            onClick = {
                LoginManager.getInstance().logInWithReadPermissions(
                    localContext as Activity,
                    listOf("email", "public_profile")
                )
                LoginManager.getInstance().registerCallback(
                    callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult) {
                            viewModel.onFacebookSignIn(result.accessToken.token)
                        }
                        override fun onCancel() {
                            // Handle cancel
                        }
                        override fun onError(error: FacebookException) {
                            // Handle error
                        }
                    }
                )
            },
            isLoading = uiState is AuthUiState.Loading
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
                    onPhoneNumberChange = viewModel::updatePhoneNumber,
                    onRegister = viewModel::onRegister,
                    onNavigateToSignIn = { isRegistering = false }
                )
            } else {
                SignInForm(
                    onSignIn = viewModel::onEmailSignIn,
                    onNavigateToRegister = { isRegistering = true },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState is AuthUiState.Loading
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