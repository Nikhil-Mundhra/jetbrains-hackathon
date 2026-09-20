package com.communityconnect.presentation.viewmodel

import com.yallapark.presentation.viewmodel.ViewModel
import com.communityconnect.data.repository.MockCommunityRepository
import com.communityconnect.data.repository.MockPostRepository
import com.communityconnect.data.repository.MockResponseRepository
import com.communityconnect.data.repository.MockUserRepository
import com.communityconnect.domain.model.Category
import com.communityconnect.domain.model.Community
import com.communityconnect.domain.model.Post
import com.communityconnect.domain.model.PostStatus
import com.communityconnect.domain.model.PostType
import com.communityconnect.domain.model.Response
import com.communityconnect.domain.model.Urgency
import com.communityconnect.domain.model.User
import com.communityconnect.domain.repository.CommunityRepository
import com.communityconnect.domain.repository.PostRepository
import com.communityconnect.domain.repository.ResponseRepository
import com.communityconnect.domain.repository.Result
import com.communityconnect.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

class MainViewModel(
    private val postRepository: PostRepository = MockPostRepository(),
    private val userRepository: UserRepository = MockUserRepository(),
    private val responseRepository: ResponseRepository = MockResponseRepository(),
    private val communityRepository: CommunityRepository = MockCommunityRepository()
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities = _communities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentLatitude = 37.7749
    private var currentLongitude = -122.4194

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null
            
            userRepository.getCurrentUser().fold(
                success = { user ->
                    _currentUser.value = user
                    currentLatitude = user.latitude
                    currentLongitude = user.longitude
                    loadPosts()
                    loadCommunities()
                },
                error = { e ->
                    _error.value = e.message ?: "Failed to load user"
                    loadPosts()
                    loadCommunities()
                }
            )
            _isLoading.value = false
        }
    }

    fun loadPosts(
        category: Category? = null,
        type: PostType? = null,
        urgency: Urgency? = null,
        radiusKm: Double = 5.0
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.getPosts(
                latitude = currentLatitude,
                longitude = currentLongitude,
                radiusKm = radiusKm,
                category = category,
                type = type,
                urgency = urgency
            ).fold(
                success = { posts ->
                    _posts.value = posts
                },
                error = { e ->
                    _error.value = e.message ?: "Failed to load posts"
                }
            )
        }
    }

    fun refreshPosts() {
        loadPosts()
    }

    fun createPost(
        type: PostType,
        title: String,
        description: String,
        category: Category,
        urgency: Urgency,
        location: com.communityconnect.domain.model.Location,
        tags: List<String> = emptyList(),
        images: List<String> = emptyList()
    ): Result<Post> {
        val user = _currentUser.value ?: return Result.Error(IllegalStateException("User not logged in"))
        
        val post = Post(
            authorId = user.id,
            authorName = user.name,
            authorAvatarUrl = user.avatarUrl,
            type = type,
            title = title,
            description = description,
            category = category,
            location = location,
            urgency = urgency,
            tags = tags,
            images = images
        )
        
        return postRepository.createPost(post).fold(
            success = { createdPost ->
                _posts.value = _posts.value + createdPost
                Result.Success(createdPost)
            },
            error = { e ->
                Result.Error(e)
            }
        )
    }

    fun likePost(postId: Uuid) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.likePost(postId, user.id).fold(
                success = { updatedPost ->
                    _posts.value = _posts.value.map { if (it.id == postId) updatedPost else it }
                },
                error = { e ->
                    _error.value = e.message ?: "Failed to like post"
                }
            )
        }
    }

    fun loadCommunities() {
        viewModelScope.launch(Dispatchers.IO) {
            communityRepository.getCommunities(currentLatitude, currentLongitude).fold(
                success = { communities ->
                    _communities.value = communities
                },
                error = { e ->
                    _error.value = e.message ?: "Failed to load communities"
                }
            )
        }
    }

    fun joinCommunity(communityId: Uuid) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            communityRepository.joinCommunity(communityId, user.id).fold(
                success = { updatedCommunity ->
                    _communities.value = _communities.value.map { if (it.id == communityId) updatedCommunity else it }
                },
                error = { e ->
                    _error.value = e.message ?: "Failed to join community"
                }
            )
        }
    }

    fun updateLocation(latitude: Double, longitude: Double, neighborhood: String, city: String) {
        val user = _currentUser.value ?: return
        currentLatitude = latitude
        currentLongitude = longitude
        
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateLocation(user.id, latitude, longitude, neighborhood, city).fold(
                success = { updatedUser ->
                    _currentUser.value = updatedUser
                    loadPosts()
                    loadCommunities()
                },
                error = { e ->
                    _error.value = e.message ?: "Failed to update location"
                }
            )
        }
    }
}

class PostDetailViewModel(
    private val postRepository: PostRepository = MockPostRepository(),
    private val responseRepository: ResponseRepository = MockResponseRepository(),
    private val userRepository: UserRepository = MockUserRepository()
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _responses = MutableStateFlow<List<Response>>(emptyList())
    val responses = _responses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadPost(postId: Uuid) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            postRepository.getPostById(postId).fold(
                success = { post ->
                    _post.value = post
                    loadResponses(postId)
                },
                error = { e ->
                    // Handle error
                }
            )
            _isLoading.value = false
        }
    }

    private fun loadResponses(postId: Uuid) {
        responseRepository.getResponses(postId).fold(
            success = { responses ->
                _responses.value = responses
            },
            error = { /* handle error */ }
        )
    }

    fun respondToPost(message: String, type: com.communityconnect.domain.model.ResponseType) {
        val user = userRepository.getCurrentUser().fold({ it }, { null }) ?: return
        val post = _post.value ?: return
        
        val response = Response(
            postId = post.id,
            responderId = user.id,
            responderName = user.name,
            responderAvatarUrl = user.avatarUrl,
            message = message,
            type = type
        )
        
        viewModelScope.launch(Dispatchers.IO) {
            responseRepository.createResponse(response).fold(
                success = { createdResponse ->
                    _responses.value = _responses.value + createdResponse
                    val updatedPost = post.copy(responsesCount = post.responsesCount + 1)
                    _post.value = updatedPost
                },
                error = { /* handle error */ }
            )
        }
    }

    fun acceptResponse(responseId: Uuid) {
        viewModelScope.launch(Dispatchers.IO) {
            responseRepository.acceptResponse(responseId).fold(
                success = { updatedResponse ->
                    _responses.value = _responses.value.map { if (it.id == responseId) updatedResponse else it }
                    val post = _post.value?.copy(status = com.communityconnect.domain.model.PostStatus.IN_PROGRESS)
                    post?.let { _post.value = it }
                },
                error = { /* handle error */ }
            )
        }
    }
}

class ProfileViewModel(
    private val userRepository: UserRepository = MockUserRepository(),
    private val postRepository: PostRepository = MockPostRepository()
) : ViewModel() {

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts = _userPosts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadUserPosts(userId: Uuid) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            postRepository.getUserPosts(userId).fold(
                success = { posts ->
                    _userPosts.value = posts
                },
                error = { /* handle error */ }
            )
            _isLoading.value = false
        }
    }

    fun updateProfile(name: String, bio: String?, skills: List<String>) {
        val user = userRepository.getCurrentUser().fold({ it }, { null }) ?: return
        val updatedUser = user.copy(name = name, bio = bio, skills = skills)
        
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUser(updatedUser).fold(
                success = { /* update UI */ },
                error = { /* handle error */ }
            )
        }
    }
}

class CreatePostViewModel(
    private val postRepository: PostRepository = MockPostRepository(),
    private val userRepository: UserRepository = MockUserRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun createPost(
        type: PostType,
        title: String,
        description: String,
        category: Category,
        urgency: Urgency,
        location: com.communityconnect.domain.model.Location,
        tags: List<String> = emptyList(),
        images: List<String> = emptyList()
    ): Result<Post> {
        val user = userRepository.getCurrentUser().fold({ it }, { null }) ?: return Result.Error(IllegalStateException("User not logged in"))
        
        _isLoading.value = true
        _error.value = null
        
        val post = Post(
            authorId = user.id,
            authorName = user.name,
            authorAvatarUrl = user.avatarUrl,
            type = type,
            title = title,
            description = description,
            category = category,
            location = location,
            urgency = urgency,
            tags = tags,
            images = images
        )
        
        val result = postRepository.createPost(post)
        
        _isLoading.value = false
        result.fold(
            success = { },
            error = { _error.value = it.message ?: "Failed to create post" }
        )
        
        return result
    }
}