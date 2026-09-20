package com.yallapark

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.ai.YallaAiViewModel
import com.yallapark.data.repository.YallaParkRepositoryImpl
import com.yallapark.domain.model.AccountType
import com.yallapark.domain.model.ParkingLot
import com.yallapark.domain.model.UserRole
import com.yallapark.presentation.viewmodel.AdminViewModel
import com.yallapark.presentation.viewmodel.AuthViewModel
import com.yallapark.presentation.viewmodel.BookingViewModel
import com.yallapark.presentation.viewmodel.MapViewModel
import com.yallapark.ui.screens.admin.AdminDashboardScreen
import com.yallapark.ui.screens.admin.BayConfigScreen
import com.yallapark.ui.screens.admin.ManageLotsScreen
import com.yallapark.ui.screens.ai.YallaAiScreen
import com.yallapark.ui.screens.auth.LoginScreen
import com.yallapark.ui.screens.driver.ActiveSessionScreen
import com.yallapark.ui.screens.driver.BookingFlowScreen
import com.yallapark.ui.screens.driver.LotDetailScreen
import com.yallapark.ui.screens.driver.MapExplorerScreen
import com.yallapark.ui.screens.driver.RewardsScreen
import com.yallapark.ui.screens.home.LandingHomeScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import com.yallapark.ui.theme.YallaGoldSecondary
import com.yallapark.ui.theme.YallaParkTheme
import com.yallapark.ui.theme.YallaTealDark
import com.yallapark.ui.theme.YallaTealLight
import com.yallapark.ui.theme.YallaTealPrimary

enum class DriverTab(val label: String) {
    MAP("Explore"),
    ACTIVE("My Pass"),
    AI("Yalla AI"),
    REWARDS("Rewards")
}

enum class ScreenState {
    HOME_LANDING,
    LOGIN,
    DRIVER_FEED,
    LOT_DETAIL,
    BOOKING_FLOW,
    ADMIN_DASHBOARD,
    ADMIN_ADD_LOT,
    ADMIN_BAY_CONFIG
}

@Composable
fun App() {
    val repository = remember { YallaParkRepositoryImpl() }
    val mapViewModel = remember { MapViewModel(repository) }
    val bookingViewModel = remember { BookingViewModel(repository) }
    val adminViewModel = remember { AdminViewModel(repository) }
    val aiViewModel = remember { YallaAiViewModel() }
    val authViewModel = remember { AuthViewModel() }

    var isAdminMode by remember { mutableStateOf(false) }
    var currentDriverTab by remember { mutableStateOf(DriverTab.MAP) }
    var currentScreen by remember { mutableStateOf(ScreenState.HOME_LANDING) }
    var viewingLot by remember { mutableStateOf<ParkingLot?>(null) }
    var initialLoginAccountType by remember { mutableStateOf(AccountType.MOTORIST_DRIVER) }
    var isDarkMode by remember { mutableStateOf(false) }

    val activeReservation by bookingViewModel.activeReservation.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    YallaParkTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                // Top Global Bar with Brand, User Session & Switcher
                Surface(
                    color = YallaTealDark,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Logo & Title (Clicking navigates to Home)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { currentScreen = ScreenState.HOME_LANDING }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(YallaTealLight, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "P",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = YallaTealDark
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "YallaPark",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0x33F59E0B), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("DUBAI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = YallaGoldSecondary)
                                    }
                                }
                                Text(
                                    text = "Smart Mobility & Predictive Parking",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Right Actions: User status / Mode Switcher / Home
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Home Shortcut button
                            if (currentScreen != ScreenState.HOME_LANDING) {
                                TextButton(
                                    onClick = { currentScreen = ScreenState.HOME_LANDING },
                                    colors = ButtonDefaults.textButtonColors(contentColor = YallaTealLight)
                                ) {
                                    Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            // User Profile Chip or Sign In
                            if (currentUser != null) {
                                val user = currentUser!!
                                Box(
                                    modifier = Modifier
                                        .background(Color(0x3300897B), RoundedCornerShape(8.dp))
                                        .border(1.dp, YallaTealPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            initialLoginAccountType = user.accountType
                                            currentScreen = ScreenState.LOGIN
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = user.name.split(" ").firstOrNull() ?: user.name,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        initialLoginAccountType = AccountType.MOTORIST_DRIVER
                                        currentScreen = ScreenState.LOGIN
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Sign In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Mode Switcher Toggle (Motorist vs. Operator Admin)
                            Row(
                                modifier = Modifier
                                    .background(Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                    .padding(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (!isAdminMode && (currentScreen != ScreenState.HOME_LANDING && currentScreen != ScreenState.LOGIN)) Color.White else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            isAdminMode = false
                                            if (currentScreen == ScreenState.HOME_LANDING || currentScreen == ScreenState.LOGIN || currentScreen == ScreenState.ADMIN_DASHBOARD || currentScreen == ScreenState.ADMIN_ADD_LOT || currentScreen == ScreenState.ADMIN_BAY_CONFIG) {
                                                currentScreen = ScreenState.DRIVER_FEED
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Driver",
                                        fontSize = 10.sp,
                                        fontWeight = if (!isAdminMode) FontWeight.Bold else FontWeight.Normal,
                                        color = if (!isAdminMode && (currentScreen != ScreenState.HOME_LANDING && currentScreen != ScreenState.LOGIN)) YallaTealDark else Color.White
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isAdminMode && (currentScreen != ScreenState.HOME_LANDING && currentScreen != ScreenState.LOGIN)) Color.White else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            isAdminMode = true
                                            currentScreen = ScreenState.ADMIN_DASHBOARD
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Provider",
                                        fontSize = 10.sp,
                                        fontWeight = if (isAdminMode) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isAdminMode && (currentScreen != ScreenState.HOME_LANDING && currentScreen != ScreenState.LOGIN)) YallaTealDark else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (!isAdminMode && (currentScreen == ScreenState.DRIVER_FEED)) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        DriverTab.values().forEach { tab ->
                            val isSelected = currentDriverTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentDriverTab = tab },
                                icon = {
                                    val tabIcon = when (tab) {
                                        DriverTab.MAP -> Icons.Default.LocationOn
                                        DriverTab.ACTIVE -> Icons.Default.Check
                                        DriverTab.AI -> Icons.Default.Search
                                        DriverTab.REWARDS -> Icons.Default.Star
                                    }
                                    Icon(
                                        imageVector = tabIcon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = YallaTealPrimary,
                                    selectedTextColor = YallaTealPrimary,
                                    indicatorColor = YallaTealPrimary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentScreen) {
                    ScreenState.HOME_LANDING -> {
                        LandingHomeScreen(
                            onLoginAsDriverClick = {
                                initialLoginAccountType = AccountType.MOTORIST_DRIVER
                                currentScreen = ScreenState.LOGIN
                            },
                            onLoginAsProviderClick = {
                                initialLoginAccountType = AccountType.PARKING_PROVIDER
                                currentScreen = ScreenState.LOGIN
                            },
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { isDarkMode = !isDarkMode }
                        )
                    }

                    ScreenState.LOGIN -> {
                        LoginScreen(
                            authViewModel = authViewModel,
                            initialAccountType = initialLoginAccountType,
                            onLoginSuccess = { session ->
                                if (session.accountType == AccountType.MOTORIST_DRIVER) {
                                    isAdminMode = false
                                    currentScreen = ScreenState.DRIVER_FEED
                                } else {
                                    isAdminMode = true
                                    currentScreen = ScreenState.ADMIN_DASHBOARD
                                }
                            },
                            onBackToHome = {
                                currentScreen = ScreenState.HOME_LANDING
                            }
                        )
                    }

                    ScreenState.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(
                            viewModel = adminViewModel,
                            onNavigateToAddLot = { currentScreen = ScreenState.ADMIN_ADD_LOT },
                            onNavigateToBayConfig = { lot ->
                                viewingLot = lot
                                currentScreen = ScreenState.ADMIN_BAY_CONFIG
                            }
                        )
                    }

                    ScreenState.ADMIN_ADD_LOT -> {
                        ManageLotsScreen(
                            viewModel = adminViewModel,
                            onBackClick = { currentScreen = ScreenState.ADMIN_DASHBOARD },
                            onLotAdded = { currentScreen = ScreenState.ADMIN_DASHBOARD }
                        )
                    }

                    ScreenState.ADMIN_BAY_CONFIG -> {
                        viewingLot?.let { lot ->
                            BayConfigScreen(
                                lot = lot,
                                viewModel = adminViewModel,
                                onBackClick = { currentScreen = ScreenState.ADMIN_DASHBOARD }
                            )
                        } ?: run {
                            currentScreen = ScreenState.ADMIN_DASHBOARD
                        }
                    }

                    ScreenState.DRIVER_FEED -> {
                        when (currentDriverTab) {
                            DriverTab.MAP -> {
                                MapExplorerScreen(
                                    viewModel = mapViewModel,
                                    onLotClick = { lot ->
                                        viewingLot = lot
                                        currentScreen = ScreenState.LOT_DETAIL
                                    }
                                )
                            }
                            DriverTab.ACTIVE -> {
                                ActiveSessionScreen(
                                    reservation = activeReservation,
                                    viewModel = bookingViewModel,
                                    onFindAnotherLot = { currentDriverTab = DriverTab.MAP }
                                )
                            }
                            DriverTab.AI -> {
                                YallaAiScreen(viewModel = aiViewModel)
                            }
                            DriverTab.REWARDS -> {
                                RewardsScreen()
                            }
                        }
                    }

                    ScreenState.LOT_DETAIL -> {
                        viewingLot?.let { lot ->
                            LotDetailScreen(
                                lot = lot,
                                mapViewModel = mapViewModel,
                                bookingViewModel = bookingViewModel,
                                onBackClick = { currentScreen = ScreenState.DRIVER_FEED },
                                onProceedToBook = { currentScreen = ScreenState.BOOKING_FLOW }
                            )
                        } ?: run {
                            currentScreen = ScreenState.DRIVER_FEED
                        }
                    }

                    ScreenState.BOOKING_FLOW -> {
                        viewingLot?.let { lot ->
                            BookingFlowScreen(
                                lot = lot,
                                viewModel = bookingViewModel,
                                onBackClick = { currentScreen = ScreenState.LOT_DETAIL },
                                onBookingSuccess = {
                                    currentScreen = ScreenState.DRIVER_FEED
                                    currentDriverTab = DriverTab.ACTIVE
                                }
                            )
                        } ?: run {
                            currentScreen = ScreenState.DRIVER_FEED
                        }
                    }
                }
            }
        }
    }
}
