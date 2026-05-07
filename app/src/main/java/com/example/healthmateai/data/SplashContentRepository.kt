package com.example.healthmateai.data

import android.content.Context
import com.example.healthmateai.ui.model.SplashContent
import org.json.JSONObject

class SplashContentRepository(private val context: Context) {
    fun loadContent(assetName: String = "splash_content.json"): SplashContent {
        val json = readAsset(assetName) ?: return SplashContent()
        val root = JSONObject(json)
        return SplashContent(
            title = root.optString("title"),
            subtitle = root.optString("subtitle"),
            badge = root.optString("badge"),
            durationMs = root.optLong("durationMs", 1200L)
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
