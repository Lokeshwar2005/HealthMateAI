package com.example.healthmateai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ui.components.AppPadding
import com.example.healthmateai.ui.components.AppSpacing
import com.example.healthmateai.ui.components.PremiumBackButton
import com.example.healthmateai.ui.components.PremiumGlowButton
import com.example.healthmateai.ui.components.PremiumHeaderCard
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.CyberCyanBright
import com.example.healthmateai.ui.theme.SurfaceOutline
import com.example.healthmateai.ui.theme.GradientEnd
import com.example.healthmateai.ui.theme.GradientStart
import com.example.healthmateai.ui.theme.HealthMateAITheme
import com.example.healthmateai.ui.viewmodel.PredictionViewModel
import com.example.healthmateai.ui.viewmodel.PredictionViewModelFactory
import kotlin.math.roundToInt

data class WizardField(
    val key: String,
    val label: String,
    val min: Float,
    val max: Float,
    val isToggle: Boolean = false,
    val step: Float = 1f
)

private data class FactorItem(
    val label: String,
    val level: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private data class AdviceSection(
    val title: String,
    val items: List<String>,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private data class RecommendationStep(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private data class LifestyleTip(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

enum class PredictionState { INPUT, LOADING, RESULT }

@Composable
fun PredictionScreen(
    contentPadding: PaddingValues,
    onPrediction: (disease: String, probability: Float, inputs: Map<String, Any>) -> Unit,
    viewModel: PredictionViewModel = viewModel(factory = PredictionViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var disease by remember { mutableStateOf("diabetes") }
    val listState = rememberLazyListState()

    val diabetesFields = remember {
        listOf(
            WizardField("Glucose", "Glucose", 70f, 250f),
            WizardField("BMI", "BMI", 15f, 50f, step = 0.5f),
            WizardField("Age", "Age", 18f, 90f),
            WizardField("Insulin", "Insulin", 0f, 300f),
            WizardField("BloodPressure", "Blood Pressure", 50f, 140f),
            WizardField("DiabetesPedigreeFunction", "Diabetes Pedigree", 0.05f, 2.50f, step = 0.01f),
        )
    }

    val heartFields = remember {
        listOf(
            WizardField("age", "Age", 40f, 95f),
            WizardField("ejection_fraction", "Ejection Fraction", 14f, 80f),
            WizardField("serum_creatinine", "Serum Creatinine", 0.5f, 9.4f, step = 0.1f),
            WizardField("creatinine_phosphokinase", "CPK", 23f, 7861f),
            WizardField("serum_sodium", "Serum Sodium", 113f, 148f),
            WizardField("anaemia", "Anaemia", 0f, 1f, isToggle = true),
            WizardField("diabetes", "Diabetes", 0f, 1f, isToggle = true),
            WizardField("high_blood_pressure", "High Blood Pressure", 0f, 1f, isToggle = true),
            WizardField("smoking", "Smoking", 0f, 1f, isToggle = true),
            WizardField("platelets", "Platelets", 25000f, 850000f)
        )
    }

    val activeFields = if (disease == "diabetes") diabetesFields else heartFields
    var currentStep by remember(disease) { mutableIntStateOf(0) }
    val values = remember(disease) {
        mutableStateMapOf<String, Float>().apply {
            activeFields.forEach { field ->
                put(field.key, (field.min + field.max) / 2f)
            }
        }
    }

    val probability = uiState.result?.probability?.toFloat() ?: 0f
    val animatedProbability by animateFloatAsState(
        targetValue = probability,
        animationSpec = tween(durationMillis = 700),
        label = "probability"
    )

    LaunchedEffect(uiState.result?.probability) {
        uiState.result?.let { result ->
            onPrediction(disease, result.probability.toFloat(), values.toMap())
        }
    }

    val screenState = when {
        uiState.isLoading -> PredictionState.LOADING
        uiState.result != null -> PredictionState.RESULT
        else -> PredictionState.INPUT
    }

    AnimatedContent(
        targetState = screenState,
        transitionSpec = {
            fadeIn(tween(600)) togetherWith fadeOut(tween(600))
        },
        label = "predictionState",
        modifier = Modifier.fillMaxSize().background(BgDark).padding(contentPadding)
    ) { state ->
        when (state) {
            PredictionState.INPUT -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(
                        horizontal = AppPadding.screenHorizontal,
                        vertical = AppPadding.screenVertical
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)
                ) {
                    item { Spacer(Modifier.height(AppSpacing.md)); PredictionHeroBar() }
                    item { DiseaseSegmentedControl(disease, { disease = it }) }
                    item {
                        val progress = (currentStep + 1f) / activeFields.size.toFloat()
                        val animatedProgress by animateFloatAsState(progress.coerceIn(0f, 1f), tween(280), label = "p")
                        StepProgressBar(animatedProgress)
                    }
                    item { Text("Step ${currentStep + 1} of ${activeFields.size}", style = MaterialTheme.typography.labelLarge, color = Color(0xFFB1BFDF)) }
                    item {
                        AnimatedContent(
                            targetState = activeFields[currentStep],
                            transitionSpec = { (slideInHorizontally { it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { -it / 4 } + fadeOut()) },
                            label = "wizard"
                        ) { field ->
                            WizardFieldCard(field, values[field.key] ?: field.min) { values[field.key] = it }
                        }
                    }
                    if (uiState.errorMessage != null) {
                        item {
                            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF3B1D24), border = BorderStroke(1.dp, Color(0xFF5B2D34)), modifier = Modifier.fillMaxWidth()) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Warning, null, tint = Color(0xFFFF8C8C), modifier = Modifier.size(18.dp))
                                    Text(uiState.errorMessage ?: "Unknown error", color = Color(0xFFFF8C8C), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PremiumBackButton(onClick = { if (currentStep > 0) currentStep -= 1 }, enabled = currentStep > 0, modifier = Modifier.weight(0.9f))
                            PremiumGlowButton(
                                text = if (currentStep < activeFields.lastIndex) "Next" else "Run Prediction",
                                onClick = {
                                    if (currentStep < activeFields.lastIndex) currentStep += 1 else {
                                        val payload = values.mapValues { (_, value) -> if (value == 0f || value == 1f) value.toInt() else String.format("%.2f", value).toDouble() }
                                        viewModel.predict(disease, payload)
                                    }
                                },
                                enabled = true,
                                modifier = Modifier.weight(1.1f)
                            )
                        }
                    }
                    item { Spacer(Modifier.height(AppSpacing.xl)) }
                }
            }
            PredictionState.LOADING -> {
                PredictionLoadingScreen()
            }
            PredictionState.RESULT -> {
                uiState.result?.let { result ->
                    val riskLabel = result.risk
                    val riskColor = riskColorFor(riskLabel)
                    val summaryText = summaryTextFor(riskLabel)
                    val factors = buildFactorItems(result.factors, riskLabel)
                    val adviceSections = buildAdviceSections(result.advice)
                    val recommendations = buildRecommendationSteps(disease)
                    val lifestyleTips = buildLifestyleTips()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = AppPadding.screenHorizontal, vertical = AppPadding.screenVertical),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)
                    ) {
                        item { Spacer(Modifier.height(AppSpacing.md)) }
                        item {
                            ResultSummaryCard(riskLabel, probability, summaryText, riskColor, iconForRisk(riskLabel, disease))
                        }
                        item { FactorsSection(factors) }
                        item { AdviceSectionCard(riskColor, adviceSections) }
                        item { RecommendationsSection(recommendations) }
                        item { LifestyleTipsSection(lifestyleTips) }
                        item {
                            PremiumGlowButton(
                                text = "Start New Prediction",
                                onClick = {
                                    viewModel.clearResult()
                                    currentStep = 0
                                },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item { Spacer(Modifier.height(AppSpacing.xl)) }
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionLoadingScreen() {
    val messages = listOf(
        "Analyzing Health Parameters...",
        "Running AI Medical Prediction...",
        "Evaluating Risk Factors...",
        "Processing Clinical Indicators...",
        "Generating Personalized Insights..."
    )
    var messageIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        while(true) {
            kotlinx.coroutines.delay(2500)
            messageIndex = (messageIndex + 1) % messages.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loadingAnim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse),
        label = "pulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse),
        label = "glowAlpha"
    )
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(3000, easing = androidx.compose.animation.core.LinearEasing)),
        label = "rotateAnim"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = CyberCyanBright.copy(alpha = glowAlpha),
                    radius = (size.minDimension / 2f) * pulseScale,
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(Color.Transparent, CyberCyanBright, Color.Transparent)),
                    startAngle = rotateAngle,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF1E2D52),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            Icon(
                imageVector = Icons.Default.AutoGraph,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = messages[messageIndex],
            transitionSpec = {
                (slideInHorizontally { it / 2 } + fadeIn(tween(400))) togetherWith
                    (slideOutHorizontally { -it / 2 } + fadeOut(tween(400)))
            },
            label = "loadingMessage"
        ) { msg ->
            Text(
                text = msg,
                style = MaterialTheme.typography.titleMedium,
                color = CyberCyanBright,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "AI Diagnostic Engine Active",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8A9DC4)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Mini AI Processing Cards
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProcessingCard("Analyzing your inputs...", messageIndex >= 1)
            ProcessingCard("Calculating risk score...", messageIndex >= 2)
            ProcessingCard("Evaluating health pattern...", messageIndex >= 3)
            ProcessingCard("Generating Prediction Results...", messageIndex >= 4)
        }
    }
}

@Composable
fun ProcessingCard(title: String, isComplete: Boolean) {
    val bgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isComplete) Color(0xFF1A3340) else Color(0xFF131D34),
        animationSpec = tween(400), label = "bgColor"
    )
    val iconTint by androidx.compose.animation.animateColorAsState(
        targetValue = if (isComplete) CyberCyanBright else Color(0xFF4A5E80),
        animationSpec = tween(400), label = "iconTint"
    )
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, if (isComplete) CyberCyanBright.copy(alpha = 0.3f) else Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isComplete) {
                Icon(Icons.Default.CheckCircle, null, tint = iconTint, modifier = Modifier.size(20.dp))
            } else {
                CircularProgressIndicator(
                    color = iconTint,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                color = if (isComplete) Color.White else Color(0xFF8A9DC4),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isComplete) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun WizardFieldCard(
    field: WizardField,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val animatedValue by animateFloatAsState(targetValue = value, animationSpec = tween(180), label = "fieldValue")
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFF162447), Color(0xFF1B2C55)))
                )
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(field.label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFEFF5FF))
            if (field.isToggle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (value >= 0.5f) "Enabled" else "Disabled")
                    Switch(checked = value >= 0.5f, onCheckedChange = { onValueChange(if (it) 1f else 0f) })
                }
            } else {
                Text(
                    text = if (field.step < 1f) String.format("%.2f", animatedValue) else animatedValue.roundToInt().toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF4EE6E1),
                    fontWeight = FontWeight.ExtraBold
                )
                Slider(
                    value = animatedValue,
                    onValueChange = { onValueChange(it.coerceIn(field.min, field.max)) },
                    valueRange = field.min..field.max,
                )
            }
        }
    }
}

@Composable
private fun PredictionGauge(probability: Float, risk: String, loading: Boolean) {
    val ringProgress by animateFloatAsState(
        targetValue = probability.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "ringProgress"
    )
    val infinite = rememberInfiniteTransition(label = "ringPulse")
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse),
        label = "ringGlow"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18274A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Live Probability", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFEFF5FF))

            if (loading) {
                CircularProgressIndicator(color = Color(0xFF4EE6E1), modifier = Modifier.size(44.dp))
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(152.dp)) {
                        drawCircle(
                            color = Color(0xFF3FCFE0).copy(alpha = pulseAlpha),
                            radius = size.minDimension / 2f,
                            style = Stroke(width = 10.dp.toPx())
                        )
                        drawArc(
                            color = Color(0xFF263562),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            brush = Brush.sweepGradient(listOf(Color(0xFF25C2BE), Color(0xFF58E5FF), Color(0xFF25C2BE))),
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        "${(ringProgress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF5FAFF)
                    )
                }
            }

            Text("Risk: $risk", color = Color(0xFFB7C5E7), style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun ResultSummaryCard(
    riskLabel: String,
    probability: Float,
    summary: String,
    riskColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val ringProgress by animateFloatAsState(
        targetValue = probability.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "resultRing"
    )
    val shimmer = rememberInfiniteTransition(label = "resultGlow")
    val glowAlpha by shimmer.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(animation = tween(1600), repeatMode = RepeatMode.Reverse),
        label = "resultGlowAlpha"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0F1E3C), Color(0xFF16284E), Color(0xFF0F1D33))
                    )
                )
                .border(1.dp, riskColor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = riskColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, riskColor.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = riskColor,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Column {
                    Text(riskLabel.uppercase(), color = riskColor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${(probability * 100).roundToInt()}% Risk Detected", color = Color(0xFFE2ECFF), style = MaterialTheme.typography.titleMedium)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(summary, color = Color(0xFFB8C9EA), style = MaterialTheme.typography.bodyLarge)
                    Text("AI-powered risk insights", color = CyberCyanBright, style = MaterialTheme.typography.labelLarge)
                }

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(96.dp)) {
                    Canvas(modifier = Modifier.size(96.dp)) {
                        drawCircle(
                            color = riskColor.copy(alpha = glowAlpha),
                            radius = size.minDimension / 2f,
                            style = Stroke(width = 10.dp.toPx())
                        )
                        drawArc(
                            color = Color(0xFF23345B),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = riskColor,
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "${(ringProgress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF3F8FF)
                    )
                }
            }
        }
    }
}

@Composable
private fun FactorsSection(items: List<FactorItem>) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14223E))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Key Factors", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFF1F6FF))
            items.forEachIndexed { index, item ->
                FactorRow(item = item)
                if (index != items.lastIndex) {
                    Divider(color = SurfaceOutline.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun FactorRow(item: FactorItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0x1F3FE2FF),
            border = BorderStroke(1.dp, Color(0x553FE2FF))
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Color(0xFF6AEAFF),
                modifier = Modifier.padding(8.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(item.label, color = Color(0xFFE9F2FF), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text("Risk: ${item.level}", color = Color(0xFF9CB4DD), style = MaterialTheme.typography.bodyMedium)
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = badgeColor(item.level).copy(alpha = 0.2f),
            border = BorderStroke(1.dp, badgeColor(item.level).copy(alpha = 0.5f))
        ) {
            Text(
                text = item.level,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = badgeColor(item.level),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdviceSectionCard(riskColor: Color, sections: List<AdviceSection>) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A223A))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = riskColor)
                Text("Medical Advice", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFF1F6FF))
            }
            sections.forEach { section ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF151E33),
                    border = BorderStroke(1.dp, SurfaceOutline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(section.icon, contentDescription = null, tint = Color(0xFF74E6FF), modifier = Modifier.size(18.dp))
                            Text(section.title, color = Color(0xFFE6F0FF), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        section.items.forEach { item ->
                            Text("• $item", color = Color(0xFFB8C9EA), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationsSection(steps: List<RecommendationStep>) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141E36))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Doctor Recommendations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFF1F6FF))
            steps.forEach { step ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1A2947),
                    border = BorderStroke(1.dp, Color(0x333FE2FF))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x1F3FE2FF),
                            border = BorderStroke(1.dp, Color(0x553FE2FF))
                        ) {
                            Icon(step.icon, contentDescription = null, tint = Color(0xFF7AEFFF), modifier = Modifier.padding(8.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(step.title, color = Color(0xFFF0F6FF), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(step.description, color = Color(0xFF9DB3D8), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun LifestyleTipsSection(tips: List<LifestyleTip>) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D34))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Lifestyle & Prevention", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFF1F6FF))
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tips.forEach { tip ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF162340),
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(tip.icon, contentDescription = null, tint = Color(0xFF6AEAFF))
                            Text(tip.title, color = Color(0xFFF0F6FF), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(tip.description, color = Color(0xFF9EB5DB), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

private fun riskColorFor(riskLabel: String): Color {
    return when (riskLabel.lowercase()) {
        "high" -> Color(0xFFFF6B6B)
        "medium" -> Color(0xFFF2B84B)
        "low" -> Color(0xFF2BD4A4)
        else -> Color(0xFF58E5FF)
    }
}

private fun iconForRisk(riskLabel: String, disease: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (riskLabel.lowercase()) {
        "high" -> Icons.Default.Warning
        "medium" -> if (disease == "heart") Icons.Default.Favorite else Icons.Default.Shield
        "low" -> Icons.Default.HealthAndSafety
        else -> Icons.Default.MedicalServices
    }
}

private fun summaryTextFor(riskLabel: String): String {
    return when (riskLabel.lowercase()) {
        "high" -> "Your current health indicators suggest elevated risk and need attention."
        "medium" -> "Your current indicators show moderate risk with room for improvement."
        "low" -> "Your current indicators look stable. Keep steady preventative habits."
        else -> "Your latest prediction is ready for review."
    }
}

private fun buildFactorItems(factors: List<String>, overallRisk: String): List<FactorItem> {
    return factors.map { label ->
        val level = when {
            label.contains("High", ignoreCase = true) -> "High"
            label.contains("Combined", ignoreCase = true) -> "Medium"
            else -> overallRisk
        }
        FactorItem(label = label, level = level, icon = iconForFactor(label))
    }
}

private fun iconForFactor(label: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        label.contains("Blood", ignoreCase = true) -> Icons.Default.Bloodtype
        label.contains("BMI", ignoreCase = true) -> Icons.Default.FitnessCenter
        label.contains("Heart", ignoreCase = true) -> Icons.Default.Favorite
        label.contains("Kidney", ignoreCase = true) -> Icons.Default.Shield
        label.contains("Smoking", ignoreCase = true) -> Icons.Default.Warning
        else -> Icons.Default.HealthAndSafety
    }
}

private fun badgeColor(level: String): Color {
    return when (level.lowercase()) {
        "high" -> Color(0xFFFF6B6B)
        "medium" -> Color(0xFFF2B84B)
        "low" -> Color(0xFF2BD4A4)
        else -> Color(0xFF58E5FF)
    }
}

private fun buildAdviceSections(advice: String): List<AdviceSection> {
    val sentences = advice.split(".", "\n", ";")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val buckets = listOf(
        "Immediate Attention" to Icons.Default.Warning,
        "Lifestyle Changes" to Icons.Default.Restaurant,
        "Doctor Consultation" to Icons.Default.MedicalServices,
        "Monitoring Tips" to Icons.Default.HealthAndSafety
    )

    return buckets.mapIndexed { index, entry ->
        val slice = sentences.drop(index * 2).take(2).ifEmpty { listOf("Maintain consistent monitoring and keep follow-ups regular.") }
        AdviceSection(title = entry.first, items = slice, icon = entry.second)
    }
}

private fun buildRecommendationSteps(disease: String): List<RecommendationStep> {
    return if (disease == "heart") {
        listOf(
            RecommendationStep("Comprehensive Cardiac Evaluation", "Schedule ECG and echocardiogram review.", Icons.Default.MedicalServices),
            RecommendationStep("Blood Pressure Management", "Track readings daily and reduce sodium intake.", Icons.Default.HealthAndSafety),
            RecommendationStep("Activity & Rehab", "Adopt low-impact cardio under guidance.", Icons.Default.DirectionsRun),
            RecommendationStep("Regular Monitoring", "Keep monthly follow-ups with your clinician.", Icons.Default.Shield)
        )
    } else {
        listOf(
            RecommendationStep("Comprehensive Medical Evaluation", "Review glucose and A1C values with a clinician.", Icons.Default.MedicalServices),
            RecommendationStep("Blood Sugar Management", "Align meals with medication timing and monitor daily.", Icons.Default.Bloodtype),
            RecommendationStep("Lifestyle Adjustments", "Add balanced meals and consistent activity.", Icons.Default.DirectionsRun),
            RecommendationStep("Regular Monitoring", "Track progress and schedule follow-up checks.", Icons.Default.Shield)
        )
    }
}

private fun buildLifestyleTips(): List<LifestyleTip> {
    return listOf(
        LifestyleTip("Healthy Diet", "Prioritize fiber, lean protein, and low sugar meals.", Icons.Default.Restaurant),
        LifestyleTip("Stay Hydrated", "Aim for steady hydration throughout the day.", Icons.Default.Opacity),
        LifestyleTip("Exercise", "Add light cardio and strength sessions weekly.", Icons.Default.DirectionsRun),
        LifestyleTip("Sleep Improvement", "Keep a consistent sleep schedule.", Icons.Default.Bedtime)
    )
}

@Composable
private fun PredictionHeroBar() {
    PremiumHeaderCard(
        title = "Disease Prediction",
        subtitle = "AI-guided assessment with step-by-step inputs",
        icon = null
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun DiseaseSegmentedControl(
    selectedDisease: String,
    onDiseaseSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF152445),
        shadowElevation = 4.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val indicatorWidth = (maxWidth - 4.dp) / 2f
            val offsetX by animateDpAsState(
                targetValue = if (selectedDisease == "diabetes") 0.dp else indicatorWidth,
                animationSpec = tween(240),
                label = "segmentOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(width = indicatorWidth, height = 44.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF27C5C0), Color(0xFF54E4FF))),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                SegmentButton(
                    title = "Diabetes",
                    selected = selectedDisease == "diabetes",
                    onClick = { onDiseaseSelected("diabetes") },
                    modifier = Modifier.weight(1f)
                )
                SegmentButton(
                    title = "Heart",
                    selected = selectedDisease == "heart",
                    onClick = { onDiseaseSelected("heart") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SegmentButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(110),
        label = "segmentScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        color = Color.Transparent,
        modifier = modifier
            .height(44.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) Color(0xFF042532) else Color(0xFFB7C5E6),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StepProgressBar(progress: Float) {
    val progressValue = progress.coerceIn(0f, 1f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF24345C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LinearProgressIndicator(
            progress = { progressValue },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF4EE6E1),
            trackColor = Color(0xFF24345C)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PredictionPreview() {
    HealthMateAITheme {
        PredictionScreen(contentPadding = PaddingValues(0.dp), onPrediction = { _, _, _ -> })
    }
}
