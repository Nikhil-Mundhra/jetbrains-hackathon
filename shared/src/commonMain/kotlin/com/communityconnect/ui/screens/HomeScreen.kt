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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.communityconnect.domain.model.Category
import com.communityconnect.domain.model.Post
import com.communityconnect.domain.model.PostType
import com.communityconnect.domain.model.Urgency
import com.communityconnect.presentation.viewmodel.MainViewModel
import com.communityconnect.ui.components.AsyncImage
import com.communityconnect.ui.components.EmptyState
import com.communityconnect.ui.components.PostCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onPostClick: (Post) -> Unit,
    onCreatePostClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCommunitiesClick: () -> Unit,
    onMapClick: () -> Unit
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val error by viewModel.error.collectAsState()
    val showFilter by remember { mutableStateOf(false) }
    val selectedCategory by remember { mutableStateOf<Category?>(null) }
    val selectedType by remember { mutableStateOf<PostType?>(null) }
    val selectedUrgency by remember { mutableStateOf<Urgency?>(null) }
    
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("CommunityConnect") },
                navigationIcon = {
                    IconButton(onClick = { /* Open drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilter = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = onProfileClick) {
                        AsyncImage(
                            url = currentUser?.avatarUrl,
                            contentDescription = currentUser?.name ?: "Profile",
                            size = 32.dp
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePostClick) {
                Icon(Icons.Default.Add, contentDescription = "Create post")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Location header
                LocationHeader(
                    neighborhood = currentUser?.neighborhood ?: "Mission District",
                    city = currentUser?.city ?: "San Francisco",
                    onMapClick = onMapClick
                )
                
                // Filter chips row
                if (showFilter) {
                    FilterChipsRow(
                        selectedCategory = selectedCategory,
                        onCategoryChange = { selectedCategory = it },
                        selectedType = selectedType,
                        onTypeChange = { selectedType = it },
                        selectedUrgency = selectedUrgency,
                        onUrgencyChange = { selectedUrgency = it },
                        onClear = {
                            selectedCategory = null
                            selectedType = null
                            selectedUrgency = null
                            showFilter = false
                            viewModel.refreshPosts()
                        }
                    )
                }
                
                // Error message
                error?.let { msg ->
                    ErrorBanner(message = msg, onDismiss = { viewModel.error.value = null })
                }
                
                // Posts list
                if (isLoading && posts.isEmpty()) {
                    LoadingState(modifier = Modifier.weight(1f))
                } else if (posts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.LocationOn,
                        title = "No posts nearby",
                        message = "Be the first to create a post in your area!",
                        actionText = "Create Post",
                        onAction = onCreatePostClick,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, top = 8.dp)
                    ) {
                        items(posts) { post ->
                            PostCard(
                                post = post,
                                onClick = { onPostClick(post) },
                                onLikeClick = { viewModel.likePost(post.id) },
                                onResponseClick = { /* Navigate to responses */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationHeader(
    neighborhood: String,
    city: String,
    onMapClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onMapClick),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                Column(crossAxisAlignment = androidx.compose.ui.Alignment.Start) {
                    Text(
                        text = neighborhood,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.onPrimaryContainer
                    )
                    Text(
                        text = city,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Open map",
                tint = colors.onPrimaryContainer
            )
        }
    }
}

@Composable
fun FilterChipsRow(
    selectedCategory: Category?,
    onCategoryChange: (Category?) -> Unit,
    selectedType: PostType?,
    onTypeChange: (PostType?) -> Unit,
    selectedUrgency: Urgency?,
    onUrgencyChange: (Urgency?) -> Unit,
    onClear: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Filters", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
                Text("Clear", style = MaterialTheme.typography.labelMedium, color = colors.primary)
                    .let { androidx.compose.foundation.layout.Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable(onClick = onClear) { it } }
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            
            // Category chips
            SingleLineChipGroup(
                label = "Category",
                options = Category.values().map { it.label },
                selectedIndex = selectedCategory?.ordinal,
                onSelect = { index -> onCategoryChange(if (index >= 0) Category.values()[index] else null) }
            )
            
            // Type chips
            SingleLineChipGroup(
                label = "Type",
                options = PostType.values().map { it.name.capitalize() },
                selectedIndex = selectedType?.ordinal,
                onSelect = { index -> onTypeChange(if (index >= 0) PostType.values()[index] else null) }
            )
            
            // Urgency chips
            SingleLineChipGroup(
                label = "Urgency",
                options = Urgency.values().map { it.label },
                selectedIndex = selectedUrgency?.ordinal,
                onSelect = { index -> onUrgencyChange(if (index >= 0) Urgency.values()[index] else null) }
            )
        }
    }
}

@Composable
fun SingleLineChipGroup(
    label: String,
    options: List<String>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = selectedIndex == index
            FilterChip(
                text = option,
                selected = isSelected,
                onClick = { onSelect(if (isSelected) -1 else index) }
            )
        }
    }
}

@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Chip(
        onClick = onClick,
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = if (selected) colors.primaryContainer else colors.surfaceContainerHighest,
            contentColor = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.errorContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = message, color = colors.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
            androidx.compose.material3.IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = colors.onErrorContainer)
            }
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}