package com.communityconnect.data.repository

import com.communityconnect.domain.model.Category
import com.communityconnect.domain.model.Community
import com.communityconnect.domain.model.LatLng
import com.communityconnect.domain.model.MapBounds
import com.communityconnect.domain.model.Neighborhood
import com.communityconnect.domain.model.Post
import com.communityconnect.domain.model.PostStatus
import com.communityconnect.domain.model.PostType
import com.communityconnect.domain.model.Response
import com.communityconnect.domain.model.ResponseStatus
import com.communityconnect.domain.model.ResponseType
import com.communityconnect.domain.model.Urgency
import com.communityconnect.domain.model.User
import com.communityconnect.domain.repository.CommunityRepository
import com.communityconnect.domain.repository.PostRepository
import com.communityconnect.domain.repository.ResponseRepository
import com.communityconnect.domain.repository.Result
import com.communityconnect.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.utc
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.uuid.Uuid

private const val EARTH_RADIUS_KM = 6371.0

class MockPostRepository : PostRepository {
    private val posts = MutableStateFlow<List<Post>>(generateMockPosts())
    private val userPosts = mutableMapOf<Uuid, MutableList<Post>>()

    override suspend fun getPosts(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        category: Category?,
        type: PostType?,
        urgency: Urgency?,
        limit: Int,
        offset: Int
    ): Result<List<Post>> {
        delay(300)
        val filtered = posts.value.filter { post ->
            val distance = calculateDistance(latitude, longitude, post.location.latitude, post.location.longitude)
            val inRadius = distance <= radiusKm
            val matchesCategory = category == null || post.category == category
            val matchesType = type == null || post.type == type
            val matchesUrgency = urgency == null || post.urgency == urgency
            val isActive = post.status == PostStatus.ACTIVE
            inRadius && matchesCategory && matchesType && matchesUrgency && isActive
        }.sortedByDescending { it.createdAt }
            .drop(offset)
            .take(limit)
            .map { it.copy(distanceKm = calculateDistance(latitude, longitude, it.location.latitude, it.location.longitude)) }
        
        return Result.Success(filtered)
    }

    override suspend fun getPostById(postId: Uuid): Result<Post> {
        delay(100)
        val post = posts.value.find { it.id == postId }
        return if (post != null) Result.Success(post) else Result.Error(IllegalArgumentException("Post not found"))
    }

    override suspend fun createPost(post: Post): Result<Post> {
        delay(500)
        val newPosts = posts.value + post
        posts.value = newPosts
        userPosts.getOrPut(post.authorId) { mutableListOf() }.add(post)
        return Result.Success(post)
    }

    override suspend fun updatePost(post: Post): Result<Post> {
        delay(300)
        val updated = posts.value.map { if (it.id == post.id) post else it }
        posts.value = updated
        return Result.Success(post)
    }

    override suspend fun deletePost(postId: Uuid): Result<Unit> {
        delay(200)
        posts.value = posts.value.filter { it.id != postId }
        return Result.Success(Unit)
    }

    override suspend fun likePost(postId: Uuid, userId: Uuid): Result<Post> {
        delay(200)
        val post = posts.value.find { it.id == postId }
        if (post != null) {
            val liked = !post.isLikedByCurrentUser
            val updated = post.copy(
                likesCount = if (liked) post.likesCount + 1 else post.likesCount - 1,
                isLikedByCurrentUser = liked
            )
            posts.value = posts.value.map { if (it.id == postId) updated else it }
            return Result.Success(updated)
        }
        return Result.Error(IllegalArgumentException("Post not found"))
    }

    override suspend fun getUserPosts(userId: Uuid, status: PostStatus?): Result<List<Post>> {
        delay(200)
        val userPostList = userPosts[userId] ?: mutableListOf()
        val filtered = if (status != null) userPostList.filter { it.status == status } else userPostList
        return Result.Success(filtered.sortedByDescending { it.createdAt })
    }

    override fun observePosts(latitude: Double, longitude: Double, radiusKm: Double) = posts.asStateFlow()

    override suspend fun searchPosts(query: String, latitude: Double, longitude: Double, radiusKm: Double): Result<List<Post>> {
        delay(300)
        val lowerQuery = query.lowercase()
        val filtered = posts.value.filter { post ->
            val distance = calculateDistance(latitude, longitude, post.location.latitude, post.location.longitude)
            val inRadius = distance <= radiusKm
            val matchesQuery = post.title.lowercase().contains(lowerQuery) ||
                               post.description.lowercase().contains(lowerQuery) ||
                               post.tags.any { it.lowercase().contains(lowerQuery) }
            inRadius && matchesQuery && post.status == PostStatus.ACTIVE
        }.map { it.copy(distanceKm = calculateDistance(latitude, longitude, it.location.latitude, it.location.longitude)) }
        return Result.Success(filtered)
    }

    private fun generateMockPosts(): List<Post> {
        val now = Clock.System.now().inTimeZone(TimeZone.currentSystemDefault()).toLocalDateTime()
        val centerLat = 37.7749
        val centerLng = -122.4194
        
        return listOf(
            Post(
                authorId = Uuid.fromString("11111111-1111-1111-1111-111111111111"),
                authorName = "Maria Santos",
                authorAvatarUrl = null,
                type = PostType.REQUEST,
                title = "Need help with grocery shopping",
                description = "I'm recovering from surgery and can't carry heavy bags. Would appreciate someone picking up groceries for me this week.",
                category = Category.FOOD,
                location = Location(centerLat + 0.002, centerLng - 0.001, "123 Mission St", "Mission District", "San Francisco"),
                urgency = Urgency.HIGH,
                createdAt = now.minusHours(2),
                tags = listOf("groceries", "recovery", "elderly"),
                responsesCount = 3,
                likesCount = 5
            ),
            Post(
                authorId = Uuid.fromString("22222222-2222-2222-2222-222222222222"),
                authorName = "James Chen",
                authorAvatarUrl = null,
                type = PostType.OFFER,
                title = "Free power tools - drill, saw, sander",
                description = "Moving out and have quality power tools to give away. Dewalt drill, circular saw, orbital sander. All in great condition.",
                category = Category.TOOLS,
                location = Location(centerLat - 0.003, centerLng + 0.002, "456 Valencia St", "Mission District", "San Francisco"),
                urgency = Urgency.LOW,
                createdAt = now.minusHours(5),
                tags = listOf("tools", "moving", "free"),
                responsesCount = 8,
                likesCount = 12
            ),
            Post(
                authorId = Uuid.fromString("33333333-3333-3333-3333-333333333333"),
                authorName = "Aisha Patel",
                authorAvatarUrl = null,
                type = PostType.REQUEST,
                title = "Urgent: Need ride to hospital for dialysis",
                description = "My regular ride fell through. Need transportation to UCSF Medical Center by 2 PM today for dialysis appointment. Will pay for gas.",
                category = Category.TRANSPORT,
                location = Location(centerLat + 0.01, centerLng - 0.005, "789 Castro St", "Castro", "San Francisco"),
                urgency = Urgency.CRITICAL,
                createdAt = now.minusMinutes(30),
                tags = listOf("medical", "urgent", "dialysis"),
                responsesCount = 1,
                likesCount = 2
            ),
            Post(
                authorId = Uuid.fromString("44444444-4444-4444-4444-444444444444"),
                authorName = "Robert Kim",
                authorAvatarUrl = null,
                type = PostType.OFFER,
                title = "Free Spanish tutoring - 2 hours/week",
                description = "Native Spanish speaker offering conversational practice sessions. Great for beginners or intermediate learners. Flexible schedule.",
                category = Category.LANGUAGE,
                location = Location(centerLat - 0.008, centerLng + 0.01, "321 Haight St", "Haight-Ashbury", "San Francisco"),
                urgency = Urgency.LOW,
                createdAt = now.minusDays(1),
                tags = listOf("spanish", "tutoring", "language"),
                responsesCount = 4,
                likesCount = 7
            ),
            Post(
                authorId = Uuid.fromString("55555555-5555-5555-5555-555555555555"),
                authorName = "Sarah Johnson",
                authorAvatarUrl = null,
                type = PostType.EVENT,
                title = "Community Garden Cleanup - Saturday 10 AM",
                description = "Join us for the monthly community garden cleanup! We'll be weeding, planting spring vegetables, and composting. Tools and gloves provided. All ages welcome!",
                category = Category.COMMUNITY,
                location = Location(centerLat + 0.005, centerLng + 0.008, "Golden Gate Park Community Garden", "Richmond District", "San Francisco"),
                urgency = Urgency.MEDIUM,
                createdAt = now.minusDays(2),
                expiresAt = now.plusDays(3),
                tags = listOf("gardening", "volunteer", "outdoor", "family-friendly"),
                responsesCount = 15,
                likesCount = 23
            ),
            Post(
                authorId = Uuid.fromString("66666666-6666-6666-6666-666666666666"),
                authorName = "David Park",
                authorAvatarUrl = null,
                type = PostType.REQUEST,
                title = "Tech help for elderly neighbor - smartphone setup",
                description = "My 82-year-old neighbor needs help setting up her new iPhone - video calls with grandkids, medication reminders, emergency contacts. 1-2 hours needed.",
                category = Category.TECHNOLOGY,
                location = Location(centerLat - 0.001, centerLng - 0.008, "555 Fillmore St", "Pacific Heights", "San Francisco"),
                urgency = Urgency.MEDIUM,
                createdAt = now.minusHours(8),
                tags = listOf("tech-help", "elderly", "smartphone"),
                responsesCount = 2,
                likesCount = 6
            ),
            Post(
                authorId = Uuid.fromString("77777777-7777-7777-7777-777777777777"),
                authorName = "Lisa Thompson",
                authorAvatarUrl = null,
                type = PostType.OFFER,
                title = "Free baby clothes (0-12 months) and stroller",
                description = "My little one outgrew these! Gently used organic cotton onesies, sleepers, and a BOB jogging stroller. Porch pickup in Noe Valley.",
                category = Category.CHILDCARE,
                location = Location(centerLat + 0.007, centerLng - 0.01, "888 Sanchez St", "Noe Valley", "San Francisco"),
                urgency = Urgency.LOW,
                createdAt = now.minusHours(12),
                tags = listOf("baby", "clothes", "stroller", "free"),
                responsesCount = 6,
                likesCount = 9
            ),
            Post(
                authorId = Uuid.fromString("88888888-8888-8888-8888-888888888888"),
                authorName = "Marcus Williams",
                authorAvatarUrl = null,
                type = PostType.ANNOUNCEMENT,
                title = "Neighborhood Watch Meeting - Tonight 7 PM",
                description = "Monthly safety meeting at the community center. Officer Ramirez from SFPD will discuss recent incidents and prevention tips. Light refreshments served.",
                category = Category.COMMUNITY,
                location = Location(centerLat + 0.003, centerLng + 0.003, "Mission Community Center", "Mission District", "San Francisco"),
                urgency = Urgency.MEDIUM,
                createdAt = now.minusHours(4),
                expiresAt = now.plusHours(6),
                tags = listOf("safety", "meeting", "neighborhood-watch"),
                responsesCount = 11,
                likesCount = 14
            )
        )
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }
}

class MockUserRepository : UserRepository {
    private val currentUser = MutableStateFlow<User?>(generateMockUser())
    
    override suspend fun getCurrentUser(): Result<User> {
        delay(100)
        return currentUser.value?.let { Result.Success(it) } ?: Result.Error(IllegalStateException("Not logged in"))
    }

    override suspend fun getUserById(userId: Uuid): Result<User> {
        delay(100)
        if (currentUser.value?.id == userId) return Result.Success(currentUser.value!!)
        return Result.Error(IllegalArgumentException("User not found"))
    }

    override suspend fun updateUser(user: User): Result<User> {
        delay(300)
        currentUser.value = user
        return Result.Success(user)
    }

    override suspend fun updateLocation(userId: Uuid, latitude: Double, longitude: Double, neighborhood: String, city: String): Result<User> {
        delay(200)
        val user = currentUser.value?.copy(latitude = latitude, longitude = longitude, neighborhood = neighborhood, city = city)
        user?.let { currentUser.value = it }
        return user?.let { Result.Success(it) } ?: Result.Error(IllegalStateException("Not logged in"))
    }

    override suspend fun updateNotificationSettings(userId: Uuid, settings: com.communityconnect.domain.model.NotificationSettings): Result<User> {
        delay(200)
        val user = currentUser.value?.copy(notificationSettings = settings)
        user?.let { currentUser.value = it }
        return user?.let { Result.Success(it) } ?: Result.Error(IllegalStateException("Not logged in"))
    }

    override suspend fun signup(user: User): Result<User> {
        delay(500)
        currentUser.value = user
        return Result.Success(user)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        delay(500)
        val user = generateMockUser().copy(email = email)
        currentUser.value = user
        return Result.Success(user)
    }

    override suspend fun logout(): Result<Unit> {
        delay(200)
        currentUser.value = null
        return Result.Success(Unit)
    }

    override fun observeCurrentUser() = currentUser.asStateFlow()

    private fun generateMockUser(): User {
        return User(
            id = Uuid.fromString("11111111-1111-1111-1111-111111111111"),
            name = "Maria Santos",
            email = "maria.santos@email.com",
            phone = "+1-555-0123",
            avatarUrl = null,
            bio = "Retired teacher, love gardening and community building. Happy to help neighbors with tutoring, cooking, or just a friendly chat!",
            neighborhood = "Mission District",
            city = "San Francisco",
            latitude = 37.7749,
            longitude = -122.4194,
            skills = listOf("Cooking", "Spanish Tutoring", "Gardening", "Childcare"),
            reputationScore = 127,
            completedRequests = 12,
            completedOffers = 28,
            isVerified = true
        )
    }
}

class MockResponseRepository : ResponseRepository {
    private val responses = mutableMapOf<Uuid, MutableList<Response>>()
    private val responseFlows = mutableMapOf<Uuid, MutableStateFlow<List<Response>>>()

    override suspend fun getResponses(postId: Uuid): Result<List<Response>> {
        delay(150)
        return Result.Success(responses[postId] ?: emptyList())
    }

    override suspend fun createResponse(response: Response): Result<Response> {
        delay(300)
        val list = responses.getOrPut(response.postId) { mutableListOf() }
        list.add(response)
        responseFlows[response.postId]?.value = list.toList()
        return Result.Success(response)
    }

    override suspend fun updateResponse(response: Response): Result<Response> {
        delay(200)
        val list = responses[response.postId] ?: mutableListOf()
        val index = list.indexOfFirst { it.id == response.id }
        if (index >= 0) {
            list[index] = response
            responseFlows[response.postId]?.value = list.toList()
            return Result.Success(response)
        }
        return Result.Error(IllegalArgumentException("Response not found"))
    }

    override suspend fun acceptResponse(responseId: Uuid): Result<Response> {
        delay(200)
        for ((postId, list) in responses) {
            val index = list.indexOfFirst { it.id == responseId }
            if (index >= 0) {
                val updated = list[index].copy(status = ResponseStatus.ACCEPTED)
                list[index] = updated
                responseFlows[postId]?.value = list.toList()
                return Result.Success(updated)
            }
        }
        return Result.Error(IllegalArgumentException("Response not found"))
    }

    override suspend fun declineResponse(responseId: Uuid): Result<Response> {
        delay(200)
        for ((postId, list) in responses) {
            val index = list.indexOfFirst { it.id == responseId }
            if (index >= 0) {
                val updated = list[index].copy(status = ResponseStatus.DECLINED)
                list[index] = updated
                responseFlows[postId]?.value = list.toList()
                return Result.Success(updated)
            }
        }
        return Result.Error(IllegalArgumentException("Response not found"))
    }

    override fun observeResponses(postId: Uuid) = 
        responseFlows.getOrPut(postId) { MutableStateFlow(responses[postId] ?: emptyList()) }.asStateFlow()
}

class MockCommunityRepository : CommunityRepository {
    private val communities = generateMockCommunities()

    override suspend fun getCommunities(latitude: Double, longitude: Double, radiusKm: Double): Result<List<Community>> {
        delay(300)
        return Result.Success(communities)
    }

    override suspend fun getCommunityById(communityId: Uuid): Result<Community> {
        delay(100)
        val community = communities.find { it.id == communityId }
        return community?.let { Result.Success(it) } ?: Result.Error(IllegalArgumentException("Community not found"))
    }

    override suspend fun joinCommunity(communityId: Uuid, userId: Uuid): Result<Community> {
        delay(300)
        val community = communities.find { it.id == communityId }?.copy(isMember = true, memberCount = it.memberCount + 1)
        return community?.let { Result.Success(it) } ?: Result.Error(IllegalArgumentException("Community not found"))
    }

    override suspend fun leaveCommunity(communityId: Uuid, userId: Uuid): Result<Unit> {
        delay(200)
        return Result.Success(Unit)
    }

    override suspend fun createCommunity(community: Community): Result<Community> {
        delay(500)
        return Result.Success(community)
    }

    override fun observeCommunities(latitude: Double, longitude: Double, radiusKm: Double) = kotlinx.coroutines.flow.flowOf(communities)

    override suspend fun getNeighborhoods(city: String): Result<List<Neighborhood>> {
        delay(200)
        val neighborhoods = listOf(
            Neighborhood("Mission District", "San Francisco", MapBounds(LatLng(37.78, -122.41), LatLng(37.76, -122.43)), 45, 1200),
            Neighborhood("Castro", "San Francisco", MapBounds(LatLng(37.77, -122.42), LatLng(37.75, -122.44)), 32, 890),
            Neighborhood("Haight-Ashbury", "San Francisco", MapBounds(LatLng(37.78, -122.44), LatLng(37.76, -122.46)), 28, 750),
            Neighborhood("Noe Valley", "San Francisco", MapBounds(LatLng(37.76, -122.43), LatLng(37.74, -122.45)), 38, 920),
            Neighborhood("Richmond District", "San Francisco", MapBounds(LatLng(37.78, -122.48), LatLng(37.76, -122.50)), 22, 680),
            Neighborhood("Pacific Heights", "San Francisco", MapBounds(LatLng(37.80, -122.43), LatLng(37.78, -122.45)), 18, 540)
        )
        return Result.Success(neighborhoods)
    }

    private fun generateMockCommunities(): List<Community> {
        val now = Clock.System.now().inTimeZone(TimeZone.currentSystemDefault()).toLocalDateTime()
        return listOf(
            Community(
                id = Uuid.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                name = "Mission District Neighbors",
                description = "Vibrant community connecting residents of the Mission District. Share resources, organize events, and support each other!",
                neighborhood = "Mission District",
                city = "San Francisco",
                memberCount = 1247,
                activePostsCount = 45,
                coverImageUrl = null,
                createdAt = now.minusMonths(18),
                isMember = true
            ),
            Community(
                id = Uuid.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                name = "Castro Cares",
                description = "LGBTQ+ friendly mutual aid network in the Castro. We support each other through all of life's challenges.",
                neighborhood = "Castro",
                city = "San Francisco",
                memberCount = 892,
                activePostsCount = 32,
                coverImageUrl = null,
                createdAt = now.minusMonths(12),
                isMember = false
            ),
            Community(
                id = Uuid.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                name = "Haight-Ashbury Helpers",
                description = "Neighbors helping neighbors in the historic Haight. From tool sharing to grocery runs, we're here for each other.",
                neighborhood = "Haight-Ashbury",
                city = "San Francisco",
                memberCount = 756,
                activePostsCount = 28,
                coverImageUrl = null,
                createdAt = now.minusMonths(24),
                isMember = false
            ),
            Community(
                id = Uuid.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                name = "Noe Valley Families",
                description = "Family-focused community for Noe Valley parents. Playdates, childcare swaps, hand-me-downs, and parenting support.",
                neighborhood = "Noe Valley",
                city = "San Francisco",
                memberCount = 934,
                activePostsCount = 38,
                coverImageUrl = null,
                createdAt = now.minusMonths(8),
                isMember = false
            )
        )
    }
}