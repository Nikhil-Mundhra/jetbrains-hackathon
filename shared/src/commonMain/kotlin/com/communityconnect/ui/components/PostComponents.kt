package com.communityconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.Colors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.communityconnect.domain.model.Category
import com.communityconnect.domain.model.Post
import com.communityconnect.domain.model.PostType
import com.communityconnect.domain.model.Urgency
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormatter

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onResponseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with type badge and urgency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostTypeBadge(type = post.type)
                UrgencyBadge(urgency = post.urgency)
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            
            // Title and category
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = post.category)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatRelativeTime(post.createdAt),
                    style = typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            Text(
                text = post.title,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
            
            // Description
            Text(
                text = post.description,
                style = typography.bodyMedium,
                color = colors.onSurfaceVariant,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            
            // Footer with author, location, and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        url = post.authorAvatarUrl,
                        contentDescription = post.authorName,
                        size = 36.dp
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Column(crossAxisAlignment = androidx.compose.ui.Alignment.Start) {
                        Text(
                            text = post.authorName,
                            style = typography.labelMedium,
                            color = colors.onSurface
                        )
                        if (post.distanceKm != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = colors.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f km away", post.distanceKm),
                                    style = typography.labelSmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            imageVector = if (post.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (post.isLikedByCurrentUser) "Unlike" else "Like",
                            tint = if (post.isLikedByCurrentUser) colors.error else colors.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.likesCount.toString(),
                        style = typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                    
                    IconButton(onClick = onResponseClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Responses",
                            tint = colors.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.responsesCount.toString(),
                        style = typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PostTypeBadge(type: PostType) {
    val colors = MaterialTheme.colorScheme
    val (backgroundColor, textColor, label) = when (type) {
        PostType.REQUEST -> colors.errorContainer to colors.onErrorContainer to "Request"
        PostType.OFFER -> colors.tertiaryContainer to colors.onTertiaryContainer to "Offer"
        PostType.EVENT -> colors.primaryContainer to colors.onPrimaryContainer to "Event"
        PostType.ANNOUNCEMENT -> colors.secondaryContainer to colors.onSecondaryContainer to "Notice"
    }
    
    Chip(
        modifier = Modifier.height(28.dp),
        onClick = {},
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun UrgencyBadge(urgency: Urgency) {
    val colors = MaterialTheme.colorScheme
    val (backgroundColor, textColor) = when (urgency) {
        Urgency.CRITICAL -> colors.errorContainer to colors.onErrorContainer
        Urgency.HIGH -> Color(0xFFFF9800) to Color.White
        Urgency.MEDIUM -> colors.tertiaryContainer to colors.onTertiaryContainer
        Urgency.LOW -> colors.surfaceContainerHigh to colors.onSurfaceVariant
    }
    
    Chip(
        modifier = Modifier.height(28.dp),
        onClick = {},
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = urgency.icon,
                style = MaterialTheme.typography.labelSmall
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = urgency.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun CategoryChip(category: Category) {
    val colors = MaterialTheme.colorScheme
    Chip(
        modifier = Modifier.height(28.dp),
        onClick = {},
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = colors.surfaceContainerHighest,
            contentColor = colors.onSurfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.labelSmall
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category.label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun AsyncImage(
    url: String?,
    contentDescription: String?,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 2))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        url?.let { imageUrl ->
            androidx.compose.foundation.image.Image(
                painter = coil.compose.rememberAsyncImagePainter(imageUrl),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size).clip(RoundedCornerShape(size / 2))
            )
        } ?: Icon(
            imageVector = Icons.Default.Person,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(size * 0.6).align(Alignment.Center)
        )
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurface,
            textAlign = TextAlign.Center
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        actionText?.let { text ->
            onAction?.let { action ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = action) {
                    Text(text = text)
                }
            }
        }
    }
}

@Composable
fun LoadingShimmer(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(colors.surfaceContainerHighest)
    ) {
        // Shimmer animation would go here
    }
}

fun formatRelativeTime(dateTime: LocalDateTime): String {
    val now = kotlinx.datetime.Clock.System.now().inTimeZone(kotlinx.datetime.TimeZone.currentSystemDefault()).toLocalDateTime()
    val diffMinutes = (now.toEpochSeconds(kotlinx.datetime.TimeZone.currentSystemDefault()) - dateTime.toEpochSeconds(kotlinx.datetime.TimeZone.currentSystemDefault())) / 60
    
    return when {
        diffMinutes < 1 -> "Just now"
        diffMinutes < 60 -> "${diffMinutes}m ago"
        diffMinutes < 1440 -> "${diffMinutes / 60}h ago"
        else -> "${diffMinutes / 1440}d ago"
    }
}