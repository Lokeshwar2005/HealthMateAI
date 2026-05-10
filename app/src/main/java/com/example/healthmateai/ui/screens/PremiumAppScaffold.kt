package com.example.healthmateai.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthmateai.ai.HealthInsightsViewModel
import com.example.healthmateai.ui.navigation.AppRoutes
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.BgDarkAlt
import com.example.healthmateai.ui.theme.GradientEnd
import com.example.healthmateai.ui.theme.GradientStart
import com.example.healthmateai.ui.viewmodel.PredictionViewModel
import com.example.healthmateai.ui.viewmodel.PredictionViewModelFactory

private data class AppTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val primaryTabs = listOf(
    AppTab(AppRoutes.DASHBOARD, "Dashboard", Icons.Default.GridView),
    AppTab(AppRoutes.PREDICTION, "Predict", Icons.Default.Timeline),
    AppTab(AppRoutes.PROFILE, "Profile", Icons.Default.Person)
)

@Composable
fun PremiumAppScaffold(
    userName: String?,
    userEmail: String?,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val insightsViewModel: HealthInsightsViewModel = viewModel()
    val predictionViewModel: PredictionViewModel = viewModel(factory = PredictionViewModelFactory)

    val snapshot by insightsViewModel.snapshot.collectAsState()
    val predictionState by predictionViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = primaryTabs.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        containerColor = BgDark,
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(26.dp),
                    color = BgDarkAlt,
                    shadowElevation = 14.dp,
                    tonalElevation = 6.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        primaryTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            val animatedScale by animateFloatAsState(
                                targetValue = if (selected) 1.1f else 1f,
                                animationSpec = tween(220),
                                label = "navScale"
                            )
                            val indicatorWidth by animateDpAsState(
                                targetValue = if (selected) 24.dp else 0.dp,
                                animationSpec = tween(220),
                                label = "navIndicator"
                            )
                            val containerSize by animateDpAsState(
                                targetValue = if (selected) 38.dp else 32.dp,
                                animationSpec = tween(220),
                                label = "navContainerSize"
                            )

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent,
                                    selectedIconColor = Color(0xFF58E5FF),
                                    selectedTextColor = Color(0xFF58E5FF),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                icon = {
                                    androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .width(indicatorWidth)
                                                .height(3.dp)
                                                .background(
                                                    brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                                                    shape = RoundedCornerShape(50)
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(containerSize)
                                                .shadow(
                                                    elevation = if (selected) 10.dp else 0.dp,
                                                    shape = CircleShape,
                                                    clip = false
                                                )
                                                .background(
                                                    brush = if (selected) {
                                                        Brush.linearGradient(
                                                            listOf(
                                                                GradientStart.copy(alpha = 0.34f),
                                                                GradientEnd.copy(alpha = 0.34f)
                                                            )
                                                        )
                                                    } else {
                                                        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                                    },
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.label,
                                                modifier = Modifier.size((19.dp.value * animatedScale).dp)
                                            )
                                        }
                                    }
                                },
                                label = {
                                    Text(tab.label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.DASHBOARD,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(AppRoutes.DASHBOARD) {
                DashboardScreen(
                    userName = userName ?: "User",
                    uiState = predictionState,
                    onQuickAction = { action ->
                        val route = when (action) {
                            QuickAction.DietPlanner -> AppRoutes.DIET_PLANNER
                            QuickAction.MedicineReminder -> AppRoutes.MEDICINE_REMINDER
                            QuickAction.Chatbot -> AppRoutes.CHATBOT
                        }
                        navController.navigate(route)
                    }
                )
            }

            composable(AppRoutes.PREDICTION) {
                PredictionScreen(
                    contentPadding = PaddingValues(bottom = 12.dp),
                    onPrediction = { disease, probability, inputs ->
                        insightsViewModel.updatePrediction(disease, probability, inputs)
                    },
                    viewModel = predictionViewModel
                )
            }

            composable(AppRoutes.PROFILE) {
                ProfileScreen(
                    userName = userName ?: "User",
                    userEmail = userEmail ?: "user@healthmate.ai",
                    onLogout = onLogout,
                    healthSnapshot = snapshot
                )
            }

            composable(AppRoutes.DIET_PLANNER) {
                DietPlannerScreen(
                    contentPadding = PaddingValues(0.dp),
                    snapshot = snapshot
                )
            }

            composable(AppRoutes.MEDICINE_REMINDER) {
                MedicineReminderScreen(contentPadding = PaddingValues(0.dp))
            }

            composable(AppRoutes.CHATBOT) {
                ChatbotScreen(contentPadding = PaddingValues(0.dp))
            }
        }
    }
}
