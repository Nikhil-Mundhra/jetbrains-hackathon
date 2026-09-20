package com.yallapark.ui.screens.driver

import androidx.compose.foundation.background
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
import com.yallapark.domain.model.Reservation
import com.yallapark.presentation.viewmodel.BookingViewModel
import com.yallapark.ui.theme.*

@Composable
fun ActiveSessionScreen(
    reservation: Reservation?,
    viewModel: BookingViewModel,
    onFindAnotherLot: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (reservation == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(YallaTealPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = YallaTealPrimary
                    )
                }
                Text(
                    text = "No Active Parking Session",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Browse parking lots across Bur Dubai, Karama, Deira, or Downtown and pre-book your space.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = onFindAnotherLot,
                    colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Explore Parking Lots")
                }
            }
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "ACTIVE PARKING PASS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = YallaTealPrimary
                )
                Text(
                    text = reservation.lotName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Digital Parking Pass Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = YallaTealDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "YALLAPARK DIGITAL PASS",
                                color = YallaGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .background(StatusAvailableGreen, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = reservation.status.name,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Bay Number Callout
                        Text(
                            text = "BAY ${reservation.bayNumber}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${reservation.bayType.name.replace("_", " ")} BAY",
                            fontSize = 13.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Pass Code Box
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "[ SMART GATE QR PASS ]", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text(text = reservation.qrPassCode, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = YallaTealDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Meta row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Vehicle Plate", fontSize = 10.sp, color = Color.LightGray)
                                Text(reservation.vehiclePlateNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Paid via", fontSize = 10.sp, color = Color.LightGray)
                                Text(reservation.paymentMethod.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Live Countdown Timer & Navigation
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PARKING SESSION TIME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${reservation.durationMinutes} Minutes Reserved",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "AED ${reservation.totalCostAed}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = YallaTealPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Turn-by-turn bay navigation active. Camera detection will automatically verify your arrival.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // 1-Click Remote Extension
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1-CLICK REMOTE EXTENSION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YallaTealPrimary
                        )
                        Text(
                            text = "Extend your session remotely without returning to the vehicle meter.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.extendActiveSession(15) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+15m", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.extendActiveSession(30) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+30m", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.extendActiveSession(60) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+1 hr", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Release / Complete Session Button
            item {
                Button(
                    onClick = { viewModel.cancelActiveSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Vacate Bay & End Session", fontWeight = FontWeight.Bold)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
