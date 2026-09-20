package com.yallapark.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.ParkingLot
import com.yallapark.ui.theme.*

@Composable
fun DubaiZoneMap(
    lots: List<ParkingLot>,
    selectedLot: ParkingLot?,
    selectedZone: DubaiZone?,
    onLotClick: (ParkingLot) -> Unit,
    onZoneClick: (DubaiZone) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B242C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(lots) {
                        detectTapGestures { offset ->
                            val width = size.width.toFloat()
                            val height = size.height.toFloat()

                            // Check if a lot pin was clicked
                            for (lot in lots) {
                                val (px, py) = projectDubaiCoords(lot.latitude, lot.longitude, width, height)
                                val distSq = (offset.x - px) * (offset.x - px) + (offset.y - py) * (offset.y - py)
                                if (distSq <= 24 * 24) {
                                    onLotClick(lot)
                                    return@detectTapGestures
                                }
                            }

                            // Check if a zone label was clicked
                            for (zone in DubaiZone.values()) {
                                val (zx, zy) = projectDubaiCoords(zone.lat, zone.lon, width, height)
                                val distSq = (offset.x - zx) * (offset.x - zx) + (offset.y - zy) * (offset.y - zy)
                                if (distSq <= 40 * 40) {
                                    onZoneClick(zone)
                                    return@detectTapGestures
                                }
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw Dubai Coastline & Dubai Creek (stylized vector)
                val creekPath = Path().apply {
                    moveTo(w * 0.95f, h * 0.15f)
                    cubicTo(w * 0.65f, h * 0.35f, w * 0.50f, h * 0.38f, w * 0.45f, h * 0.48f)
                    cubicTo(w * 0.40f, h * 0.55f, w * 0.30f, h * 0.70f, w * 0.15f, h * 0.95f)
                }
                drawPath(
                    path = creekPath,
                    color = Color(0xFF263238),
                    style = Stroke(width = 24f)
                )

                // Draw Waterway Core
                drawPath(
                    path = creekPath,
                    color = Color(0xFF193549),
                    style = Stroke(width = 14f)
                )

                // Draw Zone Boundaries / Labels
                for (zone in DubaiZone.values()) {
                    val (zx, zy) = projectDubaiCoords(zone.lat, zone.lon, w, h)
                    val isZoneActive = selectedZone == null || selectedZone == zone

                    drawCircle(
                        color = if (isZoneActive) Color(0x3300897B) else Color(0x11FFFFFF),
                        radius = if (isZoneActive) 38f else 24f,
                        center = Offset(zx, zy)
                    )
                }

                // Draw Parking Lots as Interactive Pins
                for (lot in lots) {
                    val (px, py) = projectDubaiCoords(lot.latitude, lot.longitude, w, h)
                    val isSelected = selectedLot?.id == lot.id

                    val pinColor = when {
                        lot.occupancyRate > 0.85 -> StatusCongestedRed
                        lot.occupancyRate > 0.50 -> StatusModerateAmber
                        else -> StatusAvailableGreen
                    }

                    // Outer pulse halo for selected lot
                    if (isSelected) {
                        drawCircle(
                            color = YallaGoldSecondary.copy(alpha = 0.5f),
                            radius = 18f,
                            center = Offset(px, py)
                        )
                    }

                    // Base Pin
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 10f else 8f,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = pinColor,
                        radius = if (isSelected) 8f else 6f,
                        center = Offset(px, py)
                    )
                }
            }

            // Map Overlays / Legend
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color(0xCC111820), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "DUBAI SMART MOBILITY MAP",
                    color = YallaTealLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bur Dubai • Karama • Deira • Downtown",
                    color = Color.LightGray,
                    fontSize = 10.sp
                )
            }

            // Heatmap Legend
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color(0xCC111820), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(color = StatusAvailableGreen, label = "Available")
                LegendDot(color = StatusModerateAmber, label = "Moderate")
                LegendDot(color = StatusCongestedRed, label = "Busy")
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, RoundedCornerShape(8.dp))
        )
        Text(text = label, color = Color.White, fontSize = 9.sp)
    }
}

private fun projectDubaiCoords(lat: Double, lon: Double, width: Float, height: Float): Pair<Float, Float> {
    // Bounding coordinates covering Downtown to Deira
    val minLat = 25.1850
    val maxLat = 25.2800
    val minLon = 25.2600
    val maxLon = 55.3350

    // Coordinates mapping
    val normX = ((lon - minLon) / (maxLon - minLon)).toFloat().coerceIn(0.1f, 0.9f)
    // Latitude inverted because higher lat is up (lower y)
    val normY = (1.0f - ((lat - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0.1f, 0.9f)

    return Pair(normX * width, normY * height)
}
