package com.example.healthmateai.ui.model

data class HomeUiState(
    val content: HomeUiContent = HomeUiContent(),
    val selectedDiseaseIndex: Int = 0,
    val isChatOpen: Boolean = false
)

data class HomeUiContent(
    val appTitle: String = "",
    val appSubtitle: String = "",
    val logoutLabel: String = "",
    val liveBadge: String = "",
    val clinicianName: String = "",
    val clinicianSubtitle: String = "",
    val clinicianInitials: String = "",
    val stats: List<StatCard> = emptyList(),
    val welcomeTitle: String = "",
    val welcomeSubtitle: String = "",
    val servicesTitle: String = "",
    val services: List<ServiceCard> = emptyList(),
    val predictiveTitle: String = "",
    val predictiveSubtitle: String = "",
    val graphTitle: String = "",
    val graphSubtitle: String = "",
    val seriesLabel: String = "",
    val diseases: List<DiseaseTab> = emptyList(),
    val ragFabLabel: String = "",
    val ragBadge: String = "",
    val ragSheetTitle: String = "",
    val ragInputPlaceholder: String = "",
    val ragMessages: List<ChatMessage> = emptyList()
)

data class ServiceCard(
    val title: String,
    val subtitle: String,
    val icon: String
)

data class StatCard(
    val value: String,
    val label: String,
    val icon: String
)

data class DiseaseTab(
    val name: String,
    val featuresTitle: String,
    val features: List<String>,
    val series: List<Float>
)

data class ChatMessage(
    val role: String,
    val text: String
)
