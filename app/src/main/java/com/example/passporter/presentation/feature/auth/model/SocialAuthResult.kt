package com.example.passporter.presentation.feature.auth.model

sealed class SocialAuthResult {
    data class Google(val idToken: String) : SocialAuthResult()
    data class Facebook(val accessToken: String) : SocialAuthResult()
    data class Error(val message: String) : SocialAuthResult()
}