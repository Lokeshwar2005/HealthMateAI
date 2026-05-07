package com.example.healthmateai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ui.theme.AccentBlue
import com.example.healthmateai.ui.theme.AccentCyan
import com.example.healthmateai.ui.theme.AccentGreen
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.BgDarkAlt
import com.example.healthmateai.ui.theme.SurfaceCard
import com.example.healthmateai.ui.theme.SurfaceCardAlt
import com.example.healthmateai.ui.theme.TextLight
import com.example.healthmateai.ui.theme.TextPrimary
import com.example.healthmateai.ui.theme.TextSecondary
import com.example.healthmateai.ui.viewmodel.PredictionViewModel
import com.example.healthmateai.ui.viewmodel.PredictionViewModelFactory

private data class InputSpec(
    val key: String,
    val label: String,
    val keyboardType: KeyboardType = KeyboardType.Number
)

private val diabetesFields = listOf(
    InputSpec("Glucose", "Glucose"),
    InputSpec("BMI", "BMI", KeyboardType.Decimal),
    InputSpec("Age", "Age"),
    InputSpec("Insulin", "Insulin"),
    InputSpec("BloodPressure", "Blood Pressure"),
    InputSpec("DiabetesPedigreeFunction", "Diabetes Pedigree Function", KeyboardType.Decimal)
)

private val heartFields = listOf(
    InputSpec("age", "Age"),
    InputSpec("ejection_fraction", "Ejection Fraction"),
    InputSpec("serum_creatinine", "Serum Creatinine", KeyboardType.Decimal),
    InputSpec("creatinine_phosphokinase", "Creatinine Phosphokinase"),
    InputSpec("serum_sodium", "Serum Sodium"),
    InputSpec("anaemia", "Anaemia (0 or 1)"),
    InputSpec("diabetes", "Diabetes (0 or 1)"),
    InputSpec("high_blood_pressure", "High Blood Pressure (0 or 1)"),
    InputSpec("smoking", "Smoking (0 or 1)"),
    InputSpec("platelets", "Platelets", KeyboardType.Decimal)
)

private val heartPrefillValues = mapOf(
    "age" to "70",
    "ejection_fraction" to "25",
    "serum_creatinine" to "2.5",
    "creatinine_phosphokinase" to "600",
    "serum_sodium" to "130",
    "anaemia" to "1",
    "diabetes" to "1",
    "high_blood_pressure" to "1",
    "smoking" to "0",
    "platelets" to "200000"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseasePredictionScreen(
    onBackClick: () -> Unit,
    showBackButton: Boolean = true,
    viewModel: PredictionViewModel = viewModel(factory = PredictionViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedDisease by rememberSaveable { mutableStateOf("diabetes") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val inputValues = remember { mutableStateMapOf<String, String>() }

    val activeFields = if (selectedDisease == "diabetes") diabetesFields else heartFields

    LaunchedEffect(selectedDisease) {
        inputValues.clear()
        activeFields.forEach { spec ->
            inputValues[spec.key] = if (selectedDisease == "heart") {
                heartPrefillValues[spec.key].orEmpty()
            } else {
                ""
            }
        }
        viewModel.clearError()
        viewModel.clearResult()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text(text = "Disease Predictor", color = TextPrimary) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Choose disease",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = selectedDisease.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Disease") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) }
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    listOf("diabetes", "heart").forEach { disease ->
                        DropdownMenuItem(
                            text = { Text(disease.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                selectedDisease = disease
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = if (selectedDisease == "diabetes") {
                    "Enter diabetes-related values"
                } else {
                    "Enter heart-related values"
                },
                color = TextSecondary,
                fontSize = 13.sp
            )

            activeFields.forEach { spec ->
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = inputValues[spec.key].orEmpty(),
                    onValueChange = { inputValues[spec.key] = it },
                    label = { Text(spec.label) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = spec.keyboardType
                    )
                )
            }

            Button(
                onClick = {
                    val missingField = activeFields.firstOrNull { spec ->
                        inputValues[spec.key].isNullOrBlank()
                    }
                    if (missingField != null) {
                        Toast.makeText(
                            context,
                            "Please enter ${missingField.label}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val parsedInputs = mutableMapOf<String, Any>()
                    activeFields.forEach { spec ->
                        val rawValue = inputValues[spec.key].orEmpty().trim()
                        val numberValue = rawValue.toDoubleOrNull()
                        if (numberValue == null) {
                            Toast.makeText(
                                context,
                                "Invalid number for ${spec.label}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        parsedInputs[spec.key] = numberValue
                    }

                    viewModel.predict(selectedDisease, parsedInputs)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Predicting...")
                } else {
                    Text(text = "Predict")
                }
            }

            uiState.errorMessage?.let { errorMessage ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3A1E1E)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFFB4B4),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            uiState.result?.let { result ->
                PredictionResultCard(
                    disease = selectedDisease,
                    probability = result.probability,
                    risk = result.risk,
                    factors = result.factors,
                    advice = result.advice
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PredictionResultCard(
    disease: String,
    probability: Double,
    risk: String,
    factors: List<String>,
    advice: String
) {
    val riskColor = when (risk.lowercase()) {
        "high" -> Color(0xFFE53935)
        "medium" -> Color(0xFFF9A825)
        else -> AccentGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${disease.replaceFirstChar { it.uppercase() }} AI Report",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RiskChip(text = risk, color = riskColor)
                RiskChip(text = String.format("%.2f%%", probability * 100), color = AccentBlue)
            }

            Text(
                text = "Key Factors",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            factors.forEach { factor ->
                Text(
                    text = "• $factor",
                    color = TextLight,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "Recommendation",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp),
                colors = CardDefaults.cardColors(containerColor = BgDarkAlt),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = advice,
                    color = TextLight,
                    modifier = Modifier
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
private fun RiskChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}