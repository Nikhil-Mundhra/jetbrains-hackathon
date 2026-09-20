package com.yallapark.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.domain.model.PredictiveForecast
import com.yallapark.ui.theme.*

@Composable
fun PredictiveSlider(
    forecast: PredictiveForecast?,
    etaMinutes: Int,
    onEtaChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PREDICTIVE AVAILABILITY AT ARRIVAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = YallaTealPrimary
                    )
                    Text(
                        text = "Estimated Time of Arrival (ETA): in $etaMinutes mins",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                forecast?.let {
                    val prob = it.openBayProbabilityPercentage
                    val badgeColor = when {
                        prob >= 70 -> StatusAvailableGreen
                        prob >= 40 -> StatusModerateAmber
                        else -> StatusCongestedRed
                    }

                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$prob% Open",
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Slider(
                value = etaMinutes.toFloat(),
                onValueChange = { onEtaChanged(it.toInt()) },
                valueRange = 5f..60f,
                steps = 10,
                colors = SliderDefaults.colors(
                    thumbColor = YallaTealPrimary,
                    activeTrackColor = YallaTealPrimary,
                    inactiveTrackColor = Color(0xFFCFD8DC)
                )
            )

            forecast?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(YallaGoldSecondary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("TIP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = YallaGoldDark)
                    }
                    Text(
                        text = it.recommendation,
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
