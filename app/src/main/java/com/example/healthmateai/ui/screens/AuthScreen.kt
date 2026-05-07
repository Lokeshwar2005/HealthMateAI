package com.example.healthmateai.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ui.viewmodel.AuthContentViewModel
import com.example.healthmateai.ui.viewmodel.AuthState
import com.example.healthmateai.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(),
    onGoogleSignIn: () -> Unit = {},
    onAuthSuccess: () -> Unit = {}
) {
    var showLoginScreen by remember { mutableStateOf(true) }
    val authContentViewModel: AuthContentViewModel = viewModel()
    val authContent by authContentViewModel.content.collectAsState()

    val authState by authViewModel.authState.collectAsState()

    // Handle success navigation
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }

    AnimatedContent(
        targetState = showLoginScreen,
        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(220)) }
    ) { isLogin ->
        if (isLogin) {
            LoginScreen(
                content = authContent.login,
                sharedContent = authContent.shared,
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onSignUpClick = { showLoginScreen = false },
                onGoogleSignInClick = { onGoogleSignIn() },
                isLoading = authState is AuthState.Loading,
                errorMessage = (authState as? AuthState.Error)?.message
            )
        } else {
            SignUpScreen(
                content = authContent.signup,
                sharedContent = authContent.shared,
                validationContent = authContent.validation,
                onSignUpClick = { name, email, password ->
                    authViewModel.signUp(name, email, password)
                },
                onLoginClick = { showLoginScreen = true },
                isLoading = authState is AuthState.Loading,
                errorMessage = (authState as? AuthState.Error)?.message
            )
        }
    }
}

