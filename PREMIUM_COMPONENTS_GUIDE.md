# Premium UI Components - Code Reference

## Quick Start: Copy-Paste Examples

### Example 1: Using Premium Header with Icon

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Heart
import com.example.healthmateai.ui.components.PremiumHeaderCard

// In your Composable function:
PremiumHeaderCard(
    title = "Disease Prediction",
    subtitle = "AI-guided assessment with step-by-step inputs",
    icon = Icons.Default.Heart,
    modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = AppSpacing.xl)
)
```

---

### Example 2: Button Pair (Back & Next)

```kotlin
import com.example.healthmateai.ui.components.PremiumBackButton
import com.example.healthmateai.ui.components.PremiumGlowButton

Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = Color(0xFF172444),
    shadowElevation = 8.dp,
    tonalElevation = 2.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PremiumBackButton(
            onClick = { /* Handle back */ },
            enabled = currentStep > 0,
            modifier = Modifier.weight(0.95f)
        )

        PremiumGlowButton(
            text = "Next",
            onClick = { /* Handle next */ },
            modifier = Modifier.weight(1.2f)
        )
    }
}
```

---

### Example 3: Screen Layout with Proper Spacing

```kotlin
import com.example.healthmateai.ui.components.AppSpacing
import com.example.healthmateai.ui.components.AppPadding

Column(
    modifier = Modifier
        .fillMaxSize()
        .background(BgDark)
        .padding(
            horizontal = AppPadding.screenHorizontal,
            vertical = AppPadding.screenVertical
        )
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)
) {
    Spacer(modifier = Modifier.height(AppSpacing.md))
    
    // Header
    PremiumHeaderCard(
        title = "Section Title",
        subtitle = "Section subtitle"
    )
    
    // Spacing: xl (20dp) between sections
    
    // Content
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppPadding.containerLarge),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column {
            // Your content here
        }
    }
    
    // Spacing: xl (20dp)
    
    // Buttons
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PremiumBackButton(modifier = Modifier.weight(0.95f))
        PremiumGlowButton(
            text = "Continue",
            onClick = { },
            modifier = Modifier.weight(1.2f)
        )
    }
}
```

---

### Example 4: Loading State Button

```kotlin
var isLoading by remember { mutableStateOf(false) }

PremiumGlowButton(
    text = "Predict",
    onClick = {
        isLoading = true
        // Make your API call
        // isLoading = false when done
    },
    isLoading = isLoading,
    enabled = !isLoading
)
```

---

### Example 5: Disabled Button

```kotlin
var stepCompleted by remember { mutableStateOf(false) }

PremiumGlowButton(
    text = "Next",
    onClick = { /* Navigate */ },
    enabled = stepCompleted  // Button grays out if false
)
```

---

### Example 6: Custom Color Buttons

For custom colors, create wrapper functions:

```kotlin
@Composable
fun DangerGlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
            .height(52.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFEF4444),  // Error Red
                        Color(0xFFFF6B6B)   // Bright Red
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

// Usage:
DangerGlowButton(
    text = "Delete",
    onClick = { /* Handle delete */ }
)
```

---

## Color Constants Reference

### Import in your files:

```kotlin
import com.example.healthmateai.ui.theme.*
```

### Using Color Constants:

```kotlin
// Main accent colors
val primaryCyan = AccentCyan           // #3DD6D0
val brightCyan = Color(0xFF54E4FF)     // #54E4FF
val primaryBlue = AccentBlue            // #5B7CFF

// Backgrounds
val darkBg = BgDark                     // #0E1220
val darkBgAlt = BgDarkAlt               // #12182A
val cardSurface = SurfaceCard           // #1A2135

// Text
val textPrimary = TextPrimary           // #E7EAF2
val textSecondary = TextSecondary       // #9AA4B5
val textLight = TextLight               // #6F7787

// Cyber theme
val cyberNavy = CyberNavy               // #0F1E3C
val cyberDeepBlue = CyberDeepBlue       // #1A3A52
val cyberCyan = CyberCyan               // #3DD6D0

// Status
val successGreen = SuccessGreen         // #10B981
val errorRed = ErrorRed                 // #EF4444
val warningYellow = WarningYellow       // #EAB308

// Apply alpha with extension:
AccentCyan.withAlpha(0.5f)  // 50% transparent cyan
```

---

## Spacing System Reference

### Using AppSpacing:

```kotlin
import com.example.healthmateai.ui.components.AppSpacing

// Minimal: 4dp
Spacer(modifier = Modifier.height(AppSpacing.xs))

// Small: 8dp
Spacer(modifier = Modifier.height(AppSpacing.sm))

// Medium: 12dp
Spacer(modifier = Modifier.height(AppSpacing.md))

// Large: 16dp
Spacer(modifier = Modifier.height(AppSpacing.lg))

// Extra Large: 20dp (most common)
Spacer(modifier = Modifier.height(AppSpacing.xl))

// Double Extra Large: 24dp
Spacer(modifier = Modifier.height(AppSpacing.xxl))

// Triple Extra Large: 32dp
Spacer(modifier = Modifier.height(AppSpacing.xxxl))

// In Column arrangements:
Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)) {
    // Content with 20dp between items
}

// In Row arrangements:
Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
    // Content with 16dp between items
}
```

---

## Padding System Reference

### Using AppPadding:

```kotlin
import com.example.healthmateai.ui.components.AppPadding

// Screen-level padding (16dp horizontal, 12dp vertical)
Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(
            horizontal = AppPadding.screenHorizontal,
            vertical = AppPadding.screenVertical
        )
)

// Card padding options
Box(modifier = Modifier.padding(AppPadding.containerSmall)) {
    // 12dp horizontal, 8dp vertical
}

Box(modifier = Modifier.padding(AppPadding.containerMedium)) {
    // 16dp horizontal, 12dp vertical
}

Box(modifier = Modifier.padding(AppPadding.containerLarge)) {
    // 20dp horizontal, 16dp vertical
}

// Direct usage
Text(
    text = "Sample",
    modifier = Modifier.padding(horizontal = AppPadding.screenHorizontal)
)
```

---

## Typography Reference

### Material 3 Typography in HealthMateAI Theme:

```kotlin
// Display sizes (not commonly used)
MaterialTheme.typography.displayLarge      // 57sp

// Headline sizes
MaterialTheme.typography.headlineLarge     // 32sp, ExtraBold
MaterialTheme.typography.headlineMedium    // 24sp, Bold
MaterialTheme.typography.headlineSmall     // 20sp, Bold

// Title sizes
MaterialTheme.typography.titleLarge        // 20sp, SemiBold
MaterialTheme.typography.titleMedium       // 16sp, Medium
MaterialTheme.typography.titleSmall        // 14sp, SemiBold

// Body sizes
MaterialTheme.typography.bodyLarge         // 16sp, Normal
MaterialTheme.typography.bodyMedium        // 14sp, Normal

// Label sizes
MaterialTheme.typography.labelLarge        // 13sp, SemiBold

// Usage in Text:
Text(
    text = "Main Title",
    style = MaterialTheme.typography.headlineSmall,
    fontWeight = FontWeight.Bold,
    color = TextPrimary
)
```

---

## Gradient Examples

### Horizontal Gradient (Buttons):

```kotlin
Brush.horizontalGradient(
    colors = listOf(Color(0xFF25C2BE), Color(0xFF58E5FF))
)
```

### Vertical Gradient (Background):

```kotlin
Brush.linearGradient(
    colors = listOf(Color(0xFF0F1E3C), Color(0xFF1A3A52)),
    start = Offset(0f, 0f),
    end = Offset(0f, 500f)
)
```

### Sweep Gradient (Circular):

```kotlin
Brush.sweepGradient(
    colors = listOf(Color(0xFF25C2BE), Color(0xFF58E5FF), Color(0xFF25C2BE))
)
```

### Radial Gradient (From center):

```kotlin
Brush.radialGradient(
    colors = listOf(Color(0xFF25C2BE), Color(0xFF1A3A52)),
    radius = 200f
)
```

---

## Shadow & Elevation Reference

### Button-style Shadow:

```kotlin
.shadow(
    elevation = 12.dp,
    shape = RoundedCornerShape(14.dp),
    clip = false  // Important for glow effect
)
```

### Elevation Levels:

```kotlin
// Minimal elevation
elevation = 2.dp

// Standard elevation
elevation = 4.dp

// Medium elevation
elevation = 8.dp

// High elevation (glow effect)
elevation = 12.dp

// Extra high elevation
elevation = 16.dp
```

### Usage in Card:

```kotlin
Card(
    elevation = CardDefaults.cardElevation(
        defaultElevation = 8.dp,
        pressedElevation = 4.dp,
        hoveredElevation = 10.dp,
        focusedElevation = 8.dp,
        disabledElevation = 0.dp
    )
)
```

---

## Animation Reference

### Button Press Animation:

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val pressed by interactionSource.collectIsPressedAsState()
val scale by animateFloatAsState(
    targetValue = if (pressed) 0.97f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "buttonScale"
)

Button(
    interactionSource = interactionSource,
    modifier = Modifier
        .graphicsLayer { scaleX = scale; scaleY = scale }
) {
    // Button content
}
```

### Smooth Transitions:

```kotlin
// 280ms transition
animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(280),
    label = "progress"
)

// 700ms transition with easing
animateFloatAsState(
    targetValue = value,
    animationSpec = tween(durationMillis = 700, easing = EaseOutCubic),
    label = "value"
)
```

---

## Common Patterns

### Full-Width Button with Padding:

```kotlin
PremiumGlowButton(
    text = "Action",
    onClick = { },
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = AppPadding.screenHorizontal)
)
```

### Centered Content:

```kotlin
Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
) {
    Text("Centered Text")
}
```

### Card with Elevated Shadow:

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            clip = false
        ),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = SurfaceCard
    )
) {
    Column(modifier = Modifier.padding(AppPadding.containerLarge)) {
        // Content
    }
}
```

---

## Debugging Tips

### Check Component Layout:

```kotlin
// Add background to see bounds
.background(Color.Red.copy(alpha = 0.3f))

// Add border to see edges
.border(1.dp, Color.Blue)
```

### Preview Different States:

```kotlin
@Preview(showBackground = true)
@Composable
fun ButtonPreview() {
    HealthMateAITheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            PremiumGlowButton(text = "Enabled", onClick = {})
            PremiumGlowButton(text = "Disabled", onClick = {}, enabled = false)
            PremiumGlowButton(text = "Loading", onClick = {}, isLoading = true)
        }
    }
}
```

---

## Migration Checklist

Converting existing code to use new components:

- [ ] Import new components from `UIComponents.kt`
- [ ] Import color constants from `Theme.kt`
- [ ] Replace old header with `PremiumHeaderCard`
- [ ] Replace old buttons with `PremiumGlowButton` / `PremiumBackButton`
- [ ] Apply `AppSpacing` to `Arrangement.spacedBy()`
- [ ] Apply `AppPadding` to `padding()` calls
- [ ] Update colors to use new palette constants
- [ ] Test on multiple screen sizes
- [ ] Verify text contrast ratios
- [ ] Test animations on different devices

---

## Support & Customization

### To create a new button variant:

1. Copy `PremiumGlowButton` code
2. Change colors, sizes, or shapes as needed
3. Create a new composable function
4. Keep the same animation patterns

### To create a new header variant:

1. Copy `PremiumHeaderCard` code
2. Modify padding, gradient, or styling
3. Create a new composable function
4. Maintain the glow border effect

---

## Performance Notes

- All components use `remember {}` to prevent unnecessary recompositions
- Animations use efficient `animateFloatAsState` which only recomposes on value change
- Shadow and elevation effects are hardware-optimized
- Gradients are computed at composition time, not during draw

No performance issues expected on devices targeting Android API 21+.
