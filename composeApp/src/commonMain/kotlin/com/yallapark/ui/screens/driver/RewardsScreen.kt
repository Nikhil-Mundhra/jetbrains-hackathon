package com.yallapark.ui.screens.driver

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
import com.yallapark.domain.model.MerchantPerk
import com.yallapark.ui.theme.*

@Composable
fun RewardsScreen(
    modifier: Modifier = Modifier
) {
    val samplePerks = listOf(
        MerchantPerk("P1", "Arabica Specialty Coffee", "Al Fahidi, Bur Dubai", "25% Off Any Hand Drip", 120, "BUR-ECO25", "Dining"),
        MerchantPerk("P2", "Karama Spice House", "18B Street, Al Karama", "AED 15 Cashback on Grocery", 180, "KRM-SPICE15", "Retail"),
        MerchantPerk("P3", "Deira Waterfront Souq Café", "Baniyas Road, Deira", "Buy 1 Get 1 Free Karak Tea", 90, "DEI-TEA2", "Beverage"),
        MerchantPerk("P4", "Downtown Cinema Lounge", "The Dubai Mall, Downtown", "Free Valet Upgrade Voucher", 250, "DT-VALETVIP", "Entertainment")
    )

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
                    text = "ECO-MOBILITY REWARDS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = YallaTealPrimary
                )
                Text(
                    text = "Gamified Green Parking",
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
            // Eco Impact Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = YallaTealDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "GREEN COMMUTER LEVEL 2", color = YallaGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "340 Points", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CO₂ Emissions Prevented", fontSize = 11.sp, color = Color.LightGray)
                                Text("4.8 kg CO₂", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Search Time Saved", fontSize = 11.sp, color = Color.LightGray)
                                Text("38 Mins", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Earn 50 pts each time you pre-book off-peak or park at designated transit hubs!",
                            fontSize = 11.sp,
                            color = Color(0xFFB2DFDB)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "LOCAL DUBAI MERCHANT PERKS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            items(samplePerks) { perk ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = perk.perkTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${perk.merchantName} • ${perk.location}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Code: ${perk.promoCode}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = YallaTealPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .background(YallaGoldLight.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${perk.requiredEcoPoints} Pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {},
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = YallaTealPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Redeem", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
