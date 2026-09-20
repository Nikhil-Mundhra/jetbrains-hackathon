package com.yallapark.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import com.yallapark.ui.theme.*

/**
 * YallaPark UAE Smart Mobility Design System
 * into 100% Pure Kotlin Compose Multiplatform.
 *
 * Implements:
 * - Clean, modern Light Theme (white surfaces, gray-50 sections, crisp contrast)
 * - Brand Navbar with dual-login gateway (Driver App vs Provider Console)
 * - Hero Section with instant booking tagline and trust badges
 * - Smart Parking Intelligence (Live nearby bays & interactive AI predictions)
 * - Available Parking Spots Explorer (Real-time search + interactive UAE map)
 * - "Everything you need to park smarter" 6-Pillar feature showcase
 * - "How Yalla Park Works" 4-step timeline
 * - UAE'S Largest Parking Network metrics banner
 * - "Loved by UAE Drivers" testimonials
 * - "Stop Circling. Start Parking." Call-to-action banner
 * - Full UAE multi-city footer
 *
 * All components strictly adhere to tight uniform 8.dp corner radiuses.
 */

data class YallaSpot(
    val id: Int,
    val name: String,
    val area: String,
    val available: Int,
    val total: Int,
    val rate: String,
    val walkTime: String
)

data class FeatureItem(
    val title: String,
    val description: String,
    val categoryTag: String,
    val bgColor: Color,
    val iconTint: Color
)

data class HowItWorksStep(
    val stepNumber: String,
    val title: String,
    val description: String,
    val badgeColor: Color
)

data class TestimonialItem(
    val name: String,
    val role: String,
    val quote: String,
    val rating: Int,
    val initials: String
)

@Composable
fun LandingHomeScreen(
    onLoginAsDriverClick: () -> Unit,
    onLoginAsProviderClick: () -> Unit,
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Theme-derived colors
    val pageBg = if (isDarkMode) Color(0xFF0B1120) else Color(0xFFF9FAFB)
    val navbarBg = if (isDarkMode) Color(0xFF111827) else Color.White
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val subtleCardBg = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val borderCol = if (isDarkMode) Color(0xFF334155) else Color(0xFFE5E7EB)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF4B5563)
    val sectionAltBg = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val sectionBg = if (isDarkMode) Color(0xFF0B1120) else Color.White

    // Interactive Map Search State
    var searchQuery by remember { mutableStateOf("") }
    var isSatelliteMode by remember { mutableStateOf(false) }

    val spots = remember {
        listOf(
            YallaSpot(1, "Dubai Mall P1", "Downtown Dubai", 45, 100, "AED 20/hr", "3 min walk"),
            YallaSpot(2, "Marina Walk Promenade", "Dubai Marina", 23, 75, "AED 15/hr", "2 min walk"),
            YallaSpot(3, "JBR The Walk Plaza", "Jumeirah Beach", 12, 50, "AED 25/hr", "1 min walk"),
            YallaSpot(4, "Mall of the Emirates P2", "Al Barsha", 67, 150, "AED 20/hr", "4 min walk"),
            YallaSpot(5, "DIFC Gate Village P3", "Financial Centre", 5, 40, "AED 25/hr", "2 min walk"),
            YallaSpot(6, "Al Karama RTA Zone A", "Al Karama", 31, 80, "AED 10/hr", "5 min walk")
        )
    }

    var selectedSpot by remember { mutableStateOf(spots[0]) }

    // Filtered spots
    val filteredSpots = remember(searchQuery) {
        if (searchQuery.isBlank()) spots
        else spots.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.area.contains(searchQuery, ignoreCase = true)
        }
    }

    // AI Parking Insights Dynamic State
    val aiInsightsList = remember {
        listOf(
            "Based on your usual schedule, reserve parking at Dubai Mall by 6 PM Thursdays — availability drops 80% after 7 PM.",
            "Demand spike detected at DIFC Gate Village: 5 spots remaining. Reserving now locks your 15-minute guaranteed slot.",
            "City Walk parking will reach full capacity in ~45 minutes. Reserve a Mall of the Emirates bay instead — only 8 min further.",
            "RTA Zone A in Al Karama has optimal turnover today: 31 open bays with standard AED 10/hr rate."
        )
    }
    var currentAiInsightIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            // ================= 1. TOP BRAND NAVBAR & DUAL LOGIN BAR (Rh) =================
            item {
                Surface(
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo (Yalla in TechBlue, Park in EcoGreen)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(EcoGreen500, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("P", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(color = TechBlue500, fontWeight = FontWeight.Black)) {
                                        append("Yalla")
                                    }
                                    withStyle(SpanStyle(color = EcoGreen500, fontWeight = FontWeight.Black)) {
                                        append("Park")
                                    }
                                },
                                fontSize = 22.sp
                            )
                        }

                        // Actions: Theme Toggle + Dual Gateways
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Theme Toggle Chip
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF3F4F6),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isDarkMode) Color(0xFF334155) else Color(0xFFE5E7EB),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onToggleDarkMode() }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = if (isDarkMode) "Light Mode" else "Dark Mode",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF1F2937)
                                )
                            }

                            OutlinedButton(
                                onClick = onLoginAsProviderClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TechBlue600),
                                border = BorderStroke(1.dp, TechBlue500),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Provider", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onLoginAsDriverClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreen500),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Driver App", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // ================= 2. HERO SECTION (KO) =================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFFF0FDF4), Color.White))
                        )
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Initiative Pill Badge
                        Box(
                            modifier = Modifier
                                 .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                 .border(1.dp, EcoGreen500.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                 .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "UAE SMART PARKING PLATFORM",
                                color = EcoGreen700,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Main Headline
                        Text(
                            text = buildAnnotatedString {
                                append("Find Parking. ")
                                withStyle(SpanStyle(color = EcoGreen500)) {
                                append("Book Instantly.")
                            }
                            append(" Park Smarter.")
                        },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subtitle
                    Text(
                        text = "Yalla Park helps you discover, reserve and pay for parking spots across the UAE in seconds. Skip the circling, save time, and park with confidence.",
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hero Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onLoginAsDriverClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen500)
                        ) {
                            Text("Find Parking →", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onLoginAsProviderClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TechBlue600),
                            border = BorderStroke(2.dp, TechBlue500)
                        ) {
                            Text("List Your Spot", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Trust Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HeroTrustBadge("5,000+ Spots", Color(0xFFE8F5E9), EcoGreen700)
                        HeroTrustBadge("Book in <30s", Color(0xFFFEF3C7), Sand700)
                        HeroTrustBadge("Guaranteed Bay", Color(0xFFE3F2FD), TechBlue700)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Modern UAE Garage Showcase Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Dubai Downtown Hub", fontWeight = FontWeight.Bold, color = Color(0xFF111827), fontSize = 15.sp)
                                    Text("Live Vector Availability", fontSize = 12.sp, color = Color(0xFF6B7280))
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("98% Match Rate", color = EcoGreen700, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFE0F2FE), Color(0xFFDCFCE7))
                                        ),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.background(EcoGreen500, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text("STANDARD", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(modifier = Modifier.background(TechBlue500, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text("POD", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(modifier = Modifier.background(Color(0xFFE91E63), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text("PINK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(modifier = Modifier.background(Color(0xFF388E3C), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text("EV", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Real-Time Camera & Sensor Synchronized", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Turn-by-turn indoor wayfinding ready", color = Color(0xFF475569), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
            }

            // ================= 3. SMART PARKING INTELLIGENCE (QO) =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB))
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Smart Parking ")
                            withStyle(SpanStyle(color = EcoGreen500)) {
                                append("Intelligence")
                            }
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Best Spots Near You Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = TechBlue600, modifier = Modifier.size(20.dp))
                                Text("Best Spots Near You", fontWeight = FontWeight.Bold, color = Color(0xFF111827), fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            spots.take(3).forEach { spot ->
                                SpotIntelligenceRow(spot = spot)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AI Parking Insights Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = TechBlue600, modifier = Modifier.size(20.dp))
                                Text("AI Parking Insights", fontWeight = FontWeight.Bold, color = Color(0xFF111827), fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFBBDEFB), RoundedCornerShape(8.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = aiInsightsList[currentAiInsightIndex],
                                    color = Color(0xFF0D47A1),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(modifier = Modifier.size(8.dp).background(EcoGreen500, RoundedCornerShape(4.dp)))
                                    Text("Live occupancy active", fontSize = 11.sp, color = Color(0xFF4B5563))
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(modifier = Modifier.size(8.dp).background(TechBlue500, RoundedCornerShape(4.dp)))
                                    Text("Forecasting enabled", fontSize = 11.sp, color = Color(0xFF4B5563))
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    currentAiInsightIndex = (currentAiInsightIndex + 1) % aiInsightsList.size
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreen500),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Generate Next AI Insight", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // ================= 4. FIND AVAILABLE PARKING SPOTS & MAP (cB) =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Find Available ")
                            withStyle(SpanStyle(color = EcoGreen500)) {
                                append("Parking Spots")
                            }
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Search Box & Satellite Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search UAE locations...", fontSize = 13.sp, color = Color(0xFF9CA3AF)) },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EcoGreen500,
                                        unfocusedBorderColor = Color(0xFFD1D5DB),
                                        focusedTextColor = Color(0xFF111827),
                                        unfocusedTextColor = Color(0xFF111827),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    singleLine = true
                                )

                                Button(
                                    onClick = { isSatelliteMode = !isSatelliteMode },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSatelliteMode) Sand500 else Color(0xFFE5E7EB)
                                    ),
                                    modifier = Modifier.height(50.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) {
                                    Text(
                                        if (isSatelliteMode) "Satellite" else "Vector",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSatelliteMode) Color.White else Color(0xFF374151)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Spot Selector Row
                            Text("SELECT SPOT TO INSPECT:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(filteredSpots) { spot ->
                                    val isSelected = spot.id == selectedSpot.id
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) Color(0xFFE8F5E9) else Color.White,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) EcoGreen500 else Color(0xFFE5E7EB),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedSpot = spot }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Column {
                                            Text(spot.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("${spot.available} bays", fontSize = 11.sp, color = EcoGreen700, fontWeight = FontWeight.Bold)
                                                Text("•", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                                                Text(spot.rate, fontSize = 11.sp, color = TechBlue600, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interactive Map Visualizer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(
                                        if (isSatelliteMode)
                                            Brush.verticalGradient(listOf(Color(0xFF1E3A2F), Color(0xFF0F241C)))
                                        else
                                            Brush.verticalGradient(listOf(Color(0xFF0F1E33), Color(0xFF0B1728))),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                    .padding(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                "${if (isSatelliteMode) "Satellite View" else "Vector Map"} • ${selectedSpot.area}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(EcoGreen500, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("${selectedSpot.available}/${selectedSpot.total} Available", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Map Pin Mockup
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(EcoGreen500, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("P", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(selectedSpot.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${selectedSpot.rate} • ${selectedSpot.walkTime}", color = Color(0xFFBAE6FD), fontSize = 11.sp)
                                    }

                                    // Reserve CTA inside map
                                    Button(
                                        onClick = onLoginAsDriverClick,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen500),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Text("Reserve ${selectedSpot.name} (${selectedSpot.rate}) →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================= 5. EVERYTHING YOU NEED TO PARK SMARTER (JO) =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB))
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Everything you need to ")
                            withStyle(SpanStyle(color = EcoGreen500)) {
                                append("park smarter")
                            }
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Yalla Park puts every parking spot in the UAE at your fingertips — discover, reserve, navigate, and pay from a single app.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val features = listOf(
                        FeatureItem("Real-Time Availability", "See live occupancy across malls, residential towers, and street parking before you even leave home.", "LIVE", Color(0xFFE8F5E9), EcoGreen700),
                        FeatureItem("Reserve in Advance", "Book your spot hours or days ahead. Your space is guaranteed when you arrive — no more circling.", "RESERVE", Color(0xFFE3F2FD), TechBlue700),
                        FeatureItem("Turn-by-Turn Guidance", "Get directed straight to your reserved bay with in-app navigation and indoor wayfinding.", "NAV", Color(0xFFE8F5E9), EcoGreen700),
                        FeatureItem("Cashless Payments", "Pay seamlessly with Apple Pay, card, or wallet. Receipts and VAT invoices delivered instantly.", "PAY", Color(0xFFE3F2FD), TechBlue700),
                        FeatureItem("Secure & Verified", "Every listed spot is verified, monitored, and covered by our parking protection guarantee.", "VERIFIED", Color(0xFFFEF3C7), Sand700),
                        FeatureItem("UAE-Wide Coverage", "From Dubai Marina to Abu Dhabi Corniche and Sharjah — thousands of spots in one app.", "UAE", Color(0xFFE3F2FD), TechBlue700)
                    )

                    features.chunked(2).forEach { rowFeatures ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowFeatures.forEach { feat ->
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(feat.bgColor, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(feat.categoryTag, fontSize = 8.sp, fontWeight = FontWeight.Black, color = feat.iconTint)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(feat.title, fontWeight = FontWeight.Bold, color = Color(0xFF111827), fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(feat.description, color = Color(0xFF4B5563), fontSize = 11.sp, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // ================= 6. HOW YALLA PARK WORKS (tB) =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "How Yalla Park Works",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "From searching to parking, the entire experience takes under a minute. Here's how easy it is.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val steps = listOf(
                        HowItWorksStep("1", "Search your destination", "Type where you're headed — a mall, office, restaurant, or address — and we'll show every nearby parking option.", TechBlue500),
                        HowItWorksStep("2", "Pick the perfect spot", "Compare live availability, hourly rates, covered vs. open bays, EV chargers, and walking distance.", EcoGreen500),
                        HowItWorksStep("3", "Reserve & navigate", "Lock in your bay in seconds and follow turn-by-turn directions straight to it. No more endless circling.", Sand500),
                        HowItWorksStep("4", "Park & pay seamlessly", "Scan in, park, and pay automatically. Extend your stay from the app whenever you need more time.", EcoGreen500)
                    )

                    steps.forEach { step ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(step.badgeColor, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(step.stepNumber, color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(step.title, fontWeight = FontWeight.Bold, color = Color(0xFF111827), fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(step.description, color = Color(0xFF4B5563), fontSize = 12.sp, lineHeight = 17.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ================= 7. UAE'S LARGEST PARKING NETWORK STATS (rB) =================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .background(
                            Brush.horizontalGradient(listOf(TechBlue600, TechBlue800)),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "The UAE's Largest Parking Network",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Drivers across the Emirates trust Yalla Park to find them a spot — every single day.",
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("5,000+", "Parking Spots", Modifier.weight(1f))
                            StatCard("120+", "UAE Locations", Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("12 min", "Avg Time Saved", Modifier.weight(1f))
                            StatCard("99.2%", "Satisfaction", Modifier.weight(1f))
                        }
                    }
                }
            }

            // ================= 8. LOVED BY UAE DRIVERS - TESTIMONIALS (iB) =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "Loved by UAE Drivers",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tens of thousands of drivers across Dubai, Abu Dhabi, and Sharjah park smarter with Yalla Park.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val testimonials = listOf(
                        TestimonialItem("Ahmad Al Falasi", "Downtown Dubai Commuter", "I used to spend 20 minutes hunting for parking near DIFC. With Yalla Park I reserve my bay before I leave home — drive straight in, done.", 5, "AF"),
                        TestimonialItem("Sarah Johnson", "Dubai Mall Regular", "The live availability is a lifesaver on weekends. I can see exactly which level has space before I even reach the entrance.", 5, "SJ"),
                        TestimonialItem("Rajiv Mehta", "Marina Resident", "I list my building's spare visitor bay on Yalla Park and earn extra income every month. Setup took five minutes.", 4, "RM")
                    )

                    testimonials.forEach { review ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(review.initials, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TechBlue700)
                                    }
                                    Column {
                                        Text(review.name, fontWeight = FontWeight.Bold, color = Color(0xFF111827), fontSize = 13.sp)
                                        Text(review.role, color = Color(0xFF6B7280), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(review.rating) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "\"${review.quote}\"",
                                    fontSize = 12.sp,
                                    color = Color(0xFF374151),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            // ================= 9. CALL TO ACTION BANNER (bB) =================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .background(
                            Brush.linearGradient(listOf(EcoGreen600, EcoGreen700)),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Stop Circling. Start Parking.",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Join thousands of UAE drivers who save time, money, and hassle every day.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onLoginAsDriverClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                modifier = Modifier.weight(1f).height(44.dp)
                            ) {
                                Text("Find Parking Now →", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoGreen700)
                            }

                            OutlinedButton(
                                onClick = onLoginAsProviderClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(2.dp, Color.White),
                                modifier = Modifier.weight(1f).height(44.dp)
                            ) {
                                Text("List Your Spot", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CtaStatItem("7", "Emirates Covered")
                            CtaStatItem("24/7", "Driver Support")
                            CtaStatItem("100%", "Spot Guarantee")
                        }
                    }
                }
            }

            // ================= 10. FOOTER (Oh) =================
            item {
                Surface(
                    color = Color(0xFF111827),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(EcoGreen500, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("P", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                        append("Yalla")
                                    }
                                    withStyle(SpanStyle(color = EcoGreen500, fontWeight = FontWeight.Bold)) {
                                        append("Park")
                                    }
                                },
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Find and book the best parking spots across the UAE. Save time, avoid hassle, and park smarter with Yalla Park.",
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Dubai Silicon Oasis, Dubai, UAE\nEmail: info@yallapark.ae • Phone: +971 4 000 1234",
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFF374151))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("© 2026 Yalla Park. All rights reserved.", color = Color(0xFF6B7280), fontSize = 10.sp)
                            Text("Dubai • Abu Dhabi • Sharjah", color = Color(0xFF6B7280), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroTrustBadge(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SpotIntelligenceRow(spot: YallaSpot) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(spot.name, color = Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("${spot.area} • ${spot.walkTime}", color = Color(0xFF6B7280), fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("${spot.available} spots", color = EcoGreen700, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(spot.rate, color = TechBlue600, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun CtaStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
    }
}
