package com.yallapark.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.yallapark.domain.model.BayStatus
import com.yallapark.domain.model.BayType
import com.yallapark.domain.model.ParkingBay
import com.yallapark.ui.theme.*

@Composable
fun BayIndicatorCard(
    bay: ParkingBay,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isAvailable = bay.status == BayStatus.AVAILABLE

    val (badgeBg, badgeBorder, badgeText) = when (bay.type) {
        BayType.PEOPLE_OF_DETERMINATION -> Triple(BluePodBayBg, BluePodBay, "POD")
        BayType.WOMEN_ONLY_PINK -> Triple(PinkWomenBayBg, PinkWomenBay, "PINK")
        BayType.DELIVERY_RIDER -> Triple(AmberDeliveryBayBg, AmberDeliveryBay, "QUICK")
        BayType.EV_CHARGING -> Triple(GreenEvBayBg, GreenEvBay, "EV")
        BayType.STANDARD -> Triple(Color(0xFFF5F5F5), Color(0xFFBDBDBD), "STD")
    }

    val statusColor = when (bay.status) {
        BayStatus.AVAILABLE -> StatusAvailableGreen
        BayStatus.OCCUPIED -> StatusCongestedRed
        BayStatus.RESERVED -> StatusReservedBlue
        BayStatus.MAINTENANCE -> Color.Gray
    }

    Box(
        modifier = modifier
            .width(82.dp)
            .height(72.dp)
            .background(
                color = if (isSelected) YallaTealPrimary.copy(alpha = 0.15f) else badgeBg,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) YallaTealPrimary else badgeBorder.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            )
            .then(if (onClick != null && isAvailable) Modifier.clickable { onClick() } else Modifier)
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeBorder.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeBorder
                    )
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor, RoundedCornerShape(8.dp))
                )
            }

            Text(
                text = bay.bayNumber,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAvailable) TextPrimary else TextSecondary
            )

            Text(
                text = if (isAvailable) "OPEN" else bay.status.name.take(4),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isAvailable) badgeBorder else statusColor
            )
        }
    }
}
