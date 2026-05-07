package com.example.healthmateai

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ui.screens.AuthScreen
import com.example.healthmateai.ui.screens.PremiumAppScaffold
import com.example.healthmateai.ui.screens.SplashScreen
import com.example.healthmateai.ui.theme.HealthMateAITheme
import com.example.healthmateai.ui.viewmodel.AuthContentViewModel
import com.example.healthmateai.ui.viewmodel.AuthViewModel
import com.example.healthmateai.ui.viewmodel.SplashViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            HealthMateAITheme {
                HealthMateAIApp()
            }
        }
    }
}

@Composable
fun HealthMateAIApp() {
    val authViewModel: AuthViewModel = viewModel()
    val authContentViewModel: AuthContentViewModel = viewModel()
    val splashViewModel: SplashViewModel = viewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val authContent by authContentViewModel.content.collectAsState()
    val splashContent by splashViewModel.content.collectAsState()
    var userName by remember { mutableStateOf<String?>(null) }
    var userEmail by remember { mutableStateOf<String?>(null) }
    var showSplash by remember { mutableStateOf(true) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (!idToken.isNullOrBlank()) {
                authViewModel.signInWithGoogle(idToken)
            } else {
                authViewModel.setAuthError("Google Sign-In token missing. Try again.")
            }
        } catch (e: ApiException) {
            authViewModel.setAuthError("Google Sign-In cancelled/failed (${e.statusCode}).")
            android.util.Log.e("GoogleSignIn", "Google Sign-In failed", e)
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.initializeGoogleSignIn(context)
    }

    LaunchedEffect(splashContent.durationMs) {
        delay(splashContent.durationMs)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen(content = splashContent)
    } else {
        if (isLoggedIn) {
            LaunchedEffect(Unit) {
                val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                userName = user?.displayName
                userEmail = user?.email
            }

            PremiumAppScaffold(
                userName = userName,
                userEmail = userEmail,
                onLogout = {
                    authViewModel.logout()
                    userName = null
                    userEmail = null
                }
            )
        } else {
            AuthScreen(
                authViewModel = authViewModel,
                onGoogleSignIn = {
                    authViewModel.getGoogleSignInClient()?.signInIntent?.let { intent ->
                        googleSignInLauncher.launch(intent)
                    } ?: authViewModel.setAuthError(authContent.errors.googleInit)
                },
                onAuthSuccess = {}
            )
        }
    }
}
