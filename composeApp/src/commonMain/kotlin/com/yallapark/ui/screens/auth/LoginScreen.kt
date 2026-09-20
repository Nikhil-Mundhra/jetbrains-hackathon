package com.yallapark.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.domain.model.AccountType
import com.yallapark.domain.model.UserRole
import com.yallapark.domain.model.UserSession
import com.yallapark.presentation.viewmodel.AuthViewModel
import com.yallapark.ui.theme.*

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    initialAccountType: AccountType = AccountType.MOTORIST_DRIVER,
    onLoginSuccess: (UserSession) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialAccountType) }

    // Driver Form States
    var driverName by remember { mutableStateOf("Ahmed Al-Mansoor") }
    var driverPhone by remember { mutableStateOf("+971 50 123 4567") }
    var driverPlate by remember { mutableStateOf("DXB A 48291") }

    // Provider Form States
    var providerName by remember { mutableStateOf("Eng. Fatima Al-Hashimi") }
    var providerOrg by remember { mutableStateOf("Roads and Transport Authority (RTA)") }
    var providerGovId by remember { mutableStateOf("RTA-GOV-9824") }
    var providerRole by remember { mutableStateOf(UserRole.RTA_AUTHORITY) }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0F172A))
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Top Bar with Back Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBackToHome) {
                        Text("← Back to Home", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0x2200897B), RoundedCornerShape(8.dp))
                            .border(1.dp, YallaTealPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("YALLAPARK AUTH", color = YallaTealLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Title & Subtitle
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sign In to YallaPark",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Choose your role to access personalized parking services or facility management.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            // Two-Tab Segmented Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    // Option 1: Driver
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedTab == AccountType.MOTORIST_DRIVER) YallaTealPrimary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = AccountType.MOTORIST_DRIVER }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "End User (Driver)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == AccountType.MOTORIST_DRIVER) Color.White else Color(0xFF94A3B8)
                        )
                    }

                    // Option 2: Provider (Govt / Org)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedTab == AccountType.PARKING_PROVIDER) Color(0xFF1E3A5F) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = AccountType.PARKING_PROVIDER }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Provider (Govt / Org)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == AccountType.PARKING_PROVIDER) Color(0xFF93C5FD) else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Form Content Based on Selected Tab
            if (selectedTab == AccountType.MOTORIST_DRIVER) {
                // DRIVER LOGIN FORM
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Motorist & Car Driver Login", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Guaranteed hold, predictive ETA & NOL payment", fontSize = 11.sp, color = YallaTealLight)
                            }

                            Divider(color = Color(0xFF334155))

                            // Driver Full Name
                            OutlinedTextField(
                                value = driverName,
                                onValueChange = { driverName = it },
                                label = { Text("Full Name", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = YallaTealPrimary,
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Phone Number
                            OutlinedTextField(
                                value = driverPhone,
                                onValueChange = { driverPhone = it },
                                label = { Text("Mobile Number (UAE +971)", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = YallaTealPrimary,
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Plate Number
                            OutlinedTextField(
                                value = driverPlate,
                                onValueChange = { driverPlate = it.uppercase() },
                                label = { Text("Dubai Plate Number (e.g., DXB A 48291)", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = YallaTealPrimary,
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Submit Button
                            Button(
                                onClick = {
                                    authViewModel.setDriverPhone(driverPhone)
                                    authViewModel.setDriverPlate(driverPlate)
                                    authViewModel.loginAsDriver(driverName)
                                    authViewModel.currentUser.value?.let { onLoginSuccess(it) }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Sign In as Driver →", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            // Quick Demo Logins
                            Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("QUICK 1-CLICK DEMO ACCOUNTS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B))

                                QuickLoginButton(
                                    title = "Ahmed Al-Mansoor",
                                    subtitle = "Plate: DXB A 48291 • Downtown Regular",
                                    onClick = {
                                        driverName = "Ahmed Al-Mansoor"
                                        driverPhone = "+971 50 123 4567"
                                        driverPlate = "DXB A 48291"
                                        authViewModel.loginAsDriver("Ahmed Al-Mansoor")
                                        authViewModel.currentUser.value?.let { onLoginSuccess(it) }
                                    }
                                )

                                QuickLoginButton(
                                    title = "Sara Al-Qasimi",
                                    subtitle = "Plate: DXB K 19024 • Bur Dubai Resident",
                                    onClick = {
                                        driverName = "Sara Al-Qasimi"
                                        driverPhone = "+971 55 987 6543"
                                        driverPlate = "DXB K 19024"
                                        authViewModel.setDriverPlate("DXB K 19024")
                                        authViewModel.loginAsDriver("Sara Al-Qasimi")
                                        authViewModel.currentUser.value?.let { onLoginSuccess(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // PROVIDER (GOVT / ORG) LOGIN FORM
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Parking Provider Console (Way 2)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Government authority & facility management", fontSize = 11.sp, color = YallaGoldSecondary)
                            }

                            Divider(color = Color(0xFF334155))

                            // Provider Full Name
                            OutlinedTextField(
                                value = providerName,
                                onValueChange = { providerName = it },
                                label = { Text("Representative Name", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF60A5FA),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Organization Name
                            OutlinedTextField(
                                value = providerOrg,
                                onValueChange = { providerOrg = it },
                                label = { Text("Organization / Agency Name", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF60A5FA),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Government ID / License
                            OutlinedTextField(
                                value = providerGovId,
                                onValueChange = { providerGovId = it },
                                label = { Text("Gov Entity ID or Trade License", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF60A5FA),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Role Selection Pills
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Provider Role & Permissions", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    RolePill(
                                        title = "RTA Public Authority",
                                        isSelected = providerRole == UserRole.RTA_AUTHORITY,
                                        modifier = Modifier.weight(1f),
                                        onClick = { providerRole = UserRole.RTA_AUTHORITY }
                                    )
                                    RolePill(
                                        title = "Commercial Operator",
                                        isSelected = providerRole == UserRole.PRIVATE_OPERATOR,
                                        modifier = Modifier.weight(1f),
                                        onClick = { providerRole = UserRole.PRIVATE_OPERATOR }
                                    )
                                }
                            }

                            // Submit Button
                            Button(
                                onClick = {
                                    authViewModel.setProviderOrg(providerOrg)
                                    authViewModel.setProviderGovId(providerGovId)
                                    authViewModel.setProviderRole(providerRole)
                                    authViewModel.loginAsProvider(providerName, providerRole, providerOrg)
                                    authViewModel.currentUser.value?.let { onLoginSuccess(it) }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Enter Provider Console →", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF93C5FD))
                            }

                            // Quick Demo Logins
                            Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("QUICK 1-CLICK DEMO ACCOUNTS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B))

                                QuickLoginButton(
                                    title = "Eng. Fatima Al-Hashimi",
                                    subtitle = "RTA Dubai Public Authority • Full Regulatory Control",
                                    onClick = {
                                        providerName = "Eng. Fatima Al-Hashimi"
                                        providerOrg = "Roads and Transport Authority (RTA)"
                                        providerGovId = "RTA-GOV-9824"
                                        providerRole = UserRole.RTA_AUTHORITY
                                        authViewModel.loginAsProvider(providerName, providerRole, providerOrg)
                                        authViewModel.currentUser.value?.let { onLoginSuccess(it) }
                                    }
                                )

                                QuickLoginButton(
                                    title = "Majid Properties Operator",
                                    subtitle = "Commercial Garage Operator • Deira & Mall Facilities",
                                    onClick = {
                                        providerName = "Majid Al Futtaim Garages"
                                        providerOrg = "Majid Properties LLC"
                                        providerGovId = "DED-COM-5519"
                                        providerRole = UserRole.PRIVATE_OPERATOR
                                        authViewModel.loginAsProvider(providerName, providerRole, providerOrg)
                                        authViewModel.currentUser.value?.let { onLoginSuccess(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun RolePill(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color(0x333B82F6) else Color(0xFF0F172A),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF3B82F6) else Color(0xFF334155),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickLoginButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
            Text("→", fontSize = 14.sp, color = YallaTealLight, fontWeight = FontWeight.Bold)
        }
    }
}
