package com.example.healthmateai.ui.model

data class AuthUiContent(
    val shared: AuthSharedContent = AuthSharedContent(),
    val login: LoginContent = LoginContent(),
    val signup: SignUpContent = SignUpContent(),
    val validation: AuthValidationContent = AuthValidationContent(),
    val errors: AuthErrorContent = AuthErrorContent()
)

data class AuthSharedContent(
    val appTitle: String = "",
    val appSubtitle: String = "",
    val badgeLive: String = ""
)

data class LoginContent(
    val title: String = "",
    val subtitle: String = "",
    val emailLabel: String = "",
    val passwordLabel: String = "",
    val forgotLabel: String = "",
    val ctaLabel: String = "",
    val dividerLabel: String = "",
    val googleLabel: String = "",
    val signupPrompt: String = "",
    val signupLink: String = "",
    val termsText: String = "",
    val heroBadge: String = "",
    val icon: String = ""
)

data class SignUpContent(
    val title: String = "",
    val subtitle: String = "",
    val nameLabel: String = "",
    val emailLabel: String = "",
    val passwordLabel: String = "",
    val confirmLabel: String = "",
    val ctaLabel: String = "",
    val dividerLabel: String = "",
    val loginPrompt: String = "",
    val loginLink: String = "",
    val termsText: String = "",
    val heroBadge: String = "",
    val icon: String = ""
)

data class AuthValidationContent(
    val passwordMismatch: String = "",
    val passwordMatch: String = ""
)

data class AuthErrorContent(
    val googleInit: String = ""
)
