package com.example.healthmateai.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ui.model.ChatMessage
import com.example.healthmateai.ui.model.DiseaseTab
import com.example.healthmateai.ui.model.ServiceCard
import com.example.healthmateai.ui.model.StatCard
import com.example.healthmateai.ui.theme.AccentBlue
import com.example.healthmateai.ui.theme.AccentCyan
import com.example.healthmateai.ui.theme.AccentGreen
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.BgDarkAlt
import com.example.healthmateai.ui.theme.SurfaceCard
import com.example.healthmateai.ui.theme.SurfaceCardAlt
import com.example.healthmateai.ui.theme.SurfaceOutline
import com.example.healthmateai.ui.theme.TextLight
import com.example.healthmateai.ui.theme.TextPrimary
import com.example.healthmateai.ui.theme.TextSecondary
import com.example.healthmateai.ui.viewmodel.AuthViewModel
import com.example.healthmateai.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    userName: String? = null,
    userEmail: String? = null,
    onLogoutClick: () -> Unit = {},
    onOpenPredictor: () -> Unit = {}
) {
    val homeViewModel: HomeViewModel = viewModel()
    val uiState by homeViewModel.uiState.collectAsState()
    val content = uiState.content
    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { startAnimations = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                HeaderSection(
                    title = content.appTitle,
                    subtitle = content.appSubtitle,
                    liveBadge = content.liveBadge,
                    logoutLabel = content.logoutLabel,
                    userName = userName,
                    onLogoutClick = {
                        authViewModel.logout()
                        onLogoutClick()
                    },
                    startAnimations = startAnimations
                )
            }

            item {
                val displayName = userName?.takeIf { it.isNotBlank() }
                    ?: userEmail?.substringBefore("@")
                    ?: ""
                val displaySubtitle = userEmail ?: ""
                ClinicianCard(
                    name = displayName,
                    subtitle = displaySubtitle,
                    initials = deriveInitials(displayName, userEmail),
                    startAnimations = startAnimations
                )
            }

            item {
                StatsRow(
                    stats = content.stats,
                    startAnimations = startAnimations
                )
            }

            item {
                SectionTitle(text = content.servicesTitle)
            }

            item {
                ServicesGrid(
                    services = content.services,
                    startAnimations = startAnimations
                )
            }

            item {
                PredictiveSection(
                    title = content.predictiveTitle,
                    subtitle = content.predictiveSubtitle,
                    diseases = content.diseases,
                    selectedIndex = uiState.selectedDiseaseIndex,
                    onSelect = homeViewModel::selectDisease,
                    seriesLabel = content.seriesLabel,
                    startAnimations = startAnimations,
                    onOpenPredictor = onOpenPredictor
                )
            }
        }

        AiFloatingButton(
            label = content.ragFabLabel,
            badge = content.ragBadge,
            onClick = { homeViewModel.setChatOpen(true) }
        )
    }

    if (uiState.isChatOpen) {
        RagChatSheet(
            title = content.ragSheetTitle,
            placeholder = content.ragInputPlaceholder,
            messages = content.ragMessages,
            onDismiss = { homeViewModel.setChatOpen(false) }
        )
    }
}

@Composable
private fun HeaderSection(
    title: String,
    subtitle: String,
    liveBadge: String,
    logoutLabel: String,
    userName: String?,
    onLogoutClick: () -> Unit,
    startAnimations: Boolean
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "header_alpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 12.dp,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "header_offset"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .alpha(alpha)
            .offset(y = offsetY)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LiveBadge(text = liveBadge)
                }
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = logoutLabel,
                    tint = TextSecondary
                )
            }
        }

        if (!userName.isNullOrBlank()) {
            Text(
                text = userName,
                color = TextLight,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun LiveBadge(text: String) {
    Box(
        modifier = Modifier
            .background(AccentGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = AccentGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ClinicianCard(
    name: String,
    subtitle: String,
    initials: String,
    startAnimations: Boolean
) {
    AnimatedCard(index = 0, startAnimations = startAnimations) { modifier ->
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(SurfaceCardAlt, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = AccentBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        if (name.isNotBlank()) {
                            Text(
                                text = name,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (subtitle.isNotBlank()) {
                            Text(
                                text = subtitle,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Default.QueryStats,
                    contentDescription = null,
                    tint = AccentCyan
                )
            }
        }
    }
}

@Composable
private fun StatsRow(
    stats: List<StatCard>,
    startAnimations: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.take(2).forEachIndexed { index, stat ->
            AnimatedCard(index = index + 1, startAnimations = startAnimations) { modifier ->
                Card(
                    modifier = modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = statIcon(stat.icon),
                            contentDescription = stat.label,
                            tint = AccentCyan
                        )
                        Text(
                            text = stat.value,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stat.label,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(24.dp)
                .background(AccentBlue, RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun ServicesGrid(
    services: List<ServiceCard>,
    startAnimations: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        services.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEachIndexed { colIndex, service ->
                    val cardIndex = rowIndex * 2 + colIndex + 3
                    AnimatedCard(index = cardIndex, startAnimations = startAnimations) { modifier ->
                        ServiceCardItem(
                            modifier = modifier.weight(1f),
                            service = service
                        )
                    }
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ServiceCardItem(
    modifier: Modifier,
    service: ServiceCard
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceCardAlt, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = serviceIcon(service.icon),
                    contentDescription = service.title,
                    tint = AccentBlue
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = service.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = service.subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun PredictiveSection(
    title: String,
    subtitle: String,
    diseases: List<DiseaseTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    seriesLabel: String,
    startAnimations: Boolean,
    onOpenPredictor: () -> Unit
) {
    val safeIndex = selectedIndex.coerceIn(0, (diseases.size - 1).coerceAtLeast(0))
    val selected = diseases.getOrNull(safeIndex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )

        AnimatedCard(index = 12, startAnimations = startAnimations) { modifier ->
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassTabRow(
                        tabs = diseases.map { it.name },
                        selectedIndex = safeIndex,
                        onSelect = onSelect
                    )

                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (selected != null) {
                        FeatureList(features = selected.features)

                        AnimatedLineChart(
                            series = selected.series,
                            label = seriesLabel,
                            startAnimations = startAnimations
                        )

                        Button(
                            onClick = onOpenPredictor,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Open Disease Predictor")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedIndicator(tabPositions: List<TabPosition>, selectedIndex: Int) {
    if (tabPositions.isEmpty()) return
    val current = tabPositions[selectedIndex]
    val indicatorLeft by animateDpAsState(
        targetValue = current.left,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "indicator_left"
    )
    val indicatorRight by animateDpAsState(
        targetValue = current.right,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "indicator_right"
    )
    TabRowDefaults.Indicator(
        modifier = Modifier
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .height(3.dp),
        color = AccentCyan
    )
}

@Composable
private fun GlassTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCardAlt, RoundedCornerShape(18.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val textColor = if (selected) Color.White else TextSecondary
            val chipBg = if (selected) AccentCyan.copy(alpha = 0.85f) else Color.Transparent
            val borderColor = if (selected) AccentCyan.copy(alpha = 0.9f) else SurfaceOutline

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .background(chipBg, RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = borderColor.copy(alpha = if (selected) 0.9f else 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun FeatureList(features: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        features.forEach { feature ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AccentCyan, RoundedCornerShape(4.dp))
                )
                Text(
                    text = feature,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun AnimatedLineChart(
    series: List<Float>,
    label: String,
    startAnimations: Boolean
) {
    val hasData = series.size >= 2
    val progress by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "line_progress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(BgDarkAlt, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            if (hasData) {
                val max = series.maxOrNull() ?: 0f
                val min = series.minOrNull() ?: 0f
                val range = (max - min).takeIf { it > 0f } ?: 1f
                val visibleCount = (series.size * progress).toInt().coerceAtLeast(2)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stepX = size.width / (visibleCount - 1)
                    val path = Path()

                    series.take(visibleCount).forEachIndexed { index, value ->
                        val x = stepX * index
                        val y = size.height - ((value - min) / range * size.height)
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = AccentCyan,
                        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    val lastIndex = visibleCount - 1
                    val lastX = stepX * lastIndex
                    val lastY = size.height - ((series[visibleCount - 1] - min) / range * size.height)
                    drawCircle(
                        color = AccentCyan,
                        radius = 6f,
                        center = Offset(lastX, lastY)
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 11.sp,
            color = TextLight
        )
    }
}

@Composable
private fun AiFloatingButton(
    label: String,
    badge: String,
    onClick: () -> Unit
) {
    val pulseTransition = rememberInfiniteTransition(label = "ai_pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ai_pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        BadgedBox(
            badge = {
                if (badge.isNotBlank()) {
                    Badge(
                        containerColor = AccentCyan,
                        contentColor = BgDark
                    ) {
                        Text(text = badge, fontSize = 9.sp)
                    }
                }
            }
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(56.dp)
                    .scale(pulse)
                    .background(AccentBlue, RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = label,
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RagChatSheet(
    title: String,
    placeholder: String,
    messages: List<ChatMessage>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var input by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            messages.forEach { message ->
                ChatBubble(message = message)
                Spacer(modifier = Modifier.height(8.dp))
            }

            androidx.compose.material3.OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = placeholder,
                        tint = AccentCyan
                    )
                },
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = SurfaceOutline,
                    focusedContainerColor = SurfaceCardAlt,
                    unfocusedContainerColor = SurfaceCardAlt,
                    cursorColor = AccentCyan,
                    focusedLabelColor = AccentCyan,
                    unfocusedLabelColor = TextLight
                ),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(color = TextPrimary)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role.equals("user", ignoreCase = true)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) AccentBlue else SurfaceCardAlt,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 12.sp,
                color = if (isUser) Color.White else TextPrimary
            )
        }
    }
}

@Composable
private fun AnimatedCard(
    index: Int,
    startAnimations: Boolean,
    content: @Composable (Modifier) -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(startAnimations) {
        if (startAnimations) {
            delay(60L * index)
            visible = true
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "card_alpha_$index"
    )
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 16.dp,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "card_offset_$index"
    )

    content(
        Modifier
            .alpha(alpha)
            .offset(y = offsetY)
    )
}

private fun serviceIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (name.lowercase()) {
        "heart" -> Icons.Default.MonitorHeart
        "glucose" -> Icons.Default.WaterDrop
        "kidney" -> Icons.Default.Biotech
        "chat" -> Icons.Default.ChatBubbleOutline
        else -> Icons.Default.AutoGraph
    }
}

private fun statIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (name.lowercase()) {
        "trend" -> Icons.Default.ShowChart
        "brain" -> Icons.Default.QueryStats
        else -> Icons.Default.ShowChart
    }
}

private fun deriveInitials(name: String, email: String?): String {
    val fromName = name.trim().split(" ").filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.toString() }
        .joinToString("")
    if (fromName.isNotBlank()) return fromName.uppercase()

    val emailInitials = email?.trim()?.takeIf { it.isNotBlank() }
        ?.substringBefore("@")
        ?.split(".", "_", "-")
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.mapNotNull { it.firstOrNull()?.toString() }
        ?.joinToString("")
        ?.uppercase()

    return emailInitials ?: ""
}
