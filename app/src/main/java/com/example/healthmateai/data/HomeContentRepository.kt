package com.example.healthmateai.data

import android.content.Context
import com.example.healthmateai.ui.model.ChatMessage
import com.example.healthmateai.ui.model.DiseaseTab
import com.example.healthmateai.ui.model.HomeUiContent
import com.example.healthmateai.ui.model.ServiceCard
import com.example.healthmateai.ui.model.StatCard
import org.json.JSONArray
import org.json.JSONObject

class HomeContentRepository(private val context: Context) {
    fun loadContent(assetName: String = "home_content.json"): HomeUiContent {
        val json = readAsset(assetName) ?: return HomeUiContent()
        val root = JSONObject(json)

        val welcome = root.optJSONObject("welcome") ?: JSONObject()
        val clinician = root.optJSONObject("clinician") ?: JSONObject()
        val graph = root.optJSONObject("graph") ?: JSONObject()
        val rag = root.optJSONObject("rag") ?: JSONObject()

        return HomeUiContent(
            appTitle = root.optString("appTitle"),
            appSubtitle = root.optString("appSubtitle"),
            logoutLabel = root.optString("logoutLabel"),
            liveBadge = root.optString("liveBadge"),
            clinicianName = clinician.optString("name"),
            clinicianSubtitle = clinician.optString("subtitle"),
            clinicianInitials = clinician.optString("initials"),
            stats = parseStats(root.optJSONArray("stats")),
            welcomeTitle = welcome.optString("title"),
            welcomeSubtitle = welcome.optString("subtitle"),
            servicesTitle = root.optString("servicesTitle"),
            services = parseServices(root.optJSONArray("services")),
            predictiveTitle = root.optString("predictiveTitle"),
            predictiveSubtitle = root.optString("predictiveSubtitle"),
            graphTitle = graph.optString("title"),
            graphSubtitle = graph.optString("subtitle"),
            seriesLabel = graph.optString("seriesLabel"),
            diseases = parseDiseases(graph.optJSONArray("tabs")),
            ragFabLabel = rag.optString("fabLabel"),
            ragBadge = rag.optString("badge"),
            ragSheetTitle = rag.optString("sheetTitle"),
            ragInputPlaceholder = rag.optString("inputPlaceholder"),
            ragMessages = parseMessages(rag.optJSONArray("messages"))
        )
    }

    private fun readAsset(assetName: String): String? {
        return try {
            context.assets.open(assetName).bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            null
        }
    }

    private fun parseServices(array: JSONArray?): List<ServiceCard> {
        if (array == null) return emptyList()
        return List(array.length()) { index ->
            val item = array.optJSONObject(index) ?: JSONObject()
            ServiceCard(
                title = item.optString("title"),
                subtitle = item.optString("subtitle"),
                icon = item.optString("icon")
            )
        }
    }

    private fun parseStats(array: JSONArray?): List<StatCard> {
        if (array == null) return emptyList()
        return List(array.length()) { index ->
            val item = array.optJSONObject(index) ?: JSONObject()
            StatCard(
                value = item.optString("value"),
                label = item.optString("label"),
                icon = item.optString("icon")
            )
        }
    }

    private fun parseDiseases(array: JSONArray?): List<DiseaseTab> {
        if (array == null) return emptyList()
        return List(array.length()) { index ->
            val item = array.optJSONObject(index) ?: JSONObject()
            DiseaseTab(
                name = item.optString("name"),
                featuresTitle = item.optString("featuresTitle"),
                features = parseStringList(item.optJSONArray("features")),
                series = parseFloatList(item.optJSONArray("series"))
            )
        }
    }

    private fun parseMessages(array: JSONArray?): List<ChatMessage> {
        if (array == null) return emptyList()
        return List(array.length()) { index ->
            val item = array.optJSONObject(index) ?: JSONObject()
            ChatMessage(
                role = item.optString("role"),
                text = item.optString("text")
            )
        }
    }

    private fun parseStringList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        return List(array.length()) { index -> array.optString(index) }
    }

    private fun parseFloatList(array: JSONArray?): List<Float> {
        if (array == null) return emptyList()
        return List(array.length()) { index -> array.optDouble(index).toFloat() }
    }
}
