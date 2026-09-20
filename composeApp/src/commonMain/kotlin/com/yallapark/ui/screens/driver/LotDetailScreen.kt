package com.yallapark.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.domain.model.BayStatus
import com.yallapark.domain.model.BayType
import com.yallapark.domain.model.ParkingBay
import com.yallapark.domain.model.ParkingLot
import com.yallapark.presentation.viewmodel.BookingViewModel
import com.yallapark.presentation.viewmodel.MapViewModel
import com.yallapark.ui.components.BayIndicatorCard
import com.yallapark.ui.components.OccupancyProgressBar
import com.yallapark.ui.components.PredictiveSlider
import com.yallapark.ui.theme.*

@Composable
fun LotDetailScreen(
    lot: ParkingLot,
    mapViewModel: MapViewModel,
    bookingViewModel: BookingViewModel,
    onBackClick: () -> Unit,
    onProceedToBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val etaMinutes by mapViewModel.targetEtaMinutes.collectAsState()
    val forecast by mapViewModel.predictiveForecast.collectAsState()
    val selectedBay by bookingViewModel.selectedBay.collectAsState()

    var activeBayTypeFilter by remember { mutableStateOf<BayType?>(null) }

    val displayedBays = remember(lot.bays, activeBayTypeFilter) {
        if (activeBayTypeFilter == null) lot.bays
        else lot.bays.filter { it.type == activeBayTypeFilter }
    }

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
                    Text("← Back", color = YallaTealPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = lot.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${lot.zone.displayName} • ${lot.facilityType.name.replace("_", " ")}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedBay != null) "Selected: Bay ${selectedBay?.bayNumber}" else "Select an open bay",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedBay != null) YallaTealPrimary else TextSecondary
                        )
                        Text(
                            text = "Rate: AED ${lot.hourlyRateAed.toInt()}/hr • 15m hold",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = onProceedToBook,
                        enabled = selectedBay != null,
                        colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pre-Book Bay", fontWeight = FontWeight.Bold)
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
            item {
                Spacer(modifier = Modifier.height(4.dp))
                OccupancyProgressBar(
                    totalCapacity = lot.totalCapacity,
                    availableBays = lot.availableBays
                )
            }

            // Predictive Availability Slider
            item {
                PredictiveSlider(
                    forecast = forecast,
                    etaMinutes = etaMinutes,
                    onEtaChanged = { mapViewModel.setTargetEtaMinutes(it) }
                )
            }

            // Facility Highlights
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Operator", fontSize = 12.sp, color = TextSecondary)
                            Text(lot.operatorName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Hours", fontSize = 12.sp, color = TextSecondary)
                            Text(lot.operatingHours, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Address", fontSize = 12.sp, color = TextSecondary)
                            Text(lot.address, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // Bay Type Filter Row
            item {
                Text(
                    text = "SELECT PARKING BAY (${displayedBays.count { it.status == BayStatus.AVAILABLE }} available)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = activeBayTypeFilter == null,
                            onClick = { activeBayTypeFilter = null },
                            label = { Text("All (${lot.bays.size})", fontSize = 11.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeBayTypeFilter == BayType.PEOPLE_OF_DETERMINATION,
                            onClick = { activeBayTypeFilter = BayType.PEOPLE_OF_DETERMINATION },
                            label = { Text("POD", fontSize = 11.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeBayTypeFilter == BayType.WOMEN_ONLY_PINK,
                            onClick = { activeBayTypeFilter = BayType.WOMEN_ONLY_PINK },
                            label = { Text("Pink", fontSize = 11.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeBayTypeFilter == BayType.DELIVERY_RIDER,
                            onClick = { activeBayTypeFilter = BayType.DELIVERY_RIDER },
                            label = { Text("Delivery", fontSize = 11.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeBayTypeFilter == BayType.EV_CHARGING,
                            onClick = { activeBayTypeFilter = BayType.EV_CHARGING },
                            label = { Text("EV", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Grid of Bays (grouped into rows of 4)
            val chunkedBays = displayedBays.chunked(4)
            items(chunkedBays) { rowBays ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (bay in rowBays) {
                        BayIndicatorCard(
                            bay = bay,
                            isSelected = selectedBay?.id == bay.id,
                            onClick = { bookingViewModel.selectBay(bay) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining empty spots in row if not multiple of 4
                    val emptySlots = 4 - rowBays.size
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
