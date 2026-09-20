package com.yallapark.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.domain.model.BayStatus
import com.yallapark.domain.model.ParkingLot
import com.yallapark.presentation.viewmodel.AdminViewModel
import com.yallapark.ui.components.BayIndicatorCard
import com.yallapark.ui.components.OccupancyProgressBar
import com.yallapark.ui.theme.*

@Composable
fun BayConfigScreen(
    lot: ParkingLot,
    viewModel: AdminViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    Text("← Dashboard", color = YallaTealPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Bay & Sensor Matrix",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${lot.name} • ${lot.totalCapacity} Total Bays",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
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
            // Live Status Banner
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "TELEMETRY OVERVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OccupancyProgressBar(
                            totalCapacity = lot.totalCapacity,
                            availableBays = lot.availableBays
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tip: Tap any bay below to toggle sensor status (Available ⇄ Occupied) or simulate IoT gate camera detection.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Bay Grid
            val chunkedBays = lot.bays.chunked(4)
            items(chunkedBays) { rowBays ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (bay in rowBays) {
                        BayIndicatorCard(
                            bay = bay,
                            onClick = {
                                viewModel.simulateSensorTrigger(lot.id, bay.id)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    val emptyCount = 4 - rowBays.size
                    repeat(emptyCount) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
