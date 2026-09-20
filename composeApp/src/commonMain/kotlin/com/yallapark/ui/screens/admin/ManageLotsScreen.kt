package com.yallapark.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.FacilityType
import com.yallapark.presentation.viewmodel.AdminViewModel
import com.yallapark.ui.theme.*

@Composable
fun ManageLotsScreen(
    viewModel: AdminViewModel,
    onBackClick: () -> Unit,
    onLotAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("Al Sabkha Smart Commercial Lot") }
    var selectedZone by remember { mutableStateOf(DubaiZone.DEIRA) }
    var selectedFacilityType by remember { mutableStateOf(FacilityType.SURFACE_OPEN_LOT) }
    var capacityStr by remember { mutableStateOf("65") }
    var hourlyRateStr by remember { mutableStateOf("4.0") }
    var podCountStr by remember { mutableStateOf("4") }
    var pinkCountStr by remember { mutableStateOf("6") }
    var deliveryCountStr by remember { mutableStateOf("8") }
    var address by remember { mutableStateOf("Al Sabkha Bus Station Rd, Deira") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Text("← Cancel", color = TextSecondary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Parking Facility (Way 2)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = {
                        val cap = capacityStr.toIntOrNull() ?: 50
                        val rate = hourlyRateStr.toDoubleOrNull() ?: 4.0
                        val pod = podCountStr.toIntOrNull() ?: 3
                        val pink = pinkCountStr.toIntOrNull() ?: 5
                        val del = deliveryCountStr.toIntOrNull() ?: 6

                        viewModel.addNewParkingLot(
                            name = name,
                            zone = selectedZone,
                            facilityType = selectedFacilityType,
                            totalCapacity = cap,
                            hourlyRateAed = rate,
                            podCount = pod,
                            pinkCount = pink,
                            deliveryCount = del,
                            address = address
                        )
                        onLotAdded()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Deploy Parking Facility to YallaPark", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Facility Name
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "FACILITY NAME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = YallaTealPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Dubai Zone Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "DUBAI MUNICIPAL ZONE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = YallaTealPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (zone in DubaiZone.values()) {
                                val isSel = selectedZone == zone
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = if (isSel) YallaTealPrimary else Color(0xFFF0F0F0),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedZone = zone }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = zone.displayName.split(" ").last(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Facility Type & Rates
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "SPECIFICATIONS & TARIFF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = YallaTealPrimary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = capacityStr,
                                onValueChange = { capacityStr = it },
                                label = { Text("Total Bays") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = hourlyRateStr,
                                onValueChange = { hourlyRateStr = it },
                                label = { Text("Hourly Rate (AED)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // Dedicated Bay Allocations
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "INCLUSIVITY & SPECIALIZED BAYS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = YallaTealPrimary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = podCountStr,
                                onValueChange = { podCountStr = it },
                                label = { Text("POD Bays") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = pinkCountStr,
                                onValueChange = { pinkCountStr = it },
                                label = { Text("Pink Bays") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = deliveryCountStr,
                                onValueChange = { deliveryCountStr = it },
                                label = { Text("Quick Bays") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // Address
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "LOCATION & ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = YallaTealPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
