package com.yallapark.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.yallapark.domain.model.ParkingLot
import com.yallapark.domain.model.UserRole
import com.yallapark.presentation.viewmodel.AdminViewModel
import com.yallapark.ui.components.OccupancyProgressBar
import com.yallapark.ui.theme.*

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToAddLot: () -> Unit,
    onNavigateToBayConfig: (ParkingLot) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val lots by viewModel.liveLots.collectAsState()
    val adminMessage by viewModel.adminMessage.collectAsState()

    val totalCapacity = lots.sumOf { it.totalCapacity }
    val totalAvailable = lots.sumOf { it.availableBays }
    val totalOccupied = totalCapacity - totalAvailable
    val citywideOccupancyRate = if (totalCapacity > 0) (totalOccupied * 100) / totalCapacity else 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WAY 2 • OPERATOR CONSOLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Text(
                            text = currentRole.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = onNavigateToAddLot,
                        colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Add Space", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Multi-Tenant Role Switcher
                Text(
                    text = "SWITCH AUTHORITY / OPERATOR ROLE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(UserRole.values()) { role ->
                        RoleSelectorChip(
                            role = role,
                            isSelected = currentRole == role,
                            onClick = { viewModel.switchRole(role) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Notification Banner
            adminMessage?.let { msg ->
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE0F2F1), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "ℹ️ $msg", fontSize = 12.sp, color = YallaTealDark, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearMessage() }) {
                            Text("Dismiss", fontSize = 11.sp, color = YallaTealPrimary)
                        }
                    }
                }
            }

            // Citywide KPI Metrics Grid
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(title = "Managed Lots", value = "${lots.size}", subtitle = "Dubai Hubs", modifier = Modifier.weight(1f))
                    KpiCard(title = "Total Capacity", value = "$totalCapacity", subtitle = "Bays Registered", modifier = Modifier.weight(1f))
                    KpiCard(title = "Occupancy Rate", value = "$citywideOccupancyRate%", subtitle = "$totalOccupied In Use", modifier = Modifier.weight(1f))
                }
            }

            // Facility Registry Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTERED FACILITIES (${lots.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "Live Telemetry & Tele-control",
                        fontSize = 11.sp,
                        color = YallaTealPrimary
                    )
                }
            }

            items(lots) { lot ->
                AdminLotCard(
                    lot = lot,
                    onConfigureBays = {
                        viewModel.selectLotForAdmin(lot)
                        onNavigateToBayConfig(lot)
                    },
                    onDelete = { viewModel.deleteParkingLot(lot.id) },
                    onRateChange = { newRate -> viewModel.updateHourlyRate(lot.id, newRate) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun RoleSelectorChip(
    role: UserRole,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(role.badgeColor) else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = role.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextPrimary
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 10.sp, color = TextSecondary)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = subtitle, fontSize = 9.sp, color = YallaTealPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AdminLotCard(
    lot: ParkingLot,
    onConfigureBays: () -> Unit,
    onDelete: () -> Unit,
    onRateChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lot.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "ID: ${lot.id} • ${lot.zone.displayName} • ${lot.operatorName}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Lot",
                        tint = StatusCongestedRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OccupancyProgressBar(
                totalCapacity = lot.totalCapacity,
                availableBays = lot.availableBays
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onConfigureBays,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Manage Bays & Sensors", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val nextRate = if (lot.hourlyRateAed >= 10.0) 4.0 else lot.hourlyRateAed + 2.0
                        onRateChange(nextRate)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("AED ${lot.hourlyRateAed.toInt()}/h", fontSize = 11.sp, color = YallaTealDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
