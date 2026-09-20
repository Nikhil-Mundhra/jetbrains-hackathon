package com.communityconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.communityconnect.domain.model.Post
import com.communityconnect.domain.model.PostStatus
import com.communityconnect.presentation.viewmodel.ProfileViewModel
import com.communityconnect.ui.components.AsyncImage
import com.communityconnect.ui.components.PostCard
import com.communityconnect.ui.theme.Theme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMyPostsClick: () -> Unit,
    onMyCommunitiesClick: () -> Unit
) {
    val currentUser = androidx.compose.runtime.getValue(
        viewModel::currentUser
    ) { androidx.compose.runtime.mutableStateOf(null) }
    
    // For demo, we'll use mock data
    val mockUser = com.communityconnect.domain.model.User(
        id = java.util.UUID.fromString("11111111-1111-1111-1111-111111111111"),
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
    
    val mockPosts = listOf(
        Post(
            authorId = mockUser.id,
            authorName = mockUser.name,
            authorAvatarUrl = null,
            type = com.communityconnect.domain.model.PostType.REQUEST,
            title = "Need help with grocery shopping",
            description = "I'm recovering from surgery and can't carry heavy bags.",
            category = com.communityconnect.domain.model.Category.FOOD,
            location = com.communityconnect.domain.model.Location(
                latitude = 37.7749, longitude = -122.4194,
                address = "123 Mission St", neighborhood = "Mission District", city = "San Francisco"
            ),
            urgency = com.communityconnect.domain.model.Urgency.HIGH,
            status = PostStatus.ACTIVE,
            responsesCount = 3,
            likesCount = 5
        ),
        Post(
            authorId = mockUser.id,
            authorName = mockUser.name,
            authorAvatarUrl = null,
            type = com.communityconnect.domain.model.PostType.OFFER,
            title = "Free Spanish tutoring sessions",
            description = "Native Spanish speaker offering conversational practice.",
            category = com.communityconnect.domain.model.Category.LANGUAGE,
            location = com.communityconnect.domain.model.Location(
                latitude = 37.7749, longitude = -122.4194,
                address = "123 Mission St", neighborhood = "Mission District", city = "San Francisco"
            ),
            urgency = com.communityconnect.domain.model.Urgency.LOW,
            status = PostStatus.COMPLETED,
            responsesCount = 4,
            likesCount = 7
        )
    )
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onEditProfileClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Profile Header
                ProfileHeader(
                    user = mockUser,
                    onEditClick = onEditProfileClick
                )
                
                // Stats Row
                StatsRow(user = mockUser)
                
                // Skills
                SkillsSection(skills = mockUser.skills)
                
                // My Posts Section
                SectionHeaderWithAction(
                    title = "My Posts",
                    actionText = "View All",
                    onActionClick = onMyPostsClick
                )
                
                LazyColumn(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockPosts) { post ->
                        PostCard(
                            post = post,
                            onClick = { /* navigate to detail */ },
                            onLikeClick = { },
                            onResponseClick = { }
                        )
                    }
                }
                
                // Communities Section
                SectionHeaderWithAction(
                    title = "My Communities",
                    actionText = "View All",
                    onActionClick = onMyCommunitiesClick
                )
                
                CommunitiesList()
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: com.communityconnect.domain.model.User,
    onEditClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(modifier = androidx.compose.ui.Modifier.padding(24.dp)) {
            Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        url = user.avatarUrl,
                        contentDescription = user.name,
                        size = 80.dp
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(16.dp))
                    Column(crossAxisAlignment = androidx.compose.ui.Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = colors.onPrimaryContainer
                            )
                            if (user.isVerified) {
                                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = colors.primary,
                                    modifier = androidx.compose.ui.Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "@${user.neighborhood}, ${user.city}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        if (user.bio != null) {
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
                            Text(
                                text = user.bio!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onPrimaryContainer.copy(alpha = 0.7f),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            
            Button(
                onClick = onEditClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceContainerHighest,
                    contentColor = colors.onSurfaceVariant
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = androidx.compose.ui.Modifier.size(18.dp))
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
                    Text("Edit Profile")
                }
            }
        }
    }
}

@Composable
fun StatsRow(user: com.communityconnect.domain.model.User) {
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            icon = Icons.Default.Favorite,
            value = user.reputationScore.toString(),
            label = "Reputation",
            color = colors.error
        )
        StatItem(
            icon = Icons.Default.Star,
            value = user.completedOffers.toString(),
            label = "Helped",
            color = colors.tertiary
        )
        StatItem(
            icon = Icons.Default.Person,
            value = user.completedRequests.toString(),
            label = "Received Help",
            color = colors.primary
        )
    }
}

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, color: Color) {
    Column(
        modifier = androidx.compose.ui.Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = androidx.compose.ui.Modifier.size(24.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SkillsSection(skills: List<String>) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(16.dp),
            crossAxisAlignment = androidx.compose.ui.Alignment.Start
        ) {
            Text("Skills & Interests", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
            androidx.compose.foundation.layout.WrapLayout(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.forEach { skill ->
                    Chip(
                        onClick = {},
                        colors = androidx.compose.material3.ChipDefaults.chipColors(
                            containerColor = colors.primaryContainer,
                            contentColor = colors.onPrimaryContainer
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    ) {
                        Text(text = skill, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(title: String, actionText: String, onActionClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(text = actionText, style = MaterialTheme.typography.labelMedium, color = colors.primary)
            .let { androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(onClick = onActionClick) { it } }
    }
}

@Composable
fun CommunitiesList() {
    val colors = MaterialTheme.colorScheme
    
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, bottom = 16.dp)
    ) {
        listOf(
            "Mission District Neighbors" to true,
            "Castro Cares" to false,
            "Haight-Ashbury Helpers" to false,
            "Noe Valley Families" to false
        ).forEach { (name, isMember) ->
            Card(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = androidx.compose.ui.Modifier
                                .size(48.dp)
                                .background(colors.primaryContainer)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = androidx.compose.ui.Modifier.align(Alignment.Center)
                            )
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
                        Text(text = name, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (isMember) {
                        Chip(
                            onClick = {},
                            colors = androidx.compose.material3.ChipDefaults.chipColors(
                                containerColor = colors.primaryContainer,
                                contentColor = colors.onPrimaryContainer
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                        ) {
                            Text("Member", style = MaterialTheme.typography.labelSmall)
                        }
                    } else {
                        Button(
                            onClick = { /* join */ },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceContainerHighest
                            )
                        ) {
                            Text("Join")
                        }
                    }
                }
            }
        }
    }
}

// Simple WrapLayout implementation for skills
@Composable
fun androidx.compose.foundation.layout.WrapLayout(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    content: @Composable () -> Unit
) {
    // This is a simplified version - in production you'd use a proper flow layout
    Column(modifier = modifier, verticalArrangement = verticalArrangement) {
        content()
    }
}