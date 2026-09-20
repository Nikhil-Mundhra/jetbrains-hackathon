package com.communityconnect.domain.repository

import com.communityconnect.domain.model.Community
import com.communityconnect.domain.model.LatLng
import com.communityconnect.domain.model.Neighborhood
import com.communityconnect.domain.model.Post
import com.communityconnect.domain.model.Response
import com.communityconnect.domain.model.User
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun getPosts(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0,
        category: com.communityconnect.domain.model.Category? = null,
        type: com.communityconnect.domain.model.PostType? = null,
        urgency: com.communityconnect.domain.model.Urgency? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<Post>>

    suspend fun getPostById(postId: kotlin.uuid.Uuid): Result<Post>

    suspend fun createPost(post: Post): Result<Post>

    suspend fun updatePost(post: Post): Result<Post>

    suspend fun deletePost(postId: kotlin.uuid.Uuid): Result<Unit>

    suspend fun likePost(postId: kotlin.uuid.Uuid, userId: kotlin.uuid.Uuid): Result<Post>

    suspend fun getUserPosts(userId: kotlin.uuid.Uuid, status: com.communityconnect.domain.model.PostStatus? = null): Result<List<Post>>

    fun observePosts(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Flow<List<Post>>

    suspend fun searchPosts(query: String, latitude: Double, longitude: Double, radiusKm: Double = 10.0): Result<List<Post>>
}

interface UserRepository {
    suspend fun getCurrentUser(): Result<User>

    suspend fun getUserById(userId: kotlin.uuid.Uuid): Result<User>

    suspend fun updateUser(user: User): Result<User>

    suspend fun updateLocation(userId: kotlin.uuid.Uuid, latitude: Double, longitude: Double, neighborhood: String, city: String): Result<User>

    suspend fun updateNotificationSettings(userId: kotlin.uuid.Uuid, settings: com.communityconnect.domain.model.NotificationSettings): Result<User>

    suspend fun signup(user: User): Result<User>

    suspend fun login(email: String, password: String): Result<User>

    suspend fun logout(): Result<Unit>

    fun observeCurrentUser(): Flow<User?>
}

interface ResponseRepository {
    suspend fun getResponses(postId: kotlin.uuid.Uuid): Result<List<Response>>

    suspend fun createResponse(response: Response): Result<Response>

    suspend fun updateResponse(response: Response): Result<Response>

    suspend fun acceptResponse(responseId: kotlin.uuid.Uuid): Result<Response>

    suspend fun declineResponse(responseId: kotlin.uuid.Uuid): Result<Response>

    fun observeResponses(postId: kotlin.uuid.Uuid): Flow<List<Response>>
}

interface CommunityRepository {
    suspend fun getCommunities(latitude: Double, longitude: Double, radiusKm: Double = 10.0): Result<List<Community>>

    suspend fun getCommunityById(communityId: kotlin.uuid.Uuid): Result<Community>

    suspend fun joinCommunity(communityId: kotlin.uuid.Uuid, userId: kotlin.uuid.Uuid): Result<Community>

    suspend fun leaveCommunity(communityId: kotlin.uuid.Uuid, userId: kotlin.uuid.Uuid): Result<Unit>

    suspend fun createCommunity(community: Community): Result<Community>

    fun observeCommunities(latitude: Double, longitude: Double, radiusKm: Double = 10.0): Flow<List<Community>>

    suspend fun getNeighborhoods(city: String): Result<List<Neighborhood>>
}

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>
    
    fun <R> fold(success: (T) -> R, error: (Throwable) -> R): R = when (this) {
        is Success -> success(data)
        is Error -> error(exception)
    }
    
    fun onSuccess(action: (T) -> Unit): Result<T> = fold({ action(it); this }, { this })
    fun onError(action: (Throwable) -> Unit): Result<T> = fold({ this }, { action(it); this })
}