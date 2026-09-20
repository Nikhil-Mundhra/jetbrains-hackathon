package com.yallapark.ui.screens.driver

import androidx.compose.foundation.background
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
import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.ParkingLot
import com.yallapark.presentation.viewmodel.BayFilterCategory
import com.yallapark.presentation.viewmodel.MapViewModel
import com.yallapark.ui.components.DubaiZoneMap
import com.yallapark.ui.components.OccupancyProgressBar
import com.yallapark.ui.theme.*

@Composable
fun MapExplorerScreen(
    viewModel: MapViewModel,
    onLotClick: (ParkingLot) -> Unit,
    modifier: Modifier = Modifier
) {
    val lots by viewModel.filteredLots.collectAsState()
    val selectedZone by viewModel.selectedZone.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedLot by viewModel.selectedLot.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search Bur Dubai, Karama, Deira, Downtown...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YallaTealPrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dubai Zone Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ZoneFilterChip(
                        label = "All Dubai Hubs",
                        isSelected = selectedZone == null,
                        onClick = { viewModel.selectZone(null) }
                    )
                }
                items(DubaiZone.values()) { zone ->
                    ZoneFilterChip(
                        label = zone.displayName,
                        isSelected = selectedZone == zone,
                        onClick = { viewModel.selectZone(zone) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Specialized Bay Categories Filter
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(BayFilterCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = YallaTealPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = YallaTealPrimary
                        )
                    )
                }
            }
        }

        // Scrollable Content: Vector Map + Lots List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                DubaiZoneMap(
                    lots = lots,
                    selectedLot = selectedLot,
                    selectedZone = selectedZone,
                    onLotClick = { lot ->
                        viewModel.selectLot(lot)
                        onLotClick(lot)
                    },
                    onZoneClick = { zone -> viewModel.selectZone(zone) }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PARKING FACILITIES (${lots.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    selectedZone?.let {
                        Text(
                            text = "Zone: ${it.displayName}",
                            fontSize = 11.sp,
                            color = YallaTealPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            items(lots) { lot ->
                LotSummaryCard(
                    lot = lot,
                    onClick = {
                        viewModel.selectLot(lot)
                        onLotClick(lot)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav
            }
        }
    }
}

@Composable
private fun ZoneFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) YallaTealPrimary else Color(0xFFF0F0F0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextPrimary
        )
    }
}

@Composable
private fun LotSummaryCard(
    lot: ParkingLot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                        text = "${lot.zone.displayName} • ${lot.address}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "AED ${lot.hourlyRateAed.toInt()}/hr",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = YallaTealPrimary
                    )
                    Text(
                        text = if (lot.isManagedByRta) "RTA Verified" else "Commercial",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OccupancyProgressBar(
                totalCapacity = lot.totalCapacity,
                availableBays = lot.availableBays
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Specialized Bay Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (lot.hasPodBays) {
                    SpecializedPill(label = "${lot.podAvailableCount} POD", color = BluePodBay, bg = BluePodBayBg)
                }
                if (lot.hasWomenPinkBays) {
                    SpecializedPill(label = "${lot.womenPinkAvailableCount} Pink", color = PinkWomenBay, bg = PinkWomenBayBg)
                }
                if (lot.hasDeliveryBays) {
                    SpecializedPill(label = "${lot.deliveryAvailableCount} Quick", color = AmberDeliveryBay, bg = AmberDeliveryBayBg)
                }
                if (lot.hasEvCharging) {
                    SpecializedPill(label = "EV Charging", color = GreenEvBay, bg = GreenEvBayBg)
                }
            }
        }
    }
}

@Composable
private fun SpecializedPill(label: String, color: Color, bg: Color) {
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = label, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
