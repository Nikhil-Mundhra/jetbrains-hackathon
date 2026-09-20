package com.yallapark.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.ui.theme.*

@Composable
fun OccupancyProgressBar(
    totalCapacity: Int,
    availableBays: Int,
    modifier: Modifier = Modifier
) {
    val occupied = (totalCapacity - availableBays).coerceAtLeast(0)
    val ratio = if (totalCapacity > 0) occupied.toFloat() / totalCapacity else 0f
    val percentage = (ratio * 100).toInt().coerceIn(0, 100)

    val (barColor, statusLabel) = when {
        percentage >= 85 -> Pair(StatusCongestedRed, "High Density")
        percentage >= 50 -> Pair(StatusModerateAmber, "Moderate")
        else -> Pair(StatusAvailableGreen, "Spacious")
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$availableBays bays free of $totalCapacity",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(barColor, RoundedCornerShape(8.dp))
                )
                Text(
                    text = "$statusLabel ($percentage%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = barColor
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(barColor, RoundedCornerShape(8.dp))
            )
        }
    }
}
