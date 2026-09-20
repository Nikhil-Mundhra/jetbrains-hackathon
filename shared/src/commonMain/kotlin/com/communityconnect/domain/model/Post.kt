package com.communityconnect.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Post(
    val id: Uuid = Uuid.randomUuid(),
    val authorId: Uuid,
    val authorName: String,
    val authorAvatarUrl: String?,
    val type: PostType,
    val title: String,
    val description: String,
    val category: Category,
    val location: Location,
    val urgency: Urgency,
    val status: PostStatus = PostStatus.ACTIVE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val responsesCount: Int = 0,
    val likesCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val distanceKm: Double? = null
)

@Serializable
enum class PostType {
    REQUEST,
    OFFER,
    EVENT,
    ANNOUNCEMENT
}

@Serializable
enum class Category {
    FOOD("FOOD", "Food & Meals"),
    TOOLS("TOOLS", "Tools & Equipment"),
    SKILLS("SKILLS", "Skills & Services"),
    TRANSPORT("TRANS", "Transportation"),
    CHILDCARE("CHILD", "Childcare"),
    ELDERCARE("ELDER", "Elder Care"),
    TECHNOLOGY("TECH", "Technology Help"),
    LANGUAGE("LANG", "Language & Translation"),
    EMERGENCY("EMERG", "Emergency"),
    COMMUNITY("COMM", "Community Events"),
    OTHER("OTHER", "Other")

    val icon: String
    val label: String
}

@Serializable
enum class Urgency {
    LOW("Low", "LOW"),
    MEDIUM("Medium", "MED"),
    HIGH("High", "HIGH"),
    CRITICAL("Critical", "CRIT")

    val label: String
    val icon: String
}

@Serializable
enum class PostStatus {
    ACTIVE,
    IN_PROGRESS,
    COMPLETED,
    EXPIRED,
    CANCELLED
}

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val neighborhood: String?,
    val city: String
)

@Serializable
data class User(
    val id: Uuid,
    val name: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
    val bio: String?,
    val neighborhood: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val skills: List<String> = emptyList(),
    val joinedAt: LocalDateTime = LocalDateTime.now(),
    val reputationScore: Int = 0,
    val completedRequests: Int = 0,
    val completedOffers: Int = 0,
    val isVerified: Boolean = false,
    val notificationSettings: NotificationSettings = NotificationSettings()
)

@Serializable
data class NotificationSettings(
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val newRequestsInArea: Boolean = true,
    val responsesToMyPosts: Boolean = true,
    val communityEvents: Boolean = true,
    val emergencyAlerts: Boolean = true
)

@Serializable
data class Response(
    val id: Uuid = Uuid.randomUuid(),
    val postId: Uuid,
    val responderId: Uuid,
    val responderName: String,
    val responderAvatarUrl: String?,
    val message: String,
    val type: ResponseType,
    val status: ResponseStatus = ResponseStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Serializable
enum class ResponseType {
    INTERESTED,
    CAN_HELP,
    NEED_MORE_INFO,
    OFFER_ALTERNATIVE
}

@Serializable
enum class ResponseStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    COMPLETED
}

@Serializable
data class Community(
    val id: Uuid,
    val name: String,
    val description: String,
    val neighborhood: String,
    val city: String,
    val memberCount: Int,
    val activePostsCount: Int,
    val coverImageUrl: String?,
    val createdAt: LocalDateTime,
    val isMember: Boolean = false
)

@Serializable
data class Neighborhood(
    val name: String,
    val city: String,
    val bounds: MapBounds,
    val postCount: Int,
    val memberCount: Int
)

@Serializable
data class MapBounds(
    val northEast: LatLng,
    val southWest: LatLng
)

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double
)