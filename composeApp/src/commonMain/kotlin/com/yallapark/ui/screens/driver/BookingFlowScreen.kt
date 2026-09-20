package com.yallapark.ui.screens.driver

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
import com.yallapark.domain.model.ParkingLot
import com.yallapark.domain.model.PaymentMethod
import com.yallapark.presentation.viewmodel.BookingViewModel
import com.yallapark.ui.theme.*

@Composable
fun BookingFlowScreen(
    lot: ParkingLot,
    viewModel: BookingViewModel,
    onBackClick: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedBay by viewModel.selectedBay.collectAsState()
    val durationMinutes by viewModel.durationMinutes.collectAsState()
    val vehiclePlate by viewModel.vehiclePlate.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val bookingError by viewModel.bookingError.collectAsState()

    val totalCost = (durationMinutes / 60.0) * lot.hourlyRateAed
    val formattedCost = (totalCost * 100).toInt() / 100.0

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
                    text = "Confirm Reservation",
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
                Column(modifier = Modifier.padding(16.dp)) {
                    bookingError?.let {
                        Text(
                            text = it,
                            color = StatusCongestedRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.createReservation(lot) {
                                onBookingSuccess()
                            }
                        },
                        enabled = !isProcessing && selectedBay != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Pay AED $formattedCost & Lock Bay",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selected Bay Summary Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TARGET PARKING BAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bay ${selectedBay?.bayNumber ?: "None"} (${selectedBay?.type?.name?.replace("_", " ")})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${lot.name} • ${lot.zone.displayName}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "15-Minute Guaranteed Slot Hold", fontSize = 12.sp, color = StatusAvailableGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Duration Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "RESERVATION DURATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DurationPill(label = "30 min", isSelected = durationMinutes == 30, onClick = { viewModel.setDurationMinutes(30) }, modifier = Modifier.weight(1f))
                            DurationPill(label = "1 hr", isSelected = durationMinutes == 60, onClick = { viewModel.setDurationMinutes(60) }, modifier = Modifier.weight(1f))
                            DurationPill(label = "2 hrs", isSelected = durationMinutes == 120, onClick = { viewModel.setDurationMinutes(120) }, modifier = Modifier.weight(1f))
                            DurationPill(label = "3 hrs", isSelected = durationMinutes == 180, onClick = { viewModel.setDurationMinutes(180) }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Vehicle Plate Number
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "VEHICLE LICENSE PLATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = vehiclePlate,
                            onValueChange = { viewModel.setVehiclePlate(it.uppercase()) },
                            placeholder = { Text("e.g. DXB A 48291") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = YallaTealPrimary
                            )
                        )
                        Text(
                            text = "Used by ANPR / smart gate cameras to auto-lift barriers upon arrival.",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Digital Payment Method
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DIGITAL PAYMENT METHOD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PaymentOptionRow(
                                title = PaymentMethod.APPLE_PAY.label,
                                badge = "APPLE",
                                isSelected = paymentMethod == PaymentMethod.APPLE_PAY,
                                onClick = { viewModel.setPaymentMethod(PaymentMethod.APPLE_PAY) }
                            )
                            PaymentOptionRow(
                                title = PaymentMethod.GOOGLE_PAY.label,
                                badge = "GPAY",
                                isSelected = paymentMethod == PaymentMethod.GOOGLE_PAY,
                                onClick = { viewModel.setPaymentMethod(PaymentMethod.GOOGLE_PAY) }
                            )
                            PaymentOptionRow(
                                title = PaymentMethod.DUBAI_NOL_CARD.label,
                                badge = "NOL",
                                subtitle = "RTA Unified Transit Balance",
                                isSelected = paymentMethod == PaymentMethod.DUBAI_NOL_CARD,
                                onClick = { viewModel.setPaymentMethod(PaymentMethod.DUBAI_NOL_CARD) }
                            )
                            PaymentOptionRow(
                                title = PaymentMethod.CREDIT_DEBIT_CARD.label,
                                badge = "CARD",
                                isSelected = paymentMethod == PaymentMethod.CREDIT_DEBIT_CARD,
                                onClick = { viewModel.setPaymentMethod(PaymentMethod.CREDIT_DEBIT_CARD) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DurationChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) YallaTealPrimary else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextPrimary
        )
    }
}

@Composable
private fun PaymentOptionRow(
    title: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) YallaTealPrimary.copy(alpha = 0.08f) else Color(0xFFFAFAFA),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(if (isSelected) YallaTealPrimary else Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.White else Color(0xFF475569)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = TextPrimary
                )
                subtitle?.let {
                    Text(text = it, fontSize = 10.sp, color = YallaTealDark)
                }
            }
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = YallaTealPrimary)
        )
    }
}
