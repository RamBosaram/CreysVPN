package com.creysvpn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collect



// --- PALETTE ---
val MidnightPurple = Color(0xFF120E24)
val DeepViolet = Color(0xFF1E1736)

// Colors
val NeonGreen = Color(0xFF00FF9D)
val DeepGreenBg = Color(0xFF051A10)
val SwitchOff = Color(0xFF2E274F)

val TextDim = Color(0xFF6B6585)
val White = Color(0xFFFFFFFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                VpnScreen()
            }
        }
    }
}

@Composable
fun VpnScreen() {
    var isConnected by remember { mutableStateOf(false) }
    var isCrashTriggerEnabled by remember { mutableStateOf(false) }

    // --- COLOR ANIMATION ---
    val activeColor by animateColorAsState(if (isConnected) NeonGreen else White, tween(800), label = "mainColor")

    // Background: Deep Purple (Off) -> Dark Green (On)
    val bgColor1 by animateColorAsState(if (isConnected) DeepGreenBg else DeepViolet, tween(1000), label = "bg1")
    val bgColor2 by animateColorAsState(if (isConnected) Color(0xFF000F05) else MidnightPurple, tween(1000), label = "bg2")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(bgColor1, bgColor2),
                    radius = 1500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Use systemBarsPadding to respect status bar/nav bar
                .systemBarsPadding()
                .padding(horizontal = 30.dp, vertical = 24.dp), // Reduced vertical padding
            horizontalAlignment = Alignment.CenterHorizontally
            // Removed verticalArrangement here, we will use spacers with weight
        ) {

            // --- 1. HEADER ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(10.dp))

                Box(contentAlignment = Alignment.Center) {
                    // GLOW BEHIND ICON
                    val glowAlpha by animateFloatAsState(if (isConnected) 0.4f else 0.15f, tween(1000), label = "glow")
                    val glowColor = if (isConnected) NeonGreen else DeepViolet

                    Canvas(modifier = Modifier.size(150.dp)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(glowColor.copy(alpha = glowAlpha), Color.Transparent),
                                center = center,
                                radius = size.minDimension / 2
                            )
                        )
                    }

                    // --- ICON (ORIGINAL COLOR) ---
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        colorFilter = null,
                        modifier = Modifier.size(90.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- GRADIENT TEXT ---
                val textGradient = Brush.linearGradient(
                    colors = if (isConnected)
                        listOf(Color.White, NeonGreen, Color.White)
                    else
                        listOf(Color.White, Color(0xFFAAAAAA), Color.White),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    tileMode = TileMode.Mirror
                )

                Text(
                    text = "CREYS VPN",
                    style = TextStyle(
                        brush = textGradient,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        shadow = if(isConnected) Shadow(
                            color = NeonGreen,
                            offset = Offset.Zero,
                            blurRadius = 60f
                        ) else Shadow(
                            color = Color.White.copy(alpha = 0.5f),
                            offset = Offset.Zero,
                            blurRadius = 15f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Brawl Stars DDoS Attacker",
                    color = TextDim,
                    fontSize = 11.sp,
                    letterSpacing = 3.sp
                )
            }

            // --- SPACER WEIGHT 1 ---
            // This pushes the button into the center of available space
            Spacer(modifier = Modifier.weight(1f))

            // --- 2. BUTTON ---
            Box(contentAlignment = Alignment.Center) {
                BigConnectButton(isConnected = isConnected) {
                    isConnected = !isConnected
                }
            }

            // --- SPACER WEIGHT 1 ---
            Spacer(modifier = Modifier.weight(1f))

            // --- 3. STATUS TEXT ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isConnected) "CONNECTED" else "DISCONNECTED",
                    color = activeColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp,
                    style = TextStyle(
                        shadow = if(isConnected) Shadow(NeonGreen, Offset.Zero, 30f) else null
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if(isConnected) {
                        PulsingDot(0, NeonGreen)
                        PulsingDot(300, NeonGreen)
                        PulsingDot(600, NeonGreen)
                    } else {
                        val dotColor = Color(0xFF332D47)
                        Box(modifier = Modifier.size(5.dp).background(dotColor, CircleShape))
                        Box(modifier = Modifier.size(5.dp).background(dotColor, CircleShape))
                        Box(modifier = Modifier.size(5.dp).background(dotColor, CircleShape))
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- 4. BOTTOM CARDS ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Main Status Card
                PremiumCard(isConnected)

                // New Crash Trigger Slider Card
                SettingsCard(
                    title = "Crash Movement",
                    subtitle = "Trigger Button Logic",
                    isChecked = isCrashTriggerEnabled,
                    onCheckedChange = { isCrashTriggerEnabled = it }
                )
            }
        }
    }
}

// --- NEW COMPONENT: SETTINGS CARD WITH SLIDER ---
@Composable
fun SettingsCard(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A1530).copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                color = if(isChecked) NeonGreen.copy(alpha = 0.3f) else Color(0xFF2E274F),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(subtitle, color = TextDim, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    title,
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Custom Neon Slider
            CyberSwitch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun CyberSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Animation vars
    val thumbOffset by animateDpAsState(if (checked) 22.dp else 2.dp, tween(400), label = "offset")
    val trackColor by animateColorAsState(if (checked) NeonGreen.copy(alpha=0.2f) else SwitchOff, tween(400), label = "track")
    val thumbColor by animateColorAsState(if (checked) NeonGreen else Color(0xFF554F6D), tween(400), label = "thumb")

    // Using Dp for elevation/shadow size
    val thumbGlow by animateDpAsState(if (checked) 10.dp else 0.dp, tween(400), label = "glow")

    Box(
        modifier = Modifier
            .width(44.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp, horizontal = 0.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // The moving thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                // FIXED SHADOW HERE:
                .shadow(elevation = thumbGlow, shape = CircleShape, spotColor = NeonGreen)
                .background(thumbColor, CircleShape)
        )
    }
}

// --- EXISTING COMPONENTS ---

@Composable
fun BigConnectButton(isConnected: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val mistScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected) 1.5f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "mist"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "btnScale")

    // REDUCED SIZE SLIGHTLY TO FIT SMALLER SCREENS (300 -> 260)
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {

        // Mist/Glow
        val glowColor = if(isConnected) NeonGreen else DeepViolet
        val glowAlpha = if(isConnected) 0.3f else 0.1f

        Canvas(modifier = Modifier.fillMaxSize().scale(mistScale)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor.copy(alpha = glowAlpha), Color.Transparent),
                    center = center,
                    radius = size.minDimension / 2
                )
            )
        }

        // BUTTON
        Box(
            modifier = Modifier
                .scale(buttonScale)
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = if(isConnected)
                            listOf(Color(0xFF004422), Color(0xFF001108))
                        else
                            listOf(Color(0xFF262042), Color(0xFF17122E))
                    )
                )
                .border(1.dp, if(isConnected) NeonGreen.copy(0.3f) else Color(0xFF332D47), CircleShape)
                .clickable(interactionSource = interactionSource, indication = null) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Power",
                tint = if(isConnected) NeonGreen else White,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Composable
fun InteractionSource.collectIsPressedAsState(): State<Boolean> {
    val isPressed = remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed.value = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed.value = false
            }
        }
    }
    return isPressed
}

@Composable
fun PulsingDot(delay: Int, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "dot"
    )

    Box(
        modifier = Modifier
            .size(5.dp)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

@Composable
fun PremiumCard(isConnected: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1A1530).copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                color = if(isConnected) NeonGreen.copy(alpha = 0.3f) else Color(0xFF2E274F),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Status", color = TextDim, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if(isConnected) NeonGreen else TextDim, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if(isConnected) "Protected" else "Unprotected",
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFF332D47)))

            Column(horizontalAlignment = Alignment.End) {
                Text("Server", color = TextDim, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Finland",
                    color = White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
