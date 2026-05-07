package com.example.healthmateai.data

import android.content.Context
import com.example.healthmateai.ui.model.AuthErrorContent
import com.example.healthmateai.ui.model.AuthSharedContent
import com.example.healthmateai.ui.model.AuthUiContent
import com.example.healthmateai.ui.model.AuthValidationContent
import com.example.healthmateai.ui.model.LoginContent
import com.example.healthmateai.ui.model.SignUpContent
import org.json.JSONObject

class AuthContentRepository(private val context: Context) {
    fun loadContent(assetName: String = "auth_content.json"): AuthUiContent {
        val json = readAsset(assetName) ?: return AuthUiContent()
        val root = JSONObject(json)

        val shared = root.optJSONObject("shared") ?: JSONObject()
        val login = root.optJSONObject("login") ?: JSONObject()
        val signup = root.optJSONObject("signup") ?: JSONObject()
        val validation = root.optJSONObject("validation") ?: JSONObject()
        val errors = root.optJSONObject("errors") ?: JSONObject()

        return AuthUiContent(
            shared = AuthSharedContent(
                appTitle = shared.optString("appTitle"),
                appSubtitle = shared.optString("appSubtitle"),
                badgeLive = shared.optString("badgeLive")
            ),
            login = LoginContent(
                title = login.optString("title"),
                subtitle = login.optString("subtitle"),
                emailLabel = login.optString("emailLabel"),
                passwordLabel = login.optString("passwordLabel"),
                forgotLabel = login.optString("forgotLabel"),
                ctaLabel = login.optString("ctaLabel"),
                dividerLabel = login.optString("dividerLabel"),
                googleLabel = login.optString("googleLabel"),
                signupPrompt = login.optString("signupPrompt"),
                signupLink = login.optString("signupLink"),
                termsText = login.optString("termsText"),
                heroBadge = login.optString("heroBadge"),
                icon = login.optString("icon")
            ),
            signup = SignUpContent(
                title = signup.optString("title"),
                subtitle = signup.optString("subtitle"),
                nameLabel = signup.optString("nameLabel"),
                emailLabel = signup.optString("emailLabel"),
                passwordLabel = signup.optString("passwordLabel"),
                confirmLabel = signup.optString("confirmLabel"),
                ctaLabel = signup.optString("ctaLabel"),
                dividerLabel = signup.optString("dividerLabel"),
                loginPrompt = signup.optString("loginPrompt"),
                loginLink = signup.optString("loginLink"),
                termsText = signup.optString("termsText"),
                heroBadge = signup.optString("heroBadge"),
                icon = signup.optString("icon")
            ),
            validation = AuthValidationContent(
                passwordMismatch = validation.optString("passwordMismatch"),
                passwordMatch = validation.optString("passwordMatch")
            ),
            errors = AuthErrorContent(
                googleInit = errors.optString("googleInit")
            )
        )
    }

    private fun readAsset(assetName: String): String? {
        return try {
            context.assets.open(assetName).bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            null
        }
    }
}
