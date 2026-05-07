package com.example.healthmateai.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient? = null

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        checkLoginStatus()
    }

    fun initializeGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("432932300025-gmvkusvdbupp65ju3mtg9qof0kmf3mun.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    private fun checkLoginStatus() {
        _isLoggedIn.value = firebaseAuth.currentUser != null
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        Log.d("AuthViewModel", "Starting Google Sign-In with token")

        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    Log.d("AuthViewModel", "Google Sign-In successful: ${authResult.user?.uid}")
                    Log.d("AuthViewModel", "User email: ${authResult.user?.email}")
                    _authState.value = AuthState.Success
                    _isLoggedIn.value = true
                }
                .addOnFailureListener { exception ->
                    Log.e("AuthViewModel", "Google Sign-In failed", exception)
                    Log.e("AuthViewModel", "Error message: ${exception.message}")
                    _authState.value = AuthState.Error("Google sign-in failed: ${exception.message}")
                }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Google Sign-In error", e)
            _authState.value = AuthState.Error("Authentication error: ${e.message}")
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient? = googleSignInClient

    fun signOutGoogle() {
        googleSignInClient?.signOut()?.addOnCompleteListener {
            Log.d("AuthViewModel", "Google Sign-Out completed")
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Login successful: ${it.user?.uid}")
                _authState.value = AuthState.Success
                _isLoggedIn.value = true
            }
            .addOnFailureListener { exception ->
                Log.e("AuthViewModel", "Login failed", exception)
                val errorMsg = when {
                    exception.message?.contains("no user record", ignoreCase = true) == true ->
                        "Email not found. Please sign up."
                    exception.message?.contains("password", ignoreCase = true) == true ->
                        "Incorrect password."
                    exception.message?.contains("invalid", ignoreCase = true) == true ->
                        "Invalid email format."
                    else -> exception.message ?: "Login failed. Please try again."
                }
                _authState.value = AuthState.Error(errorMsg)
            }
    }

    fun signUp(name: String, email: String, password: String) {
        _authState.value = AuthState.Loading

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Log.d("AuthViewModel", "Sign up successful: ${authResult.user?.uid}")
                // Update user profile with name
                val user = authResult.user
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {
                        Log.d("AuthViewModel", "Profile updated with name: $name")
                        _authState.value = AuthState.Success
                        _isLoggedIn.value = true
                    }
                    ?.addOnFailureListener { exception ->
                        Log.e("AuthViewModel", "Profile update failed", exception)
                        _authState.value = AuthState.Error("Account created but profile update failed.")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("AuthViewModel", "Sign up failed", exception)
                val errorMsg = when {
                    exception.message?.contains("already", ignoreCase = true) == true ->
                        "This email is already registered. Please log in."
                    exception.message?.contains("password", ignoreCase = true) == true ->
                        "Password must be at least 6 characters."
                    exception.message?.contains("invalid", ignoreCase = true) == true ->
                        "Invalid email format."
                    else -> exception.message ?: "Sign up failed. Please try again."
                }
                _authState.value = AuthState.Error(errorMsg)
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        signOutGoogle()
        _isLoggedIn.value = false
        _authState.value = AuthState.Idle
        Log.d("AuthViewModel", "User logged out")
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun setAuthError(message: String) {
        _authState.value = AuthState.Error(message)
    }
}
