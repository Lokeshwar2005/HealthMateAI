package com.example.healthmateai.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmateai.ui.model.SplashContent
import com.example.healthmateai.ui.theme.AccentCyan
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.SurfaceCard
import com.example.healthmateai.ui.theme.TextPrimary
import com.example.healthmateai.ui.theme.TextSecondary

@Composable
fun SplashScreen(content: SplashContent) {
    val transition = rememberInfiniteTransition(label = "splash_pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_scale"
    )
    val shimmer by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_shimmer"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(pulse)
                    .background(SurfaceCard, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(
                        color = AccentCyan.copy(alpha = 0.25f),
                        radius = size.minDimension / 2.2f,
                        center = center
                    )
                    drawCircle(
                        color = AccentCyan.copy(alpha = shimmer),
                        radius = size.minDimension / 4.2f,
                        center = center
                    )
                }
            }

            if (content.badge.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .background(AccentCyan.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = content.badge,
                        color = AccentCyan,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = content.title,
                fontSize = 20.sp,
                color = TextPrimary
            )
            Text(
                text = content.subtitle,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.alpha(0.9f)
            )
        }
    }
}
